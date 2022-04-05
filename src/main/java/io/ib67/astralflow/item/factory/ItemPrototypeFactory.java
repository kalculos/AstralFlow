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

package io.ib67.astralflow.item.factory;

import io.ib67.astralflow.item.ItemKey;
import io.ib67.astralflow.item.ItemState;
import io.ib67.astralflow.item.LogicalHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The definition of prototypes for items. Determines how does the item look like ({@link #getPrototype()}) and its empty state ({@link #getStatePrototype()})
 * <p>
 */
@ApiStatus.AvailableSince("0.1.0")
public interface ItemPrototypeFactory {

    /**
     * Create a sample itemstack, which will be processed for a real astral item. (attach data)
     * You shouldn't modify the itemstack, always clone it
     *
     * @return sample itemstack
     */
    @Contract(pure = true)
    @NotNull
    ItemStack getPrototype();

    /**
     * The empty state.
     * You shouldn't modify it, always clone it
     *
     * @return null if there is a empty state
     */
    @Contract(pure = true)
    @Nullable // stateless item
    ItemState getStatePrototype();

    /**
     * The ItemKey of this item
     *
     * @return item key
     */
    @Contract(pure = true)
    ItemKey getId();

    /**
     * The final registry. this is for decorators.
     *
     * @return
     * @implSpec Decorators **must** return their decorated prototype.
     */
    default ItemPrototypeFactory getRegistry() {
        return this;
    }

    /**
     * The logical holder of this item. Such as {@link io.ib67.astralflow.api.item.machine.MachineItems}
     *
     * @return null if there is no logical holder
     */
    default LogicalHolder getHolder() {
        return null;
    }
}
