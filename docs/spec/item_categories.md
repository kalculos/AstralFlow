# 物品类别

[物品类别](https://flow.bukkit.rip/javadoc/io/ib67/astralflow/item/builder/ItemCategory.html)
用于规定一类物品的行为和定义，它主要为了 `ItemBuilder` 服务，并非核心组件。

```java

/**
 * A category of items.<br />
 * This class defines how {@link ItemPrototypeFactory}s are created from your custom item types.<br />
 * See {@link io.ib67.astralflow.api.item.weapon.WeaponCategory} for an example.
 *
 * @param <I> The type of item this category is for.
 */
@ApiStatus.AvailableSince("0.1.0")
public interface ItemCategory<I> {

    /**
     * Produces an {@link ItemPrototypeFactory} for the given item, which will be registered into {@link io.ib67.astralflow.manager.ItemRegistry}
     */
    ItemPrototypeFactory getFactory(I item);

}
```

由上定义可知，`ItemCategory` 接受一个类型参数 `I`，用于指定它接收的 *原型* 类型。并且类似一个 `Function<I,ItemPrototypeFactory>` 用于构造 IPF。

关于 ItemPrototypeFactory 请参考 [物品与原型](./spec/item_prototype.md)

* `MachineCategory` 的原型类型是 `MachineItem`
* `WeaponCategory` 的原型类型是 `WeaponBase`，`WeaponBase` 有若干子类....

## 自定义物品类别

你可以通过自定义一个物品类别的方式来将你的新物品接入 `ItemBuilder` 中，只需提供单例对象即可。  
在 [io.ib67.astralflow.api.item](https://github.com/InlinedLambdas/AstralFlow/tree/main/src/main/java/io/ib67/astralflow/api/item)
下有详尽的参考对象。

以及，我们通常推荐你的新物品使用 `ItemBase` 作为基类，正如你在 AstralFlow 内置的物品类型内看到的，它可以节省掉一些样版代码。