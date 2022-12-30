我们引入了 [ILeakTracker](https://flow.bukkit.rip/javadoc/io/ib67/astralflow/security/mem/ILeakTracker.html)
来帮助我们追踪没有被正确卸载掉的对象，并且把他们在控制台打印出来。

# 注册内存泄漏检测

`ILeakTracker` 属于 [SecurityService](https://flow.bukkit.rip/javadoc/io/ib67/astralflow/security/ISecurityService.html)
的一部分。

```java
flow.getSecurityService().getLeakTracker().track(yourObjHere);
```

类似的还有 `untrack`，不多赘述。

# 检测原理

AstralFlow
采用 [SimpleLeakTracker](https://github.com/InlinedLambdas/AstralFlow/blob/main/src/main/java/io/ib67/astralflow/security/mem/impl/SimpleLeakTracker.java)
作为实现。

```hocon
  # 多少个 tick 给潜在的内存泄漏对象记一次周期，有20的倍数个周期的对象会被标记为内存泄露并且在控制台中输出
  # 设置为 -1 禁用内存泄漏检测。此项用于寻找潜在的内存泄漏
  # 如果你不知道怎么调，就把它设置为 100
  leak-check-interval = 100
```

正如 [配置文件](./user_guide/configuration.md) 说的一样，泄露检查具有周期性，并且周期由配置文件来决定。

每过一个周期，SLT 就会给被追踪的对象的计数器自增一次。当 `counter % 20 == 0 ` 时，就会判断为一次内存泄漏，并且播报到控制台中以便尽早发现问题。