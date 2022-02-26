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

import io.ib67.Util;
import io.ib67.astralflow.internal.RecipeHelper;
import io.ib67.astralflow.item.recipe.AstralRecipe;
import io.ib67.astralflow.item.recipe.IngredientChoice;
import io.ib67.util.Lazy;
import io.ib67.util.Randomly;
import io.ib67.util.bukkit.Log;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class Shaped implements AstralRecipe {
    private static final ItemStack PLACEHOLDER = new ItemStack(Material.STONE);
    private final NamespacedKey key;
    private final IngredientChoice[] originMatrix; // todo: performance issues.
    private final Lazy<IngredientChoice[], Integer> compiledHash = Lazy.by(t ->
            RecipeHelper.generateMatrixPatternHash(
                    RecipeHelper.toStringMatrix(
                            Arrays.stream(t).map(e ->
                                    Util.or(Randomly.pickOrNull(e.getRepresentativeItems()), PLACEHOLDER)
                            ).toArray(ItemStack[]::new)
                    )
            )
    );
    private Supplier<ItemStack> factory;

    public Shaped(NamespacedKey key, IngredientChoice[] originMatrix) {
        this.key = key;
        this.originMatrix = originMatrix;
    }

    @Override
    public ItemStack produceResult() {
        return factory.get();
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

    public int getCompiledHash() {
        return compiledHash.get(originMatrix);
    }

    @Override
    public boolean test(ItemStack[] itemStacks) {
        // check for patterns.

        var paramHash = RecipeHelper.generateMatrixPatternHash(RecipeHelper.toStringMatrix(itemStacks));
        if (paramHash != compiledHash.get(originMatrix)) {
            return false;
        }
        if (originMatrix.length != itemStacks.length) {
            // actually it shouldn't happen.
            Log.warn("Recipe matrix length mismatch. This shouldn't happen! KEY: " + key + ". Report it to the author of " + key.getNamespace());
            return false;
        }
        // check for ingredients.
        for (int i = 0; i < originMatrix.length; i++) {
            var choice = originMatrix[i];
            var item = itemStacks[i];
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
        var tran = Arrays.copyOf(itemStacks, itemStacks.length);
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
                throw new IllegalArgumentException("Invalid item stacks for this recipe.");
            }
            item = choice.apply(item);
        }
        return tran; // todo: @BEFORE_RELEASE@ TEST @SECURITY@
    }

    @RequiredArgsConstructor
    public static class ShapedBuilder {
        private final NamespacedKey key;
        private String[] stringMatrix;
        private Map<Character, IngredientChoice> itemMap = new HashMap<>(8); // most recipes matches.
        private Supplier<ItemStack> result;

        public ShapedBuilder shape(String... matrix) {
            stringMatrix = RecipeHelper.populateEmptyRows(RecipeHelper.leftAlignMatrix(matrix));
            return this;
        }

        public ShapedBuilder setResult(Supplier<ItemStack> result) {
            this.result = result;
            return this;
        }

        public ShapedBuilder setIngredient(char key, IngredientChoice choice) {
            itemMap.put(key, choice);
            return this;
        }

        public Shaped build() {
            // compiles matrix.
            IngredientChoice[] matrix = new IngredientChoice[9];
            for (int i = 0; i < stringMatrix.length; i++) {
                var row = stringMatrix[i].toCharArray();
                for (int i1 = 0; i1 < row.length; i1++) {
                    matrix[i * 3 + i1] = itemMap.getOrDefault(row[i1], null);
                }
            }
            var r = new Shaped(key, matrix);
            r.setResult(result);
            return r;
        }
    }
}
