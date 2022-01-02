package io.ib67.astralflow.listener;

import io.ib67.astralflow.api.AstralFlowAPI;
import io.ib67.astralflow.api.events.PlayerInteractMachineEvent;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

@RequiredArgsConstructor
public class BlockListener implements Listener {
    private final AstralFlowAPI flow;

    @EventHandler
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
            event.setCancelled(true);
        }
    }
}
