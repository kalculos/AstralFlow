package io.ib67.astralflow.api.events;

import io.ib67.astralflow.machines.IMachine;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Getter
@Builder
public class PlayerInteractMachineEvent extends Event implements Cancellable {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final IMachine machine;
    private final Player player;
    private final ItemStack itemInHand;
    private final Action clickType;
    @Setter
    private boolean cancelled;

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
