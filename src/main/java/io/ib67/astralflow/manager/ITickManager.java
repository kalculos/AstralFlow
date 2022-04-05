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

package io.ib67.astralflow.manager;

import io.ib67.astralflow.Tickable;
import io.ib67.astralflow.scheduler.TickReceipt;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@ApiStatus.AvailableSince("0.1.0")
public interface ITickManager {

    <T extends Tickable<T>> TickReceipt<T> registerTickable(Tickable<T> tickable);

    /**
     * Add a tick receipt into manager, kept in weak reference.
     * Also see {@link TickReceipt}
     *
     * @param tickReceipt
     */
    void addReceipt(TickReceipt<?> tickReceipt);

    /**
     * Stream of receipts
     * Also see {@link TickReceipt}
     *
     * @return
     */
    Stream<? extends TickReceipt<?>> receiptStream();

    /**
     * May lead to {@link ClassCastException}.
     * Used for a situation that you knew name but not sure whether the receipt has been cleared by GC.
     * Also see {@link TickReceipt}
     *
     * @param name
     * @param typeOfT
     * @param <T>
     * @return
     */
    <T extends Tickable<T>> Optional<? extends TickReceipt<T>> getReceipt(String name, Class<T> typeOfT);

    /**
     * Find receipt exactly，May lead to {@link AssertionError} or {@link ClassCastException}
     * **Only use it if you knew what are you doing**
     * Also see {@link TickReceipt}
     *
     * @param name
     * @param typeOfT
     * @param <T>
     * @return
     */
    <T extends Tickable<T>> TickReceipt<T> getReceiptExactly(String name, Class<T> typeOfT);

    /**
     * Find receipt by name without any type cast.
     * Also see {@link TickReceipt}
     *
     * @param name
     * @return
     */
    Optional<? extends TickReceipt<?>> getReceipt(String name);

    /**
     * Find receipt by a prefix or regex.
     *
     * @param prefixOrRegex prefix or regex
     * @param isRegex       is the 1st arg a regex expr?
     * @return List
     */
    @NotNull
    List<? extends TickReceipt<?>> matchReceipt(String prefixOrRegex, boolean isRegex);
}
