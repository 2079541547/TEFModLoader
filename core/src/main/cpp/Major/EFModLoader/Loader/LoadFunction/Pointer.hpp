#pragma once

#include <iostream>
#include <vector>
#include <fstream>
#include <type_traits>
#include <cstdarg>
#include <cstdint>

using namespace std;

namespace Pointer
{


    template <typename R, typename... Args>
    R callFunction(void *funcPtr, Args &&...args)
    {
        // 从 uintptr_t 转换为函数指针
        using FuncPtr = R (*)(Args...);
        auto f = reinterpret_cast<FuncPtr>(funcPtr);

        // 调用函数
        return f(std::forward<Args>(args)...);
    }

     // 将 uintptr_t 数组写入文件
    void writePointersToFile(const vector<uintptr_t>& pointers, const string& filename)
    {
        ofstream file(filename, ios::binary | ios::out);
        if (!file.is_open())
        {
            cerr << "Failed to open file for writing." << endl;
            return;
        }

        for (uintptr_t ptr : pointers)
        {
            file.write((const char*)&ptr, sizeof(uintptr_t));
        }

        file.close();
    }

    // 从文件中读取 uintptr_t 数组，并返回指定索引处的值
    uintptr_t readPointerFromFile(const string& filename, size_t index)
    {
        ifstream file(filename, ios::binary | ios::in);
        if (!file.is_open())
        {
            cerr << "Failed to open file for reading." << endl;
            return 0; // 或者抛出异常
        }

        vector<uintptr_t> pointers;
        uintptr_t temp;
        while (file.read((char*)&temp, sizeof(uintptr_t)))
        {
            pointers.push_back(temp);
        }

        if (index >= pointers.size())
        {
            cerr << "Index out of bounds." << endl;
            return 0; // 或者抛出异常
        }

        file.close();
        return pointers[index];
    }
};