在 AstralFlow 中，总会有一些机器需要跟玩家打交道，而这是一个机器特质。

# Interactive

[Interactive](https://af.ib67.io/javadoc/io/ib67/astralflow/machines/trait/Interactive.html) 代表你的机器能够与玩家交互。

```java
/**
 * Represents an interactive trait for machines.
 */
@ApiStatus.AvailableSince("0.1.0")
public interface Interactive {
    /**
     * Called when a player tries to interact with the machine. (clicking)
     *
     * @param clickType  The type of interaction.
     * @param player     The player that interacted.
     * @param itemInHand The item in the player's hand.
     */
    void onInteract(Action clickType, Player player, @Nullable ItemStack itemInHand);
}
```

只需实现 `Interactive`，接下来当你的机器被玩家点击时，AstralFlow 会调用 `onInteract` 方法。