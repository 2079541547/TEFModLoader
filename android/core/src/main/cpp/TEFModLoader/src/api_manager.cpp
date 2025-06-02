/*******************************************************************************
 * 文件名称: api_manager
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

#include <api_manager.hpp>
#include <logger.hpp>

#include <tefmod-api/tefmod.hpp>

#include <BNM/UserSettings/GlobalSettings.hpp>
#include <BNM/Field.hpp>
#include <BNM/Method.hpp>

void TEFModLoader::APIManager::auto_processing() {
    const auto& apiDescriptors = TEFModAPI::GetInstance()->getAllApiDescriptors();
    if (apiDescriptors.empty()) {
        LOGF_WARN("自动创建API: 未找到任何API描述符");
        return;
    }

    LOGF_INFO("开始自动处理API，共 {} 个描述符", apiDescriptors.size());

    for (const auto& [id, desc] : apiDescriptors) {
        LOGF_DEBUG("处理API描述符 - ID: {}, 类型: {}, 命名空间: {}, 类: {}, 名称: {}",
                   id, desc.Type, desc.Namespace, desc.Class, desc.Name);

        const size_t dotPos = desc.Class.find('.');
        const bool isInnerClass = (dotPos != std::string::npos);
        const std::string outerClass = isInnerClass ? desc.Class.substr(0, dotPos) : "";
        const std::string innerClass = isInnerClass ? desc.Class.substr(dotPos + 1) : "";

        try {
            if (desc.Type == "Field") {
                BNM::FieldBase* field = isInnerClass
                                        ? new BNM::FieldBase(BNM::Class(desc.Namespace, outerClass).GetInnerClass(innerClass).GetField(desc.Name))
                                        : new BNM::FieldBase(BNM::Class(desc.Namespace, desc.Class).GetField(desc.Name));

                LOGF_INFO("成功创建字段API - ID: {}, 地址: {}, 类型: {}", desc.GetID(), static_cast<void*>(field), desc.Type);
                TEFModAPI::GetInstance()->registerApiImplementation(desc, field);
            }
            else if (desc.Type == "Class") {
                BNM::Class* cls = isInnerClass
                                  ? new BNM::Class(BNM::Class(desc.Namespace, outerClass).GetInnerClass(innerClass))
                                  : new BNM::Class(BNM::Class(desc.Namespace, desc.Class));

                LOGF_INFO("成功创建类API - 地址: {}, 类型: {}", static_cast<void*>(cls), desc.Type);
                TEFModAPI::GetInstance()->registerApiImplementation(desc, cls);
            }
            else if (desc.Type == "Method") {
                BNM::MethodBase* method = isInnerClass
                                          ? new BNM::MethodBase(BNM::Class(desc.Namespace, outerClass).GetInnerClass(innerClass).GetMethod(desc.Name, desc.Arg))
                                          : new BNM::MethodBase(BNM::Class(desc.Namespace, desc.Class).GetMethod(desc.Name, desc.Arg));

                LOGF_INFO("成功创建方法API - 地址: {}, 参数数: {}", static_cast<void*>(method), desc.Arg);
                TEFModAPI::GetInstance()->registerApiImplementation(desc, method);
            }
            else {
                LOGF_ERROR("未知API类型: {}", desc.Type);
            }
        }
        catch (const std::exception& e) {
            LOGF_ERROR("处理API描述符失败 - ID: {}, 错误: {}", id, e.what());
        }
    }

    LOGF_INFO("自动处理API完成，共处理 {} 个描述符", apiDescriptors.size());
}