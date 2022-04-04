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

package io.ib67.astralflow.api.item.dummy;

import io.ib67.astralflow.item.ItemKey;
import io.ib67.astralflow.item.builder.ItemBuilder;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;

/**
 * Item without action.
 *
 * @param key  item key
 * @param item prototype
 */
@ApiStatus.AvailableSince("0.1.0")
@RequiredArgsConstructor
public record DummyItem(
        ItemKey key,
        ItemStack item,
        DummyItemProperty property
) {
    public DummyItem {
        for (FetchMethod fetchMethod : property.fetchMethods()) {
            fetchMethod.init(() -> key.createNewItem().asItemStack());
        }
    }

    /**
     * Utility method to create a dummy item fastly.
     */
    public static ItemKey registerItem(ItemKey key, ItemStack item, @Nullable String oreDict) {
        ItemBuilder.of(DummyCategory.INSTANCE)
                .oreDict(oreDict)
                .prototype(new DummyItem(key, item, new DummyItemProperty(Collections.emptyList())))
                .register();
        return key;
    }
}