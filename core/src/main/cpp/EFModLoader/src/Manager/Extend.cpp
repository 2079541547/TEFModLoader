/*******************************************************************************
 * 文件名称: Extend
 * 项目名称: EFModLoader
 * 创建时间: 2024/12/29
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
 * 描述信息: 本文件为EFModLoader项目中的一部分。
 * 注意事项: 请严格遵守GNU AGPL v3.0协议使用本代码，任何未经授权的商业用途均属侵权行为。
 *******************************************************************************/
 
#include <Manager/Extend.hpp>
#include <Load/Loader.hpp>

void EFModLoader::Manager::Extend::registerFunc(const ModFuncDescriptor &func, void *ptr) {
        for (auto _: extend) {
                if (_.id == func.getID()) {
                        _.funcPtr.push_back(ptr);
                        return;
                }
        }
        extend.push_back({func.getID(), {ptr}});
}