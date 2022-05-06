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
import io.ib67.astralflow.item.ItemKey;
import io.ib67.astralflow.item.ItemKeys;
import io.ib67.astralflow.test.TestUtil;
import io.ib67.astralflow.util.ItemStacks;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.block.BlockPlaceEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MachineItemTest {
    private static final ItemKey TEST_MACHINE_ITEM = ItemKeys.from("testitem:test_machine_item");
    private ItemKey machineItem;
    private World anyWorld;

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
    public void testMachineSetup() {
        var block = anyWorld.getBlockAt(0, 0, 0);
        var exampleItem = machineItem.createNewItem();
        Assertions.assertTrue(AstralHelper.isItem(exampleItem.asItemStack()), "Test Item Allocation");
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
        Assertions.assertTrue(AstralHelper.hasMachine(block), "Test Block Placement");
    }
}
