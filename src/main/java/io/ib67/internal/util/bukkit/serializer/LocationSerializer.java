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

package io.ib67.internal.util.bukkit.serializer;

import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.lang.reflect.Type;

public class LocationSerializer implements JsonSerializer<Location>, JsonDeserializer<Location> {
    public JsonElement serialize(Location src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jo = new JsonObject();
        jo.add("x", new JsonPrimitive(src.getX()));
        jo.add("y", new JsonPrimitive(src.getY()));
        jo.add("z", new JsonPrimitive(src.getZ()));
        jo.add("world", new JsonPrimitive(src.getWorld().getName()));
        return jo;
    }

    @Override
    public Location deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return new Location(Bukkit.getWorld(json.getAsJsonObject().get("world").getAsString()), json.getAsJsonObject().get("x").getAsDouble(), json.getAsJsonObject().get("y").getAsDouble(), json.getAsJsonObject().get("z").getAsDouble());
    }
}
