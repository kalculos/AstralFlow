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

import org.bukkit.NamespacedKey;

import java.util.*;
import java.util.stream.Collectors;

public final class RecipeRegistryImpl implements IRecipeRegistry {
    private final Map<NamespacedKey, AstralRecipe> recipesMap = new WeakHashMap<>();

    private final Map<RecipeType, List<AstralRecipe>> recipes = new EnumMap<>(RecipeType.class);

    @Override
    public IRecipeRegistry registerRecipe(AstralRecipe recipe) {
        recipesMap.put(recipe.getKey(), recipe);
        recipes.computeIfAbsent(recipe.getRecipeType(), k -> new ArrayList<>()).add(recipe);
        return this;
    }

    @Override
    public IRecipeRegistry unregisterRecipe(AstralRecipe recipe) {
        recipesMap.remove(recipe.getKey());
        recipes.computeIfAbsent(recipe.getRecipeType(), k -> new ArrayList<>()).remove(recipe);
        return this;
    }

    @Override
    public AstralRecipe getRecipeByKey(NamespacedKey key) {
        return recipesMap.get(key);
    }

    @Override
    public List<? extends AstralRecipe> getRecipes() {
        return recipes.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    @Override
    public AstralRecipe matchRecipe(ItemMatrix imatrix) {
        var type = imatrix.getType();
        var matrix = imatrix.getMatrix();
        var craftingRecipes = recipes.getOrDefault(type, Collections.emptyList());
        for (AstralRecipe recipe : craftingRecipes) {
            if (recipe.test(matrix)) {
                return recipe;
            }
        }
        return null;
    }
}
