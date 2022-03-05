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

package io.ib67.astralflow.item;

import io.ib67.astralflow.item.definitions.DummyStatelessItem;
import io.ib67.astralflow.item.tag.UUIDTag;
import io.ib67.astralflow.manager.ItemRegistry;
import io.ib67.astralflow.manager.impl.ItemRegistryImpl;
import io.ib67.astralflow.storage.MockItemStorage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static io.ib67.astralflow.test.TestUtil.init;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ItemRegistryTest {
    private ItemRegistry registry;

    @BeforeAll
    public void setup() {
        init();
        registry = new ItemRegistryImpl(new MockItemStorage(), new OreDictImpl());
    }

    @Test
    public void testStatelessItems() {
        var p = new DummyStatelessItem();
        registry.registerItem(p);
        // get items.

        var item = registry.createItem(p.getId());
        assertNotEquals(null, item, "Item should not be null");
        assertNotEquals(item.asItemStack(), p.getPrototype(), "ItemRegistry can't modify the prototype");
        assertTrue(item.getState().isEmpty(), "Stateless states must be empty");

        // let's see uuid allocation.
        var item2 = registry.createItem(p.getId());
        var uuid1 = item.asItemStack().getItemMeta().getPersistentDataContainer().get(StateScope.USER_ITEM.getTagKey(), new UUIDTag());
        var uuid2 = item2.asItemStack().getItemMeta().getPersistentDataContainer().get(StateScope.USER_ITEM.getTagKey(), new UUIDTag());
        assertEquals(uuid1, uuid2, "UUIDs must be equal");
    }
}
