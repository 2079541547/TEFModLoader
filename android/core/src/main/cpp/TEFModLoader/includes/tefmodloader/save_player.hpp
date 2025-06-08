/*******************************************************************************
 * 文件名称: save_player
 * 项目名称: TEFModLoader
 * 创建时间: 2025/6/1
 * 作者: EternalFuture゙
 * Github: https://github.com/2079541547
 * 版权声明: Copyright © 2024 EternalFuture. All rights reserved.
 * 许可证: This program is free software: you can redistribute it and/or modify
 *         it under the terms of the GNU Affero General Public License as published
 *         by the Free Software Foundation, either version 3 of the License, or
 *         (at your option) any later version.
 *
 *         This program is distributed in the hope that it will be useful,
 *         but WITHOUT ANY WARRANTY; without even the implied warranty of
 *         MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *         GNU Affero General Public License for more details.
 *
 *         You should have received a copy of the GNU Affero General Public License
 *         along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * 注意事项: 请严格遵守GNU AGPL v3.0协议使用本代码，任何未经授权的商业用途均属侵权行为。
 *******************************************************************************/

#pragma once

#include <cstdint>
#include <string>
#include <vector>
#include "tefmod_api.hpp"

namespace TEFModLoader::SavePlayer {

    template<typename T>
    void serialize_base(std::vector<uint8_t>& buffer, T value);

    template<typename T>
    T deserialize_base(const uint8_t*& ptr);

    void serialize_string(std::vector<uint8_t>& buffer, const std::string& str);

    inline std::string deserialize_string(const uint8_t*& ptr);

    template<typename T>
    void serialize_complex_vector(std::vector<uint8_t>& buffer, const std::vector<T>& vec);

    template<typename T>
    void serialize_basic_vector(std::vector<uint8_t>& buffer, const std::vector<T>& vec);

    template<typename T>
    std::vector<T> deserialize_basic_vector(const uint8_t*& ptr);

    template<typename T>
    std::vector<T> deserialize_complex_vector(const uint8_t*& ptr);

    struct address {
        uint64_t _offset,        // 偏移
        _size;          // 大小

        void serialize(std::vector<uint8_t>& buffer) const;

        static address deserialize(const uint8_t*& ptr);
    };

    struct file_header {
        uint64_t _magic_number = 0x5445464D4C50;  // 魔数，标识文件类型 "TEFMLP" (0x5445464D4C50)
        uint8_t _version = 1;        // 版本号
        address _player_data{},    // 人物数据
        _inventory_data{}, // 主背包数据
        _bank_data{};      // 其他背包数据

        [[nodiscard]] std::vector<uint8_t> serialize() const;

        static file_header deserialize(const uint8_t* data);
    };

    struct prefix {
        int o_id{};               // 原版 id
        std::string id;         // 标识: 命名空间::内部名称

        void serialize(std::vector<uint8_t>& buffer) const;

        static prefix deserialize(const uint8_t*& ptr);
    };

    struct item_entry {
        std::string _id;               // 标识: 命名空间::内部名称
        prefix _prefix;                // 修饰词
        int _stack;                    // 堆叠数量
        bool _favorited;               // 是否收藏
        int _flag;                     // 占位 (用于表示处于哪个栏目)
        bool _is_no_mod;               // 是否为原版物品
        int _sacrifice;                // 已研究数量

        void serialize(std::vector<uint8_t>& buffer) const;

        static item_entry deserialize(const uint8_t*& ptr);
    };

    struct inventory {
        std::vector<item_entry> _data;

        [[nodiscard]] std::vector<uint8_t> serialize() const;

        static inventory deserialize(const uint8_t* data);
    };

    struct bank {
        std::vector<item_entry> _bank1; // 猪猪存钱罐
        std::vector<item_entry> _bank3; // 保险箱
        std::vector<item_entry> _bank2; // 护卫保险箱
        std::vector<item_entry> _bank4; // 虚空保险箱

        [[nodiscard]] std::vector<uint8_t> serialize() const;

        static bank deserialize(const uint8_t* data);
    };

    struct equipment {
        std::vector<item_entry> _armor;      // 护甲
        std::vector<item_entry> _dye;        // 染料
        std::vector<item_entry> _miscEquips; // 杂项装备
        std::vector<item_entry> _miscDyes;   // 杂项染料

