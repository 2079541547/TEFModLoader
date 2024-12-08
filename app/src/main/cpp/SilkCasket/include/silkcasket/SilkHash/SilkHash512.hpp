/*******************************************************************************
 * 文件名称: SilkHash512
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
#include <algorithm>

class SilkHash512
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
    };

    // 直接传入 uint8_t 数组
    static HashResult hash(const uint8_t *input, size_t length)
    {
        std::vector<uint8_t> paddedInput = padInput(input, length);
        return computeHash(paddedInput.data(), paddedInput.size());
    }

    // 对文件进行校验
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

        // 处理剩余未满缓冲区的数据
        size_t remaining = file.gcount();
        if (remaining > 0)
        {
            fullData.insert(fullData.end(), buffer.begin(), buffer.begin() + remaining);
        }

        return hash(fullData.data(), fullData.size());
    }

    // 对字符串进行校验
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

    static uint64_t generateSeed(const uint8_t *input, size_t length)
    {
        std::hash<std::string> hasher;
        std::string str(reinterpret_cast<const char *>(input), length);
        return static_cast<uint64_t>(hasher(str));
    }

    static uint64_t generateRotation(const uint8_t *input, size_t length, int index)
    {
        std::hash<std::string> hasher;
        std::string str(reinterpret_cast<const char *>(input), length);
        std::string key = str + std::to_string(index);
        uint64_t rotation = static_cast<uint64_t>(hasher(key)) % 64; // 限制在0-63之间
        return rotation;
    }

    static uint64_t generateMixConstant(const uint8_t *input, size_t length, size_t index)
    {
        std::hash<std::string> hasher;
        std::string str(reinterpret_cast<const char *>(input), length);
        std::string key = str + std::to_string(index);
        return static_cast<uint64_t>(hasher(key));
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
        return static_cast<uint64_t>(hasher(key));
    }

    static void applySpecialValues(uint64_t &hash1, uint64_t &hash2, uint64_t &hash3, uint64_t &hash4,
                                   uint64_t &hash5, uint64_t &hash6, uint64_t &hash7, uint64_t &hash8,
                                   const uint8_t *input, size_t length)
    {
        uint64_t specialValue1 = generateSpecialValue(input, length, 1);
        uint64_t specialValue2 = generateSpecialValue(input, length, 2);
        uint64_t specialValue3 = generateSpecialValue(input, length, 3);
        uint64_t specialValue4 = generateSpecialValue(input, length, 4);
        uint64_t specialValue5 = generateSpecialValue(input, length, 5);
        uint64_t specialValue6 = generateSpecialValue(input, length, 6);
        uint64_t specialValue7 = generateSpecialValue(input, length, 7);
        uint64_t specialValue8 = generateSpecialValue(input, length, 8);

        hash1 ^= specialValue1;
        hash1 = rotateLeft(hash1, 7) * 0xff51afd7ed558ccdULL;
        hash1 ^= specialValue2;
        hash1 = rotateLeft(hash1, 11) * 0xff51afd7ed558ccdULL;
        hash1 ^= specialValue3;
        hash1 = rotateLeft(hash1, 13) * 0xff51afd7ed558ccdULL;

        hash2 ^= specialValue4;
        hash2 = rotateLeft(hash2, 17) * 0xff51afd7ed558ccdULL;
        hash2 ^= specialValue1;
        hash2 = rotateLeft(hash2, 19) * 0xff51afd7ed558ccdULL;
        hash2 ^= specialValue2;
        hash2 = rotateLeft(hash2, 23) * 0xff51afd7ed558ccdULL;

        hash3 ^= specialValue3;
        hash3 = rotateLeft(hash3, 29) * 0xff51afd7ed558ccdULL;
        hash3 ^= specialValue4;
        hash3 = rotateLeft(hash3, 31) * 0xff51afd7ed558ccdULL;
        hash3 ^= specialValue1;
        hash3 = rotateLeft(hash3, 37) * 0xff51afd7ed558ccdULL;

        hash4 ^= specialValue2;
        hash4 = rotateLeft(hash4, 41) * 0xff51afd7ed558ccdULL;
        hash4 ^= specialValue3;
        hash4 = rotateLeft(hash4, 43) * 0xff51afd7ed558ccdULL;
        hash4 ^= specialValue4;
        hash4 = rotateLeft(hash4, 47) * 0xff51afd7ed558ccdULL;

        hash5 ^= specialValue5;
        hash5 = rotateLeft(hash5, 53) * 0xff51afd7ed558ccdULL;
        hash5 ^= specialValue6;
        hash5 = rotateLeft(hash5, 59) * 0xff51afd7ed558ccdULL;
        hash5 ^= specialValue7;
        hash5 = rotateLeft(hash5, 61) * 0xff51afd7ed558ccdULL;

        hash6 ^= specialValue8;
        hash6 = rotateLeft(hash6, 67) * 0xff51afd7ed558ccdULL;
        hash6 ^= specialValue5;
        hash6 = rotateLeft(hash6, 71) * 0xff51afd7ed558ccdULL;
        hash6 ^= specialValue6;
        hash6 = rotateLeft(hash6, 73) * 0xff51afd7ed558ccdULL;

        hash7 ^= specialValue7;
        hash7 = rotateLeft(hash7, 79) * 0xff51afd7ed558ccdULL;
        hash7 ^= specialValue8;
        hash7 = rotateLeft(hash7, 83) * 0xff51afd7ed558ccdULL;
        hash7 ^= specialValue5;
        hash7 = rotateLeft(hash7, 89) * 0xff51afd7ed558ccdULL;

        hash8 ^= specialValue6;
        hash8 = rotateLeft(hash8, 97) * 0xff51afd7ed558ccdULL;
        hash8 ^= specialValue7;
        hash8 = rotateLeft(hash8, 101) * 0xff51afd7ed558ccdULL;
        hash8 ^= specialValue8;
        hash8 = rotateLeft(hash8, 103) * 0xff51afd7ed558ccdULL;

        hash1 ^= 19491001; // 新中国成立时间（1949年10月1日）
        hash2 ^= -1300101; // 丝绸之路的起源时间（公元前130年1月1日）
        hash3 ^= 0x9d7a10d058596800;
        hash4 ^= 0x4b1eabfeacf50400;
        hash5 ^= 0x5802faa25d67980;
        hash6 ^= 0xe4c3297d1b8a8800;
        hash7 ^= 0xc3d8ccf7557b900;
        hash8 ^= 0xd1661ffad388e000;
    }

    static uint64_t disruptData(const uint8_t *input, size_t length, size_t index)
    {
        std::hash<std::string> hasher;
        std::string str(reinterpret_cast<const char *>(input), length);
        std::string key = str + std::to_string(index);
        uint64_t disruption = static_cast<uint64_t>(hasher(key));
        return disruption;
    }

    static void mixHash(uint64_t &currentHash1, uint64_t &currentHash2, uint64_t &currentHash3, uint64_t &currentHash4,
                        uint64_t &currentHash5, uint64_t &currentHash6, uint64_t &currentHash7, uint64_t &currentHash8,
                        const uint8_t *input, size_t length)
    {
        HashResult newHash = hash(input, length);
        currentHash1 ^= newHash.part1;
        currentHash2 ^= newHash.part2;
        currentHash3 ^= newHash.part3;
        currentHash4 ^= newHash.part4;
        currentHash5 ^= newHash.part5;
        currentHash6 ^= newHash.part6;
        currentHash7 ^= newHash.part7;
        currentHash8 ^= newHash.part8;
    }

    static std::vector<uint8_t> insertRandomBytes(const uint8_t *input, size_t length)
    {
        std::vector<uint8_t> inputData(input, input + length);
        std::vector<uint8_t> randomBytes;
        size_t numInsertions = 10; // 插入10个随机字节

        // 生成随机字节
        for (size_t i = 0; i < numInsertions; ++i)
        {
            uint8_t randomByte = static_cast<uint8_t>(generateSeed(input, length) % 256);
            randomBytes.push_back(randomByte);
        }

        // 计算插入位置
        std::vector<size_t> insertionPoints(numInsertions);
        for (size_t i = 0; i < numInsertions; ++i)
        {
            insertionPoints[i] = (generateSeed(input, length) + i) % (length + i);
        }

        // 排序插入位置以避免覆盖
        std::sort(insertionPoints.begin(), insertionPoints.end());

        // 插入随机字节
        for (size_t i = 0; i < numInsertions; ++i)
        {
            inputData.insert(inputData.begin() + insertionPoints[i], randomBytes[i]);
        }

        return inputData;
    }

    static std::vector<uint8_t> padInput(const uint8_t *input, size_t length)
    {
        std::vector<uint8_t> inputData = insertRandomBytes(input, length);
        size_t targetSize = 4096; // Increase target size for more padding
        if (inputData.size() >= targetSize)
        {
            return inputData;
        }

        // 计算需要填充的长度
        size_t paddingLength = targetSize - inputData.size();

        // 根据输入数据生成填充数据
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
        uint64_t seed1 = generateSeed(input, length);
        uint64_t seed2 = generateSeed(input, length) ^ 0xdeadbeefULL;
        uint64_t seed3 = generateSeed(input, length) ^ 0xbeefdeadULL;
        uint64_t seed4 = generateSeed(input, length) ^ 0xfeedfaceULL;
        uint64_t seed5 = generateSeed(input, length) ^ 0xcafebabeULL;
        uint64_t seed6 = generateSeed(input, length) ^ 0xdeadc0deULL;
        uint64_t seed7 = generateSeed(input, length) ^ 0xabad1deaULL;
        uint64_t seed8 = generateSeed(input, length) ^ 0x1badf00dULL;

        uint64_t c1 = 0x9ddfea08eb382d69;
        uint64_t c2 = 0xa7f5f35426a8e989;
        uint64_t r1 = generateRotation(input, length, 1);
        uint64_t r2 = generateRotation(input, length, 2);
        uint64_t m = 0xff51afd7ed558ccd;
        uint64_t n = 0xc4ceb9fe1a85ec53;

        uint64_t hash1 = seed1;
        uint64_t hash2 = seed2;
        uint64_t hash3 = seed3;
        uint64_t hash4 = seed4;
        uint64_t hash5 = seed5;
        uint64_t hash6 = seed6;
        uint64_t hash7 = seed7;
        uint64_t hash8 = seed8;

        for (size_t i = 0; i < length; i += 64)
        {
            size_t chunkSize = std::min<size_t>(64, length - i);
            uint64_t k1 = 0;
            uint64_t k2 = 0;
            uint64_t k3 = 0;
            uint64_t k4 = 0;
            uint64_t k5 = 0;
            uint64_t k6 = 0;
            uint64_t k7 = 0;
            uint64_t k8 = 0;
            for (size_t j = 0; j < chunkSize && j < 8; ++j)
            {
                k1 |= static_cast<uint64_t>(input[i + j]) << (j * 8);
            }
            for (size_t j = 8; j < chunkSize && j < 16; ++j)
            {
                k2 |= static_cast<uint64_t>(input[i + j]) << ((j - 8) * 8);
            }
            for (size_t j = 16; j < chunkSize && j < 24; ++j)
            {
                k3 |= static_cast<uint64_t>(input[i + j]) << ((j - 16) * 8);
            }
            for (size_t j = 24; j < chunkSize && j < 32; ++j)
            {
                k4 |= static_cast<uint64_t>(input[i + j]) << ((j - 24) * 8);
            }
            for (size_t j = 32; j < chunkSize && j < 40; ++j)
            {
                k5 |= static_cast<uint64_t>(input[i + j]) << ((j - 32) * 8);
            }
            for (size_t j = 40; j < chunkSize && j < 48; ++j)
            {
                k6 |= static_cast<uint64_t>(input[i + j]) << ((j - 40) * 8);
            }
            for (size_t j = 48; j < chunkSize && j < 56; ++j)
            {
                k7 |= static_cast<uint64_t>(input[i + j]) << ((j - 48) * 8);
            }
            for (size_t j = 56; j < chunkSize; ++j)
            {
                k8 |= static_cast<uint64_t>(input[i + j]) << ((j - 56) * 8);
            }

            // 破坏原数据
            k1 ^= disruptData(input, length, i);
            k2 ^= disruptData(input, length, i + 8);
            k3 ^= disruptData(input, length, i + 16);
            k4 ^= disruptData(input, length, i + 24);
            k5 ^= disruptData(input, length, i + 32);
            k6 ^= disruptData(input, length, i + 40);
            k7 ^= disruptData(input, length, i + 48);
            k8 ^= disruptData(input, length, i + 56);

            k1 *= c1;
            k1 = rotateLeft(k1, r1);
            k1 *= c2;

            k2 *= c1;
            k2 = rotateLeft(k2, r1);
            k2 *= c2;

            k3 *= c1;
            k3 = rotateLeft(k3, r1);
            k3 *= c2;

            k4 *= c1;
            k4 = rotateLeft(k4, r1);
            k4 *= c2;

            k5 *= c1;
            k5 = rotateLeft(k5, r1);
            k5 *= c2;

            k6 *= c1;
            k6 = rotateLeft(k6, r1);
            k6 *= c2;

            k7 *= c1;
            k7 = rotateLeft(k7, r1);
            k7 *= c2;

            k8 *= c1;
            k8 = rotateLeft(k8, r1);
            k8 *= c2;

            hash1 ^= k1;
            hash2 ^= k2;
            hash3 ^= k3;
            hash4 ^= k4;
            hash5 ^= k5;
            hash6 ^= k6;
            hash7 ^= k7;
            hash8 ^= k8;

            hash1 = rotateLeft(hash1, r2) * m + n;
            hash2 = rotateLeft(hash2, r2) * m + n;
            hash3 = rotateLeft(hash3, r2) * m + n;
            hash4 = rotateLeft(hash4, r2) * m + n;
            hash5 = rotateLeft(hash5, r2) * m + n;
            hash6 = rotateLeft(hash6, r2) * m + n;
            hash7 = rotateLeft(hash7, r2) * m + n;
            hash8 = rotateLeft(hash8, r2) * m + n;

            hash1 = mix(hash1, length, i, generateMixConstant(input, length, i));
            hash2 = mix(hash2, length, i + 8, generateMixConstant(input, length, i + 8));
            hash3 = mix(hash3, length, i + 16, generateMixConstant(input, length, i + 16));
            hash4 = mix(hash4, length, i + 24, generateMixConstant(input, length, i + 24));
            hash5 = mix(hash5, length, i + 32, generateMixConstant(input, length, i + 32));
            hash6 = mix(hash6, length, i + 40, generateMixConstant(input, length, i + 40));
            hash7 = mix(hash7, length, i + 48, generateMixConstant(input, length, i + 48));
            hash8 = mix(hash8, length, i + 56, generateMixConstant(input, length, i + 56));

            // 多轮混合
            for (int round = 0; round < 32; ++round)
            {
                hash1 ^= rotateLeft(hash1, 19);
                hash1 *= 0xd6e8feb866cc9c03;
                hash1 ^= rotateLeft(hash1, 23);
                hash1 *= 0xcaaf00ab1889d485;

                hash2 ^= rotateLeft(hash2, 19);
                hash2 *= 0xd6e8feb866cc9c03;
                hash2 ^= rotateLeft(hash2, 23);
                hash2 *= 0xcaaf00ab1889d485;

                hash3 ^= rotateLeft(hash3, 19);
                hash3 *= 0xd6e8feb866cc9c03;
                hash3 ^= rotateLeft(hash3, 23);
                hash3 *= 0xcaaf00ab1889d485;

                hash4 ^= rotateLeft(hash4, 19);
                hash4 *= 0xd6e8feb866cc9c03;
                hash4 ^= rotateLeft(hash4, 23);
                hash4 *= 0xcaaf00ab1889d485;

                hash5 ^= rotateLeft(hash5, 19);
                hash5 *= 0xd6e8feb866cc9c03;
                hash5 ^= rotateLeft(hash5, 23);
                hash5 *= 0xcaaf00ab1889d485;

                hash6 ^= rotateLeft(hash6, 19);
                hash6 *= 0xd6e8feb866cc9c03;
                hash6 ^= rotateLeft(hash6, 23);
                hash6 *= 0xcaaf00ab1889d485;

                hash7 ^= rotateLeft(hash7, 19);
                hash7 *= 0xd6e8feb866cc9c03;
                hash7 ^= rotateLeft(hash7, 23);
                hash7 *= 0xcaaf00ab1889d485;

                hash8 ^= rotateLeft(hash8, 19);
                hash8 *= 0xd6e8feb866cc9c03;
                hash8 ^= rotateLeft(hash8, 23);
                hash8 *= 0xcaaf00ab1889d485;
            }
        }

        // 最终混合
        hash1 ^= length;
        hash1 ^= hash1 >> 32;
        hash1 *= 0xff51afd7ed558ccd;
        hash1 ^= hash1 >> 32;
        hash1 *= 0xc4ceb9fe1a85ec53;
        hash1 ^= hash1 >> 32;

        hash2 ^= length;
        hash2 ^= hash2 >> 32;
        hash2 *= 0xff51afd7ed558ccd;
        hash2 ^= hash2 >> 32;
        hash2 *= 0xc4ceb9fe1a85ec53;
        hash2 ^= hash2 >> 32;

        hash3 ^= length;
        hash3 ^= hash3 >> 32;
        hash3 *= 0xff51afd7ed558ccd;
        hash3 ^= hash3 >> 32;
        hash3 *= 0xc4ceb9fe1a85ec53;
        hash3 ^= hash3 >> 32;

        hash4 ^= length;
        hash4 ^= hash4 >> 32;
        hash4 *= 0xff51afd7ed558ccd;
        hash4 ^= hash4 >> 32;
        hash4 *= 0xc4ceb9fe1a85ec53;
        hash4 ^= hash4 >> 32;

        hash5 ^= length;
        hash5 ^= hash5 >> 32;
        hash5 *= 0xff51afd7ed558ccd;
        hash5 ^= hash5 >> 32;
        hash5 *= 0xc4ceb9fe1a85ec53;
        hash5 ^= hash5 >> 32;

        hash6 ^= length;
        hash6 ^= hash6 >> 32;
        hash6 *= 0xff51afd7ed558ccd;
        hash6 ^= hash6 >> 32;
        hash6 *= 0xc4ceb9fe1a85ec53;
        hash6 ^= hash6 >> 32;

        hash7 ^= length;
        hash7 ^= hash7 >> 32;
        hash7 *= 0xff51afd7ed558ccd;
        hash7 ^= hash7 >> 32;
        hash7 *= 0xc4ceb9fe1a85ec53;
        hash7 ^= hash7 >> 32;

        hash8 ^= length;
        hash8 ^= hash8 >> 32;
        hash8 *= 0xff51afd7ed558ccd;
        hash8 ^= hash8 >> 32;
        hash8 *= 0xc4ceb9fe1a85ec53;
        hash8 ^= hash8 >> 32;

        applySpecialValues(hash1, hash2, hash3, hash4, hash5, hash6, hash7, hash8, input, length);

        // 多轮最终混合
        for (int round = 0; round < 64; ++round)
        {
            hash1 ^= rotateLeft(hash1, 17);
            hash1 *= 0xe08ff6fa0df1bbcb;
            hash1 ^= rotateLeft(hash1, 31);
            hash1 *= 0xafd7db2bd41cf79;

            hash2 ^= rotateLeft(hash2, 17);
            hash2 *= 0xe08ff6fa0df1bbcb;
            hash2 ^= rotateLeft(hash2, 31);
            hash2 *= 0xafd7db2bd41cf79;

            hash3 ^= rotateLeft(hash3, 17);
            hash3 *= 0xe08ff6fa0df1bbcb;
            hash3 ^= rotateLeft(hash3, 31);
            hash3 *= 0xafd7db2bd41cf79;

            hash4 ^= rotateLeft(hash4, 17);
            hash4 *= 0xe08ff6fa0df1bbcb;
            hash4 ^= rotateLeft(hash4, 31);
            hash4 *= 0xafd7db2bd41cf79;

            hash5 ^= rotateLeft(hash5, 17);
            hash5 *= 0xe08ff6fa0df1bbcb;
            hash5 ^= rotateLeft(hash5, 31);
            hash5 *= 0xafd7db2bd41cf79;

            hash6 ^= rotateLeft(hash6, 17);
            hash6 *= 0xe08ff6fa0df1bbcb;
            hash6 ^= rotateLeft(hash6, 31);
            hash6 *= 0xafd7db2bd41cf79;

            hash7 ^= rotateLeft(hash7, 17);
            hash7 *= 0xe08ff6fa0df1bbcb;
            hash7 ^= rotateLeft(hash7, 31);
            hash7 *= 0xafd7db2bd41cf79;

            hash8 ^= rotateLeft(hash8, 17);
            hash8 *= 0xe08ff6fa0df1bbcb;
            hash8 ^= rotateLeft(hash8, 31);
            hash8 *= 0xafd7db2bd41cf79;
        }

        // 添加额外的计算循环以增加计算时间
        for (int loop = 0; loop < 1000000; ++loop)
        {
            hash1 ^= rotateLeft(hash1, 13);
            hash1 *= 0x8a91a6d40bf42040;
            hash1 ^= rotateLeft(hash1, 29);
            hash1 *= 0xcbf29ce484222325;

            hash2 ^= rotateLeft(hash2, 13);
            hash2 *= 0x8a91a6d40bf42040;
            hash2 ^= rotateLeft(hash2, 29);
            hash2 *= 0xcbf29ce484222325;

            hash3 ^= rotateLeft(hash3, 13);
            hash3 *= 0x8a91a6d40bf42040;
            hash3 ^= rotateLeft(hash3, 29);
            hash3 *= 0xcbf29ce484222325;

            hash4 ^= rotateLeft(hash4, 13);
            hash4 *= 0x8a91a6d40bf42040;
            hash4 ^= rotateLeft(hash4, 29);
            hash4 *= 0xcbf29ce484222325;

            hash5 ^= rotateLeft(hash5, 13);
            hash5 *= 0x8a91a6d40bf42040;
            hash5 ^= rotateLeft(hash5, 29);
            hash5 *= 0xcbf29ce484222325;

            hash6 ^= rotateLeft(hash6, 13);
            hash6 *= 0x8a91a6d40bf42040;
            hash6 ^= rotateLeft(hash6, 29);
            hash6 *= 0xcbf29ce484222325;

            hash7 ^= rotateLeft(hash7, 13);
            hash7 *= 0x8a91a6d40bf42040;
            hash7 ^= rotateLeft(hash7, 29);
            hash7 *= 0xcbf29ce484222325;

            hash8 ^= rotateLeft(hash8, 13);
            hash8 *= 0x8a91a6d40bf42040;
            hash8 ^= rotateLeft(hash8, 29);
            hash8 *= 0xcbf29ce484222325;
        }

        return {hash1, hash2, hash3, hash4, hash5, hash6, hash7, hash8};
    }
};