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

package io.ib67.astralflow.manager.impl;

import io.ib67.astralflow.AstralFlow;
import io.ib67.astralflow.hook.HookType;
import io.ib67.astralflow.internal.item.state.InternalItemState;
import io.ib67.astralflow.item.AstralItem;
import io.ib67.astralflow.item.IOreDict;
import io.ib67.astralflow.item.ItemState;
import io.ib67.astralflow.item.StateScope;
import io.ib67.astralflow.item.factory.ItemPrototypeFactory;
import io.ib67.astralflow.item.tag.UUIDTag;
import io.ib67.astralflow.manager.ItemRegistry;
import io.ib67.astralflow.storage.ItemStateStorage;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static org.bukkit.Material.AIR;

public class ItemRegistryImpl implements ItemRegistry {
    private static final UUIDTag TAG = new UUIDTag();
    private final IOreDict oreDict;
    private final Map<String, ItemPrototypeFactory> itemMap = new HashMap<>();
    private final Map<UUID, ItemState> userStateCache = new HashMap<>();
    private final Map<UUID, ItemState> internalStateCache = new HashMap<>();
    private final ItemStateStorage states;

    public ItemRegistryImpl(ItemStateStorage states, IOreDict oreDict) {
        this.states = states;

        this.oreDict = oreDict;
        AstralFlow.getInstance().addHook(HookType.SAVE_DATA, () -> {
            userStateCache.forEach(states::save);
            internalStateCache.forEach(states::save);
        });
    }

    @Override
    public void registerItem(ItemPrototypeFactory item, String oreDictId) {
        itemMap.put(item.getId(), item);
        if (oreDictId != null) {
            oreDict.registerItem(oreDictId, item.getPrototype().clone(), t -> {
                var state = (InternalItemState) getState(t, StateScope.INTERNAL_ITEM);
                if (state == null) return false;
                return state.getPrototypeKey().equals(item.getId());
            });
        }
    }

    @Override
    public boolean isItem(ItemStack item) {
        return getState(item, StateScope.INTERNAL_ITEM) != null;
    }

    @Override
    public IOreDict getOreDict() {
        return oreDict;
    }

    @Override
    public Collection<? extends ItemPrototypeFactory> getItemPrototypes() {
        return itemMap.values();
    }

    @Override
    public ItemPrototypeFactory getRegistry(String key) {
        return itemMap.get(key);
    }

    @Override
    public Optional<ItemPrototypeFactory> getRegistry(ItemStack itemStack) {
        var state = (InternalItemState) getState(itemStack, StateScope.INTERNAL_ITEM);
        if (state == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(itemMap.get(state.getPrototypeKey()));
    }


    @Override
    public ItemState getState(ItemStack itemStack, StateScope scope) {
        if (!itemStack.hasItemMeta()) return null;
        var im = itemStack.getItemMeta();
        if (!im.getPersistentDataContainer().has(scope.getTagKey(), TAG)) {
            return null;
        }
        var uuid = im.getPersistentDataContainer().get(scope.getTagKey(), TAG);
        var cache = scope == StateScope.INTERNAL_ITEM ? internalStateCache : userStateCache;
        return cache.computeIfAbsent(uuid, u -> {
            if (!states.has(uuid)) {
                return null;
            }
            return states.get(uuid);
        });
    }

    @Override
    public AstralItem createItem(String key) {
        var item = getRegistry(key);
        if (item == null) return null;
        // validation
        var prototype = item.getPrototype();
        if (prototype == null || prototype.getType() == AIR)
            throw new IllegalStateException("The prototype of " + key + " is null or AIR");
        // pack item.
        var userState = item.getStatePrototype();
        var afState = new InternalItemState(key); // store prototype info to this
        UUID uuid;
        if (userState == null) {
            uuid = UUID.nameUUIDFromBytes(key.getBytes());
        } else {
            uuid = UUID.randomUUID();
            userState = userState.clone();
            //states.saveState(uuid, state);
        }
        userStateCache.put(uuid, userState);
        internalStateCache.put(uuid, afState);
        // state done
        var itemStack = prototype.clone();
        var im = itemStack.getItemMeta();

        im.getPersistentDataContainer().set(StateScope.USER_ITEM.getTagKey(), TAG, uuid);
        im.getPersistentDataContainer().set(StateScope.INTERNAL_ITEM.getTagKey(), TAG, uuid);
        itemStack.setItemMeta(im);
        return new AstralItem(itemStack, this);
    }
}
