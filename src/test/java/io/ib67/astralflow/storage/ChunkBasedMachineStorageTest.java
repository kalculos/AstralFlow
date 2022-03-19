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

package io.ib67.astralflow.storage;

import io.ib67.astralflow.AstralFlow;
import io.ib67.astralflow.api.factories.StatelessMachineFactory;
import io.ib67.astralflow.storage.impl.MachineStorageType;
import io.ib67.astralflow.storage.impl.chunk.*;
import io.ib67.astralflow.test.TestUtil;
import io.ib67.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ChunkBasedMachineStorageTest {
    private ChunkBasedMachineStorage storage;

    @BeforeAll
    public void setup() {
        TestUtil.init();
    }

    @Test
    public void testMachineDataTag() {
        var machineData = new MachineData(114, 514);

        var worldMock = Bukkit.getWorld("world");

        machineData.getMachineData().put(new Location(worldMock, 1, 1, 1), Pair.of(MachineStorageType.JSON, """
                {"a":"b"}
                """.trim().getBytes(StandardCharsets.UTF_8)));
        // serialize machine data
        var tag = MachineDataTag.INSTANCE;
        var serializedData = tag.toPrimitive(machineData, null);

        // deserialize.
        var desMd = tag.fromPrimitive(serializedData, null);
        assertArrayEquals(machineData.getMachineData().get(new Location(worldMock, 1, 1, 1)).value, desMd.getMachineData().get(new Location(worldMock, 1, 1, 1)).value, "Test MachineData Serialization");
    }

    @Test
    public void testMachineIndexTag() {
        var tag = MachineIndexTag.INSTANCE;
        var machineIndex = new ChunkMachineIndex(Map.of(
                new Location(Bukkit.getWorld("world"), 1, 1, 1),
                "dummydummy"
        ), 1, 1);
        var serializedData = tag.toPrimitive(machineIndex, null);
        var desMd = tag.fromPrimitive(serializedData, null);
        assertEquals(machineIndex.getMachineType(new Location(Bukkit.getWorld("world"), 1, 1, 1)), desMd.getMachineType(new Location(Bukkit.getWorld("world"), 1, 1, 1)), "Test MachineIndex Serialization");

        //todo test empty machine index.
    }

    @Test
    public void testChunkStorage() throws IOException {
        // register factory
        AstralFlow.getInstance().getFactories().register(DummyStatefulMachine.class, new StatelessMachineFactory<>((l, u) -> new DummyStatefulMachine(u, l)));
        var file = AstralFlow.getInstance().asPlugin().getDataFolder().toPath().resolve("test.index");
        Files.createFile(file);
        var randomLoc = new Location(Bukkit.getWorld("world"), ThreadLocalRandom.current().nextInt(3000), 1, ThreadLocalRandom.current().nextInt(3000));
        var storage = new ChunkBasedMachineStorage(new MachineCache(file), AstralFlow.getInstance().getFactories(), MachineStorageType.JSON);
        storage.initChunk(randomLoc.getChunk());
        var machine = new DummyStatefulMachine(UUID.randomUUID(), randomLoc);
        storage.save(randomLoc, machine);

        // read
        storage.finalizeChunk(randomLoc.getChunk());

        storage.initChunk(randomLoc.getChunk());
        var readMachine = (DummyStatefulMachine) storage.get(randomLoc);
        assertNotNull(readMachine.getState());
        assertEquals(readMachine.getState().get("nullcat?"), "sexy!");
    }
}
