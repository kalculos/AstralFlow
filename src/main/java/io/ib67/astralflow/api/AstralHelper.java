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

package io.ib67.astralflow.api;

import com.google.gson.annotations.Since;
import io.ib67.astralflow.AstralFlow;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;

/**
 * Functional class providing utility methods
 */
@ApiStatus.AvailableSince("0.1.0")
public enum AstralHelper {
    @Deprecated
    USELESS_INSTANCE;

    public static boolean isItem(ItemStack itemStack) {
        return AstralFlow.getInstance().getItemRegistry().isItem(itemStack);
    }

    public static boolean hasMachine(Location location) {
        return AstralFlow.getInstance().getMachineManager().isMachine(location.getBlock());
    }

    public static boolean hasMachine(Block block) {
        return AstralFlow.getInstance().getMachineManager().isMachine(block);
    }

    public static Location purifyLocation(Location location) {
        return new Location(location.getWorld(), Location.locToBlock(location.getX()), Location.locToBlock(location.getY()), Location.locToBlock(location.getZ()));
    }

    // Only compares for block x-y-z
    public static boolean equalsLocationFuzzily(Location l1, Location l2) {
        return l1 != null && l2 != null
                && l1.getWorld() == l2.getWorld()
                && l1.getWorld() != null
                && Double.doubleToLongBits(Location.locToBlock(l1.getX())) == Double.doubleToLongBits(Location.locToBlock(l2.getX()))
                && Double.doubleToLongBits(Location.locToBlock(l1.getY())) == Double.doubleToLongBits(Location.locToBlock(l2.getY()))
                && Double.doubleToLongBits(Location.locToBlock(l1.getZ())) == Double.doubleToLongBits(Location.locToBlock(l2.getZ())); // we don't care yaw and pitch.
    }
}
