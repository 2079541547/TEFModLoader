/*******************************************************************************
 * 文件名称: utility
 * 项目名称: TEFModLoader
 * 创建时间: 2025/2/23
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

#include <TEFModLoader/API/utility.hpp>
#include <BNM/UserSettings/GlobalSettings.hpp>
#include <BNM/UnityStructures.hpp>
#include <BNM/Field.hpp>
#include <EFModLoader/EFMod/EFMod.hpp>
#include <unordered_map>
#include <BNM/Method.hpp>
#include <type_traits>

template<typename T>
void TEFModLoader::API::Utility::setValue(void *field, T value, void *instance) {
    auto Field = (BNM::Field<T>*) field;
    auto Instance = (BNM::UnityEngine::Object*) instance;
    if (instance != nullptr) {
        Field->SetInstance(Instance);
    }
    Field->Set(value);
}

template<typename T>
T TEFModLoader::API::Utility::getValue(void *field, void *instance) {
    auto Field = (BNM::Field<T>*) field;
    auto Instance = (BNM::UnityEngine::Object*) instance;
    if (instance != nullptr) {
        Field->SetInstance(Instance);
    }
    return Field->Get();
}

template<typename T>
void TEFModLoader::API::Utility::setArrayValue(void *field, size_t index, T value, void *instance) {
    auto Field = (BNM::Field<BNM::Structures::Mono::Array<T>*>*) field;
    auto Instance = (BNM::UnityEngine::Object*) instance;
    if (instance != nullptr) {
        Field->SetInstance(Instance);
    }
    Field->Get()->At(index) = value;
}

template<typename T>
T TEFModLoader::API::Utility::getArrayValue(void *field, size_t index, void *instance) {
    auto Field = (BNM::Field<BNM::Structures::Mono::Array<T>*>*) field;
    auto Instance = (BNM::UnityEngine::Object*) instance;
    if (instance != nullptr) {
        Field->SetInstance(Instance);
    }
    return Field->Get()->At(index);
}

template<typename T>
void TEFModLoader::API::Utility::setArray(void *field, const std::vector<T>& value, void *instance) {
    auto Field = (BNM::Field<BNM::Structures::Mono::Array<T>*>*) field;
    auto Instance = (BNM::UnityEngine::Object*) instance;
    if (instance != nullptr) {
        Field->SetInstance(Instance);
    }

    Field->Get()->CopyFrom(value);
}

template<typename T>
std::vector<T> TEFModLoader::API::Utility::getArray(void *field, void *instance) {
    auto Field = (BNM::Field<BNM::Structures::Mono::Array<T>*>*) field;
    auto Instance = (BNM::UnityEngine::Object*) instance;
    if (instance != nullptr) {
        Field->SetInstance(Instance);
    }
    return Field->Get()->ToVector();
}

void TEFModLoader::API::Utility::setString(void *field, const std::string& value, void *instance) {
    setValue<BNM::Structures::Mono::String*>(field, BNM::CreateMonoString(std::move(value)), instance);
}

std::string TEFModLoader::API::Utility::getString(void *field, void *instance) {
    return getValue<BNM::Structures::Mono::String*>(field, instance)->str();
}

template<typename T>
T TEFModLoader::API::Utility::callMethod(void *method, void *instance, ...) {
    auto Method = (BNM::Method<T>*)method;
    auto Instance = (BNM::UnityEngine::Object*)instance;

    va_list args;
    va_start(args, instance);

    if (instance != nullptr) {
        Method->SetInstance(Instance);
    }

    if constexpr (std::is_same_v<T, void>) {
        Method->Call(args);
    } else {
        T result = Method->Call(args);
        va_end(args);
        return result;
    }

    va_end(args);
}

std::string TEFModLoader::API::Utility::callStringMethod(void *method, void *instance, ...) {
    auto Method = (BNM::Method<BNM::Structures::Mono::String*>*)method;
    auto Instance = (BNM::UnityEngine::Object*)instance;

    va_list args;
    va_start(args, instance);

    if (instance != nullptr) {
        Method->SetInstance(Instance);
    }

    va_end(args);

    return Method->Call(args)->str();

    va_end(args);
}

std::string TEFModLoader::API::Utility::toString(void *str) {
    return ((BNM::Structures::Mono::String*)str)->str();
}

void *TEFModLoader::API::Utility::toMonoString(const std::string& str) {
    return BNM::CreateMonoString(str);
}

void TEFModLoader::API::Utility::registration() {
    auto p = &EFModAPI::getEFModAPI();

    std::unordered_map<std::string, std::pair<int, void*>> function_FiledMap ={
            { "setInt", { 3, (void*)setValue<int> } },
            { "setInt64", { 3, (void*)setValue<int64_t> } },
            { "setInt32", { 3, (void*)setValue<int32_t> } },
            { "setBool", { 3, (void*)setValue<bool> } },
            { "setString", { 3, (void*)setString } },
            { "setFloat", { 3, (void*)setValue<float> } },
            { "setDouble", { 3, (void*)setValue<double> } },
            { "setInstance", { 3, (void*)setValue<BNM::UnityEngine::Object*> } },

            { "setIntArray", { 3, (void*)setArray<int> } },
            { "setInt32Array", { 3, (void*)setArray<int32_t> } },
            { "setInt64Array", { 3, (void*)setArray<int64_t> } },
            { "setFloat", { 3, (void*)setArray<float> } },
            { "setDouble", { 3, (void*)setArray<double> } },
            { "setInstance", { 3, (void*)setArray<BNM::UnityEngine::Object*> } },

            { "getInt", { 2, (void*) getValue<int> } },
            { "getInt64", { 2, (void*) getValue<int64_t> } },
            { "getInt32", { 2, (void*) getValue<int32_t> } },
            { "getBool", { 2, (void*) getValue<bool> } },
            { "getString", { 2, (void*) getValue<std::string> } },
            { "getFloat", { 2, (void*) getValue<float> } },
            { "getDouble", { 2, (void*) getValue<double> } },
            { "getInstance", { 2, (void*) getValue<BNM::UnityEngine::Object*> } },

            { "getIntArray", { 2, (void*) getArray<int> } },
            { "getInt32Array", { 2, (void*) getArray<int32_t> } },
            { "getInt64Array", { 2, (void*) getArray<int64_t> } },
            { "getFloatArray", { 2, (void*) getArray<float> } },
            { "getDoubleArray", { 2, (void*) getArray<double> } },
            { "getBoolArray", { 2, (void*) getArray<bool> } },
            { "getInstanceArray", { 2, (void*) getArray<BNM::UnityEngine::Object*> } },

            { "setIntArrayElement", { 4, (void*) setArrayValue<int> } },
            { "setInt32ArrayElement", { 4, (void*) setArrayValue<int32_t> } },
            { "setInt64ArrayElement", { 4, (void*) setArrayValue<int64_t> } },
            { "setFloatArrayElement", { 4, (void*) setArrayValue<float> } },
            { "setDoubleArrayElement", { 4, (void*) setArrayValue<double> } },
            { "setBoolArrayElement", { 4, (void*) setArrayValue<bool> } },
            { "setInstanceArrayElement", { 4, (void*) setArrayValue<BNM::UnityEngine::Object*> } },

            { "getIntArrayElement", { 3, (void*) getArrayValue<int> } },
            { "getInt32ArrayElement", { 3, (void*) getArrayValue<int32_t> } },
            { "getInt64ArrayElement", { 3, (void*) getArrayValue<int64_t> } },
            { "getFloatArrayElement", { 3, (void*) getArrayValue<float> } },
            { "getDoubleArrayElement", { 3, (void*) getArrayValue<double> } },
            { "getBoolArrayElement", { 3, (void*) getArrayValue<bool> } },
            { "getInstanceArrayElement", { 3, (void*) getArrayValue<BNM::UnityEngine::Object*> } },
    };

    std::unordered_map<std::string, std::pair<int, void*>> function_MethodMap = {
            { "callIntMethod", { 3, (void*) callMethod<int> } },
            { "callInt64Method", { 3, (void*) callMethod<int64_t> } },
            { "callInt32Method", { 3, (void*) callMethod<int32_t> } },
            { "callBoolMethod", { 3, (void*) callMethod<bool> } },
            { "callStringMethod", { 3, (void*) callMethod<BNM::Structures::Mono::String*> } },
            { "callStdStringMethod", { 3, (void*) callStringMethod } },
            { "callFloatMethod", { 3, (void*) callMethod<float> } },
            { "callDoubleMethod", { 3, (void*) callMethod<double> } },
            { "callInstanceMethod", { 3, (void*) callMethod<BNM::UnityEngine::Object*> } }
    };

    std::unordered_map<std::string, std::pair<int, void*>> functionMap = {
            { "toString", { 1, (void*) toString } },
            { "toMonoString", { 1, (void*) toMonoString } }
    };

    for (const auto& function : functionMap) {
        auto id = ModApiDescriptor {
                "TEFModLoader",
                "API",
                "Utility",
                function.first,
                "function",
                function.second.first
        }.getID();
        p->registerAPI(id, function.second.second);
    }


    for (const auto& function : function_MethodMap) {
        auto id = ModApiDescriptor {
                "TEFModLoader",
                "API",
                "Utility::Method",
                function.first,
                "function",
                function.second.first
        }.getID();
        p->registerAPI(id, function.second.second);
    }

    for (const auto& function : function_FiledMap) {
        auto id = ModApiDescriptor {
                "TEFModLoader",
                "API",
                "Utility::Filed",
                function.first,
                "function",
                function.second.first
        }.getID();
        p->registerAPI(id, function.second.second);
    }
}