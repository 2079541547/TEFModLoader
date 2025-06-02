# SilkCasket

* 一个注重灵活性的压缩包格式
* [English](README-en.md)
* [Русский язык](README-ru.md)

## 优点

* 允许不同条目间使用不同算法进行压缩
* 官方加密支持
* 数据复用
* 集成LZ4、LZW、LZMA2 Fast、Lizard算法
* 兼容性强
* 轻便

## 兼容


| 平台    | x86_64 | x86 | arm64 | arm32 |
| ------- | ------ | --- | ----- | ----- |
| Android | ✔     | ✔  | ✔    | ✔    |
| Linux   | ✔     | ✔  | ✔    | ✔    |
| Windows | ✔      | ✔  | ?    | ?    |
| Mac     | ?      | ?   | ?     | ?     |
| iOS     | ?      | ×  | ?     | ×    |

* 注：Mac, IOS 未进行测试，IOS没有x86_64及x86架构

## API

### 压缩相关

- **压缩指定目录**

  - 描述：允许用户压缩整个目录。
  - 参数：
    - `suffix`：布尔值，决定是否在输出文件名后添加压缩模式的后缀。
    - `targetPath`：要压缩的目录路径。
    - `outPath`：输出压缩文件的路径。
    - `mode`：选择使用的压缩算法。
    - `entryEncryption`（可选）：布尔值，默认为 `false`，表示是否对条目进行加密。
    - `Key`（可选）：字符串，如果 `entryEncryption` 设置为 `true`，则必须提供一个非空字符串作为密钥。
- **压缩单个文件**

  - 描述：仅作用于单个文件的压缩。
  - 参数同上。
- **压缩多个文件**

  - 描述：允许用户同时压缩多个文件，并可以指定每个文件在压缩包中的相对路径。
  - 参数：
    - `targetPaths`：文件路径映射，键为源文件路径，值为目标输出路径（在压缩包内）。
    - 其他参数同上。
- **额外功能的压缩接口**

  - 描述：可能提供了额外的功能或选项，具体取决于实现。
  - 参数同上。

### 解压相关

- **解压所有条目到指定路径**

  - 描述：从给定的压缩文件中提取所有内容到指定目录。
  - 参数：
    - `filePath`：要解压的压缩文件路径。
    - `outPath`：输出目录路径。
    - `key`：如果压缩时使用了加密，那么这里需要提供相同的密钥来解压。
- **解压特定条目到指定路径**

  - 描述：从给定的压缩文件中提取特定条目（文件或文件夹）到指定目录。
  - 参数：
    - `Entry`：指定要解压的条目名称。
    - 其他参数同上。
- **解压特定文件夹到指定路径**

  - 描述：从给定的压缩文件中提取特定文件夹到指定目录。
  - 参数同“解压特定条目到指定路径”。

```C++
/**
 * @brief 压缩指定目录。
 *
 * 该函数允许用户压缩整个目录，并可选择是否加密以及采用哪种压缩算法。
 *
 * @param suffix 是否在输出文件名后添加压缩模式的后缀。
 * @param targetPath 要压缩的目录路径。
 * @param outPath 输出压缩文件的路径。
 * @param mode 压缩算法的选择，使用枚举类型SilkCasket::Compress::Mode::MODE。
 * @param entryEncryption 是否对条目进行加密，默认为false。
 * @param Key 加密使用的密钥，如果entryEncryption设置为true，则必须提供一个非空字符串作为密钥。
 */
void SilkCasket_compressDirectory(bool suffix,
                                  const std::filesystem::path &targetPath,
                                  std::filesystem::path outPath,
                                  SilkCasket::Compress::Mode::MODE mode,
                                  bool entryEncryption = false,
                                  const std::string &Key = "");

/**
 * @brief 压缩单个文件。
 *
 * 与压缩目录类似，但仅作用于单个文件。
 *
 * @param suffix 同上。
 * @param targetPath 要压缩的文件路径。
 * @param outPath 输出压缩文件的路径。
 * @param mode 压缩算法的选择。
 * @param entryEncryption 是否对条目进行加密。
 * @param Key 加密使用的密钥。
 */
void SilkCasket_compress_A_File(bool suffix,
                                const std::filesystem::path &targetPath,
                                std::filesystem::path outPath,
                                SilkCasket::Compress::Mode::MODE mode,
                                bool entryEncryption = false,
                                const std::string &Key = "");

/**
 * @brief 压缩多个文件。
 *
 * 允许用户同时压缩多个文件，并可以指定每个文件在压缩包中的相对路径。
 *
 * @param suffix 同上。
 * @param targetPaths 文件路径映射，键为源文件路径，值为目标输出路径（在压缩包内）。
 * @param outPath 输出压缩文件的路径。
 * @param mode 压缩算法的选择。
 * @param entryEncryption 是否对条目进行加密。
 * @param Key 加密使用的密钥。
 */
void SilkCasket_compress_Files(
    bool suffix,
    const std::map<std::filesystem::path, std::filesystem::path>& targetPaths,
    std::filesystem::path outPath,
    SilkCasket::Compress::Mode::MODE mode,
    bool entryEncryption = false,
    const std::string &Key = ""
);

/**
 * @brief 可能具有额外功能的压缩接口。
 *
 * 该函数可能提供了额外的功能或选项，具体取决于实现。
 *
 * 参数同上。
 */
void SilkCasket_compress(
    bool suffix,
    const std::map<std::filesystem::path, std::filesystem::path>& targetPaths,
    std::filesystem::path outPath,
    SilkCasket::Compress::Mode::MODE mode,
    bool entryEncryption = false,
    const std::string &Key = ""
);

/**
 * @brief 解压所有条目到指定路径。
 *
 * 从给定的压缩文件中提取所有内容到指定目录。
 *
 * @param filePath 要解压的压缩文件路径。
 * @param outPath 输出目录路径。
 * @param key 如果压缩时使用了加密，那么这里需要提供相同的密钥来解压。
 */
void releaseAllEntry(const std::filesystem::path& filePath,
                     const std::filesystem::path& outPath,
                     std::string key);

/**
 * @brief 解压特定条目到指定路径。
 *
 * 从给定的压缩文件中提取特定条目（文件或文件夹）到指定目录。
 *
 * @param filePath 要解压的压缩文件路径。
 * @param Entry 指定要解压的条目名称。
 * @param outPath 输出目录路径。
 * @param key 如果压缩时使用了加密，那么这里需要提供相同的密钥来解压。
 */
void releaseEntry(const std::filesystem::path& filePath,
                  const std::string& Entry,
                  const std::filesystem::path& outPath,
                  std::string key);

/**
 * @brief 解压特定文件夹到指定路径。
 *
 * 从给定的压缩文件中提取特定文件夹到指定目录。
 *
 * 参数同上。
 */
void releaseFolder(const std::filesystem::path& filePath,
                   std::string Entry,
                   const std::filesystem::path& outPath,
                   std::string key);
```
