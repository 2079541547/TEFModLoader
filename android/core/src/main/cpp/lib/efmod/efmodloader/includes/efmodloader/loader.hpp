/*******************************************************************************
 * 文件名称: loader
 * 项目名称: EFMod
 * 创建时间: 25-5-10
 * 作者: EternalFuture゙
 * Gitlab: https://gitlab.com/2079541547/
 * 协议: MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *******************************************************************************/

#pragma once

#include <atomic>
#include <functional>
#include <future>
#include <mutex>
#include <string>
#include <unordered_map>

#include "efmod_core.hpp"

namespace EFModLoader {
    class Loader final {
    public:
        /**
         * @brief 模块加载函数类型
         * @param path 模块文件路径
         * @return 成功返回模块句柄，失败返回nullptr
         */
        using LoadFunc = std::function<void*(const std::string &path)>;

        /**
         * @brief 模块卸载函数类型
         * @param handle 模块句柄
         */
        using UnloadFunc = std::function<void(void *handle)>;

        /**
         * @brief 符号查找函数类型
         * @param handle 模块句柄
         * @param name 符号名称
         * @return 成功返回符号地址，失败返回nullptr
         */
        using SymbolFunc = std::function<void*(void *handle, const std::string &name)>;

        /**
         * @brief 构造函数
         * @param loadFunc 库加载函数
         * @param unloadFunc 库卸载函数
         * @param symbolFunc 符号查找函数
         */
        explicit Loader(
            LoadFunc loadFunc,
            UnloadFunc unloadFunc,
            SymbolFunc symbolFunc
        );

        // ==================== 同步操作 ====================

        /**
         * @brief 同步加载单个模块
         * @param path 模块文件路径
         * @param private_path 模块私有数据路径
         * @return 成功返回模块ID，失败返回空字符串
         */
        std::string load(const std::string &path, const std::string &private_path);

        /**
         * @brief 同步批量加载模块
         * @param modMap 模块路径映射表 <模块路径, 私有路径>
         * @return 成功加载的模块ID列表
         */
        std::vector<std::string> loadBatch(const std::unordered_map<std::string, std::string> &modMap);

        /**
         * @brief 同步初始化单个模块
         * @param modId 模块ID
         * @return 成功返回true，失败返回false
         */
        bool initialize(const std::string &modId);

        /**
         * @brief 同步批量初始化模块
         * @param modIds 要初始化的模块ID列表
         * @return 成功初始化的模块ID列表
         */
        std::vector<std::string> initializeBatch(const std::vector<std::string> &modIds);

        /**
         * @brief 同步发送数据到单个模块
         * @param modId 模块ID
         */
        void send(const std::string &modId);

        /**
         * @brief 同步从单个模块接收数据
         * @param modId 模块ID
         */
        void receive(const std::string &modId);

        /**
         * @brief 同步卸载单个模块
         * @param modId 模块ID
         * @return 成功返回true，失败返回false
         */
        bool unload(const std::string &modId);

        /**
         * @brief 同步批量卸载模块
         * @param modIds 要卸载的模块ID列表
         * @return 成功卸载的模块ID列表
         */
        std::vector<std::string> unloadBatch(const std::vector<std::string> &modIds);

        // ==================== 异步操作 ====================

        /**
         * @brief 异步加载单个模块
         * @param path 模块文件路径
         * @param private_path 模块私有数据路径
         * @return future对象，包含模块ID
         */
        std::future<std::string> loadAsync(const std::string &path, const std::string &private_path);

        /**
         * @brief 异步批量加载模块
         * @param modMap 模块路径映射表 <模块路径, 私有路径>
         * @return future对象，包含成功加载的模块ID列表
         */
        std::future<std::vector<std::string> > loadBatchAsync(
            const std::unordered_map<std::string, std::string> &modMap);

        /**
         * @brief 异步初始化单个模块
         * @param modId 模块ID
         * @return future对象，表示操作是否成功
         */
        std::future<bool> initializeAsync(const std::string &modId);

