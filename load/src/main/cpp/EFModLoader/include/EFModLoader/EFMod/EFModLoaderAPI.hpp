/*******************************************************************************
 * 文件名称: EFModAPI
 * 项目名称: EFModLoader
 * 创建时间: 2024/9/28
 * 作者: EternalFuture゙
 * Github: https://github.com/2079541547
 * 版权声明: Copyright © 2024 EternalFuture゙. All rights reserved.
 * 许可证: This project is licensed under the MIT License.
 *
 *         Permission is hereby granted, free of charge, to any person obtaining a copy
 *         of this software and associated documentation files (the "Software"), to deal
 *         in the Software without restriction, including without limitation the rights
 *         to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *         copies of the Software, and to permit persons to whom the Software is
 *         furnished to do so, subject to the following conditions:
 *
 *         The above copyright notice and this permission notice shall be included in all
 *         copies or substantial portions of the Software.
 *
 *         THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *         IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *         FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *         AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *         LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *         OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *         SOFTWARE.
 * 描述信息: 本文件为EFModLoader项目中的一部分。
 * 注意事项: 请严格遵守MIT协议使用本代码，任何未经授权的商业用途均属侵权行为。
 *******************************************************************************/

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
