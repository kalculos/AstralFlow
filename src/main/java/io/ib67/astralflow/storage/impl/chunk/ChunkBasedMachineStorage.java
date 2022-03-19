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

import com.google.common.collect.HashBiMap;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import io.ib67.astralflow.AstralFlow;
import io.ib67.astralflow.api.AstralHelper;
import io.ib67.astralflow.hook.HookType;
import io.ib67.astralflow.machines.IMachine;
import io.ib67.astralflow.manager.IFactoryManager;
import io.ib67.astralflow.storage.IMachineStorage;
import io.ib67.astralflow.storage.MachineSerializer;
import io.ib67.astralflow.storage.impl.MachineStorageType;
import io.ib67.util.Pair;
import io.ib67.util.Util;
import io.ib67.util.bukkit.Log;
import lombok.SneakyThrows;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;

import java.lang.ref.WeakReference;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ChunkBasedMachineStorage implements IMachineStorage {
    private static final Gson SERIALIZER = Util.BukkitAPI.gsonForBukkit();
    private final Map<UUID, Location> cachedMachineLocations;
    private final Map<Location, UUID> reversedCachedMachineLocations;


    /// --- Runtime Caches --- ///

    private final Set<Location> cachedLocations = new HashSet<>();

    private final Map<Chunk, Pair<ChunkMachineIndex, MachineData>> chunks = new WeakHashMap<>();

    private final Map<Location, WeakReference<IMachine>> machineCache = new HashMap<>();

    private static final NamespacedKey MACHINE_INDEX_KEY = new NamespacedKey(AstralFlow.getInstance().asPlugin(), "machine_index");
    private static final NamespacedKey MACHINE_DATA_KEY = new NamespacedKey(AstralFlow.getInstance().asPlugin(), "machine_data");

    private final MachineStorageType defaultStorageType;

    private final Map<MachineStorageType, MachineSerializer> serializers = new EnumMap<>(MachineStorageType.class);

    private final IFactoryManager machineFactory;

    @SneakyThrows
    public ChunkBasedMachineStorage(Path dataPath, MachineStorageType defaultStorageType, IFactoryManager machineFactory) {
        this.defaultStorageType = defaultStorageType;
        this.machineFactory = machineFactory;
        //todo add hooks for its data saving

        if (Files.isDirectory(dataPath)) {
            throw new IllegalArgumentException("The provided data path is a directory");
        }
        var a = (HashMap<UUID, Location>) SERIALIZER.fromJson(Files.readString(dataPath), new TypeToken<HashMap<UUID, Location>>() {
        }.getType());
        cachedMachineLocations = a == null ? new HashMap<>() : a;
        reversedCachedMachineLocations = HashBiMap.create(cachedMachineLocations).inverse();
        cachedLocations.addAll(cachedMachineLocations.values());
        HookType.CHUNK_LOAD.register(chunk -> initChunk(chunk.getChunk()));
        HookType.CHUNK_UNLOAD.register(event -> finalizeChunk(event.getChunk()));
    }

    @Override
    public Location getLocationByUUID(UUID uuid) {
        return cachedMachineLocations.get(uuid);
    }

    @Override
    public UUID getUUIDByLocation(Location location) {
        return reversedCachedMachineLocations.get(location);
    }

    @Override
    public boolean has(Location loc) {
        return cachedLocations.contains(AstralHelper.purifyLocation(loc));
    }

    public boolean finalizeChunk(Chunk chunk) {
        chunks.get(chunk).key.machines.forEach((location, machType) -> {
            if (location.getChunk() == chunk) {
                var machine = get(location);

                save(location, machine);
                machine.onUnload();
                AstralFlow.getInstance().getMachineManager().deactivateMachine(machine);

                // write data
                var pdc = location.getChunk().getPersistentDataContainer();
                pdc.set(MACHINE_INDEX_KEY, MachineIndexTag.INSTANCE, chunks.get(location.getChunk()).key);
                pdc.set(MACHINE_DATA_KEY, MachineDataTag.INSTANCE, chunks.get(location.getChunk()).value);
            }
        });
        return true;
    }

    public boolean initChunk(Chunk chunk) {
        var pdc = chunk.getPersistentDataContainer();
        if (!pdc.has(MACHINE_INDEX_KEY, MachineIndexTag.INSTANCE) || !pdc.has(MACHINE_INDEX_KEY, MachineDataTag.INSTANCE)) {
            chunks.put(chunk, Pair.of(new ChunkMachineIndex(new HashMap<>(), chunk.getX(), chunk.getZ()), new MachineData(chunk.getX(), chunk.getZ())));
            return false;
        }
        Pair<ChunkMachineIndex, MachineData> indexMachineDataPair = Pair.of(pdc.get(MACHINE_INDEX_KEY, MachineIndexTag.INSTANCE), pdc.get(MACHINE_DATA_KEY, MachineDataTag.INSTANCE));
        chunks.put(chunk, indexMachineDataPair);

        // load machines.
        indexMachineDataPair.key.machines.keySet().forEach(this::get);
        return true;
    }

    @Override
    public IMachine get(Location locc) {
        var loc = AstralHelper.purifyLocation(locc);

        // check if cached.
        IMachine cachedMachine = machineCache.get(loc).get();
        if (machineCache.containsKey(loc) && cachedMachine != null) {
            return cachedMachine;
        }
        var index = chunks.get(loc.getChunk());

        if (!index.key.hasMachines) {
            return null;
        }
        // read machine data.
        var dataPair = index.value.machineData.get(AstralHelper.purifyLocation(loc));
        //machineCache.put(loc, new WeakReference<>(dataPair.key.fromBytes(dataPair.value)));
        machineCache.put(loc, new WeakReference<>(getSerializer(dataPair.key).fromData(dataPair.value)));
        var machine = machineCache.get(loc).get();
        AstralFlow.getInstance().getMachineManager().setupMachine(machine, true);
        return machineCache.get(loc).get();
    }

    private MachineSerializer getSerializer(MachineStorageType key) {
        return serializers.computeIfAbsent(key, it -> it.apply(machineFactory));
    }

    @Override
    public Collection<? extends Location> getKeys() {
        return new ArrayList<>(cachedLocations); // defensive-copy.
    }

    @Override
    public void save(Location locc, IMachine state) {
        var loc = AstralHelper.purifyLocation(locc);
        cachedLocations.add(loc);
        cachedMachineLocations.put(state.getId(), loc);
        machineCache.put(loc, new WeakReference<>(state));

        boolean chunkLoaded = locc.getWorld().isChunkLoaded(locc.getBlockX() >> 4, locc.getBlockZ() >> 4);
        if (!chunkLoaded) {
            Log.warn("ChunkBasedMachineStorage", "Chunk not loaded when saving machine. This is a potential bug!");
            initChunk(loc.getChunk());
        }
        var v = chunks.get(loc.getChunk()).value;
        var originalMachineData = v.machineData.get(loc);

        if (originalMachineData == null) {
            originalMachineData = Pair.of(defaultStorageType, getSerializer(defaultStorageType).toData(state));
        } else {
            originalMachineData.value = getSerializer(originalMachineData.key).toData(state);
        }
        v.machineData.put(loc, originalMachineData);
    }

    @Override
    public void remove(Location locc) {
        var loc = AstralHelper.purifyLocation(locc);
        var machine = get(loc);
        if (machine == null) return;
        cachedLocations.remove(loc);
        machineCache.remove(loc);
        cachedMachineLocations.remove(machine.getId());

        var indexAndData = chunks.get(loc.getChunk());
        indexAndData.value.machineData.remove(loc);
        indexAndData.key.removeMachine(loc);
    }

}
