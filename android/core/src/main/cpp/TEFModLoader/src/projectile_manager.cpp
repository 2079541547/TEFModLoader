/*******************************************************************************
 * 文件名称: projectile_manager
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

#include "projectile_manager.hpp"
#include "set_factory.hpp"

#include "tefmod-api/projectile.hpp"
#include "logger.hpp"

#include <BNM/UserSettings/GlobalSettings.hpp>
#include <BNM/Field.hpp>
#include <BNM/Method.hpp>

void TEFModLoader::projectile_manager::SetDefaults_T(void *instance, int i) {
    old_SetDefaults(instance, i);
    for (auto fun : SetDefaults_HookTemplate.FunctionArray) {
        if (fun) reinterpret_cast<decltype(old_SetDefaults)>(fun)(instance, i);
    }
}

void TEFModLoader::projectile_manager::SetDefaults(void *instance, int Type) {
    static auto manager = ProjectileManager::GetInstance();
    static auto Projectile_class = BNM::Class("Terraria", "Projectile");
    static BNM::Field<int> width = Projectile_class.GetField("width");
    static BNM::Field<int> height = Projectile_class.GetField("height");
    static BNM::Field<float> scale = Projectile_class.GetField("scale");
    static BNM::Field<bool> active = Projectile_class.GetField("active");
    static BNM::Field<int> maxPenetrate = Projectile_class.GetField("maxPenetrate");

    if (Type >= TEFModLoader::SetFactory::count.projectile && Type <= manager->get_count()) {
        LOGF_DEBUG("检测到自定义弹幕(类型ID: {})，开始处理", Type);
        manager->init_localized();
        if (auto it = manager->get_projectile_instance(Type)) {
            it->set_defaults(instance);
            LOGF_DEBUG("自定义弹幕(类型ID: {})已设置属性", Type);
            active[instance].Set(true);
            width[instance].Set(static_cast<int>(width[instance].Get() * scale[instance].Get()));
            height[instance].Set(static_cast<int>(height[instance].Get() * scale[instance].Get()));
            maxPenetrate[instance].Set(static_cast<BNM::Field<int>>(Projectile_class.GetField("penetrate"))[instance].Get());
        }
    }
}

void TEFModLoader::projectile_manager::Kill_T(void *instance) {
    old_Kill(instance);
    for (auto fun : Kill_HookTemplate.FunctionArray) {
        if (fun) reinterpret_cast<decltype(old_Kill)>(fun)(instance);
    }
}

void TEFModLoader::projectile_manager::Kill(void *instance) {
    static auto manager = ProjectileManager::GetInstance();
    static auto Projectile_class = BNM::Class("Terraria", "Projectile");
    static BNM::Field<bool> active = Projectile_class.GetField("active");
    static BNM::Field<int> type = Projectile_class.GetField("type");

    int Type = type[instance].Get();
    if (Type >= SetFactory::count.projectile && Type <= manager->get_count()) {
        LOGF_DEBUG("检测到自定义弹幕(类型ID: {})，开始处理", Type);
        if (auto it = manager->get_projectile_instance(Type)) {
            it->kill(instance);
            LOGF_DEBUG("自定义弹幕(类型ID: {})已杀死", Type);
        }
        // active[instance].Set(false);
    }
}

void TEFModLoader::projectile_manager::Damage_T(void *instance) {
    old_Damage(instance);
    for (auto fun : Damage_HookTemplate.FunctionArray) {
        if (fun) reinterpret_cast<decltype(old_Damage)>(fun)(instance);
    }
}

void TEFModLoader::projectile_manager::Damage(void *instance) {
    static auto manager = ProjectileManager::GetInstance();
    static auto Projectile_class = BNM::Class("Terraria", "Projectile");
    static BNM::Field<bool> active = Projectile_class.GetField("active");
    static BNM::Field<int> type = Projectile_class.GetField("type");
    static BNM::Field<int> owner = Projectile_class.GetField("owner");
    static BNM::Method<int> myPlayer = BNM::Class("Terraria", "Main").GetMethod("get_myPlayer", 0);

    int Type = type[instance].Get();
    if (Type >= SetFactory::count.projectile && Type <= manager->get_count()) {
        LOGF_DEBUG("检测到自定义弹幕(类型ID: {})，开始处理", Type);
        if (auto it = manager->get_projectile_instance(Type)) {
            it->damage(instance);
            LOGF_DEBUG("自定义弹幕(类型ID: {})造成伤害", Type);
        }
    }
}

void TEFModLoader::projectile_manager::init(TEFMod::TEFModAPI *api) {
    static bool inited = false;
    if (!inited) {

        api->registerFunctionDescriptor({
            "Terraria",
            "Projectile",
            "SetDefaults",
            "hook>>void",
            1,
            &SetDefaults_HookTemplate,
            { reinterpret_cast<void*>(SetDefaults) }
        });


        api->registerFunctionDescriptor({
            "Terraria",
            "Projectile",
            "Kill",
            "hook>>void",
            0,
            &Kill_HookTemplate,
            { reinterpret_cast<void*>(Kill) }
        });


        api->registerFunctionDescriptor({
            "Terraria",
            "Projectile",
            "Damage",
            "hook>>void",
            0,
            &Damage_HookTemplate,
            { reinterpret_cast<void*>(Damage) }
        });

        inited = true;
    } else {
        old_SetDefaults = api->GetAPI<decltype(old_SetDefaults)>({
            "Terraria",
            "Projectile",
            "SetDefaults",
            "old_fun",
            1
        });

        old_Kill = api->GetAPI<decltype(old_Kill)>({
            "Terraria",
            "Projectile",
            "Kill",
            "old_fun",
            0
        });

        old_Damage = api->GetAPI<decltype(old_Damage)>({
            "Terraria",
            "Projectile",
            "Damage",
            "old_fun",
            0
        });
    }
}