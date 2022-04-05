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

package io.ib67.astralflow.internal.storage.impl;

import io.ib67.astralflow.internal.serialization.MachineStorageHelper;
import io.ib67.astralflow.internal.storage.MachineSerializer;
import io.ib67.astralflow.manager.IFactoryManager;
import io.ib67.astralflow.manager.IMachineManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.BiFunction;

@RequiredArgsConstructor
@Getter
public enum MachineStorageType implements BiFunction<IFactoryManager, IMachineManager, MachineSerializer> {
    JSON(0, MachineStorageHelper::new);

    private final int typeIndex;
    private final BiFunction<IFactoryManager, IMachineManager, MachineSerializer> factory;

    public static MachineStorageType getType(int index) {
        return switch (index) {
            case 0 -> JSON;
            default -> throw new IllegalArgumentException("Invalid type index");
        };
    }

    @Override
    public MachineSerializer apply(IFactoryManager factoryManager, IMachineManager manager) {
        return this.factory.apply(factoryManager, manager);
    }
}
