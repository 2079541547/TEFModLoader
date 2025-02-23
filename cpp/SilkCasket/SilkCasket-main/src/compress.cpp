/*******************************************************************************
 * 文件名称: compress
 * 项目名称: SilkCasket
 * 创建时间: 2025/1/15
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

#include <compress.hpp>
#include <iostream>
#include <future>
#include <utils.hpp>
#include <algorithm>
#include <unordered_map>
#include <fstream>
#include <lz4.h>
#include <lizard_compress.h>
#include <lizard_decompress.h>
#include <fast-lzma2.h>

std::vector<uint8_t>
compressData(const std::vector<uint8_t> &data, SilkCasket::Mode mode, size_t blockSize = 8096 * 1024) {
    switch (mode) {
        case SilkCasket::Mode::Storage:
        {
            std::vector<uint8_t> result;
            auto _a = data;
            int modeValue = static_cast<int>(mode);
            result.insert(result.end(), reinterpret_cast<const uint8_t*>(&modeValue), reinterpret_cast<const uint8_t*>(&modeValue) + sizeof(int));
            result.insert(result.end(), _a.begin(), _a.end());
            return result;
        }
        case SilkCasket::Mode::LZMA2FAST: {
            std::vector<uint8_t> result;
            auto _a = SilkCasket::LZMA2Fast::compress(data, blockSize);
            int modeValue = static_cast<int>(mode);
            result.insert(result.end(), reinterpret_cast<const uint8_t*>(&modeValue), reinterpret_cast<const uint8_t*>(&modeValue) + sizeof(int));
            result.insert(result.end(), _a.begin(), _a.end());
            return result;
        }
        case SilkCasket::Mode::LZ4: {
            std::vector<uint8_t> result;
            auto _a = SilkCasket::LZ4::compress(data, blockSize);
            int modeValue = static_cast<int>(mode);
            result.insert(result.end(), reinterpret_cast<const uint8_t*>(&modeValue), reinterpret_cast<const uint8_t*>(&modeValue) + sizeof(int));
            result.insert(result.end(), _a.begin(), _a.end());
            return result;
        }
        case SilkCasket::Mode::LZW: {
            std::vector<uint8_t> result;
            auto _a = SilkCasket::LZW::compress(data);
            int modeValue = static_cast<int>(mode);
            result.insert(result.end(), reinterpret_cast<const uint8_t*>(&modeValue), reinterpret_cast<const uint8_t*>(&modeValue) + sizeof(int));
            result.insert(result.end(), _a.begin(), _a.end());
            return result;
        }
        case SilkCasket::Mode::LIZARD: {
            std::vector<uint8_t> result;
            auto _a = SilkCasket::Lizard::compress(data, blockSize);
            int modeValue = static_cast<int>(mode);
            result.insert(result.end(), reinterpret_cast<const uint8_t*>(&modeValue), reinterpret_cast<const uint8_t*>(&modeValue) + sizeof(int));
            result.insert(result.end(), _a.begin(), _a.end());
            return result;
        }
        default:
            std::cerr << "Unknown compression mode" << std::endl;
    }
}

std::future<std::pair<SilkCasket::Mode, std::filesystem::path>>
asyncCompressData(const std::vector<uint8_t>& data, SilkCasket::Mode mode, size_t blockSize = 8096 * 1024) {
    return std::async(std::launch::async, [&data, mode, &blockSize](){
        auto compressedData = compressData(data, mode, blockSize);
        auto tempFile = SilkCasket::temp_path / ("compressed_" + std::to_string(static_cast<int>(mode)) + ".tmp");
        SilkCasket::Utils::Vuint8ToFile(tempFile, compressedData);
        return std::make_pair(mode, tempFile);
    });
}

std::vector<uint8_t>
SilkCasket::Compress::smartCompress(const std::filesystem::path &inputFile, const SilkCasket::MODE &compress,
                                    size_t blockSize) {

    if (!exists(inputFile)) {
        return {};
    }

    auto data = Utils::readFile(inputFile);

    std::vector<std::future<std::pair<SilkCasket::Mode, std::filesystem::path>>> futures;

    if (compress.Storage) futures.push_back(asyncCompressData(data, SilkCasket::Mode::Storage, blockSize));
    if (compress.LZMA2FAST) futures.push_back(asyncCompressData(data, SilkCasket::Mode::LZMA2FAST, blockSize));
    if (compress.LZ4) futures.push_back(asyncCompressData(data, SilkCasket::Mode::LZ4, blockSize));
    if (compress.LZW) futures.push_back(asyncCompressData(data, SilkCasket::Mode::LZW, blockSize));
    if (compress.LIZARD) futures.push_back(asyncCompressData(data, SilkCasket::Mode::LIZARD, blockSize));

    if (futures.empty()) {
        return {};
    }

    std::vector<std::pair<SilkCasket::Mode, std::filesystem::path>> results;
    results.reserve(futures.size());
    for (auto& future : futures) {
        results.push_back(future.get());
    }

    auto smallestResultIt = std::min_element(results.begin(), results.end(),
                                             [](const auto& a, const auto& b) { return file_size(a.second) < file_size(b.second); });

    std::vector<uint8_t> result = Utils::readFile(smallestResultIt->second);

    for (const auto& [mode, path] : results) {
        remove(path);
    }

    return result;
}

std::future<std::pair<SilkCasket::Mode, std::vector<uint8_t>>>
V8asyncCompressData(const std::vector<uint8_t>& data, SilkCasket::Mode mode, size_t blockSize) {
    return std::async(std::launch::async, [&data, mode, blockSize](){
        auto compressedData = compressData(data, mode, blockSize);
        return std::make_pair(mode, compressedData);
    });
}

std::vector<uint8_t>
SilkCasket::Compress::smartCompress(const std::vector<uint8_t> &inputData, const SilkCasket::MODE &compress,
                                    size_t blockSize) {

    if (inputData.empty()) {
        return {};
    }

    std::vector<std::future<std::pair<SilkCasket::Mode, std::vector<uint8_t>>>> futures;

    if (compress.Storage) futures.push_back(V8asyncCompressData(inputData, Mode::Storage, blockSize));
    if (compress.LZMA2FAST) futures.push_back(V8asyncCompressData(inputData, Mode::LZMA2FAST, blockSize));
    if (compress.LZ4) futures.push_back(V8asyncCompressData(inputData, Mode::LZ4, blockSize));
    if (compress.LZW) futures.push_back(V8asyncCompressData(inputData, Mode::LZW, blockSize));
    if (compress.LIZARD) futures.push_back(V8asyncCompressData(inputData, Mode::LIZARD, blockSize));

    if (futures.empty()) {
        return {};
    }

    std::vector<std::pair<SilkCasket::Mode, std::vector<uint8_t>>> results;
    results.reserve(futures.size());
    for (auto& future : futures) {
        results.push_back(future.get());
    }

    auto smallestResultIt = std::min_element(results.begin(), results.end(),
                                             [](const auto& a, const auto& b) { return a.second.size() < b.second.size(); });

    return smallestResultIt->second;
}

std::vector<uint8_t>
SilkCasket::Compress::smartDecompress(const std::vector<uint8_t> &compressedData) {

    if (compressedData.size() < sizeof(int)) {
        throw std::runtime_error("compressedData is null!");
    }

    int modeValue;
    std::memcpy(&modeValue, &compressedData[0], sizeof(int));

    std::vector<uint8_t> actualData(compressedData.begin() + sizeof(int), compressedData.end());

    switch (modeValue) {
        case Mode::Storage: {
            return actualData;
        }
        case Mode::LZMA2FAST: {
            auto decompressedData = LZMA2Fast::decompress(actualData);
            return decompressedData;
        }
        case Mode::LZ4: {
            auto decompressedData = LZ4::decompress(actualData);
            return decompressedData;
        }
        case Mode::LZW: {
            auto decompressedData = LZW::decompress(actualData);
            return decompressedData;
        }
        case Mode::LIZARD: {
            auto decompressedData = Lizard::decompress(actualData);
            return decompressedData;
        }
        default:
            std::cerr << "Unknown compression mode:" << modeValue << std::endl;
            return actualData;
    }
}


std::vector<uint8_t> SilkCasket::LZW::encodeVLE(uint32_t value) {
    std::vector<uint8_t> encoded;
    do {
        uint8_t byte = value & 0x7f;
        value >>= 7;
        if (value > 0) {
            byte |= 0x80;
        }
        encoded.push_back(byte);
    } while (value > 0);

    return encoded;
}

uint32_t SilkCasket::LZW::decodeVLE(const std::vector<uint8_t> &bytes, size_t &index) {
    uint32_t value = 0;
    uint32_t shift = 0;
    while (index < bytes.size()) {
        uint8_t byte = bytes[index++];
        value |= (byte & 0x7f) << shift;
        if (!(byte & 0x80)) {
            break;
        }
        shift += 7;
    }

    return value;
}

std::vector<uint8_t> SilkCasket::LZW::compress(const std::vector<uint8_t> &data) {
    std::unordered_map<std::string, uint32_t> dictionary;

    for (int i = 0; i <= 255; ++i) {
        dictionary[std::string(1, static_cast<char>(i))] = static_cast<uint32_t>(i);
    }

    std::vector<uint8_t> result;
    std::string prefix;
    size_t maxDictSize = 4096;

    for (auto byte : data) {
        std::string candidate = prefix + static_cast<char>(byte);

        if (dictionary.find(candidate) != dictionary.end()) {
            prefix = candidate;
        } else {
            auto encodedValue = encodeVLE(dictionary[prefix]);
            result.insert(result.end(), encodedValue.begin(), encodedValue.end());

            if (dictionary.size() >= maxDictSize) {
                maxDictSize += 4096 / 2;
            }

            dictionary[candidate] = static_cast<uint32_t>(dictionary.size());

            prefix = std::string(1, static_cast<char>(byte));
        }
    }

    if (!prefix.empty()) {
        auto encodedValue = encodeVLE(dictionary[prefix]);
        result.insert(result.end(), encodedValue.begin(), encodedValue.end());
    }

    return result;
}

std::vector<uint8_t> SilkCasket::LZW::decompress(const std::vector<uint8_t> &compressed) {
    std::unordered_map<uint32_t, std::string> dictionary;
    for (int i = 0; i <= 255; ++i)
    {
        dictionary[i] = std::string(1, static_cast<char>(i));
    }

    std::vector<uint8_t> result;
    size_t index = 0;
    uint32_t old = decodeVLE(compressed, index);
    result.push_back(static_cast<uint8_t>(old));

    while (index < compressed.size())
    {
        uint32_t entry = decodeVLE(compressed, index);
        std::string s;
        if (dictionary.find(entry) == dictionary.end())
        {
            s = dictionary[old] + dictionary[old].substr(0, 1);
        }
        else
        {
            s = dictionary[entry];
        }

        for (char c : s)
        {
            result.push_back(static_cast<uint8_t>(c));
        }

        dictionary[dictionary.size()] = dictionary[old] + s.substr(0, 1);
        old = entry;
    }

    return result;
}

std::vector<uint8_t> SilkCasket::LZ4::compress(const std::vector<uint8_t> &data, size_t blockSize) {
    auto tempFilePath = SilkCasket::temp_path / "temp_compressed_data.lz4";
    std::ofstream tempFile(tempFilePath, std::ios::binary);
    if (!tempFile.is_open()) {
        throw std::runtime_error("Failed to open temporary file for compression.");
    }

    tempFile.write(reinterpret_cast<const char*>(&blockSize), sizeof(blockSize));

    for (size_t i = 0; i < data.size(); i += blockSize) {
        const size_t chunkSize = std::min(blockSize, data.size() - i);
        const size_t maxCompressedChunkSize = LZ4_compressBound(chunkSize);
        auto* compressedChunk = new uint8_t[maxCompressedChunkSize];

        const size_t compressedChunkSize = LZ4_compress_default(
                reinterpret_cast<const char*>(data.data() + i),
                reinterpret_cast<char*>(compressedChunk),
                chunkSize,
                maxCompressedChunkSize
        );

        if (compressedChunkSize > 0) {
            tempFile.write(reinterpret_cast<const char*>(compressedChunk), (long)compressedChunkSize);
        } else {
            delete[] compressedChunk;
            throw std::runtime_error("Compression of a chunk failed.");
        }

        delete[] compressedChunk;
    }
    tempFile.close();

    std::ifstream tempFileIn(tempFilePath, std::ios::binary | std::ios::ate);
    if (!tempFileIn.is_open()) {
        throw std::runtime_error("Failed to open temporary file for reading.");
    }

    std::streamsize size = tempFileIn.tellg();
    std::vector<uint8_t> compressedData(size);
    tempFileIn.seekg(0, std::ios::beg);
    tempFileIn.read(reinterpret_cast<char*>(compressedData.data()), size);
    tempFileIn.close();

    std::filesystem::remove(tempFilePath);

    return compressedData;
}

std::vector<uint8_t> SilkCasket::LZ4::decompress(const std::vector<uint8_t> &compressed) {
    auto tempFilePath = SilkCasket::temp_path / "temp_compressed_data.lz4";
    std::ofstream tempFile(tempFilePath, std::ios::binary);
    if (!tempFile.is_open()) {
        throw std::runtime_error("Failed to open temporary file for decompression.");
    }

    size_t blockSize;
    std::memcpy(&blockSize, compressed.data(), sizeof(blockSize));

    for (size_t i = sizeof(blockSize); i < compressed.size(); i += blockSize) {
        const size_t chunkSize = std::min(blockSize, compressed.size() - i);
        auto* decompressedChunk = new uint8_t[chunkSize * 5];

        const size_t decompressedChunkSize = LZ4_decompress_safe(
                reinterpret_cast<const char*>(compressed.data() + i),
                reinterpret_cast<char*>(decompressedChunk),
                chunkSize,
                chunkSize * 5
        );

        if (decompressedChunkSize > 0) {
            tempFile.write(reinterpret_cast<const char*>(decompressedChunk), (long)decompressedChunkSize);
        } else {
            delete[] decompressedChunk;
            throw std::runtime_error("Decompression of a chunk failed.");
        }

        delete[] decompressedChunk;
    }
    tempFile.close();

    std::ifstream tempFileIn(tempFilePath, std::ios::binary | std::ios::ate);
    if (!tempFileIn.is_open()) {
        throw std::runtime_error("Failed to open temporary file for reading.");
    }

    std::streamsize size = tempFileIn.tellg();
    std::vector<uint8_t> decompressedData(size);
    tempFileIn.seekg(0, std::ios::beg);
    tempFileIn.read(reinterpret_cast<char*>(decompressedData.data()), size);
    tempFileIn.close();

    std::filesystem::remove(tempFilePath);

    return decompressedData;
}

std::vector<uint8_t> SilkCasket::Lizard::compress(const std::vector<uint8_t> &data, size_t blockSize) {
    auto tempFilePath = SilkCasket::temp_path / "temp_compressed_data.lizard";
    std::ofstream tempFile(tempFilePath, std::ios::binary);
    if (!tempFile.is_open()) {
        throw std::runtime_error("Failed to open temporary file for compression.");
    }

    tempFile.write(reinterpret_cast<const char*>(&blockSize), sizeof(blockSize));

    for (size_t i = 0; i < data.size(); i += blockSize) {
        const size_t chunkSize = std::min(blockSize, data.size() - i);
        const size_t maxCompressedChunkSize = Lizard_compressBound(chunkSize);
        auto* compressedChunk = new uint8_t[maxCompressedChunkSize];

        const size_t compressedChunkSize = Lizard_compress(
                reinterpret_cast<const char*>(data.data() + i),
                reinterpret_cast<char*>(compressedChunk),
                chunkSize,
                maxCompressedChunkSize,
                49
        );

        if (compressedChunkSize > 0) {
            tempFile.write(reinterpret_cast<const char*>(compressedChunk), (long)compressedChunkSize);
        } else {
            delete[] compressedChunk;
            throw std::runtime_error("Compression of a chunk failed.");
        }

        delete[] compressedChunk;
    }
    tempFile.close();

    std::ifstream tempFileIn(tempFilePath, std::ios::binary | std::ios::ate);
    if (!tempFileIn.is_open()) {
        throw std::runtime_error("Failed to open temporary file for reading.");
    }

    std::streamsize size = tempFileIn.tellg();
    std::vector<uint8_t> compressedData(size);
    tempFileIn.seekg(0, std::ios::beg);
    tempFileIn.read(reinterpret_cast<char*>(compressedData.data()), size);
    tempFileIn.close();

    std::filesystem::remove(tempFilePath);

    return compressedData;
}

std::vector<uint8_t> SilkCasket::Lizard::decompress(const std::vector<uint8_t> &compressed) {
    auto tempFilePath = SilkCasket::temp_path / "temp_compressed_data.lizard";
    std::ofstream tempFile(tempFilePath, std::ios::binary);
    if (!tempFile.is_open()) {
        throw std::runtime_error("Failed to open temporary file for decompression.");
    }

    size_t blockSize;
    std::memcpy(&blockSize, compressed.data(), sizeof(blockSize));

    for (size_t i = sizeof(blockSize); i < compressed.size(); i += blockSize) {
        const size_t chunkSize = std::min(blockSize, compressed.size() - i);
        auto* decompressedChunk = new uint8_t[chunkSize * 2];

        const size_t decompressedChunkSize = Lizard_decompress_safe(
                reinterpret_cast<const char*>(compressed.data() + i),
                reinterpret_cast<char*>(decompressedChunk),
                chunkSize,
                chunkSize * 2
        );

        if (decompressedChunkSize > 0) {
            tempFile.write(reinterpret_cast<const char*>(decompressedChunk), (long)decompressedChunkSize);
        } else {
            delete[] decompressedChunk;
            throw std::runtime_error("Decompression of a chunk failed.");
        }

        delete[] decompressedChunk;
    }
    tempFile.close();

    std::ifstream tempFileIn(tempFilePath, std::ios::binary | std::ios::ate);
    if (!tempFileIn.is_open()) {
        throw std::runtime_error("Failed to open temporary file for reading.");
    }

    std::streamsize size = tempFileIn.tellg();
    std::vector<uint8_t> decompressedData(size);
    tempFileIn.seekg(0, std::ios::beg);
    tempFileIn.read(reinterpret_cast<char*>(decompressedData.data()), size);
    tempFileIn.close();

    std::filesystem::remove(tempFilePath);

    return decompressedData;
}

std::vector<uint8_t> SilkCasket::LZMA2Fast::compress(const std::vector<uint8_t> &data, size_t blockSize) {

    auto tempFilePath = SilkCasket::temp_path / "temp_compressed_data.lzma2-fast";

    std::ofstream tempFile(tempFilePath, std::ios::binary);
    if (!tempFile.is_open()) {
        throw std::runtime_error("Failed to open temporary file for compression.");
    }

    const size_t inputSize = data.size();

    for (size_t i = 0; i < inputSize; i += blockSize) {
        const size_t chunkSize = std::min(blockSize, inputSize - i);
        const size_t maxCompressedChunkSize = FL2_compressBound(chunkSize);
        std::vector<uint8_t> compressedChunk(maxCompressedChunkSize + sizeof(size_t));

        std::memcpy(compressedChunk.data(), &chunkSize, sizeof(size_t));

        const size_t compressedChunkSize = FL2_compress(
                compressedChunk.data() + sizeof(size_t),
                maxCompressedChunkSize,
                data.data() + i,
                chunkSize,
                9
        );

        if (compressedChunkSize > 0) {
            compressedChunk.resize(compressedChunkSize + sizeof(size_t));
            tempFile.write(reinterpret_cast<const char*>(compressedChunk.data()), compressedChunk.size());
        } else {
            tempFile.close();
            throw std::runtime_error("Compression of a chunk failed.");
        }
    }
    tempFile.close();

    std::ifstream tempFileIn(tempFilePath, std::ios::binary | std::ios::ate);
    if (!tempFileIn.is_open()) {
        throw std::runtime_error("Failed to open temporary file for reading.");
    }

    std::streamsize size = tempFileIn.tellg();
    std::vector<uint8_t> compressedData(size);
    tempFileIn.seekg(0, std::ios::beg);
    tempFileIn.read(reinterpret_cast<char*>(compressedData.data()), size);

    if (tempFileIn.fail()) {
        throw std::runtime_error("Failed to read from temporary file.");
    }

    tempFileIn.close();

    if (!std::filesystem::remove(tempFilePath)) {
        std::cerr << "Warning: Failed to delete temporary file: " << tempFilePath << std::endl;
    }

    return compressedData;
}

std::vector<uint8_t> SilkCasket::LZMA2Fast::decompress(const std::vector<uint8_t> &compressed) {

    auto tempFilePath = SilkCasket::temp_path / "temp_compressed_data.lzma2-fast";

    std::ofstream tempFile(tempFilePath, std::ios::binary);
    if (!tempFile.is_open()) {
        throw std::runtime_error("Failed to open temporary file for decompression.");
    }

    size_t offset = 0;

    while (offset < compressed.size()) {
        size_t chunkSize;
        std::memcpy(&chunkSize, compressed.data() + offset, sizeof(size_t));
        offset += sizeof(size_t);

        const size_t compressedChunkSize = compressed.size() - offset;
        std::vector<uint8_t> decompressedChunk(chunkSize);

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
            tempFile.close();
            throw std::runtime_error("Decompression of a chunk failed or unexpected size.");
        }
    }
    tempFile.close();

    std::ifstream tempFileIn(tempFilePath, std::ios::binary | std::ios::ate);
    if (!tempFileIn.is_open()) {
        throw std::runtime_error("Failed to open temporary file for reading.");
    }

    std::streamsize size = tempFileIn.tellg();
    std::vector<uint8_t> decompressedData(size);
    tempFileIn.seekg(0, std::ios::beg);
    tempFileIn.read(reinterpret_cast<char*>(decompressedData.data()), size);

    if (tempFileIn.fail()) {
        throw std::runtime_error("Failed to read from temporary file.");
    }

    tempFileIn.close();

    if (!std::filesystem::remove(tempFilePath)) {
        std::cerr << "Warning: Failed to delete temporary file: " << tempFilePath << std::endl;
    }

    return decompressedData;
}
