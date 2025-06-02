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

#include <save_player.hpp>

#include <filesystem>
#include <fstream>

#include <BNM/UserSettings/GlobalSettings.hpp>
#include <BNM/Method.hpp>
#include <BNM/Field.hpp>

#include <tefmod-api/IL2CppArray.hpp>
#include <set_factory.hpp>
#include <tefmod-api/item.hpp>
#include <logger.hpp>
#include <item_manager.hpp>

void TEFModLoader::SavePlayer::serialize_string(std::vector<uint8_t>& buffer, const std::string& str) {
    serialize_base<uint32_t>(buffer, str.size());
    buffer.insert(buffer.end(), str.begin(), str.end());
}

std::string TEFModLoader::SavePlayer::deserialize_string(const uint8_t*& ptr) {
    const auto len = deserialize_base<uint32_t>(ptr);
    std::string str(reinterpret_cast<const char*>(ptr), len);
    ptr += len;
    return str;
}

void TEFModLoader::SavePlayer::address::serialize(std::vector<uint8_t>& buffer) const {
    serialize_base<uint64_t>(buffer, _offset);
    serialize_base<uint64_t>(buffer, _size);
}

TEFModLoader::SavePlayer::address TEFModLoader::SavePlayer::address::deserialize(const uint8_t*& ptr) {
    return {deserialize_base<uint64_t>(ptr), deserialize_base<uint64_t>(ptr)};
}

std::vector<uint8_t> TEFModLoader::SavePlayer::file_header::serialize() const {
    std::vector<uint8_t> buffer;
    serialize_base<uint64_t>(buffer, _magic_number);
    serialize_base<uint8_t>(buffer, _version);
    _player_data.serialize(buffer);
    _inventory_data.serialize(buffer);
    _bank_data.serialize(buffer);
    return buffer;
}

TEFModLoader::SavePlayer::file_header TEFModLoader::SavePlayer::file_header::deserialize(const uint8_t* data) {
    auto ptr = data;
    return {
            deserialize_base<uint64_t>(ptr),
            deserialize_base<uint8_t>(ptr),
            address::deserialize(ptr),
            address::deserialize(ptr),
            address::deserialize(ptr)
    };
}

void TEFModLoader::SavePlayer::prefix::serialize(std::vector<uint8_t>& buffer) const {
    serialize_base<int>(buffer, o_id);
    serialize_string(buffer, id);
}

TEFModLoader::SavePlayer::prefix TEFModLoader::SavePlayer::prefix::deserialize(const uint8_t*& ptr) {
    return {(deserialize_base<int>(ptr)), deserialize_string(ptr)};
}

void TEFModLoader::SavePlayer::item_entry::serialize(std::vector<uint8_t>& buffer) const {
    serialize_string(buffer, _id);
    _prefix.serialize(buffer);
    serialize_base<int>(buffer, _stack);
    serialize_base<uint8_t>(buffer, _favorited);
    serialize_base<int>(buffer, _flag);
    serialize_base<uint8_t>(buffer, _is_no_mod);
}

TEFModLoader::SavePlayer::item_entry TEFModLoader::SavePlayer::item_entry::deserialize(const uint8_t*& ptr) {
    return {
            deserialize_string(ptr),
            prefix::deserialize(ptr),
            deserialize_base<int>(ptr),
            static_cast<bool>(deserialize_base<uint8_t>(ptr)),
            deserialize_base<int>(ptr),
            static_cast<bool>(deserialize_base<uint8_t>(ptr))
    };
}

std::vector<uint8_t> TEFModLoader::SavePlayer::inventory::serialize() const {
    std::vector<uint8_t> buffer;
    serialize_complex_vector<item_entry>(buffer, _data);
    return buffer;
}

TEFModLoader::SavePlayer::inventory TEFModLoader::SavePlayer::inventory::deserialize(const uint8_t* data) {
    const uint8_t* ptr = data;
    return {deserialize_complex_vector<item_entry>(ptr)};
}


