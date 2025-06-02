# EFModLoader 开发文档 🚀

## 概述 🌟

EFModLoader 是一个高性能、跨平台的模块加载系统，专为游戏和应用程序的模块化架构设计。它支持动态加载、初始化和通信功能，同时提供完善的日志系统和错误处理机制。

## 核心功能 💪

- **跨平台支持**：兼容 Windows、Linux、Android 等多种平台
- **动态加载**：支持运行时加载和卸载模块
- **模块通信**：提供多通道通信机制
- **异步操作**：支持所有核心功能的异步版本
- **批量操作**：支持批量加载、初始化和卸载模块
- **线程安全**：内置互斥锁保护共享资源


## 加载器目录结构 📂

```
efmodloader/              # 加载器根目录
├── lib/                  # 平台相关库文件目录
│   │── android/          # Android平台库
│   │    ├── arm64-v8a/   # 64位ARM架构
│   │    │   └── libTEFModLoader.so # 加载器动态库
│   │    ├── armeabi-v7a/ # 32位ARM架构
│   │    ├── x86_64/      # 64位x86架构
│   │    └── x86/         # 32位x86架构
│   │── windows/          # Windows平台库
│   │    ├── arm64/       # ARM64架构
│   │    ├── arm/         # ARM32架构
│   │    ├── x64/         # x64架构
│   │    └── x86/         # x86架构
│   │── linux/            # Linux平台库
│   │── ios/              # iOS平台库
│   └── mac/              # macOS平台库
│
├── efmodloader.icon      # 加载器图标(64x64 PNG)
└── efmodloader.toml      # 加载器配置文件
```

## 加载器配置文件 ⚙️

```toml
# 加载器基本信息
[info]
name = "TEFModLoader"      # 加载器名称(必填)
author = "EternalFuture゙" # 作者名称(必填)
version = "20250316"       # 版本号(必填)

# GitHub信息
[github]
overview = "https://github.com/2079541547" # 作者主页
url = "https://github.com/2079541547/TEFModLoader" # 项目地址

# 平台兼容性配置
[platform.windows]
arm64 = false  # Windows ARM64支持
arm32 = false  # Windows ARM32支持
x86_64 = false # Windows x64支持
x86 = false    # Windows x86支持

[platform.android]
arm64 = true   # Android ARM64支持
arm32 = true   # Android ARM32支持
x86_64 = false # Android x64支持
x86 = false    # Android x86支持

# 加载器配置
[loader]
lib_name = "TEFModLoader" # 库文件基础名称(不含前缀和后缀)

# 各平台支持的工作模式
[loader.support_mode.android]
inline = false  # 不支持内联模式
external = true # 支持外部加载模式
root = false    # 不支持root模式
share = true    # 支持共享库模式

[loader.support_mode.windows]
hijack = false  # 不支持劫持模式

# 兼容性配置
[compatible]
supported_versions = ["20250316"] # 兼容的旧版本
minimum_standards = 20250316      # 最低兼容的Mod标准
highest_standards = 20250316      # 最高兼容的Mod标准

# 多语言介绍
[introduce]
zh-cn = "官方轻便加载器"
zh-hant = "官方輕便加載器"
en = "Official Lightweight Loader"
ja = "公式の軽量ローダー"
ko = "공식 경량 로더"
it = "Loader Leggero Ufficiale"
es = "Cargador Liviano Oficial"
fr = "Chargeur Léger Officiel"
de = "Offizieller Leichtgewichts-Loader"
```

## 代码示例 🧑‍💻

### 1. 基本使用示例

```c++
// 自定义加载函数
void* MyLoadFunc(const std::string& path) {
    #ifdef _WIN32
        return LoadLibraryA(path.c_str());
    #else
        return dlopen(path.c_str(), RTLD_LAZY | RTLD_LOCAL);
    #endif
}

// 自定义卸载函数
void MyUnloadFunc(void* handle) {
    #ifdef _WIN32
        FreeLibrary((HMODULE)handle);
    #else
        dlclose(handle);
    #endif
}

// 自定义符号查找函数
void* MySymbolFunc(void* handle, const std::string& name) {
    #ifdef _WIN32
        return (void*)GetProcAddress((HMODULE)handle, name.c_str());
    #else
        return dlsym(handle, name.c_str());
    #endif
}

int main() {
    // 创建加载器实例
    EFModLoader::Loader loader(MyLoadFunc, MyUnloadFunc, MySymbolFunc);
    
    // 加载模块
    std::string modId = loader.load("./libtest_mod.so", "data/module1");
    
    // 初始化模块
    if(loader.initialize(modId)) {
        // 创建通信通道
        auto channel = EFModLoader::LoaderMultiChannel::GetInstance();
        
        // 与模块交互
        loader.send(modId, channel);
        loader.receive(modId, channel);
    }
    
    // 卸载模块
    loader.unload(modId);
    return 0;
}
```

