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

package io.ib67.astralflow.api.factories;

import io.ib67.astralflow.machines.IMachine;
import io.ib67.astralflow.machines.IMachineData;
import io.ib67.astralflow.machines.factories.IMachineFactory;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;

import java.util.UUID;
import java.util.function.BiFunction;

@RequiredArgsConstructor
public class StatelessMachineFactory<T extends IMachine> implements IMachineFactory<T, IMachineData> {
    private final BiFunction<Location, UUID, T> factory;

    @Override
    public T createMachine(Location location) {
        return factory.apply(location, null);
    }

    @Override
    public T createMachine(Location location, UUID uuid) {
        return factory.apply(location, uuid);
    }

    @Override
    public T createMachine(T anotherMachine) {
        return factory.apply(anotherMachine.getLocation(), null);
    }

    @Override
    public T createMachine(Location location, IMachineData initialState) {
        return createMachine(location);
    }

    @Override
    public T createMachine(Location location, UUID uuid, IMachineData initialState) {
        return createMachine(location, uuid);
    }
}
