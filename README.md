# TEFModLoader 🌍 [English](README-en.md) | [Русский](README-ru.md)

> 下一代高性能跨平台Terraria模组加载器

## 📚 开源组件

| 项目名称                                                             | 用途                                  | 协议         |
|------------------------------------------------------------------|-------------------------------------|------------|
| [Jetpack Compose](https://developer.android.com/jetpack/compose) | Android UI框架                        | Apache 2.0 |
| [EFModLoader](https://github.com/2079541547/EFModLoader)         | TEFModLoader加载器核心                   | AGPL v3.0  |
| [EFMod](https://github.com/2079541547/EFMod)                     | TEFModLoader的Mod设计标准                | Apache 2.0 |
| [BNM-Android](https://github.com/ByNameModding/BNM-Android)      | 在 Android 上通过类、方法、字段名称修改 il2cpp 游戏的库 | MIT        |
| [Dobby](https://github.com/jmpews/Dobby)                         | 一个轻量级、多平台、多架构的漏洞利用钩子框架。             | Apache 2.0 |
| [SilkCasket](https://github.com/2079541547/SilkCasket)           | TEFMod文件格式                          | Apache 2.0 |
| [Axml2xml](https://github.com/developer-krushna/Axml2xml)        | 高级和增强的 axml 编译器                     | -           |
| [axml-parser](https://github.com/ZaratustraN/axml-parser)        | 将axml转换为xml格式                       | -          |
| [xml2axml](https://github.com/hzw1199/xml2axml)                   | 将xml转换为axml格式                       | -          |



## ✨ 核心优势

🔧 **开放架构**
支持C++原生模组开发，未来将扩展多语言生态

⚡ **极致性能**

- 内存占用逼近原版游戏
- 模组运行效率超越TL Pro
- 冷启动时间与原版游戏基本持平

🖥️ **跨平台覆盖**
Android/Windows/Linux三端协同开发，一次开发多端部署

## 🛠 技术架构 |

| 组件         | 技术选型              | 特性                          |
| ------------ | --------------------- | ----------------------------- |
| **核心引擎** | C++20标准             | 自动管理内存                  |
| **UI框架**   | Jetpack Compose + MD3 | 动态主题/交互动画/多语言      |
| **ABI接口**  | C++20稳定接口         | 兼容NDK/Windows SDK/Linux GCC |
| **构建系统** | CMake + Gradle        | 跨平台增量编译                |

## 兼容性

| 系统平台 | arm64 | x86_64 | x86_32 | arm32 |
| -------- | ----- | ------ | ------ | ----- |
| Android  | ✔️  | ⚠️   | ⚠️   | ✔️  |
| Windows  | 🚫    | 🛠️   | 🛠️   | 🚫    |
| Linux    | 🚫    | 🛠️   | 🛠️   | 🚫    |
| iOS      | 🚫    | 🚫     | 🚫     | 🚫    |
| macOS    | 🚫    | 🚫     | 🚫     | 🚫    |

**图标说明：**

- ✔️ 已验证支持
- ⚠️ 已适配未测试
- 🛠️ 开发中
- 🚫 不支持

**平台说明：**

1. Android：
   
   - x86架构完成适配但尚未测试
   - arm架构已通过基础测试
2. Windows：
   
   - x86_32架构正在开发
   - x86_64架构正在开发
   - 全系ARM架构暂不支持
3. Linux：
   
   - 优先考虑Windows
   - x86_32架构正在开发
   - x86_64架构正在开发
   - 全系ARM架构暂不支持
4. Apple生态：
   
   - iOS/macOS因设备限制暂停开发
   - 暂不提供任何架构支持

## 📊 性能表现（预研阶段）

### 实测环境对比

| 设备型号  | 处理器架构    | 内存配置 | 运行表现          |
| --------- | ------------- | -------- | ----------------- |
| vivo Y33s | MT6833        | 6+2GB    | 稳定60fps，零卡顿 |
| 荣耀9i    | Mali-T830 MP2 | 4GB      | 60fps，肉眼无差异 |

*当前测试基于小型模组包（<5MB），因社区生态尚未成熟，暂未进行大型模组压力测试*

### 理论性能对比

TL Pro使用的就是V8引擎，理论上优于该方案
与JavaScript方案(V8引擎)的预期差异：

| 性能维度       | C++原生方案优势             | JS JIT方案局限         |
| -------------- | --------------------------- | ---------------------- |
| 内存开销       | 仅增加原生库加载内存(≈3MB) | 需承载V8运行时(≈35MB) |
| 计算密集型任务 | 直接调用CPU指令集           | 存在JIT编译开销        |
| 冷启动速度     | 无运行时初始化环节          | 需预热V8引擎(≈800ms)  |

## 🛠 开发&构建指南

### 环境要求

- **推荐系统**: Debian
- **跨平台构建工具链**:
  
  ```bash
  Gradle-8.11.1
  
  # Android
  export ANDROID_MIN_SDK=24
  export ANDROID_MAX_SDK=35
  export AGP_VERSION=8.6.1
  export NDK_VERSION=28.0.12916984
  
  # Windows
  - Visual Studio 2022 17.8+ 
  - CMake 3.29+ with Ninja
  
  # Linux
   -Clang
   -Cmake
   -Mingw
  ```

### Mod开发

- [API]()
- [基础功能]()

