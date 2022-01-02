/*
 *
 *
 *  *
 *  *     AstralFlow - Storage utilities for spigot servers.
 *  *     Copyright (C) 2022 iceBear67
 *  *
 *  *     This library is free software; you can redistribute it and/or
 *  *     modify it under the terms of the GNU Lesser General Public
 *  *     License as published by the Free Software Foundation; either
 *  *     version 2.1 of the License, or (at your option) any later version.
 *  *
 *  *     This library is distributed in the hope that it will be useful,
 *  *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  *     Lesser General Public License for more details.
 *  *
 *  *     You should have received a copy of the GNU Lesser General Public
 *  *     License along with this library; if not, write to the Free Software
 *  *     Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 *  *     USA
 *
 */

package io.ib67.astralflow.util.internal;
 
import com.google.gson.*;
import io.ib67.Util;
import io.ib67.astralflow.machines.IMachineData;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Type;

@RequiredArgsConstructor
public class MachineDataSerializer implements JsonSerializer<IMachineData>, JsonDeserializer<IMachineData> {
    private static final String KEY_TYPE = "type";
    private static final String KEY_DATA = "data";
    private final Gson defaultSerializer;

    @Override
    public IMachineData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        // assertion 1. context is a bukkit compatible serializer
        var jo = json.getAsJsonObject();
        var clazName = jo.get(KEY_TYPE).getAsString();
        return (IMachineData) Util.runCatching(() -> {
            return (Object) Class.forName(clazName);
        }).onFailure(t -> {
            if (true)
                throw new JsonParseException("Can't find machine type: " + clazName, t); // constant if-condition fixes idea highlight rendering.
        }).onSuccess(claz -> {
            return defaultSerializer.fromJson(jo.getAsJsonObject(KEY_DATA), (Type) claz);
        });
    }

    @Override
    public JsonElement serialize(IMachineData src, Type typeOfSrc, JsonSerializationContext context) {
        var jo = new JsonObject();
        jo.addProperty(KEY_TYPE, src.getType().getCanonicalName());
        jo.add(KEY_DATA, defaultSerializer.toJsonTree(src));
        return jo;
    }
}
