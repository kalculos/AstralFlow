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

import io.ib67.astralflow.storage.impl.MachineStorageType;
import io.ib67.util.Pair;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

@ApiStatus.Internal
public final class MachineDataTag implements PersistentDataType<byte[], MachineData> {
    public static final MachineDataTag INSTANCE = new MachineDataTag();
    private static final int STORAGE_VERSION = 0;

    public static void writeLocation(Location loc, ByteBuf buf) {
        // [name len] [name] [x(1 byte)] [y(1b)] [z(1b)]
        var worldName = loc.getWorld().getName();
        buf.writeInt(worldName.length());
        buf.writeBytes(worldName.getBytes(UTF_8));
        buf.writeByte(loc.getBlockX() & 15);
        buf.writeByte(loc.getBlockY());
        buf.writeByte(loc.getBlockZ() & 15);
    }

    public static Location readLocation(int chunkX, int chunkZ, ByteBuf buf) {
        var worldNameLen = buf.readInt();
        var worldName = buf.readBytes(worldNameLen).toString();
        var x = buf.readByte();
        var y = buf.readByte();
        var z = buf.readByte();
        return new Location(Bukkit.getWorld(worldName), (chunkX * 16) + x, y, (chunkZ * 16) + z);
    }

    @NotNull
    @Override
    public Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @NotNull
    @Override
    public Class<MachineData> getComplexType() {
        return MachineData.class;
    }

    @NotNull
    @Override
    public byte[] toPrimitive(@NotNull MachineData complex, @NotNull PersistentDataAdapterContext context) {
        /**
         * [version][chunkXZ][count]{ [int(the loc hash)] [dataType] [dataLen][data] }
         */
        var buf = Unpooled.buffer();
        buf.writeByte(STORAGE_VERSION);
        buf.writeInt(complex.chunkX);
        buf.writeInt(complex.chunkZ);
        buf.writeInt(complex.machineDatas.size());
        for (Map.Entry<Location, Pair<MachineStorageType, byte[]>> longPairEntry : complex.machineDatas.entrySet()) {
            var intHash = longPairEntry.getKey();
            // write loc
            writeLocation(longPairEntry.getKey(), buf);

            buf.writeByte(longPairEntry.getValue().key.getTypeIndex());
            var data = longPairEntry.getValue().value;
            buf.writeInt(data.length);
            buf.writeBytes(data);
        }
        var result = buf.array();
        buf.release();
        return result;
    }

    @NotNull
    @Override
    public MachineData fromPrimitive(@NotNull byte[] primitive, @NotNull PersistentDataAdapterContext context) {
        var buf = Unpooled.wrappedBuffer(primitive);
        var version = buf.readByte();
        if (version != STORAGE_VERSION) {
            throw new IllegalArgumentException("Unsupported version: " + version);
        }
        var chunkX = buf.readInt();
        var chunkZ = buf.readInt();
        var count = buf.readInt();
        var result = new MachineData(chunkX, chunkZ);
        for (int i = 0; i < count; i++) {
            // read loc
            var loc = readLocation(chunkX, chunkZ, buf);

            var type = MachineStorageType.getType(buf.readByte());
            var dataLen = buf.readInt();
            var data = buf.readBytes(dataLen).array();
            result.machineDatas.put(loc, Pair.of(type, data));
        }
        buf.release();
        return result;
    }
}
