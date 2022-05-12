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

package io.ib67.astralflow.internal.storage.impl.chunk.tag;

import io.ib67.astralflow.internal.storage.impl.MachineStorageType;
import io.ib67.astralflow.internal.storage.impl.chunk.MachineData;
import io.ib67.util.bukkit.Log;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.bukkit.Location;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.inlambda.kiwi.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static io.ib67.astralflow.internal.storage.impl.chunk.BufferUtil.*;
import static org.inlambda.kiwi.Kiwi.pairOf;

@ApiStatus.Internal
public final class MachineDataTag implements PersistentDataType<byte[], MachineData> {
    public static final MachineDataTag INSTANCE = new MachineDataTag();
    private static final int STORAGE_VERSION = 1;


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
        try {
            buf.writeByte(STORAGE_VERSION);
            buf.writeInt(complex.getChunkX());
            buf.writeInt(complex.getChunkZ());
            buf.writeInt(complex.getMachineData().size());
            for (Map.Entry<Location, Pair<MachineStorageType, byte[]>> longPairEntry : complex.getMachineData().entrySet()) {
                var intHash = longPairEntry.getKey();
                // write loc
                writeLocation2(longPairEntry.getKey(), buf);

                buf.writeByte(longPairEntry.getValue().left.getTypeIndex());
                var data = longPairEntry.getValue().right;
                buf.writeInt(data.length);
                buf.writeBytes(data);
            }
            var result = buf.array();
            buf.release();
            return result;
        } catch (Throwable t) {
            t.printStackTrace();
            Log.warn("CBMS", "Failed to write machine data!!");
            buf.clear();
            buf.writeByte(STORAGE_VERSION);
            buf.writeInt(complex.getChunkX());
            buf.writeInt(complex.getChunkZ());
            buf.writeInt(0);
            var result = buf.array();
            buf.release();
            return result;
        }
    }

    @NotNull
    @Override
    public MachineData fromPrimitive(@NotNull byte[] primitive, @NotNull PersistentDataAdapterContext context) {
        var buf = Unpooled.wrappedBuffer(primitive);
        var version = buf.readByte();
        if (version != STORAGE_VERSION) {
            switch (version) {
                case 0:
                    return fromPrimitiveV0(buf);
                default:
                    throw new IllegalArgumentException("Unknown version: " + version);
            }
        }
        try {
            var chunkX = buf.readInt();
            var chunkZ = buf.readInt();
            var count = buf.readInt();
            var result = new MachineData(chunkX, chunkZ);
            for (int i = 0; i < count; i++) {
                // read loc
                var loc = readLocation2(chunkX, chunkZ, buf);

                var type = MachineStorageType.getType(buf.readByte());
                var dataLen = buf.readInt();
                var data = new byte[dataLen];
                buf.readBytes(data);
                result.getMachineData().put(loc, new Pair<>(type, data));
            }
            return result;
        } catch (Throwable t) {
            Log.warn("Failed to read machine data!!");
            return null;
        } finally {
            buf.release();
        }
    }

    @Deprecated
    private MachineData fromPrimitiveV0(ByteBuf buf) {
        var chunkX = buf.readInt();
        var chunkZ = buf.readInt();
        var count = buf.readInt();
        var result = new MachineData(chunkX, chunkZ);
        for (int i = 0; i < count; i++) {
            // read loc
            var loc = readLocation(chunkX, chunkZ, buf);

            var type = MachineStorageType.getType(buf.readByte());
            var dataLen = buf.readInt();
            var data = new byte[dataLen];
            buf.readBytes(data);
            result.getMachineData().put(loc, pairOf(type, data));
        }
        buf.release();
        return result;
    }
}
