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

package io.ib67.astralflow.storage.impl.chunk;

import io.ib67.astralflow.machines.IMachine;
import io.ib67.astralflow.storage.MachineSerializer;
import io.ib67.astralflow.storage.impl.MachineStorageType;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.Location;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

public class InMemoryChunk {
    @Getter(AccessLevel.PACKAGE)
    private final ChunkMachineIndex index;
    @Getter(AccessLevel.PACKAGE)
    private final MachineData machineDatas;

    private final Map<Location, IMachine> machines;

    private final MachineStorageType defaultStorageType;
    private final MachineSerializer serializer;

    public InMemoryChunk(ChunkMachineIndex index, MachineData machineDatas, Map<Location, IMachine> machines, MachineStorageType defaultStorageType, MachineSerializer serializer) {
        Objects.requireNonNull(index);
        Objects.requireNonNull(machineDatas);
        Objects.requireNonNull(machines);
        Objects.requireNonNull(defaultStorageType);
        Objects.requireNonNull(serializer);

        this.defaultStorageType = defaultStorageType;
        this.serializer = serializer;
        this.index = index;
        this.machineDatas = machineDatas;
        this.machines = machines;
    }

    public IMachine getMachine(Location loc) {
        return machines.get(loc);
    }

    public Collection<? extends IMachine> getMachines() {
        return machines.values();
    }

    public void saveMachine(Location loc, IMachine state) {
        machines.put(loc, state);
        index.addMachine(state);
        machineDatas.save(loc, defaultStorageType, serializer.toData(state));
    }

    public void removeMachine(Location loc) {
        machines.remove(loc);
        index.removeMachine(loc);
        machineDatas.remove(loc);
    }
}
