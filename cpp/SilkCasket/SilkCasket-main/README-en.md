# SilkCasket

* A compression format that emphasizes flexibility

## Advantages

* Allows different entries to be compressed with different algorithms
* Official encryption support
* Data reusability
* Integrates LZ4, LZW, LZMA2 Fast, and Lizard algorithms
* High compatibility
* Lightweight

## Compatibility


| Platform | x86_64 | x86 | arm64 | arm32 |
| -------- | ------ | --- | ----- | ----- |
| Android  | ✔     | ✔  | ✔    | ✔    |
| Linux    | ✔     | ✔  | ✔    | ✔    |
| Windows  | ✔      | ✔  | ?    | ?    |
| Mac      | ?      | ?   | ?     | ?     |
| iOS      | ?      | ×  | ?     | ×    |

* Note: Mac, and iOS have not been tested. iOS does not support x86_64 and x86 architectures.

## API

### Compression Related

- **Compress a specified directory**

  - Description: Allows the user to compress an entire directory.
  - Parameters:
    - `suffix`: A boolean value deciding whether to add a suffix indicating the compression mode to the output file name.
    - `targetPath`: The path of the directory to be compressed.
    - `outPath`: The path where the compressed file will be saved.
    - `mode`: The compression algorithm to use.
    - `entryEncryption` (optional): A boolean value, default is `false`, indicating if the entries should be encrypted.
    - `Key` (optional): A string, if `entryEncryption` is set to `true`, a non-empty string must be provided as the key.

- **Compress a single file**

  - Description: Compresses only a single file.
  - Parameters are the same as above.

- **Compress multiple files**

  - Description: Allows the user to compress multiple files at once, and can specify the relative path for each file within the archive.
  - Parameters:
    - `targetPaths`: A map of file paths, where the key is the source file path and the value is the target output path (inside the archive).
    - Other parameters are the same as above.

- **Compression interface with additional features**

  - Description: May provide extra features or options, depending on the implementation.
  - Parameters are the same as above.

### Decompression Related

- **Decompress all entries to a specified path**

  - Description: Extracts all contents from the given compressed file to a specified directory.
  - Parameters:
    - `filePath`: The path of the compressed file to decompress.
    - `outPath`: The output directory path.
    - `key`: If encryption was used during compression, the same key must be provided here to decompress.

- **Decompress specific entry to a specified path**

  - Description: Extracts a specific entry (file or folder) from the given compressed file to a specified directory.
  - Parameters:
    - `Entry`: The name of the entry to decompress.
    - Other parameters are the same as above.

- **Decompress specific folder to a specified path**

  - Description: Extracts a specific folder from the given compressed file to a specified directory.
  - Parameters are the same as "Decompress specific entry to a specified path".

```C++
/**
 * @brief Compress a specified directory.
 *
 * This function allows the user to compress an entire directory, with the option to encrypt and choose which compression algorithm to use.
 *
 * @param suffix Whether to add a suffix indicating the compression mode to the output file name.
 * @param targetPath The path of the directory to be compressed.
 * @param outPath The path where the compressed file will be saved.
 * @param mode The choice of compression algorithm, using the enum type SilkCasket::Compress::Mode::MODE.
 * @param entryEncryption Whether to encrypt the entries, defaults to false.
 * @param Key The key used for encryption, if entryEncryption is set to true, a non-empty string must be provided as the key.
 */
void SilkCasket_compressDirectory(bool suffix,
                                  const std::filesystem::path &targetPath,
                                  std::filesystem::path outPath,
                                  SilkCasket::Compress::Mode::MODE mode,
                                  bool entryEncryption = false,
                                  const std::string &Key = "");

/**
 * @brief Compress a single file.
 *
 * Similar to compressing a directory, but operates on a single file.
 *
 * @param suffix As above.
 * @param targetPath The path of the file to be compressed.
 * @param outPath The path where the compressed file will be saved.
 * @param mode The choice of compression algorithm.
 * @param entryEncryption Whether to encrypt the entries.
 * @param Key The key used for encryption.
 */
void SilkCasket_compress_A_File(bool suffix,
                                const std::filesystem::path &targetPath,
                                std::filesystem::path outPath,
                                SilkCasket::Compress::Mode::MODE mode,
                                bool entryEncryption = false,
                                const std::string &Key = "");

/**
 * @brief Compress multiple files.
 *
 * Allows the user to compress multiple files at once, and can specify the relative path for each file within the archive.
 *
 * @param suffix As above.
 * @param targetPaths A map of file paths, where the key is the source file path and the value is the target output path (inside the archive).
 * @param outPath The path where the compressed file will be saved.
 * @param mode The choice of compression algorithm.
 * @param entryEncryption Whether to encrypt the entries.
 * @param Key The key used for encryption.
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
 * @brief Compression interface with possible additional features.
 *
 * This function may provide extra features or options, depending on the implementation.
 *
 * Parameters are the same as above.
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
 * @brief Decompress all entries to a specified path.
 *
 * Extracts all contents from the given compressed file to a specified directory.
 *
 * @param filePath The path of the compressed file to decompress.
 * @param outPath The output directory path.
 * @param key If encryption was used during compression, the same key must be provided here to decompress.
 */
void releaseAllEntry(const std::filesystem::path& filePath,
                     const std::filesystem::path& outPath,
                     std::string key);

/**
 * @brief Decompress a specific entry to a specified path.
 *
 * Extracts a specific entry (file or folder) from the given compressed file to a specified directory.
 *
 * @param filePath The path of the compressed file to decompress.
 * @param Entry The name of the entry to decompress.
 * @param outPath The output directory path.
 * @param key If encryption was used during compression, the same key must be provided here to decompress.
 */
void releaseEntry(const std::filesystem::path& filePath,
                  const std::string& Entry,
                  const std::filesystem::path& outPath,
                  std::string key);

/**
 * @brief Decompress a specific folder to a specified path.
 *
 * Extracts a specific folder from the given compressed file to a specified directory.
 *
 * Parameters are the same as above.
 */
void releaseFolder(const std::filesystem::path& filePath,
                   std::string Entry,
                   const std::filesystem::path& outPath,
                   std::string key);
```
