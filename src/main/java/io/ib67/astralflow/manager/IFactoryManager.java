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
import io.ib67.astralflow.machines.IState;
import io.ib67.astralflow.machines.factories.IBlockItemFactory;
import io.ib67.astralflow.machines.factories.IMachineFactory;

import java.util.Collection;

public interface IFactoryManager {
    <T extends IMachine, S extends IState> IMachineFactory<T, S> getMachineFactory(Class<T> type);

    Collection<? extends IMachineFactory<?, ?>> getMachineFactories();

    <T extends IMachine> IBlockItemFactory<T> getBlockItemFactory(Class<T> type);

    Collection<? extends IBlockItemFactory<?>> getBlockItemFactories();

    <T extends IMachine, S extends IState> boolean register(Class<T> clazz, IMachineFactory<T, S> factory);

    <T extends IMachine> boolean register(Class<T> clazz, IBlockItemFactory<T> factory);

    <T extends IMachine, S extends IState> boolean unregister(Class<T> type);
}
