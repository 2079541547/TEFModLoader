/*******************************************************************************
 * 文件名称: SilkCasket
 * 项目名称: SilkCasket
 * 创建时间: 2025/1/4
 * 作者: EternalFuture゙
 * Github: https://github.com/2079541547
 * 版权声明: Copyright © 2024 EternalFuture. All rights reserved.
 * 许可证: Licensed under the Apache License, Version 2.0 (the "License");
 *         you may not use this file except in compliance with the License.
 *         You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *         Unless required by applicable law or agreed to in writing, software
 *         distributed under the License is distributed on an "AS IS" BASIS,
 *         WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *         See the License for the specific language governing permissions and
 *         limitations under the License.
 *
 * 描述信息: 本文件为Silk Casket项目中的一部分，允许在遵守Apache License 2.0的条件下自由用于商业用途。
 * 注意事项: 请严格遵守Apache License 2.0协议使用本代码。Apache License 2.0允许商业用途，无需额外授权。
 *******************************************************************************/

#pragma once

#include "compress.hpp"
#include "analysis.hpp"
#include <map>

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
extern "C" void SilkCasket_compressDirectory(bool suffix,
                                  const std::filesystem::path &targetPath,
                                  std::filesystem::path outPath,
                                  SilkCasket::MODE mode,
                                  size_t blockSize = 8096 * 1024,
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
extern "C" void SilkCasket_compress_A_File(bool suffix,
                                const std::filesystem::path &targetPath,
                                std::filesystem::path outPath,
                                SilkCasket::MODE mode,
                                size_t blockSize = 8096 * 1024,
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
extern "C" void SilkCasket_compress_Files(
        bool suffix,
        const std::map<std::filesystem::path, std::filesystem::path> &targetPaths,
        std::filesystem::path outPath,
        SilkCasket::MODE mode,
        size_t blockSize = 8096 * 1024,
        bool entryEncryption = false,
        const std::string &Key = "");

/**
 * @brief 可能具有额外功能的压缩接口。
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
extern "C" void SilkCasket_compress(
        bool suffix,
        const std::map<std::filesystem::path, std::filesystem::path> &targetPaths,
        std::filesystem::path outPath,
        SilkCasket::MODE mode,
        size_t blockSize = 8096 * 1024,
        bool entryEncryption = false,
        const std::string &Key = "");

/**
 * @brief 可能具有额外功能的压缩接口。
 *
 * 该函数可能提供了额外的功能或选项，具体取决于实现。
 *
 * 参数同上。
 */
extern "C" void releaseAllEntry(const std::filesystem::path &filePath, const std::filesystem::path &outPath, const std::string& key = "");

/**
 * @brief 解压所有条目到指定路径。
 *
 * 从给定的压缩文件中提取所有内容到指定目录。
 *
 * @param filePath 要解压的压缩文件路径。
 * @param outPath 输出目录路径。
 * @param key 如果压缩时使用了加密，那么这里需要提供相同的密钥来解压。
 */
extern "C" void releaseEntry(const std::filesystem::path &filePath, const std::string &Entry, const std::filesystem::path &outPath, const std::string& key = "");

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
extern "C" void releaseFolder(const std::filesystem::path &filePath, const std::string& Entry, const std::filesystem::path &outPath, const std::string& key = "");

/**
 * @brief 解压特定文件夹到指定路径。
 *
 * 从给定的压缩文件中提取特定文件夹到指定目录。
 *
 * @param filePath 要解压的压缩文件路径。
 * @param Entry 指定要解压的条目名称。
 * @param outPath 输出目录路径。
 * @param key 如果压缩时使用了加密，那么这里需要提供相同的密钥来解压。
 */
extern "C" std::vector<uint8_t> get_entry_data(const std::filesystem::path &filePath, const std::string& Entry, const std::string &key = "");