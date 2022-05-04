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

package io.ib67.astralflow.api.item.dummy.fetch;

import io.ib67.astralflow.api.item.dummy.FetchMethod;
import io.ib67.astralflow.hook.HookType;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * A way to fetch {@link io.ib67.astralflow.api.item.dummy.DummyItem}s.<br />
 * This way listens on breaking a block.
 */
@ApiStatus.AvailableSince("0.1.0")
public final class BreakBlock implements FetchMethod {
    private final Predicate<Block> blockPredicator;
    private Supplier<ItemStack> producer;

    public BreakBlock(Predicate<Block> blockPredicator) {
        this.blockPredicator = blockPredicator;
    }

    @Override
    public void init(Supplier<ItemStack> producer) {
        this.producer = producer;
        HookType.BLOCK_BREAK.register(this::onBlockBreak);
    }

    private void onBlockBreak(BlockBreakEvent event) {
        if (producer == null) {
            throw new IllegalStateException("Producer is null");
        }
        if (blockPredicator.test(event.getBlock())) {
            var loc = event.getBlock().getLocation();
            loc.getWorld().dropItemNaturally(loc, producer.get());
        }
    }
}
