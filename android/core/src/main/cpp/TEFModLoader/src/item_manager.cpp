/*******************************************************************************
 * 文件名称: item_manager
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

#include <item_manager.hpp>
#include <set_factory.hpp>
#include <texture_assets.hpp>
#include <tefmod-api/item.hpp>
#include <logger.hpp>

#include <BNM/UserSettings/GlobalSettings.hpp>
#include <BNM/Field.hpp>
#include <BNM/Method.hpp>
#include <tefmod-api/IL2CppArray.hpp>

void TEFModLoader::item_manager::SetDefaults_T(void *instance, int Type, bool noMatCheck,
                                               void *variant) {
    old_SetDefaults(instance, Type, noMatCheck, variant);
    for (auto fun: SetDefaults_HookTemplate.FunctionArray) {
        if (fun) reinterpret_cast<decltype(old_SetDefaults)>(fun)(instance, Type, noMatCheck, variant);
    }
}

void TEFModLoader::item_manager::SetDefaults(void *instance, int Type, bool noMatCheck, void *variant) {
    static auto manager =  TEFModLoader::ItemManager::GetInstance();
    static auto ItemClass = BNM::Class("Terraria", "Item");

    if (Type >= TEFModLoader::SetFactory::count.item && Type <= manager->get_count()) {
        LOGF_DEBUG("检测到自定义物品(类型ID: {})，开始处理", Type);
        if (auto it = manager->get_item_instance(Type)) {
            manager->init_localized();

            if (need_flush_localized) {
                manager->flushed_localized();
                LOGF_INFO("刷新本地化数据");
                need_flush_localized = false;
            }


            LOGF_DEBUG("调用物品实例的set_defaults方法");
            it->set_defaults(instance);

            LOGF_DEBUG("获取本地化实例数据");
            auto localized_instance = manager->get_localized_instance(Type);

            if (localized_instance.second) {
                LOGF_DEBUG("设置物品提示信息");
                static_cast<BNM::Field<void *>>(ItemClass.GetField("BestiaryNotes"))[instance].Set(
                        localized_instance.second);
                /*static_cast<BNM::Field<void *>>(ItemClass.GetField("ToolTip"))[instance].Set(
                        localized_instance.second);*/
            } else {
                LOGF_WARN("物品类型 {} 缺少提示信息", Type);
            }

            if (localized_instance.first) {
                LOGF_DEBUG("设置物品名称");
                static_cast<BNM::Field<void *>>(ItemClass.GetField("_nameOverride"))[instance].Set(
                        localized_instance.first);
            } else {
                LOGF_WARN("物品类型 {} 缺少名称", Type);
                static_cast<BNM::Field<void *>>(ItemClass.GetField("_nameOverride"))[instance].Set(
                        BNM::CreateMonoString("Unknown"));
            }
        }

        static_cast<BNM::Field<int>>(ItemClass.GetField("type"))[instance].Set(Type);
        static_cast<BNM::Field<int>>(ItemClass.GetField("netID"))[instance].Set(Type);
        static_cast<BNM::Field<int>>(ItemClass.GetField("stack"))[instance].Set(1);
    }
}

void TEFModLoader::item_manager::GrantArmorBenefits_T(void *instance, void* armorPiece) {
    old_GrantArmorBenefits(instance, armorPiece);
    for (auto fun: GrantArmorBenefits_HookTemplate.FunctionArray) {
        if (fun) reinterpret_cast<decltype(old_GrantArmorBenefits)>(fun)(instance, armorPiece);
    }
}

void TEFModLoader::item_manager::GrantArmorBenefits(void *instance, void *armorPiece) {
    static auto manager =  TEFModLoader::ItemManager::GetInstance();
    static auto ItemClass = BNM::Class("Terraria", "Item");

    if (auto Type = static_cast<BNM::Field<int>>(ItemClass.GetField("type"))[armorPiece].Get(); Type >= TEFModLoader::SetFactory::count.item && Type <= manager->get_count()) {
        LOGF_DEBUG("检测到自定义物品(类型ID: {})，开始处理", Type);
        if (auto it = manager->get_item_instance(Type)) {
            it->apply_equip_effects(instance, armorPiece);
            it->update_armor_sets(instance);
            LOGF_INFO("已调用装备处理");
        }
    }
}
bool TEFModLoader::item_manager::ItemCheck_CheckCanUse(void *instance, void *item) {
    static auto manager =  TEFModLoader::ItemManager::GetInstance();
    static auto ItemClass = BNM::Class("Terraria", "Item");
    static BNM::Field<int> Item_Type = ItemClass.GetField("type");

    if (auto Type = Item_Type[item].Get(); Type >= TEFModLoader::SetFactory::count.item && Type <= manager->get_count()) {
        LOGF_DEBUG("检测到自定义物品(类型ID: {})，开始处理", Type);
        if (auto it = manager->get_item_instance(Type)) {
            return it->can_use(instance, item);
        }
    }

    return old_ItemCheck_CheckCanUse(instance, item);
}

void TEFModLoader::item_manager::SetupRecipeGroups_T() {
    old_SetupRecipeGroups();
    for (auto fun: SetupRecipeGroups_HookTemplate.FunctionArray) {
        if (fun) reinterpret_cast<decltype(old_SetupRecipeGroups)>(fun)();
    }
}

void TEFModLoader::item_manager::init(TEFMod::TEFModAPI *api) {
    static bool inited = false;
    if (!inited) {
        api->registerFunctionDescriptor({
            "Terraria",
            "Item",
            "SetDefaults",
            "hook>>void",
            3,
            &SetDefaults_HookTemplate,
            { reinterpret_cast<void*>(SetDefaults) }
        });
        api->registerFunctionDescriptor({
            "Terraria",
            "Player",
            "GrantArmorBenefits",
            "hook>>void",
            1,
            &GrantArmorBenefits_HookTemplate,
            { reinterpret_cast<void*>(GrantArmorBenefits) }
        });
        inited = true;
    } else {
        old_SetDefaults = api->GetAPI<decltype(old_SetDefaults)>({
            "Terraria",
            "Item",
            "SetDefaults",
            "old_fun",
            3
        });
        old_GrantArmorBenefits = api->GetAPI<decltype(old_GrantArmorBenefits)>({
            "Terraria",
            "Player",
            "GrantArmorBenefits",
            "old_fun",
            1
        });
    }
}

void TEFModLoader::item_manager::init() {
    BNM::BasicHook(BNM::Class("Terraria", "Player").GetMethod("ItemCheck_CheckCanUse", 1),
                   ItemCheck_CheckCanUse, old_ItemCheck_CheckCanUse);
}