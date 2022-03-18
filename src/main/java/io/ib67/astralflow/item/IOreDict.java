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

package io.ib67.astralflow.item;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.function.Predicate;

@ApiStatus.AvailableSince("0.1.0")
public interface IOreDict {
    /**
     * 插件完成初始化后将会封锁所有的 registerItem 请求以减小维护成本
     *
     * @param prototype
     * @param dictKey
     * @return
     * @throws IllegalStateException if locked
     */
    IOreDict registerItem(String dictKey, ItemStack prototype, Predicate<ItemStack> tester);

    boolean matchItem(String oredictId, ItemStack itemStack);

    /**
     * This method will return a collection of all the registered items. Only for being shown to player
     *
     * @param oredictId
     * @return
     */
    Collection<? extends ItemStack> getItems(String oredictId);

}
