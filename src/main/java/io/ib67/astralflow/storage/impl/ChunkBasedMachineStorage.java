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

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import io.ib67.astralflow.api.AstralHelper;
import io.ib67.astralflow.machines.IMachine;
import io.ib67.astralflow.storage.IMachineStorage;
import io.ib67.util.Pair;
import io.ib67.util.Util;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ChunkBasedMachineStorage implements IMachineStorage {
    private static final Gson SERIALIZER = Util.BukkitAPI.gsonForBukkit();
    private final Map<UUID, Location> cachedMachineLocations;
    private final Set<Location> cachedLocations = new HashSet<>();
    private final Map<Chunk, Pair<ChunkMachineIndex, MachineData>> chunks = new WeakHashMap<>();

    private final Map<Location, WeakReference<IMachine>> machineCache = new HashMap<>();

    private final NamespacedKey chunkIndexMetaKey;
    private final NamespacedKey chunkDataMetaKey;

    @SneakyThrows
    public ChunkBasedMachineStorage(Path dataPath, NamespacedKey chunkMetaKey, NamespacedKey chunkDataMetaKey) {
        //todo add hooks for chunk load and unload.
        if (Files.isDirectory(dataPath)) {
            throw new IllegalArgumentException("The provided data path is a directory");
        }
        cachedMachineLocations = SERIALIZER.fromJson(Files.readString(dataPath), new TypeToken<HashMap<UUID, Location>>() {
        }.getType());
        cachedLocations.addAll(cachedMachineLocations.values());
        this.chunkIndexMetaKey = chunkMetaKey;
        this.chunkDataMetaKey = chunkDataMetaKey;
    }

    @Override
    public Location getLocationByUUID(UUID uuid) {
        return cachedMachineLocations.get(uuid);
    }

    @Override
    public boolean has(Location loc) {
        return cachedLocations.contains(AstralHelper.purifyLocation(loc));
    }

    @Override
    public IMachine get(Location locc) {
        var loc = AstralHelper.purifyLocation(locc);

        // check if cached.
        IMachine cachedMachine = machineCache.get(loc).get();
        if (machineCache.containsKey(loc) && cachedMachine != null) {
            return cachedMachine;
        }

        // this will load chunk.
        if (!loc.getWorld().isChunkLoaded(loc.getBlockX() >> 4, loc.getBlockZ() >> 4)) {
            // load data....
            var chunk = loc.getChunk();
            var pdc = loc.getChunk().getPersistentDataContainer();
            if (!pdc.has(chunkIndexMetaKey, MachineIndexTag.INSTANCE) || !pdc.has(chunkIndexMetaKey, MachineDataTag.INSTANCE)) {
                return null;
            }
            chunks.put(chunk, Pair.of(pdc.get(chunkIndexMetaKey, MachineIndexTag.INSTANCE), pdc.get(chunkDataMetaKey, MachineDataTag.INSTANCE)));
        }
        var index = chunks.get(loc.getChunk());
        // initialization.
        var type = index.key.machines.get(loc);

        // read machine data.
        var dataPair = index.value.machineDatas.get(AstralHelper.purifyLocation(loc));
        machineCache.put(loc, new WeakReference<>(dataPair.key.apply(dataPair.value)));
        return machineCache.get(loc).get();
    }

    @Override
    public Collection<? extends Location> getKeys() {
        return null;
    }

    @Override
    public void save(Location uuid, IMachine state) {

    }

    @Override
    public void remove(Location uuid) {

    }

    @ApiStatus.Internal
    @RequiredArgsConstructor
    public static final class MachineData {
        // 谁也阻止不了我开摆
        private final Map<Location, Pair<MachineStorageType, byte[]>> machineDatas = new HashMap<>();
        private final int chunkX;
        private final int chunkZ;
    }

    @ApiStatus.Internal
    public static final class MachineDataTag implements PersistentDataType<byte[], MachineData> {
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
        public byte[] toPrimitive(@NotNull ChunkBasedMachineStorage.MachineData complex, @NotNull PersistentDataAdapterContext context) {
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

    @ApiStatus.Internal
    public static final class ChunkMachineIndex {

        private final Map<Location, String> machines;
        private final int chunkX;
        private final int chunkZ;
        private boolean hasMachines = false;

        public ChunkMachineIndex(Map<Location, String> machines, int chunkX, int chunkZ) {
            this.machines = machines;
            this.chunkX = chunkX;
            this.chunkZ = chunkZ;
            if (!machines.isEmpty()) {
                hasMachines = true;
            }
        }

        public void addMachine(IMachine machine) {
            hasMachines = true;
            machines.put(machine.getLocation(), machine.getType().getName());
        }

        public void removeMachine(Location loc) {
            machines.remove(loc);
            if (machines.isEmpty()) {
                hasMachines = false;
            }
        }
    }

    @ApiStatus.Internal
    public static final class MachineIndexTag implements PersistentDataType<byte[], ChunkMachineIndex> {
        public static final MachineIndexTag INSTANCE = new MachineIndexTag();
        private static final int STORAGE_VERSION = 0;

        public static void writeEntries(Collection<? extends Map.Entry<Location, String>> collection, ByteBuf buffer) {
            // [typeNameLen] [typeName] [Location]
            for (Map.Entry<Location, String> pair : collection) {
                var loc = pair.getKey();
                var type = pair.getValue();
                buffer.writeInt(type.length());
                buffer.writeBytes(type.getBytes(UTF_8));
                writeLocation(loc, buffer);
            }
        }

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

        public static Map<Location, String> readEntries(int chunkX, int chunkZ, int count, ByteBuf buf) {
            var result = new HashMap<Location, String>(count, 2); // avoid-resizing
            for (int i = 1; i < count; i++) {
                var nameLen = buf.readInt();
                var typeName = buf.readBytes(nameLen).toString();
                var loc = readLocation(chunkX, chunkZ, buf);
                result.put(loc, typeName);
            }
            return result;
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
        public byte[] toPrimitive(@NotNull ChunkBasedMachineStorage.ChunkMachineIndex complex, @NotNull PersistentDataAdapterContext context) {
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
            buffer.writeByte(STORAGE_VERSION);
            buffer.writeInt(complex.chunkX);
            buffer.writeInt(complex.chunkZ);
            buffer.writeBoolean(complex.hasMachines);
            buffer.writeInt(complex.machines.size());
            writeEntries(complex.machines.entrySet(), buffer);
            var result = buffer.array();
            buffer.release();
            return result;
        }

        @NotNull
        @Override
        public ChunkMachineIndex fromPrimitive(@NotNull byte[] primitive, @NotNull PersistentDataAdapterContext context) {

            var buf = Unpooled.wrappedBuffer(primitive);
            var version = buf.readByte();
            var chunkX = buf.readInt();
            var chunkZ = buf.readInt();
            if (version != STORAGE_VERSION) {
                throw new IllegalArgumentException("Unsupported version: " + version);
            }
            var hasMachines = buf.readBoolean();
            if (!hasMachines) {
                return new ChunkMachineIndex(new HashMap<>(), chunkX, chunkZ);
            }
            var count = buf.readInt();
            var entries = readEntries(chunkX, chunkZ, count, buf);
            buf.release();
            return new ChunkMachineIndex(entries, chunkX, chunkZ);
        }
    }
}
