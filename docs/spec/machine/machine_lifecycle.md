在 AstralFlow 中，每个注册的 [IMachine](https://af.ib67.io/javadoc/io/ib67/astralflow/machines/IMachine.html)
都被严格规定了生命周期，这是为了避免潜在的内存泄漏以及安全问题。

# 生命周期

默认的，在 AstralFlow 中，你的机器应该随着他所在的区块而存在，当区块被卸载的时候也应该随之被卸载卸载。

区块加载时 -> 机器加载 -> ... 机器运行 ... 区块运行 ... -> 区块卸载时 -> 机器卸载 -> 区块卸载完毕

因此，如果你有必要储存一个 `IMachine` 对象，你应该考虑到他的生命周期问题。典型的做法就是使用 `WeakReference<T>` 那样的弱引用容器来储存，或者是使用 `Flow` 来管理数据的来源从而避免机器对象的直接参与。

## 内存泄漏检测

当**注册过的机器**被对应的 [IMachineManager](https://af.ib67.io/javadoc/io/ib67/astralflow/manager/IMachineManager.html)
标注为卸载后，将会被同时纳入内存泄漏检器的目标之内。

关于内存泄漏检测，请参考 [内存泄漏检测](./misc/memleakcheck.md)

