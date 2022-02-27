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
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RecipeRegistryTest {
    private static final NamespacedKey KEY_SHAPED = new NamespacedKey("astralflow", "shaped");
    private IRecipeRegistry registry;

    @Before
    public void setup() {
        registry = new RecipeRegistryImpl();
    }

    @Test
    public void testShaped() {
        var recipe = Shaped.of(KEY_SHAPED).shape(
                        " A ",
                        " B "
                )
                .setIngredient('A', new IngredientChoice.MaterialChoice(Material.COAL))
                .setIngredient('B', new IngredientChoice.MaterialChoice(Material.STICK))
                .build();
        registry.registerRecipe(recipe);
        Assert.assertEquals(recipe, registry.getRecipeByKey(KEY_SHAPED));
        Assert.assertTrue("Test Recipe REGULAR Match #1", recipe.test(new ItemStack[]{
                null, new ItemStack(Material.COAL), null,
                null, new ItemStack(Material.STICK), null,
                null, null, null
        }));
        Assert.assertTrue("Test Recipe MORPH Match #2", recipe.test(new ItemStack[]{
                new ItemStack(Material.COAL), null, null,
                new ItemStack(Material.STICK), null, null,
                null, null, null
        }));
        Assert.assertTrue("Test Recipe MORPH Match #3", recipe.test(new ItemStack[]{
                null, null, null,
                new ItemStack(Material.COAL), null, null,
                new ItemStack(Material.STICK), null, null
        }));
        Assert.assertTrue("Test Recipe MORPH Match #4", recipe.test(new ItemStack[]{
                null, null, null,
                null, null, new ItemStack(Material.COAL),
                null, null, new ItemStack(Material.STICK)
        }));
    }
}