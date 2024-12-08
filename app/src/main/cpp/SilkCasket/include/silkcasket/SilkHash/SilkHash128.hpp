/*******************************************************************************
 * 文件名称: SilkHash128
 * 项目名称: SilkCasket
 * 创建时间: 2024/11/22
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


#include <iostream>
#include <fstream>
#include <vector>
#include <string>
#include <cstring>
#include <functional>
#include <cstdint>
#include <numeric>
#include <random>
#include <chrono>
#include <thread>
#include <mutex>
#include <unordered_set>
#include <atomic>
#include <algorithm>

class SilkHash128 {
public:

    struct HashResult {
        uint64_t high;
        uint64_t low;
    };

    // 直接传入 uint8_t 数组
    static HashResult hash(const uint8_t* input, size_t length) {
        std::vector<uint8_t> paddedInput = padInput(input, length);
        return computeHash(paddedInput.data(), paddedInput.size());
    }

    // 对文件进行校验
    static HashResult hashFile(const std::string& filename) {
        std::ifstream file(filename, std::ios::binary);
        if (!file.is_open()) {
            throw std::runtime_error("Failed to open file: " + filename);
        }

        std::vector<uint8_t> buffer(4096);
        std::vector<uint8_t> fullData;

        while (file.read(reinterpret_cast<char*>(buffer.data()), buffer.size())) {
            fullData.insert(fullData.end(), buffer.begin(), buffer.begin() + file.gcount());
        }

        // 处理剩余未满缓冲区的数据
        size_t remaining = file.gcount();
        if (remaining > 0) {
            fullData.insert(fullData.end(), buffer.begin(), buffer.begin() + remaining);
        }

        return hash(fullData.data(), fullData.size());
    }

    // 对字符串进行校验
    static HashResult hashString(const std::string& str) {
        std::vector<uint8_t> bytes(str.begin(), str.end());
        return hash(bytes.data(), bytes.size());
    }

private:
    static uint64_t rotateLeft(uint64_t value, int shift) {
        return (value << shift) | (value >> (64 - shift));
    }

    static uint64_t generateSeed(const uint8_t* input, size_t length) {
        std::hash<std::string> hasher;
        std::string str(reinterpret_cast<const char*>(input), length);
        return static_cast<uint64_t>(hasher(str) & 0xFFFFFFFFFFFFFFFF);
    }

    static uint64_t generateRotation(const uint8_t* input, size_t length, int index) {
        std::hash<std::string> hasher;
        std::string str(reinterpret_cast<const char*>(input), length);
        std::string key = str + std::to_string(index);
        uint64_t rotation = static_cast<uint64_t>(hasher(key) & 0x3F); // 限制在0-63之间
        return rotation;
    }

    static uint64_t generateMixConstant(const uint8_t* input, size_t length, size_t index) {
        std::hash<std::string> hasher;
        std::string str(reinterpret_cast<const char*>(input), length);
        std::string key = str + std::to_string(index);
        return static_cast<uint64_t>(hasher(key) & 0xFFFFFFFFFFFFFFFF);
    }

    static uint64_t mix(uint64_t hash, size_t inputLength, size_t index, uint64_t mixConstant) {
        const uint64_t c3 = 0x6b8b45b3;
        const uint64_t c4 = 0x52dce729;
        uint64_t mixValue = (hash ^ (inputLength + index)) * c3;
        mixValue = rotateLeft(mixValue, 17);
        mixValue *= c4;
        return hash ^ (mixValue ^ mixConstant);
    }

    static uint64_t generateSpecialValue(const uint8_t* input, size_t length, size_t featureIndex) {
        std::hash<std::string> hasher;
        std::string str(reinterpret_cast<const char*>(input), length);
        std::string key = str + std::to_string(featureIndex);
        return static_cast<uint64_t>(hasher(key) & 0xFFFFFFFFFFFFFFFF);
    }

    static uint64_t applySpecialValues(uint64_t hash, const uint8_t* input, size_t length) {
        uint64_t specialValue1 = generateSpecialValue(input, length, 1);
        uint64_t specialValue2 = generateSpecialValue(input, length, 2);
        uint64_t specialValue3 = generateSpecialValue(input, length, 3);

        hash ^= specialValue1;
        hash = rotateLeft(hash, 7) * 0xff51afd7ed558ccd;
        hash ^= specialValue2;
        hash = rotateLeft(hash, 11) * 0xff51afd7ed558ccd;
        hash ^= specialValue3;
        hash = rotateLeft(hash, 13) * 0xff51afd7ed558ccd;

        hash ^= 19491001; // 新中国成立时间（1949年10月1日）
        hash ^= -1300101;  // 丝绸之路的起源时间（公元前130年1月1日）
        hash ^= 0xde9dc36bb2409000;

        return hash;
    }

    static uint64_t disruptData(const uint8_t* input, size_t length, size_t index) {
        std::hash<std::string> hasher;
        std::string str(reinterpret_cast<const char*>(input), length);
        std::string key = str + std::to_string(index);
        uint64_t disruption = static_cast<uint64_t>(hasher(key) & 0xFFFFFFFFFFFFFFFF);
        return disruption;
    }

    static uint64_t mixHash(uint64_t currentHash, const uint8_t* input, size_t length) {
        uint64_t newHash = hash(input, length).high ^ hash(input, length).low;
        return currentHash ^ newHash;
    }

    static std::vector<uint8_t> insertRandomBytes(const uint8_t* input, size_t length) {
        std::vector<uint8_t> inputData(input, input + length);
        std::vector<uint8_t> randomBytes;
        size_t numInsertions = 10; // 插入10个随机字节

        // 生成随机字节
        for (size_t i = 0; i < numInsertions; ++i) {
            uint8_t randomByte = static_cast<uint8_t>(generateSeed(input, length) % 256);
            randomBytes.push_back(randomByte);
        }

        // 计算插入位置
        std::vector<size_t> insertionPoints(numInsertions);
        for (size_t i = 0; i < numInsertions; ++i) {
            insertionPoints[i] = (generateSeed(input, length) + i) % (length + i);
        }

        // 排序插入位置以避免覆盖
        std::sort(insertionPoints.begin(), insertionPoints.end());

        // 插入随机字节
        for (size_t i = 0; i < numInsertions; ++i) {
            inputData.insert(inputData.begin() + insertionPoints[i], randomBytes[i]);
        }

        return inputData;
    }

    static std::vector<uint8_t> padInput(const uint8_t* input, size_t length) {
        std::vector<uint8_t> inputData = insertRandomBytes(input, length);
        size_t targetSize = 4096; // Increase target size for more padding
        if (inputData.size() >= targetSize) {
            return inputData;
        }

        // 计算需要填充的长度
        size_t paddingLength = targetSize - inputData.size();

        // 根据输入数据生成填充数据
        for (size_t i = 0; i < paddingLength; ++i) {
            uint8_t paddingByte = generatePaddingByte(input, length, i);
            inputData.push_back(paddingByte);
        }

        return inputData;
    }

    static uint8_t generatePaddingByte(const uint8_t* input, size_t length, size_t index) {
        std::hash<std::string> hasher;
        std::string str(reinterpret_cast<const char*>(input), length);
        std::string key = str + std::to_string(index);
        uint64_t hashValue = hasher(key);
        return static_cast<uint8_t>(hashValue & 0xFF);
    }

    static HashResult computeHash(const uint8_t* input, size_t length) {
        uint64_t seedHigh = generateSeed(input, length);
        uint64_t seedLow = generateSeed(input + 1, length - 1);
        uint64_t c1High = 0x9ddfea08eb382d69;
        uint64_t c1Low = 0xa7f5f35426a8e989;
        uint64_t c2High = 0xff51afd7ed558ccd;
        uint64_t c2Low = 0xc4ceb9fe1a85ec53;
        uint64_t r1High = generateRotation(input, length, 1);
        uint64_t r1Low = generateRotation(input + 1, length - 1, 2);
        uint64_t r2High = generateRotation(input, length, 3);
        uint64_t r2Low = generateRotation(input + 1, length - 1, 4);
        uint64_t mHigh = 0xd6e8feb866cc9c03;
        uint64_t mLow = 0xcaaf00ab1889d485;
        uint64_t nHigh = 0xe08ff6fa0df1bbcb;
        uint64_t nLow = 0xafd7db2bd41cf79;

        uint64_t hashHigh = seedHigh;
        uint64_t hashLow = seedLow;

        for (size_t i = 0; i < length; i += 8) {
            size_t chunkSize = std::min<size_t>(8, length - i);
            uint64_t kHigh = 0;
            uint64_t kLow = 0;
            for (size_t j = 0; j < chunkSize; ++j) {
                kHigh |= static_cast<uint64_t>(input[i + j]) << (j * 8);
                if (i + j + 1 < length) {
                    kLow |= static_cast<uint64_t>(input[i + j + 1]) << (j * 8);
                }
            }

            // 破坏原数据
            kHigh ^= disruptData(input, length, i);
            kLow ^= disruptData(input + 1, length - 1, i);

            kHigh *= c1High;
            kHigh = rotateLeft(kHigh, r1High);
            kHigh *= c2High;

            kLow *= c1Low;
            kLow = rotateLeft(kLow, r1Low);
            kLow *= c2Low;

            hashHigh ^= kHigh;
            hashHigh = rotateLeft(hashHigh, r2High) * mHigh + nHigh;
            hashHigh = mix(hashHigh, length, i, generateMixConstant(input, length, i));

            hashLow ^= kLow;
            hashLow = rotateLeft(hashLow, r2Low) * mLow + nLow;
            hashLow = mix(hashLow, length, i, generateMixConstant(input + 1, length - 1, i));

            // 多轮混合
            for (int round = 0; round < 32; ++round) {
                hashHigh ^= rotateLeft(hashHigh, 19);
                hashHigh *= 0xd6e8feb866cc9c03;
                hashHigh ^= rotateLeft(hashHigh, 23);
                hashHigh *= 0xcaaf00ab1889d485;

                hashLow ^= rotateLeft(hashLow, 19);
                hashLow *= 0xd6e8feb866cc9c03;
                hashLow ^= rotateLeft(hashLow, 23);
                hashLow *= 0xcaaf00ab1889d485;
            }
        }

        // 最终混合
        hashHigh ^= length;
        hashHigh ^= hashHigh >> 32;
        hashHigh *= 0xff51afd7ed558ccd;
        hashHigh ^= hashHigh >> 32;
        hashHigh *= 0xc4ceb9fe1a85ec53;
        hashHigh ^= hashHigh >> 32;

        hashLow ^= length;
        hashLow ^= hashLow >> 32;
        hashLow *= 0xff51afd7ed558ccd;
        hashLow ^= hashLow >> 32;
        hashLow *= 0xc4ceb9fe1a85ec53;
        hashLow ^= hashLow >> 32;

        hashHigh = applySpecialValues(hashHigh, input, length);
        hashLow = applySpecialValues(hashLow, input + 1, length - 1);

        // 多轮最终混合
        for (int round = 0; round < 64; ++round) {
            hashHigh ^= rotateLeft(hashHigh, 17);
            hashHigh *= 0xe08ff6fa0df1bbcb;
            hashHigh ^= rotateLeft(hashHigh, 31);
            hashHigh *= 0xafd7db2bd41cf79;

            hashLow ^= rotateLeft(hashLow, 17);
            hashLow *= 0xe08ff6fa0df1bbcb;
            hashLow ^= rotateLeft(hashLow, 31);
            hashLow *= 0xafd7db2bd41cf79;
        }

        // 添加额外的计算循环以增加计算时间
        for (int loop = 0; loop < 1000000; ++loop) {
            hashHigh ^= rotateLeft(hashHigh, 13);
            hashHigh *= 0x8a91a6d40bf42040;
            hashHigh ^= rotateLeft(hashHigh, 29);
            hashHigh *= 0xcbf29ce484222325;

            hashLow ^= rotateLeft(hashLow, 13);
            hashLow *= 0x8a91a6d40bf42040;
            hashLow ^= rotateLeft(hashLow, 29);
            hashLow *= 0xcbf29ce484222325;
        }

        return {hashHigh, hashLow};
    }
};