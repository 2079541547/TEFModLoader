/*******************************************************************************
 * 文件名称: RegisterApi
 * 项目名称: EFModLoader
 * 创建时间: 2024/9/28
 * 作者: EternalFuture゙
 * Github: https://github.com/2079541547
 * 版权声明: Copyright © 2024 EternalFuture゙. All rights reserved.
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
 * 描述信息: 本文件为EFModLoader项目中的一部分。
 * 注意事项: 请严格遵守GNU AGPL v3.0协议使用本代码，任何未经授权的商业用途均属侵权行为。
 *******************************************************************************/

#pragma once

#include <iostream>
#include <vector>
#include <string>
#include <cstdint>
#include "EFModLoader/EFMod/EFMod.hpp"
#include "EFModLoader/api/Redirect.hpp"
#include "EFModLoader/log.hpp"

namespace EFModLoader::RegisterApi {

    /**
     * @struct API
     * @brief 表示一个待注册的API。
     *
     * 包含API的名称和新的函数指针。
     */
    struct API {
        std::string apiName;  ///< API的名称
        uintptr_t new_ptr;    ///< 新的API函数指针
    };

    /**
     * @var registerAPI
     * @brief 存储所有待注册的API信息的全局变量。
     */
    extern std::vector<API> registerAPI;

    /**
     * @fn RegisterAPI
     * @brief 注册一个API到系统中。
     *
     * 该函数检查是否存在同名的API，如果不存在，则将其添加到注册列表中。
     *
     * @param apiName API的名称。
     * @param api_ptr API函数指针。
     */
    void RegisterAPI(const std::string& apiName, uintptr_t api_ptr);

    /**
     * @fn Register
     * @brief 执行所有已注册API的实际注册过程。
     *
     * 该函数遍历注册列表，尝试从EFModLoader中查找每个API，并将其重定向到新的实现。
     * 如果注册列表为空，会记录警告日志。如果找不到API或重定向过程中发生错误，会记录错误日志。
     */
    void Register();

}