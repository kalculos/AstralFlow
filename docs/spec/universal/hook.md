Hook 是 AstralFlow 中主要提供信息通知的组件，你可以通过 Hook 接收到一些来自 Bukkit 的 Event 从而避免繁琐的注册监听器等等过程。

# HookType

[io.ib67.astralflow.hook.HookType](https://af.ib67.io/javadoc/io/ib67/astralflow/hook/HookType.html) 是 Hook
系统里的核心类，你可以通过 HookType 对象来注册你的监听器。

> 你也可以通过自己创建一个 HookType 用于广播你的自定义信息，这可以简化扩展之间的交互。

不过要注意的是，并非所有的 Bukkit 事件都有它对应的 HookType. 如果遇到了没有对应的 HookType 的事件，请自行实现它的转播或者发一个 Issue.

## 注册

可以使用 `HookType<T>#register(Runnable)` 注册，也可以使用 `HookType<T>#register(Consumer<T>)` 来注册。

HookType 本身提供了大量已经支持的常量 hook, 如果遇到符合需求的直接使用即可。

例如：

```java
        HookType.CHUNK_LOAD.register(this::initChunk);
        HookType.CHUNK_UNLOAD.register(t -> finalizeChunk(t.getChunk()));
        HookType.SAVE_DATA.register(this::onSaveData);
```

要注意的是，有的 HookType 是不会传递任何值的（也就是 null ），但是在 AstralFlow 内，只要 HookType 的类型参数明确（比如`HookType<ChunkLoadEvent>`），就必然有值，否之亦然。

## 播报消息

可以使用 `AstralFlowAPI#callHooks(HookType,Event)` 播报消息。

这个方法返回一个 boolean, 代表传递的 `Event` 是否被 cancell（ 如果这个 `Event` 是一个 `Cancellable` ），这样设计主要是为了方便。

```java
    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        event.setCancelled(
                flow.callHooks(
                        HookType.BLOCK_PLACE,
                        event
                )
        );
    }
```

## 所有已经支持的 HookType

请查阅 [Javadoc](https://af.ib67.io/javadoc/io/ib67/astralflow/hook/HookType.html)