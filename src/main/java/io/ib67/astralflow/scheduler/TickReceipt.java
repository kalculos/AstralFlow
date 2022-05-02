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
import io.ib67.astralflow.scheduler.exception.TickTaskException;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * A Receipt that used to control pre-tick behaviours.
 * Also see {@link Scheduler#add(Tickable)}
 *
 * @param <T> Tick Target
 */
@ApiStatus.AvailableSince("0.1.0")
public final class TickReceipt<T extends Tickable<T>> {
    private final List<AwaitingTickable<T>> syncs = new ArrayList<>(); //todo Flattening
    private final List<AwaitingTickable<T>> always = new ArrayList<>();
    private Predicate<T> requirement;
    private boolean dropped = false;
    private String name = null;

    /**
     * Also see {@link io.ib67.astralflow.scheduler.strategies.PeriodicTicks}
     *
     * @param consumer
     * @return
     */
    public TickReceipt<T> requires(Supplier<Predicate<T>> consumer) {
        Validate.notNull(consumer);
        return requires(consumer.get());
    }

    /**
     * Add a condition to call the real tickable.
     * A utility class for it: {@link io.ib67.astralflow.scheduler.strategies.PeriodicTicks}
     *
     * @param func
     * @return
     */
    public TickReceipt<T> requires(Predicate<T> func) {
        Validate.notNull(func);
        if (requirement != null) {
            requirement = requirement.and(func);
            return this;
        }
        this.requirement = func;
        return this;
    }

    /**
     * Runs a new tickable when the original tickable is called.
     * Contrary to {@link #alwaysTicks(Tickable)} ，It is depending on conditions coming from {@link #requires(Predicate)}
     *
     * @param tickable
     * @return the new tickable's receipt.
     */
    public TickReceipt<T> alsoTicks(Tickable<T> tickable) {
        Validate.notNull(tickable);
        var receipt = new TickReceipt<T>();
        syncs.add(new AwaitingTickable<>(tickable, receipt));
        return receipt;
    }

    /**
     * Calls a new tickable when the original tickable is called, even though the conditions are not passed.
     *
     * @param tickable
     * @return the new tickable's receipt
     */
    public TickReceipt<T> alwaysTicks(Tickable<T> tickable) {
        Validate.notNull(tickable);
        var receipt = new TickReceipt<T>();
        always.add(new AwaitingTickable<>(tickable, receipt));
        return receipt;
    }

    /**
     * Like {@link #alsoTicks(Tickable)}, but this method doesn't returns a new receipt, only returning itself.
     * Restricted by conditions that comes from {@link #requires(Predicate)}
     *
     * @param tickable
     * @return itself
     */
    public TickReceipt<T> syncWith(Tickable<T> tickable) {
        Validate.notNull(tickable);
        alsoTicks(tickable);
        return this;
    }

    /**
     * Set the name of receipt, for searching
     * Also see {@link io.ib67.astralflow.manager.ITickManager#getReceipt(String)}
     *
     * @param name receipt name
     * @return itself
     */
    public TickReceipt<T> name(String name) {
        Validate.notNull(name);
        this.name = name;
        return this;
    }

    /**
     * Mark this receipt is dropped and won't be called.
     * GC will clear this if there is no strong references left.
     * Also see {@link #isDropped()}
     */
    public void drop() {
        this.dropped = true;
    }

    /**
     * Is it dropped?
     *
     * @return
     */
    public boolean isDropped() {
        return this.dropped;
    }

    /**
     * name of the receipt
     *
     * @return
     */
    @Nullable
    public String name() {
        return name;
    }

    @SuppressWarnings("all")
    protected boolean tick(Object t) throws TickTaskException {
        if (always.size() != 0) {
            for (AwaitingTickable<?> alway : always) {
                alway.tick();
            }
        }
        if (requirement == null || requirement.test((T) t)) {
            if (syncs.size() != 0) {
                for (AwaitingTickable<?> sync : syncs) {
                    sync.tick();
                }
            }
            return true;
        } else {
            return false;
        }
    }
}
