# 事件触发顺序

AstralFlow 内置了一些事件监听器以驱动 [Hook 系统](./spec/universal/hook.md) 的运行，它们之间有次序。

次序主要是为了与别的插件以及现有内容准备的，如「先广播机器交互，再广播物品接触了机器」

具体的次序如下：

- 与机器方块的交互 -- 最先触发，若此阶段有事件被取消则下游不会触发
- 与物品，方块的交互 -- 同上半条

同时，还有一些特殊情况：

- [手持物品和实体交互](https://af.ib67.io/javadoc/io/ib67/astralflow/hook/HookType.html#PLAYER_INTERACT_ENTITY)
  使用的优先级为 `EventPriority.HIGH`，此优先级低于一般监听器
- 如果你需要绕过被机器方块阻止的事件，可以用带有后缀 `_LOW` 的 Hook,
  例如 [PLAYER_INTERACT_BLOCK_LOW](https://af.ib67.io/javadoc/io/ib67/astralflow/hook/HookType.html#PLAYER_INTERACT_BLOCK_LOW)
  ，同时这类 Hook 的优先级通常都高于同类型的其他 Hook.