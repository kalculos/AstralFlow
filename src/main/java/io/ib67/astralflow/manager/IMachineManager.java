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
import io.ib67.astralflow.scheduler.TickReceipt;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;

/**
 * Manager for machines, where you can activate machines and load machines.
 */
@ApiStatus.AvailableSince("0.1.0")
public interface IMachineManager {
    /**
     * Add a machine into tick queue and save it to machine storage.
     * <p>
     * equals {@link #activateMachine(IMachine)} and {@link #registerMachine(IMachine)}
     *
     * @param machine machine
     * @param update  should we tick that
     */
    default void setupMachine(IMachine machine, boolean update) {
        if (update) activateMachine(machine);
        registerMachine(machine);
    }

    /**
     * Get machine by location, also load it.
     *
     * @param location location of machine
     * @return machine
     */
    IMachine getAndLoadMachine(Location location);

    /**
     * Deactivate the machine (untick)
     *
     * @param machine machine to be deactivated
     */
    void deactivateMachine(IMachine machine);

    /**
     * Add the machine into tick queue
     *
     * @param machine machine
     */
    void activateMachine(IMachine machine);

    /**
     * Get all loaded machines, whatever they've activated.
     *
     * @return
     */
    Collection<? extends IMachine> getLoadedMachines();

    // todo: consider removal Collection<? extends Location> getAllMachines();

    /**
     * Register the machine and track it in storage
     *
     * @param machine
     */
    void registerMachine(IMachine machine);

    void unregisterMachine(IMachine machine);

    /**
     * Is the block a machine?
     *
     * @param block block
     * @return result
     */
    boolean isMachine(Block block);

    /**
     * Save machine datas.
     */
    void saveMachines();

    /**
     * Remove a machine from storage. This method doesn't deactivate the machine.
     *
     * @param machine
     * @return
     */
    boolean removeMachine(IMachine machine);


    /**
     * Get its tick receipt.
     *
     * @param machine machine
     * @return tick receipt.
     */
    TickReceipt<IMachine> getReceiptByMachine(IMachine machine);

    /**
     * Is it a tracked machine?
     *
     * @param machine machine
     * @return true or false
     */
    boolean isRegistered(IMachine machine);

    void updateMachineLocation(Location previousLocation, IMachine machine);

    /**
     * Helper method to remove a machine from storage and tick queue.
     *
     * @param machine
     */
    default void terminateAndRemoveMachine(IMachine machine) {
        removeMachine(machine);
        deactivateMachine(machine);
    }
}
