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

package io.ib67.astralflow.item.oredict;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.function.Predicate;

/**
 * An ore dictionary, where you can register same name for more items
 * You should consider using {@link io.ib67.astralflow.item.builder.ItemBuilder} instead of this class. This class may cause misleading and not very easy to understand.
 */
@ApiStatus.AvailableSince("0.1.0")
@ApiStatus.Internal
public interface IOreDict {
    /**
     * Register your item to the ore dictionary with your dictKey.
     *
     * @param prototype the item to register
     * @param dictKey   the key to register the item with
     * @return this oredict, for fluent use
     * @throws IllegalStateException if locked
     */
    IOreDict registerItem(String dictKey, ItemStack prototype, Predicate<ItemStack> tester);

    boolean matchItem(String oredictId, ItemStack itemStack);

    /**
     * This method will return a collection of all the registered items. Only for being shown to player
     *
     * @param oredictId
     * @return
     */
    Collection<? extends ItemStack> getItems(String oredictId);

}
