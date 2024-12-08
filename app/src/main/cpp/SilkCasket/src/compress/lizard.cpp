/*******************************************************************************
 * 文件名称: lizard
 * 项目名称: SilkCasket
 * 创建时间: 2024/11/29
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


#include <compress/lizard.hpp>
#include <lizard_compress.h>
#include <lizard_decompress.h>
#include <filesystem>
#include <fstream>
#include <cstring>

std::vector<uint8_t> SilkCasket::Compress::Lizard::compress(const vector<uint8_t> &data, size_t blockSize) {
    auto tempFilePath = filesystem::temp_directory_path() / "temp_compressed_data.lizard";
    std::ofstream tempFile(tempFilePath, std::ios::binary);
    if (!tempFile.is_open()) {
        throw std::runtime_error("Failed to open temporary file for compression.");
    }

    // 写入块大小信息
    tempFile.write(reinterpret_cast<const char*>(&blockSize), sizeof(blockSize));

    for (size_t i = 0; i < data.size(); i += blockSize) {
        const size_t chunkSize = std::min(blockSize, data.size() - i);
        const size_t maxCompressedChunkSize = Lizard_compressBound(chunkSize);
        uint8_t* compressedChunk = new uint8_t[maxCompressedChunkSize];

        const size_t compressedChunkSize = Lizard_compress(
                reinterpret_cast<const char*>(data.data() + i),
                reinterpret_cast<char*>(compressedChunk),
                chunkSize,
                maxCompressedChunkSize,
                49
        );

        if (compressedChunkSize > 0) {
            tempFile.write(reinterpret_cast<const char*>(compressedChunk), compressedChunkSize);
        } else {
            delete[] compressedChunk;
            throw std::runtime_error("Compression of a chunk failed.");
        }

        delete[] compressedChunk;
    }
    tempFile.close();

    // 读取临时文件内容到内存
    std::ifstream tempFileIn(tempFilePath, std::ios::binary | std::ios::ate);
    if (!tempFileIn.is_open()) {
        throw std::runtime_error("Failed to open temporary file for reading.");
    }

    std::streamsize size = tempFileIn.tellg();
    std::vector<uint8_t> compressedData(size);
    tempFileIn.seekg(0, std::ios::beg);
    tempFileIn.read(reinterpret_cast<char*>(compressedData.data()), size);

    // 删除临时文件
    std::filesystem::remove(tempFilePath);

    return compressedData;
}


std::vector <uint8_t> SilkCasket::Compress::Lizard::decompress(const vector<uint8_t> &compressed) {
    auto tempFilePath = filesystem::temp_directory_path() / "temp_compressed_data.lizard";
    std::ofstream tempFile(tempFilePath, std::ios::binary);
    if (!tempFile.is_open()) {
        throw std::runtime_error("Failed to open temporary file for decompression.");
    }

    // 读取块大小信息
    size_t blockSize;
    std::memcpy(&blockSize, compressed.data(), sizeof(blockSize));

    for (size_t i = sizeof(blockSize); i < compressed.size(); i += blockSize) {
        const size_t chunkSize = std::min(blockSize, compressed.size() - i);
        auto* decompressedChunk = new uint8_t[chunkSize * 5]; // 假设解压后的数据不会超过原始数据大小的5倍

        const size_t decompressedChunkSize = Lizard_decompress_safe(
                reinterpret_cast<const char*>(compressed.data() + i),
                reinterpret_cast<char*>(decompressedChunk),
                chunkSize,
                chunkSize * 5
        );

        if (decompressedChunkSize > 0) {
            tempFile.write(reinterpret_cast<const char*>(decompressedChunk), decompressedChunkSize);
        } else {
            delete[] decompressedChunk;
            throw std::runtime_error("Decompression of a chunk failed.");
        }

        delete[] decompressedChunk;
    }
    tempFile.close();

    // 读取临时文件内容到内存
    std::ifstream tempFileIn(tempFilePath, std::ios::binary | std::ios::ate);
    if (!tempFileIn.is_open()) {
        throw std::runtime_error("Failed to open temporary file for reading.");
    }

    std::streamsize size = tempFileIn.tellg();
    std::vector<uint8_t> decompressedData(size);
    tempFileIn.seekg(0, std::ios::beg);
    tempFileIn.read(reinterpret_cast<char*>(decompressedData.data()), size);

    // 删除临时文件
    std::filesystem::remove(tempFilePath);

    return decompressedData;
}