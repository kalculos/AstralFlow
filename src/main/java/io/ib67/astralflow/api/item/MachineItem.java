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

package io.ib67.astralflow.api.item;

import io.ib67.astralflow.AstralFlow;
import io.ib67.astralflow.hook.HookType;
import io.ib67.astralflow.hook.event.item.ItemInteractBlockEvent;
import io.ib67.astralflow.hook.event.machine.MachineBreakEvent;
import io.ib67.astralflow.item.ItemKey;
import io.ib67.astralflow.item.LogicalHolder;
import io.ib67.astralflow.machines.IMachine;
import io.ib67.astralflow.machines.Tickless;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@Getter
public class MachineItem implements LogicalHolder {
    private final ItemKey id;
    private final ItemStack prototype;
    private final Class<? extends IMachine> typeOfMachine;

    public MachineItem(ItemKey id, ItemStack prototype, Class<? extends IMachine> typeOfMachine) {
        this.id = id;
        this.prototype = prototype;
        this.typeOfMachine = typeOfMachine;
        HookType.ITEM_INTERACT_BLOCK.register(this::onPlace);
        HookType.MACHINE_BREAK.register(this::onBreak);
    }

    private void onPlace(ItemInteractBlockEvent event) {
        var item = event.getItem();
        if (item.getState().isEmpty() || !(item.getState().get() instanceof MachineItemState)) {
            return;
        }
        var state = (MachineItemState) item.getState().get();
        if (typeOfMachine.getName().equals(state.getMachineType())) {
            // setup machine.
            var machineLoc = event.getClickedBlock().getLocation().clone().add(event.getClickedFace().getDirection());
            var machineUUID = UUID.randomUUID();
            var factory = AstralFlow.getInstance().getFactories().getMachineFactory(typeOfMachine);
            var machine = factory.createMachine(machineLoc, machineUUID, state.getData());
            AstralFlow.getInstance().getMachineManager().setupMachine(machine, !typeOfMachine.isAnnotationPresent(Tickless.class));
        } else {
            return;
        }
    }

    private void onBreak(MachineBreakEvent event) {
        var machine = event.getMachine();
        if (!typeOfMachine.isInstance(event.getMachine())) {
            return;
        }
        var state = new MachineItemState(machine.getState(), machine.getType().getName());
        var item = AstralFlow.getInstance().getItemRegistry().createItem(id);
        var emptyState = (MachineItemState) item.getState().get();
        emptyState.setData(state.getData());
        emptyState.setMachineType(state.getType().getName());
        var loc = event.getBrokenBlock().getLocation();
        loc.getWorld().dropItemNaturally(loc, item.asItemStack());
    }
}
