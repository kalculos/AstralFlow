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

package io.ib67.astralflow.storage.impl;

import io.ib67.astralflow.item.ItemState;
import io.ib67.astralflow.manager.IFactoryManager;
import io.ib67.astralflow.storage.ItemStateStorage;
import io.ib67.astralflow.storage.KeyedStorage;

import java.nio.file.Path;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

public class FileItemStorage implements ItemStateStorage {
    private final KeyedStorage<String, ItemState> storage;
    private final FileMachineStorage.MachineStorageHelper helper;

    public FileItemStorage(Path storageDir, IFactoryManager factoryManager) {
        helper = new FileMachineStorage.MachineStorageHelper(factoryManager);
        storage = FileBasedKeyedStorage.<ItemState>builder()
                .storageDir(storageDir)
                .valueInMapper(this::fromBytes)
                .valueOutMapper(this::toBytes)
                .build();
    }

    public ItemState fromBytes(byte[] bytes) {
        return helper.fromJson(new String(bytes), ItemState.class);
    }

    public byte[] toBytes(ItemState itemState) {
        return helper.toJson(itemState).getBytes();
    }

    @Override
    public boolean has(UUID uuid) {
        return storage.has(uuid.toString());
    }

    @Override
    public ItemState get(UUID uuid) {
        return storage.get(uuid.toString());
    }

    @Override
    public Collection<? extends UUID> getKeys() {
        return storage.getKeys().stream().map(e -> UUID.nameUUIDFromBytes(e.getBytes())).collect(Collectors.toList());
    }

    @Override
    public void save(UUID uuid, ItemState state) {
        storage.save(uuid.toString(), state);
    }

    @Override
    public void remove(UUID uuid) {
        storage.remove(uuid.toString());
    }
}
