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
import io.ib67.astralflow.item.factory.ItemPrototypeFactory;
import io.ib67.util.Lazy;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

// actually this should be abstract class but thats too late when I found that
public interface IngredientChoice extends Predicate<ItemStack>, Consumer<ItemStack> {
    short getCount();

    short getDurability();

    List<? extends ItemStack> getRepresentativeItems();

    @Override
    default void accept(ItemStack itemStack) {
        var result = itemStack.getAmount() - getCount();
        if (result == 0) {
            itemStack.setType(Material.AIR);
        } else {
            itemStack.setAmount(result);
        }

        if (itemStack.hasItemMeta()) {
            var im = itemStack.getItemMeta();
            if (im instanceof Damageable) {
                var dm = ((Damageable) im);
                if (dm.getDamage() - getDurability() <= 0) {
                    itemStack.setType(Material.AIR);
                    // broken!
                } else {
                    dm.setDamage(dm.getDamage() - getDurability());
                }
            }
        }
    }

    @Getter
    final class MaterialChoice implements IngredientChoice {
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
            return material.contains(itemStack.getType());
        }

        @Override
        public List<ItemStack> getRepresentativeItems() {
            return compiledRItems.get();
        }
    }

    @RequiredArgsConstructor
    @Getter
    final class ExactItemChoice implements IngredientChoice {
        private final short count;
        private final short durability;
        private final List<ItemStack> material;

        public ExactItemChoice(ItemStack... material) {
            this((short) 1, (short) 0, List.of(material));
        }

        @Override
        public boolean test(ItemStack itemStack) {
            return material.stream().anyMatch(e -> e.isSimilar(itemStack));
        }

        @Override
        public List<? extends ItemStack> getRepresentativeItems() {
            return material;
        }
    }

    @Getter
    final class AstralItemChoice implements IngredientChoice {
        private final short count;
        private final short durability;
        private final Set<String> materials;
        private Lazy<Set<String>, List<ItemStack>> compiledRItems = Lazy.by(t ->
                t.stream().map(e -> AstralFlow.getInstance().getItemRegistry().getRegistry(e))
                        .map(ItemPrototypeFactory::getPrototype).collect(Collectors.toList())
        );

        public AstralItemChoice(String... oredictIds) {
            this((short) 1, (short) 0, oredictIds);
        }

        public AstralItemChoice(short durability, short count, String... oredictIds) {
            this.durability = durability;
            this.count = count;
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

        @Override
        public List<? extends ItemStack> getRepresentativeItems() {
            return compiledRItems.get();
        }
    }
}
