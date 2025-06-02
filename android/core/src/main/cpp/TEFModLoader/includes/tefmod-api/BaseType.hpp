/*******************************************************************************
 * 文件名称: BaseType
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

#include <any>
#include <cstdint>
#include <string>
#include <vector>

namespace TEFMod {

    typedef void* TerrariaInstance;

    template <typename T>
    constexpr bool IsAllowedType() {
        return std::is_same_v<T, bool> ||
               std::is_same_v<T, int8_t> ||
               std::is_same_v<T, uint8_t> ||
               std::is_same_v<T, int16_t> ||
               std::is_same_v<T, uint16_t> ||
               std::is_same_v<T, int> ||
               std::is_same_v<T, uint> ||
               std::is_same_v<T, long> ||
               std::is_same_v<T, u_long> ||
               std::is_same_v<T, float> ||
               std::is_same_v<T, double> ||
               std::is_same_v<T, char> ||
               std::is_same_v<T, void*> ||
               std::is_same_v<T, void>;
    }

    class String {
    public:
        virtual ~String() = default;

        virtual size_t length() const = 0;           // 获取字符串长度（不含'\0'）
        virtual bool empty() const = 0;              // 判断是否为空
        virtual std::string str() const = 0;         // 转换为std::string
        __attribute__((unused)) virtual bool null_or_empty() const = 0;

        virtual char operator[](size_t index) const = 0; // 下标访问（只读）
    };

    template<typename T>
    class Array {
        static_assert(
            IsAllowedType<T>(),
            "Array<T>: T must be bool, int8_t, uint8_t, int16_t, int, uint, long, u_long, float, double, char, or void*"
        );

    public:
        virtual ~Array() = default;

        virtual std::vector<T> to_vector() = 0;
        virtual T& at(std::size_t index) = 0;
        virtual std::size_t find(const T& value) = 0;
        virtual bool contains(const T& value) = 0;
        virtual void set(size_t index, const T& value) = 0;
        virtual std::size_t Size() = 0;
        virtual bool empty() = 0;
        virtual T& front() = 0;
        virtual T& back() = 0;
        virtual T* data() = 0;
        virtual T* begin() = 0;
        virtual T* end() = 0;
        virtual void assign(const std::vector<T>& vec) = 0;
        virtual void fill(const T& value) = 0;
        virtual void clear() = 0;
    };

    template<typename T>
    class Field {
        static_assert(
            IsAllowedType<T>(),
            "Field<T>: T must be bool, int8_t, uint8_t, int16_t, int, uint, long, u_long, float, double, char, or void*"
        );

    public:
        virtual ~Field() = default;

        virtual void SetInstance(TerrariaInstance instance) = 0;
        virtual void* GetOffset(TerrariaInstance instance = nullptr) = 0;
        virtual T Get(TerrariaInstance instance = nullptr) = 0;
        virtual bool Alive() = 0;
        virtual void Set(T value, TerrariaInstance instance = nullptr) = 0;
    };

    template<typename R>
    class Method {
        static_assert(
            IsAllowedType<R>(),
            "Method<R>: T must be bool, int8_t, uint8_t, int16_t, int, uint, long, u_long, float, double, char, or void*"
        );

    public:
        virtual ~Method() = default;

        virtual void SetInstance(TerrariaInstance instance) = 0;
        virtual void* GetOffset() = 0;

        virtual bool Alive() = 0;

        virtual R Call(TerrariaInstance instance, int expectedArgCount, ...) = 0;
    };

    class Class {
    public:
        virtual ~Class() = default;
        virtual TerrariaInstance CreateNewObjectParameters(int expectedArgCount, ...) = 0;
        virtual bool Alive() = 0;
    };
}