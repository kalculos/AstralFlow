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

package io.ib67.astralflow.internal;

import com.google.gson.*;
import io.ib67.astralflow.manager.IFactoryManager;
import io.ib67.astralflow.storage.ItemStateStorage;
import io.ib67.astralflow.storage.impl.FileItemStorage;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Type;
import java.nio.file.Path;

@RequiredArgsConstructor
public class ItemStorageSerializer implements JsonSerializer<ItemStateStorage>, JsonDeserializer<ItemStateStorage> {
    private final Path storage;
    private final IFactoryManager factory;

    @Override
    public ItemStateStorage deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return new FileItemStorage(storage, factory);
    }

    @Override
    public JsonElement serialize(ItemStateStorage src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive("filestorage"); // TODO
    }
}
