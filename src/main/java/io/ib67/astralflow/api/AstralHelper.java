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

package io.ib67.astralflow.api;

import io.ib67.astralflow.AstralFlow;
import io.ib67.astralflow.item.LogicalHolder;
import io.ib67.astralflow.machines.IMachine;
import io.ib67.internal.util.bukkit.Log;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * Functional class providing utility methods
 */
@ApiStatus.AvailableSince("0.1.0")
public enum AstralHelper {
    ;

    /**
     * Check if the given item is an astral item.
     *
     * @param itemStack The item to check
     * @return True if the item is an astral item, false otherwise
     */
    public static boolean isItem(ItemStack itemStack) {
        return AstralFlow.getInstance().getItemRegistry().isItem(itemStack);
    }

    /**
     * Check if there is a machine at the given location.
     *
     * @param location The location to check
     * @return True if there is a machine at the given location, false otherwise
     */
    public static boolean hasMachine(Location location) {
        return AstralFlow.getInstance().getMachineManager().isMachine(location.getBlock());
    }

    /**
     * Check if the given block is a machine.
     *
     * @param block The block to check
     * @return True if the block is a machine, false otherwise
     */
    public static boolean hasMachine(Block block) {
        return AstralFlow.getInstance().getMachineManager().isMachine(block);
    }

    /**
     * Check if the given itemstack has a given logical holder.<br />
     * Usually used by {@link LogicalHolder} themselves, to check if the item is their.
     *
     * <pre>
     *     {@code
     * if (armorContent != null && AstralHelper.isHolder(armorContent, this)) {
     * finalDamage = finalDamage - costedDamage;
     * }
     * }
     * </pre>
     *
     * @param stack  The itemstack to check
     * @param holder The holder to check
     * @return True if the itemstack has the given holder, false otherwise
     */
    public static boolean isHolder(ItemStack stack, LogicalHolder holder) {
        return AstralFlow.getInstance().getItemRegistry().getRegistry(stack).filter(e -> e.getHolder() == holder).isPresent();
    }

    /**
     * Get the machine at the given location.
     *
     * @param block The block to check
     * @return The machine at the given location, or null if there is no machine at the given location
     */
    @Nullable
    public static IMachine getMachine(Block block) {
        return AstralFlow.getInstance().getMachineManager().getAndLoadMachine(block.getLocation());
    }

    /**
     * Clean the location, removing yaw, pitch (set to 0).
     *
     * @param location The location to clean
     * @return The cleaned location
     */
    @Contract("_ -> new")
    public static Location purifyLocation(Location location) {
        return (location.getYaw() != 0 && location.getPitch() != 0)
                ? new Location(location.getWorld(), Location.locToBlock(location.getX()), Location.locToBlock(location.getY()), Location.locToBlock(location.getZ()))
                : location;
    }

    /**
     * Check if the chunk is loaded
     *
     * @param loc The location to check
     * @return True if the chunk is loaded, false otherwise
     */
    public static boolean isChunkLoaded(Location loc) {
        return loc.getWorld().isChunkLoaded(loc.getBlockX() >> 4, loc.getBlockZ() >> 4);
    }

    /**
     * Utility to ensure context is in primary thread, very helpful to catch async operations.
     *
     * @param reason the reason for users.
     */
    public static void ensureMainThread(String reason) {
        if (!Bukkit.isPrimaryThread()) {
            Log.warn("Threads", "Thread " + Thread.currentThread().getName() + " is not the main thread but operating some resources that are not thread-safe. ");
            throw new IllegalCallerException("This method must be called from the main thread. Reason: " + reason);
        }
    }

    /**
     * Utility to compare two locations except for yaw and pitch
     *
     * @param l1 The first location
     * @param l2 The second location
     * @return True if the locations are equal, false otherwise
     */
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
