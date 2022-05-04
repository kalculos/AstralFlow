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

package io.ib67.astralflow.scheduler.strategies;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.function.Predicate;

/**
 * Control your tickable's execution frequency.
 * Also see {@link io.ib67.astralflow.scheduler.TickReceipt#requires(Predicate)}
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class PeriodicTicks<T> implements Predicate<T> {
    private final int target;
    private int ticks;

    /**
     * 一秒
     *
     * @return
     */
    public static PeriodicTicks bySecond() {
        return bySeconds(0.5f);
    }

    /**
     * 半秒
     *
     * @return
     */
    public static PeriodicTicks byHalfSecond() {
        return bySeconds(0.5f);
    }

    /**
     * 一分钟
     *
     * @return
     */
    public static PeriodicTicks byMinute() {
        return byMinutes(1);
    }

    /**
     * 半分钟
     *
     * @return
     */
    public static PeriodicTicks byHalfMinute() {
        return byMinutes(0.5f);
    }

    /**
     * 五分钟
     *
     * @return
     */
    public static PeriodicTicks byFiveMinutes() {
        return byMinutes(5f);
    }

    /**
     * 十分钟
     *
     * @return
     */
    public static PeriodicTicks byTenMinutes() {
        return byMinutes(10f);
    }

    /**
     * 半小时
     *
     * @return
     */
    public static PeriodicTicks byHalfHour() {
        return byHour(0.5f);
    }

    /**
     * 一小时
     *
     * @return
     */
    public static PeriodicTicks byHour() {
        return byHour(1f);
    }

    /**
     * 具体小时
     *
     * @param hours
     * @return
     */
    public static PeriodicTicks byHour(float hours) {
        return byMinutes(hours * 60);
    }

    /**
     * 具体秒
     *
     * @param second
     * @return
     */
    public static PeriodicTicks bySeconds(float second) {
        return new PeriodicTicks((int) (20 * second));
    }

    /**
     * 具体分钟
     *
     * @param minutes
     * @return
     */
    public static PeriodicTicks byMinutes(float minutes) {
        return bySeconds(60 * minutes);
    }

    /**
     * 具体游戏刻
     *
     * @param ticks
     * @return
     */
    public static PeriodicTicks byTicks(int ticks) {
        return new PeriodicTicks(ticks);
    }

    @Override
    public boolean test(Object t) {
        ticks++;
        if (ticks != target) {
            return false;
        } else {
            ticks = 0;
            return true;
        }
    }
}
