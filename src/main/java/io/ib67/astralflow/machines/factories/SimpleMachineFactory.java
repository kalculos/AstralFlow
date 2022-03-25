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

package io.ib67.astralflow.machines.factories;

import io.ib67.astralflow.machines.IMachine;
import io.ib67.astralflow.machines.IState;
import lombok.SneakyThrows;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.util.UUID;

public class SimpleMachineFactory<M extends IMachine, S extends IState> implements IMachineFactory<M, S> {
    private final Class<M> machineClass;
    private int argLen = -1;
    private int locationSlot = -1;
    private int uuidSlot = -1;
    private int stateSlot = -1;

    private Constructor<M> constructor;


    public SimpleMachineFactory(Class<M> machineClass) {
        this.machineClass = machineClass;
        // initialization.
        for (Constructor<?> declaredConstructor : machineClass.getDeclaredConstructors()) {
            if (argLen >= 0) {
                constructor.setAccessible(true);
                break;
            }

            var types = declaredConstructor.getParameterTypes();
            argLen = types.length;
            constructor = (Constructor<M>) declaredConstructor;
            if (argLen > 3) {
                resetStates();
                continue;
            }
            for (int i = 0; i < types.length; i++) {
                if (types[i] == Location.class) {
                    if (locationSlot == -1) {
                        locationSlot = i;
                    } else {
                        resetStates();
                        break;
                    }
                } else if (types[i] == UUID.class) {
                    if (uuidSlot == -1) {
                        uuidSlot = i;
                    } else {
                        resetStates();
                        break;
                    }
                } else if (IState.class.isAssignableFrom(types[i])) {
                    if (stateSlot == -1) {
                        stateSlot = i;
                    } else {
                        resetStates();
                        break;
                    }
                }
            }
        }
        if (argLen <= 0) {
            throw new IllegalArgumentException("Cannot find any available constructor containing Location, UUID and IState, len <= 3 without repeating.");
        }
    }

    private Object[] composeArguments(Location loc, UUID uuid, S state) {
        var arg = new Object[argLen];
        if (locationSlot != -1) {
            arg[locationSlot] = loc;
        }
        if (uuidSlot != -1) {
            arg[uuidSlot] = uuid;
        }
        if (stateSlot != -1) {
            arg[stateSlot] = state;
        }
        return arg;
    }

    private void resetStates() {
        argLen = -1;
        locationSlot = -1;
        uuidSlot = -1;
        stateSlot = -1;
    }

    @Override
    @SneakyThrows
    public M createMachine(Location location, @Nullable UUID uuid, @Nullable S initialState) {
        return constructor.newInstance(composeArguments(location, uuid, initialState));
    }
}
