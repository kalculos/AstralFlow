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

package io.ib67.astralflow.item.builder;

import io.ib67.astralflow.item.ItemState;
import io.ib67.astralflow.item.factory.ItemPrototypeFactory;
import lombok.Builder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.UnaryOperator;

@Builder
class PrototypeDecorator implements ItemPrototypeFactory {
    private final ItemPrototypeFactory registry;
    @Builder.Default
    private final UnaryOperator<ItemStack> itemMapper = UnaryOperator.identity();
    @Builder.Default
    private final UnaryOperator<ItemState> stateMapper = UnaryOperator.identity();

    @Override
    public @NotNull ItemStack getPrototype() {
        return itemMapper.apply(registry.getPrototype());
    }

    @Override
    public @Nullable ItemState getStatePrototype() {
        return stateMapper.apply(registry.getStatePrototype());
    }

    @Override
    public String getId() {
        return registry.getId();
    }
}