std::vector<uint8_t> TEFModLoader::SavePlayer::bank::serialize() const {
    std::vector<uint8_t> buffer;
    serialize_complex_vector<item_entry>(buffer, _bank1);
    serialize_complex_vector<item_entry>(buffer, _bank2);
    serialize_complex_vector<item_entry>(buffer, _bank3);
    serialize_complex_vector<item_entry>(buffer, _bank4);
    return buffer;
}

TEFModLoader::SavePlayer::bank TEFModLoader::SavePlayer::bank::deserialize(const uint8_t* data) {
    const uint8_t* ptr = data;
    return {
            deserialize_complex_vector<item_entry>(ptr),
            deserialize_complex_vector<item_entry>(ptr),
            deserialize_complex_vector<item_entry>(ptr),
            deserialize_complex_vector<item_entry>(ptr)
    };
}
void TEFModLoader::SavePlayer::equipment::serialize(std::vector<uint8_t>& buffer) const {
    serialize_complex_vector<item_entry>(buffer, _armor);
    serialize_complex_vector<item_entry>(buffer, _dye);
    serialize_complex_vector<item_entry>(buffer, _miscEquips);
    serialize_complex_vector<item_entry>(buffer, _miscDyes);
};

TEFModLoader::SavePlayer::equipment TEFModLoader::SavePlayer::equipment::deserialize(const uint8_t*& ptr) {
    return {
            deserialize_complex_vector<item_entry>(ptr),
            deserialize_complex_vector<item_entry>(ptr),
            deserialize_complex_vector<item_entry>(ptr),
            deserialize_complex_vector<item_entry>(ptr)
    };
}


void TEFModLoader::SavePlayer::buff::serialize(std::vector<uint8_t>& buffer) const {
    serialize_string(buffer, _id);
    serialize_base<int>(buffer, _time);
    serialize_base<int>(buffer, _flag);
    serialize_base<uint8_t>(buffer, _is_no_mod);
}

TEFModLoader::SavePlayer::buff TEFModLoader::SavePlayer::buff::deserialize(const uint8_t*& ptr) {
    return {
            deserialize_string(ptr),
            deserialize_base<int>(ptr),
            deserialize_base<int>(ptr),
            static_cast<bool>(deserialize_base<uint8_t>(ptr))
    };
}

std::vector<uint8_t> TEFModLoader::SavePlayer::player::serialize() const {
    std::vector<uint8_t> buffer;
    serialize_complex_vector<equipment>(buffer, _equipments);
    serialize_complex_vector<buff>(buffer, _buffs);
    return buffer;
}

TEFModLoader::SavePlayer::player TEFModLoader::SavePlayer::player::deserialize(const uint8_t* data) {
    const uint8_t* ptr = data;
    return {
            deserialize_complex_vector<equipment>(ptr),
            deserialize_complex_vector<buff>(ptr)
    };
}

std::vector<uint8_t>
TEFModLoader::SavePlayer::read_file_address(const std::string &path, const size_t &offset,
                                            const size_t &size) {
    std::ifstream file(path, std::ios::binary | std::ios::in);
    file.seekg(offset, std::ios::beg);
    std::vector<uint8_t> buffer(size);
    file.read(reinterpret_cast<char*>(buffer.data()), size);
    return buffer;
}

void TEFModLoader::SavePlayer::byte_to_file(const std::string &path,
                                            const std::vector<uint8_t> &content,
                                            size_t chunk_size) {

    std::ofstream file(path, std::ios::binary | std::ios::trunc);
    try {
        size_t totalBytes = content.size();
        for (size_t offset = 0; offset < totalBytes; offset += chunk_size) {
            size_t bytesToWrite = std::min(chunk_size, totalBytes - offset);
            file.write(reinterpret_cast<const char*>(&content[offset]), static_cast<std::streamsize>(bytesToWrite));
        }
        file.flush();
    } catch (const std::exception& e) {
        file.close();
        return;
    }
    file.close();
}

