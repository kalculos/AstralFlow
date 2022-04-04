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

package io.ib67.astralflow.api.item;

import io.ib67.astralflow.item.ItemKey;
import io.ib67.astralflow.item.LogicalHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import static java.util.Objects.requireNonNull;

/**
 * Subclass this to make your own item!
 */
@ApiStatus.AvailableSince("0.1.0")
public abstract class ItemBase implements LogicalHolder {
    protected final ItemKey itemKey;
    protected final ItemStack prototype;

    protected ItemBase(ItemKey itemKey, ItemStack prototype) {
        this.itemKey = requireNonNull(itemKey);
        this.prototype = requireNonNull(prototype);
    }

    @Override
    public ItemKey getId() {
        return itemKey;
    }
}
