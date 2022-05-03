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

package io.ib67.astralflow.internal.storage;

import io.ib67.astralflow.machines.IMachine;
import io.ib67.astralflow.manager.IMachineManager;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.UUID;

@ApiStatus.AvailableSince("0.1.0")
public interface IMachineStorage extends KeyedStorage<Location, IMachine> {
    void init(IMachineManager manager);

    Location getLocationByUUID(UUID uuid);

    UUID getUUIDByLocation(Location location);

    Collection<? extends IMachine> getMachinesByChunk(Chunk chunk);

    void initChunk(Chunk chunk);

    void finalizeChunk(Chunk chunk, boolean isUnloading);
}
