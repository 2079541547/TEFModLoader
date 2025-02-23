/*******************************************************************************
 * 文件名称: utility
 * 项目名称: TEFModLoader
 * 创建时间: 2025/2/23
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

#include <string>
#include <vector>

namespace TEFModLoader::API::Utility {

    void registration();

    template<typename T>
    void setValue(void* field, T value, void* instance = nullptr);

    template<typename T>
    void setArrayValue(void* field, size_t index, T value, void* instance = nullptr);

    template<typename T>
    void setArray(void* field, const std::vector<T>&, void* instance = nullptr);

    template<typename T>
    T getValue(void* field, void* instance = nullptr);

    template<typename T>
    T getArrayValue(void* field, size_t index, void* instance = nullptr);

    template<typename T>
    std::vector<T> getArray(void* field, void* instance = nullptr);

    void setString(void* field, const std::string& value, void* instance = nullptr);
    std::string getString(void* field, void* instance = nullptr);

    template<typename T>
    T callMethod(void* method, void* instance = nullptr, ...);

    std::string callStringMethod(void* method, void* instance = nullptr, ...);

    std::string toString(void* str);
    void* toMonoString(const std::string& str);

}