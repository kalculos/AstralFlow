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

import io.ib67.astralflow.Tickable;
import org.bukkit.Location;
import org.jetbrains.annotations.ApiStatus;

import java.util.UUID;

@ApiStatus.AvailableSince("0.1.0")
public interface IMachine extends Tickable<IMachine>, LifeCycle {

    MachineProperty getProperty();

    default UUID getId() {
        return getProperty().getUuid();
    }

    default Location getLocation() {
        return getProperty().getLocation();
    }

    default IState getState() {
        return getProperty().getState();
    }

    default void update() {
        this.update(this);
    }

    default Class<? extends IMachine> getType() {
        return this.getClass();
    }
}
