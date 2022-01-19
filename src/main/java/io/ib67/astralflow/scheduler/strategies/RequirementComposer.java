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

package io.ib67.astralflow.scheduler.strategies;

import com.google.common.collect.Lists;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * 函数组合工具，适用于不使用 Function#compose/andThen 的场景（只在这一种情况才 andThen）
 * Also see {@link cc.sfclub.game.module.scheduler.TickReceipt#requires(Function)}
 *
 * @param <T>
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RequirementComposer<T> implements Function<T, Boolean> {
    private final List<Function<T, Boolean>> funcs;

    /**
     * 准备一次组装
     *
     * @param classOf 传参的 class
     * @param <T>     the type of ticking object
     * @return it
     */
    public static <T> RequirementComposer<T> of(Class<T> classOf) {
        return new RequirementComposer<>(new ArrayList<>());
    }

    /**
     * 用初始函数列准备一次组装
     *
     * @param classOf solution for the fucking type erasing
     * @param func    initial functions
     * @param <T>     the type of ticking object
     * @return it
     */
    public static <T> RequirementComposer<T> of(Class<T> classOf, Function<T, Boolean>... func) {
        Validate.notNull(func);
        return new RequirementComposer<>(Lists.newArrayList(func));
    }

    /**
     * Add into list.
     *
     * @param function func
     * @return it
     */
    public RequirementComposer<T> and(Function<T, Boolean> function) {
        Validate.notNull(function);
        funcs.add(function);
        return this;
    }

    @ApiStatus.Internal
    @Override
    public Boolean apply(T t) {
        return funcs.stream().allMatch(e -> e.apply(t));
    }
}
