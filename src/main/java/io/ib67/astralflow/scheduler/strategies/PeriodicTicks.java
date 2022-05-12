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
     * one second
     *
     * @return
     */
    public static PeriodicTicks<?> bySecond() {
        return bySeconds(0.5f);
    }

    /**
     * half second
     *
     * @return
     */
    public static PeriodicTicks<?> byHalfSecond() {
        return bySeconds(0.5f);
    }

    /**
     * one minute
     *
     * @return
     */
    public static PeriodicTicks<?> byMinute() {
        return byMinutes(1);
    }

    /**
     * half minute
     *
     * @return
     */
    public static PeriodicTicks<?> byHalfMinute() {
        return byMinutes(0.5f);
    }

    /**
     * five minutes
     *
     * @return
     */
    public static PeriodicTicks<?> byFiveMinutes() {
        return byMinutes(5f);
    }

    /**
     * ten minutes
     *
     * @return
     */
    public static PeriodicTicks<?> byTenMinutes() {
        return byMinutes(10f);
    }

    /**
     * half hour
     *
     * @return
     */
    public static PeriodicTicks<?> byHalfHour() {
        return byHour(0.5f);
    }

    /**
     * one hour
     *
     * @return
     */
    public static PeriodicTicks<?> byHour() {
        return byHour(1f);
    }

    /**
     * X hours
     *
     * @param hours
     * @return
     */
    public static PeriodicTicks<?> byHour(float hours) {
        return byMinutes(hours * 60);
    }

    /**
     * X seconds
     *
     * @param second
     * @return
     */
    public static PeriodicTicks<?> bySeconds(float second) {
        return new PeriodicTicks((int) (20 * second));
    }

    /**
     * X minutes
     *
     * @param minutes
     * @return
     */
    public static PeriodicTicks<?> byMinutes(float minutes) {
        return bySeconds(60 * minutes);
    }

    /**
     * X ticks
     *
     * @param ticks
     * @return
     */
    public static PeriodicTicks<?> byTicks(int ticks) {
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
