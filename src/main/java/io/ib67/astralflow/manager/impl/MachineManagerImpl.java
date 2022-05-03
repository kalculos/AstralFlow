/*
 *
 *   AstralFlow - The plugin enriches bukkit servers
 *   Copyright (C) 2022 The Inlined Lambdas and Contributors
 *
 *   This library is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU Lesser General Public
 *   License as published by the Free Software Foundation; either
 *   version 2.1 of the License, or (at your option) any later version.
 *
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *   Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the Free Software
 *   Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 *   USA
 */

package io.ib67.astralflow.manager.impl;

import io.ib67.astralflow.api.AstralHelper;
import io.ib67.astralflow.hook.HookType;
import io.ib67.astralflow.hook.event.server.SaveDataEvent;
import io.ib67.astralflow.internal.IChunkTracker;
import io.ib67.astralflow.internal.storage.IMachineStorage;
import io.ib67.astralflow.machines.IMachine;
import io.ib67.astralflow.machines.Tickless;
import io.ib67.astralflow.manager.IMachineManager;
import io.ib67.astralflow.manager.ITickManager;
import io.ib67.astralflow.scheduler.TickReceipt;
import io.ib67.astralflow.security.mem.ILeakTracker;
import io.ib67.astralflow.util.WeakHashSet;
import io.ib67.util.bukkit.Log;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.world.ChunkLoadEvent;

import java.util.*;

public final class MachineManagerImpl implements IMachineManager {

    private final IMachineStorage machineStorage;
    private final Set<IMachine> loadedMachines;

    private final Map<IMachine, TickReceipt<IMachine>> tickReceipts;
    private final ITickManager scheduler;

    private final IChunkTracker chunkTracker; // to check loaded chunks out of astral flow

    private final ILeakTracker leakTracker;

    public MachineManagerImpl
            (IMachineStorage storage,
             ITickManager scheduler,
             int machineCapacity,
             boolean allowResizingMachineMap,
             IChunkTracker chunkTracker, ILeakTracker leakTracker) {
        this.machineStorage = storage;
        this.leakTracker = leakTracker;
        storage.init(this);
        int defaultCapacity = Math.max(machineCapacity, 16);
        tickReceipts = new WeakHashMap<>(machineCapacity);
        this.scheduler = scheduler;

        loadedMachines = new WeakHashSet<>(defaultCapacity);
        if (!allowResizingMachineMap) {
            ((WeakHashSet<?>) loadedMachines).disableResizing();
        }

        this.chunkTracker = chunkTracker;
        HookType.CHUNK_LOAD.register(this::initChunk);
        HookType.CHUNK_UNLOAD.register(t -> finalizeChunk(t.getChunk()));
        HookType.SAVE_DATA.register(this::onSaveData);
    }

    private void finalizeAll() {
        var chunks = List.copyOf(chunkTracker.getMarkedChunks());
        chunks.stream().forEach(this::finalizeChunk);
    }

    private void initChunk(ChunkLoadEvent hook) {
        chunkTracker.markChunk(hook.getChunk());

        machineStorage.initChunk(hook.getChunk());
        for (IMachine machine : machineStorage.getMachinesByChunk(hook.getChunk())) {
            setupMachine(machine, !machine.getClass().isAnnotationPresent(Tickless.class));
        }
    }

    private void finalizeChunk(Chunk chunk) {
        chunkTracker.unmarkChunk(chunk);

        machineStorage.getMachinesByChunk(chunk)
                .forEach(this::terminateMachine);
        machineStorage.finalizeChunk(chunk, true);
    }

    private void terminateMachine(IMachine machine) {
        Objects.requireNonNull(machine, "Machine cannot be null");
        if (getReceiptByMachine(machine) != null) {
            deactivateMachine(machine);
        }
        unregisterMachine(machine);
        leakTracker.track(machine);
    }

    @Override
    public IMachine getAndLoadMachine(Location location) {
        Objects.requireNonNull(location, "Location cannot be null");
        var loc = AstralHelper.purifyLocation(location);

        boolean isChunkUnmarked = !chunkTracker.isChunkMarked(loc.getChunk());
        if (AstralHelper.isChunkLoaded(loc) && isChunkUnmarked) {
            Log.warn("MachineManager", "Chunk is loaded but not initialized by AF. This is a potential bug");
        }

        boolean init = !AstralHelper.isChunkLoaded(loc) || isChunkUnmarked;
        if (init) {
            loc.getChunk().load();
        }
        return machineStorage.get(loc); // machine will be initialized at `loadChunk`
    }

    @Override
    public void deactivateMachine(IMachine machine) {
        Objects.requireNonNull(machine, "Machine cannot be null");
        if (getReceiptByMachine(machine) == null) {
            throw new IllegalStateException("Machine " + machine + " is not active");
        }
        Optional.ofNullable(getReceiptByMachine(machine)).ifPresent(TickReceipt::drop);
        tickReceipts.remove(machine);
    }

    @Override
    public void activateMachine(IMachine machine) {
        Objects.requireNonNull(machine, "Machine cannot be null");
        if (getReceiptByMachine(machine) != null) {
            throw new IllegalStateException("Machine " + machine + " is already active");
        }
        tickReceipts.put(machine, scheduler.registerTickable(machine));

    }

    @Override
    public Collection<? extends IMachine> getLoadedMachines() {
        return loadedMachines;
    }

    @Override
    public void registerMachine(IMachine machine) {
        Objects.requireNonNull(machine, "Machine cannot be null");
        loadedMachines.add(machine);
        machineStorage.save(AstralHelper.purifyLocation(machine.getLocation()), machine);
    }

    @Override
    public void unregisterMachine(IMachine machine) {
        Objects.requireNonNull(machine, "Machine cannot be null");
        loadedMachines.remove(machine);
        machine.onUnload();
    }

    @Override
    public boolean isMachine(Block block) {
        Objects.requireNonNull(block, "Block cannot be null");
        return machineStorage.get(AstralHelper.purifyLocation(block.getLocation())) != null;
    }

    private void onSaveData(SaveDataEvent event) {
        if (event.isShuttingDown()) {
            finalizeAll();
        }
        saveMachines();
    }

    @Override
    public void saveMachines() {
        machineStorage.flush();
    }

    @Override
    public boolean removeMachine(IMachine machine) {
        Objects.requireNonNull(machine, "Machine cannot be null");
        if (!isRegistered(machine)) {
            throw new IllegalArgumentException("machine " + machine + " is not registered");
        }
        machineStorage.remove(AstralHelper.purifyLocation(machine.getLocation()));
        return true;
    }

    @Override
    public TickReceipt<IMachine> getReceiptByMachine(IMachine machine) {
        Objects.requireNonNull(machine, "Machine cannot be null");
        var r = tickReceipts.get(machine);
        if (r == null || r.isDropped()) {
            return null;
        }
        return r;
    }

    @Override
    public boolean isRegistered(IMachine machine) {
        Objects.requireNonNull(machine, "Machine cannot be null");
        return loadedMachines.contains(machine);
    }

    @Override
    public void updateMachineLocation(Location previousLocation, Location newLocation, IMachine machine) {
        Objects.requireNonNull(previousLocation, "Previous location cannot be null");
        Objects.requireNonNull(machine, "Machine cannot be null");
        if (!isRegistered(machine)) {
            throw new IllegalArgumentException("Machine " + machine + " is not registered");
        }
        machineStorage.remove(AstralHelper.purifyLocation(previousLocation));
        machineStorage.save(AstralHelper.purifyLocation(newLocation), machine);
    }
}
