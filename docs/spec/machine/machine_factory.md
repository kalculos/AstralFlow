在 AstralFlow
中，你可以注册自己的 [MachineFactory](https://af.ib67.io/javadoc/io/ib67/astralflow/machines/factories/IMachineFactory.html)
来定义机器的创建方式。

# 机器工厂

`MachineFactory<M extends IMachine>` 本质上是一个 `Function<MachineProperty, IMachine>`，用于根据机器的信息来构造出对应的机器。

需要注意的是，`MachineFactory` 和机器的类型总是一一对应的，并且不支持注册到抽象类型的机器。

## 注册

AstralFlowAPI 提供了对于 FactoryManager 的公开访问，从中可以获取/删除/注册机器工厂。

```java
flow.getFactories().register(classOfMachine, factory)
```

## 自动机器工厂

由于机器的创建过程大都相似，我们提供一个注解 `@AutoFactory`，用来标注一个 `IMachine` 的子类是可以通过自动生成的机器工厂来创建的。

使用 `@AutoFactory` 需要满足以下条件：

- 必须有公开的构造器
-
且构造器参数只能有一个，而且必须是 [MachineProperty](https://af.ib67.io/javadoc/io/ib67/astralflow/machines/MachineProperty.html)

