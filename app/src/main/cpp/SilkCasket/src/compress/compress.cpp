/*******************************************************************************
 * 文件名称: compress
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


#include <iostream>
#include <compress/compress.hpp>
#include <utils/file.hpp>
#include <log.hpp>
#include <compress/lzw.hpp>
#include <compress/lz4.hpp>
#include <compress/lzma2-fast.hpp>
#include <compress/lizard.hpp>
#include <future>
#include <vector>
#include <algorithm>
#include <filesystem>

size_t BlockSize = 8096 * 1024;

std::vector<uint8_t> compressData(const std::vector<uint8_t>& data, SilkCasket::Compress::Mode::Mode mode) {
    using namespace SilkCasket::Compress::Mode;

    switch (mode) {
        case Storage:
        {
            std::vector<uint8_t> result;
            auto _a = data;
            int modeValue = static_cast<int>(mode);
            result.insert(result.end(), reinterpret_cast<const uint8_t*>(&modeValue), reinterpret_cast<const uint8_t*>(&modeValue) + sizeof(int));
            result.insert(result.end(), _a.begin(), _a.end());
            return result;
        }
        case LZMA2FAST: {
            std::vector<uint8_t> result;
            auto _a = SilkCasket::Compress::LZMA2_Fast::compress(data, BlockSize);
            int modeValue = static_cast<int>(mode);
            result.insert(result.end(), reinterpret_cast<const uint8_t*>(&modeValue), reinterpret_cast<const uint8_t*>(&modeValue) + sizeof(int));
            result.insert(result.end(), _a.begin(), _a.end());
            return result;
        }
        case LZ4: {
            std::vector<uint8_t> result;
            auto _a = SilkCasket::Compress::LZ4::compress(data, BlockSize);
            int modeValue = static_cast<int>(mode);
            result.insert(result.end(), reinterpret_cast<const uint8_t*>(&modeValue), reinterpret_cast<const uint8_t*>(&modeValue) + sizeof(int));
            result.insert(result.end(), _a.begin(), _a.end());
            return result;
        }
        case LZW: {
            std::vector<uint8_t> result;
            auto _a = SilkCasket::Compress::LZW::compress(data);
            int modeValue = static_cast<int>(mode);
            result.insert(result.end(), reinterpret_cast<const uint8_t*>(&modeValue), reinterpret_cast<const uint8_t*>(&modeValue) + sizeof(int));
            result.insert(result.end(), _a.begin(), _a.end());
            return result;
        }
        case LIZARD: {
            std::vector<uint8_t> result;
            auto _a = SilkCasket::Compress::Lizard::compress(data, BlockSize);
            int modeValue = static_cast<int>(mode);
            result.insert(result.end(), reinterpret_cast<const uint8_t*>(&modeValue), reinterpret_cast<const uint8_t*>(&modeValue) + sizeof(int));
            result.insert(result.end(), _a.begin(), _a.end());
            return result;
        }
        default:
            throw std::invalid_argument("未知的压缩模式");
    }
}




// 异步压缩函数
std::future<std::pair<SilkCasket::Compress::Mode::Mode, std::filesystem::path>>
asyncCompressData(const std::vector<uint8_t>& data, SilkCasket::Compress::Mode::Mode mode) {
    return std::async(std::launch::async, [&data, mode](){
        auto compressedData = compressData(data, mode);
        auto tempFile = std::filesystem::temp_directory_path() / ("compressed_" + std::to_string(static_cast<int>(mode)) + ".tmp");
        SilkCasket::Utils::File::Vuint8ToFile(tempFile, compressedData);
        return std::make_pair(mode, tempFile);
    });
}

std::vector<uint8_t> SilkCasket::Compress::smartCompress(const std::filesystem::path& inputFile, const SilkCasket::Compress::Mode::MODE& compress, size_t blockSize) {
    using namespace std::filesystem;
    BlockSize = blockSize;

    if (!exists(inputFile)) {
        LOG(LogLevel::ERROR, "SilkCasket::Compress", "smartCompress", "文件不存在！");
        return {};
    }

    LOG(LogLevel::INFO, "SilkCasket::Compress", "smartCompress", "处理文件：" + inputFile.string());

    auto data = Utils::File::readFile(inputFile);

    std::vector<std::future<std::pair<SilkCasket::Compress::Mode::Mode, path>>> futures;

    if (compress.Storage) futures.push_back(asyncCompressData(data, SilkCasket::Compress::Mode::Storage));
    if (compress.LZMA2FAST) futures.push_back(asyncCompressData(data, SilkCasket::Compress::Mode::LZMA2FAST));
    if (compress.LZ4) futures.push_back(asyncCompressData(data, SilkCasket::Compress::Mode::LZ4));
    if (compress.LZW) futures.push_back(asyncCompressData(data, SilkCasket::Compress::Mode::LZW));
    if (compress.LIZARD) futures.push_back(asyncCompressData(data, SilkCasket::Compress::Mode::LIZARD));

    if (futures.empty()) {
        LOG(LogLevel::ERROR, "SilkCasket::Compress", "smartCompress", "没有启用任何压缩模式！");
        return {};
    }

    // 等待所有异步操作完成，并找到最小的压缩结果
    std::vector<std::pair<SilkCasket::Compress::Mode::Mode, path>> results;
    results.reserve(futures.size());
for (auto& future : futures) {
        results.push_back(future.get());
    }

    auto smallestResultIt = std::min_element(results.begin(), results.end(),
                                             [](const auto& a, const auto& b) { return file_size(a.second) < file_size(b.second); });

    // 读取最小的压缩结果
    std::vector<uint8_t> result = Utils::File::readFile(smallestResultIt->second);

    // 删除所有临时文件
    for (const auto& [mode, path] : results) {
        remove(path);
    }

    return result;
}


// 异步压缩函数
std::future<std::pair<SilkCasket::Compress::Mode::Mode, std::vector<uint8_t>>>
V8asyncCompressData(const std::vector<uint8_t>& data, SilkCasket::Compress::Mode::Mode mode) {
    return std::async(std::launch::async, [&data, mode](){
        auto compressedData = compressData(data, mode);
        return std::make_pair(mode, compressedData);
    });
}

// 智能压缩函数
std::vector<uint8_t> SilkCasket::Compress::smartCompress(const std::vector<uint8_t>& data, const Mode::MODE& compress, size_t blockSize) {
    using namespace std::filesystem;
    BlockSize = blockSize;

    if (data.empty()) {
        LOG(LogLevel::ERROR, "SilkCasket::Compress", "smartCompress", "输入数据为空！");
        return {};
    }

    LOG(LogLevel::INFO, "SilkCasket::Compress", "smartCompress", "处理数据...");

    std::vector<std::future<std::pair<Mode::Mode, std::vector<uint8_t>>>> futures;

    if (compress.Storage) futures.push_back(V8asyncCompressData(data, Mode::Storage));
    if (compress.LZMA2FAST) futures.push_back(V8asyncCompressData(data, Mode::LZMA2FAST));
    if (compress.LZ4) futures.push_back(V8asyncCompressData(data, Mode::LZ4));
    if (compress.LZW) futures.push_back(V8asyncCompressData(data, Mode::LZW));
    if (compress.LIZARD) futures.push_back(V8asyncCompressData(data, Mode::LIZARD));

    if (futures.empty()) {
        LOG(LogLevel::ERROR, "SilkCasket::Compress", "smartCompress", "没有启用任何压缩模式！");
        return {};
    }

    // 等待所有异步操作完成，并找到最小的压缩结果
    std::vector<std::pair<Mode::Mode, std::vector<uint8_t>>> results;
    results.reserve(futures.size());
for (auto& future : futures) {
        results.push_back(future.get());
    }

    auto smallestResultIt = std::min_element(results.begin(), results.end(),
                                             [](const auto& a, const auto& b) { return a.second.size() < b.second.size(); });

    // 返回最小的压缩结果
    return smallestResultIt->second;
}


// 智能解压函数
std::vector<uint8_t> SilkCasket::Compress::smartDecompress(const std::vector<uint8_t>& compressedData) {
    using namespace SilkCasket::Compress::Mode;

    // 确保数据至少包含一个整数大小
    if (compressedData.size() < sizeof(int)) {
        LOG(LogLevel::ERROR, "SilkCasket::Compress", "smartDecompress", "压缩数据格式错误！");
        return {};
    }

    // 从数据中提取压缩模式
    int modeValue;
    std::memcpy(&modeValue, &compressedData[0], sizeof(int));

    // 移除模式标识符，获取实际的压缩数据
    std::vector<uint8_t> actualData(compressedData.begin() + sizeof(int), compressedData.end());

    switch (modeValue) {
        case Mode::Storage: {
            LOG(LogLevel::INFO, "SilkCasket::Compress", "smartDecompress", "仅存储压缩");
            return actualData;
        }
        case Mode::LZMA2FAST: {
            LOG(LogLevel::INFO, "SilkCasket::Compress", "smartDecompress", "LZMA2-Fast压缩");
            auto decompressedData = LZMA2_Fast::decompress(actualData);
            return decompressedData;
        }
        case Mode::LZ4: {
            LOG(LogLevel::INFO, "SilkCasket::Compress", "smartDecompress", "LZ4压缩");
            auto decompressedData = LZ4::decompress(actualData);
            return decompressedData;
        }
        case Mode::LZW: {
            LOG(LogLevel::INFO, "SilkCasket::Compress", "smartDecompress", "LZW压缩");
            auto decompressedData = LZW::decompress(actualData);
            return decompressedData;
        }
        case Mode::LIZARD: {
            LOG(LogLevel::INFO, "SilkCasket::Compress", "smartDecompress", "Lizard压缩");
            auto decompressedData = Lizard::decompress(actualData);
            return decompressedData;
        }
        default:
            LOG(LogLevel::ERROR, "SilkCasket::Compress", "smartDecompress", "未知的压缩模式");
            return actualData;
    }
}