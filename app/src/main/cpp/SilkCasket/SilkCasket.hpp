/*******************************************************************************
 * 文件名称: SilkCasket
 * 项目名称: SilkCasket
 * 创建时间: 2024/11/30
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
 * 描述信息: 本文件为SilkCasket项目中的一部分，允许在遵守Apache License 2.0的条件下自由用于商业用途。
 * 注意事项: 请严格遵守Apache License 2.0协议使用本代码。Apache License 2.0允许商业用途，无需额外授权。
 *******************************************************************************/

#pragma once

#include "silkcasket/build/builder.hpp"
#include "silkcasket/compress/mode.hpp"
#include <filesystem>
#include "silkcasket/utils/file.hpp"
#include <utility>
#include "silkcasket/log.hpp"
#include "silkcasket/analysis.hpp"

extern "C"
{
    void SilkCasket_compressDirectory(bool suffix,
                                      const std::filesystem::path &targetPath,
                                      std::filesystem::path outPath,
                                      SilkCasket::Compress::Mode::MODE mode,
                                      size_t blockSize = 8096 * 1024,
                                      bool entryEncryption = false,
                                      const std::string &Key = "")
    {

        if (suffix)
            outPath += ".skc";
        SilkCasket::Build::Builder::Build().build(
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
                                    SilkCasket::Compress::Mode::MODE mode,
                                    size_t blockSize = 8096 * 1024,
                                    bool entryEncryption = false,
                                    const std::string &Key = "")
    {

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
        SilkCasket::Build::Builder::Build().build(
            tempDir,
            outPath,
            mode,
            blockSize,
            entryEncryption,
            Key);
    }

    void SilkCasket_compress_Files(
        bool suffix,
        const std::map<std::filesystem::path, std::filesystem::path> &targetPaths, // 修改为 map
        std::filesystem::path outPath,
        SilkCasket::Compress::Mode::MODE mode,
        size_t blockSize = 8096 * 1024,
        bool entryEncryption = false,
        const std::string &Key = "")
    {

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
                // 创建临时目录下的对应结构
                std::filesystem::path tempFullPath = tempDir / relativePath;
                std::filesystem::create_directories(tempFullPath.parent_path());

                // 复制文件到临时目录下对应的相对路径
                SilkCasket::Utils::File::copyFileTo(fullPath, tempFullPath);
            }

            // 构建最终的压缩包
            SilkCasket::Build::Builder::Build().build(
                tempDir,
                outPath,
                mode,
                blockSize,
                entryEncryption,
                Key);

            // 清理临时目录
            std::filesystem::remove_all(tempDir);
        }
        catch (const std::filesystem::filesystem_error &e)
        {
            LOG(SilkCasket::LogLevel::ERROR, "SilkCasket_compress_Files",
                "错误：" + (std::string)e.what());
        }
        catch (const std::exception &e)
        {
            LOG(SilkCasket::LogLevel::ERROR, "SilkCasket_compress_Files",
                "错误：" + (std::string)e.what());
        }
    }

    void SilkCasket_compress(
        bool suffix,
        const std::map<std::filesystem::path, std::filesystem::path> &targetPaths,
        std::filesystem::path outPath,
        SilkCasket::Compress::Mode::MODE mode,
        size_t blockSize = 8096 * 1024,
        bool entryEncryption = false,
        const std::string &Key = "")
    {

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
                // 创建临时目录下的对应结构
                std::filesystem::path tempFullPath = tempDir / relativePath;
                std::filesystem::create_directories(tempFullPath.parent_path());

                if (std::filesystem::is_directory(fullPath))
                {
                    SilkCasket::Utils::File::copyDirectory(fullPath, tempFullPath);
                }
                else
                {
                    // 复制文件到临时目录下对应的相对路径
                    SilkCasket::Utils::File::copyFileTo(fullPath, tempFullPath);
                }
            }

            // 构建最终的压缩包
            SilkCasket::Build::Builder::Build().build(
                tempDir,
                outPath,
                mode,
                blockSize,
                entryEncryption,
                Key);

            // 清理临时目录
            std::filesystem::remove_all(tempDir);
        }
        catch (const std::filesystem::filesystem_error &e)
        {
            LOG(SilkCasket::LogLevel::ERROR, "SilkCasket_compress_Files",
                "错误：" + (std::string)e.what());
        }
        catch (const std::exception &e)
        {
            LOG(SilkCasket::LogLevel::ERROR, "SilkCasket_compress_Files",
                "错误：" + (std::string)e.what());
        }
    }

    void releaseAllEntry(const std::filesystem::path &filePath, const std::filesystem::path &outPath, std::string key)
    {
        SilkCasket::analysis A(filePath, std::move(key));
        A.releaseEntry(outPath);
    }

    void releaseEntry(const std::filesystem::path &filePath, const std::string &Entry, const std::filesystem::path &outPath, std::string key)
    {
        SilkCasket::analysis A(filePath, std::move(key));
        A.releaseFile(Entry, outPath);
    }

    void releaseFolder(const std::filesystem::path &filePath, std::string Entry, const std::filesystem::path &outPath, std::string key)
    {
        SilkCasket::analysis A(filePath, std::move(key));
        A.releaseFolder(std::move(Entry), outPath);
    }

    std::vector<uint8_t> get_entry_data(const std::filesystem::path &filePath, std::string Entry, std::string key)
    {
        SilkCasket::analysis A(filePath, std::move(key));
        return A.getData(Entry);
    }
}