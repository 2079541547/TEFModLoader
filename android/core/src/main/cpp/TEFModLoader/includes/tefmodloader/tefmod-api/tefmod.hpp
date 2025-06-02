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

#pragma once

#include <TEFMod.hpp>

#include <shared_mutex>

namespace TEFModLoader {

    class TEFModAPI: public TEFMod::TEFModAPI {
        std::unordered_map<std::string, TEFMod::ModApiDescriptor> _api_data;
        std::unordered_map<std::string, TEFMod::ModFuncDescriptor> _func_data;

        std::unordered_map<std::string, void*> _api_ptr;

        mutable std::shared_mutex _api_data_mutex;
        mutable std::shared_mutex _func_data_mutex;
        mutable std::shared_mutex _api_ptr_mutex;

        void* getApiPointer(const std::string& id) override;

    public:

        static TEFModAPI* GetInstance() {
            static TEFModAPI instance;
            return &instance;
        }

        std::unordered_map<std::string, TEFMod::ModApiDescriptor> getAllApiDescriptors();
        std::unordered_map<std::string, TEFMod::ModFuncDescriptor> getAllFunctionDescriptors();
        void registerApiImplementation(const TEFMod::ModApiDescriptor& apiId, void* apiPtr);


        void registerApiDescriptor(const TEFMod::ModApiDescriptor& apiDesc) override;
        void registerFunctionDescriptor(const TEFMod::ModFuncDescriptor& funcDesc) override;
    };

}