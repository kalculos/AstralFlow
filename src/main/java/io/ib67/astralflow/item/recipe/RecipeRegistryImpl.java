/*
 *
 *   AstralFlow - The plugin who is turning bukkit into mod-pack
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

import io.ib67.astralflow.item.recipe.kind.Shaped;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class RecipeRegistryImpl implements IRecipeRegistry {
    private final Map<Integer, List<Shaped>> shapedRecipes = new HashMap<>(32);
    private final Map<NamespacedKey, AstralRecipe> recipesMap = new WeakHashMap<>();
    private final List<AstralRecipe> recipes = new ArrayList<>();

    @Override
    public IRecipeRegistry registerRecipe(AstralRecipe recipe) {
        recipesMap.put(recipe.getKey(), recipe);
        recipes.add(recipe);
        return this;
    }

    @Override
    public IRecipeRegistry unregisterRecipe(AstralRecipe recipe) {
        recipesMap.remove(recipe.getKey());
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
    public AstralRecipe matchRecipe(RecipeType type, ItemStack[] matrix) {
        return switch (type) {
            case CRAFTING -> {
                for (AstralRecipe recipe : recipes) {
                    if (recipe.test(matrix)) {
                        yield recipe;
                    }
                }
                yield null;
            }
            default -> throw new UnsupportedOperationException("Currently Unsupported recipe type: " + type); //todo
        };
    }
}
