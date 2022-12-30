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

import io.ib67.astralflow.internal.storage.impl.chunk.ChunkMachineIndex;
import io.ib67.internal.util.bukkit.BukkitGson;
import io.ib67.internal.util.bukkit.Log;
import io.ib67.kiwi.RandomHelper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.bukkit.Location;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static io.ib67.astralflow.internal.storage.impl.chunk.BufferUtil.*;
import static io.ib67.kiwi.Kiwi.runAny;
import static java.nio.charset.StandardCharsets.UTF_8;

@ApiStatus.Internal
public final class MachineIndexTag implements PersistentDataType<byte[], ChunkMachineIndex> {
    public static final MachineIndexTag INSTANCE = new MachineIndexTag();
    private static final int STORAGE_VERSION = 2;

    public static Map<Location, String> readEntries0(int chunkX, int chunkZ, int count, ByteBuf buf) {
        var result = new HashMap<Location, String>(count, 2); // avoid-resizing at loading
        for (int i = 0; i < count; i++) {
            var nameLen = new byte[buf.readInt()];
            buf.readBytes(nameLen);
            var typeName = new String(nameLen, UTF_8);
            var loc = readLocation(chunkX, chunkZ, buf);
            result.put(loc, typeName);
        }
        return new HashMap<>(result); // or the map cannot be resized.
    }

    public static void writeEntries2(Collection<? extends Map.Entry<Location, String>> collection, ByteBuf buffer) {
        // cpool [size] { [Len][Data] } [typeCPoolId] [Location]
        var constants = new ArrayList<String>(collection.size());
        for (Map.Entry<Location, String> locationStringEntry : collection) {
            if (!constants.contains(locationStringEntry.getValue())) constants.add(locationStringEntry.getValue());
        }
        // write cpool
        buffer.writeInt(constants.size());
        for (var constant : constants) {
            buffer.writeShort(constant.length());
            buffer.writeBytes(constant.getBytes(UTF_8));
        }

        //write entries
        for (Map.Entry<Location, String> pair : collection) {
            var loc = pair.getKey();
            var type = constants.indexOf(pair.getValue());
            buffer.writeInt(type);
            writeLocation2(loc, buffer);
        }
    }

    public static Map<Location, String> readEntries1(int chunkX, int chunkZ, int count, ByteBuf buf) {
        // read constant pool
        var poolSize = buf.readInt();
        var constants = new String[poolSize];
        for (int i = 0; i < poolSize; i++) {
            var nameLen = new byte[buf.readInt()];
            buf.readBytes(nameLen);
            constants[i] = new String(nameLen, UTF_8);
        }

        //read entries
        var result = new HashMap<Location, String>(count, 2); // avoid-resizing at loading
        for (int i = 0; i < count; i++) {
            var typeName = constants[buf.readInt()];
            var loc = readLocation(chunkX, chunkZ, buf);
            result.put(loc, typeName);
        }
        return new HashMap<>(result); // or the map cannot be resized.
    }

    public static Map<Location, String> readEntries2(int chunkX, int chunkZ, int count, ByteBuf buf) {
        // read constant pool
        var poolSize = buf.readInt();
        var constants = new String[poolSize];
        for (int i = 0; i < poolSize; i++) {
            var nameLen = new byte[buf.readShort()];
            buf.readBytes(nameLen);
            constants[i] = new String(nameLen, UTF_8);
        }

        //read entries
        var result = new HashMap<Location, String>(count, 2); // avoid-resizing at loading
        for (int i = 0; i < count; i++) {
            var typeName = constants[buf.readInt()];
            var loc = readLocation2(chunkX, chunkZ, buf);
            result.put(loc, typeName);
        }
        return new HashMap<>(result); // or the map cannot be resized.
    }

    @NotNull
    @Override
    public Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @NotNull
    @Override
    public Class<ChunkMachineIndex> getComplexType() {
        return ChunkMachineIndex.class;
    }