        void serialize(std::vector<uint8_t>& buffer) const;

        static equipment deserialize(const uint8_t*& ptr);
    };

    struct buff {
        std::string _id;               // 内部名称
        int _time;                     // buff时间
        int _flag;                     // 占位 (用于表示处于哪个栏目)
        bool _is_no_mod;               // 是否为原版buff

        void serialize(std::vector<uint8_t>& buffer) const;

        static buff deserialize(const uint8_t*& ptr);
    };

    struct player {
        std::vector<equipment> _equipments;     // 套装
        std::vector<buff> _buffs;               // 存在的buff
        std::vector<item_entry> _sacrifice_data; // 已研究物品的数据

        [[nodiscard]] std::vector<uint8_t> serialize() const;

        static player deserialize(const uint8_t* data);
    };

    std::vector<uint8_t>
    read_file_address(const std::string &path, const size_t &offset, const size_t &size);
    void byte_to_file(const std::string &path, const std::vector<uint8_t> &content,
                      size_t chunk_size = 4096 * 1024);

    void save_tefmlp_file(const std::string& path, const player& p, const inventory& i, const bank& b);
    void load_disabled_items(const std::string& path);
    bool process_single_save_file(const std::string& path);

    void init(TEFMod::TEFModAPI* api);

    inline void (*old_InternalSavePlayerFile)(void*);
    void T_InternalSavePlayerFile(void* playerFile);
    void InternalSavePlayerFile(void* playerFile);
    inline TEFMod::HookTemplate InternalSavePlayerFile_HookTemplate = {
            reinterpret_cast<void*>(T_InternalSavePlayerFile),
            {}
    };

    inline void* (*old_LoadPlayer)(void*, bool);
    void* T_LoadPlayer(void* playerPath, bool cloudSave);
    void LoadPlayer(void* playerFileData, void* playerPath, bool cloudSave);
    inline TEFMod::HookTemplate LoadPlayer_HookTemplate = {
            reinterpret_cast<void*>(T_LoadPlayer),
            {}
    };
}

template<typename T>
void TEFModLoader::SavePlayer::serialize_base(std::vector<uint8_t>& buffer, T value) {
    buffer.insert(buffer.end(), reinterpret_cast<uint8_t*>(&value),
                  reinterpret_cast<uint8_t*>(&value) + sizeof(value));
}

template<typename T>
T TEFModLoader::SavePlayer::deserialize_base(const uint8_t*& ptr) {
    T value;
    memcpy(&value, ptr, sizeof(value));
    ptr += sizeof(value);
    return value;
}

template<typename T>
void TEFModLoader::SavePlayer::serialize_complex_vector(std::vector<uint8_t>& buffer, const std::vector<T>& vec) {
    serialize_base<int>(buffer, static_cast<uint32_t>(vec.size()));
    for (const auto& item : vec) {
        item.serialize(buffer);
    }
}

template<typename T>
std::vector<T> TEFModLoader::SavePlayer::deserialize_complex_vector(const uint8_t*& ptr) {
    std::vector<T> vec;
    auto count = deserialize_base<uint32_t>(ptr);
    vec.reserve(count);
    for (uint32_t i = 0; i < count; ++i) {
        vec.push_back(T::deserialize(ptr));
    }
    return vec;
}

template<typename T>
void TEFModLoader::SavePlayer::serialize_basic_vector(std::vector<uint8_t>& buffer, const std::vector<T>& vec) {
    serialize_base<uint32_t>(buffer, static_cast<uint32_t>(vec.size()));

    if (!vec.empty()) {
        const auto* data_ptr = reinterpret_cast<const uint8_t*>(vec.data());
        buffer.insert(buffer.end(), data_ptr, data_ptr + vec.size() * sizeof(T));
    }
}

template<typename T>
std::vector<T> TEFModLoader::SavePlayer::deserialize_basic_vector(const uint8_t*& ptr) {
    std::vector<T> vec;

    auto count = deserialize_base<uint32_t>(ptr);
    vec.resize(count);

    if (count > 0) {
        std::memcpy(vec.data(), ptr, count * sizeof(T));
        ptr += count * sizeof(T);
    }

    return vec;
}