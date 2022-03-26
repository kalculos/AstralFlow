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

import io.ib67.astralflow.internal.RecipeHelper;
import io.ib67.astralflow.item.recipe.AstralRecipe;
import io.ib67.astralflow.item.recipe.IngredientChoice;
import io.ib67.astralflow.item.recipe.RecipeType;
import io.ib67.util.bukkit.Log;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class Shaped implements AstralRecipe {
    private static final ItemStack PLACEHOLDER = new ItemStack(Material.STONE);
    private final NamespacedKey key;
    private final IngredientChoice[] originMatrix; // todo: performance issues.
    private Supplier<ItemStack> factory;
    private ItemStack demo;

    private Shaped(NamespacedKey key, IngredientChoice[] originMatrix) {
        this.key = key;
        this.originMatrix = originMatrix;
    }

    public static ShapedBuilder of(NamespacedKey key, Supplier<ItemStack> supplier) {
        return new ShapedBuilder(key).setResult(supplier);
    }

    public static ShapedBuilder of(NamespacedKey key) {
        return new ShapedBuilder(key);
    }

    @Override
    public RecipeType getRecipeType() {
        return RecipeType.CRAFTING;
    }

    @Override
    public ItemStack produceResult() {
        return factory.get();
    }

    @Override
    public ItemStack getPrototype() {
        return demo;
    }

    @Override
    public void setPrototype(ItemStack itemStack) {
        this.demo = itemStack;
    }

    @Override
    public IngredientChoice[] getMatrix() {
        return originMatrix;
    }

    @Override
    public void setResult(Supplier<ItemStack> prototype) {
        this.factory = prototype;
    }

    @NotNull
    @Override
    public NamespacedKey getKey() {
        return key;
    }

    @Override
    public boolean test(ItemStack[] itemStacks) {
        // check for patterns.
        //var matrix = RecipeHelper.populateEmptyRows(RecipeHelper.leftAndUpAlignMatrix(RecipeHelper.toStringMatrix(itemStacks)));
        var matrix = RecipeHelper.populateEmptyRows(RecipeHelper.leftAndUpAlignMatrix(RecipeHelper.toStringMatrix(itemStacks)));
        if (originMatrix.length != itemStacks.length) {
            // actually it shouldn't happen.
            Log.warn("Recipe matrix length mismatch. This shouldn't happen! KEY: " + key + ". Report it to the author of " + key.getNamespace());
            return false;
        }
        // check for ingredients.
        var aligned = RecipeHelper.leftAlignMatrixItems(itemStacks);
        for (int i = 0; i < originMatrix.length; i++) {
            var choice = originMatrix[i];
            var item = aligned[i];
            if (choice == null) {
                if (item != null) {
                    return false;
                }
                continue;
            }
            if (!choice.test(item)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack[] apply(ItemStack[] itemStacks) {
        if (!test(itemStacks)) {
            throw new IllegalArgumentException("Invalid item stacks for this recipe.");
        }
        var tran = RecipeHelper.leftAlignMatrixItems(Arrays.copyOf(itemStacks, itemStacks.length));
        for (int i = 0; i < originMatrix.length; i++) {
            var choice = originMatrix[i];
            var item = tran[i];
            if (choice == null) {
                if (item != null) {
                    throw new IllegalArgumentException("Invalid item stacks for this recipe.");
                }
                continue;
            }
            if (!choice.test(item)) {
                throw new IllegalArgumentException("Invalid item stacks for this recipe. Exception: " + choice.getRepresentativeItems().get(0) + " but got " + item);
            }
            tran[i] = choice.apply(item);
        }
        return tran;
    }

    @RequiredArgsConstructor
    public static class ShapedBuilder {
        private final NamespacedKey key;
        private String[] stringMatrix;
        private final Map<Character, IngredientChoice> itemMap = new HashMap<>(8); // most recipes matches.
        private Supplier<ItemStack> result;
        private ItemStack demo;

        public ShapedBuilder shape(String... matrix) {
            stringMatrix = RecipeHelper.populateEmptyRows(RecipeHelper.leftAndUpAlignMatrix(matrix));
            return this;
        }

        public ShapedBuilder setResult(Supplier<ItemStack> result) {
            this.result = result;
            return this;
        }

        public ShapedBuilder demoItem(ItemStack item) {
            this.demo = item;
            return this;
        }

        public ShapedBuilder setIngredient(char key, IngredientChoice choice) {
            itemMap.put(key, choice);
            return this;
        }

        public Shaped build() {
            // compiles matrix.
            if (stringMatrix == null) {
                throw new NullPointerException("Matrix is null. " + key);
            }

            stringMatrix = RecipeHelper.populateEmptyRows(RecipeHelper.leftAndUpAlignMatrix(stringMatrix));
            IngredientChoice[] matrix = new IngredientChoice[9];
            for (int i = 0; i < stringMatrix.length; i++) {
                var row = stringMatrix[i].toCharArray();
                for (int i1 = 0; i1 < row.length; i1++) {
                    if (row[i1] == ' ') {
                        matrix[i * 3 + i1] = null;
                        continue;
                    }
                    int finalI = i1;
                    matrix[i * 3 + i1] = Optional.ofNullable(itemMap.getOrDefault(row[i1], null)).orElseThrow(() -> new IllegalArgumentException("Invalid ingredient " + row[finalI] + " for " + key));
                }
            }
            var r = new Shaped(key, matrix);
            r.setResult(result);
            r.setPrototype(demo == null ? new ItemStack(Material.STONE) : demo);
            return r;
        }
    }
}
