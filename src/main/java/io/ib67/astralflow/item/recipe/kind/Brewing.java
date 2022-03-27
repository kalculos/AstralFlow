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

package io.ib67.astralflow.item.recipe.kind;

import io.ib67.astralflow.item.recipe.AstralRecipe;
import io.ib67.astralflow.item.recipe.IngredientChoice;
import io.ib67.astralflow.item.recipe.RecipeType;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Supplier;

public final class Brewing implements AstralRecipe {

    private final NamespacedKey key;
    private final IngredientChoice ingredient;
    private final IngredientChoice target;
    private Supplier<ItemStack> result;
    private ItemStack prototype;

    private Brewing(@NotNull NamespacedKey key, @NotNull Supplier<ItemStack> result, @NotNull IngredientChoice ingredient, @NotNull IngredientChoice target) {
        this.key = key;
        this.result = result;
        this.ingredient = ingredient;
        this.target = target;
    }

    public static Brewing of(@NotNull NamespacedKey key, @NotNull IngredientChoice ingredient, @NotNull IngredientChoice target, @NotNull Supplier<ItemStack> result) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(ingredient, "ingredient");
        Objects.requireNonNull(target, "target");
        Objects.requireNonNull(result, "result");
        return new Brewing(key, result, ingredient, target);
    }

    public static Brewing of(@NotNull NamespacedKey key, @NotNull IngredientChoice ingredient, @NotNull IngredientChoice target) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(ingredient, "ingredient");
        Objects.requireNonNull(target, "target");
        return new Brewing(key, null, ingredient, target);
    }

    @Override
    public RecipeType getRecipeType() {
        return RecipeType.BREWING;
    }

    @Override
    public ItemStack produceResult() {
        return result.get();
    }

    @Override
    public ItemStack getPrototype() {
        return prototype;
    }

    @Override
    public void setPrototype(ItemStack itemStack) {
        this.prototype = itemStack;
    }

    @Override
    public IngredientChoice[] getMatrix() {
        return new IngredientChoice[]{ingredient, target};
    }

    @Override
    public void setResult(Supplier<ItemStack> prototype) {
        this.result = prototype;
    }

    @Override
    public ItemStack[] apply(ItemStack[] itemStacks) {
        if (!test(itemStacks)) {
            throw new IllegalArgumentException("The given item stacks do not match the recipe");
        }
        var result = Arrays.copyOf(itemStacks, itemStacks.length);
        ingredient.apply(result[0]);
        target.apply(result[1]);
        return result;
    }

    @Override
    public boolean test(ItemStack[] itemStacks) {
        if (itemStacks.length != 2) {
            return false;
        }
        return ingredient.test(itemStacks[0]) && target.test(itemStacks[1]);
    }

    @NotNull
    @Override
    public NamespacedKey getKey() {
        return key;
    }
}