    @NotNull
    @Override
    public byte[] toPrimitive(@NotNull ChunkMachineIndex complex, @NotNull PersistentDataAdapterContext context) {
        /**
         * [version]
         * [chunkX]
         * [chunkZ]
         * [hasMachine]
         * [machineCounts]
         * Machine List...
         * [machineLocation]
         * [machineType]
         */
        var buffer = Unpooled.buffer();
        try {
            buffer.writeByte(STORAGE_VERSION);
            buffer.writeInt(complex.getChunkX());
            buffer.writeInt(complex.getChunkZ());
            buffer.writeBoolean(complex.isHasMachines());
            buffer.writeInt(complex.getMachineTypes().size());
            writeEntries2(complex.getEntries(), buffer);
            return buffer.array();
        } catch (Throwable t) {
            t.printStackTrace();
            Log.warn("CBMS", "Cannot save data for chunk " + complex.getChunkX() + ", " + complex.getChunkZ());
            Log.warn("CBMS", "Trying to dump data...");
            var fileName = "astralflow-error-chunkdump-" + RandomHelper.pick(complex.getLocations()).map(e -> e.getWorld().getName()).or("UNKNOWN_WORLD") + "-" + Instant.now() + "-" + complex.getChunkX() + "-" + complex.getChunkZ() + ".json";
            runAny(() -> Files.writeString(Path.of(fileName), BukkitGson.INSTANCE.toJson(complex)));
            Log.warn("CBMS", "Dumped data to " + fileName);

            // create a empty one.
            buffer.clear();
            buffer.writeByte(STORAGE_VERSION);
            buffer.writeInt(complex.getChunkX());
            buffer.writeInt(complex.getChunkZ());
            buffer.writeBoolean(false);
            return buffer.array();
        } finally {
            buffer.release();
        }
    }

    @NotNull
    @Override
    public ChunkMachineIndex fromPrimitive(@NotNull byte[] primitive, @NotNull PersistentDataAdapterContext context) {

        var buf = Unpooled.wrappedBuffer(primitive);
        var version = buf.readByte();
        var chunkX = buf.readInt();
        var chunkZ = buf.readInt();
        if (version != STORAGE_VERSION) {
            switch (version) {
                case 0:
                    return readVersion0(buf, chunkX, chunkZ);
                case 1:
                    return readVersion1(buf, chunkX, chunkZ);
                default:
                    throw new UnsupportedOperationException("Unknown version: " + version);
            }
        }
        try {
            var hasMachines = buf.readBoolean();
            if (!hasMachines) {
                return new ChunkMachineIndex(new HashMap<>(), chunkX, chunkZ);
            }
            var count = buf.readInt();
            var entries = readEntries2(chunkX, chunkZ, count, buf);
            buf.release();
            return new ChunkMachineIndex(entries, chunkX, chunkZ);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            Log.warn("CBMS", "Cannot load chunk data!");
            return new ChunkMachineIndex(new HashMap<>(), chunkX, chunkZ);
        }
    }

    private ChunkMachineIndex readVersion1(ByteBuf buf, int chunkX, int chunkZ) {
        var hasMachines = buf.readBoolean();
        if (!hasMachines) {
            return new ChunkMachineIndex(new HashMap<>(), chunkX, chunkZ);
        }
        var count = buf.readInt();
        var entries = readEntries1(chunkX, chunkZ, count, buf);
        buf.release();
        return new ChunkMachineIndex(entries, chunkX, chunkZ);
    }

    public ChunkMachineIndex readVersion0(ByteBuf buf, int chunkX, int chunkZ) {
        var hasMachines = buf.readBoolean();
        if (!hasMachines) {
            return new ChunkMachineIndex(new HashMap<>(), chunkX, chunkZ);
        }
        var count = buf.readInt();
        var entries = readEntries0(chunkX, chunkZ, count, buf);
        buf.release();
        return new ChunkMachineIndex(entries, chunkX, chunkZ);
    }
}
