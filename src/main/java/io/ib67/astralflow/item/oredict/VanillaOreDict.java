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

package io.ib67.astralflow.item.oredict;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class VanillaOreDict implements IOreDict {
    private static final List<Material> WOOLS = Arrays.stream(Material.values())
            .filter(e -> e.name().endsWith("_WOOL"))
            .toList();
    private static final List<Material> SAPLINGS = Arrays.stream(Material.values())
            .filter(e -> e.name().endsWith("_SAPLING"))
            .toList();
    private static final List<Material> WOODS = Arrays.stream(Material.values())
            .filter(e -> e.name().endsWith("_WOOD") || e.name().endsWith("_LOG"))
            .toList();
    private static final List<Material> LEAVES = Arrays.stream(Material.values())
            .filter(e -> e.name().endsWith("_LEAVES"))
            .toList();
    private static final List<Material> TERRACOTTA = Arrays.stream(Material.values())
            .filter(e -> e.name().endsWith("_TERRACOTTA"))
            .toList();
    private static final List<Material> CARPETS = Arrays.stream(Material.values())
            .filter(e -> e.name().endsWith("_CARPET"))
            .toList();
    private static final List<Material> GLASSES = Arrays.stream(Material.values())
            .filter(e -> e.name().endsWith("GLASS"))
            .toList();
    private static final List<Material> GLASSES_PANE = Arrays.stream(Material.values())
            .filter(e -> e.name().endsWith("GLASS_PANE"))
            .toList();

    private static final List<Material> SHULKER_BOX = Arrays.stream(Material.values())
            .filter(e -> e.name().endsWith("SHULKER_BOX"))
            .toList();


    @Override
    public IOreDict registerItem(String dictKey, ItemStack prototype, Predicate<ItemStack> tester) {
        throw new UnsupportedOperationException("Vanilla ore dict does not support registration");
    }

    @Override
    public boolean matchItem(String oredictId, ItemStack itemStack) {
        return matchMaterials(oredictId).contains(itemStack.getType());
    }

    // TODO: Documentation.
    private List<Material> matchMaterials(String oreDictId) {
        return switch (oreDictId) {
            case "oreIron":
                yield List.of(Material.IRON_ORE);
            case "oreGold":
                yield List.of(Material.GOLD_ORE);
            case "oreCoal":
                yield List.of(Material.COAL_ORE);
            case "oreLapis":
                yield List.of(Material.LAPIS_ORE);
            case "oreRedstone":
                yield List.of(Material.REDSTONE_ORE);
            case "oreDiamond":
                yield List.of(Material.DIAMOND_ORE);
            case "oreEmerald":
                yield List.of(Material.EMERALD_ORE);
            case "oreQuartz":
                yield List.of(Material.NETHER_QUARTZ_ORE);
            case "oreCopper":
                yield List.of(Material.COPPER_ORE);

            case "ingotIron":
                yield List.of(Material.IRON_INGOT);
            case "ingotGold":
                yield List.of(Material.GOLD_INGOT);
            case "ingotCopper":
                yield List.of(Material.COPPER_INGOT);
            case "ingotNether":
                yield List.of(Material.NETHERITE_INGOT);

            case "wool":
                yield WOOLS;
            case "stone":
                yield List.of(Material.STONE, Material.ANDESITE, Material.DIORITE, Material.GRANITE, Material.POLISHED_ANDESITE, Material.POLISHED_DIORITE, Material.POLISHED_GRANITE);
            case "sapling":
                yield SAPLINGS;
            case "log":
            case "wood":
                yield WOODS;
            case "leaf":
                yield LEAVES;
            case "plank":
                yield List.of(Material.OAK_PLANKS, Material.SPRUCE_PLANKS, Material.BIRCH_PLANKS, Material.JUNGLE_PLANKS, Material.ACACIA_PLANKS, Material.DARK_OAK_PLANKS);
            case "flower":
                yield List.of(Material.DANDELION, Material.POPPY, Material.BLUE_ORCHID, Material.ALLIUM, Material.AZURE_BLUET, Material.RED_TULIP, Material.ORANGE_TULIP, Material.WHITE_TULIP, Material.PINK_TULIP, Material.OXEYE_DAISY);
            case "terracotta":
                yield TERRACOTTA;
            case "carpet":
                yield CARPETS;
            case "glass":
                yield GLASSES;
            case "glassPane":
                yield GLASSES_PANE;
            case "chest":
                yield List.of(Material.CHEST, Material.TRAPPED_CHEST, Material.ENDER_CHEST);
            case "box":
                yield SHULKER_BOX;
            case "ore":
                yield List.of(Material.IRON_ORE, Material.GOLD_ORE, Material.COAL_ORE, Material.LAPIS_ORE, Material.REDSTONE_ORE, Material.DIAMOND_ORE, Material.EMERALD_ORE, Material.NETHER_QUARTZ_ORE, Material.COPPER_ORE, Material.NETHERITE_INGOT);
            case "ingot":
                yield List.of(Material.IRON_INGOT, Material.GOLD_INGOT, Material.COPPER_INGOT, Material.DIAMOND, Material.NETHERITE_INGOT, Material.EMERALD);
            default:
                var result = Material.getMaterial(oreDictId);
                if (result != null) {
                    yield List.of(result);
                }
                yield Collections.emptyList();
        };
    }

    @Override
    public Collection<? extends ItemStack> getItems(String oredictId) {
        return matchMaterials(oredictId).stream().map(ItemStack::new).toList();
    }
}
