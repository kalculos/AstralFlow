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
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.Collection;
import java.util.UUID;

public interface IMachineManager {
    /**
     * @param uuid
     * @return
     * @throws IllegalArgumentException if uuid not registered.
     */
    IMachine getAndLoadMachine(UUID uuid);

    IMachine getAndLoadMachine(Location location);

    void deactivateMachine(IMachine machine);

    void activateMachine(IMachine machine);

    Collection<? extends IMachine> getLoadedMachines();

    Collection<? extends UUID> getAllMachines();

    void registerMachine(IMachine machine);

    boolean isMachine(Block block);

    void saveMachines();

    boolean removeAndTerminateMachine(IMachine machine);
}
