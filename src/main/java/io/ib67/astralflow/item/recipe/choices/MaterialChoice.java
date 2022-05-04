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

import io.ib67.astralflow.item.recipe.IngredientChoice;
import io.ib67.util.Lazy;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A {@link IngredientChoice} that matches {@link Material}<br />
 * Also see {@link io.ib67.astralflow.item.recipe.AstralRecipe}.
 */
@ApiStatus.AvailableSince("0.1.0")
@Getter
public final class MaterialChoice implements IngredientChoice {
    private final Set<Material> material;
    private final Lazy<Set<Material>, List<ItemStack>> compiledRItems = Lazy.by(t -> t.stream().map(ItemStack::new).collect(Collectors.toUnmodifiableList()));
    private final short count;
    private final short durability;

    public MaterialChoice(Material... material) {
        this((short) 1, (short) 0, material);
    }

    public MaterialChoice(short count, short durability, Material... material) {
        this.count = count;
        this.durability = durability;
        this.material = Set.of(material);
    }

    @Override
    public boolean test(ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }
        return material.contains(itemStack.getType());
    }

    @Override
    public List<ItemStack> getRepresentativeItems() {
        return compiledRItems.get(material);
    }
}
