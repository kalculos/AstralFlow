package astralflow.storage.listeners;

import astralflow.storage.machines.HelloMachine;
import io.ib67.astralflow.AstralFlow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockListener implements Listener {
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getItemInHand().hasItemMeta() && event.getItemInHand().getItemMeta().getDisplayName().equals("jeb_")) {
            var mech = AstralFlow.getInstance().getFactories().getMachineFactory(HelloMachine.class).createMachine(event.getBlock().getLocation());
            AstralFlow.getInstance().getMachineManager().registerMachine(mech);
            event.getPlayer().sendMessage("Magics will come...");
        }
    }
}
