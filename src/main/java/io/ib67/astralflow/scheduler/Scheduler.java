/*
 *
 *   AstralFlow - The plugin who is turning bukkit into mod-pack
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

package io.ib67.astralflow.scheduler;

import io.ib67.astralflow.Tickable;
import org.jetbrains.annotations.ApiStatus;

/**
 * 任务调度器。
 * 每 tick 触发一次调度队列里的所有元素
 * Also see {@link Tickable}
 */
@ApiStatus.AvailableSince("0.1.0")
public interface Scheduler {

    /**
     * tick all things if their receipt allows.
     */
    @ApiStatus.Internal
    void tick();

    /**
     * Add a ticking target, throws {@link IllegalArgumentException} when you're attempting to add an object which already exists.
     * Always invoke tick( itself )
     *
     * @param tickable target.
     * @param <T>      tickType
     * @return Receipt. also see {@link TickReceipt}
     */
    <T extends Tickable<T>> TickReceipt<T> add(Tickable<T> tickable);

    /**
     * Remove from ticklist.
     *
     * @param tickable tickable object.
     */
    void remove(Tickable<?> tickable);
}
