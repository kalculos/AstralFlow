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

package io.ib67.astralflow.internal.storage.impl.chunk;

import io.ib67.astralflow.AstralFlow;
import io.ib67.astralflow.api.AstralHelper;
import io.ib67.astralflow.internal.AstralConstants;
import io.ib67.astralflow.internal.storage.IMachineStorage;
import io.ib67.astralflow.internal.storage.impl.MachineStorageType;
import io.ib67.astralflow.internal.storage.impl.chunk.tag.MachineDataTag;
import io.ib67.astralflow.internal.storage.impl.chunk.tag.MachineIndexTag;
import io.ib67.astralflow.machines.IMachine;
import io.ib67.astralflow.manager.IFactoryManager;
import io.ib67.astralflow.manager.IMachineManager;
import io.ib67.astralflow.util.LogCategory;
import io.ib67.internal.util.bukkit.Log;
import io.ib67.kiwi.reflection.AccessibleClass;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;

import java.util.*;

public class ChunkBasedMachineStorage implements IMachineStorage {

    public static final NamespacedKey MACHINE_INDEX_TAG = new NamespacedKey(AstralFlow.getInstance().asPlugin(), "machine_index_tag");
    public static final NamespacedKey MACHINE_DATA_TAG = new NamespacedKey(AstralFlow.getInstance().asPlugin(), "machine_data_tag");
    private final MachineCache machineCache;

    private final Map<Chunk, InMemoryChunk> chunkMap;
    private final IFactoryManager factoryManager;
    private final MachineStorageType defaultSerializer;
    private InMemoryChunkFactory chunkFactory;

    public ChunkBasedMachineStorage(MachineCache cache, IFactoryManager factoryManager, MachineStorageType defaultSerializer, int initialCapacity, boolean allowResizing) {
        Objects.requireNonNull(factoryManager, "factoryManager cannot be null");
        Objects.requireNonNull(defaultSerializer, "defaultSerializer cannot be null");
        Objects.requireNonNull(cache, "machine cache cannot be null");
        chunkMap = new HashMap<>(Math.max(initialCapacity, 256)); // at least you need 256
        if (!allowResizing) {
            AccessibleClass.of(HashMap.class).virtualField("threshold").set((HashMap<Chunk, InMemoryChunk>) chunkMap, Integer.MAX_VALUE);
        }
        this.machineCache = cache;
        this.factoryManager = factoryManager;
        this.defaultSerializer = defaultSerializer;
    }

    public void finalizeChunk(Chunk unloadingChunk, boolean isUnloading) {
        Objects.requireNonNull(unloadingChunk, "chunk cannot be null");
        Objects.requireNonNull(chunkFactory, "MachineStorage hasn't been initialized");
        if (!chunkMap.containsKey(unloadingChunk)) {
            var inmem = chunkFactory.loadChunk(unloadingChunk); // todo: faster cache
            if (inmem.getMachines().size() == 0) {
                return;
            }
            Log.warn("CBMS", "It seems that chunk " + unloadingChunk.getX() + "," + unloadingChunk.getZ() + " is not registered in the chunk map. This may be a potential bug.");
            return;
        }
        var memChunk = chunkMap.get(unloadingChunk);
        if (AstralConstants.DEBUG) {
            if (memChunk.getMachines().size() != 0)
                Log.info(LogCategory.DEBUG, (isUnloading ? "UNLOADING" : "LOADED") + " " + memChunk.getMachines().size() + " machines in chunk " + unloadingChunk.getX() + "," + unloadingChunk.getZ() + " will be saved.");
        }
        for (IMachine machine : memChunk.getMachines()) {
            this.save(machine.getLocation(), machine); // avoiding undefined behaviours.
        }
        if (AstralConstants.DEBUG) {
            if (memChunk.getMachines().size() != 0)
                Log.info(LogCategory.DEBUG, "Done. Flushing cache");
        }
        if (isUnloading) {
            chunkMap.remove(unloadingChunk);
        }
        flushChunkCache(unloadingChunk, memChunk);
    }

    private void flushChunkCache(Chunk chunk, InMemoryChunk memChunk) {
        Objects.requireNonNull(chunkFactory, "MachineStorage hasn't been initialized");
        var pdc = chunk.getPersistentDataContainer();
        pdc.set(MACHINE_INDEX_TAG, MachineIndexTag.INSTANCE, memChunk.getIndex());
        pdc.set(MACHINE_DATA_TAG, MachineDataTag.INSTANCE, memChunk.getMachineDatas());
    }

    @Override
    public void init(IMachineManager manager) {
        this.chunkFactory = new InMemoryChunkFactory(
                factoryManager,
                manager,
                defaultSerializer,
                MACHINE_INDEX_TAG,
                MACHINE_DATA_TAG
        );
    }

