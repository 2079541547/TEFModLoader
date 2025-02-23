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

#include <SilkCasket.hpp>
#include <build.hpp>
#include <utils.hpp>
#include <iostream>

void SilkCasket_compressDirectory(bool suffix,
                                  const std::filesystem::path &targetPath,
                                  std::filesystem::path outPath,
                                  SilkCasket::MODE mode,
                                  size_t blockSize,
                                  bool entryEncryption,
                                  const std::string &Key) {
    if (suffix)
        outPath += ".skc";
    SilkCasket::builder().build(
            targetPath,
            outPath,
            mode,
            blockSize,
            entryEncryption,
            Key);
}

void SilkCasket_compress_A_File(bool suffix,
                                const std::filesystem::path &targetPath,
                                std::filesystem::path outPath,
                                SilkCasket::MODE mode,
                                size_t blockSize,
                                bool entryEncryption,
                                const std::string &Key) {

    std::filesystem::path tempDir =
            outPath.parent_path() / "silk_casket_temp";

    std::filesystem::remove_all(tempDir);

    if (!std::filesystem::exists(tempDir))
    {
        std::filesystem::create_directories(tempDir);
    }

    std::filesystem::copy_file(targetPath, tempDir / targetPath.filename());

    if (suffix)
        outPath += ".skc";
    SilkCasket::builder().build(
            tempDir,
            outPath,
            mode,
            blockSize,
            entryEncryption,
            Key);
}

void SilkCasket_compress_Files(
        bool suffix,
        const std::map<std::filesystem::path, std::filesystem::path> &targetPaths,
        std::filesystem::path outPath,
        SilkCasket::MODE mode,
        size_t blockSize,
        bool entryEncryption,
        const std::string &Key) {

    try
    {
        std::filesystem::path tempDir = outPath.parent_path() / "silk_casket_temp";
        std::filesystem::remove_all(tempDir);
        if (!std::filesystem::exists(tempDir))
        {
            std::filesystem::create_directories(tempDir);
        }

        if (suffix)
            outPath += ".skc";

        for (const auto &[fullPath, relativePath] : targetPaths)
        {
            std::filesystem::path tempFullPath = tempDir / relativePath;
            std::filesystem::create_directories(tempFullPath.parent_path());

            SilkCasket::Utils::copyFileTo(fullPath, tempFullPath);
        }

        SilkCasket::builder().build(
                tempDir,
                outPath,
                mode,
                blockSize,
                entryEncryption,
                Key);

        std::filesystem::remove_all(tempDir);
    }
    catch (const std::filesystem::filesystem_error &e)
    {
        std::cerr << e.what() << std::endl;
    }
    catch (const std::exception &e)
    {
        std::cerr << e.what() << std::endl;
    }
}

void SilkCasket_compress(
        bool suffix,
        const std::map<std::filesystem::path, std::filesystem::path> &targetPaths,
        std::filesystem::path outPath,
        SilkCasket::MODE mode,
        size_t blockSize,
        bool entryEncryption,
        const std::string &Key) {
    try
    {
        std::filesystem::path tempDir = outPath.parent_path() / "silk_casket_temp";
        std::filesystem::remove_all(tempDir);
        if (!std::filesystem::exists(tempDir))
        {
            std::filesystem::create_directories(tempDir);
        }

        if (suffix)
            outPath += ".skc";

        for (const auto &[fullPath, relativePath] : targetPaths)
        {
            std::filesystem::path tempFullPath = tempDir / relativePath;
            std::filesystem::create_directories(tempFullPath.parent_path());

            if (std::filesystem::is_directory(fullPath))
            {
                SilkCasket::Utils::copyDirectory(fullPath, tempFullPath);
            }
            else
            {
                SilkCasket::Utils::copyFileTo(fullPath, tempFullPath);
            }
        }

        SilkCasket::builder().build(
                tempDir,
                outPath,
                mode,
                blockSize,
                entryEncryption,
                Key);

        std::filesystem::remove_all(tempDir);
    }
    catch (const std::filesystem::filesystem_error &e)
    {
        std::cerr << e.what() << std::endl;
    }
    catch (const std::exception &e)
    {
        std::cerr << e.what() << std::endl;
    }
}


void releaseAllEntry(const std::filesystem::path &filePath, const std::filesystem::path &outPath, const std::string& key) {
    SilkCasket::analysis A(filePath, key);
    A.releaseEntry(outPath);
}

void releaseEntry(const std::filesystem::path &filePath, const std::string &Entry, const std::filesystem::path &outPath, const std::string& key){
    SilkCasket::analysis A(filePath, key);
    A.releaseFile(Entry, outPath);
}

void releaseFolder(const std::filesystem::path &filePath, const std::string& Entry, const std::filesystem::path &outPath, const std::string& key){
    SilkCasket::analysis A(filePath, key);
    A.releaseFolder(Entry, outPath);
}

std::vector<uint8_t> get_entry_data(const std::filesystem::path &filePath, const std::string& Entry, const std::string& key) {
    SilkCasket::analysis A(filePath, key);
    return A.getData(Entry);
}