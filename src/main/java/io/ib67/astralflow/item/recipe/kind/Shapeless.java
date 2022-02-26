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

package io.ib67.astralflow.item.recipe.kind;

import io.ib67.astralflow.AstralFlow;
import io.ib67.astralflow.item.recipe.AstralRecipe;
import io.ib67.astralflow.item.recipe.AstralRecipeChoice;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Getter
public class Shapeless implements AstralRecipe {
    private final List<AstralRecipeChoice> choices;
    private final NamespacedKey key;
    private Supplier<ItemStack> resultSupplier;

    private Shapeless(List<AstralRecipeChoice> choices, NamespacedKey key) {
        this.choices = choices;
        this.key = key;
    }

    public static ShapelessBuilder of(Plugin plugin, String key, Supplier<ItemStack> result) {
        return new ShapelessBuilder(new NamespacedKey(plugin, key));
    }

    public static ShapelessBuilder of(Plugin plugin, String key, ItemStack result) {
        return of(plugin, key, () -> result);
    }

    public static ShapelessBuilder of(Plugin plugin, String key, String resultItemId) {
        return of(plugin, key, () -> AstralFlow.getInstance().getItemRegistry().getRegistry(resultItemId).getPrototype().clone());
    }

    @Override
    public ItemStack getResult() {
        return resultSupplier.get();
    }

    @Override
    public void setResult(Supplier<ItemStack> prototype) {
        this.resultSupplier = prototype;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class ShapelessBuilder {
        private final NamespacedKey key;
        private final List<AstralRecipeChoice> choices = new ArrayList<>();

        public ShapelessBuilder addIngredients(AstralRecipeChoice... choices) {
            this.choices.addAll(List.of(choices));
            return this;
        }

        public Shapeless build() {
            return new Shapeless(choices, key);
        }
    }
}
