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

package io.ib67.astralflow.item.recipe;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

// actually this should be abstract class but that's too late when I found that
public interface IngredientChoice extends Predicate<ItemStack>, UnaryOperator<ItemStack> {
    short getCount();

    short getDurability();

    List<? extends ItemStack> getRepresentativeItems();

    @Override
    default ItemStack apply(ItemStack itemStack) {
        var result = itemStack.getAmount() - getCount();
        if (result == 0) {
            itemStack.setType(Material.AIR);
        } else {
            itemStack.setAmount(result);
        }

        if (itemStack.hasItemMeta()) {
            var im = itemStack.getItemMeta();
            if (im instanceof Damageable dm) {
                if (dm.getDamage() - getDurability() <= 0) {
                    itemStack.setType(Material.AIR);
                    // broken!
                } else {
                    dm.setDamage(dm.getDamage() - getDurability());
                }
            }
        }
        return itemStack;
    }

}
