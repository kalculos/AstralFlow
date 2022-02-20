/*
 *
 *   AstralFlow - Storage utilities for spigot servers.
 *   Copyright (C) 2022 iceBear67
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

import io.ib67.astralflow.AstralFlow;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Set;
import java.util.function.Predicate;

public interface AstralRecipeChoice extends Predicate<ItemStack> {

    @Getter
    public final class MaterialChoice implements AstralRecipeChoice {
        private final Set<Material> material;

        public MaterialChoice(Material... material) {
            this.material = Set.of(material);
        }

        @Override
        public boolean test(ItemStack itemStack) {
            return material.contains(itemStack.getType());
        }
    }

    @Getter
    public final class ExactItemChoice implements AstralRecipeChoice {
        private final Set<ItemStack> material;

        public ExactItemChoice(ItemStack... material) {
            this.material = Set.of(material);
        }

        @Override
        public boolean test(ItemStack itemStack) {
            return material.stream().anyMatch(e -> e.isSimilar(itemStack));
        }
    }

    @Getter
    public final class AstralItemChoice implements AstralRecipeChoice {
        private final Set<String> materials;

        public AstralItemChoice(String... oredictIds) {
            materials = Set.of(oredictIds);
        }

        @Override
        public boolean test(ItemStack itemStack) {
            var ir = AstralFlow.getInstance().getItemRegistry();
            var state = ir.getState(itemStack);
            if (state != null) {
                return materials.contains(state.getPrototypeKey());
            }
            return false;
        }
    }
}
