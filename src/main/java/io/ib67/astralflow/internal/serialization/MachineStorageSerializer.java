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

package io.ib67.astralflow.internal.serialization;

import com.google.gson.*;
import io.ib67.astralflow.internal.storage.IMachineStorage;
import io.ib67.astralflow.internal.storage.impl.MachineStorageType;
import io.ib67.astralflow.internal.storage.impl.chunk.ChunkBasedMachineStorage;
import io.ib67.astralflow.internal.storage.impl.chunk.MachineCache;
import io.ib67.astralflow.manager.IFactoryManager;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Type;
import java.nio.file.Path;

// todo more storage support
@RequiredArgsConstructor
public final class MachineStorageSerializer implements JsonDeserializer<IMachineStorage>, JsonSerializer<IMachineStorage> {
    private final Path storage;
    private final IFactoryManager factory;

    @Override
    public IMachineStorage deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return new ChunkBasedMachineStorage(new MachineCache(storage), factory, MachineStorageType.JSON); // todo
    }

    @Override
    public JsonElement serialize(IMachineStorage src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive("filestorage");
    }
}
