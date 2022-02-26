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

import com.google.common.collect.Lists;
import io.ib67.astralflow.internal.RecipeHelper;
import io.ib67.astralflow.item.recipe.kind.Shaped;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class RecipeRegistryImpl implements IRecipeRegistry {
    private final Map<HashHack, List<Shaped>> shapedRecipes = new HashMap<>(32);
    private final Map<NamespacedKey, AstralRecipe> recipesMap = new WeakHashMap<>();
    private final List<AstralRecipe> recipes = new ArrayList<>();

    @Override
    public IRecipeRegistry registerRecipe(AstralRecipe recipe) {
        recipesMap.put(recipe.getKey(), recipe);
        if (recipe instanceof Shaped) {
            var shaped = (Shaped) recipe;
            var hash = shaped.getCompiledHash();
            shapedRecipes.compute(new HashHack(hash), (k, v) -> {
                if (v != null) {
                    v.add(shaped);
                    return v;
                }
                return Lists.newArrayList(shaped);
            });
            return this;
        }
        recipes.add(recipe);
        return this;
    }

    @Override
    public IRecipeRegistry unregisterRecipe(AstralRecipe recipe) {
        recipesMap.remove(recipe.getKey());
        if (recipe instanceof Shaped) {
            shapedRecipes.remove(new HashHack(((Shaped) recipe).getCompiledHash()));
            return this;
        }
        recipes.remove(recipe);
        return this;
    }

    @Override
    public AstralRecipe getRecipeByKey(NamespacedKey key) {
        return recipesMap.get(key);
    }

    @Override
    public List<? extends AstralRecipe> getRecipes() {
        var arr = new ArrayList<>(recipes);
        shapedRecipes.values().forEach(arr::addAll);
        return arr;
    }

    @Override
    public AstralRecipe matchRecipe(ItemStack[] matrix) {
        // match shaped recipes first.
        int hash = RecipeHelper.generateMatrixPatternHash(RecipeHelper.toStringMatrix(matrix));
        var shaped = shapedRecipes.get(new HashHack(hash));
        if (shaped != null) {
            for (Shaped recipe : shaped) {
                if (recipe.test(matrix)) return recipe;
            }
        }

        // match shapeless.
        for (AstralRecipe recipe : recipes) {
            if (recipe.test(matrix)) {
                return recipe;
            }
        }
        return null;
    }
}
