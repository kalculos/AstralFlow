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

import io.ib67.astralflow.AstralFlow;
import io.ib67.astralflow.api.events.MachineBlockBreakEvent;
import io.ib67.astralflow.api.events.PlayerInteractMachineEvent;
import io.ib67.astralflow.exception.ItemPrototypeNotFound;
import io.ib67.astralflow.machines.blockitem.MachineItemState;
import io.ib67.astralflow.machines.blockitem.trait.BlockItemSupport;
import io.ib67.astralflow.machines.trait.Interactive;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class MachineListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST) // latest to know
    private void onInteract(PlayerInteractMachineEvent event) {
        if (event.getMachine() instanceof Interactive) {
            ((Interactive) event.getMachine()).onInteract(event.getClickType(), event.getPlayer(), event.getItemInHand());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onBreak_blockItem(MachineBlockBreakEvent event) {
        if (event.getMachine() instanceof BlockItemSupport) {
            event.setDropItem(false);
            var protoId = ((BlockItemSupport) event.getMachine()).itemPrototypeId();
            var im = AstralFlow.getInstance().getItemRegistry();
            if (im.getRegistry(protoId) == null) {
                // wow.
                throw new ItemPrototypeNotFound(protoId); // won't drop anything.
            }
            var is = im.createItem(protoId);
            var state = is.getState().orElseThrow(IllegalStateException::new);
            if (state instanceof MachineItemState) {
                // deactivate machine.
                var machine = event.getMachine();
                var ms = ((MachineItemState) state);
                ms.setPrototype(machine.getType().getName());
                ms.setMachineState(machine.getState());
                AstralFlow.getInstance().getMachineManager().removeAndTerminateMachine(machine);
            } else {
                throw new UnsupportedOperationException("item state of " + protoId + " is not a MachineItemState.");
            }
            Bukkit.getScheduler().runTask(AstralFlow.getInstance().asPlugin(), () -> {
                event.getBlock().getLocation().getWorld().dropItemNaturally(event.getBlock().getLocation(), is.asItemStack());
            });
        }
    }

    //todo onPlace
}
