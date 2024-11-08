/*******************************************************************************
 * 文件名称: RegisterHook
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
#include <functional>
#include "BNM/UserSettings/GlobalSettings.hpp"
#include <EFModLoader/EFMod/EFMod.hpp>
#include <EFModLoader/log.hpp>
#include <BNM/MethodBase.hpp>

namespace EFModLoader::RegisterHook::Unity {

    using namespace std;

    /**
     * @struct hooks
     * @brief 表示一个待注册的hook。
     *
     * 包含hook的名称、原始方法指针、新的方法指针和旧的方法指针。
     */
    struct hooks {
        string hookName;      ///< hook的名称
        BNM::MethodBase ptr;  ///< 原始方法指针
        void* new_ptr;        ///< 新的方法指针
        void** old_ptr;       ///< 旧的方法指针
    };

    /**
     * @typedef EventCallback
     * @brief 定义事件回调函数的类型。
     *
     * 事件回调函数没有参数和返回值。
     */
    typedef void (*EventCallback)();

    /**
     * @var registerHooks
     * @brief 存储所有待注册的hooks的全局变量。
     */
    extern vector<hooks> registerHooks;

    /**
     * @var registerLoad
     * @brief 存储所有待注册的加载事件回调函数的全局变量。
     */
    extern vector<EventCallback> registerLoad;

    /**
     * @fn RegisterLoad
     * @brief 注册一个加载事件回调函数。
     *
     * @param ptr 要注册的加载事件回调函数指针。
     */
    void RegisterLoad(EventCallback ptr);

    /**
     * @fn RegisterHOOK
     * @brief 注册一个hook。
     *
     * @param hookName hook的名称。
     * @param Ptr 原始方法指针。
     * @param new_ptr 新的方法指针。
     * @param old_Ptr 旧的方法指针。
     */
    void RegisterHOOK(string hookName, BNM::MethodBase Ptr, void* new_ptr, void** old_Ptr);

    /**
     * @fn Register
     * @brief 执行所有已注册的hooks的实际注册过程。
     *
     * 遍历所有待注册的hooks，调用BNM::InvokeHook进行实际的hook操作。
     */
    void Register();

    /**
     * @fn check_InvokeHook
     * @brief 检查是否需要调用InvokeHook。
     *
     * @param hookName hook的名称。
     * @return 返回true表示需要调用InvokeHook，false表示不需要。
     */
    bool check_InvokeHook(string hookName);

}

/**
 * @def RegisterIHOOK
 * @brief 宏定义，用于注册一个hook。
 *
 * 如果check_InvokeHook返回true，则调用BNM::InvokeHook进行hook操作。
 *
 * @param hookName hook的名称。
 * @param ptr 原始方法指针。
 * @param new_ptr 新的方法指针。
 * @param old_ptr 旧的方法指针。
 */
#define RegisterIHOOK(hookName, ptr, new_ptr, old_ptr) \
    if(EFModLoader::RegisterHook::Unity::check_InvokeHook(hookName)) \
        BNM::InvokeHook(ptr, new_ptr, old_ptr)