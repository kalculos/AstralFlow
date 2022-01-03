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

package astralflow.storage.machines;

import io.ib67.astralflow.machines.AbstractMachine;
import io.ib67.astralflow.machines.IMachine;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;

import java.util.Arrays;
import java.util.UUID;

public class HelloMachine extends AbstractMachine {
    private static final Material[] WOOL = Arrays.stream(Material.values()).filter(e -> e.name().endsWith("_WOOL")).toArray(Material[]::new);
    private int ticks = 0;
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
