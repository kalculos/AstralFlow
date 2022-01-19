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

package io.ib67.astralflow.item;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Item 所有的动作都通过 Hook 系统实现。
 * 如果需要添加功能可以委托到现有的实现类中
 * <p>
 * 身兼二职: 工厂 & Item
 */
public interface Item {

    /**
     * 返回物品原型供矿物辞典和物品创建使用。
     *
     * @return
     */
    @NotNull
    ItemStack getPrototype();

    @Nullable // stateless item
    ItemState getStatePrototype();

    /**
     * 物品的标识 ID。
     * 虽然不强制，但是请尽量使用 `命名空间:命名` 的做法来取ID.
     *
     * @return
     */
    String getId();
}