void TEFModLoader::SavePlayer::save_tefmlp_file(const std::string& path,
                                                const TEFModLoader::SavePlayer::player& p,
                                                const TEFModLoader::SavePlayer::inventory& i,
                                                const TEFModLoader::SavePlayer::bank& b) {
    LOGF_INFO("====== 开始保存TEFMLP文件 ======");
    LOGF_DEBUG("目标路径: {}", path);

    try {
        LOGF_DEBUG("正在序列化玩家数据...");
        const std::vector<uint8_t> player_buffer = p.serialize();
        LOGF_DEBUG("玩家数据序列化完成，大小: {} 字节", player_buffer.size());

        LOGF_DEBUG("正在序列化物品栏数据...");
        const std::vector<uint8_t> inventory_buffer = i.serialize();
        LOGF_DEBUG("物品栏数据序列化完成，大小: {} 字节", inventory_buffer.size());

        LOGF_DEBUG("正在序列化银行数据...");
        const std::vector<uint8_t> bank_buffer = b.serialize();
        LOGF_DEBUG("银行数据序列化完成，大小: {} 字节", bank_buffer.size());

        size_t actual_size = sizeof(file_header::_magic_number) +
                             sizeof(file_header::_version) +
                             3 * sizeof(address);
        LOGF_TRACE("文件头基础大小: {} 字节", actual_size);

        const file_header header{
                0x5445464D4C50, // 'TEFMLP'的ASCII十六进制表示
                1,              // 版本号
                {actual_size, player_buffer.size()},
                {actual_size + player_buffer.size(), inventory_buffer.size()},
                {actual_size + player_buffer.size() + inventory_buffer.size(), bank_buffer.size()}
        };
        LOGF_DEBUG("文件头构造完成: ");
        LOGF_DEBUG("  - 玩家数据偏移: {}, 大小: {}", header._player_data._offset, header._player_data._size);
        LOGF_DEBUG("  - 物品栏偏移: {}, 大小: {}", header._inventory_data._offset, header._inventory_data._size);
        LOGF_DEBUG("  - 银行偏移: {}, 大小: {}", header._bank_data._offset, header._bank_data._size);

        LOGF_DEBUG("正在合并文件数据...");
        std::vector<uint8_t> file_data = header.serialize();
        LOGF_TRACE("文件头序列化大小: {} 字节", file_data.size());

        file_data.insert(file_data.end(), player_buffer.begin(), player_buffer.end());
        file_data.insert(file_data.end(), inventory_buffer.begin(), inventory_buffer.end());
        file_data.insert(file_data.end(), bank_buffer.begin(), bank_buffer.end());
        LOGF_DEBUG("数据合并完成，总大小: {} 字节", file_data.size());

        LOGF_DEBUG("正在写入文件...");
        byte_to_file(path, file_data);
        LOGF_INFO("文件保存成功: {} (总大小: {} 字节)", path, file_data.size());

    } catch (const std::exception& e) {
        LOGF_CRITICAL("保存文件时发生异常: {}", e.what());
        throw;
    }

    LOGF_INFO("====== TEFMLP文件保存完成 ======");
}

void TEFModLoader::SavePlayer::init(TEFMod::TEFModAPI* api) {
    LOGF_INFO("====== 初始化SavePlayer模块 ======");

    static bool inited;
    if (!inited) {
        LOGF_DEBUG("首次初始化，注册Hook函数...");
        api->registerFunctionDescriptor({
                                                "Terraria",
                                                "Player",
                                                "InternalSavePlayerFile",
                                                "hook>>void",
                                                1,
                                                &InternalSavePlayerFile_HookTemplate,
                                                { reinterpret_cast<void*>(InternalSavePlayerFile) }
                                        });
        api->registerFunctionDescriptor({
            "Terraria",
            "Player",
            "LoadPlayer",
            "hook>>void",
            2,
            &LoadPlayer_HookTemplate,
            { reinterpret_cast<void*>(LoadPlayer) }
        });
        inited = true;
        LOGF_INFO("Hook函数注册完成");
    } else {
        LOGF_DEBUG("获取原始SavePlayer函数...");
        old_InternalSavePlayerFile = api->GetAPI<decltype(old_InternalSavePlayerFile)>({
                                                                       "Terraria",
                                                                       "Player",
                                                                       "InternalSavePlayerFile",
                                                                       "old_fun",
                                                                       1
                                                               });

        old_LoadPlayer = api->GetAPI<decltype(old_LoadPlayer)>({
            "Terraria",
            "Player",
            "LoadPlayer",
            "old_fun",
            2
        });

        LOGF_DEBUG("原始函数地址: {}(InternalSavePlayerFile), {}(LoadPlayer)", reinterpret_cast<void*>(old_InternalSavePlayerFile), reinterpret_cast<void*>(old_LoadPlayer));
    }

    LOGF_INFO("====== SavePlayer模块初始化完成 ======");
}

