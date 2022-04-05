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

package io.ib67.astralflow.item;

import io.ib67.astralflow.item.oredict.internal.VanillaOreDict;
import io.ib67.astralflow.test.TestUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public final class OreDictTest {
    @BeforeAll
    public void setup() {
        TestUtil.init();
    }

    @Test
    public void onTestVanillaOreDict() {
        var oreDict = new VanillaOreDict();
        Assertions.assertThrows(UnsupportedOperationException.class, () -> oreDict.registerItem(null, null, null));
        assertTrue(oreDict.matchItem("wool", new ItemStack(Material.WHITE_WOOL)));
        assertTrue(oreDict.matchItem("ingot", new ItemStack(Material.DIAMOND)));
    }
}
