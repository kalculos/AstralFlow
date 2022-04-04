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
import io.ib67.astralflow.machines.IState;
import io.ib67.util.Util;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Type;

@RequiredArgsConstructor
public final class StateSerializer implements JsonSerializer<IState>, JsonDeserializer<IState> {
    private static final String KEY_TYPE = "type";
    private static final String KEY_DATA = "data";
    private final Gson defaultSerializer;

    @Override
    public IState deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        // assertion 1. context is a bukkit compatible serializer
        var jo = json.getAsJsonObject();
        var clazName = jo.get(KEY_TYPE).getAsString();
        var result = Util.runCatching(() -> (Object) Class.forName(clazName)).onSuccess(claz -> {
            return defaultSerializer.fromJson(jo.getAsJsonObject(KEY_DATA), (Type) claz);
        }).getResult();
        if (result == null) {
            throw new JsonParseException("Can't find state type: " + clazName); // constant if-condition fixes idea highlight rendering.
        }
        return (IState) result;
    }

    @Override
    public JsonElement serialize(IState src, Type typeOfSrc, JsonSerializationContext context) {
        var jo = new JsonObject();
        jo.addProperty(KEY_TYPE, src.getType().getName());
        jo.add(KEY_DATA, defaultSerializer.toJsonTree(src));
        return jo;
    }
}
