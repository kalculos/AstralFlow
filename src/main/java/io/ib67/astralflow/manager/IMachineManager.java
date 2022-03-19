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

package io.ib67.astralflow.manager;

import io.ib67.astralflow.machines.IMachine;
import io.ib67.astralflow.scheduler.TickReceipt;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.UUID;

@ApiStatus.AvailableSince("0.1.0")
public interface IMachineManager {
    void setupMachine(IMachine machine, boolean update);

    boolean isRegistered(UUID uuid);

    IMachine getAndLoadMachine(Location location);

    IMachine getAndLoadMachine(UUID id);

    void deactivateMachine(IMachine machine);

    void activateMachine(IMachine machine);

    Collection<? extends IMachine> getLoadedMachines();

    Collection<? extends Location> getAllMachines();

    void registerMachine(IMachine machine);

    boolean isMachine(Block block);

    void saveMachines();

    boolean removeAndTerminateMachine(IMachine machine);

    void terminateMachine(IMachine machine);

    TickReceipt<IMachine> getReceiptByMachine(IMachine machine);
}
