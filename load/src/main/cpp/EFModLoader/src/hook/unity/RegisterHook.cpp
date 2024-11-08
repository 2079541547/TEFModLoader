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


#include <EFModLoader/hook/unity/RegisterHook.hpp>


namespace EFModLoader::RegisterHook::Unity {

    // 存储所有待注册的hooks
    vector<hooks> registerHooks;

    // 存储所有待注册的加载事件回调函数
    vector<EventCallback> registerLoad;

    /**
     * @fn RegisterLoad
     * @brief 注册一个加载事件回调函数。
     *
     * @param ptr 要注册的加载事件回调函数指针。
     */
    void RegisterLoad(EventCallback ptr) {
        registerLoad.push_back(ptr);
        EFLOG(LogLevel::INFO, "RegisterHook", "unity", "RegisterLoad", "注册了新的加载");
    }

    /**
     * @fn RegisterHOOK
     * @brief 注册一个hook。
     *
     * @param hookName hook的名称。
     * @param Ptr 原始方法指针。
     * @param new_ptr 新的方法指针。
     * @param old_Ptr 旧的方法指针。
     */
    void RegisterHOOK(string hookName, BNM::MethodBase Ptr, void* new_ptr, void** old_Ptr) {
        // 查找已存在的API记录
        for (auto& h : registerHooks) {
            if (h.hookName == hookName) {
                EFLOG(LogLevel::INFO, "RegisterHook", "unity", "RegisterHOOK", "Hook已存在：" + hookName + " 将不进行注册操作");
                return;
            }
        }
        // 如果没有找到，创建新的API记录
        registerHooks.push_back({hookName, Ptr, new_ptr, old_Ptr});
        EFLOG(LogLevel::INFO, "RegisterHook", "unity", "RegisterHOOK", "注册了新的hook：" + hookName);
    }

    /**
     * @fn Register
     * @brief 执行所有已注册的hooks的实际注册过程。
     *
     * 遍历所有待注册的hooks，调用BNM::InvokeHook进行实际的hook操作。
     */
    void Register() {
        if (registerHooks.empty()) {
            EFLOG(LogLevel::WARN, "RegisterHook", "unity", "Register", "什么都没有诶٩(๑`^´๑)۶");
            return;
        }

        if (!registerHooks.empty()) {
            EFLOG(LogLevel::INFO, "RegisterHook", "unity", "Register", "正在处理普通Hook...");
            for (const auto& h : registerHooks) {
                if (EFModLoaderAPI::GetEFModLoader().FindHooks(h.hookName).empty()) {
                    EFLOG(LogLevel::WARN, "RegisterHook", "unity", "Register", "没有Mod注册的Hook：" + h.hookName);
                } else {
                    BNM::InvokeHook(h.ptr, h.new_ptr, h.old_ptr);
                    EFLOG(LogLevel::INFO, "RegisterHook", "unity", "Register", "已注册Hook：" + h.hookName);
                }
            }
            // 清空注册列表，防止重复注册
            registerHooks.clear();
            EFLOG(LogLevel::INFO, "RegisterHook", "unity", "Register", "普通Hook已处理完成");
        } else {
            EFLOG(LogLevel::INFO, "RegisterHook", "unity", "Register", "没有注册普通Hook");
        }
    }

    /**
     * @fn check_InvokeHook
     * @brief 检查是否需要调用InvokeHook。
     *
     * @param hookName hook的名称。
     * @return 返回true表示需要调用InvokeHook，false表示不需要。
     */
    bool check_InvokeHook(string hookName) {
        if (EFModLoaderAPI::GetEFModLoader().FindHooks(hookName).empty()) {
            EFLOG(LogLevel::WARN, "RegisterHook", "unity", "Register", "没有Mod注册的虚拟Hook：" + hookName);
            return false;
        } else {
            EFLOG(LogLevel::INFO, "RegisterHook", "unity", "Register", "已注册虚拟Hook：" + hookName);
            return true;
        }
    }

}