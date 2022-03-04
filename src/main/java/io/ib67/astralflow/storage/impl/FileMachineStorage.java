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

package io.ib67.astralflow.storage.impl;

import com.google.gson.Gson;
import io.ib67.astralflow.internal.MachineSerializer;
import io.ib67.astralflow.internal.StateSerializer;
import io.ib67.astralflow.machines.IMachine;
import io.ib67.astralflow.machines.IState;
import io.ib67.astralflow.manager.IFactoryManager;
import io.ib67.astralflow.storage.IMachineStorage;
import io.ib67.astralflow.storage.KeyedStorage;
import io.ib67.util.Util;
import io.ib67.util.bukkit.Log;

import java.nio.file.Path;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

public class FileMachineStorage implements IMachineStorage {
    private final KeyedStorage<String, IMachine> storage;
    private final MachineStorageHelper helper;

    public FileMachineStorage(Path storageDir, IFactoryManager factoryManager) {
        helper = new MachineStorageHelper(factoryManager);
        storage = FileBasedKeyedStorage.<IMachine>builder()
                .storageDir(storageDir)
                .valueInMapper(this::fromBytes)
                .valueOutMapper(this::toBytes)
                .build();
    }

    public byte[] toBytes(IMachine machine) {
        return helper.toJson(machine).getBytes();
    }

    public IMachine fromBytes(byte[] data) {
        return helper.fromJson(new String(data));
    }

    @Override
    public boolean has(UUID uuid) {
        return storage.has(uuid.toString());
    }

    @Override
    public IMachine get(UUID uuid) {
        return storage.get(uuid.toString());
    }

    @Override
    public Collection<? extends UUID> getKeys() {
        return storage.getKeys().stream().filter(e -> {
            boolean result = e.length() == 36;
            if (!result) Log.warn("[SKIPPED] Invalid UUID Len: " + e);
            return result;
        }).map(UUID::fromString).collect(Collectors.toList());
    }

    @Override
    public void save(UUID uuid, IMachine state) {
        storage.save(uuid.toString(), state);
    }

    @Override
    public void remove(UUID uuid) {
        storage.remove(uuid.toString());
    }

    public static class MachineStorageHelper {
        private final Gson MACHINE_SERIALIZER;

        public MachineStorageHelper(IFactoryManager factories) {
            MACHINE_SERIALIZER = Util.BukkitAPI.gsonBuilderForBukkit()
                    .registerTypeHierarchyAdapter(IMachine.class, new MachineSerializer(factories))
                    .registerTypeHierarchyAdapter(IState.class, new StateSerializer(Util.BukkitAPI.gsonForBukkit()))
                    .create();
        }

        public IMachine fromJson(String json) {
            return fromJson(json, IMachine.class);
        }

        public <T> T fromJson(String json, Class<T> tClass) {
            return MACHINE_SERIALIZER.fromJson(json, tClass);
        }

        public String toJson(Object machine) {
            return MACHINE_SERIALIZER.toJson(machine);
        }
    }
}
