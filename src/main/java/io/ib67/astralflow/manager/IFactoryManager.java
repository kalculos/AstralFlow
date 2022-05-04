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

package io.ib67.astralflow.manager;

import io.ib67.astralflow.machines.IMachine;
import io.ib67.astralflow.machines.factories.IMachineFactory;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;

@ApiStatus.AvailableSince("0.1.0")
public interface IFactoryManager {
    /**
     * Get a machine factory by a machine class.
     *
     * @param type The machine class.
     * @param <T>  The machine type.
     * @return The machine factory.
     * @throws IllegalArgumentException If the machine factory is not registered.
     */
    <T extends IMachine> IMachineFactory<T> getMachineFactory(Class<T> type);

    /**
     * Get all registered machine factories.
     *
     * @return The machine factories.
     */
    Collection<? extends IMachineFactory<?>> getMachineFactories();

    /**
     * Register a machine factory.
     *
     * @param clazz   The machine class.
     * @param factory The machine factory.
     * @param <T>     The machine type.
     * @return if already exists
     */
    <T extends IMachine> boolean register(Class<T> clazz, IMachineFactory<T> factory);

    /**
     * Unregister a machine factory.
     *
     * @param type The machine class.
     * @param <T>  The machine type.
     * @return if already exists
     */
    <T extends IMachine> boolean unregister(Class<T> type);
}
