/*******************************************************************************
 * 文件名称: entry
 * 项目名称: Silk Casket
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
 * 描述信息: 本文件为Silk Casket项目中的一部分，允许在遵守Apache License 2.0的条件下自由用于商业用途。
 * 注意事项: 请严格遵守Apache License 2.0协议使用本代码。Apache License 2.0允许商业用途，无需额外授权。
 *******************************************************************************/

#pragma once

#include <iostream>
#include <vector>
#include <cstring>
#include "address.hpp"

namespace SilkCasket::FileStructure {

    using namespace std;

    struct entry {
        string name;
        bool isFile;
        bool encryption;
        int data;
    };

    struct entryData {
        vector<address> Address;
    };

    // 将字符串序列化为vector<uint8_t>
    inline vector<uint8_t> serializeString(const string &str)
    {
        vector<uint8_t> buffer;
        size_t length = str.size();
        buffer.insert(buffer.end(), reinterpret_cast<const uint8_t *>(&length), reinterpret_cast<const uint8_t *>(&length) + sizeof(length));
        buffer.insert(buffer.end(), str.begin(), str.end());
        return buffer;
    }

    // 从vector<uint8_t>反序列化为字符串
    inline string deserializeString(const vector<uint8_t> &buffer, size_t &offset)
    {
        size_t length;
        memcpy(&length, &buffer[offset], sizeof(length));
        offset += sizeof(length);
        string str(&buffer[offset], &buffer[offset] + length);
        offset += length;
        return str;
    }

    vector<uint8_t> serializeEntryData(const entryData& data);
    vector<uint8_t> serializeEntrys(const vector<entry> &entry);

    entryData deserializeEntryData(const vector<uint8_t>& buffer);
    vector<entry> deserializeEntrys(const vector<uint8_t>& buffer);

}