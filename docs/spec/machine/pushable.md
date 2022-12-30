在 AstralFlow 中，机器默认是不可推动的，你需要为能够推动的机器专门定义可推动。

> 注意：Pushable API 处于实验性阶段，未来极有可能发生改变

# Pushable

为了让你的机器方块能够推动，我们需要实现 [Pushable](https://af.ib67.io/javadoc/io/ib67/astralflow/machines/trait/Pushable.html).

```java
/**
 * Represents a trait that can be moved.
 */
@ApiStatus.AvailableSince("0.1.0")
@ApiStatus.Experimental
public interface Pushable {
    void push(Location newLocation, Vector direction);
}
```

`push(Location,Vectory)` 会在推动的时候传入两个参数，一个是新的位置，一个是推动的方向。  
要注意的是，Pushable 暂时还没有事务性( [#155](https://github.com/InlinedLambdas/AstralFlow/issues/155) )，因此一旦被推动，你的机器必须成功，不许失败。  
当然，你不需要担心方块被推到黑曜石里面去的奇怪问题。原版机制不成立的情况下，`push` 是不会被调用的，也就是不会被推动。

原版机制适用于 `Pushable`。