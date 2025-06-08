/*******************************************************************************
 * 文件名称: set_factory
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

#include <cstdio>
#include <string>
#include <unordered_map>

namespace TEFModLoader::SetFactory {

    struct Count {
        int item;
        int npc;
        int buff;
        int dust;
        int gore;
        int tile;
        int wall;
        int projectile;
        int mount;

        Count() = default;
    };

    inline Count count;
    inline Count new_count;

    void init();

    inline bool id_inited = false;

    inline void (*old__ctor)(void *, int);
    void _ctor(void *instance, int i);

    void set_item();
    void set_projectile();

    bool safe_resize_array(const std::string& namespaceName,
                           const std::string& className,
                           const std::string& fieldName,
                           const std::string& typeName,
                           size_t new_size);
}