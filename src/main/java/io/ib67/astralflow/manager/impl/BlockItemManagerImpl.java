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

import com.google.gson.JsonParseException;
import io.ib67.Util;
import io.ib67.astralflow.AstralFlow;
import io.ib67.astralflow.hook.HookType;
import io.ib67.astralflow.item.block.MachineItemTag;
import io.ib67.astralflow.machines.IMachine;
import io.ib67.astralflow.machines.IMachineData;
import io.ib67.astralflow.manager.IBlockItemManager;
import io.ib67.astralflow.manager.IFactoryManager;
import io.ib67.astralflow.storage.impl.FileMachineStorage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Async compatible.
 */
public class BlockItemManagerImpl implements IBlockItemManager {
    private final Map<UUID, CachedMachine> cache = new ConcurrentHashMap<>(); // Intent to reduce redundant serialization
    private final IFactoryManager factoryManager;
    private final Path blockItemDir; // FIXME: too much responsibility
    private final FileMachineStorage.MachineStorageHelper helper;

    {
        // todo load blockItems?
    }

    public BlockItemManagerImpl(IFactoryManager factoryManager, Path blockItemDir) {
        this.factoryManager = factoryManager;
        this.blockItemDir = blockItemDir;
        helper = new FileMachineStorage.MachineStorageHelper(factoryManager);
        AstralFlow.getInstance().addHook(HookType.PLUGIN_SHUTDOWN, this::save);
    }

    @Override
    public ItemStack copyOf(IMachine machine) {
        var template = factoryManager.getBlockItemFactory(machine.getClass()).generateTemplateBlockItem(machine);
        // setup
        if (template == null || template.getType() == Material.AIR) { //drop nothing
            return null;
        }
        var im = template.getItemMeta();
        assert im != null;

        var key = UUID.randomUUID();
        cache.put(key, new CachedMachine(machine.getState(), machine.getType().getName()));
        im.getPersistentDataContainer().set(MachineItemTag.MACHINE_TAG_KEY, MachineItemTag.MACHINE_ITEM_TAG, key);
        template.setItemMeta(im);
        return template;
    }

    @Override
    public boolean isBlockItem(ItemStack itemStack) {
        return itemStack.hasItemMeta() && itemStack.getItemMeta().getPersistentDataContainer().has(MachineItemTag.MACHINE_TAG_KEY, MachineItemTag.MACHINE_ITEM_TAG);
    }

    @Override
    public IMachine extractMachine(ItemStack itemStack, Location location) {
        if (!isBlockItem(itemStack)) {
            return null;
        }
        var im = itemStack.getItemMeta();
        var uuid = im.getPersistentDataContainer().get(MachineItemTag.MACHINE_TAG_KEY, MachineItemTag.MACHINE_ITEM_TAG);
        return Optional.ofNullable(cache.computeIfAbsent(uuid, this::load)).map(it -> it.buildMachine(location)).orElse(null);
    }

    //todo add a IBlockItemStorage.
    @SneakyThrows
    private CachedMachine load(UUID uuid) {
        var file = blockItemDir.resolve(uuid + ".json").toFile();
        if (!file.exists()) {
            return null;
        }
        return helper.fromJson(Files.readString(file.toPath()), CachedMachine.class);
    }

    @SneakyThrows
    private void save() {
        for (Map.Entry<UUID, CachedMachine> kv : cache.entrySet()) {
            var file = blockItemDir.resolve(kv.getKey() + ".json").toFile();
            if (!file.exists()) file.mkdirs();
            Files.writeString(file.toPath(), helper.toJson(kv.getValue()));
        }
    }

    @RequiredArgsConstructor
    @Getter
    public class CachedMachine {
        private final IMachineData state;
        private final String type;

        public IMachine buildMachine(Location location) {
            return (IMachine) Util.runCatching(() -> (Object) Class.forName(type)) // to be caught.
                    .onFailure(t -> {
                        throw new JsonParseException("Can't find machine type: " + type, t);
                    }).onSuccess(clazz -> {
                        var factory = factoryManager.getMachineFactory((Class<? extends IMachine>) clazz); // TODO: reusable? {@link MachineSerializer}
                        if (factory == null) {
                            throw new JsonParseException("No factories have registered for this type: " + type);
                        }
                        return factory.createMachine(location, UUID.randomUUID(), state);
                    }).getResult();
        }
    }
}
