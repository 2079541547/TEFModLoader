/*******************************************************************************
 * 文件名称: lzma2
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

#include <fast-lzma2.h>
#include <compress/lzma2-fast.hpp>
#include <filesystem>
#include <fstream>
#include <cstring>

std::vector <uint8_t> SilkCasket::Compress::LZMA2_Fast::compress(const vector<uint8_t> &data, size_t blockSize) {
    auto tempFilePath = std::filesystem::temp_directory_path() / "temp_compressed_data.lzma2-fast";
    std::ofstream tempFile(tempFilePath, std::ios::binary);
    if (!tempFile.is_open()) {
        throw std::runtime_error("Failed to open temporary file for compression.");
    }

    const size_t inputSize = data.size();

    for (size_t i = 0; i < inputSize; i += blockSize) {
        const size_t chunkSize = std::min(blockSize, inputSize - i);
        const size_t maxCompressedChunkSize = FL2_compressBound(chunkSize); // 获取最大压缩后的大小
        std::vector<uint8_t> compressedChunk(maxCompressedChunkSize + sizeof(size_t)); // 创建压缩后的存储空间，加上块大小

        // 写入块大小
        std::memcpy(compressedChunk.data(), &chunkSize, sizeof(size_t));

        // 进行压缩
        const size_t compressedChunkSize = FL2_compress(
                compressedChunk.data() + sizeof(size_t),  // 跳过头部
                maxCompressedChunkSize,
                data.data() + i,
                chunkSize,
                9  // 压缩级别
        );

        if (compressedChunkSize > 0) {
            compressedChunk.resize(compressedChunkSize + sizeof(size_t));  // 包括头部
            tempFile.write(reinterpret_cast<const char*>(compressedChunk.data()), compressedChunk.size());
        } else {
            // 压缩失败处理
            throw std::runtime_error("Compression of a chunk failed.");
        }
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


std::vector <uint8_t> SilkCasket::Compress::LZMA2_Fast::decompress(const vector<uint8_t> &compressed) {
    auto tempFilePath = std::filesystem::temp_directory_path() / "temp_compressed_data.lzma2-fast";
    std::ofstream tempFile(tempFilePath, std::ios::binary);
    if (!tempFile.is_open()) {
        throw std::runtime_error("Failed to open temporary file for decompression.");
    }

    size_t offset = 0;

    while (offset < compressed.size()) {
        // 读取块大小
        size_t chunkSize;
        std::memcpy(&chunkSize, compressed.data() + offset, sizeof(size_t));
        offset += sizeof(size_t);

        const size_t compressedChunkSize = compressed.size() - offset;
        std::vector<uint8_t> decompressedChunk(chunkSize);

        // 进行解压缩
        const size_t decompressedChunkSize = FL2_decompress(
                decompressedChunk.data(),
                chunkSize,
                compressed.data() + offset,
                compressedChunkSize
        );

        if (decompressedChunkSize > 0 && decompressedChunkSize == chunkSize) {
            tempFile.write(reinterpret_cast<const char*>(decompressedChunk.data()), decompressedChunk.size());
            offset += compressedChunkSize;
        } else {
            // 解压缩失败处理
            throw std::runtime_error("Decompression of a chunk failed or unexpected size.");
        }
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