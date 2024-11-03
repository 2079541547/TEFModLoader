/*******************************************************************************
 * 文件名称: Register
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
#include "../log.hpp"

namespace EFModLoader::Redirect {

    template<typename T>
    uintptr_t getPtr(T* ptr) {
        auto ptrAddr = reinterpret_cast<uintptr_t>(ptr);
        EFLOG(LogLevel::INFO, "Redirect", "getPtr", "获取到的指针地址：" + std::to_string(ptrAddr));
        return ptrAddr;
    }

    template <typename R, typename... Args>
    R callFunction(void *funcPtr, Args &&...args) {
        // 从 uintptr_t 转换为函数指针
        using FuncPtr = R (*)(Args...);
        auto f = reinterpret_cast<FuncPtr>(funcPtr);
        // 调用函数
        return f(std::forward<Args>(args)...);
    }

    template<typename T>
    void redirectPointer(uintptr_t originalPtrAddress, uintptr_t newPtrAddress) {
        EFLOG(LogLevel::INFO, "Redirect", "redirectPointer", "正在尝试将：" + std::to_string(originalPtrAddress) + " 重定向为：" + std::to_string(newPtrAddress));

        T** originalPtr = reinterpret_cast<T**>(originalPtrAddress);
        *originalPtr = reinterpret_cast<T*>(newPtrAddress);

        EFLOG(LogLevel::INFO, "Redirect", "redirectPointer", "重定向成功，原始地址：" + std::to_string(originalPtrAddress) + " 重定向为：" + std::to_string(newPtrAddress));
    }




}