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
import io.ib67.astralflow.manager.IMachineManager;
import io.ib67.astralflow.scheduler.TickReceipt;
import io.ib67.astralflow.storage.IMachineStorage;
import io.ib67.util.bukkit.Log;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class MachineManagerImpl implements IMachineManager {
    private final IMachineStorage machineStorage;
    private final Map<UUID, IMachine> cache = new HashMap<>(64);
    private final Map<IMachine, TickReceipt<IMachine>> receiptMap = new WeakHashMap<>(64);

    {
        AstralFlow.getInstance().addHook(HookType.SAVE_DATA, this::saveMachines);
    }

    @Override
    public void setupMachine(IMachine machine, boolean update) {
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
        return cache.containsKey(uuid);
    }

    @Override
    public IMachine getAndLoadMachine(UUID uuid) {
        //todo refactor.
        return cache.computeIfAbsent(uuid, k -> {
            var machine = machineStorage.get(machineStorage.getLocationByUUID(k));
            if (machine == null) {
                throw new IllegalArgumentException("Machine with id " + k + " is not registered.");
            }
            //setupMachine(machine);
            machine.init();
            activateMachine(machine);
            return machine;
        });
    }

    @Override
    public IMachine getAndLoadMachine(Location location) {
        return getLoadedMachines().stream().filter(machine -> AstralHelper.equalsLocationFuzzily(machine.getLocation(), location)).findFirst().orElse(null); //todo FIXME
    }

    @Override
    public void deactivateMachine(IMachine machine) {
        var receipt = receiptMap.get(machine.getId());
        if (receipt == null) {
            Log.warn("Machine " + machine.getId() + " is already deactivated. Won't do anything.");
            return;
        }
        receipt.drop();
        receiptMap.remove(machine.getId());
    }

    @Override
    public void activateMachine(IMachine machine) {
        receiptMap.computeIfAbsent(machine, k -> AstralFlow.getInstance().getTickManager().registerTickable(machine).requires(IMachine::canTick));
    }

    @Override
    public Collection<? extends IMachine> getLoadedMachines() {
        return cache.values();
    }

    @Override
    public Collection<? extends UUID> getAllMachines() {
        return machineStorage.getKeys().stream().map(machineStorage::getUUIDByLocation).collect(Collectors.toList());
    }

    @Override
    public void registerMachine(IMachine machine) {
        Objects.requireNonNull(machine, "Machine cannot be null.");
        var id = machine.getId();

        if (cache.containsKey(id)) {
            throw new IllegalArgumentException("This machine is already registered.");
        }
        cache.put(id, machine);
    }

    @Override
    public boolean isMachine(Block block) {
        // FIX: https://github.com/iceBear67/AstralFlow/issues/2
        //return getLoadedMachines().stream().anyMatch(machine -> block.getLocation().distance(machine.getLocation()) < 0.1); // for some moving entities
        return getLoadedMachines().stream().anyMatch(machine -> AstralHelper.equalsLocationFuzzily(machine.getLocation(), block.getLocation()));
    }

    @Override
    public void saveMachines() {
        getLoadedMachines().stream().filter(e -> {
            e.onUnload();
            return true;
        }).forEach(e -> machineStorage.save(e.getLocation(), e));
    }

    @Override
    public boolean removeAndTerminateMachine(IMachine machine) {
        deactivateMachine(machine);
        machine.onUnload();
        cache.remove(machine.getId());
        machineStorage.remove(machine.getLocation());
        return true;
    }

    @Override
    public TickReceipt<IMachine> getReceiptByMachine(IMachine machine) {
        var r = this.receiptMap.get(machine);
        if (r.isDropped()) {
            return null;
        }
        return r;
    }
}
