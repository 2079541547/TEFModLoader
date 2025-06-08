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

#pragma once

#include "tefmod_api.hpp"

namespace TEFModLoader::projectile_manager {

    // void init();
    void init(TEFMod::TEFModAPI* api);

    inline void (*old_SetDefaults)(void*, int);
    void SetDefaults(void* instance, int Type);
    void SetDefaults_T(void* instance, int Type);
    inline TEFMod::HookTemplate SetDefaults_HookTemplate = {
            reinterpret_cast<void*>(SetDefaults_T),
            {}
    };

    inline void (*old_Kill)(void*);
    void Kill(void* instance);
    void Kill_T(void* instance);
    inline TEFMod::HookTemplate Kill_HookTemplate = {
            reinterpret_cast<void*>(Kill_T),
            {}
    };

    inline void (*old_Damage)(void*);
    void Damage(void* instance);
    void Damage_T(void* instance);
    inline TEFMod::HookTemplate Damage_HookTemplate = {
            reinterpret_cast<void*>(Damage_T),
            {}
    };
}