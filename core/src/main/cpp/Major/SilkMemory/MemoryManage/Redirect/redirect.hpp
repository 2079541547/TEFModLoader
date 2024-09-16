//
// Created by eternalfuture on 2024/9/15.
//

#ifndef SILKMEMORY_REDIRECT_HPP
#define SILKMEMORY_REDIRECT_HPP

#include "../LogSytem/log.h"
#include <iostream>
#include <iomanip>


namespace Redirect{

    using namespace std;

    class redirect{
    public:
        template<typename T>
        static uintptr_t getPtr(T* ptr){
            auto ptrAddr = reinterpret_cast<uintptr_t>(ptr);
            LOGS::LOG("信息", "Redirect", "redirect", "getPtr", "获取到的指针地址：" + to_string(ptrAddr));
            return ptrAddr;
        }

        template <typename R, typename... Args>
        static  R callFunction(void *funcPtr, Args &&...args)
        {
            // 从 uintptr_t 转换为函数指针
            using FuncPtr = R (*)(Args...);
            auto f = reinterpret_cast<FuncPtr>(funcPtr);

            // 调用函数
            return f(std::forward<Args>(args)...);
        }



        //重定向指针
        template<typename T>
        static void redirectPointer(uintptr_t originalPtrAddress, uintptr_t newPtrAddress) {
            LOGS::LOG("调试", "Redirect", "redirect", "redirectPointer", "正在尝试将：" + to_string(originalPtrAddress) + "重定向为：" + to_string(newPtrAddress));
            #if defined(__x86_64__)
                // x86-64 汇编
                asm volatile (
                "mov %%rax, %%rdi"
                : "+a"(newPtrAddress), "+D"(*reinterpret_cast<T**>(originalPtrAddress))
                );

                LOGS::LOG("调试", "Redirect", "redirect", "redirectPointer", "重定向成功，架构：x86_64，原始地址：" + to_string(originalPtrAddress) + "重定向为：" + to_string(newPtrAddress));

            #elif defined(__i386__)
                // i386 汇编
                asm volatile (
                    "movl %%eax, %%edi"
                    : "+a"(newPtrAddress), "+D"(*reinterpret_cast<T**>(originalPtrAddress))
                    );
                LOGS::LOG("调试", "Redirect", "redirect", "redirectPointer", "重定向成功，架构：x86，原始地址：" + to_string(originalPtrAddress) + "重定向为：" + to_string(newPtrAddress));
            #elif defined(__aarch64__)
                // AArch64 汇编
                asm volatile (
                    "str x0, [x1]"
                    : : "r"(newPtrAddress), "r"(reinterpret_cast<T*>(originalPtrAddress)) : "memory"
                );
                LOGS::LOG("调试", "Redirect", "redirect", "redirectPointer", "重定向成功，架构：arm64-v8a，原始地址：" + to_string(originalPtrAddress) + "重定向为：" + to_string(newPtrAddress));
            #elif defined(__arm__)
            // AArch32 汇编
            asm volatile (
                "str %0, [%1]"
                : : "r"(newPtrAddress), "r"(reinterpret_cast<T*>(originalPtrAddress)) : "memory"
            );
            LOGS::LOG("调试", "Redirect", "redirect", "redirectPointer", "重定向成功，架构：armeabi-v7a，原始地址：" + to_string(originalPtrAddress) + "重定向为：" + to_string(newPtrAddress));
            #else
            #error "Unsupported architecture"
            #endif
        }

        template<typename T>
        static void redirectFunctionPointer(uintptr_t originalPtrAddress, uintptr_t newPtrAddress) {
            #if defined(__x86_64__)
                // x86-64 汇编
                asm volatile (
                    "mov %%rax, %%rdi"
                    : "+a"(*reinterpret_cast<T**>(newPtrAddress)), "+D"(*reinterpret_cast<T**>(originalPtrAddress))
                );
                LOGS::LOG("调试", "Redirect", "redirect", "redirectFunctionPointer", "重定向成功，架构：x86_64，原始地址：" + to_string(originalPtrAddress) + "重定向为：" + to_string(newPtrAddress));

            #elif defined(__i386__)
                // i386 汇编
                asm volatile (
                    "movl %%eax, %%edi"
                    : "+a"(*reinterpret_cast<T**>(newPtrAddress)), "+D"(*reinterpret_cast<T**>(originalPtrAddress))
                    );
            LOGS::LOG("调试", "Redirect", "redirect", "redirectFunctionPointer", "重定向成功，架构：x86，原始地址：" + to_string(originalPtrAddress) + "重定向为：" + to_string(newPtrAddress));

            #elif defined(__aarch64__)
                // AArch64 汇编
                asm volatile (
                    "str x0, [x1]"
                    : : "r"(reinterpret_cast<T*>(newPtrAddress)), "r"(reinterpret_cast<T*>(originalPtrAddress)) : "memory"
                );
                LOGS::LOG("调试", "Redirect", "redirect", "redirectFunctionPointer", "重定向成功，架构：arm64-v8a，原始地址：" + to_string(originalPtrAddress) + "重定向为：" + to_string(newPtrAddress));
            #elif defined(__arm__)
            // AArch32 汇编
                asm volatile (
                    "str %0, [%1]"
                    : : "r"(reinterpret_cast<T*>(newPtrAddress)), "r"(reinterpret_cast<T*>(originalPtrAddress)) : "memory"
                );
                LOGS::LOG("调试", "Redirect", "redirect", "redirectFunctionPointer", "重定向成功，架构：armeabi-v7a，原始地址：" + to_string(originalPtrAddress) + "重定向为：" + to_string(newPtrAddress));
            #else
            #error "Unsupported architecture"
            #endif
        }



    };
};

#endif //SILKMEMORY_REDIRECT_HPP
