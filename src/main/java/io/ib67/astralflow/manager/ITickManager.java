/*
 *
 *   AstralFlow - Storage utilities for spigot servers.
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

package io.ib67.astralflow.manager;

import io.ib67.astralflow.Tickable;
import io.ib67.astralflow.scheduler.TickReceipt;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface ITickManager {
    /**
     * 添加一个 Tick 回执，弱引用储存，请自行注意GC
     * Also see {@link TickReceipt}
     *
     * @param tickReceipt
     */
    void addReceipt(TickReceipt<?> tickReceipt);

    /**
     * 回执流
     * Also see {@link TickReceipt}
     *
     * @return
     */
    Stream<WeakReference<TickReceipt<?>>> receiptStream();

    /**
     * 可能导致 {@link ClassCastException}.
     * 适用于你知道名字但是不确定他是否被回收的情况。
     * Also see {@link TickReceipt}
     *
     * @param name
     * @param typeOfT
     * @param <T>
     * @return
     */
    <T extends Tickable<T>> Optional<? extends TickReceipt<T>> getReceipt(String name, Class<T> typeOfT);

    /**
     * 精确查找并直接返回结果，可能导致 {@link AssertionError} 或 {@link ClassCastException}
     * **只在你完全清楚情况的情况下使用他**
     * Also see {@link TickReceipt}
     *
     * @param name
     * @param typeOfT
     * @param <T>
     * @return
     */
    <T extends Tickable<T>> TickReceipt<T> getReceiptExactly(String name, Class<T> typeOfT);

    /**
     * 通过名字查找回执，没有类型转型也不确保能找到。
     * Also see {@link TickReceipt}
     *
     * @param name
     * @return
     */
    Optional<? extends TickReceipt<?>> getReceipt(String name);

    /**
     * 根据前缀或者正则匹配一批回执
     *
     * @param prefixOrRegex 前缀或者正则
     * @param isRegex       是否是正则
     * @return 可能为空的list
     */
    List<? extends TickReceipt<?>> matchReceipt(String prefixOrRegex, boolean isRegex);
}
