/*******************************************************************************
 * 文件名称: tefmod
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

#include <tefmod-api/tefmod.hpp>

#include <logger.hpp>

void *TEFModLoader::TEFModAPI::getApiPointer(const std::string &id) {
    std::shared_lock lock(_api_ptr_mutex);
    auto it = _api_ptr.find(id);
    if (it == _api_ptr.end()) {
        LOGF_WARN("未找到ID为[{}]的API指针", id);
        return nullptr;
    }
    LOGF_TRACE("成功获取ID为[{}]的API指针，地址: {}", id, static_cast<void *>(it->second));
    return it->second;
}

std::unordered_map<std::string, TEFMod::ModApiDescriptor>
TEFModLoader::TEFModAPI::getAllApiDescriptors() {
    std::shared_lock lock(_api_data_mutex);
    LOGF_DEBUG("获取所有API描述符，当前数量: {}", _api_data.size());
    return _api_data;
}

std::unordered_map<std::string, TEFMod::ModFuncDescriptor>
TEFModLoader::TEFModAPI::getAllFunctionDescriptors() {
    std::shared_lock lock(_func_data_mutex);
    LOGF_DEBUG("获取所有函数描述符，当前数量: {}", _func_data.size());
    return _func_data;
}

void TEFModLoader::TEFModAPI::registerApiImplementation(const TEFMod::ModApiDescriptor &apiDesc,
                                                        void *apiPtr) {
    const std::string &id = apiDesc.GetID();

    {
        std::unique_lock lock(_api_ptr_mutex);
        if (_api_ptr.contains(id)) {
            LOGF_WARN("尝试重复注册API实现[{}]，已存在地址: {}，新地址: {}",
                      id, static_cast<void *>(_api_ptr[id]), static_cast<void *>(apiPtr));
            return;
        }
        _api_ptr[id] = apiPtr;
    }

    LOGF_INFO("成功注册API实现[{}]，地址: {}",
              id, static_cast<void *>(apiPtr));
}

void TEFModLoader::TEFModAPI::registerApiDescriptor(const TEFMod::ModApiDescriptor &apiDesc) {
    const std::string &id = apiDesc.GetID();

    {
        std::unique_lock lock(_api_data_mutex);
        if (_api_data.contains(id)) {
            LOGF_WARN("尝试重复注册API描述符[{}]",
                      id);
            return;
        }
        _api_data[id] = apiDesc;
    }

    LOGF_INFO("成功注册API描述符 - ID: {}",
              id);
}

void TEFModLoader::TEFModAPI::registerFunctionDescriptor(const TEFMod::ModFuncDescriptor &funcDesc) {
    const std::string &id = funcDesc.GetID();

    {
        std::unique_lock lock(_func_data_mutex);
        if (_func_data.contains(id)) {
            auto& existingDesc = _func_data[id];
            for (auto ptr : funcDesc.FunPtr) {
                if (std::find(existingDesc.FunPtr.begin(),
                              existingDesc.FunPtr.end(),
                              ptr) == existingDesc.FunPtr.end()) {
                    existingDesc.FunPtr.push_back(ptr);
                    LOGF_DEBUG("合并函数指针[{}]到描述符[{}]，新指针: {}",
                               static_cast<void*>(ptr), id, static_cast<void*>(ptr));
                } else {
                    LOGF_DEBUG("函数指针[{}]已存在于描述符[{}]中，跳过: {}",
                               static_cast<void*>(ptr), id, static_cast<void*>(ptr));
                }
            }

            LOGF_INFO("合并函数描述符[{}]，现有函数指针数量: {}",
                      id, existingDesc.FunPtr.size());
            return;
        }
        _func_data[id] = funcDesc;
    }

    LOGF_INFO("成功注册函数描述符[{}]，初始函数指针数量: {}",
              id, funcDesc.FunPtr.size());
}

