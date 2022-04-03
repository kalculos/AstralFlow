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

package io.ib67.astralflow.internal;

import com.google.gson.*;
import io.ib67.astralflow.item.ItemKey;

import java.lang.reflect.Type;
import java.util.Objects;

public class ItemKeySerializer implements JsonSerializer<ItemKey>, JsonDeserializer<ItemKey> {
    @Override
    public ItemKey deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonPrimitive()) {
            throw new JsonParseException("ItemKey must be an object");
        }
        var jo = json.getAsJsonObject();
        var id = jo.getAsJsonPrimitive("id").getAsString();
        var namespace = jo.getAsJsonPrimitive("namespace").getAsString();
        Objects.requireNonNull(id);
        Objects.requireNonNull(namespace);
        return ItemKey.from(namespace, id);
    }

    @Override
    public JsonElement serialize(ItemKey src, Type typeOfSrc, JsonSerializationContext context) {
        var jo = new JsonObject();
        jo.addProperty("id", src.getId());
        jo.addProperty("namespace", src.getNamespace());
        return jo;
    }
}
