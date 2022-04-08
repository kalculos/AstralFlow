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

package io.ib67.astralflow.internal.listener;

import io.ib67.astralflow.AstralFlow;
import io.ib67.astralflow.hook.HookType;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;

public final class ItemListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onItemConsume(PlayerItemConsumeEvent event) {
        event.setCancelled(AstralFlow.getInstance().callHooks(HookType.ITEM_CONSUME, event));
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemDamaged(PlayerItemDamageEvent event) {
        event.setCancelled(AstralFlow.getInstance().callHooks(HookType.ITEM_DAMAGE, event));
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteractBlock(PlayerInteractEvent event) {
        event.setCancelled(AstralFlow.getInstance().callHooks(HookType.PLAYER_INTERACT, event));
        if (event.getItem() == null || event.getItem().getType() == Material.AIR) {
            return;
        }
        if (event.getClickedBlock() == null || event.getClickedBlock().getType() == Material.AIR) {
            event.setCancelled(AstralFlow.getInstance().callHooks(HookType.ITEM_USE, event));
        }
        event.setCancelled(AstralFlow.getInstance().callHooks(HookType.PLAYER_INTERACT_BLOCK, event));
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteractEntity(PlayerInteractAtEntityEvent event) {
        if (event.getPlayer().getEquipment() == null) {
            return;
        }
        var item = event.getPlayer().getEquipment().getItemInMainHand();
        if (item.getType() == Material.AIR) {
            return;
        }
        event.setCancelled(AstralFlow.getInstance().callHooks(HookType.PLAYER_INTERACT_ENTITY, event));
    }
}
