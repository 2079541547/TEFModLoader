/*******************************************************************************
 * 文件名称: structure
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

#include <structure.hpp>
#include <stdexcept>
#include <iostream>

std::vector<uint8_t> SilkCasket::FileStructure::serializeString(const std::string &str) {
    std::vector<uint8_t> buffer;
    size_t length = str.size();
    buffer.insert(buffer.end(), reinterpret_cast<const uint8_t *>(&length), reinterpret_cast<const uint8_t *>(&length) + sizeof(length));
    buffer.insert(buffer.end(), str.begin(), str.end());
    return buffer;
}

std::string SilkCasket::FileStructure::deserializeString(const std::vector<uint8_t> &buffer, size_t &offset) {
    size_t length;
    memcpy(&length, &buffer[offset], sizeof(length));
    offset += sizeof(length);
    std::string str(&buffer[offset], &buffer[offset] + length);
    offset += length;
    return str;
}

std::vector<uint8_t> SilkCasket::FileStructure::header::serialize(const SilkCasket::FileStructure::header &hdr) {
    std::vector<uint8_t> buffer;
    size_t idLength = strlen(hdr.id) + 1;
    buffer.insert(buffer.end(), reinterpret_cast<const uint8_t*>(hdr.id), reinterpret_cast<const uint8_t*>(hdr.id) + idLength);
    buffer.insert(buffer.end(), reinterpret_cast<const uint8_t*>(&hdr.versionNumber),
                  reinterpret_cast<const uint8_t*>(&hdr.versionNumber) + sizeof(hdr.versionNumber));
    buffer.insert(buffer.end(), reinterpret_cast<const uint8_t*>(&hdr.entry),
                  reinterpret_cast<const uint8_t*>(&hdr.entry) + sizeof(hdr.entry));
    buffer.insert(buffer.end(), reinterpret_cast<const uint8_t*>(&hdr.entryData),
                  reinterpret_cast<const uint8_t*>(&hdr.entryData) + sizeof(hdr.entryData));
    buffer.push_back(hdr.encryption ? 1 : 0);
    return buffer;
}

SilkCasket::FileStructure::header SilkCasket::FileStructure::header::deserialize(const std::vector<uint8_t> &buffer) {
    header hdr;
    size_t offset = 0;

    if (offset + sizeof(hdr.id) <= buffer.size()) {
        size_t idLength = strnlen(reinterpret_cast<const char *>(buffer.data()), buffer.size());
        hdr.id = strdup(reinterpret_cast<const char *>(buffer.data()));
        offset += idLength + 1;
    } else {
        std::cerr << "Invalid data: insufficient bytes for id" << std::endl;
    }

    if (offset + sizeof(hdr.versionNumber) <= buffer.size()) {
        std::memcpy(&hdr.versionNumber, &buffer[offset], sizeof(hdr.versionNumber));
        offset += sizeof(hdr.versionNumber);
    } else {
        std::cerr << "Invalid data: insufficient bytes for versionNumber" << std::endl;
    }

    if (offset + sizeof(hdr.entry) <= buffer.size()) {
        std::memcpy(&hdr.entry, &buffer[offset], sizeof(hdr.entry));
        offset += sizeof(hdr.entry);
    } else {
        std::cerr << "Invalid data: insufficient bytes for entry" << std::endl;
    }

    if (offset + sizeof(hdr.entryData) <= buffer.size()) {
        std::memcpy(&hdr.entryData, &buffer[offset], sizeof(hdr.entryData));
        offset += sizeof(hdr.entryData);
    } else {
        std::cerr << "Invalid data: insufficient bytes for entryData" << std::endl;
    }

    hdr.encryption = buffer[offset++] != 0;

    return hdr;
}


std::vector<uint8_t> SilkCasket::FileStructure::entry::serialize(const SilkCasket::FileStructure::entry &entry) {
    std::vector<uint8_t> buffer;
    std::vector<uint8_t> nameBuffer = serializeString(entry.name);
    buffer.insert(buffer.end(), nameBuffer.begin(), nameBuffer.end());
    buffer.push_back(entry.isFile ? 1 : 0);
    buffer.insert(buffer.end(), reinterpret_cast<const uint8_t*>(&entry.data), reinterpret_cast<const uint8_t*>(&entry.data) + sizeof(entry.data));
    return buffer;
}


SilkCasket::FileStructure::entry SilkCasket::FileStructure::entry::deserialize(const std::vector<uint8_t> &buffer, size_t &offset) {
    entry entry;

    entry.name = deserializeString(buffer, offset);

    if (offset >= buffer.size()) {
        std::cerr << "Buffer overflow when reading isFile" << std::endl;
    }

    entry.isFile = buffer[offset++] != 0;

    if (offset + sizeof(entry.data) > buffer.size()) {
        std::cerr << "Buffer overflow when reading data" << std::endl;
    }

    memcpy(&entry.data, &buffer[offset], sizeof(entry.data));
    offset += sizeof(entry.data);

    return entry;
}

std::vector<uint8_t> SilkCasket::FileStructure::data::serialize(const SilkCasket::FileStructure::data &data) {
    std::vector<uint8_t> buffer;

    size_t numAddresses = data.Address.size();
    buffer.insert(buffer.end(), reinterpret_cast<const uint8_t*>(&numAddresses), reinterpret_cast<const uint8_t*>(&numAddresses) + sizeof(numAddresses));

    for (const auto &addr : data.Address) {
        buffer.insert(buffer.end(), reinterpret_cast<const uint8_t*>(&addr.offset), reinterpret_cast<const uint8_t*>(&addr.offset) + sizeof(addr.offset));
        buffer.insert(buffer.end(), reinterpret_cast<const uint8_t*>(&addr.size), reinterpret_cast<const uint8_t*>(&addr.size) + sizeof(addr.size));
    }

    return buffer;
}

SilkCasket::FileStructure::data SilkCasket::FileStructure::data::deserialize(const std::vector<uint8_t> &buffer) {
    data data;

    if (buffer.size() < sizeof(size_t)) {
        std::cerr << "Buffer too small to read numAddresses" << std::endl;
    }

    size_t numAddresses;
    memcpy(&numAddresses, &buffer[0], sizeof(numAddresses));

    size_t offset = sizeof(numAddresses);
    for (size_t i = 0; i < numAddresses; ++i) {
        if (offset + 2 * sizeof(size_t) > buffer.size()) {
            std::cerr << "Buffer overflow when reading addresses" <<  std::endl;
        }
        struct address addr{};
        memcpy(&addr.offset, &buffer[offset], sizeof(addr.offset));
        offset += sizeof(addr.offset);
        memcpy(&addr.size, &buffer[offset], sizeof(addr.size));
        offset += sizeof(addr.size);
        data.Address.push_back(addr);
    }

    return data;
}

std::vector<uint8_t> SilkCasket::FileStructure::serialize_entries(const std::vector<entry> &entries) {
    std::vector<uint8_t> buffer;

    size_t numEntries = entries.size();
    buffer.insert(buffer.end(), reinterpret_cast<const uint8_t*>(&numEntries), reinterpret_cast<const uint8_t*>(&numEntries) + sizeof(numEntries));

    for (const auto &entry : entries) {
        std::vector<uint8_t> entryBuffer = SilkCasket::FileStructure::entry::serialize(entry);
        buffer.insert(buffer.end(), entryBuffer.begin(), entryBuffer.end());
    }

    return buffer;
}

std::vector<SilkCasket::FileStructure::entry> SilkCasket::FileStructure::deserialize_entries(const std::vector<uint8_t> &buffer) {
    std::vector<entry> entries;

    if (buffer.size() < sizeof(size_t)) {
        std::cerr << "Buffer too small to read numEntries" << std::endl;
    }

    size_t numEntries;
    memcpy(&numEntries, &buffer[0], sizeof(numEntries));

    size_t minBufferSize = sizeof(size_t) * 2;
    if (buffer.size() < minBufferSize) {
        std::cerr << "Buffer too small to contain all entries" << std::endl;
    }

    size_t offset = sizeof(size_t);
    for (size_t i = 0; i < numEntries; ++i) {
        try {
            entry entry = entry::deserialize(buffer, offset);
            entries.push_back(entry);
        } catch (const std::exception& e) {
            std::cerr << "Error deserializing entry: " << e.what() << std::endl;
            break;
        }
    }

    return entries;
}