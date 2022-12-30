“原型” 是一个经常在 AstralFlow 中出现的概念。

# 原型

在 AstralFlow 中，我们通过对物品的原型进行拷贝，随后对其装载状态便构成了一个新的物品。

因此，原型实际上就是新对象的模板，对生成的对象做的修改不会反映到原型上。

同时，原型是不可变的。有时我们无法通过限制可见性和运行时异常的方式来阻止原型被修改，但是我们遵循一个共同的约定：不要修改原型，无论是 `ItemStack` 或者是别的什么。

# ItemPrototypeFactory

ItemPrototypeFactory
的定义在 [io.ib67.astralflow.item.factory](https://af.ib67.io/javadoc/io/ib67/astralflow/item/factory/ItemPrototypeFactory.html)
内。

```java

/**
 * The definition of prototypes for items. Determines how does the item look like ({@link #getPrototype()}) and its empty state ({@link #getStatePrototype()})
 * <p>
 */
@ApiStatus.AvailableSince("0.1.0")
public interface ItemPrototypeFactory {

    /**
     * Create a sample itemstack, which will be processed for a real astral item. (attach data)
     * You shouldn't modify the itemstack, always clone it
     *
     * @return sample itemstack
     */
    @Contract(pure = true)
    @NotNull
    ItemStack getPrototype();

    /**
     * The empty state.
     * You shouldn't modify it, always clone it
     *
     * @return null if there is a empty state
     */
    @Contract(pure = true)
    @Nullable // stateless item
    ItemState getStatePrototype();

    /**
     * The ItemKey of this item
     *
     * @return item key
     */
    @Contract(pure = true)
    ItemKey getId();

    /**
     * The final registry. this is for decorators.
     *
     * @return
     * @implSpec Decorators **must** return their decorated prototype.
     */
    default ItemPrototypeFactory getRegistry() {
        return this;
    }

    /**
     * The logical holder of this item. Such as {@link io.ib67.astralflow.api.item.machine.MachineItem}
     *
     * @return null if there is no logical holder
     */
    default LogicalHolder getHolder() {
        return null;
    }
}
```

除了自己实现 ItemPrototypeFactory, 你也可以用一个内建的工具类来生成它。

```java
ItemPrototype.builder()
        .holder(null)
        .prototype(null)
        .id(null)
        .statePrototype(null)
        .build();
```

## LogicalHolder

[LogicalHolder](https://af.ib67.io/javadoc/io/ib67/astralflow/item/LogicalHolder.html) 是一个接口，它指的是这个物品的逻辑持有者。

逻辑持有者的意思就是，"操作这个物品的人"。比如说 `MachineItem`
就是一个逻辑持有者，因为其本身掌管了这一类物品的 [所有逻辑](https://github.com/InlinedLambdas/AstralFlow/blob/main/src/main/java/io/ib67/astralflow/api/item/machine/MachineItem.java)

通过 LogicalHolder 接口，你就能够在处理的时候得知这个物品是否是你的那个物品从而运行接下来的代码。
