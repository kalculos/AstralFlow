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

package io.ib67.astralflow.item.machine;


import be.seeseemelk.mockbukkit.MockBukkit;
import io.ib67.astralflow.api.AstralHelper;
import io.ib67.astralflow.api.item.machine.MachineItem;
import io.ib67.astralflow.hook.HookType;
import io.ib67.astralflow.item.ItemKey;
import io.ib67.astralflow.item.ItemKeys;
import io.ib67.astralflow.test.TestUtil;
import io.ib67.astralflow.util.ItemStacks;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MachineItemTest {
    private static final ItemKey TEST_MACHINE_ITEM = ItemKeys.from("testitem:test_machine_item");
    private ItemKey machineItem;
    private World anyWorld;
    private Block block;

    @BeforeAll
    public void setup() {
        TestUtil.init();
        machineItem = MachineItem.registerItem(TEST_MACHINE_ITEM,
                ItemStacks.builder(Material.WHITE_WOOL)
                        .build(),
                SimpleStatelessMachine.class
        );
        anyWorld = MockBukkit.getMock().addSimpleWorld("testmi");
    }

    @Test
    @Order(1)
    public void testMachineSetup() {
        block = anyWorld.getBlockAt(0, 0, 0);
        var exampleItem = machineItem.createNewItem();
        assertTrue(AstralHelper.isItem(exampleItem.asItemStack()), "Test Item Allocation");
        var player = MockBukkit.getMock().addPlayer();
        var blockPlaceEvent = new BlockPlaceEvent(
                block,
                null,
                block,
                exampleItem.asItemStack(),
                player,
                false,
                null
        );
        Bukkit.getPluginManager().callEvent(blockPlaceEvent);
        assertTrue(AstralHelper.hasMachine(block), "Test Block Placement");
    }

    @Test
    @Order(2)
    public void testMachineBreak() {
        AtomicReference<List<Item>> results = new AtomicReference<>();
        HookType.BLOCK_DROP_ITEM.register(bdie -> {
            results.set(bdie.getItems());
        });
        block.breakNaturally();
        var bbe = new BlockBreakEvent(block, MockBukkit.getMock().addPlayer());
        Bukkit.getPluginManager().callEvent(bbe);
        assertTrue(results.get().size() > 0, "Size is bigger than zero");
        var item = results.get().get(0).getItemStack();
        assertTrue(AstralHelper.isItem(item), "The first one is a machine item");
        assertFalse(AstralHelper.hasMachine(block), "Block is removed.");

    }
}
