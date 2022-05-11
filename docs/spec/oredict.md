# 矿物辞典

AstralFlow 中的矿物辞典类似于 [Tag](https://mcforge.readthedocs.io/en/1.18.x/resources/server/tags/)，通过一个 Key 可以匹配到多个物品上。

例如：stone -> 各种石头 oreCopper -> 各种扩展的铜矿和原版的铜矿

辞典可以注册很多物品进去，包括 AstralFlow 的特殊物品。但是在 `AstralExtension` 的 `init` 阶段过后就会被锁定。

# 命名规范

参chao考xi自 [Forge Documentation](https://mcforge-ko.readthedocs.io/zh/latest/utilities/oredictionary/)

> 由于辞典名称需要在不同Mod间共享，它们应该是比较统一的。请使用其它拓展可能会使用的名称。

AstralFlow 并没有规定名称需要有某种特定的格式，但下面这些规则现在已经是比较流行的标准了：

整个辞典通常使用驼峰命名法(camelCase，使用小写字母开头，接下来的单词首字母大写的复合词)，并且不要使用空格以及下划线。

辞典名称的第一个单词应该指明物品的类型。对于那些特殊的物品（比如record，dirt，egg，vine），一个单词就足够了，不用指明物品类型。

名称的最后一部分应当指明物品的材料。这将区分开ingotIron与ingotGold。

如果两个单词还是不够详细，你也可以加上第三个单词。比如说，花岗岩被注册为blockGranite，而磨制花岗岩被注册为blockGranitePolished。

如果想要一份通用前缀以及后缀的列表，见通用名称。

# 通用名称

还是参chao考xi自 [Forge Documentation](https://mcforge-ko.readthedocs.io/zh/latest/utilities/oredictionary/)

原版Minecraft物品以及方块的辞典名称可以在下方中找到。

已经使用的通用前缀包括 `ore`，`ingot`，`nugget`

已经使用的通用后缀包括 Wood，Glass，Iron，Gold，Leaves，和 Brick。

# 现有 Minecraft 辞典名称

我觉得你还是去看 [VanillaOreDict](https://github.com/InlinedLambdas/AstralFlow/blob/main/src/main/java/io/ib67/astralflow/item/oredict/internal/VanillaOreDict.java)
比较好  
如果你想在配方中运用 Minecraft 自带的 Tag 系统支持，请使用 `TagChoice`