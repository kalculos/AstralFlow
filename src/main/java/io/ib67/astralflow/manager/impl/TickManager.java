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

package io.ib67.astralflow.manager.impl;

import io.ib67.astralflow.AstralFlow;
import io.ib67.astralflow.Tickable;
import io.ib67.astralflow.manager.ITickManager;
import io.ib67.astralflow.scheduler.Scheduler;
import io.ib67.astralflow.scheduler.SchedulerAdapter;
import io.ib67.astralflow.scheduler.SyncScheduler;
import io.ib67.astralflow.scheduler.TickReceipt;
import lombok.Getter;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.ApiStatus;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Tick 管理器，所有对于他的操作都应该是同步的。
 * 每一个 Tick Manager 均会运行一个新的 Scheduler，请避免创建 Tick Manager。
 */
@ApiStatus.AvailableSince("0.1.0")
public final class TickManager implements ITickManager {
    private final SchedulerAdapter adapter;
    private final ReferenceQueue<TickReceipt<?>> refTrashQueue = new ReferenceQueue<>();
    private final List<WeakReference<TickReceipt<?>>> receipts = new ArrayList<>();

    /**
     * 获取到Scheduler/调度器对象
     */
    @Getter
    private final Scheduler scheduler;

    public TickManager() {
        this.scheduler = new SyncScheduler();
        this.adapter = new SchedulerAdapter(scheduler);
        Bukkit.getScheduler().runTaskLater(AstralFlow.getInstance().asPlugin(), () -> {
            adapter.runTaskTimer(AstralFlow.getInstance().asPlugin(), 0L, 1L);
        }, 1L);
        Bukkit.getScheduler().runTaskTimer(AstralFlow.getInstance().asPlugin(), () -> {
            // Remove trashes.
            var ref = refTrashQueue.poll();
            while (ref != null) {
                Reference<? extends TickReceipt<?>> finalRef = ref;
                receipts.removeIf(i -> i.get() == finalRef.get());
                ref = refTrashQueue.poll();
            }
        }, 0L, 300 * 20L);
    }

    @Override
    public <T extends Tickable<T>> TickReceipt<T> registerTickable(Tickable<T> tickable) {
        return scheduler.add(tickable);
    }

    /**
     * 添加一个 Tick 回执，弱引用储存，请自行注意GC
     * Also see {@link TickReceipt}
     *
     * @param tickReceipt
     */
    public void addReceipt(TickReceipt<?> tickReceipt) {
        receipts.add(new WeakReference<>(tickReceipt));
    }

    /**
     * 回执流
     * Also see {@link TickReceipt}
     *
     * @return
     */
    public Stream<? extends TickReceipt<?>> receiptStream() {
        return receipts.stream().map(e -> e.get()).filter(Objects::nonNull).filter(TickReceipt::isDropped);

    }

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
    @SuppressWarnings("all")
    public <T extends Tickable<T>> Optional<? extends TickReceipt<T>> getReceipt(String name, Class<T> typeOfT) {
        Validate.notNull(name);
        Validate.notNull(typeOfT);
        return (Optional<? extends TickReceipt<T>>) getReceipt(name);
    }

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
    @SuppressWarnings("all")
    public <T extends Tickable<T>> TickReceipt<T> getReceiptExactly(String name, Class<T> typeOfT) {
        Validate.notNull(name);
        Validate.notNull(typeOfT);
        return getReceipt(name, typeOfT).orElseThrow(AssertionError::new);
    }

    /**
     * 通过名字查找回执，没有类型转型也不确保能找到。
     * Also see {@link TickReceipt}
     *
     * @param name
     * @return
     */
    public Optional<? extends TickReceipt<?>> getReceipt(String name) {
        Validate.notNull(name);
        return receipts.stream().map(Reference::get).filter(e -> name.equals(e.name()) && !e.isDropped()).findFirst();
    }

    /**
     * 根据前缀或者正则匹配一批回执
     *
     * @param prefixOrRegex 前缀或者正则
     * @param isRegex       是否是正则
     * @return 可能为空的list
     */
    public List<? extends TickReceipt<?>> matchReceipt(String prefixOrRegex, boolean isRegex) {
        Validate.notNull(prefixOrRegex);
        return receipts.stream().map(Reference::get).filter(e -> e.name() != null && isRegex ? e.name().matches(prefixOrRegex) : e.name().startsWith(prefixOrRegex)).collect(Collectors.toList());
    }
}
