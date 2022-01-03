package astralflow.storage.machines;

import io.ib67.astralflow.machines.AbstractMachine;
import io.ib67.astralflow.machines.IMachine;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

public class HelloMachine extends AbstractMachine {
    private static final Material[] WOOL = Arrays.stream(Material.values()).filter(e -> e.name().endsWith("_WOOL")).collect(Collectors.toList()).toArray(new Material[0]);
    private volatile int ticks = 0;
    private int pointer = 0;

    public HelloMachine(UUID uuid, Location location) {
        super(uuid, location);
    }

    @Override
    public void update(IMachine self) {
        ticks++;
        if (ticks == 20 * 2) {
            ticks = 0;

            pointer++;
            if (pointer >= WOOL.length) {
                pointer = 0;
            }
            getLocation().getBlock().setType(WOOL[pointer]);
            getLocation().getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, getLocation(), 5);
        }
    }
}
