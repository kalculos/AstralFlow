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
import io.ib67.astralflow.api.AstralFlowAPI;
import io.ib67.astralflow.api.AstralHelper;
import io.ib67.astralflow.api.events.MachineBlockBreakEvent;
import io.ib67.astralflow.api.events.MachineBlockPlaceEvent;
import io.ib67.astralflow.api.events.PlayerInteractMachineEvent;
import io.ib67.astralflow.hook.HookType;
import io.ib67.astralflow.machines.IMachine;
import io.ib67.astralflow.machines.trait.Pushable;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Comparator;

@RequiredArgsConstructor
public final class BlockListener implements Listener {
    private final AstralFlowAPI flow;

    @EventHandler(ignoreCancelled = true)
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
                    new MachineBlockPlaceEvent(
                            event.getBlockPlaced(),
                            flow.getMachineManager().getAndLoadMachine(event.getBlockPlaced().getLocation()),
                            event.getPlayer()
                    )
            ));
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    // plugins like residence may cancell this event
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

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
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

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBurnt(BlockBurnEvent event) {
        var evt = new BlockBreakEvent(event.getBlock(), null);
        onBlockBreak(evt);
        if (evt.isCancelled()) {
            event.setCancelled(true);
        }
        flow.callHooks(HookType.BLOCK_BURNT, event);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockFade(BlockFadeEvent event) {
        var evt = new BlockBreakEvent(event.getBlock(), null);
        onBlockBreak(evt);
        if (evt.isCancelled()) {
            event.setCancelled(true);
        }
        flow.callHooks(HookType.BLOCK_FADE, event);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent event) {
        var it = event.blockList().iterator();
        while (it.hasNext()) {
            var block = it.next();
            var evt = new BlockBreakEvent(block, null);
            onBlockBreak(evt);
            if (evt.isCancelled()) {
                it.remove();
            }
        }
        flow.callHooks(HookType.BLOCK_EXPLODE, event);
    }

    // for slime blocks. Thanks to `Plugindustry/WheelCore` for their codes.
    private static Comparator<IMachine> getSuitableComparator(BlockFace direction) {
        if (direction.getModX() == 1)
            return Comparator.<IMachine>comparingDouble(e -> e.getLocation().getX()).reversed();
        else if (direction.getModX() == -1) return Comparator.comparingDouble(e -> e.getLocation().getX());
        else if (direction.getModY() == 1)
            return Comparator.<IMachine>comparingDouble(e -> e.getLocation().getY()).reversed();
        else if (direction.getModY() == -1) return Comparator.comparingDouble(e -> e.getLocation().getY());
        else if (direction.getModZ() == 1)
            return Comparator.<IMachine>comparingDouble(e -> e.getLocation().getZ()).reversed();
        else if (direction.getModZ() == -1) return Comparator.comparingDouble(e -> e.getLocation().getZ());
        else throw new IllegalArgumentException("Invalid direction");
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPistonPush(BlockPistonExtendEvent extendEvent) {
        extendEvent.setCancelled(onBlockMove(extendEvent.getBlocks(), extendEvent.getDirection()));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockDropItem(BlockDropItemEvent event) {
        event.setCancelled(AstralFlow.getInstance().callHooks(HookType.BLOCK_DROP_ITEM, event));

    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPistonPull(BlockPistonRetractEvent event) {
        event.setCancelled(onBlockMove(event.getBlocks(), event.getDirection()));
    }

    private boolean onBlockMove(Collection<? extends Block> movingBlocks, BlockFace direction) {
        var machines = movingBlocks.stream().filter(AstralHelper::hasMachine).map(AstralHelper::getMachine).toList();
        if (machines.isEmpty()) {
            return false;
        }
        if (!machines.stream().allMatch(it -> it instanceof Pushable)) {
            return true;
        } else {
            // all are pushable
            var comparator = getSuitableComparator(direction);
            machines.stream()
                    .sorted(comparator)
                    .filter(e -> e instanceof Pushable)
                    .map(e -> (Pushable) e)
                    .forEach(e -> handleMachineMove(e, ((IMachine) e).getLocation().clone().add(direction.getDirection()), direction.getDirection()));
            return false;
        }
    }

    @SneakyThrows
    private static void handleMachineMove(@NotNull Pushable mach, Location loc, Vector direction) {
        var machine = (IMachine) mach;
        var prevLoc = AstralHelper.purifyLocation(machine.getLocation().clone());
        mach.push(loc, direction);
        machine.getProperty().getManager().updateMachineLocation(prevLoc, loc, machine);
    }
}
