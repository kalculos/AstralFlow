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

package io.ib67.astralflow;

import com.google.gson.GsonBuilder;
import io.ib67.astralflow.api.AstralHelper;
import io.ib67.astralflow.api.item.dummy.DummyItem;
import io.ib67.astralflow.api.item.dummy.DummyItemProperty;
import io.ib67.astralflow.item.ItemKey;
import io.ib67.astralflow.item.builder.ItemBuilder;
import io.ib67.astralflow.util.ItemStacks;
import io.ib67.internal.util.bukkit.serializer.ItemStackSerializer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static io.ib67.astralflow.test.TestUtil.init;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ItemSerializerTest {
    @BeforeAll
    public void setup() {
        init();
    }

    @Test
    public void testSerialize() {
        var itemStackProto = ItemStacks.builder(Material.BOW)
                .displayName("&aaaaa")
                .build();
        var itemKey = ItemKey.from("a", "c");
        ItemBuilder.ofDummyItem()
                .prototype(new DummyItem(itemKey, itemStackProto, DummyItemProperty.builder().build()))
                .register();
        var item = itemKey.createNewItem().asItemStack();
        var serializer = new GsonBuilder()
                .registerTypeAdapter(ItemStack.class, new ItemStackSerializer())
                .create();
        var result = serializer.toJson(item);
        System.out.println(result);
        // de
        var item2 = serializer.fromJson(result, ItemStack.class);
        Assertions.assertTrue(AstralHelper.isItem(item2));
        Assertions.assertTrue(item.isSimilar(item2), "ItemStack should be simliar.");
    }
}
