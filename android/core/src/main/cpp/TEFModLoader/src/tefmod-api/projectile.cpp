/*******************************************************************************
 * 文件名称: projectile
 * 项目名称: TEFModLoader
 * 创建时间: 2025/6/8
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

#include "tefmod-api/projectile.hpp"
#include "tefmod-api/base_type.hpp"
#include "logger.hpp"

#include <BNM/UserSettings/GlobalSettings.hpp>
#include <BNM/Class.hpp>
#include <BNM/Field.hpp>
#include <BNM/Method.hpp>

void TEFModLoader::ProjectileManager::assignment(int startID) {
    LOGF_DEBUG("开始弹幕ID分配，起始ID: {}", startID);
    LOGF_DEBUG("排序前，弹幕数量: {}", m_projectiles.size());

    std::stable_sort(m_projectiles.begin(), m_projectiles.end(),
                     [](const auto& a, const auto& b) {
                         LOGF_TRACE("比较弹幕: {} 和 {}", a.first.GetID(), b.first.GetID());
                         return a.first < b.first;
                     });
    LOGF_DEBUG("排序完成，当前弹幕数量: {}", m_projectiles.size());

    m_name_to_id.clear();
    LOGF_DEBUG("已清空名称到ID的映射表");

    for (int i = 0; i < m_projectiles.size(); ++i) {
        const auto& itemName = m_projectiles[i].first;
        int assignedID = startID + i;
        LOGF_DEBUG("为弹幕 '{}' 分配ID: {}", itemName.GetID(), assignedID);

        m_name_to_id[itemName.GetID()] = assignedID;
        m_projectiles_instance[assignedID] = m_projectiles[i].second;
        m_id_to_name[assignedID] = itemName;
    }

    _count = startID + static_cast<int>(m_projectiles.size()) - 1;

    LOGF_DEBUG("已完成 {} 个弹幕的ID分配", m_projectiles.size());
}

void TEFModLoader::ProjectileManager::registered(const TEFMod::identifier &name,
                                                 TEFMod::Projectile *projectile) {
    const auto& nameID = name.GetID();
    LOGF_DEBUG("正在注册弹幕 '{}'，实例地址: {}", nameID, (void*)projectile);

    if (m_registered_names.find(nameID) == m_registered_names.end()) {
        m_projectiles.emplace_back(name, projectile);
        m_registered_names.insert(nameID);
        LOGF_INFO("成功注册新弹幕: {}", nameID);
    } else {
        LOGF_WARN("尝试注册重复弹幕: {}", nameID);
    }
}

int TEFModLoader::ProjectileManager::get_count() {
    LOGF_DEBUG("获取弹幕总数: {}", _count);
    return _count;
}

void TEFModLoader::ProjectileManager::set_localized(TEFMod::identifier name,
                                                    const std::string &localized) {
    const auto& nameID = name.GetID();
    LOGF_DEBUG("为弹幕 '{}' 设置本地化数据", nameID);
    LOGF_TRACE("本地化内容 - 名称: '{}'",
               localized);

    m_localized_data[nameID] = localized;
}

int TEFModLoader::ProjectileManager::get_id(const TEFMod::identifier &name) {
    const auto& nameID = name.GetID();
    int id = m_name_to_id[nameID] ?: 0;
    LOGF_DEBUG("获取弹幕 '{}' 的ID: {}", nameID, id);
    return id;
}

int TEFModLoader::ProjectileManager::get_id_from_str(const std::string &name) {
    int id = m_name_to_id[name] ?: 0;
    LOGF_DEBUG("获取弹幕 '{}' 的ID: {}", name, id);
    return id;
}

TEFMod::identifier TEFModLoader::ProjectileManager::get_name(int id) {
    LOGF_DEBUG("▷▷▷ 开始查询ID [{}] 对应的弹幕名称", id);

    if (auto it = m_id_to_name.find(id); it != m_id_to_name.end()) {
        const auto& name = it->second;
        LOGF_DEBUG("✔ 通过缓存找到ID [{}] 的注册弹幕: {}/{} (缓存命中)",
                   id, name.Namespace, name.Name);
        return name;
    }

    LOGF_WARN("无法找到弹幕名称: {}", id);
    return {};
}

void TEFModLoader::ProjectileManager::flushed_localized() {
    LOGF_DEBUG("开始刷新本地化数据到实例");

    static auto ItemTooltipClass = BNM::Class("Terraria.UI", "ItemTooltip");
    static auto LocalizedTextClass = BNM::Class("Terraria.Localization", "LocalizedText");
    static BNM::Field<void*> _text = ItemTooltipClass.GetField("_text");

    m_localized_instance.clear();
    LOGF_DEBUG("已清空本地化实例缓存");

    static IL2CppArray<void*> _projectileNameCache(static_cast<BNM::Field<void*>>(BNM::Class("Terraria", "Lang").GetField("_projectileNameCache")).Get());
    for (auto& localized : m_localized_data) {
        LOGF_TRACE("正在处理弹幕 '{}' 的本地化数据", localized.first);

        int projectileID = m_name_to_id[localized.first];

        auto localizedText = LocalizedTextClass
                .CreateNewObjectParameters(
                        BNM::CreateMonoString(localized.first),
                        BNM::CreateMonoString(localized.second)
                );

        LOGF_TRACE("为弹幕ID {} 创建本地化实例", projectileID);

        m_localized_instance[projectileID] = localizedText;
        _projectileNameCache.Set(projectileID, localizedText);
    }


    LOGF_DEBUG("已完成 {} 个弹幕的本地化刷新", m_localized_data.size());
}

void TEFModLoader::ProjectileManager::init_localized() {
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


    for (auto& item : m_projectiles_instance) {
        LOGF_TRACE("为物品ID {} 设置文本内容", item.first);
        item.second->set_text(lang);
    }

    flushed_localized();
    LOGF_DEBUG("本地化系统初始化完成");
}

TEFMod::Projectile *TEFModLoader::ProjectileManager::get_projectile_instance(int id) {
    LOGF_DEBUG("请求获取ID为 {} 的弹幕实例", id);
    auto instance = m_projectiles_instance[id] ?: nullptr;
    LOGF_DEBUG("返回弹幕实例指针: {}", (void*)instance);
    return instance;
}