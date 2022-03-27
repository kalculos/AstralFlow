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
import io.ib67.astralflow.api.AstralHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SyncScheduler implements Scheduler {
    private final List<AwaitingTickable<?>> tickTargets = new ArrayList<>();

    @Override
    public void tick() {
        AstralHelper.ensureMainThread("Concurrent Modification to ArrayList");
        tickTargets.removeIf(AwaitingTickable::tickAlsoClean);
    }

    @Override
    public <T extends Tickable<T>> TickReceipt<T> add(Tickable<T> tickable) throws IllegalArgumentException {
        AstralHelper.ensureMainThread("Concurrent Modification to ArrayList");
        Objects.requireNonNull(tickable, "Tickable cannot be null");
        if (tickTargets.stream().anyMatch(e -> e.tickable == tickable)) {
            throw new IllegalArgumentException(tickable.toString() + " is already in ticking.");
        }
        var receipt = new TickReceipt<T>();
        var awaitTickable = new AwaitingTickable<>(tickable, receipt);
        tickTargets.add(awaitTickable);
        return receipt;
    }

    @Override
    public void remove(Tickable<?> tickable) {
        tickTargets.removeIf(e -> e.tickable == tickable);
    }
}
