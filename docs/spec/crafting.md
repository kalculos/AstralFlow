AstralFlow 有一套自己完全重写的合成系统，并且支持注入原版合成台。

关于开启注入原版合成台，请参考 [配置文件](./user_guide/configuration.md)

# RecipeRegistry

AstralFlow
中的合成系统主要由 [IRecipeRegistry](https://github.com/InlinedLambdas/AstralFlow/blob/main/src/main/java/io/ib67/astralflow/item/recipe/IRecipeRegistry.java)
管理，通过 `AstralFlowAPI` 可以得到一个默认 `RecipeRegistry` 示例。

```java
flow.getRecipeRegistry();
```