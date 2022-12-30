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

package io.ib67.astralflow.storage;

import io.ib67.astralflow.AstralFlow;
import io.ib67.astralflow.internal.storage.SimpleChunkTracker;
import io.ib67.astralflow.internal.storage.impl.MachineStorageType;
import io.ib67.astralflow.internal.storage.impl.chunk.ChunkBasedMachineStorage;
import io.ib67.astralflow.internal.storage.impl.chunk.ChunkMachineIndex;
import io.ib67.astralflow.internal.storage.impl.chunk.MachineCache;
import io.ib67.astralflow.internal.storage.impl.chunk.MachineData;
import io.ib67.astralflow.internal.storage.impl.chunk.tag.MachineDataTag;
import io.ib67.astralflow.internal.storage.impl.chunk.tag.MachineIndexTag;
import io.ib67.astralflow.machines.MachineProperty;
import io.ib67.astralflow.manager.impl.MachineManagerImpl;
import io.ib67.astralflow.test.TestUtil;
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

import static io.ib67.kiwi.Kiwi.pairOf;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public final class ChunkBasedMachineStorageTest {
    private ChunkBasedMachineStorage storage;

    @BeforeAll
    public void setup() {
        TestUtil.init();
    }

    @Test
    public void testMachineDataTag() {

        var worldMock = Bukkit.getWorld("world");
        var first = new Location(worldMock, 22, 2, 41);
        var sec = new Location(worldMock, -22, 2, 41);
        var third = new Location(worldMock, -22, 2, -41);
        var fourth = new Location(worldMock, 22, 2, -41);
        testMDT(first, "first quad");
        testMDT(sec, "second quad");
        testMDT(third, "third quad");
        testMDT(fourth, " fourth quad");
    }

    private void testMDT(Location loc, String reason) {
        var machineData = new MachineData(loc.getChunk().getX(), loc.getChunk().getZ());
        machineData.getMachineData().put(

                loc,

                pairOf(MachineStorageType.JSON, """
                        {"a":"b"}
                        """.trim().getBytes(StandardCharsets.UTF_8)));
        // serialize machine data
        var tag = MachineDataTag.INSTANCE;
        var serializedData = tag.toPrimitive(machineData, null);

        // deserialize.
        var desMd = tag.fromPrimitive(serializedData, null);
        assertArrayEquals(machineData.getMachineData().get(loc).right, desMd.getMachineData().get(loc).right, "Test MachineData Serialization # " + reason);
    }

    @Test
    public void testMachineIndexTag() {
        var worldMock = Bukkit.getWorld("world");
        var first = new Location(worldMock, 22, 2, 41);
        var sec = new Location(worldMock, -22, 2, 41);
        var third = new Location(worldMock, -22, 2, -41);
        var fourth = new Location(worldMock, 22, 2, -41);
        testMIT(first, "first quad");
        testMIT(sec, "second quad");
        testMIT(third, "third quad");
        testMIT(fourth, " fourth quad");
        //todo test empty machine index.
    }

    private void testMIT(Location loc, String reason) {
        var tag = MachineIndexTag.INSTANCE;
        var machineIndex = new ChunkMachineIndex(Map.of(
                loc,
                "dummydummy"
        ), loc.getChunk().getX(), loc.getChunk().getZ());
        var serializedData = tag.toPrimitive(machineIndex, null);
        var desMd = tag.fromPrimitive(serializedData, null);
        assertEquals(machineIndex.getMachineType(loc), desMd.getMachineType(loc), "Test MachineIndex Serialization # " + reason);
    }

    @Test
    public void testChunkStorage() throws IOException {
        // register factory
        AstralFlow.getInstance().getFactories().register(DummyStatefulMachine.class, DummyStatefulMachine::new);
        var file = AstralFlow.getInstance().asPlugin().getDataFolder().toPath().resolve("test.index");
        Files.createFile(file);
        storage = new ChunkBasedMachineStorage(new MachineCache(file), AstralFlow.getInstance().getFactories(), MachineStorageType.JSON, 256, false);
        var machineManager = new MachineManagerImpl(storage, null, 16, true, new SimpleChunkTracker(256, true), AstralFlow.getInstance().getSecurityService().getLeakTracker());
        var random = ThreadLocalRandom.current();
        var randomLoc = new Location(Bukkit.getWorld("world"), random.nextInt(0, 3000), 1, random.nextInt(0, 3000)); //first quadrant
        saveAndTest(randomLoc, "first quad");
        randomLoc = new Location(Bukkit.getWorld("world"), random.nextInt(-3000, 0), 1, random.nextInt(0, 3000)); //second quadrant
        saveAndTest(randomLoc, "second quad");
        randomLoc = new Location(Bukkit.getWorld("world"), random.nextInt(-3000, 0), 1, random.nextInt(-3000, 0)); //third quadrant
        saveAndTest(randomLoc, "third quad");
        randomLoc = new Location(Bukkit.getWorld("world"), random.nextInt(0, 3000), 1, random.nextInt(-3000, 0)); //fourth quadrant
        saveAndTest(randomLoc, "fourth quad");
        randomLoc = new Location(Bukkit.getWorld("world"), 0, 1, 0); //zero point
        saveAndTest(randomLoc, "zero point");
        randomLoc = new Location(Bukkit.getWorld("world"), 2, 1, 0); //x-axis positive
        saveAndTest(randomLoc, "x-axis positive");
        randomLoc = new Location(Bukkit.getWorld("world"), -2, 1, 0); //x-axis negative
        saveAndTest(randomLoc, "x-axis negative");
        randomLoc = new Location(Bukkit.getWorld("world"), 0, 1, 2); //y-axis positive
        saveAndTest(randomLoc, "y-axis positive");
        randomLoc = new Location(Bukkit.getWorld("world"), 0, 1, -2); //y-axis negative
        saveAndTest(randomLoc, "y-axis negative");
    }

    private void saveAndTest(Location location, String phase) {
        storage.initChunk(location.getChunk());
        var machine = new DummyStatefulMachine(MachineProperty
                .builder()
                .uuid(UUID.randomUUID())
                .location(location)
                .build()
        );
        storage.save(location, machine);

        // read
        storage.finalizeChunk(location.getChunk(), true);

        storage.initChunk(location.getChunk());
        var readMachine = (DummyStatefulMachine) storage.get(location);
        assertNotNull(readMachine, phase);
        assertNotNull(readMachine.getState(), phase);
        assertEquals(readMachine.getState().get("nullcat?"), "sexy!", phase);
    }
}
