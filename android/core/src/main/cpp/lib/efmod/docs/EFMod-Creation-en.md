#### **Module Code Implementation 🖥️**

```c++
#include "efmod_core.hpp" // EFMod core interface header

// Custom module class inheriting from EFMod base class
class MyMod final : public EFMod {
public:
    // Module initialization function
    int Initialize(const std::string &path, MultiChannel *multiChannel) override { 
        return 0; // Return 0 for success
    }

    // Send function
    void Send(const std::string &path, MultiChannel *multiChannel) override {
        // Register "Hello function" with multi-channel
        multiChannel->send("Hello function", (void*)hello);
    }

    // Receive function
    void Receive(const std::string &path, MultiChannel *multiChannel) override {
        // Call remote sum function and print result
        const int result = multiChannel->receive<int(*)(int, int)>("sum_function")(10, 20);
        std::cout << "Sum result: " << result << std::endl;
    }

    // Module load function
    int Load(const std::string &path, MultiChannel *multiChannel) override { 
        return 0; // Return 0 for success
    }
    
    // Module unload function
    int UnLoad(const std::string &path, MultiChannel *multiChannel) override { 
        return 0; // Return 0 for success
    }

    // Get module metadata
    Metadata GetMetadata() override {
        return {
            "MyMod",                // Module name
            "EternalFuture゙",      // Author name
            "1.0.0",               // Version number
            20250509,              // Build date
            ModuleType::Library,   // Module type (Library)
            {
                false             // Call Initialize (main thread)
            }
        };
    };
};

// Module creation function (called by EFModLoader)
EFMod *CreateMod() {
    static MyMod Mod;  // Static module instance
    return &Mod;       // Return module pointer
}
```

#### **Module Directory Structure 📂**

```
my_efmod/                  # Module root directory
├── lib/                   # Platform-specific libraries
│   │── android/           # Android platform
│   │    ├── arm64-v8a/    # 64-bit ARM (modern devices)
│   │    │   └── libmod.so # Core module library
│   │    ├── armeabi-v7a/  # 32-bit ARM (older devices)
│   │    ├── x86_64/       # 64-bit x86 (emulators/few devices)
│   │    └── x86/          # 32-bit x86 (emulators)
│   │── windows/           # Windows platform
│   │    ├── arm64/        # 64-bit ARM (few devices)
│   │    ├── arm/          # 32-bit ARM (few devices)
│   │    ├── x64/          # 64-bit x86 (mainstream)
│   │    └── x86/          # 32-bit x86 (legacy)  
│   │── linux/             # Linux platform
│   │   ├── ...            # Similar structure
│   │── ios/               # iOS platform
│   │   ├── ...            # Similar structure
│   │── mac/               # macOS platform
│       ├── ...            # Similar structure    
│
├── efmod.icon             # Module icon (64x64 PNG recommended)
└── efmod.toml             # Module config (TOML format)
```

#### **Module Configuration File ⚙️**

```toml
# Basic module info
[info]
name = "MyMod"            # Module name (required)
author = "EternalFuture゙" # Author (required)
version = "1.0.0"         # Version (semver, required)

# GitHub info (optional)
[github]
open_source = true        # Is open-source? (default false)
overview = "https://gitlab.com/2079541547" # Author homepage
url = ""                  # Repository URL (if open-source)

# Platform compatibility (required)
[platform.windows]
arm64 = false             # Windows ARM64 support
arm32 = false             # Windows ARM32 support
x86_64 = false            # Windows x64 support
x86 = false               # Windows x86 support

[platform.android]
arm64 = true              # Android ARM64 support
arm32 = true              # Android ARM32 support
x86_64 = false            # Android x64 support
x86 = false               # Android x86 support

# Localized descriptions (at least one required)
[introduce]
zh-cn = "你的物品被超频了，它比以前更快！" # Simplified Chinese
zh-hant = "你的物品被超頻了，它比以前更快！" # Traditional Chinese
en = "Your items have been overclocked, they are faster than ever!" # English
ja = "あなたのアイテムはオーバークロックされ、以前よりも速くなりました！" # Japanese
ko = "당신의 아이템이 오버클럭되어 이전보다 더 빨라졌습니다!" # Korean
it = "I tuoi oggetti sono stati overcloccati, sono più veloci che mai!" # Italian
es = "¡Tus objetos han sido overclockeados, son más rápidos que nunca!" # Spanish
fr = "Vos objets ont été overclockés, ils sont plus rapides que jamais !" # French
de = "Deine Gegenstände wurden übertaktet, sie sind schneller als je zuvor!" # German

# Loader dependencies (optional)
[[loaders]]
name = "TEFModLoader-EternalFuture゙" # Loader name-author
supported_versions = ["20250316"]   # Compatible loader versions

# Module standards
[mod]
standards = 20250316    # EFMod standard version
modx = false            # Standalone operation (no loader)
```

#### **Development Notes 📝**

1. **Cross-Platform Support** 🌍
    - Provide binaries for each supported platform
    - Explicitly declare supported platforms in `efmod.toml`

2. **Version Management** 🔖
    - Use semantic versioning (MAJOR.MINOR.PATCH)
    - Update version number and build date with each release

3. **Localization** 🈯
    - At minimum provide Simplified Chinese and English descriptions
    - More languages = better internationalization

4. **Compatibility** ⚠️
    - Clearly state compatible loader versions
    - Set `modx=true` if module runs standalone

5. **Icon Standards** 🖼️
    - Recommended: 64x64 PNG format
    - Ensure visibility across different backgrounds

#### **Best Practices ✅**

1. **Initialization**
    - Perform main-thread operations in `Initialize()`
    - Non-zero return indicates failure

2. **Multi-Channel Comm** 📡
    - Use standardized function naming
    - Ensure sent/received data types match

3. **Error Handling** ❌
    - All interfaces should return status codes
    - Provide detailed error logging

4. **Resource Management** 💾
    - Load external resources in `Load()`
    - Release all resources in `UnLoad()`

5. **Performance** ⚡
    - Avoid memory allocation in frequently called functions
    - Implement caching mechanisms