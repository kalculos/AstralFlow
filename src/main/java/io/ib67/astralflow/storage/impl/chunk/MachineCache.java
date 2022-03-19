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

package io.ib67.astralflow.storage.impl.chunk;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.bukkit.Location;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public final class MachineCache {
    private final Map<UUID, Location> uuid2Loc;
    private final Map<Location, UUID> loc2uuid;
    private final Path path;

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public MachineCache(Path pathToCache) {
        path = pathToCache;
        Objects.requireNonNull(pathToCache);
        if (!Files.isRegularFile(pathToCache)) {
            throw new IllegalArgumentException("Path is not a file");
        }
        var map = (Map<UUID, Location>) new Gson().fromJson(Files.readString(pathToCache), new TypeToken<Map<UUID, Location>>() {
        }.getType());
        if (map != null) {
            uuid2Loc = map;
            loc2uuid = new HashMap<>(map.size() + 16);
        } else {
            uuid2Loc = new HashMap<>();
            loc2uuid = new HashMap<>();
        }

        map.forEach((uuid, loc) -> loc2uuid.put(loc, uuid));
    }

    public Location getLocationByUUID(UUID uuid) {
        return uuid2Loc.get(uuid);
    }

    public UUID getUUIDByLocation(Location loc) {
        return loc2uuid.get(loc);
    }

    public void update(UUID uuid, Location location) {
        uuid2Loc.put(uuid, location);
        loc2uuid.put(location, uuid);
    }

    public void remove(UUID uuid) {
        loc2uuid.remove(uuid2Loc.remove(uuid));
    }

    public void remove(Location location) {
        uuid2Loc.remove(loc2uuid.remove(location));
    }

    public void clear() {
        uuid2Loc.clear();
        loc2uuid.clear();
    }

    @SneakyThrows
    public void save() {
        var gson = new Gson();
        var json = gson.toJson(uuid2Loc);
        Files.writeString(path, json);
    }

    public Collection<? extends Location> getAllMachineLocation() {
        return loc2uuid.keySet();
    }
}
