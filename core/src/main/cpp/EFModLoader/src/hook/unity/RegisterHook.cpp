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
#include <EFModLoader/EFMod/EFMod.hpp>
#include <EFModLoader/log.hpp>
#include <BNM/MethodBase.hpp>


namespace EFModLoader::RegisterHook::Unity {

    vector<hooks> registerHooks;
    vector<EventCallback> registerLoad;

    void RegisterLoad(EventCallback ptr) {
        registerLoad.push_back({ptr});
        EFLOG(LogLevel::INFO, "RegisterHook", "unity", "RegisterLoad", "注册了新的加载");
    }


    void RegisterHOOK(string hookName, BNM::MethodBase Ptr, void* new_ptr, void** old_Ptr) {
        // 查找已存在的API记录
        for (auto& hooks : registerHooks) {
            if (hooks.hookName == hookName) {
                EFLOG(LogLevel::INFO, "RegisterHook", "unity", "RegisterHOOK", "Hook已存在：" + hookName + " 将不进行注册操作");
                return;
            }
        }
        // 如果没有找到，创建新的API记录
        registerHooks.push_back({hookName, Ptr, new_ptr, old_Ptr});
        EFLOG(LogLevel::INFO, "RegisterHook", "unity", "RegisterHOOK", "注册了新的hook：" + hookName);
    }


    void Register() {
        if (registerHooks.empty()) {
            EFLOG(LogLevel::WARN, "RegisterHook", "unity", "Register", "什么都没有诶٩(๑`^´๑)۶");
            return;
        }

        if (!registerHooks.empty()) {
            EFLOG(LogLevel::INFO, "RegisterHook", "unity", "Register", "正在处理普通Hook...");
            for (const auto& hooks : registerHooks) {
                if (EFModLoaderAPI::GetEFModLoader().FindHooks(hooks.hookName).empty()) {
                    EFLOG(LogLevel::WARN, "RegisterHook", "unity", "Register", "没有Mod注册的Hook：" + hooks.hookName);
                } else {
                    HOOK(hooks.ptr, hooks.new_ptr, hooks.old_ptr);
                    EFLOG(LogLevel::INFO, "RegisterHook", "unity", "Register", "已注册Hook：" + hooks.hookName);
                }
            }
            // 清空注册列表，防止重复注册
            registerHooks.clear();
            EFLOG(LogLevel::INFO, "RegisterHook", "unity", "Register", "普通Hook已处理完成");
        } else {
            EFLOG(LogLevel::INFO, "RegisterHook", "unity", "Register", "没有注册普通Hook");
        }
    }

    bool check_InvokeHook(string hookName) {
        if (EFModLoaderAPI::GetEFModLoader().FindHooks(hookName).empty()) {
            EFLOG(LogLevel::WARN, "RegisterHook", "unity", "Register", "没有Mod注册的虚拟Hook：" + hookName);
        } else {
            EFLOG(LogLevel::INFO, "RegisterHook", "unity", "Register", "已注册虚拟Hook：" + hookName);
            return true;
        }
        return false;
    }

}


