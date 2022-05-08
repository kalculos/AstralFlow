# 入门

AstralFlow 希望有更多开发者参与它的建设。

## 前置需求

在学习 AstralFlow 之前，您应该确保自己可以较为独立的：

-
    1. **编写 Java 代码。** AstralFlow 使用 Java 开发，您至少需要了解基础的 Java 开发知识。  
       *如果您正在使用像是 Kotlin, Scala 一样的在 JVM 上运行的语言，只需确保能看懂教程中的例子即可。*

-
    2. **编写 Bukkit 插件。** AstralFlow 基于 Bukkit 实现，您需要对 Bukkit API 有基本的认知。

准备就绪，那么踏上旅程！

## 导入依赖

AstralFlow 应该作为外部代码库被导入您的插件项目中，本文假设你已经准备好了一个空插件环境用于开发扩展。

### 通过主流包管理器

主流的包管理器通常支持 Maven 仓库。

1. Gradle    
   将下列代码添加到 `build.gradle` 文件里。

```groovy
repositories {
    maven {
        url "https://mvn.bukkit.rip/releases"
    }
}

dependencies {
    compileOnly group: "io.ib67", name: "astralflow", version: "0.1.0"
}
 ```

2. Maven     
   将下列代码添加到 `pom.xml` 里。

 ```xml

<repositories>
    <repository>
        <id>Bukkit.RIP Releases</id>
        <name>Bukkit.RIP</name>
        <url>https://mvn.bukkit.rip/releases</url>
    </repository>
</repositories>
```

```xml

<dependencies>
    <dependency>
        <groupId>io.ib67</groupId>
        <artifactId>astralflow</artifactId>
        <version>0.1.0</version>
    </dependency>
</dependencies>
```

> 注意：`https://mvn.bukkit.rip/releases` 即代表您使用 **正式发布版本**，若您需要使用 **预览开发版本** 请更换为 `https://mvn.bukkit.rip/snapshots`  
> 所有以 `-RC-X`, `-M-X` 结尾的版本号均为预览版本，关于版本命名请参见: [版本演进](https://github.com/saltedfishclub/documents/blob/main/Evolution.md)

### 通过 IDE 的依赖导入功能

您可以直接在 Inlined Lambdas 的仓库中下载到 AstralFlow 作为依赖，只需在 https://mvn.bukkit.rip/releases 搜寻片刻即可。  
对于如何通过 IDE 自带依赖管理功能导入，已经有类似的[文章](https://blog.csdn.net/qq_26525215/article/details/53239123)，本文不过赘述。

## 添加依赖

为了使 AstralFlow 在您的扩展加载之前运行必要的初始化工作，请将 AstralFlow 添加到依赖列表中。

```yml
depend: [ AstralFlow ]
```

接下來，尝试在 IDE 中补全 AstralFlow ，若出现 `io.ib67.astralflow.AstralFlow` 则表示成功。