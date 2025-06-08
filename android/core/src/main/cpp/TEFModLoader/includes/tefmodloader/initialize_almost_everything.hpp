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

#pragma once

#include "tefmod_api.hpp"

namespace TEFModLoader::Initialize_AlmostEverything {
    void init(TEFMod::TEFModAPI* api);

    void init_item();
    void init_item_animations();

    void init_projectile();

    inline void (*old_Initialize_AlmostEverything_Hook)(void*);
    void Initialize_AlmostEverything_Hook(void* Instance);
    void Initialize_AlmostEverything_T(void* Instance);
    inline TEFMod::HookTemplate Initialize_AlmostEverything_HookTemplate = {
            reinterpret_cast<void*>(Initialize_AlmostEverything_T),
            {}
    };
}