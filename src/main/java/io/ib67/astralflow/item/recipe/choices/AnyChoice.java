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

package io.ib67.astralflow.item.recipe.choices;

import io.ib67.astralflow.AstralFlow;
import io.ib67.astralflow.item.recipe.IngredientChoice;
import io.ib67.astralflow.util.ItemStacks;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

/**
 * For any ingredients, including null.
 */
@ApiStatus.AvailableSince("0.1.0")
@RequiredArgsConstructor
public class AnyChoice implements IngredientChoice {
    @Getter
    private final short count;
    @Getter
    private final short durability;

    public AnyChoice() {
        this((short) 1, (short) 0);
    }

    @Override
    public List<? extends ItemStack> getRepresentativeItems() {
        return List.of(ItemStacks.builder(Material.STONE)
                .displayName(AstralFlow.getInstance().getSettings().locale.anyItemName)
                .build());
    }

    @Override
    public boolean test(ItemStack itemStack) {
        if (getDurability() > 0) {
            if (itemStack == null) return false;
            var meta = itemStack.getItemMeta();
            if (meta instanceof Damageable damageable) {
                if (damageable.getDamage() - getDurability() < 0) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }
}
