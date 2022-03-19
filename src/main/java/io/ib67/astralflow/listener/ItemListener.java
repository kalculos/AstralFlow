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
import io.ib67.astralflow.hook.event.HookEvent;
import io.ib67.astralflow.hook.event.item.ItemConsumeEvent;
import io.ib67.astralflow.hook.event.item.ItemDamagedEvent;
import io.ib67.astralflow.item.AstralItem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;

import java.util.function.Consumer;

public class ItemListener implements Listener {
    @EventHandler
    public void onItemConsume(PlayerItemConsumeEvent event) {
        var evt = new ItemConsumeEvent(new AstralItem(event.getItem(), AstralFlow.getInstance().getItemRegistry()), event.getPlayer());
        for (Consumer<ItemConsumeEvent> hook : AstralFlow.getInstance().getHooks(HookType.ITEM_CONSUME)) {
            hook.accept(evt);
            if (evt.isCancelled()) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public <T extends HookEvent> void onItemDamaged(PlayerItemDamageEvent event) {
        var hookEvt = new ItemDamagedEvent(new AstralItem(event.getItem(), AstralFlow.getInstance().getItemRegistry()), event.getPlayer(), event.getDamage());
        for (Consumer<ItemDamagedEvent> hook : AstralFlow.getInstance().getHooks(HookType.ITEM_DAMAGE)) {
            hook.accept(hookEvt);
            if (hookEvt.isCancelled()) {
                event.setCancelled(true);
                return;
            }
        }
    }
    
}
