李华最近知道了只要给羊对染料右键就能把羊染色，但是他家有太多羊了，一只只染色很麻烦。  
于是李华找到举世无双秒天灭地的你，想让你帮他写一个范围染色的小工具。

# 自定义物品

关于物品的概念，我们已经在 [上一章](./getting_started/jeb_wool.md) 介绍过 [物品类别](./spec/item_categories.md)
和 [物品原型](./spec/item_prototype.md) 的概念，但是 AstralFlow 提供的内置物品模板非常有限，我们需要自己动手写一个新的物品类别来实现李华的需求。

实现一个物品类别并不复杂，但是在这之前我们需要先给我们的新物品类别下一个定义。

```java
import io.ib67.astralflow.api.item.ItemBase;
import io.ib67.astralflow.item.ItemKey;
import org.bukkit.inventory.ItemStack;

class MagicaWand extends ItemBase {
    protected MagicaWand(ItemKey itemKey, ItemStack prototype) {
        super(itemKey, prototype);
    }
}
```

- `ItemBase` 是一个通用的基类，他定义了一个非常常用的模板，其中包含了构造一个 [ItemPrototypeFactory](./spec/item_prototype.md) 的参数，你也可以选择不用它，自己写。
- 我们把必要的参数通过 `super(..)` 传递给 `ItemBase`。

又因为我们的 `MagicaWand` 是 `ItemBase` 的子类，我们可以直接使用一个通用的 Category。

```java
ItemBuilder.ofBase()
    .prototype(new MagicaWand(
        ItemKey.from("haha","magica_wand"),
        new ItemStack(Material.STICK)
        )
    )
    .register()
```

和上一章一样的套路，这样我们就注册了一个毫无卵用的 `MagicaWand` ！

# 赋予功能

正如我说的那样，你的 `MagicaWand` 到目前还是一点用都没有，因此我们要开始编写一些功能。

在一般的 Bukkit 开发里，我们是这样实现功能的：

- 监听某个事件
- 在监听的事件中，判断是否是我们的物品
- 做一些事情

AstralFlow 并没有改变这个流程，但是我们把一些过程简化了。以及，在这个时候我们的 `MagicaWand`
有了一个新的名字，它就是 [LogicalHolder](./spec/item_prototype.md?id=logicalholder)，意思是掌管逻辑的东西。

## 钩子系统

在 AstralFlow 里，你不需要为了物品专门去注册一个 Listener 然后大费功夫得把事件传到 `LogicalHolder` 的手上......大可不必！其实我们已经帮你做好了。

[HookType / 钩子](https://flow.bukkit.rip/javadoc/io/ib67/astralflow/hook/HookType.html) 提供了订阅事件的能力，并且 AstralFlow
内置了很多已经对接到 Bukkit Events 上的 `HookType`
。你可以在 [Javadoc](https://flow.bukkit.rip/javadoc/io/ib67/astralflow/hook/HookType.html) 上看到完整的列表

使用钩子很简单：

```java
    HookType.ITEM_USE.register(this::onItemUse);
```

这样你就可以监听任意物品的使用了，注意，是任意。

在 `onItemUse` 里，我们要这么写：

```java
private void onItemUse(PlayerInteractEvent event){ // 注意：ITEM_USE 不完全等于 PlayerInteractEvent，它有特殊的触发条件
    var player = event.getPlayer();
    var itemInHand = player.getEquipment().getItemInMainHand();
    if (itemInHand.getType() == Material.AIR) {
        return;
    }
    var isItem = AstralHelper.isHolder(itemInHand, this);
    if (!isItem) return;
    // ... 给范围内的羊变色 ... 自己写 ...
}
```

只需要通过 `AstralHelper` 的辅助方法来判断物品的逻辑持有者是不是自己即可。

由此，你就得到了一个可以给周围羊变色的手杖（当然你要先把变色代码自己写出来），试着结合上一章学到的内容给它添加一个合成表吧！

# 自定义物品类别

关于自定义物品类别，本章作为入门教程不会详讲，请参考 [文档: 物品类别](./spec/item_categories.md)
