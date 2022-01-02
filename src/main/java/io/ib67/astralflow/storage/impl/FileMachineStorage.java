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

package io.ib67.astralflow.storage.impl;

import io.ib67.astralflow.machines.IMachine;
import io.ib67.astralflow.storage.IMachineStorage;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class FileMachineStorage implements IMachineStorage {
    private final Path storage;

    @Override
    public boolean isAvailable() {
        return true; // should we check for availability?
    }

    @Override
    public Optional<? extends IMachine> readMachine(UUID uuid) {
        var file = storage.resolve(uuid.toString() + ".json").toFile();
        if (!file.exists()) {
            return Optional.empty();
        }

    }

    @Override
    public boolean saveMachine(IMachine machine) {
        return false;
    }

    @Override
    public Collection<UUID> getMachines() {
        return null;
    }

    public static class MachineStorageHelper {
        public static IMachine fromJson(String json) {

        }
    }
}
