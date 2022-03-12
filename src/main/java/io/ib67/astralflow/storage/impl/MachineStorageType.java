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

import io.ib67.astralflow.machines.IMachine;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.util.function.Function;

import static io.ib67.astralflow.internal.MachineStorageHelper.HELPER;

@RequiredArgsConstructor
@Getter
public enum MachineStorageType {
    JSON(0,
            bytes -> HELPER.fromJson(new String(bytes)),
            machine -> HELPER.toJson(machine).getBytes(StandardCharsets.UTF_8);
    );
    private final int typeIndex;
    private final Function<byte[], IMachine> deserializer;
    private final Function<IMachine, byte[]> serializer;

    public static MachineStorageType getType(int index) {
        return switch (index) {
            case 0 -> JSON;
            default -> throw new IllegalArgumentException("Invalid type index");
        };
    }

    public IMachine fromBytes(byte[] bytes) {
        return deserializer.apply(bytes);
    }

    public byte[] toBytes(IMachine machine) {
        return serializer.apply(machine);
    }
}