        /**
         * @brief 异步批量初始化模块
         * @param modIds 要初始化的模块ID列表
         * @return future对象，包含成功初始化的模块ID列表
         */
        std::future<std::vector<std::string> > initializeBatchAsync(const std::vector<std::string> &modIds);

        /**
         * @brief 异步发送数据到单个模块
         * @param modId 模块ID
         * @return future对象，表示操作完成
         */
        std::future<void> sendAsync(const std::string &modId);

        /**
         * @brief 异步从单个模块接收数据
         * @param modId 模块ID
         * @return future对象，表示操作完成
         */
        std::future<void> receiveAsync(const std::string &modId);

        /**
         * @brief 异步卸载单个模块
         * @param modId 模块ID
         * @return future对象，表示操作是否成功
         */
        std::future<bool> unloadAsync(const std::string &modId);

        /**
         * @brief 异步批量卸载模块
         * @param modIds 要卸载的模块ID列表
         * @return future对象，包含成功卸载的模块ID列表
         */
        std::future<std::vector<std::string> > unloadBatchAsync(const std::vector<std::string> &modIds);

        // ==================== 全局操作 ====================

        /**
         * @brief 对所有模块执行Load操作
         * @param private_path_base 私有路径基础前缀
         * @param excludeIds 要排除的模块ID列表
         * @return 成功加载的模块ID列表
         */
        std::vector<std::string> loadAll(const std::string &private_path_base = "",
                                         const std::vector<std::string> &excludeIds = {});

        /**
         * @brief 对所有模块执行Initialize操作
         * @param excludeIds 要排除的模块ID列表
         * @return 成功初始化的模块ID列表
         */
        std::vector<std::string> initializeAll(const std::vector<std::string> &excludeIds = {});

        /**
         * @brief 对所有模块执行Send操作
         * @param excludeIds 要排除的模块ID列表
         */
        void sendAll(const std::vector<std::string> &excludeIds = {});

        /**
         * @brief 对所有模块执行Receive操作
         * @param excludeIds 要排除的模块ID列表
         */
        void receiveAll(const std::vector<std::string> &excludeIds = {});

        /**
         * @brief 对所有模块执行UnLoad操作
         * @param excludeIds 要排除的模块ID列表
         * @return 成功卸载的模块ID列表
         */
        std::vector<std::string> unloadAll(const std::vector<std::string> &excludeIds = {});

        // ==================== 辅助函数 ====================

        /**
         * @brief 获取所有已加载模块ID
         * @return 模块ID列表
         */
        std::vector<std::string> getLoadedModules() const;

        /**
         * @brief 获取模块元数据
         * @param modId 模块ID
         * @return 模块元数据，如果模块不存在则返回空元数据
         */
        Metadata getMetadata(const std::string &modId) const;

        /**
         * @brief 获取模块实例
         * @param modId 模块ID
         * @return 模块实例指针，如果模块不存在则返回nullptr
         */
        EFMod *getInstance(const std::string &modId) const;

    private:
        struct ModuleHandle {
            void* handle = nullptr;
            std::string path;
            std::string private_path;
            EFMod* instance = nullptr;
            Metadata metadata;
            std::atomic<bool> initialized{false};
            std::atomic<bool> in_use{false};

            ModuleHandle() = default;

            // 添加移动构造函数
            ModuleHandle(ModuleHandle&& other) noexcept;

            // 添加移动赋值运算符
            ModuleHandle& operator=(ModuleHandle&& other) noexcept;

            // 删除复制操作
            ModuleHandle(const ModuleHandle&) = delete;
            ModuleHandle& operator=(const ModuleHandle&) = delete;
        };

        std::unordered_map<std::string, ModuleHandle> modules_;
        mutable std::mutex mutex_;

        LoadFunc loadFunc_;
        UnloadFunc unloadFunc_;
        SymbolFunc symbolFunc_;

        std::string loadModuleInternal(const std::string &path, const std::string &private_path);

        static std::string generateModuleId(const Metadata &metadata);
    };

    /**
     *
     * @param i 传入的ModuleType
     * @return 根据ModuleType返回对应字符串
     */
    std::string ModuleTypeToString(ModuleType i);
}
