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

/**
 * Here are some presets for the Item API. You can use these components to create some common items (such as ingredients) quickly.
 * <p/>
 * A usual case is to create a new item with dummy first, then set states for them in the future (but you will have to do some migration like {@link io.ib67.astralflow.manager.ItemRegistry#saveState(org.bukkit.inventory.ItemStack, io.ib67.astralflow.item.StateScope, io.ib67.astralflow.item.ItemState)})
 */
package io.ib67.astralflow.api.item;
