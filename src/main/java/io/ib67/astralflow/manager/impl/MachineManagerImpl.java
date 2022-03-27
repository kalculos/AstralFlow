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
import io.ib67.astralflow.hook.event.chunk.ChunkLoadHook;
import io.ib67.astralflow.hook.event.chunk.ChunkUnloadHook;
import io.ib67.astralflow.machines.IMachine;
import io.ib67.astralflow.machines.Tickless;
import io.ib67.astralflow.manager.IMachineManager;
import io.ib67.astralflow.manager.ITickManager;
import io.ib67.astralflow.scheduler.TickReceipt;
import io.ib67.astralflow.storage.IMachineStorage;
import io.ib67.astralflow.util.WeakHashSet;
import io.ib67.util.bukkit.Log;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.*;

public class MachineManagerImpl implements IMachineManager {

    private final IMachineStorage machineStorage;
    private final Set<IMachine> loadedMachines;

    private final Map<IMachine, TickReceipt<IMachine>> tickReceipts;
    private final ITickManager scheduler;

    private final Set<Chunk> checkedChunks = new WeakHashSet<>(128); // to check loaded chunks out of astral flow

    public MachineManagerImpl(IMachineStorage storage, int capacity, ITickManager scheduler) {
        this.machineStorage = storage;
        int defaultCapacity = Math.max(capacity, 16);
        tickReceipts = new WeakHashMap<>(capacity);
        this.scheduler = scheduler;

        loadedMachines = new WeakHashSet<>(defaultCapacity);
        HookType.CHUNK_LOAD.register(this::initChunk);
        HookType.CHUNK_UNLOAD.register(this::finalizeChunk);
        HookType.SAVE_DATA.register(this::saveMachines);
        HookType.PLUGIN_SHUTDOWN.register(this::finalizeAll); //todo: performance issue / flush twice
    }

    private void finalizeAll() {
        var chunks = List.copyOf(checkedChunks);
        chunks.stream().map(ChunkUnloadHook::new).forEach(this::finalizeChunk);
    }

    private void initChunk(ChunkLoadHook hook) {
        checkedChunks.add(hook.getChunk());

        machineStorage.initChunk(hook.getChunk());
        for (IMachine machine : machineStorage.getMachinesByChunk(hook.getChunk())) {
            setupMachine(machine, !machine.getClass().isAnnotationPresent(Tickless.class));
        }
    }

    private void finalizeChunk(ChunkUnloadHook hook) {
        checkedChunks.remove(hook.getChunk());

        machineStorage.getMachinesByChunk(hook.getChunk())
                .forEach(this::terminateMachine);
        machineStorage.finalizeChunk(hook.getChunk());
    }

    private void terminateMachine(IMachine machine) {
        Objects.requireNonNull(machine, "Machine cannot be null");
        if (getReceiptByMachine(machine) != null) {
            deactivateMachine(machine);
        }
        unregisterMachine(machine);
    }

    @Override
    public IMachine getAndLoadMachine(Location location) {
        Objects.requireNonNull(location, "Location cannot be null");
        var loc = AstralHelper.purifyLocation(location);

        if (AstralHelper.isChunkLoaded(loc) && !checkedChunks.contains(loc.getChunk())) {
            Log.warn("MachineManager", "Chunk is loaded but not initialized by AF. This is a potential bug");
        }

        boolean init = !AstralHelper.isChunkLoaded(loc) || !checkedChunks.contains(loc.getChunk());
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
    public void updateMachineLocation(Location previousLocation, IMachine machine) {
        Objects.requireNonNull(previousLocation, "Previous location cannot be null");
        Objects.requireNonNull(machine, "Machine cannot be null");
        if (!isRegistered(machine)) {
            throw new IllegalArgumentException("Machine " + machine + " is not registered");
        }
        machineStorage.remove(AstralHelper.purifyLocation(previousLocation));
        machineStorage.save(machine.getLocation(), machine);
    }
}
