package io.ib67.astralflow.scheduler;

import io.ib67.astralflow.machines.IMachine;
import io.ib67.astralflow.manager.IMachineManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public class TickScheduler extends BukkitRunnable {
    private final IMachineManager machineManager;

    @Override
    public void run() {
        machineManager.getLoadedMachines().stream().filter(IMachine::isActivated).forEach(IMachine::update);
    }
}
