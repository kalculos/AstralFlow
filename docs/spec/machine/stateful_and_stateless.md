根据有无对持久化的需求，我们将机器分为两种类型：`Stateful/有状态` 和 `Stateless/无状态`。

# 无状态机器

无状态机器总是不储存任何数据在内存以外的地方，实例被重复创建后数据也会随之全部丢失。  
如 [变色羊毛](./getting_started/jeb_wool.md) 则是一个无状态机器。他的数据只储存在机器实例中，而不是在 `IState` 里。

# 有状态机器

与无状态机器相反，有状态机器可以持久的保存数据，这是通过 `IState` 实现的。每个机器都有一个 `IState`，不过一般情况下是空的。

为了使用 `IState` 存储数据，你需要定义数据类并且实现 `IState` 接口。

> 需要注意的是，目前 AstralFlow **不支持** 嵌套的 `IState`，也就是一个 `IState` 内不能包含其他的 `IState`，否则会产生问题。
> 对于这种特殊情形，可以考虑使用 @JsonAdapter 并且避免使用 `IState` 作为字段的静态类型。

实例：

```java
@Getter
class BlockRepository implements IState {
    private final List<ItemStack> items = new ArrayList();
}
```

AstralFlow 额外提供了对 `ItemStack`, `Location` 的序列化支持。对于没有被支持到的常用数据类型可以发 Issue 提建议。

> 需要注意的是: 你永远不应该在 `IState` 里储存 Bukkit 的抽象类型，因为它们的序列化通常无意义。
> 如：Player, Entity, World, etc. 对于此类对象请总是使用 UUID 或其他用于标识他们的方式，并且应该考虑异常的处理，如 `玩家不在线`

## MachineItem

作为一种特殊情况，当你的机器使用 `MachineItem` 作为和物品联系的桥梁时你需要使用 `ItemState` 而不是 `IState`，好在这两个类型上并没有太大的差异。

只需更改 `IState` 到 `ItemState` 即可。
