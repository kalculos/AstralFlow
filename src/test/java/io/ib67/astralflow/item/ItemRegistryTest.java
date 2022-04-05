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

import io.ib67.astralflow.item.definitions.DummyStatefulItem;
import io.ib67.astralflow.item.definitions.DummyStatelessItem;
import io.ib67.astralflow.item.internal.serialization.ItemStorageType;
import io.ib67.astralflow.item.internal.tag.ItemStateTag;
import io.ib67.astralflow.item.oredict.internal.SimpleOreDict;
import io.ib67.astralflow.manager.ItemRegistry;
import io.ib67.astralflow.manager.impl.FactoryManagerImpl;
import io.ib67.astralflow.manager.impl.ItemRegistryImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static io.ib67.astralflow.test.TestUtil.init;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public final class ItemRegistryTest {
    private ItemRegistry registry;

    @BeforeAll
    public void setup() {
        init();
        registry = new ItemRegistryImpl(new SimpleOreDict(), new FactoryManagerImpl());
    }

    @Test
    public void testStatelessItems() {
        var p = new DummyStatelessItem();
        registry.registerItem(p);
        // get items.

        var item = registry.createItem(p.getId());
        assertNotEquals(null, item, "Item should not be null");
        assertNotSame(item.asItemStack(), p.getPrototype(), "ItemRegistry can't modify the prototype");
        assertTrue(item.getState().isEmpty(), "Stateless states must be empty");

        // user state should be null.
        var jsonStateTag = new ItemStateTag(ItemStorageType.JSON, null);
        var state1 = item.asItemStack().getItemMeta().getPersistentDataContainer().get(StateScope.USER_ITEM.getTagKey(), jsonStateTag);
        assertNull(state1, "User state must be null for Stateless items");

        // test is item
        assertTrue(registry.isItem(item.asItemStack()), "Item should be registered");
    }

    @Test
    public void testStatefulItems() {
        var p = new DummyStatefulItem();
        registry.registerItem(p);
        // get items.

        var item = registry.createItem(p.getId());
        assertNotEquals(null, item, "Item should not be null");
        assertNotSame(item.asItemStack(), p.getPrototype(), "ItemRegistry can't modify the prototype");
        assertFalse(item.getState().isEmpty(), "Stateful states must not be empty");

        //test is item
        assertTrue(registry.isItem(item.asItemStack()), "Item should be registered");

        // test states
        assertDoesNotThrow((() -> (ItemState.SimpleItemState) item.getState().orElseThrow()), "State can't be registered");
    }
}
