在实际开发中，我们总是会遇到需要一些跟周围机器打交道的需求：

- 搜索周围的机器
- 跟他们联系上

因此 AstralFlow
提供了 [IWirelessPeer](https://flow.bukkit.rip/javadoc/io/ib67/astralflow/capability/wireless/IWirelessPeer.html) 来解决这个问题。

# 发现机器

我们规定，所有希望被发现的节点都应该实现 `IWirelessPeer` 接口，并且注册到 `WirelessRegistry` 中。

无线通信和发现服务是机器能力的一部分，你可以通过 [CapabilityService](https://flow.bukkit.rip/javadoc/io/ib67/astralflow/capability/ICapabilityService.html)
得到他们:

```java
var registry = flow.getCapabilityService().getWirelessRegistry();
```

接着我们可以使用它来注册和发现一个 `Location` 附近的区域了。

## findPeers

```java
    /**
     * Find near-by peers with their type.
     *
     * @param location The location to find nearby peers.
     * @param <T>      The type of the peer.
     * @return The nearby peers.
     */
    <T> Collection<? extends IWirelessPeer<T>> findPeers(Location location, double range, @Nullable Class<T> type);
```

只需要传入寻找的中心位置，范围以及信息类型即可。

关于信息类型会在无线通信部分提到。

## findPeersAsync

```java
    /**
     * Find nearby peers with their type in async threads, this may help for large-area discovery.
     * This should be called on the main thread.
     *
     * @param location
     * @param <T>
     * @return
     */
    <T> CompletableFuture<? extends Collection<? extends IWirelessPeer<T>>> findPeersAsync(Location location, double range);
```

`findPeersAsync` 适用于大规模的寻找节点，并且不要求对结果非常精确（也就是说你得到结果的时候可能有的节点已经不在了，或者有新的节点插入进来但是你不知道）

注意，在调用 findPeersAsync 的时候需要在同步线程上调用，但是返回的 `CompletableFuture` 是异步的。

# 注册节点

如果你想让你的节点能够被发现，那么你需要注册你的节点。

```java
registry.registerPeer(yourPeer);
```

## 取消注册

`unregister` 同理。

值得一提的是，如果是机器在使用无线发现功能的话，并不需要手动 `unregister` —— 只要你的机器对象遵循机器生命周期就好，`WirelessRegistry` 内部使用的 `WeakHashMap` 将会自动清理掉被 JVM
标注为垃圾的对象。

如果你的机器不遵循 [机器生命周期](./spec/machine/machine_lifecycle.md)，那么请在你认为不应该再被发现的时候来取消注册。

不过，除了在 `WirelessRegistry` 里面注销之外，你还应该考虑到一种特殊的情况 —— *虽然你的 peer 已经注销了，但是别人还是持有你的 peer 对象*

这种情况通常发生在 `findPeersAsync` 上，它极有可能返回一些已经注销过的节点。对于这种情况，你的 `IWirelessPeer#isAvailable` 应该始终返回
false，在与其他机器无线通信的时候也应该考虑到这种情况。

也正是因为这种情况的可能性，我们建议开发者不要直接使用 `IWirelessPeer` 来传递消息，而是使用 `Flow`。

# 节点通信

[IWirelessPeer](https://flow.bukkit.rip/javadoc/io/ib67/astralflow/capability/wireless/IWirelessPeer.html)
提供了三个重要的方法用于节点之间的通信：

```java
    /**
     * Establish connection with this peer
     *
     * @param peer
     */
    void negotiate(IWirelessPeer<T> peer);

    /**
     * Receives a message from other peers. This method should be called by other peers.
     *
     * @param message
     */
    void receiveMessage(T message);
    
    Class<T> getMessageClass();
```

- `getMessageClass()` 表示所适配的消息类型，你应该始终根据消息类型来实施不同的沟通策略。
- `negotiate(peer)` 用于握手，过程由具体的实现来决定。
- `receiveMessage(T message)` 用于发送信息，发送信息的时机也由具体实现决定。

虽然 `IWirelessPeer` 只提供了极小一部分功能，大多数功能都需要依赖于实现来决定策略，我们仍然推荐：

- 对于没有必要事先沟通的 peer，他们的 `message` 应该实现 `StatelessMessage`，尽管这个接口没有定义任何方法。
- 不应该总是使用 `IWirelessPeer` 来直接传输数据。如果有传输数据的需求，应该考虑使用 `negotiate` + 一个安全的管道（比如 `Flow`）

如果你仍然有使用 Peer 直接沟通的需求，请总是检查 `isAvailable` 并且考虑使用一个 `WeakReference`

## 使用 SimplePeer

你可以使用 [SimplePeer](https://flow.bukkit.rip/javadoc/io/ib67/astralflow/capability/wireless/SimplePeer.html)
来避免实现 `IWirelessPeer` 的各种方法。

```java
    SimplePeer.createBuilder()
        .location(null)
        .messageReceiver(o -> {})
        .negotiator(o -> {})
        .registry(anotherRegistry)
        .type(messageType)
        .build();
```

在 `build()` 后 SimplePeer 就会被自动注册到 registry 里，因此不需要手动注册它。  
此外，注册完毕之后要保留返回的 `SimplePeer`，你可以把它放在机器的字段里，不然可能会被 GC。