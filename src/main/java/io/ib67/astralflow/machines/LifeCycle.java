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

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.1.0")
public interface LifeCycle {
    /**
     * Calls when the chunk is loaded and your machine is ready to initialize.
     * You can get {@link io.ib67.astralflow.scheduler.TickReceipt} by calling {@link io.ib67.astralflow.manager.IMachineManager#getReceiptByMachine(IMachine)}.
     */
    default void onLoad() {
    }

    /**
     * Called when chunk is unloading or machine is deactivated. Save your data into state here.
     */
    default void onUnload() {
    }
}
