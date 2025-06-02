## 模块代码实现 🖥️

```c++
#include "efmod_core.hpp" // EFMod核心接口头文件

// 自定义模块类，继承EFMod基类
class MyMod final : public EFMod {
public:
    // 模块初始化函数
    int Initialize(const std::string &path, MultiChannel *multiChannel) override { 
        return 0; // 返回0表示成功
    }

    // 发送功能函数
    void Send(const std::string &path, MultiChannel *multiChannel) override {
        // 向多通道注册"Hello函数"
        multiChannel->send("Hello函数", (void*)hello);
    }

    // 接收功能函数
    void Receive(const std::string &path, MultiChannel *multiChannel) override {
        // 调用远程求和函数并打印结果
        const int result = multiChannel->receive<int(*)(int, int)>("sum_function")(10, 20);
        std::cout << "Sum result: " << result << std::endl;
    }

    // 模块加载函数
    int Load(const std::string &path, MultiChannel *multiChannel) override { 
        return 0; // 返回0表示成功
    }
    
    // 模块卸载函数
    int UnLoad(const std::string &path, MultiChannel *multiChannel) override { 
        return 0; // 返回0表示成功
    }

    // 获取模块元数据
    Metadata GetMetadata() override {
        return {
            "MyMod",                // 模块名称
            "EternalFuture゙",      // 作者名称
            "1.0.0",               // 版本号
            20250509,              // 构建日期
            ModuleType::Library,   // 模块类型(库)
            {
                false             // 调用 Initialize (主线程)
            }
        };
    };
};

// 模块创建函数(由EFModLoader调用)
EFMod *CreateMod() {
    static MyMod Mod;  // 静态模块实例
    return &Mod;       // 返回模块指针
}
```

## 模块目录结构 📂

```
my_efmod/                  # 模块根目录
├── lib/                   # 平台相关库文件目录
│   │── android/           # Android平台库
│   │    ├── arm64-v8a/    # 64位ARM架构(现代设备)
│   │    │   └── libmod.so # 核心模块动态库
│   │    ├── armeabi-v7a/  # 32位ARM架构(旧设备)
│   │    ├── x86_64/       # 64位x86架构(模拟器/少数设备)
│   │    └── x86/          # 32位x86架构(模拟器)
│   │── windows/           # Windows平台库
│   │    ├── arm64/        # 64位ARM架构(少数设备)
│   │    ├── arm/          # 32位ARM架构(少数设备)
│   │    ├── x64/          # 64位x86架构(主流设备)
│   │    └── x86/          # 32位x86架构(旧设备)  
│   │── linux/             # Linux平台库
│   │   ├── ...            # 类似结构
│   │── ios/               # iOS平台库
│   │   ├── ...            # 类似结构
│   │── mac/               # macOS平台库
│       ├── ...            # 类似结构    
│
├── efmod.icon             # 模块图标文件(推荐64x64 PNG)
└── efmod.toml             # 模块配置文件(TOML格式)
```

## 模块配置文件 ⚙️

```toml
# 模块基本信息
[info]
name = "MyMod"            # 模块名称(必填)
author = "EternalFuture゙" # 作者名称(必填)
version = "1.0.0"         # 版本号(语义化版本,必填)

# GitHub相关信息(可选)
[github]
open_source = true        # 是否开源(默认false)
overview = "https://gitlab.com/2079541547" # 作者主页
url = ""                  # 开源仓库URL(如果开源)

# 平台兼容性配置(必填)
[platform.windows]
arm64 = false             # Windows ARM64支持
arm32 = false             # Windows ARM32支持
x86_64 = false            # Windows x64支持
x86 = false               # Windows x86支持

[platform.android]
arm64 = true              # Android ARM64支持
arm32 = true              # Android ARM32支持
x86_64 = false            # Android x64支持
x86 = false               # Android x86支持

# 多语言介绍文案(至少提供一种语言)
[introduce]
zh-cn = "你的物品被超频了，它比以前更快！" # 简体中文
zh-hant = "你的物品被超頻了，它比以前更快！" # 繁体中文
en = "Your items have been overclocked, they are faster than ever!" # 英文
ja = "あなたのアイテムはオーバークロックされ、以前よりも速くなりました！" # 日语
ko = "당신의 아이템이 오버클럭되어 이전보다 더 빨라졌습니다!" # 韩语
it = "I tuoi oggetti sono stati overcloccati, sono più veloci che mai!" # 意大利语
es = "¡Tus objetos han sido overclockeados, son más rápidos que nunca!" # 西班牙语
fr = "Vos objets ont été overclockés, ils sont plus rapides que jamais !" # 法语
de = "Deine Gegenstände wurden übertaktet, sie sind schneller als je zuvor!" # 德语

# 加载器依赖配置(可选)
[[loaders]]
name = "TEFModLoader-EternalFuture゙" # 加载器名称-作者
supported_versions = ["20250316"]   # 兼容的加载器版本列表

# 模块标准配置
[mod]
standards = 20250316    # 遵循的EFMod标准版本
modx = false            # 是否独立运行(不依赖加载器)
```

## 开发说明 📝

1. **跨平台支持** 🌍
    - 模块需要为每个支持的平台提供对应的二进制库文件
    - 在`efmod.toml`中明确声明支持的平台和架构

2. **版本管理** 🔖
    - 使用语义化版本控制(MAJOR.MINOR.PATCH)
    - 每次更新都需要同步修改版本号和构建日期

3. **多语言支持** 🈯
    - 至少提供简体中文和英文介绍
    - 支持的语言越多，模块的国际化程度越高

4. **兼容性声明** ⚠️
    - 明确声明兼容的加载器版本
    - 如果模块独立运行(modx=true)，则不需要加载器

5. **图标规范** 🖼️
    - 推荐使用64x64像素的PNG格式
    - 确保图标在不同背景下都能清晰可见

## 最佳实践 ✅

1. **模块初始化**
    - 在`Initialize()`中进行必须在主线程的操作
    - 返回非零值表示初始化失败

2. **多通道通信** 📡
    - 使用标准化的函数命名规范
    - 确保发送和接收的数据类型匹配

3. **错误处理** ❌
    - 所有接口都应返回状态码
    - 提供详细的错误日志

4. **资源管理** 💾
    - 在`Load()`中加载外部资源
    - 在`UnLoad()`中释放所有资源

5. **性能优化** ⚡
    - 避免在频繁调用的函数中进行内存分配
    - 使用缓存机制提高性能

通过遵循这些规范并且遵循对应efmodloader的efmod规范，您可以开发出高质量、可维护且兼容性强的EFMod模块！🎉