/*
 *
 *   AstralFlow - The plugin who is turning bukkit into mod-pack
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

package io.ib67.astralflow.machines;

import io.ib67.astralflow.machines.trait.Pushable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

import java.util.UUID;

public class JebWool extends AbstractMachine implements Pushable {
    private static final Material[] WOOLS = new Material[]{
            Material.BLUE_WOOL,
            Material.GREEN_WOOL,
            Material.RED_WOOL,
            Material.YELLOW_WOOL,
            Material.WHITE_WOOL,
            Material.BLACK_WOOL
    };
    private int counter;

    public JebWool(UUID id, Location location) {
        super(id, location);
    }

    @Override
    public void onLoad() {
        getLocation().getBlock().setType(Material.OBSIDIAN);
    }

    @Override
    public void update(IMachine self) {
        counter++;
        if (counter % 2 == 0) {
            getLocation().getBlock().setType(WOOLS[counter % WOOLS.length]);
        }
    }

    @Override
    public void push(Location newLocation, BlockFace direction) {
        super.setLocation(newLocation);
    }
}
