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

package io.ib67.astralflow.item.itembuilder;

import io.ib67.astralflow.AstralFlow;
import io.ib67.astralflow.item.ItemKeys;
import io.ib67.astralflow.item.builder.ItemBuilder;
import io.ib67.astralflow.item.itembuilder.weapon.Melee;
import io.ib67.astralflow.item.recipe.choices.MaterialChoice;
import io.ib67.astralflow.item.recipe.kind.Shaped;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static io.ib67.astralflow.test.TestUtil.init;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ItemBuilderTest {
    @BeforeAll
    public void setup() {
        init();
    }

    @Test
    public void testItemBuilder() {
        var p = new Melee(ItemKeys.from("astralflow:test_melee"), new ItemStack(Material.DIAMOND_SWORD), 114514F) {
        };
        ItemBuilder.of(Weapon.INSTANCE)
                .oreDict("melee_oredict")
                .recipe(Shaped.of(new NamespacedKey(AstralFlow.getInstance().asPlugin(), "melee_mock"))
                        .shape("x")
                        .setIngredient('x', new MaterialChoice(Material.DIAMOND))
                        .build()
                )
                //todo @pending@ .bind(Texture.of("astralflow:test_melee", Path.of("aa"), TextureType.ITEM))
                .prototype(p)
                .register();
        var r = AstralFlow.getInstance().getItemRegistry().getRegistry(ItemKeys.from("astralflow:test_melee"));
        assertNotNull(r);
        assertSame(p, r.getHolder());
        // test recipe
        var r2 = AstralFlow.getInstance().getRecipeRegistry().matchRecipe(new ItemStack(Material.DIAMOND), null, null,
                null, null, null,
                null, null, null);
        assertTrue(r2.getPrototype().isSimilar(p.getPrototype()));
        assertNotNull(r2);
    }
}
