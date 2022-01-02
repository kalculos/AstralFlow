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
