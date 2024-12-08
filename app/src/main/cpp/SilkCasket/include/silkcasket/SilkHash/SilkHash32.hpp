/*******************************************************************************
 * 文件名称: SilkHash32
 * 项目名称: Silk Casket
 * 创建时间: 2024/11/22 下午8:57
 * 作者: EternalFuture゙
 * Github: https://github.com/2079541547
 * 版权声明: Copyright © 2024 EternalFuture゙. All rights reserved.
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
#include <string>
#include <cstdint>
#include <cstring>
#include <algorithm>
#include <unordered_set>
#include <random>
#include <chrono>
#include <fstream>
#include <vector>

class SilkHash32 {
public:
    // 直接传入 uint8_t 数组
    static uint32_t hash(const uint8_t* input, size_t length) {
        uint32_t seed = generateSeed(input, length);
        uint32_t c1 = generateConstant(input, length, 1);
        uint32_t c2 = generateConstant(input, length, 2);
        uint32_t r1 = generateRotation(input, length, 1);
        uint32_t r2 = generateRotation(input, length, 2);
        uint32_t m = 5;
        uint32_t n = 0xe6546b64;

        uint32_t hash = seed;

        for (size_t i = 0; i < length; i += 4) {
            size_t chunkSize = std::min<size_t>(4, length - i);
            uint32_t k = 0;
            for (size_t j = 0; j < chunkSize; ++j) {
                k |= static_cast<uint32_t>(input[i + j]) << (j * 8);
            }

            // 破坏原数据
            k ^= disruptData(input, length, i);

            k *= c1;
            k = rotateLeft(k, r1);
            k *= c2;

            hash ^= k;
            hash = rotateLeft(hash, r2) * m + n;
            hash = mix(hash, length, i, generateMixConstant(input, length, i));
        }

        // 最终混合
        hash ^= length;
        hash ^= hash >> 16;
        hash *= 0x85ebca6b;
        hash ^= hash >> 13;
        hash *= 0xc2b2ae35;
        hash ^= hash >> 16;

        hash = applySpecialValues(hash, input, length);

        return hash;
    }

    // 对文件进行校验
    static uint32_t hashFile(const std::string& filename) {
        std::ifstream file(filename, std::ios::binary);
        if (!file.is_open()) {
            throw std::runtime_error("Failed to open file: " + filename);
        }

        std::vector<uint8_t> buffer(4096);
        uint32_t hash = 0;

        while (file.read(reinterpret_cast<char*>(buffer.data()), buffer.size())) {
            size_t bytesRead = file.gcount();
            hash = mixHash(hash, buffer.data(), bytesRead);
        }

        // 处理剩余未满缓冲区的数据
        size_t remaining = file.gcount();
        if (remaining > 0) {
            hash = mixHash(hash, buffer.data(), remaining);
        }

        return hash;
    }

    // 对字符串进行校验
    static uint32_t hashString(const std::string& str) {
        std::vector<uint8_t> bytes(str.begin(), str.end());
        return hash(bytes.data(), bytes.size());
    }

private:
    static uint32_t rotateLeft(uint32_t value, int shift) {
        return (value << shift) | (value >> (32 - shift));
    }

    static uint32_t generateSeed(const uint8_t* input, size_t length) {
        std::hash<std::string> hasher;
        std::string str(reinterpret_cast<const char*>(input), length);
        return static_cast<uint32_t>(hasher(str) & 0xFFFFFFFF);
    }

    static uint32_t generateConstant(const uint8_t* input, size_t length, int index) {
        std::hash<std::string> hasher;
        std::string str(reinterpret_cast<const char*>(input), length);
        std::string key = str + std::to_string(index);
        return static_cast<uint32_t>(hasher(key) & 0xFFFFFFFF);
    }

    static uint32_t generateRotation(const uint8_t* input, size_t length, int index) {
        std::hash<std::string> hasher;
        std::string str(reinterpret_cast<const char*>(input), length);
        std::string key = str + std::to_string(index);
        uint32_t rotation = static_cast<uint32_t>(hasher(key) & 0x1F); // 限制在0-31之间
        return rotation;
    }

    static uint32_t generateMixConstant(const uint8_t* input, size_t length, size_t index) {
        std::hash<std::string> hasher;
        std::string str(reinterpret_cast<const char*>(input), length);
        std::string key = str + std::to_string(index);
        return static_cast<uint32_t>(hasher(key) & 0xFFFFFFFF);
    }

    static uint32_t mix(uint32_t hash, size_t inputLength, size_t index, uint32_t mixConstant) {
        const uint32_t c3 = 0x6b8b45b3;
        const uint32_t c4 = 0x52dce729;
        uint32_t mixValue = (hash ^ (inputLength + index)) * c3;
        mixValue = rotateLeft(mixValue, 17);
        mixValue *= c4;
        return hash ^ (mixValue ^ mixConstant);
    }

    static uint32_t generateSpecialValue(const uint8_t* input, size_t length, size_t featureIndex) {
        std::hash<std::string> hasher;
        std::string str(reinterpret_cast<const char*>(input), length);
        std::string key = str + std::to_string(featureIndex);
        return static_cast<uint32_t>(hasher(key) & 0xFFFFFFFF);
    }

    static uint32_t applySpecialValues(uint32_t hash, const uint8_t* input, size_t length) {
        uint32_t specialValue1 = generateSpecialValue(input, length, 1);
        uint32_t specialValue2 = generateSpecialValue(input, length, 2);
        uint32_t specialValue3 = generateSpecialValue(input, length, 3);

        hash ^= specialValue1;
        hash = rotateLeft(hash, 7) * 0x9e3779b9;
        hash ^= specialValue2;
        hash = rotateLeft(hash, 11) * 0x9e3779b9;
        hash ^= specialValue3;
        hash = rotateLeft(hash, 13) * 0x9e3779b9;

        hash ^= 19491001; // 新中国成立时间（1949年10月1日）
        hash ^= -1300101;  // 丝绸之路的起源时间（公元前130年1月1日）

        return hash;
    }

    static uint32_t disruptData(const uint8_t* input, size_t length, size_t index) {
        std::hash<std::string> hasher;
        std::string str(reinterpret_cast<const char*>(input), length);
        std::string key = str + std::to_string(index);
        uint32_t disruption = static_cast<uint32_t>(hasher(key) & 0xFFFFFFFF);
        return disruption;
    }

    static uint32_t mixHash(uint32_t currentHash, const uint8_t* input, size_t length) {
        uint32_t newHash = hash(input, length);
        return currentHash ^ newHash;
    }
};
