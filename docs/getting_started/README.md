# 入门

AstralFlow 希望有更多开发者参与它的建设。

## 前置需求

在学习 AstralFlow 之前，您应该确保自己可以较为独立的：

-
    1. **编写 Java 代码。** AstralFlow 由 Java 开发，您至少需要了解基础的 Java 开发知识。  
       *如果您正在使用像是 Kotlin, Scala 一样的在 JVM 上运行的语言，只需确保能看懂教程中的例子即可。*
-
    2. **编写 Bukkit 插件。** AstralFlow 基于 Bukkit 实现，您需要对 Bukkit API 有基本的认知。
       *此项可选。*

准备就绪，那么踏上旅程！

## 导入依赖

AstralFlow 应该作为外部代码库被导入您的项目中。

### 通过主流包管理器

主流的包管理器通常支持 Maven 仓库。

1. Gradle    
   将下列代码添加到 `repositories {...}` 中，这段代码通常出现在 `build.gradle` 文件里。

```groovy
maven {
    url "https://mvn.bukkit.rip/releases"
}
 ```

2. Maven     
   将下列代码添加到 `<repositories> ... </repositories>` 中，这段代码通常出现在 `pom.xml` 里。

 ```xml

<repository>
    <id>Bukkit.RIP Releases</id>
    <name>Bukkit.RIP</name>
    <url>https://mvn.bukkit.rip/releases</url>
</repository>
```

> 注意：`https://mvn.bukkit.rip/releases` 即代表您使用 **正式发布版本**，若您需要使用 **预览开发版本** 请更换为 `https://mvn.bukkit.rip/snapshots`  
所有以 `-RC-X`, `-M-X` 结尾的版本号均为预览版本，关于版本命名请参见: [版本演进](https://github.com/saltedfishclub/documents/blob/main/Evolution.md)

### 通过 IDE 的依赖导入功能

已经有类似的[文章](https://blog.csdn.net/qq_26525215/article/details/53239123)，本文不过赘述。
