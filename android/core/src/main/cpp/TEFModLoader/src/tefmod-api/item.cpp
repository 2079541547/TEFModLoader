/*******************************************************************************
 * 文件名称: item
 * 项目名称: TEFModLoader
 * 创建时间: 2025/5/31
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

#include <tefmod-api/item.hpp>
#include <logger.hpp>

#include <BNM/UserSettings/GlobalSettings.hpp>
#include <BNM/Class.hpp>
#include <BNM/Field.hpp>
#include <BNM/UnityStructures.hpp>

void TEFModLoader::ItemManager::assignment(int startID) {
    LOGF_DEBUG("开始物品ID分配，起始ID: {}", startID);
    LOGF_DEBUG("排序前，物品数量: {}", m_items.size());

    std::stable_sort(m_items.begin(), m_items.end(),
                     [](const auto& a, const auto& b) {
                         LOGF_TRACE("比较物品: {} 和 {}", a.first.GetID(), b.first.GetID());
                         return a.first < b.first;
                     });
    LOGF_DEBUG("排序完成，当前物品数量: {}", m_items.size());

    m_name_to_id.clear();
    LOGF_DEBUG("已清空名称到ID的映射表");

    for (int i = 0; i < m_items.size(); ++i) {
        const auto& itemName = m_items[i].first;
        int assignedID = startID + i;
        LOGF_DEBUG("为物品 '{}' 分配ID: {}", itemName.GetID(), assignedID);

        m_name_to_id[itemName.GetID()] = assignedID;
        m_items_instance[assignedID] = m_items[i].second;
        m_id_to_name[assignedID] = itemName;
    }
    LOGF_DEBUG("已完成 {} 个物品的ID分配", m_items.size());

    m_needs_sorting = false;
}

TEFMod::Item* TEFModLoader::ItemManager::get_item_instance(int id) {
    LOGF_DEBUG("请求获取ID为 {} 的物品实例", id);
    auto instance = m_items_instance[id] ?: nullptr;
    LOGF_DEBUG("返回物品实例指针: {}", (void*)instance);
    return instance;
}

int TEFModLoader::ItemManager::get_count() {
    if (m_items_instance.empty()) {
        LOGF_DEBUG("获取物品总数: 0 (容器为空)");
        return 0;
    }

    int count = m_items_instance.rbegin()->first;
    LOGF_DEBUG("获取物品总数: {}", count);
    return count;
}

void TEFModLoader::ItemManager::registered(const TEFMod::item_name& name, TEFMod::Item* item) {
    const auto& nameID = name.GetID();
    LOGF_DEBUG("正在注册物品 '{}'，实例地址: {}", nameID, (void*)item);

    if (m_registered_names.find(nameID) == m_registered_names.end()) {
        m_items.emplace_back(name, item);
        m_registered_names.insert(nameID);
        m_needs_sorting = true;
        LOGF_INFO("成功注册新物品: {}", nameID);
    } else {
        LOGF_WARN("尝试注册重复物品: {}", nameID);
    }
}

void TEFModLoader::ItemManager::set_localized(TEFMod::item_name name,
                                              const TEFMod::item_localized& localized) {
    const auto& nameID = name.GetID();
    LOGF_DEBUG("为物品 '{}' 设置本地化数据", nameID);
    LOGF_TRACE("本地化内容 - 名称: '{}'，描述: '{}'",
               localized.name, localized.tool_tip);

    m_localized_data[nameID] = localized;
}

TEFMod::item_localized* TEFModLoader::ItemManager::get_localized(const TEFMod::item_name& name) {
    const auto& nameID = name.GetID();
    LOGF_DEBUG("获取物品 '{}' 的本地化数据", nameID);

    auto it = m_localized_data.find(nameID);
    if (it != m_localized_data.end()) {
        LOGF_TRACE("找到物品 '{}' 的本地化数据", nameID);
        return &it->second;
    }

    LOGF_WARN("未找到物品 '{}' 的本地化数据", nameID);
    return nullptr;
}

std::unordered_map<std::string, TEFMod::item_localized>
TEFModLoader::ItemManager::get_all_localized() {
    LOGF_DEBUG("获取所有本地化数据，总数: {}", m_localized_data.size());
    return m_localized_data;
}

void TEFModLoader::ItemManager::flushed_localized() {
    LOGF_DEBUG("开始刷新本地化数据到实例");

    static auto ItemTooltipClass = BNM::Class("Terraria.UI", "ItemTooltip");
    static auto LocalizedTextClass = BNM::Class("Terraria.Localization", "LocalizedText");
    static BNM::Field<void*> _text = ItemTooltipClass.GetField("_text");

    m_localized_instance.clear();
    LOGF_DEBUG("已清空本地化实例缓存");

    for (auto& localized : m_localized_data) {
        LOGF_TRACE("正在处理物品 '{}' 的本地化数据", localized.first);

        auto ItemTooltip = ItemTooltipClass.CreateNewObjectParameters();
        auto localizedText = LocalizedTextClass
                .CreateNewObjectParameters(
                        BNM::CreateMonoString("ItemTooltip." + localized.first),
                        BNM::CreateMonoString(localized.second.tool_tip)
                );

        _text[ItemTooltip].Set(localizedText);
        int itemID = m_name_to_id[localized.first];

        LOGF_TRACE("为物品ID {} 创建本地化实例", itemID);
        m_localized_instance[itemID] = {
                BNM::CreateMonoString(localized.second.name),
                ItemTooltip
        };
    }
    LOGF_DEBUG("已完成 {} 个物品的本地化刷新", m_localized_data.size());
}

void TEFModLoader::ItemManager::init_localized() {
    static std::string last_lang;

    LOGF_DEBUG("开始初始化本地化系统");

    BNM::Method<void*> get_ActiveCulture = BNM::Class("Terraria.Localization", "Language")
            .GetMethod("get_ActiveCulture", 0);

    static BNM::Method<BNM::Structures::Mono::String*> get_Name =
            BNM::Class("Terraria.Localization", "GameCulture").GetMethod("get_Name", 0);

    auto lang = get_Name[get_ActiveCulture.Call()].Call()->str();
    LOGF_DEBUG("当前语言设置: {}", lang);
    if (lang == last_lang) return;
    last_lang = lang;


    for (auto& item : m_items_instance) {
        LOGF_TRACE("为物品ID {} 设置文本内容", item.first);
        item.second->set_text(lang);
    }

    flushed_localized();
    LOGF_DEBUG("本地化系统初始化完成");
}

int TEFModLoader::ItemManager::get_id(const TEFMod::item_name& name) {
    const auto& nameID = name.GetID();
    int id = m_name_to_id[nameID] ?: 0;
    LOGF_DEBUG("获取物品 '{}' 的ID: {}", nameID, id);
    return id;
}

int TEFModLoader::ItemManager::get_id_from_str(const std::string &name) {
    int id = m_name_to_id[name] ?: 0;
    LOGF_DEBUG("获取物品 '{}' 的ID: {}", name, id);
    return id;
}

TEFMod::item_name TEFModLoader::ItemManager::get_name(int id) {
    LOGF_DEBUG("▷▷▷ 开始查询ID [{}] 对应的物品名称", id);

    if (auto it = m_id_to_name.find(id); it != m_id_to_name.end()) {
        const auto& name = it->second;
        LOGF_DEBUG("✔ 通过缓存找到ID [{}] 的注册名称: {}/{} (缓存命中)",
                   id, name.Namespace, name.Name);
        return name;
    }

    LOGF_TRACE("检查实例映射表...");
    if (auto instance_it = m_items_instance.find(id); instance_it != m_items_instance.end()) {
        TEFMod::Item* target_instance = instance_it->second;
        LOGF_TRACE("在实例表中找到ID [{}] 对应的实例指针: {}", id, (void*)target_instance);

        LOGF_TRACE("开始反向查找注册表...");
        for (const auto& [item_name, item_ptr] : m_items) {
            if (item_ptr == target_instance) {
                m_id_to_name[id] = item_name;

                LOGF_DEBUG("✔ 通过实例反向查找到ID [{}] 的注册名称: {}/{} (遍历匹配)",
                           id, item_name.Namespace, item_name.Name);
                LOGF_TRACE("已更新ID->名称缓存");
                return item_name;
            }
        }

        LOGF_WARN("⚠ ID [{}] 存在实例但未在注册表中找到对应名称", id);
    } else {
        LOGF_TRACE("实例表中未找到ID [{}] 的记录", id);
    }

    LOGF_TRACE("检查原始物品列表...");
    for (const auto& [item_name, item_id] : m_name_to_id) {
        if (item_id == id) {
            TEFMod::item_name name;
            size_t split_pos = item_name.find(':');
            if (split_pos != std::string::npos) {
                name.Namespace = item_name.substr(0, split_pos);
                name.Name = item_name.substr(split_pos + 1);
                m_id_to_name[id] = name;

                LOGF_DEBUG("✔ 通过名称映射找到ID [{}] 的名称: {}/{} (兼容模式)",
                           id, name.Namespace, name.Name);
                return name;
            }
        }
    }

    LOGF_WARN("✖ 未找到ID [{}] 对应的任何注册名称", id);
    LOGF_DEBUG("当前注册物品数量: {}", m_items.size());
    LOGF_DEBUG("当前实例表大小: {}", m_items_instance.size());
    LOGF_DEBUG("当前名称映射表大小: {}", m_name_to_id.size());

    return {"", ""};
}

std::pair<TEFMod::TerrariaInstance, TEFMod::TerrariaInstance>&
TEFModLoader::ItemManager::get_localized_instance(int id) {
    LOGF_DEBUG("请求获取ID为 {} 的本地化实例", id);
    auto& instance = m_localized_instance[id];
    LOGF_TRACE("返回ID {} 的本地化实例", id);
    return instance;
}

void TEFModLoader::ItemManager::add_recipe(const TEFMod::recipe &item) {}

void TEFModLoader::ItemManager::registered_unknown(const std::string &name) {
    static auto parse = [](const std::string& input) -> TEFMod::item_name {
        if (input.empty()) {
            throw std::invalid_argument("Empty input string");
        }

        const size_t sep_pos = input.find("::");

        if (sep_pos != std::string::npos) {
            return {
                    input.substr(0, sep_pos),  // Namespace部分
                    input.substr(sep_pos + 2)  // Name部分（跳过"::"）
            };
        }

        return { "TEFModLoader::Unclaimed", input };
    };

    auto i = parse(name);
    registered(i, new UnKnown_Item(i));
}