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

package io.ib67.astralflow.manager.impl;

import io.ib67.astralflow.AstralFlow;
import io.ib67.astralflow.hook.HookType;
import io.ib67.astralflow.internal.item.state.InternalItemState;
import io.ib67.astralflow.item.AstralItem;
import io.ib67.astralflow.item.ItemKey;
import io.ib67.astralflow.item.ItemState;
import io.ib67.astralflow.item.StateScope;
import io.ib67.astralflow.item.factory.ItemPrototypeFactory;
import io.ib67.astralflow.item.oredict.IOreDict;
import io.ib67.astralflow.item.tag.UUIDTag;
import io.ib67.astralflow.manager.ItemRegistry;
import io.ib67.astralflow.storage.ItemStateStorage;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static org.bukkit.Material.AIR;

public class ItemRegistryImpl implements ItemRegistry {
    private static final UUIDTag TAG = new UUIDTag();
    private final IOreDict oreDict;
    private final Map<ItemKey, ItemPrototypeFactory> itemMap = new HashMap<>();
    private final Map<UUID, ItemState> userStateCache = new HashMap<>();
    private final Map<UUID, ItemState> internalStateCache = new HashMap<>();
    private final ItemStateStorage userStates;
    private final ItemStateStorage internalStates;

    public ItemRegistryImpl(ItemStateStorage internalStates, ItemStateStorage userStates, IOreDict oreDict) {
        this.userStates = Objects.requireNonNull(userStates);
        this.internalStates = Objects.requireNonNull(internalStates);
        this.oreDict = Objects.requireNonNull(oreDict);

        AstralFlow.getInstance().addHook(HookType.SAVE_DATA, () -> {
            userStateCache.forEach(userStates::save); // FIXME: Overriding save method
            internalStateCache.forEach(internalStates::save);
        });
    }

    @Override
    public void registerItem(@NotNull ItemPrototypeFactory item, String oreDictId) {
        Objects.requireNonNull(item, "ItemPrototypeFactory cannot be null");
        Objects.requireNonNull(item.getId(), "ItemPrototypeFactory id cannot be null");

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
        Objects.requireNonNull(item);
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
    public ItemPrototypeFactory getRegistry(ItemKey key) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(key.getId());
        Objects.requireNonNull(key.getNamespace());
        return itemMap.get(key);
    }

    @Override
    public Optional<ItemPrototypeFactory> getRegistry(ItemStack itemStack) {
        Objects.requireNonNull(itemStack);
        var state = (InternalItemState) getState(itemStack, StateScope.INTERNAL_ITEM);
        if (state == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(itemMap.get(state.getPrototypeKey()));
    }


    @Override
    public ItemState getState(ItemStack itemStack, StateScope scope) {
        Objects.requireNonNull(itemStack);
        Objects.requireNonNull(scope);
        if (!itemStack.hasItemMeta()) return null;
        var im = itemStack.getItemMeta();
        if (!im.getPersistentDataContainer().has(scope.getTagKey(), TAG)) {
            return null;
        }
        var uuid = im.getPersistentDataContainer().get(scope.getTagKey(), TAG);
        var cache = scope == StateScope.INTERNAL_ITEM ? internalStateCache : userStateCache;
        return cache.computeIfAbsent(uuid, u -> {
            var state = scope == StateScope.INTERNAL_ITEM ? internalStates : userStates;
            if (!state.has(uuid)) {
                return null;
            }
            return state.get(uuid);
        });
    }

    @Override
    public AstralItem createItem(ItemKey key) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(key.getId());
        Objects.requireNonNull(key.getNamespace());
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
            uuid = UUID.nameUUIDFromBytes(key.asString().getBytes());
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
