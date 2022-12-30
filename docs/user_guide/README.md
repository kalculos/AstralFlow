这一篇指南包含了如何安装插件以及版本类型等注意事项。

# 下载插件

在安装之前，我们首先需要下载到可用的星流。  
一般来说，星流的维护者们会在以下渠道发布更新:

- [GitHub Releases](https://github.com/InlinedLambdas/AstralFlow/releases)

以及插件本身出于安全考虑也会进行自动更新。
> 若要关闭自动更新，请详见 [配置文件](./user_guide/configuration.md)

在一般情况下，我们总是推荐您使用最新的 Spigot/Paper 来配合最新的星流使用。若您正好正在使用最新的 Spigot/Paper，那么您可以直接在 Release 上下载最新的星流。
> 在 Github Release 上下载星流时，请选择正式发布版本 ，即标题右边是绿色的 *Release*

> 若您正巧没有在使用最新的 Spigot/Paper，请参见 [支持的版本列表](./user_guide/maintained_versions.md)

## 版本类型

星流具有三种版本类型，并且会在版本名字内标出。  
它们分别为： `Milestone / 里程碑`，`Release Candidate / 预览` 和 `Release / 正式发布`。如果您不知道您应该选择哪种，请总是选择 `Release` 版。  
另外，`Release` 版本没有 `-RC` `-M` 作为后缀，请注意这一点。

> 在开发中，维护着以 -M1 ，-M2 等后缀发布里程碑版本，此类版本标志一系列功能的完成，并且里程碑版本内可能会引入新的 API 变更，并不稳定。  
> 在大版本即将发布前，Maintainer 会以 -RC 版本后缀发布最终的预览版本，表示新版本 API 已经确定，离稳定版发布只差最后的一些内部优化或 bug 修复。
>
>  -- [版本演进策略 from SaltedFish Club](https://github.com/saltedfishclub/documents/blob/main/Evolution.md)

## 安装插件

像大多数的 Spigot 插件一样，只需要把星流和需要的拓展拖入插件目录即可。在重启服务器后，插件会自动加载。

加载成功后，插件将会在控制台输出构建信息和它的 logo ，以及在插件目录下生成对应的数据文件夹。

接下来：[配置插件](./user_guide/configuration.md)