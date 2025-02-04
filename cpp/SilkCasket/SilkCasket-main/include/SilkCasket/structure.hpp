/*******************************************************************************
 * 文件名称: structure
 * 项目名称: SilkCasket
 * 创建时间: 2025/1/4
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

#include <cstring>
#include <string>
#include <vector>
#include <cstdint>

namespace SilkCasket::FileStructure  {

    struct address {
        size_t offset;
        size_t size;
    };

    struct header {
        const char *id = "SilkCasket";
        int versionNumber = 20250121;
        address entry{};
        address entryData{};
        bool encryption{};

        static std::vector<uint8_t> serialize(const header &hdr);
        static header deserialize(const std::vector<uint8_t> &buffer);
    };

    struct entry {
        std::string name;
        bool isFile;
        int data;

        static std::vector<uint8_t> serialize(const entry& entry);
        static entry deserialize(const std::vector<uint8_t>& buffer, size_t &offset);
    };

    struct data {
        std::vector<address> Address;

        static std::vector<uint8_t> serialize(const data& data);
        static data deserialize(const std::vector<uint8_t>& buffer);
    };

    std::vector<uint8_t> serializeString(const std::string &str);
    std::string deserializeString(const std::vector<uint8_t> &buffer, size_t &offset);


    std::vector<uint8_t> serialize_entries(const std::vector<entry>& entries);
    std::vector<entry> deserialize_entries(const std::vector<uint8_t>& buffer);
}