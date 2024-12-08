/*******************************************************************************
 * 文件名称: entry
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

#include <structure/entry.hpp>
#include <stdexcept>
#include <cstring>
#include <log.hpp>

namespace SilkCasket::FileStructure {

    // 序列化entryData
    std::vector<uint8_t> serializeEntryData(const entryData& data) {
        std::vector<uint8_t> buffer;

        // 写入地址数量
        size_t numAddresses = data.Address.size();
        buffer.insert(buffer.end(), reinterpret_cast<const uint8_t*>(&numAddresses), reinterpret_cast<const uint8_t*>(&numAddresses) + sizeof(numAddresses));

        // 写入每个地址
        for (const auto &addr : data.Address) {
            buffer.insert(buffer.end(), reinterpret_cast<const uint8_t*>(&addr.offset), reinterpret_cast<const uint8_t*>(&addr.offset) + sizeof(addr.offset));
            buffer.insert(buffer.end(), reinterpret_cast<const uint8_t*>(&addr.size), reinterpret_cast<const uint8_t*>(&addr.size) + sizeof(addr.size));
        }

        return buffer;
    }

    // 序列化单个entry
    std::vector<uint8_t> serializeEntry(const entry& entry) {
        std::vector<uint8_t> buffer;

        // 序列化name
        std::vector<uint8_t> nameBuffer = serializeString(entry.name);
        buffer.insert(buffer.end(), nameBuffer.begin(), nameBuffer.end());

        // 序列化isFile
        buffer.push_back(entry.isFile ? 1 : 0);

        // 序列化encryption
        buffer.push_back(entry.encryption ? 1 : 0);

        // 序列化data
        buffer.insert(buffer.end(), reinterpret_cast<const uint8_t*>(&entry.data), reinterpret_cast<const uint8_t*>(&entry.data) + sizeof(entry.data));

        return buffer;
    }

    // 序列化entries
    std::vector<uint8_t> serializeEntrys(const std::vector<entry> &entries) {
        std::vector<uint8_t> buffer;

        // 写入entry数量
        size_t numEntries = entries.size();
        buffer.insert(buffer.end(), reinterpret_cast<const uint8_t*>(&numEntries), reinterpret_cast<const uint8_t*>(&numEntries) + sizeof(numEntries));

        // 写入每个entry
        for (const auto &entry : entries) {
            std::vector<uint8_t> entryBuffer = serializeEntry(entry);
            buffer.insert(buffer.end(), entryBuffer.begin(), entryBuffer.end());
        }

        return buffer;
    }

    // 反序列化entryData
    entryData deserializeEntryData(const std::vector<uint8_t>& buffer) {
        entryData data;

        // 打印缓冲区大小
        LOG(LogLevel::INFO, "SilkCasket", "deserializeEntryData", "start", "Buffer size: " + std::to_string(buffer.size()));

        // 读取地址数量
        if (buffer.size() < sizeof(size_t)) {
            LOG(LogLevel::ERROR, "SilkCasket", "deserializeEntryData", "error", "Buffer too small to read numAddresses");
            throw std::runtime_error("Buffer too small to read numAddresses");
        }
        size_t numAddresses;
        memcpy(&numAddresses, &buffer[0], sizeof(numAddresses));
        LOG(LogLevel::INFO, "SilkCasket", "deserializeEntryData", "addresses", "Number of addresses: " + std::to_string(numAddresses));

        // 读取每个地址
        size_t offset = sizeof(numAddresses);
        for (size_t i = 0; i < numAddresses; ++i) {
            if (offset + 2 * sizeof(size_t) > buffer.size()) {
                LOG(LogLevel::ERROR, "SilkCasket", "deserializeEntryData", "error", "Buffer overflow when reading addresses");
                throw std::runtime_error("Buffer overflow when reading addresses");
            }
            address addr{};
            memcpy(&addr.offset, &buffer[offset], sizeof(addr.offset));
            offset += sizeof(addr.offset);
            memcpy(&addr.size, &buffer[offset], sizeof(addr.size));
            offset += sizeof(addr.size);
            data.Address.push_back(addr);
        }

        return data;
    }

    // 从vector<uint8_t>反序列化为entry
    entry deserializeEntry(const std::vector<uint8_t> &buffer, size_t &offset) {
        entry entry;

        // 反序列化name
        entry.name = deserializeString(buffer, offset);

        // 反序列化isFile
        if (offset >= buffer.size()) {
            throw std::runtime_error("Buffer overflow when reading isFile");
        }
        entry.isFile = buffer[offset++] != 0;

        // 反序列化encryption
        if (offset >= buffer.size()) {
            throw std::runtime_error("Buffer overflow when reading encryption");
        }
        entry.encryption = buffer[offset++] != 0;

        // 反序列化data
        if (offset + sizeof(entry.data) > buffer.size()) {
            throw std::runtime_error("Buffer overflow when reading data");
        }
        memcpy(&entry.data, &buffer[offset], sizeof(entry.data));
        offset += sizeof(entry.data);

        return entry;
    }

    // 反序列化entries
    std::vector<entry> deserializeEntrys(const std::vector<uint8_t>& buffer) {
        std::vector<entry> entries;

        // 检查缓冲区是否足够大
        if (buffer.size() < sizeof(size_t)) {
            LOG(LogLevel::ERROR, "SilkCasket", "deserializeEntrys", "error", "Buffer too small to read numEntries");
            throw std::runtime_error("Buffer too small to read numEntries");
        }

        // 读取条目数量
        size_t numEntries;
        memcpy(&numEntries, &buffer[0], sizeof(numEntries));
        LOG(LogLevel::INFO, "SilkCasket", "deserializeEntrys", "info", "Number of entries: " + std::to_string(numEntries));

        // 计算需要的最小缓冲区大小
        size_t minBufferSize = sizeof(size_t) * 2;
        if (buffer.size() < minBufferSize) {
            LOG(LogLevel::ERROR, "SilkCasket", "deserializeEntrys", "error", "Buffer too small to contain all entries");
            throw std::runtime_error("Buffer too small to contain all entries");
        }

        // 读取每个条目
        size_t offset = sizeof(size_t);
        for (size_t i = 0; i < numEntries; ++i) {
            try {
                entry entry = deserializeEntry(buffer, offset);
                entries.push_back(entry);
            } catch (const std::exception& e) {
                LOG(LogLevel::ERROR, "SilkCasket", "deserializeEntrys", "error", "Error deserializing entry: " + std::string(e.what()));
                break;
            }
        }

        return entries;
    }

}