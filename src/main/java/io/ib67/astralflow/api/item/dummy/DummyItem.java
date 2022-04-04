package io.ib67.astralflow.api.item.dummy;

import io.ib67.astralflow.item.ItemKey;
import io.ib67.astralflow.item.builder.ItemBuilder;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Item without action.
 *
 * @param key  item key
 * @param item prototype
 */
@ApiStatus.AvailableSince("0.1.0")
@RequiredArgsConstructor
public record DummyItem(
        ItemKey key,
        ItemStack item
) {
    /**
     * Utility method to create a dummy item fastly.
     */
    public static ItemKey registerItem(ItemKey key, ItemStack item, @Nullable String oreDict) {
        ItemBuilder.of(DummyCategory.INSTANCE)
                .oreDict(oreDict)
                .prototype(new DummyItem(key, item))
                .register();
        return key;
    }
}