void TEFModLoader::SavePlayer::T_InternalSavePlayerFile(void* playerFile) {
    try {
        old_InternalSavePlayerFile(playerFile);
        for (auto fun : InternalSavePlayerFile_HookTemplate.FunctionArray) {
            if (fun) {
                LOGF_TRACE("调用Hook函数: {}", reinterpret_cast<void*>(fun));
                reinterpret_cast<decltype(old_InternalSavePlayerFile)>(fun)(playerFile);
            }
        }
    } catch (const std::exception& e) {
        LOGF_CRITICAL("T_SavePlayer执行异常: {}", e.what());
        throw;
    }
}

void TEFModLoader::SavePlayer::InternalSavePlayerFile(void* playerFile) {
    LOGF_INFO("====== 开始保存玩家数据 ======");

    // 静态反射初始化
    static BNM::Method<void*> get_Player = BNM::Class("Terraria.IO", "PlayerFileData").GetMethod("get_Player", 0);
    static BNM::Method<void*> GetFileName = BNM::Class("Terraria.IO", "FileData").GetMethod("GetFileName", 1);
    static auto ItemClass = BNM::Class("Terraria", "Item");
    static BNM::Field<int> Item_ID = ItemClass.GetField("type");
    static BNM::Field<int> Item_Stack = ItemClass.GetField("stack");
    static BNM::Field<uint8_t> Item_Prefix = ItemClass.GetField("prefix");
    static BNM::Field<bool> Item_Favorited = ItemClass.GetField("favorited");
    static auto PlayerClass = BNM::Class("Terraria", "Player");
    static auto manager = ItemManager::GetInstance();
    static auto EquipmentLoadoutClass = BNM::Class("Terraria", "EquipmentLoadout");

    try {
        // 获取当前玩家实例
        LOGF_DEBUG("获取玩家实例...");
        auto current_player = get_Player[playerFile].Call();
        LOGF_DEBUG("玩家实例地址: {}", current_player);

        // 准备文件路径
        std::filesystem::path path = static_cast<BNM::Field<BNM::Structures::Mono::String*>>(BNM::Class("Terraria.IO", "FileData").GetField("_path"))[playerFile].Get()->str() + ".tefmlp";
        LOGF_INFO("保存路径: {}", path.string());

        // 备份现有文件
        if (exists(path)) {
            LOGF_DEBUG("检测到已有存档，创建备份...");
            std::filesystem::path backupPath = path.string() + ".bak";
            copy_file(path, backupPath, std::filesystem::copy_options::overwrite_existing);
            LOGF_DEBUG("备份创建完成: {}", backupPath.string());
        }

        // 物品构建函数
        static auto build_item = [](void* item, int flag) -> item_entry {
            int itemId = Item_ID[item].Get();
            return {
                    ItemManager::GetInstance()->get_name(itemId).GetID(),
                    { Item_Prefix[item].Get(), "" },
                    Item_Stack[item].Get(),
                    Item_Favorited[item].Get(),
                    flag,
                    itemId < SetFactory::count.item
            };
        };

        // 初始化数据结构
        player p{};
        inventory p_inventory{};
        bank b;

        LOGF_DEBUG("开始收集装备数据...");

        // 主装备栏
        equipment equipment_main{};

        // miscEquips
        auto miscEquips = IL2CppArray<void*>(*static_cast<void**>(PlayerClass.GetField("miscEquips")[current_player].GetFieldPointer()));
        LOGF_DEBUG("处理miscEquips (数量: {})", miscEquips.Size());
        for (int i = 0; i < miscEquips.Size(); ++i) {
            auto item = miscEquips.At(i);
            auto item_id = Item_ID[item].Get();
            if (item_id >= SetFactory::count.item) {
                LOGF_TRACE("添加miscEquip物品: ID={}, 槽位={}", item_id, i);
                equipment_main._miscEquips.push_back(build_item(item, i));
            }
        }

        // miscDyes
        auto miscDyes = IL2CppArray<void*>(*static_cast<void**>(PlayerClass.GetField("miscDyes")[current_player].GetFieldPointer()));
        LOGF_DEBUG("处理miscDyes (数量: {})", miscDyes.Size());
        for (int i = 0; i < miscDyes.Size(); ++i) {
            auto item = miscDyes.At(i);
            auto item_id = Item_ID[item].Get();
            if (item_id >= SetFactory::count.item) {
                LOGF_TRACE("添加miscDye物品: ID={}, 槽位={}", item_id, i);
                equipment_main._miscDyes.push_back(build_item(item, i));
            }
        }

        p._equipments.push_back(equipment_main);
        LOGF_DEBUG("主装备栏收集完成 (miscEquips: {}, miscDyes: {})",
                   equipment_main._miscEquips.size(), equipment_main._miscDyes.size());

        // 装备套装
        LOGF_DEBUG("处理装备套装...");
        auto Loadouts = IL2CppArray<void*>(*static_cast<void**>(PlayerClass.GetField("Loadouts")[current_player].GetFieldPointer()));
        LOGF_DEBUG("发现装备套装数量: {}", Loadouts.Size());


        for (auto equipment_instance : Loadouts) {
            equipment current_equipment{};

            // armor
            auto armor = IL2CppArray<void*>(*static_cast<void**>(EquipmentLoadoutClass.GetField("Armor")[equipment_instance].GetFieldPointer()));
            LOGF_TRACE("处理armor (数量: {})", armor.Size());
            for (int i = 0; i < armor.Size(); ++i) {
                auto item = armor.At(i);
                auto item_id = Item_ID[item].Get();
                if (item_id >= SetFactory::count.item) {
                    LOGF_TRACE("添加armor物品: ID={}, 槽位={}", item_id, i);
                    current_equipment._armor.push_back(build_item(item, i));
                }
            }

            // dye
            auto dye = IL2CppArray<void*>(*static_cast<void**>(EquipmentLoadoutClass.GetField("Dye")[equipment_instance].GetFieldPointer()));
            LOGF_TRACE("处理dye (数量: {})", dye.Size());
            for (int i = 0; i < dye.Size(); ++i) {
                auto item = dye.At(i);
                auto item_id = Item_ID[item].Get();
                if (item_id >= SetFactory::count.item) {
                    LOGF_TRACE("添加dye物品: ID={}, 槽位={}", item_id, i);
                    current_equipment._dye.push_back(build_item(item, i));
                }
            }

            p._equipments.push_back(current_equipment);
            LOGF_DEBUG("套装收集完成 (armor: {}, dye: {})",
                       current_equipment._armor.size(), current_equipment._dye.size());
        }

        // 物品栏
        LOGF_DEBUG("处理物品栏数据...");
        auto inventoryItems = IL2CppArray<void*>(*static_cast<void**>(PlayerClass.GetField("inventory")[current_player].GetFieldPointer()));
        LOGF_DEBUG("物品栏物品数量: {}", inventoryItems.Size());
        for (int i = 0; i < inventoryItems.Size(); ++i) {
            auto item = inventoryItems.At(i);
            auto item_id = Item_ID[item].Get();
            if (item_id >= SetFactory::count.item) {
                LOGF_TRACE("添加物品栏物品: ID={}, 槽位={}", item_id, i);
                p_inventory._data.push_back(build_item(item, i));
            }
        }
        LOGF_DEBUG("物品栏收集完成 (自定义物品: {})", p_inventory._data.size());

        // 银行数据
        LOGF_DEBUG("处理银行数据...");

        auto process_bank = [&](const std::string& bankName, std::vector<item_entry>& target) {
            auto bankItems = IL2CppArray<void*>(*static_cast<void**>(PlayerClass.GetField(bankName)[current_player].GetFieldPointer()));
            LOGF_DEBUG("处理{} (数量: {})", bankName, bankItems.Size());
            for (int i = 0; i < bankItems.Size(); ++i) {
                auto item = bankItems.At(i);
                auto item_id = Item_ID[item].Get();
                if (item_id >= SetFactory::count.item) {
                    LOGF_TRACE("添加{}物品: ID={}, 槽位={}", bankName, item_id, i);
                    target.push_back(build_item(item, i));
                }
            }
        };

        process_bank("bank", b._bank1);
        process_bank("bank2", b._bank2);
        process_bank("bank3", b._bank3);
        process_bank("bank4", b._bank4);

        LOGF_DEBUG("银行数据收集完成 (bank1: {}, bank2: {}, bank3: {}, bank4: {})",
                   b._bank1.size(), b._bank2.size(), b._bank3.size(), b._bank4.size());

        // 保存文件
        LOGF_DEBUG("准备保存数据到文件...");
        save_tefmlp_file(path.string(), p, p_inventory, b);
        LOGF_INFO("玩家数据保存完成");

    } catch (const std::exception& e) {
        LOGF_CRITICAL("保存玩家数据时发生异常: {}", e.what());
        throw;
    }

    LOGF_INFO("====== 玩家数据保存流程结束 ======");
}


