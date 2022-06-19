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
import io.ib67.astralflow.api.events.MachineBlockBreakEvent;
import io.ib67.astralflow.api.events.MachineBlockPlaceEvent;
import io.ib67.astralflow.api.item.ItemBase;
import io.ib67.astralflow.api.item.machine.internal.SimpleMachineItemState;
import io.ib67.astralflow.hook.HookType;
import io.ib67.astralflow.item.AstralItem;
import io.ib67.astralflow.item.ItemKey;
import io.ib67.astralflow.item.ItemState;
import io.ib67.astralflow.item.StateScope;
import io.ib67.astralflow.item.builder.ItemBuilder;
import io.ib67.astralflow.machines.IMachine;
import io.ib67.astralflow.machines.MachineContext;
import io.ib67.astralflow.machines.MachineProperty;
import io.ib67.astralflow.machines.Tickless;
import io.ib67.astralflow.util.Blocks;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

/**
 * <p>The machine blockItem, which creates your machine when placed and destroys {@literal &} save it when broken.</p>
 * Machines using this class should use a {@link ItemState} instead of {@link io.ib67.astralflow.machines.IState}.
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
        requireNonNull(typeOfMachine, "typeOfMachine");
        this.typeOfMachine = typeOfMachine;
        if (!prototype.getType().isBlock() || !Blocks.isNonPhysical(prototype.getType())) {
            throw new IllegalArgumentException("MachineItem must be a solid and non-physical block!");
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
        var item = new AstralItem(event.getItemInHand(), AstralFlow.getInstance().getItemRegistry());
        if (item.getState().isEmpty() || !(item.getState().get() instanceof SimpleMachineItemState)) {
            return;
        }
        var state = (SimpleMachineItemState) item.getState().get();
        var machineState = AstralFlow.getInstance().getItemRegistry().getState(item.asItemStack(), StateScope.USER_MACHINE);
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
                            .context(MachineContext.builder()
                                    .owningMachine(null)
                                    .owningPlayer(event.getPlayer())
                                    .reason(MachineContext.Reason.PLAYER)
                                    .build()
                            )
                            .state(machineState)
                            .manager(AstralFlow.getInstance().getMachineManager())
                            .build()
            );
            var evt = new MachineBlockPlaceEvent(machine.getLocation().getBlock(), machine, event.getPlayer());
            Bukkit.getServer().getPluginManager().callEvent(evt);
            if (evt.isCancelled()) {
                return;
            }
            AstralFlow.getInstance().getMachineManager().setupMachine(machine, !typeOfMachine.isAnnotationPresent(Tickless.class));
        } else {
            return;
        }
    }

    private void onBreak(MachineBlockBreakEvent event) {
        var machine = event.getMachine();
        if (!typeOfMachine.isInstance(event.getMachine())) {
            return;
        }
        var item = AstralFlow.getInstance().getItemRegistry().createItem(getId());
        var emptyState = (SimpleMachineItemState) item.getState().get();
        emptyState.setMachineType(machine.getType().getName());
        item.saveState(emptyState);
        if (machine.getState() != null) {
            if (!(machine.getState() instanceof ItemState)) {
                throw new IllegalArgumentException("Machine state must be an ItemState!");
            }
            AstralFlow.getInstance().getItemRegistry().saveState(item.asItemStack(), StateScope.USER_MACHINE, (ItemState) machine.getState());
        }
        var loc = event.getBlock().getLocation();
        var actuallyDroppedItems = List.of(loc.getWorld().dropItemNaturally(loc, item.asItemStack()));
        var itemToDrop = new ArrayList<>(actuallyDroppedItems);
        if (event.getPlayer() != null) {
            var bdie = new BlockDropItemEvent(loc.getBlock(), loc.getBlock().getState(), event.getPlayer(), itemToDrop);
            Bukkit.getPluginManager().callEvent(bdie);
            if (bdie.isCancelled() || itemToDrop.isEmpty()) {
                for (Item actuallyDroppedItem : actuallyDroppedItems) {
                    actuallyDroppedItem.remove();
                }
            }
        }

    }
}
