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

import io.ib67.Util;
import io.ib67.astralflow.item.ItemState;
import io.ib67.astralflow.manager.IFactoryManager;
import io.ib67.astralflow.storage.ItemStateStorage;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileItemStorage implements ItemStateStorage {
    private transient final Path storage;
    private transient final IFactoryManager factory;
    private transient final FileMachineStorage.MachineStorageHelper serializer;
    private transient final Map<UUID, ItemState> states = new HashMap<>();

    public FileItemStorage(Path storage, IFactoryManager factory) {
        this.storage = storage;
        this.factory = factory;
        serializer = new FileMachineStorage.MachineStorageHelper(factory);
    }

    @Override
    public boolean hasState(UUID uuid) {
        return storage.resolve(uuid.toString() + ".json").toFile().exists();
    }

    @Override
    public ItemState getState(UUID uuid) {
        return states.computeIfAbsent(uuid, u -> Util.runCatching(() -> serializer.fromJson(Files.readString(storage.resolve(uuid.toString() + ".json")), ItemState.class)).getResult());
    }

    @Override
    public Collection<? extends UUID> getStates() {
        var a = storage.toFile().listFiles();
        if (a != null) return Stream.of(a)
                .map(File::getName)
                .map(e -> e.substring(0, 36))
                .map(UUID::fromString)
                .collect(Collectors.toList());
        return Collections.emptyList();
    }

    @Override
    public void saveState(UUID uuid, ItemState state) {
        Util.runCatching(() -> Files.writeString(storage.resolve(uuid.toString() + ".json"), serializer.toJson(state)));
    }

    @Override
    public void removeState(UUID uuid) {
        states.remove(uuid);
        Util.runCatching(() -> {
            Files.delete(storage.resolve(uuid.toString() + ".json"));
            return null;
        });
    }
}
