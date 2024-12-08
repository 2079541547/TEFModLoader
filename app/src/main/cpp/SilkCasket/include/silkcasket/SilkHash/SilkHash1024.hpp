/*******************************************************************************
 * 文件名称: SilkHash1024
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
#include <array>
#include <algorithm>

class SilkHash1024
{
public:
    struct HashResult
    {
        uint64_t part1;
        uint64_t part2;
        uint64_t part3;
        uint64_t part4;
        uint64_t part5;
        uint64_t part6;
        uint64_t part7;
        uint64_t part8;
        uint64_t part9;
        uint64_t part10;
        uint64_t part11;
        uint64_t part12;
        uint64_t part13;
        uint64_t part14;
        uint64_t part15;
        uint64_t part16;
    };

    static HashResult hash(const uint8_t *input, size_t length)
    {
        std::vector<uint8_t> paddedInput = padInput(input, length);
        return computeHash(paddedInput.data(), paddedInput.size());
    }

    static HashResult hashFile(const std::string &filename)
    {
        std::ifstream file(filename, std::ios::binary);
        if (!file.is_open())
        {
            throw std::runtime_error("Failed to open file: " + filename);
        }

        std::vector<uint8_t> buffer(4096);
        std::vector<uint8_t> fullData;

        while (file.read(reinterpret_cast<char *>(buffer.data()), buffer.size()))
        {
            fullData.insert(fullData.end(), buffer.begin(), buffer.begin() + file.gcount());
        }

        size_t remaining = file.gcount();
        if (remaining > 0)
        {
            fullData.insert(fullData.end(), buffer.begin(), buffer.begin() + remaining);
        }

        return hash(fullData.data(), fullData.size());
    }

    static HashResult hashString(const std::string &str)
    {
        std::vector<uint8_t> bytes(str.begin(), str.end());
        return hash(bytes.data(), bytes.size());
    }

private:
    static uint64_t rotateLeft(uint64_t value, int shift)
    {
        return (value << shift) | (value >> (64 - shift));
    }

    static uint64_t generateSeed(const uint8_t *input, size_t length, size_t index)
    {
        std::hash<std::string> hasher;
        std::string str(reinterpret_cast<const char *>(input), length);
        std::string key = str + std::to_string(index);
        return static_cast<uint64_t>(hasher(key) & 0xFFFFFFFFFFFFFFFF);
    }

    static uint64_t generateRotation(const uint8_t *input, size_t length, size_t index)
    {
        std::hash<std::string> hasher;
        std::string str(reinterpret_cast<const char *>(input), length);
        std::string key = str + std::to_string(index);
        uint64_t rotation = static_cast<uint64_t>(hasher(key) & 0x3F);
        return rotation;
    }

    static uint64_t generateMixConstant(const uint8_t *input, size_t length, size_t index)
    {
        std::hash<std::string> hasher;
        std::string str(reinterpret_cast<const char *>(input), length);
        std::string key = str + std::to_string(index);
        return static_cast<uint64_t>(hasher(key) & 0xFFFFFFFFFFFFFFFF);
    }

    static uint64_t mix(uint64_t hash, size_t inputLength, size_t index, uint64_t mixConstant)
    {
        const uint64_t c3 = 0x6b8b45b3;
        const uint64_t c4 = 0x52dce729;
        uint64_t mixValue = (hash ^ (inputLength + index)) * c3;
        mixValue = rotateLeft(mixValue, 17);
        mixValue *= c4;
        return hash ^ (mixValue ^ mixConstant);
    }

    static uint64_t generateSpecialValue(const uint8_t *input, size_t length, size_t featureIndex)
    {
        std::hash<std::string> hasher;
        std::string str(reinterpret_cast<const char *>(input), length);
        std::string key = str + std::to_string(featureIndex);
        return static_cast<uint64_t>(hasher(key) & 0xFFFFFFFFFFFFFFFF);
    }

    static void applySpecialValues(std::array<uint64_t, 16> &hashes, const uint8_t *input, size_t length)
    {
        for (size_t i = 1; i <= 16; ++i)
        {
            uint64_t specialValue = generateSpecialValue(input, length, i);
            hashes[i - 1] ^= specialValue;
            hashes[i - 1] = rotateLeft(hashes[i - 1], 7) * 0xff51afd7ed558ccd;
        }

        hashes[0] ^= 19491001; // 新中国成立时间（1949年10月1日）
        hashes[1] ^= -1300101; // 丝绸之路的起源时间（公元前130年1月1日）
        hashes[2] ^= 0xadfee553feab2000;
        hashes[3] ^= 0xd1661ffad388e000;
        hashes[4] ^= 0xa7ced5bc08391800;
        hashes[5] ^= 0xe4c3297d1b8a8800;
        hashes[6] ^= 0x4b1eabfeacf50400;
        hashes[7] ^= 0xc3d8ccf7557b900;
        hashes[8] ^= 0x3d54503815688800;
        hashes[9] ^= 0xaa8af1afe257e000;
        hashes[10] ^= 0xa04a6a287e8f2000;
        hashes[11] ^= 0xf7c7edb9e84bd800;
        hashes[12] ^= 0x4a8f03b7855d7400;
        hashes[13] ^= 0x82fdb1929c3be000;
        hashes[14] ^= 0x80f7d0d2e7760000;
        hashes[15] ^= 0xafdab951dcdc5800;
    }

    static uint64_t disruptData(const uint8_t *input, size_t length, size_t index)
    {
        std::hash<std::string> hasher;
        std::string str(reinterpret_cast<const char *>(input), length);
        std::string key = str + std::to_string(index);
        uint64_t disruption = static_cast<uint64_t>(hasher(key) & 0xFFFFFFFFFFFFFFFF);
        return disruption;
    }

    static std::vector<uint8_t> insertRandomBytes(const uint8_t *input, size_t length)
    {
        std::vector<uint8_t> inputData(input, input + length);
        std::vector<uint8_t> randomBytes;
        size_t numInsertions = 20;

        for (size_t i = 0; i < numInsertions; ++i)
        {
            uint8_t randomByte = static_cast<uint8_t>(generateSeed(input, length, i) % 256);
            randomBytes.push_back(randomByte);
        }

        std::vector<size_t> insertionPoints(numInsertions);
        for (size_t i = 0; i < numInsertions; ++i)
        {
            insertionPoints[i] = (generateSeed(input, length, i) + i) % (length + i);
        }

        std::sort(insertionPoints.begin(), insertionPoints.end());

        for (size_t i = 0; i < numInsertions; ++i)
        {
            inputData.insert(inputData.begin() + insertionPoints[i], randomBytes[i]);
        }

        return inputData;
    }

    static std::vector<uint8_t> padInput(const uint8_t *input, size_t length)
    {
        std::vector<uint8_t> inputData = insertRandomBytes(input, length);
        size_t targetSize = 8192;
        if (inputData.size() >= targetSize)
        {
            return inputData;
        }

        size_t paddingLength = targetSize - inputData.size();

        for (size_t i = 0; i < paddingLength; ++i)
        {
            uint8_t paddingByte = generatePaddingByte(input, length, i);
            inputData.push_back(paddingByte);
        }

        return inputData;
    }

    static uint8_t generatePaddingByte(const uint8_t *input, size_t length, size_t index)
    {
        std::hash<std::string> hasher;
        std::string str(reinterpret_cast<const char *>(input), length);
        std::string key = str + std::to_string(index);
        uint64_t hashValue = hasher(key);
        return static_cast<uint8_t>(hashValue & 0xFF);
    }

    static HashResult computeHash(const uint8_t *input, size_t length)
    {
        std::array<uint64_t, 16> seeds;
        for (size_t i = 0; i < 16; ++i)
        {
            seeds[i] = generateSeed(input, length, i);
        }

        uint64_t c1 = 0x9ddfea08eb382d69;
        uint64_t c2 = 0xa7f5f35426a8e989;
        uint64_t m = 0xff51afd7ed558ccd;
        uint64_t n = 0xc4ceb9fe1a85ec53;

        std::array<uint64_t, 16> hashes(seeds);

        for (size_t i = 0; i < length; i += 8)
        {
            size_t chunkSize = std::min<size_t>(8, length - i);
            uint64_t k = 0;
            for (size_t j = 0; j < chunkSize; ++j)
            {
                k |= static_cast<uint64_t>(input[i + j]) << (j * 8);
            }

            k ^= disruptData(input, length, i);
            k *= c1;
            k = rotateLeft(k, generateRotation(input, length, i));
            k *= c2;

            for (size_t h = 0; h < 16; ++h)
            {
                hashes[h] ^= k;
                hashes[h] = rotateLeft(hashes[h], generateRotation(input, length, h)) * m + n;
                hashes[h] = mix(hashes[h], length, i, generateMixConstant(input, length, i));

                for (int round = 0; round < 64; ++round)
                {
                    hashes[h] ^= rotateLeft(hashes[h], 19);
                    hashes[h] *= 0xd6e8feb866cc9c03;
                    hashes[h] ^= rotateLeft(hashes[h], 23);
                    hashes[h] *= 0xcaaf00ab1889d485;
                }
            }
        }

        for (size_t h = 0; h < 16; ++h)
        {
            hashes[h] ^= length;
            hashes[h] ^= hashes[h] >> 32;
            hashes[h] *= 0xff51afd7ed558ccd;
            hashes[h] ^= hashes[h] >> 32;
            hashes[h] *= 0xc4ceb9fe1a85ec53;
            hashes[h] ^= hashes[h] >> 32;
        }

        applySpecialValues(hashes, input, length);

        for (size_t h = 0; h < 16; ++h)
        {
            for (int round = 0; round < 128; ++round)
            {
                hashes[h] ^= rotateLeft(hashes[h], 17);
                hashes[h] *= 0xe08ff6fa0df1bbcb;
                hashes[h] ^= rotateLeft(hashes[h], 31);
                hashes[h] *= 0xafd7db2bd41cf79;
            }
        }

        for (size_t h = 0; h < 16; ++h)
        {
            for (int loop = 0; loop < 500000; ++loop)
            {
                hashes[h] ^= rotateLeft(hashes[h], 13);
                hashes[h] *= 0x8a91a6d40bf42040;
                hashes[h] ^= rotateLeft(hashes[h], 29);
                hashes[h] *= 0xcbf29ce484222325;
            }
        }

        return {hashes[0], hashes[1], hashes[2], hashes[3],
                hashes[4], hashes[5], hashes[6], hashes[7],
                hashes[8], hashes[9], hashes[10], hashes[11],
                hashes[12], hashes[13], hashes[14], hashes[15]};
    }
};