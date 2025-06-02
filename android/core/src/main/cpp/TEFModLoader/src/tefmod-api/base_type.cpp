/*******************************************************************************
 * 文件名称: base_type
 * 项目名称: TEFModLoader
 * 创建时间: 2025/5/11
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

#include "tefmod-api/base_type.hpp"

#include "BNM/UserSettings/GlobalSettings.hpp"
#include "BNM/UnityStructures.hpp"
#include "BNM/Field.hpp"

std::string TEFModLoader::IL2CPP_String::str() const {
    return reinterpret_cast<BNM::Structures::Mono::String*>(this->_ptr)->str();
}

bool TEFModLoader::IL2CPP_String::empty() const {
    return reinterpret_cast<BNM::Structures::Mono::String*>(this->_ptr)->Empty();
}

size_t TEFModLoader::IL2CPP_String::length() const {
    return reinterpret_cast<BNM::Structures::Mono::String*>(this->_ptr)->length;
}

bool TEFModLoader::IL2CPP_String::null_or_empty() const {
    return reinterpret_cast<BNM::Structures::Mono::String*>(this->_ptr)->IsNullOrEmpty();
}

void *TEFModLoader::IL2CPP_String::Create(const std::string &str) {
    return BNM::CreateMonoString(str);
}

template<typename T>
TEFModLoader::IL2CPP_Array<T>::IL2CPP_Array(void *ptr):  _ptr(ptr) {
    _data = IL2CppArray<T>(ptr);
}

template<typename T>
TEFMod::Array<T>* TEFModLoader::IL2CPP_Array<T>::CreateFromVector(std::vector <T> &vec) {
    auto *result = new IL2CPP_Array<T>();
    result->_data = IL2CppArray<T>::CreateFromVector(vec);
    result->_ptr = &result->_data;

    return result;
}

template<typename T>
TEFMod::Array<T>* TEFModLoader::IL2CPP_Array<T>::CreateFromPointer(T *ptr, size_t count) {
    auto *result = new IL2CPP_Array<T>();
    result->_data = IL2CppArray<T>::CreateFromPointer(ptr, count);
    result->_ptr = &result->_data;

    return result;
}


template<typename T>
TEFMod::Array<T>* TEFModLoader::IL2CPP_Array<T>::ParseFromPointer(void *ptr) {
    return new IL2CPP_Array<T>(ptr);
}

template<typename T>
std::vector<T> TEFModLoader::IL2CPP_Array<T>::to_vector() { return this->_data.ToVector(); }

template<typename T>
T &TEFModLoader::IL2CPP_Array<T>::at(std::size_t index) {
    return this->_data.At(index);
}

template<typename T>
std::size_t TEFModLoader::IL2CPP_Array<T>::find(const T &value) {
    auto it = std::find(this->begin(), this->end(), value);
    if (it == end()) {
        return -1;
    } else {
        return static_cast<int>(std::distance(this->begin(), it));
    }
}

template<typename T>
bool TEFModLoader::IL2CPP_Array<T>::contains(const T &value) { return std::find(begin(), end(), value) != end(); }

template<typename T>
void TEFModLoader::IL2CPP_Array<T>::set(size_t index, const T &value) {
    this->_data.Set(index, value);
}

template<typename T>
std::size_t TEFModLoader::IL2CPP_Array<T>::Size() {
    return this->_data.Size();
}

template<typename T>
bool TEFModLoader::IL2CPP_Array<T>::empty() {
    return this->_data.size == 0;
}

template<typename T>
T &TEFModLoader::IL2CPP_Array<T>::front() {
    return this->_data.Front();
}

template<typename T>
T &TEFModLoader::IL2CPP_Array<T>::back() {
    return this->_data.Back();
}

template<typename T>
T *TEFModLoader::IL2CPP_Array<T>::data() {
    return this->_data.Data();
}

template<typename T>
T *TEFModLoader::IL2CPP_Array<T>::begin() {
    return this->_data.begin();
}

template<typename T>
T *TEFModLoader::IL2CPP_Array<T>::end() {
    return this->_data.end();
}

template<typename T>
void TEFModLoader::IL2CPP_Array<T>::assign(const std::vector<T>& vec) {
    this->_data.Assign(vec);
}

template<typename T>
void TEFModLoader::IL2CPP_Array<T>::fill(const T &value) {
    _data.Fill(value);
}

template<typename T>
void TEFModLoader::IL2CPP_Array<T>::clear() {
    _data.Clear();
    _ptr = nullptr;
}

template<typename T>
TEFMod::Field<T> *TEFModLoader::IL2CPP_Field<T>::ParseFromPointer(void *ptr) {
    return new TEFModLoader::IL2CPP_Field<T>(ptr);
}

template<typename T>
void TEFModLoader::IL2CPP_Field<T>::SetInstance(TEFMod::TerrariaInstance instance) {
    static_cast<BNM::Field<T>*>(_ptr)->SetInstance(static_cast<BNM::IL2CPP::Il2CppObject *>(instance));
}

template<typename T>
void* TEFModLoader::IL2CPP_Field<T>::GetOffset(TEFMod::TerrariaInstance instance) {
    auto p = static_cast<BNM::Field<T>*>(_ptr);
    if (instance) {
        p->SetInstance(static_cast<BNM::IL2CPP::Il2CppObject *>(instance));
    }
    return p->GetPointer();
}

template<typename T>
T TEFModLoader::IL2CPP_Field<T>::Get(TEFMod::TerrariaInstance instance) {
    auto p = static_cast<BNM::Field<T>*>(_ptr);
    if (instance) {
        p->SetInstance(static_cast<BNM::IL2CPP::Il2CppObject *>(instance));
    }
    return p->Get();
}

template<typename T>
bool TEFModLoader::IL2CPP_Field<T>::Alive() {
    return static_cast<BNM::Field<T>*>(_ptr)->GetInfo() != nullptr;
}

template<typename T>
void TEFModLoader::IL2CPP_Field<T>::Set(T value, TEFMod::TerrariaInstance instance) {
    auto p = static_cast<BNM::Field<T>*>(_ptr);
    if (instance) {
        p->SetInstance(static_cast<BNM::IL2CPP::Il2CppObject *>(instance));
    }
    p->Set(value);
}

template<typename R>
TEFMod::Method<R> *TEFModLoader::IL2CPP_Method<R>::ParseFromPointer(void *ptr) {
    return new TEFModLoader::IL2CPP_Method<R>(ptr);
}

template<typename T>
void TEFModLoader::IL2CPP_Method<T>::SetInstance(TEFMod::TerrariaInstance instance) {
    static_cast<BNM::Method<T>*>(_ptr)->SetInstance(static_cast<BNM::IL2CPP::Il2CppObject *>(instance));
}

template<typename T>
bool TEFModLoader::IL2CPP_Method<T>::Alive() {
    return static_cast<BNM::Method<T>*>(_ptr)->_data != nullptr;
}

template<typename T>
void* TEFModLoader::IL2CPP_Method<T>::GetOffset() {
    return static_cast<BNM::Method<T>*>(_ptr);
}

template<typename R>
R TEFModLoader::IL2CPP_Method<R>::Call(TEFMod::TerrariaInstance instance, int expectedArgCount, ...)
{
    // 参数校验
    if (expectedArgCount < 0 || expectedArgCount > 30) {
        throw std::invalid_argument("参数数量必须为0-30");
    }

    auto method = static_cast<BNM::Method<R>*>(_ptr);
    if (instance) {
        method->SetInstance(static_cast<BNM::IL2CPP::Il2CppObject*>(instance));
    }

    va_list args;
    va_start(args, expectedArgCount);

    try {
        // 统一收集参数指针
        void* argPtrs[30];
        for (int i = 0; i < expectedArgCount; ++i) {
            argPtrs[i] = va_arg(args, void*);
        }
        va_end(args);

        // 根据参数数量分发调用
        switch (expectedArgCount) {
            case 0: if constexpr (!std::is_same_v<R, void>) { return method->Call(); } else { method->Call(); } break;
            case 1: if constexpr (!std::is_same_v<R, void>) { return method->Call(argPtrs[0]); } else { method->Call(argPtrs[0]); } break;
            case 2: if constexpr (!std::is_same_v<R, void>) { return method->Call(argPtrs[0], argPtrs[1]); } else { method->Call(argPtrs[0], argPtrs[1]); } break;
            case 3: if constexpr (!std::is_same_v<R, void>) { return method->Call(argPtrs[0], argPtrs[1], argPtrs[2]); } else { method->Call(argPtrs[0], argPtrs[1], argPtrs[2]); } break;
            case 4: if constexpr (!std::is_same_v<R, void>) { return method->Call(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3]); } else { method->Call(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3]); } break;
            case 5: if constexpr (!std::is_same_v<R, void>) { return method->Call(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4]); } else { method->Call(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4]); } break;
            case 6: if constexpr (!std::is_same_v<R, void>) { return method->Call(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5]); } else { method->Call(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5]); } break;
            case 7: if constexpr (!std::is_same_v<R, void>) { return method->Call(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6]); } else { method->Call(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6]); } break;
            case 8: if constexpr (!std::is_same_v<R, void>) { return method->Call(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7]); } else { method->Call(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7]); } break;
            case 9: if constexpr (!std::is_same_v<R, void>) { return method->Call(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8]); } else { method->Call(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8]); } break;
            case 10: if constexpr (!std::is_same_v<R, void>) { return method->Call(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8], argPtrs[9]); } else { method->Call(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8], argPtrs[9]); } break;
            case 11: if constexpr (!std::is_same_v<R, void>) { return method->Call(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8], argPtrs[9], argPtrs[10]); } else { method->Call(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8], argPtrs[9], argPtrs[10]); } break;
            case 12: if constexpr (!std::is_same_v<R, void>) { return method->Call(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8], argPtrs[9], argPtrs[10], argPtrs[11]); } else { method->Call(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8], argPtrs[9], argPtrs[10], argPtrs[11]); } break;
            case 13: if constexpr (!std::is_same_v<R, void>) { return method->Call(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8], argPtrs[9], argPtrs[10], argPtrs[11], argPtrs[12]); } else { method->Call(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8], argPtrs[9], argPtrs[10], argPtrs[11], argPtrs[12]); } break;
            case 14: if constexpr (!std::is_same_v<R, void>) { return method->Call(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8], argPtrs[9], argPtrs[10], argPtrs[11], argPtrs[12], argPtrs[13]); } else { method->Call(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8], argPtrs[9], argPtrs[10], argPtrs[11], argPtrs[12], argPtrs[13]); } break;
            case 15: if constexpr (!std::is_same_v<R, void>) { return method->Call(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8], argPtrs[9], argPtrs[10], argPtrs[11], argPtrs[12], argPtrs[13], argPtrs[14]); } else { method->Call(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8], argPtrs[9], argPtrs[10], argPtrs[11], argPtrs[12], argPtrs[13], argPtrs[14]); } break;
            case 16: if constexpr (!std::is_same_v<R, void>) { return method->Call(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8], argPtrs[9], argPtrs[10], argPtrs[11], argPtrs[12], argPtrs[13], argPtrs[14], argPtrs[15]); } else { method->Call(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8], argPtrs[9], argPtrs[10], argPtrs[11], argPtrs[12], argPtrs[13], argPtrs[14], argPtrs[15]); } break;
            case 17: if constexpr (!std::is_same_v<R, void>) { return method->Call(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8], argPtrs[9], argPtrs[10], argPtrs[11], argPtrs[12], argPtrs[13], argPtrs[14], argPtrs[15], argPtrs[16]); } else { method->Call(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8], argPtrs[9], argPtrs[10], argPtrs[11], argPtrs[12], argPtrs[13], argPtrs[14], argPtrs[15], argPtrs[16]); } break;
            case 18: if constexpr (!std::is_same_v<R, void>) { return method->Call(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8], argPtrs[9], argPtrs[10], argPtrs[11], argPtrs[12], argPtrs[13], argPtrs[14], argPtrs[15], argPtrs[16], argPtrs[17]); } else { method->Call(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8], argPtrs[9], argPtrs[10], argPtrs[11], argPtrs[12], argPtrs[13], argPtrs[14], argPtrs[15], argPtrs[16], argPtrs[17]); } break;
            case 19: if constexpr (!std::is_same_v<R, void>) { return method->Call(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8], argPtrs[9], argPtrs[10], argPtrs[11], argPtrs[12], argPtrs[13], argPtrs[14], argPtrs[15], argPtrs[16], argPtrs[17], argPtrs[18]); } else { method->Call(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8], argPtrs[9], argPtrs[10], argPtrs[11], argPtrs[12], argPtrs[13], argPtrs[14], argPtrs[15], argPtrs[16], argPtrs[17], argPtrs[18]); } break;
            case 20: if constexpr (!std::is_same_v<R, void>) { return method->Call(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8], argPtrs[9], argPtrs[10], argPtrs[11], argPtrs[12], argPtrs[13], argPtrs[14], argPtrs[15], argPtrs[16], argPtrs[17], argPtrs[18], argPtrs[19]); } else { method->Call(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8], argPtrs[9], argPtrs[10], argPtrs[11], argPtrs[12], argPtrs[13], argPtrs[14], argPtrs[15], argPtrs[16], argPtrs[17], argPtrs[18], argPtrs[19]); } break;
            case 21: if constexpr (!std::is_same_v<R, void>) { return method->Call(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8], argPtrs[9], argPtrs[10], argPtrs[11], argPtrs[12], argPtrs[13], argPtrs[14], argPtrs[15], argPtrs[16], argPtrs[17], argPtrs[18], argPtrs[19], argPtrs[20]); } else { method->Call(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8], argPtrs[9], argPtrs[10], argPtrs[11], argPtrs[12], argPtrs[13], argPtrs[14], argPtrs[15], argPtrs[16], argPtrs[17], argPtrs[18], argPtrs[19], argPtrs[20]); } break;
            case 22: if constexpr (!std::is_same_v<R, void>) { return method->Call(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8], argPtrs[9], argPtrs[10], argPtrs[11], argPtrs[12], argPtrs[13], argPtrs[14], argPtrs[15], argPtrs[16], argPtrs[17], argPtrs[18], argPtrs[19], argPtrs[20], argPtrs[21]); } else { method->Call(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8], argPtrs[9], argPtrs[10], argPtrs[11], argPtrs[12], argPtrs[13], argPtrs[14], argPtrs[15], argPtrs[16], argPtrs[17], argPtrs[18], argPtrs[19], argPtrs[20], argPtrs[21]); } break;
            case 23: if constexpr (!std::is_same_v<R, void>) { return method->Call(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8], argPtrs[9], argPtrs[10], argPtrs[11], argPtrs[12], argPtrs[13], argPtrs[14], argPtrs[15], argPtrs[16], argPtrs[17], argPtrs[18], argPtrs[19], argPtrs[20], argPtrs[21], argPtrs[22]); } else { method->Call(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8], argPtrs[9], argPtrs[10], argPtrs[11], argPtrs[12], argPtrs[13], argPtrs[14], argPtrs[15], argPtrs[16], argPtrs[17], argPtrs[18], argPtrs[19], argPtrs[20], argPtrs[21], argPtrs[22]); } break;
            case 24: if constexpr (!std::is_same_v<R, void>) { return method->Call(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8], argPtrs[9], argPtrs[10], argPtrs[11], argPtrs[12], argPtrs[13], argPtrs[14], argPtrs[15], argPtrs[16], argPtrs[17], argPtrs[18], argPtrs[19], argPtrs[20], argPtrs[21], argPtrs[22], argPtrs[23]); } else { method->Call(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8], argPtrs[9], argPtrs[10], argPtrs[11], argPtrs[12], argPtrs[13], argPtrs[14], argPtrs[15], argPtrs[16], argPtrs[17], argPtrs[18], argPtrs[19], argPtrs[20], argPtrs[21], argPtrs[22], argPtrs[23]); } break;
            case 25: if constexpr (!std::is_same_v<R, void>) { return method->Call(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8], argPtrs[9], argPtrs[10], argPtrs[11], argPtrs[12], argPtrs[13], argPtrs[14], argPtrs[15], argPtrs[16], argPtrs[17], argPtrs[18], argPtrs[19], argPtrs[20], argPtrs[21], argPtrs[22], argPtrs[23], argPtrs[24]); } else { method->Call(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8], argPtrs[9], argPtrs[10], argPtrs[11], argPtrs[12], argPtrs[13], argPtrs[14], argPtrs[15], argPtrs[16], argPtrs[17], argPtrs[18], argPtrs[19], argPtrs[20], argPtrs[21], argPtrs[22], argPtrs[23], argPtrs[24]); } break;
            case 26: if constexpr (!std::is_same_v<R, void>) { return method->Call(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8], argPtrs[9], argPtrs[10], argPtrs[11], argPtrs[12], argPtrs[13], argPtrs[14], argPtrs[15], argPtrs[16], argPtrs[17], argPtrs[18], argPtrs[19], argPtrs[20], argPtrs[21], argPtrs[22], argPtrs[23], argPtrs[24], argPtrs[25]); } else { method->Call(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8], argPtrs[9], argPtrs[10], argPtrs[11], argPtrs[12], argPtrs[13], argPtrs[14], argPtrs[15], argPtrs[16], argPtrs[17], argPtrs[18], argPtrs[19], argPtrs[20], argPtrs[21], argPtrs[22], argPtrs[23], argPtrs[24], argPtrs[25]); } break;
            case 27: if constexpr (!std::is_same_v<R, void>) { return method->Call(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8], argPtrs[9], argPtrs[10], argPtrs[11], argPtrs[12], argPtrs[13], argPtrs[14], argPtrs[15], argPtrs[16], argPtrs[17], argPtrs[18], argPtrs[19], argPtrs[20], argPtrs[21], argPtrs[22], argPtrs[23], argPtrs[24], argPtrs[25], argPtrs[26]); } else { method->Call(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8], argPtrs[9], argPtrs[10], argPtrs[11], argPtrs[12], argPtrs[13], argPtrs[14], argPtrs[15], argPtrs[16], argPtrs[17], argPtrs[18], argPtrs[19], argPtrs[20], argPtrs[21], argPtrs[22], argPtrs[23], argPtrs[24], argPtrs[25], argPtrs[26]); } break;
            case 28: if constexpr (!std::is_same_v<R, void>) { return method->Call(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8], argPtrs[9], argPtrs[10], argPtrs[11], argPtrs[12], argPtrs[13], argPtrs[14], argPtrs[15], argPtrs[16], argPtrs[17], argPtrs[18], argPtrs[19], argPtrs[20], argPtrs[21], argPtrs[22], argPtrs[23], argPtrs[24], argPtrs[25], argPtrs[26], argPtrs[27]); } else { method->Call(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8], argPtrs[9], argPtrs[10], argPtrs[11], argPtrs[12], argPtrs[13], argPtrs[14], argPtrs[15], argPtrs[16], argPtrs[17], argPtrs[18], argPtrs[19], argPtrs[20], argPtrs[21], argPtrs[22], argPtrs[23], argPtrs[24], argPtrs[25], argPtrs[26], argPtrs[27]); } break;
            case 29: if constexpr (!std::is_same_v<R, void>) { return method->Call(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8], argPtrs[9], argPtrs[10], argPtrs[11], argPtrs[12], argPtrs[13], argPtrs[14], argPtrs[15], argPtrs[16], argPtrs[17], argPtrs[18], argPtrs[19], argPtrs[20], argPtrs[21], argPtrs[22], argPtrs[23], argPtrs[24], argPtrs[25], argPtrs[26], argPtrs[27], argPtrs[28]); } else { method->Call(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8], argPtrs[9], argPtrs[10], argPtrs[11], argPtrs[12], argPtrs[13], argPtrs[14], argPtrs[15], argPtrs[16], argPtrs[17], argPtrs[18], argPtrs[19], argPtrs[20], argPtrs[21], argPtrs[22], argPtrs[23], argPtrs[24], argPtrs[25], argPtrs[26], argPtrs[27], argPtrs[28]); } break;
            case 30: if constexpr (!std::is_same_v<R, void>) { return method->Call(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8], argPtrs[9], argPtrs[10], argPtrs[11], argPtrs[12], argPtrs[13], argPtrs[14], argPtrs[15], argPtrs[16], argPtrs[17], argPtrs[18], argPtrs[19], argPtrs[20], argPtrs[21], argPtrs[22], argPtrs[23], argPtrs[24], argPtrs[25], argPtrs[26], argPtrs[27], argPtrs[28], argPtrs[29]); } else { method->Call(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8], argPtrs[9], argPtrs[10], argPtrs[11], argPtrs[12], argPtrs[13], argPtrs[14], argPtrs[15], argPtrs[16], argPtrs[17], argPtrs[18], argPtrs[19], argPtrs[20], argPtrs[21], argPtrs[22], argPtrs[23], argPtrs[24], argPtrs[25], argPtrs[26], argPtrs[27], argPtrs[28], argPtrs[29]); } break;
            default: throw std::logic_error("参数数量异常");
        }
    } catch (const std::exception& e) {
        va_end(args);
        throw std::runtime_error(std::string("方法调用失败: ") + e.what());
    }

    // 对于void特化，不需要返回值
    if constexpr (!std::is_same_v<R, void>) {
        return R{}; // 默认返回值，实际不会执行到这里
    }
}


TEFMod::Class *TEFModLoader::IL2CPP_Class::ParseFromPointer(void *ptr) {
    return new IL2CPP_Class(ptr);
}

bool TEFModLoader::IL2CPP_Class::Alive() {
    return static_cast<BNM::Class*>(_ptr)->Alive();
}

TEFMod::TerrariaInstance
TEFModLoader::IL2CPP_Class::CreateNewObjectParameters(int expectedArgCount, ...)
{
    if (expectedArgCount < 0 || expectedArgCount > 30) {
        throw std::invalid_argument("参数数量必须为0-30");
    }

    auto pClass = static_cast<BNM::Class*>(_ptr);
    va_list args;
    va_start(args, expectedArgCount);

    try {
        void* argPtrs[30];
        for (int i = 0; i < expectedArgCount; ++i) {
            argPtrs[i] = va_arg(args, void*);
        }
        va_end(args);

        switch (expectedArgCount) {
            case 0:  return pClass->CreateNewInstance();
            case 1:  return pClass->CreateNewObjectParameters(argPtrs[0]);
            case 2:  return pClass->CreateNewObjectParameters(argPtrs[0], argPtrs[1]);
            case 3:  return pClass->CreateNewObjectParameters(argPtrs[0], argPtrs[1], argPtrs[2]);
            case 4:  return pClass->CreateNewObjectParameters(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3]);
            case 5:  return pClass->CreateNewObjectParameters(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4]);
            case 6:  return pClass->CreateNewObjectParameters(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5]);
            case 7:  return pClass->CreateNewObjectParameters(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6]);
            case 8:  return pClass->CreateNewObjectParameters(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7]);
            case 9:  return pClass->CreateNewObjectParameters(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8]);
            case 10:  return pClass->CreateNewObjectParameters(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8], argPtrs[9]);
            case 11:  return pClass->CreateNewObjectParameters(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8], argPtrs[9], argPtrs[10]);
            case 12:  return pClass->CreateNewObjectParameters(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8], argPtrs[9], argPtrs[10], argPtrs[11]);
            case 13:  return pClass->CreateNewObjectParameters(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8], argPtrs[9], argPtrs[10], argPtrs[11], argPtrs[12]);
            case 14:  return pClass->CreateNewObjectParameters(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8], argPtrs[9], argPtrs[10], argPtrs[11], argPtrs[12], argPtrs[13]);
            case 15:  return pClass->CreateNewObjectParameters(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8], argPtrs[9], argPtrs[10], argPtrs[11], argPtrs[12], argPtrs[13], argPtrs[14]);
            case 16:  return pClass->CreateNewObjectParameters(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8], argPtrs[9], argPtrs[10], argPtrs[11], argPtrs[12], argPtrs[13], argPtrs[14], argPtrs[15]);
            case 17:  return pClass->CreateNewObjectParameters(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8], argPtrs[9], argPtrs[10], argPtrs[11], argPtrs[12], argPtrs[13], argPtrs[14], argPtrs[15], argPtrs[16]);
            case 18:  return pClass->CreateNewObjectParameters(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8], argPtrs[9], argPtrs[10], argPtrs[11], argPtrs[12], argPtrs[13], argPtrs[14], argPtrs[15], argPtrs[16], argPtrs[17]);
            case 19:  return pClass->CreateNewObjectParameters(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8], argPtrs[9], argPtrs[10], argPtrs[11], argPtrs[12], argPtrs[13], argPtrs[14], argPtrs[15], argPtrs[16], argPtrs[17], argPtrs[18]);
            case 20:  return pClass->CreateNewObjectParameters(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8], argPtrs[9], argPtrs[10], argPtrs[11], argPtrs[12], argPtrs[13], argPtrs[14], argPtrs[15], argPtrs[16], argPtrs[17], argPtrs[18], argPtrs[19]);
            case 21:  return pClass->CreateNewObjectParameters(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8], argPtrs[9], argPtrs[10], argPtrs[11], argPtrs[12], argPtrs[13], argPtrs[14], argPtrs[15], argPtrs[16], argPtrs[17], argPtrs[18], argPtrs[19], argPtrs[20]);
            case 22:  return pClass->CreateNewObjectParameters(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8], argPtrs[9], argPtrs[10], argPtrs[11], argPtrs[12], argPtrs[13], argPtrs[14], argPtrs[15], argPtrs[16], argPtrs[17], argPtrs[18], argPtrs[19], argPtrs[20], argPtrs[21]);
            case 23:  return pClass->CreateNewObjectParameters(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8], argPtrs[9], argPtrs[10], argPtrs[11], argPtrs[12], argPtrs[13], argPtrs[14], argPtrs[15], argPtrs[16], argPtrs[17], argPtrs[18], argPtrs[19], argPtrs[20], argPtrs[21], argPtrs[22]);
            case 24:  return pClass->CreateNewObjectParameters(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8], argPtrs[9], argPtrs[10], argPtrs[11], argPtrs[12], argPtrs[13], argPtrs[14], argPtrs[15], argPtrs[16], argPtrs[17], argPtrs[18], argPtrs[19], argPtrs[20], argPtrs[21], argPtrs[22], argPtrs[23]);
            case 25:  return pClass->CreateNewObjectParameters(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8], argPtrs[9], argPtrs[10], argPtrs[11], argPtrs[12], argPtrs[13], argPtrs[14], argPtrs[15], argPtrs[16], argPtrs[17], argPtrs[18], argPtrs[19], argPtrs[20], argPtrs[21], argPtrs[22], argPtrs[23], argPtrs[24]);
            case 26:  return pClass->CreateNewObjectParameters(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8], argPtrs[9], argPtrs[10], argPtrs[11], argPtrs[12], argPtrs[13], argPtrs[14], argPtrs[15], argPtrs[16], argPtrs[17], argPtrs[18], argPtrs[19], argPtrs[20], argPtrs[21], argPtrs[22], argPtrs[23], argPtrs[24], argPtrs[25]);
            case 27:  return pClass->CreateNewObjectParameters(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8], argPtrs[9], argPtrs[10], argPtrs[11], argPtrs[12], argPtrs[13], argPtrs[14], argPtrs[15], argPtrs[16], argPtrs[17], argPtrs[18], argPtrs[19], argPtrs[20], argPtrs[21], argPtrs[22], argPtrs[23], argPtrs[24], argPtrs[25], argPtrs[26]);
            case 28:  return pClass->CreateNewObjectParameters(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8], argPtrs[9], argPtrs[10], argPtrs[11], argPtrs[12], argPtrs[13], argPtrs[14], argPtrs[15], argPtrs[16], argPtrs[17], argPtrs[18], argPtrs[19], argPtrs[20], argPtrs[21], argPtrs[22], argPtrs[23], argPtrs[24], argPtrs[25], argPtrs[26], argPtrs[27]);
            case 29:  return pClass->CreateNewObjectParameters(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8], argPtrs[9], argPtrs[10], argPtrs[11], argPtrs[12], argPtrs[13], argPtrs[14], argPtrs[15], argPtrs[16], argPtrs[17], argPtrs[18], argPtrs[19], argPtrs[20], argPtrs[21], argPtrs[22], argPtrs[23], argPtrs[24], argPtrs[25], argPtrs[26], argPtrs[27], argPtrs[28]);
            case 30:  return pClass->CreateNewObjectParameters(argPtrs[0], argPtrs[1], argPtrs[2], argPtrs[3], argPtrs[4], argPtrs[5], argPtrs[6], argPtrs[7], argPtrs[8], argPtrs[9], argPtrs[10], argPtrs[11], argPtrs[12], argPtrs[13], argPtrs[14], argPtrs[15], argPtrs[16], argPtrs[17], argPtrs[18], argPtrs[19], argPtrs[20], argPtrs[21], argPtrs[22], argPtrs[23], argPtrs[24], argPtrs[25], argPtrs[26], argPtrs[27], argPtrs[28], argPtrs[29]);
            default: throw std::logic_error("参数数量异常");
        }
    } catch (...) {
        va_end(args);
        throw;
    }
}


template class TEFModLoader::IL2CPP_Array<bool>;
template class TEFModLoader::IL2CPP_Array<int8_t>;
template class TEFModLoader::IL2CPP_Array<uint8_t>;
template class TEFModLoader::IL2CPP_Array<int16_t>;
template class TEFModLoader::IL2CPP_Array<uint16_t>;
template class TEFModLoader::IL2CPP_Array<int>;
template class TEFModLoader::IL2CPP_Array<uint>;
template class TEFModLoader::IL2CPP_Array<long>;
template class TEFModLoader::IL2CPP_Array<u_long>;
template class TEFModLoader::IL2CPP_Array<float>;
template class TEFModLoader::IL2CPP_Array<double>;
template class TEFModLoader::IL2CPP_Array<char>;
template class TEFModLoader::IL2CPP_Array<void*>;

template class TEFModLoader::IL2CPP_Field<bool>;
template class TEFModLoader::IL2CPP_Field<int8_t>;
template class TEFModLoader::IL2CPP_Field<uint8_t>;
template class TEFModLoader::IL2CPP_Field<int16_t>;
template class TEFModLoader::IL2CPP_Field<uint16_t>;
template class TEFModLoader::IL2CPP_Field<int>;
template class TEFModLoader::IL2CPP_Field<uint>;
template class TEFModLoader::IL2CPP_Field<long>;
template class TEFModLoader::IL2CPP_Field<u_long>;
template class TEFModLoader::IL2CPP_Field<float>;
template class TEFModLoader::IL2CPP_Field<double>;
template class TEFModLoader::IL2CPP_Field<char>;
template class TEFModLoader::IL2CPP_Field<void*>;

template class TEFModLoader::IL2CPP_Method<bool>;
template class TEFModLoader::IL2CPP_Method<int8_t>;
template class TEFModLoader::IL2CPP_Method<uint8_t>;
template class TEFModLoader::IL2CPP_Method<int16_t>;
template class TEFModLoader::IL2CPP_Method<uint16_t>;
template class TEFModLoader::IL2CPP_Method<int>;
template class TEFModLoader::IL2CPP_Method<uint>;
template class TEFModLoader::IL2CPP_Method<long>;
template class TEFModLoader::IL2CPP_Method<u_long>;
template class TEFModLoader::IL2CPP_Method<float>;
template class TEFModLoader::IL2CPP_Method<double>;
template class TEFModLoader::IL2CPP_Method<char>;
template class TEFModLoader::IL2CPP_Method<void*>;
template class TEFModLoader::IL2CPP_Method<void>;