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

package io.ib67.astralflow.listener;

import io.ib67.astralflow.AstralFlow;
import io.ib67.astralflow.hook.HookType;
import io.ib67.astralflow.hook.event.item.ItemConsumeEvent;
import io.ib67.astralflow.hook.event.item.ItemDamagedEvent;
import io.ib67.astralflow.hook.event.item.ItemInteractBlockEvent;
import io.ib67.astralflow.hook.event.item.ItemInteractEntityEvent;
import io.ib67.astralflow.item.AstralItem;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;

public class ItemListener implements Listener {
    @EventHandler
    public void onItemConsume(PlayerItemConsumeEvent event) {
        var evt = new ItemConsumeEvent(new AstralItem(event.getItem(), AstralFlow.getInstance().getItemRegistry()), event.getPlayer());
        event.setCancelled(AstralFlow.getInstance().callHooks(HookType.ITEM_CONSUME, evt));
    }

    @EventHandler
    public void onItemDamaged(PlayerItemDamageEvent event) {
        var hookEvt = new ItemDamagedEvent(new AstralItem(event.getItem(), AstralFlow.getInstance().getItemRegistry()), event.getPlayer(), event.getDamage());
        event.setCancelled(AstralFlow.getInstance().callHooks(HookType.ITEM_DAMAGE, hookEvt));
    }

    @EventHandler
    public void onInteractBlock(PlayerInteractEvent event) {
        if (event.getItem() == null || event.getItem().getType() == Material.AIR) {
            return;
        }
        if (event.getClickedBlock() == null || event.getClickedBlock().getType() == Material.AIR) {
            //todo itemUse
            return;
        }
        var item = new AstralItem(event.getItem(), AstralFlow.getInstance().getItemRegistry());
        var hookEvt = new ItemInteractBlockEvent(item, event.getPlayer(), event.getAction(), event.getClickedBlock(), event.getBlockFace());
        event.setCancelled(AstralFlow.getInstance().callHooks(HookType.ITEM_INTERACT_BLOCK, hookEvt));
    }

    @EventHandler
    public void onInteractEntity(PlayerInteractAtEntityEvent event) {
        if (event.getPlayer().getEquipment() == null) {
            return;
        }
        var item = event.getPlayer().getEquipment().getItemInMainHand();
        if (item.getType() == Material.AIR) {
            return;
        }
        var afItem = new AstralItem(item, AstralFlow.getInstance().getItemRegistry());
        var hookEvt = new ItemInteractEntityEvent(afItem, event.getPlayer(), event.getRightClicked());
        event.setCancelled(AstralFlow.getInstance().callHooks(HookType.ITEM_INTERACT_ENTITY, hookEvt));
    }
}
