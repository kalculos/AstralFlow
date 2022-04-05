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

package io.ib67.astralflow.internal.storage.impl.chunk;

import io.ib67.astralflow.internal.storage.MachineSerializer;
import io.ib67.astralflow.internal.storage.impl.MachineStorageType;
import io.ib67.astralflow.machines.IMachine;
import io.ib67.astralflow.manager.IFactoryManager;
import io.ib67.astralflow.manager.IMachineManager;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class InMemoryChunkFactory {
    private final IFactoryManager factory;
    private final IMachineManager defaultMachineManager;
    private final MachineStorageType storageType;

    private final NamespacedKey machineIndexKey;
    private final NamespacedKey machineDataKey;

    private final Map<MachineStorageType, MachineSerializer> serializers = new EnumMap<>(MachineStorageType.class);

    public InMemoryChunkFactory(IFactoryManager factory, IMachineManager defaultMachineManager, MachineStorageType storageType, NamespacedKey machineIndexKey, NamespacedKey machineDataKey) {
        Objects.requireNonNull(factory, "factory cannot be null");
        Objects.requireNonNull(storageType, "storageType cannot be null");
        Objects.requireNonNull(machineIndexKey, "machineIndexKey cannot be null");
        Objects.requireNonNull(machineDataKey, "machineDataKey cannot be null");
        Objects.requireNonNull(defaultMachineManager, "defaultMachineManager cannot be null");
        this.defaultMachineManager = defaultMachineManager;
        this.machineIndexKey = machineIndexKey;
        this.machineDataKey = machineDataKey;
        this.factory = factory;
        this.storageType = storageType;
    }


    public InMemoryChunk loadChunk(Chunk chunk) {
        // load data.
        var pdc = chunk.getPersistentDataContainer();
        if (!pdc.has(machineIndexKey, MachineIndexTag.INSTANCE)) {
            // initialization.
            var index = new ChunkMachineIndex(new HashMap<>(), chunk.getX(), chunk.getZ());
            pdc.set(machineIndexKey, MachineIndexTag.INSTANCE, index);
        }

        // load index
        var chunksIndex = pdc.get(machineIndexKey, MachineIndexTag.INSTANCE);
        if (!chunksIndex.isHasMachines()) {
            return new InMemoryChunk(chunksIndex, new MachineData(chunk.getX(), chunk.getZ()), new HashMap<>(), storageType, getSerializer(storageType));
        } else {
            var machines = pdc.get(machineDataKey, MachineDataTag.INSTANCE);
            var machinesMap = new HashMap<Location, IMachine>();
            // initialize machines.
            for (Map.Entry<Location, String> entry : chunksIndex.getEntries()) {
                var location = entry.getKey();
                var type = entry.getValue();
                var machineData = machines.getData(location);

                var machine = getSerializer(machineData.key).fromData(machineData.value);
                location = machine.getLocation(); // to keep same reference with the machine state.
                machinesMap.put(location, machine);
            }
            return new InMemoryChunk(chunksIndex, machines, machinesMap, storageType, getSerializer(storageType));
        }
    }

    private MachineSerializer getSerializer(MachineStorageType type) {
        return serializers.computeIfAbsent(type, k -> k.apply(factory, defaultMachineManager));
    }
}
