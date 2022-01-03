/*
 *
 *   AstralFlow - Storage utilities for spigot servers.
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

package io.ib67.astralflow.manager.impl;

import io.ib67.astralflow.machines.IMachine;
import io.ib67.astralflow.machines.IMachineData;
import io.ib67.astralflow.manager.IBlockItemManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Async compatible.
 */
public class BlockItemManagerImpl implements IBlockItemManager {
    private final Map<UUID, CachedMachine> cache = new ConcurrentHashMap<>(); // Intent to reduce redundant serializations

    @Override
    public ItemStack copyOf(IMachine machine) {
        ItemStack
    }

    @Override
    public boolean isBlockItem(ItemStack itemStack) {
        return false;
    }

    @Override
    public IMachine extractMachine(ItemStack itemStack, Location location) {
        return null;
    }

    @RequiredArgsConstructor
    @Getter
    private static class CachedMachine {
        private final IMachineData state;
        private final String type;
    }
}
