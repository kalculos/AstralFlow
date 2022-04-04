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
import io.ib67.astralflow.api.events.MachineBlockBreakEvent;
import io.ib67.astralflow.api.events.PlayerInteractMachineEvent;
import io.ib67.astralflow.hook.HookType;
import io.ib67.astralflow.hook.event.machine.MachineBreakEvent;
import io.ib67.astralflow.machines.trait.Interactive;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public final class MachineListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST) // latest to know
    private void onInteract(PlayerInteractMachineEvent event) {
        if (event.getMachine() instanceof Interactive) {
            ((Interactive) event.getMachine()).onInteract(event.getClickType(), event.getPlayer(), event.getItemInHand());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onBreak(MachineBlockBreakEvent event) {
        var hookEvt = new MachineBreakEvent(event.getMachine(), event.getPlayer(), event.getBlock());
        event.setCancelled(AstralFlow.getInstance().callHooks(HookType.MACHINE_BREAK, hookEvt));
    }
}
