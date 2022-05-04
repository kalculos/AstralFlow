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

package io.ib67.astralflow.machines;

import io.ib67.astralflow.manager.IMachineManager;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.jetbrains.annotations.ApiStatus;

import java.util.UUID;

/**
 * Represents a machine property, containing all fundamental information about a machine.
 */
@ApiStatus.AvailableSince("0.1.0")
@Builder
@Setter
@Getter
public final class MachineProperty {
    /**
     * The related machine manager.
     */
    private final IMachineManager manager;
    /**
     * The unique machine ID.
     */
    private final UUID uuid;
    /**
     * The machine's location.
     */
    private Location location;
    /**
     * The machine's data.
     */
    private IState state;
}
