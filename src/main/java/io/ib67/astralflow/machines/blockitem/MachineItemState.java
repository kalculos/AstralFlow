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

package io.ib67.astralflow.machines.blockitem;

import io.ib67.astralflow.AstralFlow;
import io.ib67.astralflow.item.ItemState;
import io.ib67.astralflow.machines.IMachine;
import io.ib67.astralflow.machines.IState;
import io.ib67.util.bukkit.Log;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

/**
 * Use null machine when you're defining item prototype, we'll replace it with actual item state.
 */
@Setter
public class MachineItemState extends ItemState {

    private String prototype;
    @Getter
    private IState machineState;

    public MachineItemState(String prototype, String machineType, IState machineState) {
        super(prototype);
        this.prototype = machineType;
        this.machineState = machineState;
    }

    @SuppressWarnings("unchecked")
    public IMachine createMachine(Location location) {
        try {
            return AstralFlow.getInstance().getFactories().getMachineFactory((Class<? extends IMachine>) Class.forName(prototype)).createMachine(location, machineState);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Log.warn("Machine type " + prototype + " not found!");
        } catch (ClassCastException e) {
            e.printStackTrace();
            ;
            Log.warn("Machine type " + prototype + " is not a machine!");
        }
        return null;
    }
}
