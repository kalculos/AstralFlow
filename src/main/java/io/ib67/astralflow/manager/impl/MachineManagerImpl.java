/*
 *
 *   AstralFlow - The plugin who is turning bukkit into mod-pack
 *   Copyright (C) 2022 iceBear67
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

import io.ib67.astralflow.AstralFlow;
import io.ib67.astralflow.api.AstralHelper;
import io.ib67.astralflow.hook.HookType;
import io.ib67.astralflow.machines.IMachine;
import io.ib67.astralflow.machines.Tickless;
import io.ib67.astralflow.manager.IMachineManager;
import io.ib67.astralflow.scheduler.TickReceipt;
import io.ib67.astralflow.storage.IMachineStorage;
import io.ib67.util.bukkit.Log;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.*;

@RequiredArgsConstructor
public class MachineManagerImpl implements IMachineManager {
    private static final int INITIAL_MACHINE_CAPACITY = 64;
    private static final Object EMPTY_OBJ = new Object();

    private final IMachineStorage machineStorage;
    private final Map<IMachine, TickReceipt<IMachine>> receiptMap = new WeakHashMap<>(INITIAL_MACHINE_CAPACITY);

    private final Map<IMachine, Object> loadedMachines = new WeakHashMap<>(INITIAL_MACHINE_CAPACITY);
    private final Map<Chunk, Object> checkedChunks = new WeakHashMap<>(INITIAL_MACHINE_CAPACITY);

    {
        AstralFlow.getInstance().addHook(HookType.PLUGIN_SHUTDOWN, this::saveMachines);
        HookType.CHUNK_LOAD.register(chunkLoad -> initChunk(chunkLoad.getChunk()));
        HookType.CHUNK_UNLOAD.register(chunkUnload -> finalizeChunk(chunkUnload.getChunk()));
    }

    @Override
    public void setupMachine(IMachine machine, boolean update) {
        Objects.requireNonNull(machine);
        loadedMachines.put(machine, EMPTY_OBJ);
        if (!isRegistered(machine.getId())) {
            registerMachine(machine);
        }
        //registerMachine(machine);
        machine.init();
        if (update) activateMachine(machine);
    }

    @Override
    public boolean isRegistered(UUID uuid) {
        Objects.requireNonNull(uuid, "UUID cannot be null.");
        return machineStorage.getLocationByUUID(uuid) != null;
    }

    private void finalizeChunk(Chunk chunk) {
        Objects.requireNonNull(chunk);
        checkedChunks.remove(chunk);
        machineStorage.getMachinesByChunk(chunk).forEach(this::terminateMachine);
        machineStorage.finalizeChunk(chunk);
    }

    private void initChunk(Chunk chunk) {
        Objects.requireNonNull(chunk);
        machineStorage.initChunk(chunk);
        checkedChunks.put(chunk, EMPTY_OBJ);
        for (IMachine machine : machineStorage.getMachinesByChunk(chunk)) {
            machine.onLoad();
            setupMachine(machine, !machine.getClass().isAnnotationPresent(Tickless.class));
        }
    }

    @Override
    public IMachine getAndLoadMachine(Location alocation) {
        Objects.requireNonNull(alocation, "Location cannot be null.");
        var location = AstralHelper.purifyLocation(alocation);
        boolean init = false;
        if (AstralHelper.isChunkLoaded(location)) {
            if (!checkedChunks.containsKey(location.getChunk())) {
                init = true;
                Log.warn("Chunk is loaded but not initialized by AF. This is a potential bug");
            }
        } else {
            init = true;
        }

        if (init) {
            initChunk(location.getChunk());
        }
        loadedMachines.put(machineStorage.get(location), EMPTY_OBJ);
        return machineStorage.get(location);
    }

    @Override
    public IMachine getAndLoadMachine(UUID id) {
        Objects.requireNonNull(id, "UUID cannot be null.");
        return getAndLoadMachine(machineStorage.getLocationByUUID(id));
    }

    @Override
    public void deactivateMachine(IMachine machine) {
        var receipt = receiptMap.get(machine);
        if (receipt == null) {
            Log.warn("Machine " + machine.getId() + " is already deactivated. Won't do anything.");
            return;
        }
        receipt.drop();
        receiptMap.remove(machine);
    }

    @Override
    public void activateMachine(IMachine machine) {
        Objects.requireNonNull(machine);
        if (machine.getClass().isAnnotationPresent(Tickless.class)) {
            Log.warn("Machine " + machine.getId() + " is tickless. But still activated.");
        }
        receiptMap.computeIfAbsent(machine, k -> AstralFlow.getInstance().getTickManager().registerTickable(machine).requires(IMachine::canTick));
    }

    @Override
    public Collection<? extends IMachine> getLoadedMachines() {
        return loadedMachines.keySet();
    }

    @Override
    public Collection<? extends Location> getAllMachines() {
        return machineStorage.getKeys().stream().map(Location::clone).toList();
    }

    @Override
    public void registerMachine(IMachine machine) {
        Objects.requireNonNull(machine, "Machine cannot be null.");

        if (loadedMachines.containsKey(machine)) {
            throw new IllegalArgumentException("This machine is already registered.");
        }
        loadedMachines.put(machine, EMPTY_OBJ);
        machineStorage.save(AstralHelper.purifyLocation(machine.getLocation()), machine);
    }

    @Override
    public boolean isMachine(Block block) {
        // FIX: https://github.com/iceBear67/AstralFlow/issues/2
        //return getLoadedMachines().stream().anyMatch(machine -> block.getLocation().distance(machine.getLocation()) < 0.1); // for some moving entities
        Objects.requireNonNull(block);
        return getLoadedMachines().stream().anyMatch(machine -> AstralHelper.equalsLocationFuzzily(machine.getLocation(), block.getLocation()));
    }

    @Override
    public void saveMachines() {
        for (World world : Bukkit.getWorlds()) {
            for (Chunk loadedChunk : world.getLoadedChunks()) {
                finalizeChunk(loadedChunk);
            }
        }
        machineStorage.flush();
    }

    @Override
    public boolean removeAndTerminateMachine(IMachine machine) {
        Objects.requireNonNull(machine);
        terminateMachine(machine);
        machineStorage.remove(AstralHelper.purifyLocation(machine.getLocation()));
        return true;
    }

    @Override
    public void terminateMachine(IMachine machine) {
        Objects.requireNonNull(machine);
        deactivateMachine(machine);
        machine.onUnload();
        loadedMachines.remove(machine);
    }

    @Override
    public TickReceipt<IMachine> getReceiptByMachine(IMachine machine) {
        Objects.requireNonNull(machine);
        var r = this.receiptMap.get(machine);
        if (r == null || r.isDropped()) {
            return null;
        }
        return r;
    }
}
