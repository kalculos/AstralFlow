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

package io.ib67.astralflow.scheduler;

import io.ib67.astralflow.Tickable;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public final class AwaitingTickable<T extends Tickable<T>> {
    public final Tickable<T> tickable;
    public final TickReceipt<T> receipt;
    public final AtomicInteger exceptionCounter = new AtomicInteger(); // for SimpleCatchingScheduler.

    @SuppressWarnings("all")
    public void tick() throws Throwable {
        if (receipt.tick(tickable)) {
            tickable.update();
        }
    }

    @Deprecated
    @SneakyThrows
    public boolean tickAlsoClean() {
        if (receipt.isDropped()) {
            return true;
        }
        tick();
        return false;
    }
}
