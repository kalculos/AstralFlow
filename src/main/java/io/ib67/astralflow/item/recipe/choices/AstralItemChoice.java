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
import io.ib67.astralflow.internal.item.state.InternalItemState;
import io.ib67.astralflow.item.ItemKey;
import io.ib67.astralflow.item.StateScope;
import io.ib67.astralflow.item.factory.ItemPrototypeFactory;
import io.ib67.astralflow.item.recipe.IngredientChoice;
import io.ib67.util.Lazy;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class represents an item choice for astral items.<br />
 * {@link io.ib67.astralflow.item.recipe.AstralRecipe}
 */
@ApiStatus.AvailableSince("0.1.0")
@Getter
public class AstralItemChoice implements IngredientChoice {
    private final short count;
    private final short durability;
    private final Set<ItemKey> materials;
    private final Lazy<Set<ItemKey>, List<ItemStack>> compiledRItems = Lazy.by(t ->
            t.stream().map(e -> AstralFlow.getInstance().getItemRegistry().getRegistry(e))
                    .map(ItemPrototypeFactory::getPrototype).collect(Collectors.toList())
    );

    public AstralItemChoice(ItemKey... itemIds) {
        this((short) 1, (short) 0, itemIds);
    }

    public AstralItemChoice(short durability, short count, ItemKey... itemIds) {
        this.durability = durability;
        this.count = count;
        materials = Set.of(itemIds);
    }

    @Override
    public boolean test(ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }
        if (getDurability() > 0) {
            var meta = itemStack.getItemMeta();
            if (meta instanceof Damageable damageable) {
                if (damageable.getDamage() - getDurability() < 0) {
                    return false;
                }
            } else {
                return false;
            }
        }
        var ir = AstralFlow.getInstance().getItemRegistry();
        var isItem = ir.isItem(itemStack);
        if (isItem) {
            var state = (InternalItemState) ir.getState(itemStack, StateScope.INTERNAL_ITEM);
            if (state.getPrototypeKey() == null) {
                return false;
            }
            return materials.contains(state.getPrototypeKey());
        }
        return false;
    }

    @Override
    public List<? extends ItemStack> getRepresentativeItems() {
        return compiledRItems.get();
    }
}
