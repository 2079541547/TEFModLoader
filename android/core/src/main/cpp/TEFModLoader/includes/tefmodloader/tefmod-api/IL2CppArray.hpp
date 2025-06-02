//
// Created by EternalFuture゙ on 2025/1/25.
//

#pragma once

#include <iostream>
#include <stdexcept>
#include <cstring>
#include <vector>
#include <algorithm>

template <typename T>
struct IL2CppArray {
    void* obj;      // 占位符，用于解析 (Placeholder, used for parsing)
    void* bounds;   // 占位符，用于解析 (Placeholder, used for parsing)
    void* unknown;  // 占位符，用于解析 (Placeholder, used for parsing)
    size_t size;    // Array size
    T* m_Items;     // 使用裸指针指向元素 (Pointer to elements)

    IL2CppArray() = default;

    // Constructor, which initializes the struct from the given void* pointer
    IL2CppArray(void* ptr) {
        memcpy(this, ptr, sizeof(*this) - sizeof(T*)); // Do not copy m_Items
        const size_t nonElementsSize = sizeof(*this) - sizeof(T*);
        m_Items = reinterpret_cast<T*>(reinterpret_cast<char*>(ptr) + nonElementsSize);
    }

    // Static factory function: Create a new IL2CppArray instance from std::vector
    static IL2CppArray<T> CreateFromVector(const std::vector<T>& vec) {
        // Allocate enough memory to store the entire struct and all elements
        size_t totalSize = sizeof(IL2CppArray<T>) - sizeof(T*) + vec.size() * sizeof(T);
        void* buffer = malloc(totalSize);
        if (!buffer) {
            throw std::bad_alloc();
        }

        // Initialize the struct header information
        auto* result = new (buffer) IL2CppArray<T>;
        result->size = vec.size();
        result->m_Items = reinterpret_cast<T*>(reinterpret_cast<char*>(buffer) + sizeof(IL2CppArray<T>) - sizeof(T*));

        // Copy the data
        if constexpr (std::is_same_v<T, bool>) {
            for (size_t i = 0; i < vec.size(); ++i) {
                result->m_Items[i] = vec[i];
            }
        } else {
            std::memcpy(result->m_Items, vec.data(), vec.size() * sizeof(T));
        }

        return *result;
    }

    // Static Factory Function: Create a new IL2CppArray instance from a C-style array
    template <size_t N>
    static IL2CppArray<T> CreateFromArray(const T (&arr)[N]) {
        // Allocate enough memory to store the entire struct and all elements
        size_t totalSize = sizeof(IL2CppArray<T>) - sizeof(T*) + N * sizeof(T);
        void* buffer = malloc(totalSize);
        if (!buffer) {
            throw std::bad_alloc();
        }

        // Initialize the struct header information
        auto* result = new (buffer) IL2CppArray<T>;
        result->size = N;
        result->m_Items = reinterpret_cast<T*>(reinterpret_cast<char*>(buffer) + sizeof(IL2CppArray<T>) - sizeof(T*));

        // Copy the data
        std::memcpy(result->m_Items, arr, N * sizeof(T));

        return *result;
    }

    // Static Factory Function: Create a new IL2CppArray instance from the pointer and size
    static IL2CppArray<T> CreateFromPointer(const T* ptr, size_t count) {
        // Allocate enough memory to store the entire struct and all elements
        size_t totalSize = sizeof(IL2CppArray<T>) - sizeof(T*) + count * sizeof(T);
        void* buffer = malloc(totalSize);
        if (!buffer) {
            throw std::bad_alloc();
        }

        // Initialize the struct header information
        auto* result = new (buffer) IL2CppArray<T>;
        result->size = count;
        result->m_Items = reinterpret_cast<T*>(reinterpret_cast<char*>(buffer) + sizeof(IL2CppArray<T>) - sizeof(T*));

        // Copy the data
        std::memcpy(result->m_Items, ptr, count * sizeof(T));

        return *result;
    }

    // At function: Securely access and modify elements
    T& At(size_t index) {
        if (index >= size) {
            throw std::out_of_range("Index out of range");
        }
        return m_Items[index];
    }

    // Set function: Sets the value of the element
    void Set(size_t index, const T& value) {
        At(index) = value; // Use the At for boundary checking
    }

    // Get the array size
    [[nodiscard]] size_t Size() const {
        return size;
    }

    // Check if the array is empty
    [[nodiscard]] bool Empty() const {
        return size == 0;
    }

    // Get the first element (if it exists)
    T& Front() {
        if (Empty()) {
            throw std::out_of_range("Array is empty");
        }
        return m_Items[0];
    }

    // Get the last element (if it exists)
    T& Back() {
        if (Empty()) {
            throw std::out_of_range("Array is empty");
        }
        return m_Items[size - 1];
    }

    // Gets a pointer to an element, allowing direct manipulation of raw data
    T* Data() {
        return m_Items;
    }

    // Iterator support
    T* begin() {
        return m_Items;
    }

    T* end() {
        return m_Items + size;
    }

