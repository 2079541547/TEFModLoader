/*******************************************************************************
 * 文件名称: lzw
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

#include <compress/lzw.hpp>
#include <log.hpp>

std::vector<uint8_t> SilkCasket::Compress::LZW::encodeVLE(uint32_t value) {
    std::vector<uint8_t> encoded;

    // 循环处理直到 value 为 0
    do {
        // 提取最低7位
        uint8_t byte = value & 0x7f;

        // 右移7位，准备处理下一次循环
        value >>= 7;

        // 如果 value 还有剩余部分，设置最高位（0x80）以表示还有更多字节
        if (value > 0) {
            byte |= 0x80;
        }

        // 将处理后的字节添加到结果向量中
        encoded.push_back(byte);
    } while (value > 0);  // 继续处理直到 value 为 0

    return encoded;
}

uint32_t SilkCasket::Compress::LZW::decodeVLE(const vector<uint8_t> &bytes, size_t &index) {
    uint32_t value = 0;  // 初始化解码后的值为0
    uint32_t shift = 0;  // 初始化位移量为0

    // 循环处理直到读取完所有相关字节
    while (index < bytes.size()) {
        uint8_t byte = bytes[index++];  // 获取当前字节并递增索引

        // 将当前字节的低7位左移shift位，并与当前值进行按位或操作
        value |= (byte & 0x7f) << shift;

        // 检查当前字节的最高位是否为0，如果是则表示这是最后一个字节
        if (!(byte & 0x80)) {
            break;
        }

        // 更新位移量，准备处理下一个字节
        shift += 7;
    }

    return value;  // 返回解码后的32位无符号整数
}

std::vector<uint8_t> SilkCasket::Compress::LZW::compress(const vector<uint8_t> &data) {
    std::unordered_map<std::string, uint32_t> dictionary;

    // 初始化字典，包含所有单字节字符
    for (int i = 0; i <= 255; ++i) {
        dictionary[std::string(1, static_cast<char>(i))] = static_cast<uint32_t>(i);
    }

    std::vector<uint8_t> result;  // 存储压缩后的数据
    std::string prefix;           // 当前匹配的字符串前缀
    size_t maxDictSize = INITIAL_MAX_DICT_SIZE;  // 字典的最大大小

    // 遍历输入数据的每个字节
    for (auto byte : data) {
        std::string candidate = prefix + static_cast<char>(byte);  // 形成新的候选字符串

        // 检查候选字符串是否在字典中
        if (dictionary.find(candidate) != dictionary.end()) {
            prefix = candidate;  // 更新前缀为候选字符串
        } else {
            // 将当前前缀的字典值编码为变长字节序列并添加到结果中
            auto encodedValue = encodeVLE(dictionary[prefix]);
            result.insert(result.end(), encodedValue.begin(), encodedValue.end());

            // 如果字典大小达到上限，扩展字典大小
            if (dictionary.size() >= maxDictSize) {
                maxDictSize += INITIAL_MAX_DICT_SIZE / 2;
            }

            // 将新的候选字符串添加到字典中
            dictionary[candidate] = static_cast<uint32_t>(dictionary.size());

            // 重置前缀为当前字节
            prefix = std::string(1, static_cast<char>(byte));
        }
    }

    // 处理最后的前缀
    if (!prefix.empty()) {
        auto encodedValue = encodeVLE(dictionary[prefix]);
        result.insert(result.end(), encodedValue.begin(), encodedValue.end());
    }

    return result;  // 返回压缩后的数据
}

std::vector<uint8_t> SilkCasket::Compress::LZW::decompress(const vector<uint8_t> &compressed) {
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

        // 直接迭代而不是复制字符串
        for (char c : s)
        {
            result.push_back(static_cast<uint8_t>(c));
        }

        // 更新字典
        dictionary[dictionary.size()] = dictionary[old] + s.substr(0, 1);
        old = entry;
    }

    return result;
}