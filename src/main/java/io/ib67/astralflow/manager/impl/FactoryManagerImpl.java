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

import io.ib67.astralflow.machines.AutoFactory;
import io.ib67.astralflow.machines.IMachine;
import io.ib67.astralflow.machines.internal.factories.IMachineFactory;
import io.ib67.astralflow.machines.internal.factories.SimpleMachineFactory;
import io.ib67.astralflow.manager.IFactoryManager;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class FactoryManagerImpl implements IFactoryManager {
    private final Map<Class<? extends IMachine>, IMachineFactory<?>> machineFactories = new HashMap<>();

    @SuppressWarnings("unchecked")
    @Override
    public <T extends IMachine> IMachineFactory<T> getMachineFactory(Class<T> type) {
        Objects.requireNonNull(type, "Type cannot be null");
        return (IMachineFactory<T>) machineFactories.computeIfAbsent(type, t -> {
            if (!t.isAnnotationPresent(AutoFactory.class)) {
                return null;
            }
            var annotation = t.getAnnotation(AutoFactory.class);
            if (annotation.value() == SimpleMachineFactory.class) {
                return new SimpleMachineFactory<>(t);
            } else {
                try {
                    return annotation.value().getDeclaredConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    throw new UnsupportedOperationException("Cannot instantiate factory from @AutoFactory for " + t + ", is there any available empty contructors?", e);
                }
            }
        });
    }

    @Override
    public Collection<? extends IMachineFactory<?>> getMachineFactories() {
        return machineFactories.values();
    }

    @Override
    public <T extends IMachine> boolean register(Class<T> claz, IMachineFactory<T> factory) {
        Objects.requireNonNull(claz, "Class cannot be null");
        Objects.requireNonNull(factory, "Factory cannot be null");

        if (machineFactories.containsKey(claz)) {
            return false;
        }

        machineFactories.put(claz, factory);
        return true;
    }

    @Override
    public <T extends IMachine> boolean unregister(Class<T> type) {
        Objects.requireNonNull(type, "Type cannot be null");
        return machineFactories.containsKey(type) && machineFactories.remove(type) != null;
    }
}
