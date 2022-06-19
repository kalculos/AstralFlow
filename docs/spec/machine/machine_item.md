AstralFlow 提供了 `MachineItem` 用于创建机器的对于物品。

# 物品与机器

熔炉(物品) 能放出 熔炉(方块)，而熔炉(方块) 能挖出 熔炉(物品)。

因此可以推导 -> 熔炉物品 即为 熔炉方块 的 `MachineItem`。在 [变色羊毛](./getting_started/jeb_wool.md) 中，我们就使用了 `MachineItem`。

```java
    // 注册
    ItemBuilder.of(MachineCategory.INSTANCE) // 声明类别
            .prototype(new MachineItem( // 声明原型
                    itemKey, 
                    itemStack,
                    JebWool.class // JebWool 的类
            ))
            .register();
```

使用非常简单，不过多赘述，但是有一些特殊的条件需要注意。

# 限制

有状态机器的 State 必须使用 `ItemState`，否则无法储存状态。

传入物品原型时不能使用有物理性质的方块，例如 TNT，沙子，沙砾 等。使用这些方块可能会带来意想不到的后果（处于性能考虑，AstralFlow 并没有针对某些方块来处理 BlockPhysicEvent ）
