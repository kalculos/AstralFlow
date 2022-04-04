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

package io.ib67.astralflow.listener;

import io.ib67.astralflow.AstralFlow;
import io.ib67.astralflow.api.AstralFlowAPI;
import io.ib67.astralflow.api.AstralHelper;
import io.ib67.astralflow.api.events.MachineBlockBreakEvent;
import io.ib67.astralflow.api.events.PlayerInteractMachineEvent;
import io.ib67.astralflow.hook.HookType;
import io.ib67.astralflow.hook.event.machine.MachinePlaceEvent;
import io.ib67.astralflow.machines.IMachine;
import io.ib67.astralflow.machines.trait.Pushable;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.Comparator;

@RequiredArgsConstructor
public final class BlockListener implements Listener {
    private final AstralFlowAPI flow;

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        event.setCancelled(
                flow.callHooks(
                        HookType.BLOCK_PLACE,
                        event
                )
        );

        if (flow.getMachineManager().isMachine(event.getBlockPlaced()) && !event.isCancelled()) {
            event.setCancelled(flow.callHooks(
                    HookType.MACHINE_PLACE,
                    new MachinePlaceEvent(flow.getMachineManager().getAndLoadMachine(event.getBlockPlaced().getLocation()), event.getBlockPlaced().getLocation(), event.getPlayer())
            ));
        }
    }

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
                    .machine(flow.getMachineManager().getAndLoadMachine(event.getClickedBlock().getLocation()))
                    .player(event.getPlayer())
                    .build();
            Bukkit.getPluginManager().callEvent(evt);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        var clickedBlock = event.getBlock();
        if (flow.getMachineManager().isMachine(clickedBlock)) {
            var machine = flow.getMachineManager().getAndLoadMachine(clickedBlock.getLocation());
            var evt = MachineBlockBreakEvent.builder()
                    .cancelled(false)
                    .dropItem(false)
                    .block(clickedBlock)
                    .player(event.getPlayer())
                    .machine(machine)
                    .build();
            Bukkit.getPluginManager().callEvent(evt);
            if (evt.isCancelled()) {
                event.setCancelled(true);
            } else {
                AstralFlow.getInstance().getMachineManager().terminateAndRemoveMachine(machine);
            }
            event.setDropItems(evt.isDropItem());
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPistonPush(BlockPistonExtendEvent extendEvent) {
        extendEvent.setCancelled(onBlockMove(extendEvent.getBlock(), extendEvent.getBlocks(), extendEvent.getDirection().getDirection(), false));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPistonPull(BlockPistonRetractEvent event) {
        event.setCancelled(onBlockMove(event.getBlock(), event.getBlocks(), event.getDirection().getDirection(), true));
    }

    private boolean onBlockMove(Block piston, Collection<? extends Block> movingBlocks, Vector direction, boolean pulling) {
        var machines = movingBlocks.stream().filter(AstralHelper::hasMachine).map(AstralHelper::getMachine).toList();
        if (machines.isEmpty()) {
            return false;
        }
        if (!machines.stream().allMatch(it -> it instanceof Pushable)) {
            return true;
        } else {
            // all are pushable
            var comparator = Comparator.<IMachine>comparingDouble(it -> it.getLocation().distanceSquared(piston.getLocation()));
            if (pulling) comparator = comparator.reversed();
            machines.stream()
                    .sorted(comparator)
                    .map(e -> (Pushable) e)
                    .forEach(e -> e.push(((IMachine) e).getLocation().add(direction), direction));
            return false;
        }
    }
}
