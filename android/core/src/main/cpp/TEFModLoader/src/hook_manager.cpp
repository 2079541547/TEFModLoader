/*******************************************************************************
 * 文件名称: hook_manager
 * 项目名称: TEFModLoader
 * 创建时间: 2025/5/17
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

#include <hook_manager.hpp>
#include <logger.hpp>

#include <tefmod-api/tefmod.hpp>

#include <BNM/UserSettings/GlobalSettings.hpp>
#include <BNM/Method.hpp>
#include <BNM/Class.hpp>

void TEFModLoader::HookManager::auto_hook() {
    LOGF_INFO("开始自动挂钩处理，共检测到 {} 个函数描述符",
              TEFModAPI::GetInstance()->getAllFunctionDescriptors().size());

    for (const auto& funcDesc : TEFModAPI::GetInstance()->getAllFunctionDescriptors()) {
        const auto& desc = funcDesc.second;
        LOGF_DEBUG("处理函数描述符 - ID: {}, 类: {}::{}, 方法: {}, 参数数: {}",
                   desc.GetID(), desc.Namespace, desc.Class, desc.Name, desc.Arg);

        // 设置函数指针到模板
        desc.Template->setFunctions(desc.FunPtr);
        LOGF_TRACE("设置 {} 个函数指针到模板", desc.FunPtr.size());

        // 解析类信息
        BNM::Class Class;
        size_t dotPosition = desc.Class.find('.');
        if (dotPosition != std::string::npos) {
            std::string outerClass = desc.Class.substr(0, dotPosition);
            std::string innerClass = desc.Class.substr(dotPosition + 1);
            Class = BNM::Class(desc.Namespace, outerClass).GetInnerClass(innerClass);
            LOGF_DEBUG("解析嵌套类: {}.{} -> {}", outerClass, innerClass, Class ? "成功" : "失败");
        } else {
            Class = BNM::Class(desc.Namespace, desc.Class);
            LOGF_DEBUG("解析普通类: {} -> {}", desc.Class, Class ? "成功" : "失败");
        }

        if (!Class) {
            LOGF_ERROR("无法解析类: {}::{}", desc.Namespace, desc.Class);
            continue;
        }

        BNM::MethodBase method = Class.GetMethod(desc.Name, desc.Arg);

        std::string hookT = desc.Type.substr(0, desc.Type.find(">>"));
        void** old_fun = nullptr;
        void* hooked_fun = desc.Template->Trampoline;

        if (!hooked_fun) {
            LOGF_ERROR("模板跳板函数未初始化，跳过挂钩");
            continue;
        }

        bool hookSuccess = false;
        if (hookT == "hook") {
            LOGF_DEBUG("尝试普通挂钩");
            BasicHook(method, hooked_fun, old_fun);
            hookSuccess = (old_fun != nullptr);
        }
        else if (hookT == "ihook") {
            LOGF_DEBUG("尝试调用挂钩(INVOKE)");
            hookSuccess = BNM::InvokeHook(method, hooked_fun, old_fun);
        }
        else if (hookT == "vhook") {
            LOGF_DEBUG("尝试虚函数挂钩(VIRTUAL)");
            hookSuccess = BNM::VirtualHook(Class, method, hooked_fun, old_fun);
        }
        else {
            LOGF_ERROR("未知挂钩类型: '{}'，跳过", hookT);
            continue;
        }

        if (hookSuccess) {
            LOGF_INFO("挂钩成功 - 类型: {}, 原函数地址: {}, 新函数地址: {}",
                      hookT, static_cast<void*>(old_fun), hooked_fun);

            TEFModAPI::GetInstance()->registerApiImplementation({
                                                                        desc.Namespace,
                                                                        desc.Class,
                                                                        desc.Name,
                                                                        "old_fun",
                                                                        desc.Arg
                                                                }, (void*)old_fun);
        } else {
            LOGF_ERROR("挂钩失败 - 类型: {}", hookT);
        }
    }

    LOGF_INFO("自动挂钩处理完成");
}