### 2. 异步操作示例

```c++
// 异步加载和初始化模块
auto future = loader.loadAsync("./libasync_mod.so", "data/async_mod")
    .then([&loader](std::string modId) {
        return loader.initializeAsync(modId);
    })
    .then([&loader](bool initResult) {
        if(initResult) {
            // 初始化成功后的操作
        }
    });

// 等待异步操作完成
future.wait();
```

### 3. 批量操作示例

```c++
// 准备批量加载的模块列表
std::unordered_map<std::string, std::string> modMap = {
    {"./mod1.so", "data/mod1"},
    {"./mod2.so", "data/mod2"},
    {"./mod3.so", "data/mod3"}
};

// 批量加载
auto loadedMods = loader.loadBatch(modMap);

// 批量初始化
auto initializedMods = loader.initializeBatch(loadedMods);

// 批量卸载
loader.unloadBatch(initializedMods);
```

## 核心类详解 🧐

### `Loader` 类

#### 构造函数
```c++
explicit Loader(LoadFunc loadFunc, UnloadFunc unloadFunc, SymbolFunc symbolFunc);
```
- `loadFunc`: 自定义库加载函数
- `unloadFunc`: 自定义库卸载函数
- `symbolFunc`: 自定义符号查找函数

#### 同步操作
- `load()`: 同步加载单个模块
- `loadBatch()`: 同步批量加载模块
- `initialize()`: 同步初始化单个模块
- `initializeBatch()`: 同步批量初始化模块
- `send()`: 同步发送数据到模块
- `receive()`: 同步从模块接收数据
- `unload()`: 同步卸载单个模块
- `unloadBatch()`: 同步批量卸载模块

#### 异步操作
- `loadAsync()`: 异步加载单个模块
- `loadBatchAsync()`: 异步批量加载模块
- `initializeAsync()`: 异步初始化单个模块
- `initializeBatchAsync()`: 异步批量初始化模块
- `sendAsync()`: 异步发送数据到模块
- `receiveAsync()`: 异步从模块接收数据
- `unloadAsync()`: 异步卸载单个模块
- `unloadBatchAsync()`: 异步批量卸载模块

#### 全局操作
- `loadAll()`: 加载所有模块
- `initializeAll()`: 初始化所有模块
- `sendAll()`: 向所有模块发送数据
- `receiveAll()`: 从所有模块接收数据
- `unloadAll()`: 卸载所有模块

#### 辅助函数
- `getLoadedModules()`: 获取已加载模块列表
- `getMetadata()`: 获取模块元数据
- `getInstance()`: 获取模块实例指针

### `ModuleHandle` 结构体
内部用于管理模块状态：
- `handle`: 模块句柄
- `path`: 模块路径
- `private_path`: 模块私有数据路径
- `instance`: 模块实例指针
- `metadata`: 模块元数据
- `initialized`: 初始化状态(原子变量)
- `in_use`: 使用中标志(原子变量)

## 最佳实践 🏆

1. **错误处理**：始终检查模块加载和初始化的返回值
2. **资源管理**：确保在程序退出前卸载所有模块
3. **线程安全**：在多线程环境中使用适当的同步机制
4. **性能优化**：批量加载相关模块减少IO开销
5. **异步优先**：在可能的情况下使用异步版本的操作
6. **模块隔离**：为每个模块提供独立的私有数据路径
7. **日志记录**：记录关键操作以便调试和故障排除

## 兼容性说明 ℹ️

EFModLoader 支持以下平台和架构：

| 平台      | x86_64 | x86 | arm64 | arm32 |
|---------|--------|-----|------|-------|
| Windows | ✔      | ✔   | ✔    | ✔     |
| Linux   | ✔      | ✔   | ✔    | ✔     |
| Android | ✔      | ✔   | ✔    | ✔     |
| macOS   | ✔      | ✔   | ✖     | ✖     |
| IOS     | ✖      | ✖   | ✔     | ✔     |

## 常见问题解答 ❓

**Q: 模块加载失败的可能原因有哪些？**
A:
- 模块文件路径不正确
- 缺少依赖库
- 架构不兼容
- 权限不足

**Q: 如何实现模块间的安全通信？**
A:
- 使用类型安全的函数注册和调用
- 实现消息验证机制
- 限制模块间的直接内存访问

**Q: 支持热更新吗？**
A: 是的，可以通过先卸载再重新加载模块的方式实现热更新，但需要注意状态保存和恢复的问题。

通过遵循这些规范，您可以构建出高效、可维护的模块化应用程序！🎉