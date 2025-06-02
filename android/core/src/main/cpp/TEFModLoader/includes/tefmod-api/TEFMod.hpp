/*******************************************************************************
 * 文件名称: TEFMod
 * 项目名称: TEFMod-API
 * 创建时间: 25-5-11
 * 作者: EternalFuture゙
 * Gitlab: https://gitlab.com/2079541547/
 * 协议: Apache License 2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
 
#pragma once

#include <string>
#include <unordered_map>
#include <vector>

namespace TEFMod {

    struct HookTemplate {
        void* Trampoline;
        std::vector<void*> FunctionArray;
        void setFunctions(std::vector<void*> functions) { FunctionArray = std::move(functions); }
    };

    struct DescriptorBase {
        std::string Namespace;
        std::string Class;
        std::string Name;
        std::string Type;

        std::string ID() const { return Namespace + Class + Name + Type; }
    };

    struct ModApiDescriptor : DescriptorBase {
        int Arg;

        std::string GetID() const { return ID() + std::to_string(Arg); }
    };

    struct ModFuncDescriptor : DescriptorBase {
        int Arg;
        HookTemplate* Template;
        std::vector<void*> FunPtr;

        std::string GetID() const { return ID() + std::to_string(Arg); }
    };

    class TEFModAPI {
        virtual void* getApiPointer(const std::string& id) = 0;

    public:
        virtual ~TEFModAPI() = default;

        template<typename T>
        T GetAPI(const ModApiDescriptor& api) { return reinterpret_cast<T>(getApiPointer(api.GetID())); }

        virtual void registerApiDescriptor(const ModApiDescriptor& apiDesc) = 0;
        virtual void registerFunctionDescriptor(const ModFuncDescriptor& funcDesc) = 0;
    };

}