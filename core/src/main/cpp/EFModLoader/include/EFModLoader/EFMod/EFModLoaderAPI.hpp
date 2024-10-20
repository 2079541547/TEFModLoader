//
// Created by eternalfuture on 2024/9/28.
//

#pragma once

#include <map>
#include <string>
#include <vector>
#include <mutex>

class EFModLoaderAPI final {
public:
    // 注册模组的扩展函数
    void RegisterExtension(const std::string& extensionPoint, uintptr_t hook);

    // 注册API（被Mod调用）
    void RegisterAPI(const std::string& APIPoint, uintptr_t Api);

    // 查找扩展函数集合
    std::vector<uintptr_t> FindHooks(const std::string& extensionPoint);

    // 查找API集合
    std::vector<uintptr_t> FindAPIS(const std::string& APIPoint);

    // 获取单例实例
    static EFModLoaderAPI& GetEFModLoader();

private:
    // 用于存储扩展函数指针集合
    std::map<std::string, std::vector<uintptr_t>> extensions;
    std::mutex extensionsMutex;

    // 用于存储注册的API列表用于重定向
    std::map<std::string, std::vector<uintptr_t>> APIS;
    std::mutex APISMutex;

    // 私有构造函数
    EFModLoaderAPI() {}
};


// 实现单例的获取方法
inline EFModLoaderAPI& EFModLoaderAPI::GetEFModLoader() {
    static EFModLoaderAPI instance;
    return instance;
}

// 实现 RegisterExtension 方法
inline void EFModLoaderAPI::RegisterExtension(const std::string& extensionPoint, uintptr_t hook) {
    std::lock_guard<std::mutex> lock(extensionsMutex);
    extensions[extensionPoint].push_back(hook);
}

// 实现 RegisterAPI 方法
inline void EFModLoaderAPI::RegisterAPI(const std::string &APIPoint, uintptr_t Api) {
    std::lock_guard<std::mutex> lock(APISMutex);
    APIS[APIPoint].push_back(Api);
}

// 实现 FindHooks 方法
inline std::vector<uintptr_t> EFModLoaderAPI::FindHooks(const std::string& extensionPoint) {
    std::lock_guard<std::mutex> lock(extensionsMutex);
    auto it = extensions.find(extensionPoint);
    if (it == extensions.end()) {
        return {}; // 返回空向量表示没有找到对应的扩展点
    }
    return it->second;
}


inline std::vector<uintptr_t> EFModLoaderAPI::FindAPIS(const std::string& APIPoint) {
    std::lock_guard<std::mutex> lock(APISMutex);
    auto it = APIS.find(APIPoint);
    if (it == APIS.end()) {
        return {}; // 返回空向量表示没有找到对应的API
    }
    return it->second;
}
