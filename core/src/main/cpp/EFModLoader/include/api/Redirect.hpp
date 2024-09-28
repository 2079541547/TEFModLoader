//
// Created by eternalfuture on 2024/9/28.
//

#pragma once

#include <iostream>
#include "../log.hpp"

namespace EFModLoader::Redirect {

    template<typename T>
    uintptr_t getPtr(T* ptr) {
        auto ptrAddr = reinterpret_cast<uintptr_t>(ptr);
        EFModLoader::Log::LOG("Info", "Redirect", "getPtr", "获取到的指针地址：" + std::to_string(ptrAddr));
        return ptrAddr;
    }

    template <typename R, typename... Args>
    R callFunction(void *funcPtr, Args &&...args) {
        // 从 uintptr_t 转换为函数指针
        using FuncPtr = R (*)(Args...);
        auto f = reinterpret_cast<FuncPtr>(funcPtr);
        // 调用函数
        return f(std::forward<Args>(args)...);
    }

    template<typename T>
    void redirectPointer(uintptr_t originalPtrAddress, uintptr_t newPtrAddress) {
        EFModLoader::Log::LOG("Debug", "Redirect", "redirectPointer", "正在尝试将：" + std::to_string(originalPtrAddress) + " 重定向为：" + std::to_string(newPtrAddress));

        T** originalPtr = reinterpret_cast<T**>(originalPtrAddress);
        *originalPtr = reinterpret_cast<T*>(newPtrAddress);

        EFModLoader::Log::LOG("Debug", "Redirect", "redirectPointer", "重定向成功，原始地址：" + std::to_string(originalPtrAddress) + " 重定向为：" + std::to_string(newPtrAddress));
    }

}