void* TEFModLoader::SavePlayer::T_LoadPlayer(void *playerPath, bool cloudSave) {
    auto r = old_LoadPlayer(playerPath, cloudSave);
    LoadPlayer(r, playerPath, cloudSave);
    for (auto fun : InternalSavePlayerFile_HookTemplate.FunctionArray) {
        if (fun) {
            LOGF_TRACE("调用Hook函数: {}", reinterpret_cast<void*>(fun));
            reinterpret_cast<void(*)(void* playerFileData, void* playerPath, bool cloudSave)>(fun)(r, playerPath, cloudSave);
        }
    }
    return r;
}

void TEFModLoader::SavePlayer::LoadPlayer(void* playerFileData, void* playerPath, bool cloudSave) {
    LOGF_INFO("====== 开始加载TEFMLP玩家数据 ======");

    // 静态反射初始化
    static BNM::Method<void*> get_Player = BNM::Class("Terraria.IO", "PlayerFileData").GetMethod("get_Player", 0);
    static BNM::Method<void*> GetFileName = BNM::Class("Terraria.IO", "FileData").GetMethod("GetFileName", 1);
    static auto ItemClass = BNM::Class("Terraria", "Item");
    static BNM::Field<int> Item_Stack = ItemClass.GetField("stack");
    static BNM::Field<bool> Item_Favorited = ItemClass.GetField("favorited");
    static BNM::Method<void> Item_netDefaults = ItemClass.GetMethod("netDefaults", 1);
    static BNM::Method<void> Item_Prefix = ItemClass.GetMethod("Prefix", 1);
    static auto PlayerClass = BNM::Class("Terraria", "Player");
    static auto EquipmentLoadoutClass = BNM::Class("Terraria", "EquipmentLoadout");
    static auto manager = ItemManager::GetInstance();

    try {
        LOGF_DEBUG("获取玩家实例...");
        auto current_player = get_Player[playerFileData].Call();
        BNM::Field<BNM::Structures::Mono::String*> name = PlayerClass.GetField("name");

        LOGF_DEBUG("玩家实例地址: {}, 玩家名称: {}", current_player, name[current_player].Get()->str());

        std::filesystem::path path = static_cast<BNM::Field<BNM::Structures::Mono::String*>>(
                                             BNM::Class("Terraria.IO", "FileData").GetField("_path")
                                     )[playerFileData].Get()->str() + ".tefmlp";
        LOGF_INFO("加载路径: {}", path.string());

        if (!exists(path)) {
            std::filesystem::path bak_file = path.string() + ".bak";
            if (exists(bak_file)) {
                LOGF_WARN("主文件不存在，从备份恢复...");
                copy_file(bak_file, path, std::filesystem::copy_options::overwrite_existing);
                LOGF_INFO("备份恢复完成: {}", bak_file.string());
            } else {
                LOGF_WARN("未找到TEFMLP存档文件: {}", path.string());
                return;
            }
        }

        LOGF_DEBUG("解析文件头...");
        size_t file_header_size = sizeof(file_header::_magic_number) +
                                  sizeof(file_header::_version) +
                                  3 * sizeof(address);

        auto header_data = read_file_address(path, 0, file_header_size);
        if (header_data.empty()) {
            LOGF_ERROR("文件头读取失败!");
            return;
        }

        auto info = file_header::deserialize(header_data.data());
        LOGF_DEBUG("文件头解析完成: player_offset={}, player_size={}",
                   info._player_data._offset, info._player_data._size);

        LOGF_DEBUG("反序列化玩家数据...");
        auto player_data = read_file_address(path, info._player_data._offset, info._player_data._size);
        auto i_player = player::deserialize(player_data.data());
        LOGF_DEBUG("玩家装备套装数: {}", i_player._equipments.size());

        LOGF_DEBUG("反序列化物品栏数据...");
        auto inv_data = read_file_address(path, info._inventory_data._offset, info._inventory_data._size);
        auto i_inventory = inventory::deserialize(inv_data.data());
        LOGF_DEBUG("物品栏物品数: {}", i_inventory._data.size());

        LOGF_DEBUG("反序列化银行数据...");
        auto bank_data = read_file_address(path, info._bank_data._offset, info._bank_data._size);
        auto i_bank = bank::deserialize(bank_data.data());
        LOGF_DEBUG("银行物品数: [1]={}, [2]={}, [3]={}, [4]={}",
                   i_bank._bank1.size(), i_bank._bank2.size(),
                   i_bank._bank3.size(), i_bank._bank4.size());

        LOGF_INFO("加载装备数据...");
        for (int i = 0; i < i_player._equipments.size(); ++i) {
            auto& c_equipment = i_player._equipments.at(i);

            if (i == 0) {
                LOGF_DEBUG("处理主装备栏...");
                // 处理miscEquips
                auto miscEquips = IL2CppArray<void*>(*static_cast<void**>(
                        PlayerClass.GetField("miscEquips")[current_player].GetFieldPointer()));
                LOGF_TRACE("miscEquips槽位数: {}", miscEquips.Size());

                for (const auto& c_miscEquips : c_equipment._miscEquips) {
                    LOGF_TRACE("加载miscEquip[{}]: ID={}, Stack={}",
                               c_miscEquips._flag, c_miscEquips._id, c_miscEquips._stack);
                    auto item = miscEquips.At(c_miscEquips._flag);
                    Item_netDefaults[item].Call(manager->get_id_from_str(c_miscEquips._id));
                    Item_Stack[item].Set(c_miscEquips._stack);
                    Item_Prefix[item].Call(c_miscEquips._prefix);
                }

                // 处理miscDyes
                auto miscDyes = IL2CppArray<void*>(*static_cast<void**>(
                        PlayerClass.GetField("miscDyes")[current_player].GetFieldPointer()));
                LOGF_TRACE("miscDyes槽位数: {}", miscDyes.Size());

                for (const auto& c_miscDyes : c_equipment._miscDyes) {
                    LOGF_TRACE("加载miscDye[{}]: ID={}, Stack={}",
                               c_miscDyes._flag, c_miscDyes._id, c_miscDyes._stack);
                    auto item = miscDyes.At(c_miscDyes._flag);
                    Item_netDefaults[item].Call(manager->get_id_from_str(c_miscDyes._id));
                    Item_Stack[item].Set(c_miscDyes._stack);
                    Item_Prefix[item].Call(c_miscDyes._prefix);
                }
                continue;
            }

            // 处理装备套装
            LOGF_DEBUG("处理装备套装[{}]...", i);
            auto Loadouts = IL2CppArray<void*>(*static_cast<void**>(
                    PlayerClass.GetField("Loadouts")[current_player].GetFieldPointer()));

            // 加载护甲
            auto armor = IL2CppArray<void*>(*static_cast<void**>(
                    EquipmentLoadoutClass.GetField("Armor")[Loadouts.At(i - 1)].GetFieldPointer()));
            LOGF_TRACE("护甲槽位数: {}", armor.Size());

            for (const auto& c_armor : c_equipment._armor) {
                LOGF_TRACE("加载护甲[{}]: ID={}, Stack={}",
                           c_armor._flag, c_armor._id, c_armor._stack);
                auto item = armor.At(c_armor._flag);
                Item_netDefaults[item].Call(manager->get_id_from_str(c_armor._id));
                Item_Stack[item].Set(c_armor._stack);
                Item_Prefix[item].Call(c_armor._prefix);
            }

            // 加载染料
            auto dye = IL2CppArray<void*>(*static_cast<void**>(
                    EquipmentLoadoutClass.GetField("Dye")[Loadouts.At(i - 1)].GetFieldPointer()));
            LOGF_TRACE("染料槽位数: {}", dye.Size());

            for (const auto& c_dye : c_equipment._dye) {
                LOGF_TRACE("加载染料[{}]: ID={}, Stack={}",
                           c_dye._flag, c_dye._id, c_dye._stack);
                auto item = dye.At(c_dye._flag);
                Item_netDefaults[item].Call(manager->get_id_from_str(c_dye._id));
                Item_Stack[item].Set(c_dye._stack);
                Item_Prefix[item].Call(c_dye._prefix);
            }
        }

        LOGF_INFO("加载物品栏数据...");
        auto inventoryItems = IL2CppArray<void*>(*static_cast<void**>(
                PlayerClass.GetField("inventory")[current_player].GetFieldPointer()));
        LOGF_DEBUG("物品栏槽位数: {}", inventoryItems.Size());

        for (const auto& c_inventory : i_inventory._data) {
            LOGF_TRACE("加载物品栏[{}]: ID={}, Stack={}",
                       c_inventory._flag, c_inventory._id, c_inventory._stack);
            auto item = inventoryItems.At(c_inventory._flag);
            Item_netDefaults[item].Call(manager->get_id_from_str(c_inventory._id));
            Item_Stack[item].Set(c_inventory._stack);
            Item_Prefix[item].Call(c_inventory._prefix);
        }

        LOGF_INFO("加载银行数据...");
        auto process_bank = [&](const std::string& bankName, const std::vector<item_entry>& items, int bank_index) {
            try {
                LOGF_DEBUG("处理银行{}...", bankName);
                if (items.empty()) {
                    LOGF_WARN("银行数据为空{}...", bankName);
                    return;
                }

                auto bankItems = IL2CppArray<void*>(*static_cast<void**>(
                        PlayerClass.GetField(bankName)[current_player].GetFieldPointer()));

                auto chestItems = IL2CppArray<void*>(*static_cast<void**>(
                        BNM::Class("Terraria", "Chest").GetField("item")[bankItems.At(bank_index)].GetFieldPointer()));
                LOGF_TRACE("{}槽位数: {}", bankName, chestItems.Size());

                for (const auto& c_item : items) {
                    LOGF_TRACE("加载{}[{}]: ID={}, Stack={}",
                               bankName, c_item._flag, c_item._id, c_item._stack);
                    auto item = chestItems.At(c_item._flag);
                    Item_netDefaults[item].Call(manager->get_id_from_str(c_item._id));
                    Item_Stack[item].Set(c_item._stack);
                    Item_Prefix[item].Call(c_item._prefix);
                }
            } catch (const std::exception& e) {
                LOGF_ERROR("处理银行{}失败: {}", bankName, e.what());
            }
        };

        process_bank("bank", i_bank._bank1, 0);
        process_bank("bank2", i_bank._bank2, 1);
        process_bank("bank3", i_bank._bank3, 2);
        process_bank("bank4", i_bank._bank4, 3);

        LOGF_INFO("====== TEFMLP玩家数据加载完成 ======");
    } catch (const std::exception& e) {
        LOGF_CRITICAL("加载玩家数据时发生异常: {}", e.what());
        throw;
    }
}