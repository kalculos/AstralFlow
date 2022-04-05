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

package io.ib67.astralflow.item.definitions;

import io.ib67.astralflow.item.ItemKey;
import io.ib67.astralflow.item.ItemState;
import io.ib67.astralflow.item.internal.factory.ItemPrototypeFactory;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class DummyStatelessItem implements ItemPrototypeFactory {
    private final ItemStack item = new ItemStack(Material.DIAMOND_SWORD);

    @Override
    public @NotNull ItemStack getPrototype() {
        return item;
    }

    @Override
    public @Nullable ItemState getStatePrototype() {
        return null;
    }

    @Override
    public ItemKey getId() {
        return ItemKey.from("test", "dummy_stateless_item");
    }
}
