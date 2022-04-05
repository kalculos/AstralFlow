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

import org.bukkit.Keyed;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * Represents a recipe that can be used to craft an item.
 */
//todo refactor
@ApiStatus.AvailableSince("0.1.0")
public interface AstralRecipe extends Keyed, Predicate<ItemStack[]>, UnaryOperator<ItemStack[]> {
    /**
     * The type of recipe.
     *
     * @return the type of recipe.
     */
    RecipeType getRecipeType();

    /**
     * The result of the recipe.
     *
     * @return the result of the recipe.
     */
    @Contract(" -> new")
    ItemStack produceResult();

    /**
     * The sample result of the recipe.
     *
     * @return sample
     */
    ItemStack getPrototype();

    /**
     * Set the sample
     *
     * @param itemStack sample
     */
    void setPrototype(ItemStack itemStack);

    IngredientChoice[] getMatrix();

    default void setResult(ItemStack is) {
        setResult(() -> is);
    }

    void setResult(Supplier<ItemStack> prototype); // use supplier for instantiation from ItemRegistry.
}
