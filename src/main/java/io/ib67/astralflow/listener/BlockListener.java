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

package io.ib67.astralflow.listener;

import io.ib67.astralflow.api.AstralFlowAPI;
import io.ib67.astralflow.api.events.MachineBlockBreakEvent;
import io.ib67.astralflow.api.events.PlayerInteractMachineEvent;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

@RequiredArgsConstructor
public class BlockListener implements Listener {
    private final AstralFlowAPI flow;

    @EventHandler(priority = EventPriority.HIGH) // plugins like resident may cancell this event
    public void onBlockInteraction(PlayerInteractEvent event) {
        if (!event.hasBlock()) {
            return;
        }
        var clickedBlock = event.getClickedBlock();
        if (flow.getMachineManager().isMachine(clickedBlock)) {
            var evt = PlayerInteractMachineEvent.builder()
                    .cancelled(false)
                    .itemInHand(event.getItem())
                    .clickType(event.getAction())
                    .machine(flow.getMachineManager().getMachine(event.getClickedBlock().getLocation()))
                    .player(event.getPlayer())
                    .build();
            Bukkit.getPluginManager().callEvent(evt);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        var clickedBlock = event.getBlock();
        if (flow.getMachineManager().isMachine(clickedBlock)) {
            var evt = MachineBlockBreakEvent.builder()
                    .cancelled(false)
                    .block(clickedBlock)
                    .player(event.getPlayer())
                    .machine(flow.getMachineManager().getMachine(clickedBlock.getLocation()))
                    .build();
            Bukkit.getPluginManager().callEvent(evt);
            if (evt.isCancelled()) {
                event.setCancelled(true);
            }
        }
    }
}
