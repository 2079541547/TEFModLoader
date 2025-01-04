/*******************************************************************************
 * 文件名称: build
 * 项目名称: SilkCasket
 * 创建时间: 2024/11/24
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

#include <build/builder.hpp>
#include <utils/file.hpp>
#include <compress/compress.hpp>
#include <encryption.hpp>
#include <cstring>
#include <log.hpp>
#include <future>
#include <vector>
#include <thread>
#include <filesystem>

void SilkCasket::Build::Builder::Build::build(const std::filesystem::path &Path,
                                                 const std::filesystem::path &OutPath,
                                                 SilkCasket::Compress::Mode::MODE Mode,
                                                 size_t blockSize,
                                                 bool Encryption,
                                                 const string &key)
{
    auto cacheDir = Path.parent_path() / "cache";
    filesystem::create_directories(cacheDir); // 创建缓存目录
    LOG(LogLevel::INFO, "SilkCasket::Build::Builder", "Build", "build", "已创建缓存目录：" + cacheDir.string());
    
    // 遍历源目录中的文件并处理
    for (const auto &entry : filesystem::recursive_directory_iterator(Path))
    {
        if (entry.is_regular_file())
        {
            auto relativePath = std::filesystem::relative(entry.path(), Path);
            auto cacheFilePath = cacheDir / relativePath;

            // 确保缓存文件路径的目录存在
            filesystem::create_directories(cacheFilePath.parent_path());

            // 压缩文件并保存到缓存目录
            auto compressedData = SilkCasket::Compress::smartCompress(entry.path(), Mode, blockSize);
            if (!compressedData.empty())
            {
                Utils::File::Vuint8ToFile(cacheFilePath, compressedData);

                // 如果需要加密，立即加密压缩后的文件
                if (Encryption)
                {
                    auto encryptionData = SilkCasket::encryptFile(key, compressedData);
                    if (!encryptionData.empty())
                    {
                        Utils::File::Vuint8ToFile(cacheFilePath, encryptionData);
                    }
                }
            }
        }
    }

    // 配置条目，创建索引等操作
    configureEntries(cacheDir, Encryption);

    // 序列化文件条目和数据条目
    auto FileEntry = SilkCasket::Compress::smartCompress(SilkCasket::FileStructure::serializeEntrys(Entry), Mode);
    auto FileEntryData = SilkCasket::Compress::smartCompress(SilkCasket::FileStructure::serializeEntryData(EntryData), Mode);

    // 构建头部信息
    Header = {
        "SilkCasket",
        20241130,
        {(long)tempData.size() + 46, (long)FileEntry.size()},
        {(long)tempData.size() + (long)FileEntry.size() + 46, (long)FileEntryData.size()},
    };

    auto FileHeader = SilkCasket::FileStructure::serializeHeader(Header);

    Data.data.insert(Data.data.end(), FileHeader.begin(), FileHeader.end());
    Data.data.insert(Data.data.end(), tempData.begin(), tempData.end());

    Data.data.insert(Data.data.end(), FileEntry.begin(), FileEntry.end());
    Data.data.insert(Data.data.end(), FileEntryData.begin(), FileEntryData.end());

    // 将最终打包的数据写入输出文件
    SilkCasket::Utils::File::Vuint8ToFile(OutPath, Data.data);
    LOG(LogLevel::INFO, "SilkCasket::Build::Builder", "Build", "build", "打包完成：" + OutPath.string());

    // 清理缓存目录
    filesystem::remove_all(cacheDir);
}