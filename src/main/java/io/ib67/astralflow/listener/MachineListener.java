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

import io.ib67.astralflow.api.events.PlayerInteractMachineEvent;
import io.ib67.astralflow.machines.Interactive;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MachineListener implements Listener {
    @EventHandler
    private void onInteract(PlayerInteractMachineEvent event) {
        if (!event.isCancelled() && event.getMachine() instanceof Interactive) {
            ((Interactive) event.getMachine()).onInteract(event.getClickType(), event.getPlayer(), event.getItemInHand());
        }
    }
}
