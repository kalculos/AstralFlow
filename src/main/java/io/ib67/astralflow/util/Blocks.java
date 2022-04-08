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

package io.ib67.astralflow.util;

import lombok.experimental.UtilityClass;
import org.bukkit.Material;

import static java.util.Objects.requireNonNull;

/**
 * Some utility classes that are used for {@link org.bukkit.block.Block}s
 */
@UtilityClass
public class Blocks {
    /**
     * Check if a block is physical, meaning it can move itself(like gravity) or being broken by state changes (like TNT)
     *
     * @return
     */
    public static boolean isNonPhysical(Material material) {
        requireNonNull(material);
        if (material.isAir() || !material.isBlock() || !material.isSolid()) {
            return false;
        }
        return switch (material) {
            case SAND, GRAVEL:
            case ACACIA_LEAVES, BIRCH_LEAVES, DARK_OAK_LEAVES, JUNGLE_LEAVES, OAK_LEAVES, SPRUCE_LEAVES, AZALEA_LEAVES, FLOWERING_AZALEA_LEAVES:
            case AZALEA:
            case TNT:
                yield false;
            default:
                yield true;
        };
    }
}
