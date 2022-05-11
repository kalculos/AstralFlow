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

package io.ib67.astralflow.item.recipe;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;

/**
 * This class represents a matrix of items.
 */
@Getter
@ApiStatus.AvailableSince("0.1.0")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemMatrix {
    private final ItemStack[] matrix;
    private final RecipeType type;

    /**
     * Do not use it unless you know what are you doing
     *
     * @param itemStacks item matrix
     * @return matrix
     */
    @Deprecated
    @ApiStatus.Internal
    public static ItemMatrix createRawMatrix(RecipeType type, ItemStack... itemStacks) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(itemStacks, "itemStacks");
        return new ItemMatrix(itemStacks, type);
    }

    public static ItemMatrix of(CraftingInventory inventory) {
        Objects.requireNonNull(inventory, "inventory");
        return new ItemMatrix(inventory.getMatrix(), RecipeType.CRAFTING);
    }

    public static ItemMatrix of(FurnaceInventory inventory) {
        Objects.requireNonNull(inventory, "inventory");
        return new ItemMatrix(new ItemStack[]{
                inventory.getSmelting(),
                inventory.getFuel()
        }, RecipeType.SMELTING);
    }

    public static ItemMatrix of(BrewerInventory inventory) {
        Objects.requireNonNull(inventory, "inventory");
        return new ItemMatrix(
                new ItemStack[]{
                        inventory.getIngredient(),
                        inventory.getItem(0),
                        inventory.getItem(1),
                        inventory.getItem(2)
                },
                RecipeType.BREWING
        );
    }
}
