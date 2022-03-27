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
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Supplier;

@ApiStatus.AvailableSince("0.1.0")
public final class Smelting implements AstralRecipe {
    private final NamespacedKey key;
    private final IngredientChoice fuel;
    private final IngredientChoice input;
    private Supplier<ItemStack> result;
    private ItemStack resultPrototype;

    private Smelting(NamespacedKey key, IngredientChoice fuel, IngredientChoice input, Supplier<ItemStack> result) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(fuel, "fuel");
        Objects.requireNonNull(input, "input");
        this.key = key;
        this.fuel = fuel;
        this.input = input;
        this.result = result;
    }

    public static Smelting of(NamespacedKey key, IngredientChoice fuel, IngredientChoice input, Supplier<ItemStack> result) {
        return new Smelting(key, fuel, input, result);
    }

    public static Smelting of(NamespacedKey key, IngredientChoice fuel, IngredientChoice input) {
        return of(key, fuel, input, null);
    }

    @Override
    public RecipeType getRecipeType() {
        return RecipeType.SMELTING;
    }

    @Override
    public ItemStack produceResult() {
        return result.get();
    }

    @Override
    public ItemStack getPrototype() {
        return resultPrototype;
    }

    @Override
    public void setPrototype(ItemStack itemStack) {
        this.resultPrototype = itemStack;
    }

    @Override
    public IngredientChoice[] getMatrix() {
        return new IngredientChoice[]{
                input, fuel
        };
    }

    @Override
    public void setResult(Supplier<ItemStack> prototype) {
        this.result = prototype;
    }

    @Override
    public ItemStack[] apply(ItemStack[] itemStacks) {
        if (!test(itemStacks)) {
            throw new IllegalArgumentException("Invalid item stacks for this recipe.");
        }
        var input = itemStacks[0];
        var fuel = itemStacks[1];
        var result = Arrays.copyOf(itemStacks, itemStacks.length);
        result[0] = this.input.apply(input);
        result[1] = this.fuel.apply(fuel);
        return result;
    }

    @Override
    public boolean test(ItemStack[] itemStacks) {
        if (itemStacks.length != 2) {
            throw new IllegalArgumentException("Invalid matrix length for this smelting recipe. " + Arrays.toString(itemStacks));
        }
        var input = itemStacks[0];
        var fuel = itemStacks[1];
        return this.fuel.test(fuel) && this.input.test(input);
    }

    @NotNull
    @Override
    public NamespacedKey getKey() {
        return key;
    }
}
