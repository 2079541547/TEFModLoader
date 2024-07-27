//
// Created by eternalfuture on 2024/7/26.
//
#include <dlfcn.h>
#include <iostream>
#include <vector>
#include <string>
#include <BNM/Loading.hpp>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>
#include "getElement.cpp"

typedef int (*IntFunctionPtr)();
std::vector<int> loadAndInvokeIntFunctions(const std::string& soName, const std::vector<std::string>& methodNames) {
    std::vector<int> results;
    void* handle = dlopen(soName.c_str(), RTLD_LAZY);
    if (!handle) {
        BNM_LOG_ERR("无法打开库：%s", dlerror());
        return results;
    }
    for (const std::string& methodName : methodNames) {
        IntFunctionPtr func = (IntFunctionPtr) dlsym(handle, methodName.c_str());
        if (!func) {
            BNM_LOG_ERR("无法调用方法：%s", dlerror());
            continue;
        }
        int result = func();
        results.push_back(result);
        BNM_LOG_INFO("调用方法：%s \n 调用库：%s \n 返回值：%d", methodName.c_str(), soName.c_str(), result);
    }
    dlclose(handle);
    return results;
}


typedef bool (*BoolFunctionPtr)();
std::vector<bool> loadAndInvokeBoolFunctions(const std::string& soName, const std::vector<std::string>& methodNames) {
    std::vector<bool> results;
    void* handle = dlopen(soName.c_str(), RTLD_LAZY);
    if (!handle) {
        BNM_LOG_ERR("无法打开库：%s", dlerror());
        return results;
    }
    for (const std::string& methodName : methodNames) {
        BoolFunctionPtr func = (BoolFunctionPtr) dlsym(handle, methodName.c_str());
        if (!func) {
            BNM_LOG_ERR("无法调用方法：%s", dlerror());
            continue;
        }
        bool result = func();
        results.push_back(result);
        BNM_LOG_INFO("调用方法：%s \n 调用库：%s \n 返回值：%s", methodName.c_str(), soName.c_str(), result ? "true" : "false");
    }
    dlclose(handle);
    return results;
}


typedef std::string (*StringFunctionPtr)();
std::vector<std::string> loadAndInvokeStringFunctions(const std::string& soName, const std::vector<std::string>& methodNames) {
    std::vector<std::string> results;
    void* handle = dlopen(soName.c_str(), RTLD_LAZY);
    if (!handle) {
        BNM_LOG_ERR("无法打开库：%s", dlerror());
        return results;
    }
    for (const std::string& methodName : methodNames) {
        StringFunctionPtr func = (StringFunctionPtr) dlsym(handle, methodName.c_str());
        if (!func) {
            BNM_LOG_ERR("无法调用方法：%s", dlerror());
            continue;
        }
        std::string result = func();
        results.push_back(result);
        BNM_LOG_INFO("调用方法：%s \n 调用库：%s \n 返回值：%s", methodName.c_str(), soName.c_str(), result.c_str());
    }
    dlclose(handle);
    return results;
}


typedef float (*FloatFunctionPtr)();
std::vector<float> loadAndInvokeFloatFunctions(const std::string& soName, const std::vector<std::string>& methodNames) {
    std::vector<float> results;
    void* handle = dlopen(soName.c_str(), RTLD_LAZY);
    if (!handle) {
        BNM_LOG_ERR("无法打开库：%s", dlerror());
        return results;
    }
    for (const std::string& methodName : methodNames) {
        FloatFunctionPtr func = (FloatFunctionPtr) dlsym(handle, methodName.c_str());
        if (!func) {
            BNM_LOG_ERR("无法调用方法：%s", dlerror());
            continue;
        }
        float result = func();
        results.push_back(result);
        BNM_LOG_INFO("调用方法：%s \n 调用库：%s \n 返回值：%f", methodName.c_str(), soName.c_str(), result);
    }
    dlclose(handle);
    return results;
}


typedef int (*IntFunctionPtr)();
void loadAndInvokeVoidFunctions(const std::string& soName, const std::vector<std::string>& methodNames) {
    void* handle = dlopen(soName.c_str(), RTLD_LAZY);
    if (!handle) {
        BNM_LOG_ERR("无法打开库：%s", dlerror());
        return;
    }
    for (const std::string& methodName : methodNames) {
        IntFunctionPtr func = (IntFunctionPtr) dlsym(handle, methodName.c_str());
        if (!func) {
            BNM_LOG_ERR("无法调用方法：%s", dlerror());
            continue;
        }
        int result = func();
        BNM_LOG_INFO("调用方法：%s \n 调用库：%s \n 返回值：%d", methodName.c_str(), soName.c_str(), result);
    }

    dlclose(handle);
}