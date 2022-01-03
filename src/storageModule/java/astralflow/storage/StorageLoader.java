package astralflow.storage;

import astralflow.storage.listeners.BlockListener;
import astralflow.storage.machines.HelloMachine;
import io.ib67.astralflow.AstralFlow;
import io.ib67.astralflow.api.factories.StatelessMachineFactory;
import io.ib67.util.bukkit.Log;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public class StorageLoader {
    {
        Log.info("Loading &aStorage Module");
        AstralFlow.getInstance().getFactories().register(HelloMachine.class, new StatelessMachineFactory<>((location, uuid) -> {
            if (uuid == null) { // create a new machine.
                return new HelloMachine(UUID.randomUUID(), location);
            } else {
                return new HelloMachine(uuid, location);
            }
        }));
        Bukkit.getPluginManager().registerEvents(new BlockListener(), (Plugin) AstralFlow.getInstance());
    }
}
