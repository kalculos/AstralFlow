对于一些特殊的机器（比如装饰性方块），他们不做任何事情。因此，AstralFlow
提供了 [@Tickless](https://flow.bukkit.rip/javadoc/io/ib67/astralflow/machines/Tickless.html) 注解来解决这个问题

只需要在任意 `IMachine` 的子类上标注这个注解，AstralFlow 就不会再去更新你的机器了。  
