/*
 *
 *   AstralFlow - The plugin enriches bukkit servers
 *   Copyright (C) 2022 The Inlined Lambdas and Contributors
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

import io.ib67.astralflow.machines.trait.Interactive;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

@AutoFactory // MachineFactory will be generated automatically.
public class InteractiveBarrel extends AbstractMachine implements Interactive {

    protected InteractiveBarrel(MachineProperty property) { // some basic information, such as location, context.
        super(property);
    }

    @Override
    public void update(IMachine self) {
        // executed for every tick.
    }

    @Override
    public void onInteract(Action clickType, Player player, ItemStack itemInHand) {
        this.getLocation().getWorld().createExplosion(this.getLocation(), 5f);
    }
}
