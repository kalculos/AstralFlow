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

package io.ib67.astralflow.api.item.machine;

import io.ib67.astralflow.AstralFlow;
import io.ib67.astralflow.api.item.ItemBase;
import io.ib67.astralflow.hook.HookType;
import io.ib67.astralflow.hook.event.machine.MachineBreakEvent;
import io.ib67.astralflow.item.AstralItem;
import io.ib67.astralflow.item.ItemKey;
import io.ib67.astralflow.item.builder.ItemBuilder;
import io.ib67.astralflow.machines.IMachine;
import io.ib67.astralflow.machines.MachineProperty;
import io.ib67.astralflow.machines.Tickless;
import lombok.Getter;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.UUID;

/**
 * The machine blockItem, which creates your machine when placed and destroys & save it when broken.
 */
@ApiStatus.AvailableSince("0.1.0")
@Getter
public class MachineItem extends ItemBase {
    private final Class<? extends IMachine> typeOfMachine;

    /**
     * Constructor
     *
     * @param id            item key
     * @param prototype     prototype, must be a solid block
     * @param typeOfMachine machine type
     */
    public MachineItem(ItemKey id, ItemStack prototype, Class<? extends IMachine> typeOfMachine) {
        super(id, prototype);
        this.typeOfMachine = typeOfMachine;
        if (!prototype.getType().isBlock() || !prototype.getType().isSolid()) {
            throw new IllegalArgumentException("MachineItem must be a block!");
        }
        HookType.BLOCK_PLACE.register(this::onPlace);
        HookType.MACHINE_BREAK.register(this::onBreak);
    }

    /**
     * Utility method to register machine items
     *
     * @param id            itemkey
     * @param prototype     prototype
     * @param typeOfMachine machine type
     * @return itemkey
     */
    public static ItemKey registerItem(ItemKey id, ItemStack prototype, Class<? extends IMachine> typeOfMachine) {
        ItemBuilder.of(MachineCategory.INSTANCE)
                .prototype(new MachineItem(id, prototype, typeOfMachine))
                .register();
        return id;
    }

    private void onPlace(BlockPlaceEvent event) {
        var item = new AstralItem(event.getItemInHand(), AstralFlow.getInstance().getItemRegistry()); // todo: refactor BlockPlaceEvent
        if (item.getState().isEmpty() || !(item.getState().get() instanceof MachineItemState)) {
            return;
        }
        var state = (MachineItemState) item.getState().get();
        if (typeOfMachine.getName().equals(state.getMachineType())) {
            // setup machine.
            var machineLoc = event.getBlock().getLocation();
            var machineUUID = UUID.randomUUID();
            var factory = AstralFlow.getInstance().getFactories().getMachineFactory(typeOfMachine);
            //var machine = factory.createMachine(machineLoc, machineUUID, state.getData());
            var machine = factory.createMachine(
                    MachineProperty.builder()
                            .location(machineLoc)
                            .uuid(machineUUID)
                            .state(state.getData())
                            .manager(AstralFlow.getInstance().getMachineManager())
                            .build()
            );
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
        var item = AstralFlow.getInstance().getItemRegistry().createItem(getId());
        var emptyState = (MachineItemState) item.getState().get();
        emptyState.setData(state.getData());
        emptyState.setMachineType(machine.getType().getName());
        var loc = event.getBrokenBlock().getLocation();
        loc.getWorld().dropItemNaturally(loc, item.asItemStack());
    }
}
