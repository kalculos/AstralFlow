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

package io.ib67.astralflow.api.item.weapon;

import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.ApiStatus;

/**
 * Properties that defines a weapon. Build it by its builder method {@link WeaponProperty#builder()}.
 */
@ApiStatus.AvailableSince("0.1.0")
@Builder
@Getter
public final class WeaponProperty {
    /**
     * The direct damage
     */
    private final double damage;
    /**
     * Possibility of critical hit (0~1)
     */
    private final double criticalChance;
    /**
     * The critical damage multiplier
     */
    private final double criticalMultiplexer;
    /**
     * Should we override the original damage?
     */
    private final boolean clearOriginalDamage;
}
