# TEFModLoader

> Next-generation high-performance cross-platform Terraria mod loader

## [💝 Donation List](Document/donation.md)

## 📦 [Official Mod Repository](https://github.com/eternalfuture-e38299/Terraria-EFMod)

## [![Telegram Channel](https://img.shields.io/badge/Official_Telegram_Channel-2CA5E0?style=for-the-badge&logo=telegram&logoColor=white)](https://t.me/TEFModLoader)

## 🏛️ Community Guidelines
1. Use polite language and maintain a friendly environment
2. No discussion of politically sensitive topics (compliant with Chinese and international laws)
3. No violent, extremist or illegal content
4. Keep technical discussions professional and constructive


## 📚 Open Source Components

| Project Name | Purpose | License |
|--------------|---------|---------|
| [Jetpack Compose](https://developer.android.com/jetpack/compose) | Android UI framework | Apache 2.0 |
| [EFMod](https://gitlab.com/2079541547/efmod) | Mod design standard for TEFModLoader | MIT |
| [BNM-Android](https://github.com/ByNameModding/BNM-Android) | Library for modifying il2cpp games on Android via class/method/field names | MIT |
| [Dobby](https://github.com/jmpews/Dobby) | Lightweight, cross-platform, multi-architecture hooking framework | Apache 2.0 |
| [SilkCasket](https://github.com/2079541547/SilkCasket) | TEFMod file format | Apache 2.0 |
| [Axml2xml](https://github.com/developer-krushna/Axml2xml) | Advanced and enhanced axml compiler | - |
| [axml-parser](https://github.com/ZaratustraN/axml-parser) | Converts axml to xml format | - |
| [xml2axml](https://github.com/hzw1199/xml2axml) | Converts xml to axml format | - |

## ✨ Core Advantages

🔧 **Open Architecture**  
Supports C++ native mod development, with future multi-language ecosystem expansion

⚡ **Extreme Performance**  
- Memory usage approaches vanilla game levels  
- Mod runtime efficiency surpasses TL Pro  
- Cold start time comparable to vanilla game

🖥️ **Cross-Platform Coverage**  
Android/Windows/Linux collaborative development, write once deploy everywhere

## 🛠 Technical Architecture

| Component | Technology Stack | Features |
|-----------|------------------|----------|
| **Core Engine** | C++20 standard | Automatic memory management |
| **UI Framework** | Jetpack Compose + MD3 | Dynamic themes/Interactive animations/Multi-language |
| **ABI Interface** | C++20 stable API | NDK/Windows SDK/Linux GCC compatibility |
| **Build System** | CMake + Gradle | Cross-platform incremental compilation |

## Compatibility

| Platform     | arm64 | x86_64 | x86_32 | arm32 |
|-------------|-------|--------|--------|-------|
| Android     | ✔️    | ⚠️     | ⚠️     | ✔️    |
| Windows     | 🚫    | 🛠️     | 🛠️     | 🚫    |
| Linux       | 🚫    | 🛠️     | 🛠️     | 🚫    |
| iOS         | 🚫    | 🚫     | 🚫     | 🚫    |
| macOS       | 🚫    | 🚫     | 🚫     | 🚫    |

**Legend:**  
- ✔️ Verified support  
- ⚠️ Adapted but untested  
- 🛠️ In development  
- 🚫 Unsupported  

**Platform Notes:**  
1. Android:  
   - x86 architectures adapted but untested  
   - arm architectures baseline tested  
2. Windows:  
   - x86_32 under development  
   - x86_64 under development  
   - ARM architectures unsupported  
3. Linux:  
   - Windows takes priority  
   - x86_32 under development  
   - x86_64 under development  
   - ARM architectures unsupported  
4. Apple Ecosystem:  
   - iOS/macOS development paused due to device restrictions  
   - No architecture support currently  

## 📊 Performance Metrics (Pre-research Phase)

### Real-World Testing

| Device Model | CPU Architecture | RAM  | Performance |
|--------------|------------------|------|-------------|
| vivo Y33s    | MT6833           | 6+2GB| Stable 60fps, zero lag |
| Honor 9i     | Mali-T830 MP2    | 4GB  | 60fps, visually identical |

*Current tests based on small mod packs (<5MB). Large mod stress testing pending community ecosystem maturity.*

### Theoretical Comparison

TL Pro uses V8 engine, theoretically outperformed by this solution  
Comparison with JavaScript (V8) solution:

| Metric         | C++ Native Advantage          | JS JIT Limitations         |
|----------------|--------------------------------|----------------------------|
| Memory Overhead| Adds only native lib load (~3MB)| Carries V8 runtime (~35MB)|
| Compute Tasks  | Direct CPU instruction access | JIT compilation overhead   |
| Cold Start     | No runtime initialization     | V8 warmup required (~800ms)|

## 🛠 Development & Build Guide

### Requirements

- **Recommended OS**: Debian  
- **Cross-platform toolchain**:  

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
   - Clang
   - CMake
   - Mingw
  ```

## Mod Development

### Path Spelling Error Fix
**Affected Versions**: All Beta versions below 10.0.0 Beta3.4  
**Error Manifestation**: Incorrectly spelled `TEFModLoader` as `TEFModLoaLoader` in paths  
**Automatic Fix Solution**:

```cpp
/**
 * Automatically fixes path spelling errors
 * @param path Original path
 * @return Corrected path
 */
std::string FixPathTypo(const std::string& path) {
    std::string corrected = path;
    const std::string WRONG_STR = "TEFModLoaLoader";
    const std::string CORRECT_STR = "TEFModLoader";
    
    size_t typoPos = corrected.find(WRONG_STR);
    while (typoPos != std::string::npos) {
        corrected.replace(typoPos, WRONG_STR.length(), CORRECT_STR);
        typoPos = corrected.find(WRONG_STR, typoPos + CORRECT_STR.length());
    }
    
    return corrected;
}
```

- [EFMod Documentation](https://gitlab.com/2079541547/efmod)
- [Basic API](Document/Development/BasicAPI.md)