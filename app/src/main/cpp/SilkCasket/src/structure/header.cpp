/*******************************************************************************
 * 文件名称: header
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

#include <structure/header.hpp>

std::vector <uint8_t>
SilkCasket::FileStructure::serializeHeader(const SilkCasket::FileStructure::header &hdr) {
    std::vector<uint8_t> buffer;

    // 写入固定的 identification 字符串 "SilkCasket"
    const char* identification = "SilkCasket";
    for (int i = 0; i < 10; ++i) {
        buffer.push_back(identification[i]);
    }

    // 写入 versionNumber
    buffer.insert(buffer.end(), reinterpret_cast<const uint8_t*>(&hdr.versionNumber),
                  reinterpret_cast<const uint8_t*>(&hdr.versionNumber) + sizeof(hdr.versionNumber));

    // 写入 entry 和 entryData
    buffer.insert(buffer.end(), reinterpret_cast<const uint8_t*>(&hdr.entry),
                  reinterpret_cast<const uint8_t*>(&hdr.entry) + sizeof(hdr.entry));
    buffer.insert(buffer.end(), reinterpret_cast<const uint8_t*>(&hdr.entryData),
                  reinterpret_cast<const uint8_t*>(&hdr.entryData) + sizeof(hdr.entryData));

    return buffer;
}


SilkCasket::FileStructure::header SilkCasket::FileStructure::deserializeHeader(const std::vector<uint8_t> &buffer) {
    header hdr;
    size_t offset = 0;

    // 跳过固定的 identification 字符串 "SilkCasket"，它总是10字节
    offset += 10;

    // 读取 versionNumber
    if (offset + sizeof(hdr.versionNumber) <= buffer.size()) {
        std::memcpy(&hdr.versionNumber, &buffer[offset], sizeof(hdr.versionNumber));
        offset += sizeof(hdr.versionNumber);
    } else {
        // 如果数据不足，则抛出异常或设置默认值
        throw std::runtime_error("Invalid data: insufficient bytes for versionNumber");
    }

    // 读取 entry 和 entryData
    if (offset + sizeof(hdr.entry) <= buffer.size()) {
        std::memcpy(&hdr.entry, &buffer[offset], sizeof(hdr.entry));
        offset += sizeof(hdr.entry);
    } else {
        // 如果数据不足，则抛出异常或设置默认值
        throw std::runtime_error("Invalid data: insufficient bytes for entry");
    }

    if (offset + sizeof(hdr.entryData) <= buffer.size()) {
        std::memcpy(&hdr.entryData, &buffer[offset], sizeof(hdr.entryData));
    } else {
        // 如果数据不足，则抛出异常或设置默认值
        throw std::runtime_error("Invalid data: insufficient bytes for entryData");
    }

    return hdr;
}