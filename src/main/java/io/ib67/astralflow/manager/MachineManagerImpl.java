/*
 *
 *   AstralFlow - Storage utilities for spigot servers.
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

package io.ib67.astralflow.manager;

import io.ib67.astralflow.machines.IMachine;
import io.ib67.astralflow.storage.IMachineStorage;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class MachineManagerImpl implements IMachineManager {
    private final IMachineStorage machineStorage;
    private final Map<UUID, IMachine> cache = new HashMap<>(64);

    @Override
    public IMachine getMachine(UUID uuid) {
        return cache.computeIfAbsent(uuid, k -> {
            var machine = machineStorage.readMachine(k).orElseThrow();
                machine.onLoad();
            return machine;
        });
    }

    @Override
    public IMachine getMachine(Location location) {
        return getLoadedMachines().stream().filter(e -> e.getLocation().distance(location) < 0.1).findFirst().orElse(null);
    }

    @Override
    public Collection<? extends IMachine> getLoadedMachines() {
        return cache.values();
    }

    @Override
    public Collection<UUID> getAllMachines() {
        return machineStorage.getMachines();
    }

    @Override
    public void registerMachine(IMachine machine) {
        // validations.
        var id = machine.getId();
        if (cache.containsKey(id)) {
            throw new IllegalArgumentException("This machine is already registered.");
        }
        cache.put(id, machine);
    }

    @Override
    public boolean isMachine(Block block) {
        return getLoadedMachines().stream().anyMatch(machine -> block.getLocation().distance(machine.getLocation()) < 0.1); // for some moving entities
    }

    @Override
    public void saveMachines() {
        getLoadedMachines().stream().filter(e -> {
            e.onUnload();
            return true;
        }).forEach(machineStorage::saveMachine);
    }
}
