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

package io.ib67.astralflow.item;

import io.ib67.astralflow.AstralFlow;
import io.ib67.astralflow.item.builder.ItemPrototype;
import io.ib67.astralflow.item.recipe.choices.AstralItemChoice;
import io.ib67.astralflow.item.recipe.choices.ExactItemChoice;
import io.ib67.astralflow.item.recipe.choices.MaterialChoice;
import io.ib67.astralflow.item.recipe.choices.OreDictChoice;
import io.ib67.astralflow.test.TestUtil;
import io.ib67.astralflow.util.ItemStacks;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ChoiceTest {

    public static final ItemKey CAT_KEY = ItemKeys.from("test:cat");
    public static final ItemKey DOG_WOOL_KEY = ItemKeys.from("test:dog_wool");
    public static final ItemKey CAT_WOOL_KEY = ItemKeys.from("test:cat_wool");

    @BeforeAll
    public void setup() {
        TestUtil.init();
        AstralFlow.getInstance().getItemRegistry().registerItem(ItemPrototype
                .builder()
                .prototype(new ItemStack(Material.MUSIC_DISC_CAT))
                .id(CAT_KEY)
                .build()
        );

    }

    @Test
    public void testAstralChoice() {
        var choice = new AstralItemChoice(CAT_KEY);
        var item = AstralFlow.getInstance().getItemRegistry().createItem(CAT_KEY);
        assertTrue(choice.test(item.asItemStack()));
        var fakeItem = new ItemStack(Material.MUSIC_DISC_CAT);
        assertFalse(choice.test(fakeItem));
        var cloned = item.asItemStack().clone();
        assertTrue(choice.test(cloned));
        assertFalse(choice.test(null));
    }

    @Test
    public void testExactChoice() {
        var item = ItemStacks.builder(Material.WHITE_WOOL).displayName("&a Cat Wool").build();
        var choice = new ExactItemChoice(item);
        assertTrue(choice.test(item));
        var fakeItem = new ItemStack(Material.MUSIC_DISC_CAT);
        assertFalse(choice.test(fakeItem));
        fakeItem = new ItemStack(Material.WHITE_WOOL);
        assertFalse(choice.test(fakeItem));
        var cloned = item.clone();
        assertTrue(choice.test(cloned));
        var anotherItem = ItemStacks.builder(Material.WHITE_WOOL).displayName("&a Cat Wool").build();
        assertTrue(choice.test(anotherItem));
        var anotherFakeItem = ItemStacks.builder(Material.WHITE_WOOL).displayName("&a Dog Wool").build();
        assertFalse(choice.test(anotherFakeItem));
        assertFalse(choice.test(null));
    }

    @Test
    public void testMaterialChoice() {
        var choice = new MaterialChoice(Material.WHITE_WOOL);
        assertFalse(choice.test(null));
        assertTrue(choice.test(new ItemStack(Material.WHITE_WOOL)));
        assertFalse(choice.test(new ItemStack(Material.MUSIC_DISC_CAT)));
        assertFalse(choice.test(new ItemStack(Material.BLUE_WOOL)));
    }

    @Test
    public void testOreDictChoice() {
        var choice = new OreDictChoice("testing_wool");
        AstralFlow.getInstance().getItemRegistry().registerItem(ItemPrototype.builder().id(DOG_WOOL_KEY).prototype(new ItemStack(Material.BROWN_WOOL)).build(), "testing_wool");
        var item = DOG_WOOL_KEY.createNewItem();
        assertTrue(choice.test(item.asItemStack()));
        var fakeItem = new ItemStack(Material.MUSIC_DISC_CAT);
        assertFalse(choice.test(fakeItem));
        fakeItem = new ItemStack(Material.BROWN_WOOL);
        assertFalse(choice.test(fakeItem));
        AstralFlow.getInstance().getItemRegistry().registerItem(ItemPrototype.builder().id(CAT_WOOL_KEY).prototype(new ItemStack(Material.BROWN_WOOL)).build(), "testing_wool");
        assertTrue(choice.test(CAT_WOOL_KEY.createNewItem().asItemStack()));
    }
}
