//
// Created by eternalfuture on 2024/9/15.
//

#ifndef SILKMEMORY_MEMORYMANAGE_HPP
#define SILKMEMORY_MEMORYMANAGE_HPP

#include <iostream>
#include <memory>
#include <type_traits>
#include <utility>
#include <string>
#include <functional>
#include "LogSytem/log.h"


namespace MemoryManage {
    using namespace std;
    #define LOGE(hs, mgs) LOGS::LOG("Error", "MemoryManage", "SafeFunctionCaller", hs, mgs);

    template <typename R, typename... Args>
    R SafeFunctionCaller(void *funcPtr, Args &&...args)
    {
        try {
            // 从 uintptr_t 转换为函数指针
            using FuncPtr = R (*)(Args...);
            auto f = reinterpret_cast<FuncPtr>(funcPtr);

            // 调用函数
            return f(std::forward<Args>(args)...);
        } catch (const std::exception& e) {
            LOGE("call", "捕获到异常：" + std::string(e.what()));
            return {}; // 返回默认构造的对象
        } catch (...) {
            LOGE("call", "捕获到未知异常");
            return {}; // 返回默认构造的对象
        }
    }


};



#endif //SILKMEMORY_MEMORYMANAGE_HPP
