/*
 *
 *   AstralFlow - Storage utilities for spigot servers.
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

package io.ib67.astralflow.manager.impl;

import io.ib67.astralflow.item.IOreDict;
import io.ib67.astralflow.item.Item;
import io.ib67.astralflow.item.ItemState;
import io.ib67.astralflow.item.OreDictImpl;
import io.ib67.astralflow.item.block.UUIDTag;
import io.ib67.astralflow.manager.ItemManager;
import io.ib67.astralflow.storage.ItemStateStorage;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@RequiredArgsConstructor
public class ItemManagerImpl implements ItemManager {
    private static final UUIDTag TAG = new UUIDTag("custom_item");
    private final IOreDict oreDict = new OreDictImpl();
    private final Map<String, Item> itemMap = new HashMap<>();
    private final ItemStateStorage states;

    @Override
    public void registerItem(Item item) {
        itemMap.put(item.getId(), item);
    }

    @Override
    public IOreDict getOreDict() {
        return oreDict;
    }

    @Override
    public Collection<? extends Item> getItems() {
        return itemMap.values();
    }

    @Override
    public Item getItem(String key) {
        return itemMap.get(key);
    }

    @Override
    public Optional<Item> getItem(ItemStack itemStack) {
        var state = getState(itemStack);
        if (state == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(itemMap.get(state.getPrototypeKey()));
    }

    private ItemState getState(ItemStack itemStack) {
        if (!itemStack.hasItemMeta()) return null;
        var im = itemStack.getItemMeta();
        if (!im.getPersistentDataContainer().has(TAG.getTagKey(), TAG)) {
            return null;
        }
        var uuid = im.getPersistentDataContainer().get(TAG.getTagKey(), TAG);
        if (!states.hasState(uuid)) {
            return null;
        }
        return states.getState(uuid);
    }

    @Override
    public ItemStack createItem(String key) {
        var item = getItem(key);
        if (item == null) return null;
        // validation
        var prototype = item.getPrototype();
        if (prototype == null || !prototype.hasItemMeta())
            throw new IllegalStateException("The prototype of " + key + " is null or AIR");
        // pack item.
        var uuid = UUID.randomUUID();

    }
}
