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

#pragma once

#include "base_type_api.hpp"

#include "IL2CppArray.hpp"

namespace TEFModLoader {

    TEFMod::identifier parse_identifier_form_str(const std::string& input);

    class IL2CPP_String: public TEFMod::String {
        void* _ptr;
    public:

        explicit IL2CPP_String(void* ptr): _ptr(ptr) {}

        [[nodiscard]] std::size_t length() const override;
        [[nodiscard]] bool empty() const override;
        [[nodiscard]] std::string str() const override;
        [[nodiscard]] bool null_or_empty() const override;

        virtual char operator[](size_t index) const override;

        static void* Create(const std::string& str);
        static TEFMod::String* ParseFromPointer(void* ptr);
    };


    template<typename T>
    class IL2CPP_Array: public TEFMod::Array<T> {
    public:
        void* _ptr;
        IL2CppArray<T> _data;

        IL2CPP_Array() = default;
        explicit IL2CPP_Array(void* ptr);

        static TEFMod::Array<T>* CreateFromVector(std::vector<T>& vec);

        static TEFMod::Array<T>* CreateFromPointer(T* ptr, size_t count);

        static TEFMod::Array<T>* ParseFromPointer(void* ptr);

        std::vector<T> to_vector() override;
        T& at(std::size_t index) override;
        std::size_t find(const T& value) override;
        bool contains(const T& value) override;
        void set(size_t index, const T& value) override;
        std::size_t Size() override;
        bool empty() override;
        T& front() override;
        T& back() override;
        T* data() override;
        T* begin() override;
        T* end() override;
        void assign(const std::vector<T>& vec) override;
        void fill(const T& value) override;
        void clear() override;
    };

    template<typename T>
    class IL2CPP_Field: public TEFMod::Field<T> {
    private:
        void* _ptr;

    public:
        explicit IL2CPP_Field(void* ptr): _ptr(ptr) {}

        static TEFMod::Field<T>* ParseFromPointer(void* ptr);

        void SetInstance(TEFMod::TerrariaInstance instance) override;
        void* GetOffset(TEFMod::TerrariaInstance instance = nullptr) override;
        T Get(TEFMod::TerrariaInstance instance = nullptr) override;
        bool Alive() override;
        void Set(T value, TEFMod::TerrariaInstance instance = nullptr) override;
    };

    template<typename R>
    class IL2CPP_Method: public TEFMod::Method<R> {
        void* _ptr;
    public:
        explicit IL2CPP_Method(void* ptr): _ptr(ptr) {}

        static TEFMod::Method<R>* ParseFromPointer(void* ptr);

        void SetInstance(TEFMod::TerrariaInstance instance) override;
        void* GetOffset() override;

        bool Alive() override;

        R Call(TEFMod::TerrariaInstance instance, int expectedArgCount, ...) override;
    };

    class IL2CPP_Class: public TEFMod::Class {
    private:
        void* _ptr;
    public:
        explicit IL2CPP_Class(void* ptr): _ptr(ptr) {}

        static TEFMod::Class* ParseFromPointer(void* ptr);

        TEFMod::TerrariaInstance CreateNewObjectParameters(int expectedArgCount, ...) override;
        bool Alive() override;
    };

}