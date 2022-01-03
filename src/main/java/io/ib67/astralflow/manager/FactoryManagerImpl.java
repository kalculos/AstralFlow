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
import io.ib67.astralflow.machines.IMachineData;
import io.ib67.astralflow.machines.factories.IBlockItemFactory;
import io.ib67.astralflow.machines.factories.IMachineFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class FactoryManagerImpl implements IFactoryManager {
    private final Map<Class<? extends IMachine>, IMachineFactory<?, ?>> machineFactories = new HashMap<>();
    private final Map<Class<? extends IMachine>, IBlockItemFactory<?>> blockItemFactories = new HashMap<>();

    @SuppressWarnings("unchecked")
    @Override
    public <T extends IMachine, S extends IMachineData> IMachineFactory<T, S> getMachineFactory(Class<T> type) {
        return (IMachineFactory<T, S>) machineFactories.get(type);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<? extends IMachineFactory<?, ?>> getMachineFactories() {
        return machineFactories.values();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends IMachine> IBlockItemFactory<T> getBlockItemFactory(Class<T> type) {
        return (IBlockItemFactory<T>) blockItemFactories.get(type);
    }

    @Override
    public Collection<? extends IBlockItemFactory<?>> getBlockItemFactories() {
        return blockItemFactories.values();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IMachine, S extends IMachineData> boolean register(Class<T> claz, IMachineFactory<T, S> factory) {
        if (machineFactories.containsKey(claz)) {
            return false;
        }
        machineFactories.put(claz, factory);
        return true;
    }

    @Override
    public <T extends IMachine> boolean register(Class<T> clazz, IBlockItemFactory<T> factory) {
        return false;
    }

    @Override
    public <T extends IMachine, S extends IMachineData> boolean unregister(Class<T> type) {
        return machineFactories.containsKey(type) && machineFactories.remove(type) != null;
    }
}
