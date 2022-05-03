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

import io.ib67.astralflow.internal.item.state.InternalItemState;
import io.ib67.astralflow.item.*;
import io.ib67.astralflow.item.factory.ItemPrototypeFactory;
import io.ib67.astralflow.item.internal.serialization.ItemStorageType;
import io.ib67.astralflow.item.internal.tag.ItemStateTag;
import io.ib67.astralflow.item.oredict.IOreDict;
import io.ib67.astralflow.manager.IFactoryManager;
import io.ib67.astralflow.manager.ItemRegistry;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class ItemRegistryImpl implements ItemRegistry {
    @Getter
    private final IOreDict oreDict;
    private final Map<ItemKey, ItemPrototypeFactory> itemMap = new HashMap<>();
    private final ItemStateTag stateTag;

    public ItemRegistryImpl(IOreDict oreDict, IFactoryManager factoryManager) {
        this.oreDict = Objects.requireNonNull(oreDict);
        this.stateTag = new ItemStateTag(ItemStorageType.JSON, factoryManager);
    }

    @Override
    public void registerItem(ItemPrototypeFactory item, String oredict) {
        Objects.requireNonNull(item);
        itemMap.put(ItemKeys.clone(item.getId()), item);
        if (oredict != null) {
            oreDict.registerItem(oredict, item.getPrototype().clone(), this::isItem);
        }
    }

    @Override
    public boolean isItem(ItemStack item) {
        Objects.requireNonNull(item);
        return getState(item, StateScope.INTERNAL_ITEM) != null;
    }

    @Override
    public Collection<? extends ItemPrototypeFactory> getItemPrototypes() {
        return itemMap.values();
    }

    @Override
    public ItemPrototypeFactory getRegistry(ItemKey key) {
        Objects.requireNonNull(key);
        return itemMap.get(ItemKeys.clone(key));
    }

    @Override
    public Optional<ItemPrototypeFactory> getRegistry(ItemStack itemStack) {
        Objects.requireNonNull(itemStack);
        var state = ((InternalItemState) getState(itemStack, StateScope.INTERNAL_ITEM));
        if (state == null) return Optional.empty();
        return Optional.ofNullable(getRegistry(state.getPrototypeKey()));
    }

    @Override
    public AstralItem createItem(ItemKey key) {
        Objects.requireNonNull(key);
        var registry = Objects.requireNonNull(Objects.requireNonNull(getRegistry(key)));
        var proto = Objects.requireNonNull(registry.getPrototype()).clone();
        if (proto.getType().name().contains("AIR")) {
            throw new UnsupportedOperationException("Cannot create AIR item");
        }
        // initialize states.
        var meta = Objects.requireNonNull(proto.getItemMeta());
        var pdc = meta.getPersistentDataContainer();

        // set internal state.
        pdc.set(StateScope.INTERNAL_ITEM.getTagKey(), stateTag, new InternalItemState(key));
        var userState = registry.getStatePrototype();
        if (userState != null) { // stateful
            pdc.set(StateScope.USER_ITEM.getTagKey(), stateTag, userState.clone());
        }
        proto.setItemMeta(meta);
        return new AstralItem(proto, this);
    }

    @Override
    public @Nullable ItemState getState(ItemStack itemStack, StateScope stateScope) {
        Objects.requireNonNull(itemStack);
        Objects.requireNonNull(stateScope);
        if (!itemStack.hasItemMeta()) {
            return null;
        }
        var meta = itemStack.getItemMeta();
        var pdc = meta.getPersistentDataContainer();
        return pdc.get(stateScope.getTagKey(), stateTag);
    }

    @Override
    public void saveState(ItemStack itemStack, StateScope scope, ItemState state) {
        Objects.requireNonNull(itemStack);
        Objects.requireNonNull(scope);
        Objects.requireNonNull(state);
        var meta = itemStack.getItemMeta();
        if (meta == null) {
            throw new IllegalArgumentException("ItemStack has no ItemMeta");
        }
        var pdc = meta.getPersistentDataContainer();
        pdc.set(scope.getTagKey(), stateTag, state);
        itemStack.setItemMeta(meta);
    }
}
