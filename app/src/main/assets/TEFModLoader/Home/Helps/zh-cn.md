# 目录
- [简介](#introduction)
  - [Mod问题](#mod)
    - [Mod没有效果？](#modinvalid)
    - [Mod加载时闪退？](#modflashretreat)
    - [Mod不显示图标信息？](#modnoiconorinformation)
    - [Mod解压时闪退？](#modflashbackduringdecompression)
    - [Mod运行中闪退？](#modruntimecrash)
    - [Mod解压时出现空指针？](#emptypointerappearsduringmoddecompression)

## 简介 <a id="introduction"></a>
* 请确保你有正常的理解能力和正常的视力并且不会在一棵树上吊死，这是阅读此文档的最低要求，请不要跟个小唐人一样到处问这个问那个，实在不行去挂个脑科

## Mod问题 <a id="mod"></a>

## Mod没有效果？<a id="modinvalid"></a>
* 请确保内核已注入进游戏，且游戏拥有读取/写入权限。如果是安卓13以下可以尝试使用共享模式，你需要在游戏声明文件中添加 `android:sharedUserId="silkways.terraria.efmodloader"`，注意：此工作模式的游戏可以不需要权限即可。

## Mod加载时闪退？<a id="modflashretreat"></a>
* 查看日志，一般是由于Mod使用了过时API导致的，如果是独立Mod就可能是依赖或者其他问题。如果都不是可以在Github上发问题以及详细的日志文件

## Mod不显示图标信息？<a id="modnoiconorinformation"></a>
* 一般是由于不支持文件或者压根不是EFMod。也可能是因为Mod使用的文件系统版本较为落后或者高于当前管理器文件系统的版本，导致管理器的文件系统无法正常解析

## Mod解压时闪退？<a id="modflashbackduringdecompression"></a>
* 导致的方面很多，如：Mod使用了非标准或已废弃甚至领先的压缩模式，您应该联系Mod作者

## Mod运行中闪退？<a id="modruntimecrash"></a>
* 这个很难说，你应该提供日志文件，如果你能看懂的话可以尝试查找是内核的bug还是mod的bug，然后找对应的开发者解决

## Mod解压时出现空指针？<a id="emptypointerappearsduringmoddecompression"></a>
* 大概率是因为你没有给TEFModLoader管理文件权限，如果不是请联系开发者并提供日志文件
