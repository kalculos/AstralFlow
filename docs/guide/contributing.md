星流是开源而免费的，它存活在维护者的空余时间里，新人的参与总是喜闻乐见的。

这篇指南将会提及一些贡献星流的注意事项，等等。

# 如何贡献

星流的代码在 GitHub 上维护，你可以点击文档那个右上角的猫进入项目主页。

在提交贡献之前，你需要先登录 GitHub，并且 Fork 星流的仓库，切换到一个新分支后再在本地提交更改。

如果你不熟悉 Git 操作，你也可以直接用 GitHub 提供的网页端编辑器 或者是 SmartGit 之类的软件辅助你进行提交（当然这一条仅针对翻译和文档的贡献者）。

## 提交问题

只需在 [GitHub Issues](https://github.com/InlinedLambdas/AstralFlow/issues) 处点击 `New Issue` 即可开始提交问题。

> 在提交问题之前，请先用中英文搜索是否有类似的问题存在。

## 提交文档

只需修改 `docs/` 内的对应文件即可，你可以在 `_sidebar.md` 上看到侧边栏菜单对应的文件位置。

文档使用 `Markdown` 格式描述，如果你还不会你应该先去学一学。

文档内的引用统统从根目录开始引用。比如 `[配置文件](./user_guide/configuration.md)`。

修改完毕之后，只需要从你的分支发起 [Pull Request](https://github.com/InlinedLambdas/AstralFlow/pulls) 即可。

## 提交翻译

翻译的提交比文档轻松很多，只需要修改 `translations/语言代号.conf` 即可。

如果没有的话，可以复制一份别人的然后改名开始翻译，不过你最好选择最近更新过的而且是你熟悉的语言。

一般来说，`zh_CN` 不包含在 `translations`
里，它是被硬编码到 [Language](https://github.com/InlinedLambdas/AstralFlow/blob/main/src/main/java/io/ib67/astralflow/internal/config/Language.java)
的，在熟悉 Java 基础语法的情况下也可以尝试进行编辑，但最好还是发 issue....

修改完毕之后，只需要从你的分支发起 [Pull Request](https://github.com/InlinedLambdas/AstralFlow/pulls) 即可。

# 贡献代码

AstralFlow 使用 Java 编写，为了提交代码，你需要先学会写 Java。

> 类似 Scala, Kotlin 的一样在 JVM 上运行语言，如果本身没有被模块引入（未来可能有），那么请不要使用它们，我们总是使用 Java。

## Setup

部署过程并不复杂，只需要在 IDE 内 clone 下来项目然后打开便是。

**需要注意的是， AstralFlow 使用了 `lombok`，你需要给 IDE 安装 Lombok 插件支持，否则可能会遇到奇怪的问题。**

一旦 Gradle 准备完毕，你就可以开始贡献了。

## Guideline

在 [SaltedFish Club - 代码贡献规范](https://github.com/saltedfishclub/documents/blob/main/CONTRIBUTING.md) 的基础上，我们另外加了几条规则：

- 凡是新功能的引入，重构，和某个 issue 有关的一系列提交，**必须**在新的分支上进行。

新分支的命名格式要求**必须**能够看得出来是哪个 issue. 例如: `issue-156-do-xx`

- 发 PR 时，**必须** 在标题简短一句话写明白你做了什么的摘要，接着在描述里写你具体做了哪些事情，和哪些 issue 相关联。（比如 `This resolves #111`）
- 引入的新功能/新模块需要一同承包对应的单元测试。
- 提交前，我们推荐你在本地运行一次 Gradle 的 build，好提前发现所有爆炸的单元测试。

## 注意事项

- Javadoc 生成的时候会忽略掉所有文件夹路径上有 `impl` 或者 `internal` 的文件。
- 请不要随便暴露可见性，总是采用 Getter 和 `private`。
- 请尝试编写 [不可变](https://www.cnblogs.com/shamo89/p/10330080.html) 的代码。
- 对于公开的 API，请记得加上 `@ApiStatus.AvailableSince(当前版本)`
- 以及给公开的 API 加上 Javadoc (不要漏了`package-info.java`)
- 不公开的部分请放到 `internal` 或者 `impl` 里面，以避免出现在 Javadoc 里。