总所周知，在 Minecraft 中有一个和羊相关的[彩蛋](https://www.youtube.com/watch?v=gtA3pDwbCes)，那就是 `jeb_`。  
给羊命名上 `jeb_` 就可以让羊不断变色，那么我们能否让一块命名 `jeb_` 的羊毛也不断变色呢？  


# 机器

在 AstralFlow 中，我们将所有的自定义方块都抽象为一个`机器`，无论有功能与否。  

简单的说，机器具有这些属性：
 - **位置**: 机器的位置是唯一且确定的
 - **状态**: 每一个机器都有它专属的状态，状态储存了机器的数据

其次，机器还可以被`更新`以及和其他机器交互。机器的更新过程与 Minecraft 的 Tick 相同步，且机器每一 tick 都会被更新一次。

因此，在机器更新时，你可以处理数据并且更改机器的状态。

上面的话听起来有点晦涩难懂，那么让我们看看 AstralFlow 中的机器是怎么样的吧！

## 一个简单机器

在 AstralFlow 中，一个 [IMachine](https://github.com/InlinedLambdas/AstralFlow/blob/main/src/main/java/io/ib67/astralflow/machines/IMachine.java) 代表了一个机器。但我们不推荐开发者直接实现这个接口，我们推荐你继承 `AbstractMachine` 。

```java
import io.ib67.astralflow.machines.trait.Pushable;

@AutoFactory // 此处说明这个机器可以被自动生成的工厂构造
public final class JebWool extends AbstractMachine {
  public JebWool(MachineProperty p) {
        super(p); // 此处传入了一个 MachineProperty ，我们只要把他交给 AbstractMachine 就好。
    }
}
```

我们来逐条解释这些代码的意思。

 - `@AutoFactory` 是一个注解，他用于标注这个机器可以被反射自动构造。  
在 AstralFlow 中，我们将机器的创建委托给 `MachineFactory` 完成，而 `MachineFactory` 需要用户手动注册来定义机器的创建行为。  
但是大多数情况下，我们并不需要那么复杂的功能。因此，如果你的机器 **有一个公开的，而且只有`MachineProperty`作为参数的构造器** 就可以直接用 `@AutoFactory` 从而避免繁琐的注册过程。

关于 `MachineFactory` 的使用，我们会在后续的章节中提到。  

- ` extends AbstractMachine` 表示这个简单机器类继承 `AbstractMachine`。 这是一个面向用户的机器骨架（因为 IMachine 的方法真的是太多了），他可以让你把更多的时间专注在编写具体的逻辑上。

像这样的骨架类还会有很多，我们会在后续的章节中提到。  

- `MachineProperty` 包含了这个机器所需的基本信息，例如位置，所属的`MachineManager`，`状态`，等等。

接下来，我们还要重写他的 `update` 方法。

```java
    //... 代码被省略
    @Override
    public void update(IMachine self) {
       // 在此处运行你的更新逻辑
    }
```

`update` 方法储存了每个机器的更新逻辑，并且每秒被调用 20 次（1 tick / 一次），并且和主线程同步。  

> **黄金法则：不要在 `update` 中编写或涉及到任何可能的堵塞代码，否则将会极为严重的卡服**

请把这条法则牢记在心，这是编写高性能机器的秘诀。

## 设置方块外观

在机器的构造器内，我们得到了一个 `MachineProperty`，其中包含了一个 `Location` ，也就是机器的位置信息。  

但是我们不能太着急。机器被创建的时机是不确定的，因此 AstralFlow 提供了[机器生命周期](https://github.com/InlinedLambdas/AstralFlow/blob/main/src/main/java/io/ib67/astralflow/machines/LifeCycle.java)来解决这个问题。  

接下来，在区块加载时对对应位置的方块进行初始化。  

```java
    @Override
    public void onLoad() {
        getLocation().getBlock().setType(Material.OBSIDIAN); // 在机器的位置放置一个黑曜石
        // getLocation() 来自 IMachine, 返回的就是机器的位置
    }
    // onUnload 同理...
```

当区块加载的时候，你的机器代码就会在它的位置放一个黑曜石了。同样的，我们在 `update` 里写出变色代码。

```java
   private static final Material[] WOOLS = new Material[]{ // 变色羊毛的颜色们
            Material.BLUE_WOOL,
            Material.GREEN_WOOL,
            Material.RED_WOOL,
            Material.YELLOW_WOOL,
            Material.WHITE_WOOL,
            Material.BLACK_WOOL
    };
    private int req; // 一个计数器
    @Override
    public void update(IMachine self) {
        req++; // 自增 req
        if (req % 10 == 0) { // 当 req 是 10 的倍数
            getLocation().getBlock().setType(WOOLS[req / 10 % WOOLS.length]); // 从 WOOLS 中选择一种羊毛并且设置在位置上
        }
    }
```

将这些代码组合起来，我们就有了一块会变色的羊毛！