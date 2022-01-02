package io.ib67.astralflow.machines;

import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface Interactive {
    void onInteract(Action clickType, Player player, @Nullable ItemStack itemInHand);
}
