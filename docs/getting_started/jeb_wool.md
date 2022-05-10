总所周知，在 Minecraft 中有一个和羊相关的[彩蛋](https://www.youtube.com/watch?v=gtA3pDwbCes)，那就是 `jeb_`。  
给羊命名上 `jeb_` 就可以让羊不断变色，那么我们能否让一块命名 `jeb_` 的羊毛也不断变色呢？  

在看本文的同时，请尝试动手跟着本文的代码走，这可以使得你学习开发扩展事半功倍。

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
        getLocation().getBlock().setType(WOOLS[req % WOOLS.length]); // 从 WOOLS 中选择一种羊毛并且设置在位置上
    }
```

将这些代码组合起来，我们就有了一块会变色的羊毛！然而，我们还没有办法把它放置出来。  

回想一下，在 Minecraft 里，我们是如何放置一个方块的... 首先我们要有一个对应的物品，接着我们才能做 "放置" 的动作。因此，我们需要先注册一个新的物品。  

# 物品

AstralFlow 提供了注册自定义物品以及绑定逻辑的 API。  

## 物品类别

在开始注册物品之前，不妨先思考一个问题——香蕉，苹果和巧克力，他们的共性在哪？  

这个问题并不难以回答，他们都是吃的，也就是`食物`。`食物`都有相同的属性，因此我们可以把它们划为一类，也就是`物品类别`。  
在 AstralFlow 中，一个 [ItemCategory](https://github.com/InlinedLambdas/AstralFlow/blob/main/src/main/java/io/ib67/astralflow/item/builder/ItemCategory.java) 代表了对这类物品行为的定义以及创建他们原型的方法。  

机器物品也是一种类别，那么现在让我们试试看。  

## 注册物品  

注册过程很简单，只需要借助 [ItemBuilder](https://github.com/InlinedLambdas/AstralFlow/blob/main/src/main/java/io/ib67/astralflow/item/builder/ItemBuilder.java) 即可。但是物品的注册过程最好在模块的 `init` 中进行，否则可能会遇到某些东西（矿物词典）已经被锁定的情况。  

```java
@Override
public void init(){
    // 物品的标识符
    var itemKey = ItemKey.from("test_namespace", "jebwool");
    var itemStack = new ItemStack(Material.WHITE_WOOL);
    // 设定模板物品
    var itemMeta = itemStack.getItemMeta();
    itemMeta.setDisplayName(ChatColor.RED + "JEB_WOOL");
    itemStack.setItemMeta(itemMeta);
    // 注册
    ItemBuilder.of(MachineCategory.INSTANCE) // 声明类别
            .prototype(new MachineItem( // 声明原型
                    itemKey, 
                    itemStack,
                    JebWool.class // JebWool 的类
            ))
            .register();
}
```

上述代码是一个注册你的变色羊毛的例子，我们接下来会逐条讲解这些代码的意思。  

- `itemKey` 是在 AstralFlow 中你的物品的"名字"（也就是id），它由命名空间和名字组成，以此避免名字上的冲突/污染，你也可以用它创建一个新的物品。
- `MachineCategory` 是机器物品的类别。大多数类别都以这样一个单例的模式呈现（也就是：只有一个开放的 `INSTANCE` 字段作为他的唯一实例）
- `prototype` 定义了你的物品的原型，这是由你上文中声明的 `MachineCategory` 来决定的。对于不同的物品类别，他们的原型的类型也不一致，这使得自定义物品的编写工作能够轻松不少。  
- `register()` 不返回任何东西，它会直接注册你的物品到 AstralFlow 中。

稍后，我们可以用 `ItemKey#createNewItem()` 来得到你的新物品。  

```java
var machineItem = itemKey.createNewItem(); // ItemStack
```

要注意的是，对于`有状态`的物品，它们几乎总是不相似的，而且创建新物品的过程有可能会因为数据序列化带来额外的开销。  
因此，当你需要一些物品占位符的时候，请获取它们对应的物品原型：  

```java
flow.getItemRegistry().getRegistry(TestItems.JEB_WOOL).getPrototype().clone(); // flow 由 AstralExtension 提供，你也可以用 AstralFlow.getInstance() 得到一个 flow
```


### 一些捷径

其实在写扩展的时候我们有更快捷的写法，先来看一个精简过后的注册物品流程。

```java
@Override
public void init(){
        // 物品的标识符
        var itemKey = TestItems.JEB_WOOL // 3
        var itemStack = ItemStacks.builder(Material.WHITE_WOOL) // 1
                .displayName(ChatColor.RED + "JEB_WOOL")
                .build();
        // 注册
        itemMachine() // 2
                .prototype(new MachineItem(
                        itemKey,
                        itemStack,
                        JebWool.class
                ))
                .register();
}
```

1. `ItemStacks` 是 AstralFlow 提供的工具类，用于快捷创建模板物品。
2. `itemMachine()` 是 [AstralExtension](https://github.com/InlinedLambdas/AstralFlow/blob/main/src/main/java/io/ib67/astralflow/api/external/AstralExtension.java) 中提供的方法，类似的在这里的方法还有很多，你可以自行查阅它的代码。
3. `TestItems` 是一个枚举。

等等，枚举是怎么回事？

如果你有自己上手写的话，你可能会发现 `ItemKey` 是一个接口，而这是有意为之。在 Java 中，枚举是可以实现接口的，由此我们可以写出这样的代码：  

```java
public enum TestItems implements ItemKey {
    JEB_WOOL("jeb_wool"),

    private final String id;

    TestItems(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getNamespace() {
        return "tester";
    }
}
```

由此，我们可以充分利用枚举的特性来为你的各种 `ItemKey` 服务。

## 物品配方