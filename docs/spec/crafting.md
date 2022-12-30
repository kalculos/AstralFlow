AstralFlow 有一套自己完全重写的合成系统，并且支持注入原版合成台。

关于开启注入原版合成台，请参考 [配置文件](./user_guide/configuration.md)

# RecipeRegistry

AstralFlow
中的合成系统主要由 [IRecipeRegistry](https://flow.bukkit.rip/javadoc/io/ib67/astralflow/item/recipe/IRecipeRegistry.html)
管理，通过 `AstralFlowAPI` 可以得到一个默认 `RecipeRegistry` 示例。

```java
flow.getRecipeRegistry();
```

接着你就可以通过它来匹配配方和注册配方，做一个自己的工作台。

## ItemMatrix

由于各类配方的物品矩阵结构不同，因此向 `matchRecipe` 传入物品的时候需要按照规矩。    
对此，AstralFlow 提供了一个 [ItemMatrix](https://flow.bukkit.rip/javadoc/io/ib67/astralflow/item/recipe/ItemMatrix.html)
类，可以帮助你构建一个矩阵。

```java
ItemMatrix.of(...any...machine...inventory...);
```