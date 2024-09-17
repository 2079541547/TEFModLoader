//
// Created by eternalfuture on 2024/9/17.
//

#pragma once

#include <map>
#include <string>
#include <vector>
#include <mutex>

class ModLoaderAPI final {
public:
    // 注册模组的扩展函数
    void RegisterExtension(const std::string& extensionPoint, uintptr_t hook);

    // 查找扩展函数集合
    std::vector<uintptr_t> FindHooks(const std::string& extensionPoint);

    // 获取单例实例
    static ModLoaderAPI& GetAPI();

private:
    // 用于存储扩展函数指针集合
    std::map<std::string, std::vector<uintptr_t>> extensions;
    std::mutex extensionsMutex;

    // 私有构造函数
    ModLoaderAPI() {}
};

// 实现单例的获取方法
inline ModLoaderAPI& ModLoaderAPI::GetAPI() {
    static ModLoaderAPI instance;
    return instance;
}

// 实现 RegisterExtension 方法
inline void ModLoaderAPI::RegisterExtension(const std::string& extensionPoint, uintptr_t hook) {
    std::lock_guard<std::mutex> lock(extensionsMutex);
    extensions[extensionPoint].push_back(hook);
}

// 实现 FindHooks 方法
inline std::vector<uintptr_t> ModLoaderAPI::FindHooks(const std::string& extensionPoint) {
    std::lock_guard<std::mutex> lock(extensionsMutex);
    auto it = extensions.find(extensionPoint);
    if (it == extensions.end()) {
        return {}; // 返回空向量表示没有找到对应的扩展点
    }
    return it->second;
}