    // Modify the array size directly (note: this may require an external guarantee of memory validity)
    void Resize(size_t newSize) {
        if (!obj) {
            throw std::runtime_error("Cannot resize uninitialized array");
        }
        const size_t sizeOffset = offsetof(IL2CppArray<T>, size);
        *reinterpret_cast<size_t*>(reinterpret_cast<char*>(obj) + sizeOffset) = newSize;
        size = newSize;
    }

    // Sets the contents of the current array to the contents of another array
    template <typename U>
    void Assign(const IL2CppArray<U>& other) {
        if (sizeof(T) != sizeof(U)) {
            throw std::invalid_argument("Types must be of the same size");
        }
        if (other.Size() > this->size) {
            throw std::out_of_range("Target array is too small to hold source data");
        }
        std::memcpy(m_Items, other.Data(), other.Size() * sizeof(T));
        size = other.Size();
    }

    // Assign a value from std::vector
    void Assign(const std::vector<T>& vec) {
        if (vec.size() > this->size) {
            throw std::out_of_range("Target array is too small to hold source data");
        }
        if constexpr (std::is_same_v<T, bool>) {
            for (size_t i = 0; i < vec.size(); ++i) {
                m_Items[i] = vec[i] ? 1 : 0;
            }
        }
        else {
            std::memcpy(m_Items, vec.data(), vec.size() * sizeof(T));
        }
        size = vec.size();
    }

    // Assign values from C-style arrays
    template <size_t N>
    void Assign(const T (&arr)[N]) {
        if (N > this->size) {
            throw std::out_of_range("Target array is too small to hold source data");
        }
        std::memcpy(m_Items, arr, N * sizeof(T));
        size = N;
    }

    // Assign values from pointers and sizes
    void Assign(const T* ptr, size_t count) {
        if (count > this->size) {
            throw std::out_of_range("Target array is too small to hold source data");
        }
        std::memcpy(m_Items, ptr, count * sizeof(T));
        size = count;
    }

    // Populate the contents of the array
    void Fill(const T& value) {
        std::fill_n(m_Items, size, value);
    }

    // Swap the contents of two arrays
    void Swap(IL2CppArray<T>& other) {
        using std::swap;
        swap(obj, other.obj);
        swap(bounds, other.bounds);
        swap(unknown, other.unknown);
        swap(size, other.size);
        swap(m_Items, other.m_Items);
    }

    // Clearing the array (only reset size to 0, does not release memory)
    void Clear() {
        size = 0;
    }

    // Convert the array to std::vector
    [[nodiscard]] std::vector<T> ToVector() const {
        return std::vector<T>(m_Items, m_Items + size);
    }

    // Check if the array contains an element
    [[nodiscard]] bool Contains(const T& value) const {
        return std::find(begin(), end(), value) != end();
    }

    // Looks for the location of the element (returns the index) and returns -1 if it does not exist
    [[nodiscard]] int Find(const T& value) const {
        auto it = std::find(begin(), end(), value);
        if (it == end()) {
            return -1;
        } else {
            return static_cast<int>(std::distance(begin(), it));
        }
    }

    // 在 IL2CppArray 类中添加这个函数
    void Delete() {
        // 检查是否是工厂方法创建的对象（通过 malloc 分配）
        if (m_Items != nullptr &&
            reinterpret_cast<char*>(m_Items) == reinterpret_cast<char*>(this) + (sizeof(*this) - sizeof(T*))) {
            // 这是工厂方法创建的对象，需要整体释放
            free(this);
        } else {
            // 这是从现有指针解析的对象，只重置指针
            obj = nullptr;
            bounds = nullptr;
            unknown = nullptr;
            m_Items = nullptr;
            size = 0;
        }
    }

    ~IL2CppArray() {
        Delete();
    }
};


/*
void example_usage() {
    try {
        // Example 0: Parse from the field
        // Warning: This operation directly manipulates memory and requires an instance of Filed to work.
        // Please use with caution as it can lead to undefined behavior if not handled properly.


        IL2CppArray<bool> arr0(*((void**)Filed.GetPointer())); // Create array by dereferencing the pointer obtained from Filed instance
        arr0.Size(); // Retrieve the size of the array
        arr0.At(0); // Access the element at index 0 of the array
        arr0.Set(0, true); // Set the element at index 0 of the array to true



        // Example 1: Creating from std::vector
        std::vector<int> vec = {1, 2, 3, 4, 5};
        IL2CppArray<int> arr1 = IL2CppArray<int>::CreateFromVector(vec);

        // Example 2: Creating from C-style array
        int cStyleArr[] = {6, 7, 8, 9, 10};
        IL2CppArray<int> arr2 = IL2CppArray<int>::CreateFromArray(cStyleArr);

        // Example 3: Creating from pointer and size
        int* ptr = new int[5]{11, 12, 13, 14, 15};
        IL2CppArray<int> arr3 = IL2CppArray<int>::CreateFromPointer(ptr, 5);

        delete[] ptr;

        // Modifying the first element's value in arr1
        arr1.Set(0, 100);

        // Safely accessing an element in arr1
        std::cout << "Element at index 2 in arr1: " << arr1.At(2) << "\n";

        // Attempting to access an out-of-bounds element (will throw an exception)
        arr1.At(10);
    } catch (const std::exception& e) {
        std::cerr << "Caught an exception: " << e.what() << '\n';
    }
}
*/