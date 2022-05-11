# 配方类型

AstralFlow 的配方系统接受任何 `AstralRecipe`，不过内置的只有几种。

配方大多数都有静态方法用于构造 `Builder`，例如 `Shaped.of()`，请留心。

在使用 `ItemBuilder` 等组件时 `result` 和 `demoItem` 等参数会被自动填充，因此不需要你手动去填。

## 原料

原料是参与配方合成的材料，但他们不局限于具体的某种物体。相反，他们是一类谓词(`Predicate<ItemStack>`)，负责匹配并且消耗对应槽位上的物品。

内置的原料类型只会消耗特定数目的物品和它的耐久，用户可以自己实现或者继承现有的 `IngredientChoice` 来改变配方的消耗方式。

## AstralItemChoice

此类原料只接受某种特定的自定义物品，使用 `AstralFlow` 提供的默认组件进行判断。

示例：`new AstralItemChoice(ItemKey.from(...), ItemKey.from(...), ......)`

## ExactItemChoice

此类原料只接受某种具体的 `ItemStack`。

示例：`new ExactItemChoice(new ItemStack(WOOL), .....)`

## MaterialChoice

此类原料只接受某种 `Material`。

示例：`new MaterialChoice(Material.WOOL, Material.STONE, ......)`

## OreDictChoice

此类原料只接受某种 [矿物辞典](./spec/oredict.md) 名，使用 `AstralFlow` 提供的默认组件进行判断。

示例：`new OreDictChoice("...", "...", ......)`

## Shaped 有序配方

有序配方严格规定了物品摆放的形状，不过不限制他们的数量。  
火把就是有序配方，你可以把火把的物品摆在左上角，也可以摆在右下角。只要是上煤炭下木棍就都可以匹配到，和数量没有太大的关系。

例子：

```java
Shaped.of(jebWoolKey)
        .shape(
        "AAA",
        "A A",
        "AAA") // 一个 A 表示一种原料，此处的三个字符串摆出来一个配方的样子，空格表示空气。
        .setIngredient('A', materialChoice(Material.BLACK_WOOL, Material.WHITE_WOOL)) // 定义 A 是什么
        .build()
```

## Shapeless 无序配方

与有序配方相反，无序配方不关心你如何摆放物品，只关心有什么材料。

例子:

```java
Shapeless.of(KEY_SHAPELESS, null)
        .addIngredients(new MaterialChoice(Material.PAPER))
        .addIngredients(new MaterialChoice(Material.GUNPOWDER))
```

## Brewing 酿造配方

目前该配方尚未被原版配方注入(`inject-vanilla-craft`)功能支持。

构造参数中最后一个 `IngredientChoice target` 针对药水槽。  
由于药水配方的特殊性，目前 AstralFlow 还没有提供针对药水的 `Choice`
以供判断和消耗原料，请自行根据需求编写或者[发 PR](https://github.com/InlinedLambdas/AstralFlow/pulls)

此外，对于药水的合成配方，它对应的 `IngredientChoice` 应该产出结果而不是单纯的消耗。

所有的药水瓶都会共用一个 `IngredientChoice`，请注意。

例子：

```java
Brewing.of(KEY_BREWING,astralItemChoice(...), potionChoice(....))
```

## Smelting 熔炉配方

目前该配方尚未被原版配方注入(`inject-vanilla-craft`)功能支持。

例子：

```java
Smelting.of(KEY_SMELTING,materialChoice(COAL),materialChoice(ROTTEN_FLESH)); // 典型腐肉烧皮革。。
```