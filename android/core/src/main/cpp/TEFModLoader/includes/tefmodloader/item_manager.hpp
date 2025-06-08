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

#pragma once

#include "tefmod_api.hpp"

namespace TEFModLoader::item_manager {

    inline bool need_flush_localized = false;

    inline void (*old_SetDefaults)(void*, int, bool, void*);
    void SetDefaults(void* instance, int Type, bool noMatCheck, void* variant);
    void SetDefaults_T(void* instance, int Type, bool noMatCheck, void* variant);
    inline TEFMod::HookTemplate SetDefaults_HookTemplate = {
            reinterpret_cast<void*>(SetDefaults_T),
            {}
    };

    inline void (*old_GrantArmorBenefits)(void*, void*);
    void GrantArmorBenefits(void* instance, void* armorPiece);
    void GrantArmorBenefits_T(void* instance, void* armorPiece);
    inline TEFMod::HookTemplate GrantArmorBenefits_HookTemplate = {
            reinterpret_cast<void*>(GrantArmorBenefits_T),
            {}
    };

    inline bool (*old_ItemCheck_CheckCanUse)(void*, void*);
    bool ItemCheck_CheckCanUse(void* instance, void* item);

    void SetupRecipeGroups();
    inline void (*old_SetupRecipeGroups)();
    void SetupRecipeGroups_T();
    inline TEFMod::HookTemplate SetupRecipeGroups_HookTemplate = {
            reinterpret_cast<void*>(SetupRecipeGroups_T),
            {}
    };

    void Prefix_cctor();
    inline void (*old_Prefix_cctor)();
    void Prefix_cctor_T();
    inline TEFMod::HookTemplate Prefix_cctor_HookTemplate = {
            reinterpret_cast<void*>(Prefix_cctor_T),
            {}
    };

    void init(TEFMod::TEFModAPI* api);
    void init();
}
