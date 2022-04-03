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

import io.ib67.astralflow.item.AstralItem;
import io.ib67.astralflow.item.ItemKey;
import io.ib67.astralflow.item.ItemState;
import io.ib67.astralflow.item.StateScope;
import io.ib67.astralflow.item.factory.ItemPrototypeFactory;
import io.ib67.astralflow.item.oredict.IOreDict;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;

@ApiStatus.AvailableSince("0.1.0")
public interface ItemRegistry {
    default void registerItem(ItemPrototypeFactory item) {
        registerItem(item, null);
    }

    void registerItem(ItemPrototypeFactory item, String oredict);

    boolean isItem(ItemStack item);

    IOreDict getOreDict();

    Collection<? extends ItemPrototypeFactory> getItemPrototypes();

    ItemPrototypeFactory getRegistry(ItemKey key);

    Optional<ItemPrototypeFactory> getRegistry(ItemStack itemStack);

    AstralItem createItem(ItemKey key);

    @ApiStatus.Experimental
    @Nullable
    ItemState getState(ItemStack itemStack, StateScope stateScope);

    @ApiStatus.Internal
    void saveState(ItemStack itemStack, StateScope scope, ItemState state);

    @Nullable
    default ItemState getState(ItemStack itemStack) {
        return getState(itemStack, StateScope.USER_ITEM);
    }
}
