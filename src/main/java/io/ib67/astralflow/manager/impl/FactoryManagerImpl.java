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

import io.ib67.astralflow.machines.IMachine;
import io.ib67.astralflow.machines.IState;
import io.ib67.astralflow.machines.factories.IMachineFactory;
import io.ib67.astralflow.manager.IFactoryManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class FactoryManagerImpl implements IFactoryManager {
    private final Map<Class<? extends IMachine>, IMachineFactory<?, ?>> machineFactories = new HashMap<>();

    @SuppressWarnings("unchecked")
    @Override
    public <T extends IMachine, S extends IState> IMachineFactory<T, S> getMachineFactory(Class<T> type) {
        return (IMachineFactory<T, S>) machineFactories.get(type);
    }

    @Override
    public Collection<? extends IMachineFactory<?, ?>> getMachineFactories() {
        return machineFactories.values();
    }

    @Override
    public <T extends IMachine, S extends IState> boolean register(Class<T> claz, IMachineFactory<T, S> factory) {
        if (machineFactories.containsKey(claz)) {
            return false;
        }
        machineFactories.put(claz, factory);
        return true;
    }

    @Override
    public <T extends IMachine, S extends IState> boolean unregister(Class<T> type) {
        return machineFactories.containsKey(type) && machineFactories.remove(type) != null;
    }
}
