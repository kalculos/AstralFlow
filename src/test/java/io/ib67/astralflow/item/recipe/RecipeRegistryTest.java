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

import io.ib67.astralflow.item.recipe.choices.MaterialChoice;
import io.ib67.astralflow.item.recipe.kind.Shaped;
import io.ib67.astralflow.item.recipe.kind.Shapeless;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static io.ib67.astralflow.test.TestUtil.init;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public final class RecipeRegistryTest {
    private static final NamespacedKey KEY_SHAPED = new NamespacedKey("astralflow", "shaped");
    private static final NamespacedKey KEY_SHAPELESS = new NamespacedKey("astralflow", "shapeless");

    private IRecipeRegistry registry;

    @BeforeAll
    public void setup() {
        init();
        registry = new RecipeRegistryImpl();
    }

    @Test
    public void testShapedMatch() {
        var recipe = Shaped.of(KEY_SHAPED).shape(
                        " A ",
                        " B "
                )
                .setIngredient('A', new MaterialChoice(Material.COAL))
                .setIngredient('B', new MaterialChoice(Material.STICK))
                .build();
        registry.registerRecipe(recipe);
        assertEquals(recipe, registry.getRecipeByKey(KEY_SHAPED));
        assertTrue(recipe.test(new ItemStack[]{
                null, new ItemStack(Material.COAL), null,
                null, new ItemStack(Material.STICK), null,
                null, null, null
        }), "Test Recipe REGULAR Match #1");
        assertTrue(recipe.test(new ItemStack[]{
                new ItemStack(Material.COAL), null, null,
                new ItemStack(Material.STICK), null, null,
                null, null, null
        }), "Test Recipe MORPH Match #2");
        assertTrue(recipe.test(new ItemStack[]{
                null, null, null,
                new ItemStack(Material.COAL), null, null,
                new ItemStack(Material.STICK), null, null
        }), "Test Recipe MORPH Match #3");
        assertTrue(recipe.test(new ItemStack[]{
                null, null, null,
                null, null, new ItemStack(Material.COAL),
                null, null, new ItemStack(Material.STICK)
        }), "Test Recipe MORPH Match #4");
        assertTrue(recipe.test(new ItemStack[]{
                null, new ItemStack(Material.COAL), null,
                null, new ItemStack(Material.STICK), null,
                null, null, null
        }), "Test Recipe Regular REGISTRY Match #5");
        assertEquals(recipe, registry.matchRecipe(new ItemStack[]{
                null, null, null,
                null, null, new ItemStack(Material.COAL),
                null, null, new ItemStack(Material.STICK)
        }), "Test Recipe MORPH REGISTRY Match #6");
    }

    @Test
    public void testShapelessMatch() {
        var recipe = Shapeless.of(KEY_SHAPELESS, null)
                .addIngredients(new MaterialChoice(Material.PAPER))
                .addIngredients(new MaterialChoice(Material.GUNPOWDER))
                .setResult(() -> new ItemStack(Material.FIREWORK_ROCKET))
                .build();
        registry.registerRecipe(recipe);
        assertTrue(recipe.test(new ItemStack[]{
                new ItemStack(Material.PAPER), new ItemStack(Material.GUNPOWDER)
        }), "Test Shapeless Match #1");
        assertTrue(recipe.test(new ItemStack[]{
                new ItemStack(Material.GUNPOWDER), new ItemStack(Material.PAPER)
        }), "Test Shapeless Match #2");
        assertEquals(recipe, registry.matchRecipe(new ItemStack[]{
                new ItemStack(Material.GUNPOWDER), new ItemStack(Material.PAPER)
        }), "Test Shapeless Match #3");
    }

    @Test
    @Tag("later")
    public void testShapelessConsumeItem() {
        var recipe = Shapeless.of(KEY_SHAPELESS, null)
                .addIngredients(new MaterialChoice(Material.CACTUS))
                .addIngredients(new MaterialChoice(Material.GUNPOWDER))
                .setResult(() -> new ItemStack(Material.FIREWORK_ROCKET))
                .build();
        registry.registerRecipe(recipe);
        var newItems = new ItemStack[]{
                new ItemStack(Material.CACTUS), new ItemStack(Material.GUNPOWDER)
        };
        recipe = (Shapeless) registry.matchRecipe(newItems);
        var expectedResult = new ItemStack(Material.FIREWORK_ROCKET);
        assertEquals(expectedResult, recipe.produceResult(), "Test Shapeless Consume #1");
        var expectedArray = new ItemStack[]{new ItemStack(Material.AIR), new ItemStack(Material.AIR)};
        assertArrayEquals(expectedArray, recipe.apply(newItems), "Test Shapeless Consume #1");
    }

    @Test
    public void testShapedConsume() {
        var shaped = Shaped.of(KEY_SHAPED)
                .shape(
                        " a ",
                        "aba",
                        " a"
                )
                .setIngredient('a', new MaterialChoice(Material.CACTUS))
                .setIngredient('b', new MaterialChoice(Material.GUNPOWDER)).setResult(() -> new ItemStack(Material.FIREWORK_ROCKET))
                .build();
        var matrix = new ItemStack[]{
                null, new ItemStack(Material.CACTUS), null,
                new ItemStack(Material.CACTUS), new ItemStack(Material.GUNPOWDER), new ItemStack(Material.CACTUS),
                null, new ItemStack(Material.CACTUS), null
        };
        var excepted = new ItemStack[]{
                null, new ItemStack(Material.AIR), null,
                new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR),
                null, new ItemStack(Material.AIR), null
        };
        matrix = shaped.apply(matrix);
        assertArrayEquals(excepted, matrix, "Test Shaped Consume #1");
    }
}