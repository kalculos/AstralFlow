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

package io.ib67.astralflow.hook.event.block;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * @author EvanLuo42
 * @date 3/20/22 12:48 PM
 */
public class BlockMoveEvent extends BlockEvent {
    @Getter
    private final Location fromLocation;

    private final Location toDirection;

    @Getter
    private final BlockMoveReason reason;

    public BlockMoveEvent(Block block, Location fromLocation, Player player, Location direction, BlockMoveReason reason) {
        super(block, player);
        this.fromLocation = fromLocation;
        this.toDirection = direction;
        this.reason = reason;
    }

    public Vector getToDirection() {
        return toDirection.toVector().subtract(fromLocation.toVector());
    }
}