    /* DELEGATED */
    @Override
    public Location getLocationByUUID(UUID uuid) {
        Objects.requireNonNull(chunkFactory, "MachineStorage hasn't been initialized");
        return machineCache.getLocationByUUID(uuid);
    }

    @Override
    public UUID getUUIDByLocation(Location location) {
        Objects.requireNonNull(location, "location cannot be null");
        Objects.requireNonNull(chunkFactory, "MachineStorage hasn't been initialized");
        return machineCache.getUUIDByLocation(AstralHelper.purifyLocation(location));
    }

    @Override
    public Collection<? extends IMachine> getMachinesByChunk(Chunk chunk) {
        Objects.requireNonNull(chunkFactory, "MachineStorage hasn't been initialized");
        var imc = initChunk$lazy(chunk, false);
        if (imc == null) {
            return Collections.emptyList();
        }
        return chunkMap.get(chunk).getMachines();
    }

    @Override
    public boolean has(Location uuid) {
        Objects.requireNonNull(chunkFactory, "MachineStorage hasn't been initialized");
        Objects.requireNonNull(uuid, "location cannot be null");
        // check cache.
        return getUUIDByLocation(AstralHelper.purifyLocation(uuid)) == null;
    }

    @Override
    public void initChunk(Chunk chunk) {
        Objects.requireNonNull(chunkFactory, "MachineStorage hasn't been initialized");
        Objects.requireNonNull(chunk, "chunk cannot be null");
        initChunk$lazy(chunk, false);
    }

    private InMemoryChunk initChunk$lazy(Chunk chunk, boolean create) { // actually it's a lazy init, returning the chunk can be used or null
        if (chunkMap.containsKey(chunk)) {
            return chunkMap.get(chunk); // or it will override the original data.
        }
        var IMChunk = chunkFactory.loadChunk(chunk);
        if (IMChunk.getMachines().size() == 0) {
            if (create) {
                chunkMap.put(chunk, IMChunk);
                return IMChunk;
            } else {
                return null;
            }
        } else {
            chunkMap.put(chunk, IMChunk);
            return IMChunk;
        }
    }


    @Override
    public IMachine get(Location aloc) {
        Objects.requireNonNull(chunkFactory, "MachineStorage hasn't been initialized");
        Objects.requireNonNull(aloc, "location cannot be null");
        var loc = AstralHelper.purifyLocation(aloc);
        if (!AstralHelper.isChunkLoaded(loc)) {
            var imc = initChunk$lazy(loc.getChunk(), false);
            if (imc == null) {
                return null;
            }
        }
        var inMemoryChunk = chunkMap.get(loc.getChunk());
        if (inMemoryChunk == null) {
            return null;
        }
        return inMemoryChunk.getMachine(loc);
    }

    @Override
    public Collection<? extends Location> getKeys() {
        return machineCache.getAllMachineLocation();
    }

    @Override
    public void save(Location aloc, IMachine state) {
        Objects.requireNonNull(chunkFactory, "MachineStorage hasn't been initialized");
        Objects.requireNonNull(aloc, "loc cannot be null");
        Objects.requireNonNull(state, "state cannot be null");

        var loc = AstralHelper.purifyLocation(aloc);
        if (!AstralHelper.equalsLocationFuzzily(loc, state.getLocation())) {
            Log.warn("CBMS", "Location and machine location are not equal! " + loc + " != " + state.getLocation() + " ,this may cause SECURITY issues.");
        }
        if (!AstralHelper.isChunkLoaded(loc) || !chunkMap.containsKey(loc.getChunk())) {
            initChunk$lazy(loc.getChunk(), true);
        }
        chunkMap.get(loc.getChunk()).saveMachine(loc, state);
        machineCache.update(state.getId(), loc);
    }

    @Override
    public void remove(Location aloc) {
        Objects.requireNonNull(chunkFactory, "MachineStorage hasn't been initialized");
        Objects.requireNonNull(aloc, "loc cannot be null");
        var loc = AstralHelper.purifyLocation(aloc);
        if (!AstralHelper.isChunkLoaded(loc) || !chunkMap.containsKey(loc.getChunk())) {
            var imc = initChunk$lazy(loc.getChunk(), true);
            if (imc == null) return;
        }
        chunkMap.get(loc.getChunk()).removeMachine(loc);
        machineCache.remove(loc);
    }

    @Override
    public void flush() {
        Objects.requireNonNull(chunkFactory, "MachineStorage hasn't been initialized");
        for (Chunk chunk : chunkMap.keySet()) {
            finalizeChunk(chunk, false);
        }
        machineCache.save();
    }
}
