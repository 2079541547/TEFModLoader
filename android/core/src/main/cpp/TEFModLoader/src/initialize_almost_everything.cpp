/*******************************************************************************
 * 文件名称: initialize_almost_everything
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

#include <initialize_almost_everything.hpp>
#include <logger.hpp>
#include <set_factory.hpp>
#include <texture_assets.hpp>
#include <item_manager.hpp>
#include <tefmod-api/item.hpp>

#include <BNM/UserSettings/GlobalSettings.hpp>
#include <BNM/Class.hpp>
#include <BNM/Method.hpp>
#include <BNM/Field.hpp>

#include <tefmod-api/IL2CppArray.hpp>

void TEFModLoader::Initialize_AlmostEverything::init(TEFMod::TEFModAPI *api) {
    static bool inited = false;
    if (!inited) {
        api->registerFunctionDescriptor({
            "Terraria",
            "Main",
            "Initialize_AlmostEverything",
            "hook>>void",
            0,
            &Initialize_AlmostEverything_HookTemplate,
            { reinterpret_cast<void*>(Initialize_AlmostEverything_Hook) }
        });
        inited = true;
    } else {
        old_Initialize_AlmostEverything_Hook = api->GetAPI<decltype(old_Initialize_AlmostEverything_Hook)>({
            "Terraria",
            "Main",
            "Initialize_AlmostEverything",
            "old_fun",
            0
        });
    }
}

void TEFModLoader::Initialize_AlmostEverything::Initialize_AlmostEverything_T(void *Instance) {
    old_Initialize_AlmostEverything_Hook(Instance);
    for (auto fun : Initialize_AlmostEverything_HookTemplate.FunctionArray) {
        if (fun) reinterpret_cast<decltype(old_Initialize_AlmostEverything_Hook)>(fun)(Instance);
    }
}

void TEFModLoader::Initialize_AlmostEverything::Initialize_AlmostEverything_Hook(void *Instance) {
    init_item();
}

void TEFModLoader::Initialize_AlmostEverything::init_item() {
    // 基础初始化
    SetFactory::set_item();
    TextureAssets::init_item();
    ItemManager::GetInstance()->init_localized();

    // 获取必要反射信息
    static auto item_class = BNM::Class("Terraria", "Item");
    static BNM::Method<void> set_defaults = item_class.GetMethod("SetDefaults", 1);

    static BNM::Field<int> head_slot = item_class.GetField("headSlot");
    static BNM::Field<int> body_slot = item_class.GetField("bodySlot");
    static BNM::Field<int> leg_slot = item_class.GetField("legSlot");

    static BNM::Field<int> use_animation = item_class.GetField("useAnimation");

    static IL2CppArray<int> head_type(*static_cast<void**>(item_class.GetField("headType").GetFieldPointer()));
    static IL2CppArray<int> body_type(*static_cast<void**>(item_class.GetField("bodyType").GetFieldPointer()));
    static IL2CppArray<int> leg_type(*static_cast<void**>(item_class.GetField("legType").GetFieldPointer()));

    static IL2CppArray<bool> item_uses_right_fire(*static_cast<void**>(BNM::Class("Terraria", "Player").GetField("ItemUsesRightFire").GetFieldPointer()));

    for (int i = SetFactory::count.item; i < SetFactory::new_count.item; ++i) {
        auto item = item_class.CreateNewInstance();
        set_defaults[item](i);

        if (int animation = use_animation[item].Get(); animation > 0) item_uses_right_fire.Set(i, true);
        if (int head = head_slot[item].Get(); head > 0) head_type.Set(head, i);
        if (int body = body_slot[item].Get(); body > 0) body_type.Set(body, i);
        if (int leg = leg_slot[item].Get(); leg > 0) leg_type.Set(leg, i);

        if (auto it = ItemManager::GetInstance()->get_item_instance(i)) {
            it->init_static();
        }
    }

    item_manager::need_flush_localized = true;
}