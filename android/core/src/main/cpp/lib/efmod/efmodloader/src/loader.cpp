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

#include "efmodloader/loader.hpp"
#include "efmodloader/logger.hpp"
#include "efmodloader/multi_channel.hpp"

#include <algorithm>
#include <utility>

EFModLoader::Loader::Loader(
    LoadFunc loadFunc,
    UnloadFunc unloadFunc,
    SymbolFunc symbolFunc
) : loadFunc_(std::move(loadFunc)),
    unloadFunc_(std::move(unloadFunc)),
    symbolFunc_(std::move(symbolFunc)) {
    LOG_INFO("EFModLoader initialized with custom loader functions");
}

// ==================== 同步操作实现 ====================

std::string EFModLoader::Loader::load(const std::string &path, const std::string &private_path) {
    LOG_DEBUG("Loading module: ", path);
    std::lock_guard lock(mutex_);
    return loadModuleInternal(path, private_path);
}

std::vector<std::string> EFModLoader::Loader::loadBatch(const std::unordered_map<std::string, std::string> &modMap) {
    LOG_INFO("Batch loading ", modMap.size(), " modules");
    std::lock_guard lock(mutex_);

    std::vector<std::string> loadedIds;
    for (const auto &[path, private_path]: modMap) {
        if (auto id = loadModuleInternal(path, private_path); !id.empty()) {
            loadedIds.push_back(id);
            LOG_DEBUG("Loaded module: ", id);
        } else {
            LOG_WARN("Failed to load module: ", path);
        }
    }
    return loadedIds;
}

bool EFModLoader::Loader::initialize(const std::string &modId) {
    LOG_DEBUG("Initializing module: ", modId);
    std::lock_guard lock(mutex_);

    const auto it = modules_.find(modId);
    if (it == modules_.end()) {
        LOG_WARN("Module not found: ", modId);
        return false;
    }

    if (it->second.initialized) {
        LOG_DEBUG("Module already initialized: ", modId);
        return true;
    }

    if (it->second.in_use) {
        LOG_ERROR("Module is in use: ", modId);
        return false;
    }

    it->second.in_use = true;
    int result = it->second.instance->Initialize(it->second.private_path, EFModLoader::LoaderMultiChannel::GetInstance());
    it->second.in_use = false;

    if (result != 0) {
        LOG_ERROR("Initialize failed for module: ", modId, " (code: ", result, ")");
        return false;
    }

    it->second.initialized = true;
    LOG_INFO("Module initialized: ", modId);
    return true;
}

std::vector<std::string> EFModLoader::Loader::initializeBatch(const std::vector<std::string> &modIds) {
    LOG_INFO("Batch initializing ", modIds.size(), " modules");
    std::lock_guard lock(mutex_);

    std::vector<std::string> initialized;
    for (const auto &modId: modIds) {
        auto it = modules_.find(modId);
        if (it == modules_.end()) {
            LOG_WARN("Module not found: ", modId);
            continue;
        }

        if (it->second.initialized) {
            initialized.push_back(modId);
            continue;
        }

        if (!it->second.in_use && it->second.instance->Initialize(it->second.private_path, EFModLoader::LoaderMultiChannel::GetInstance()) == 0) {
            it->second.initialized = true;
            initialized.push_back(modId);
            LOG_DEBUG("Initialized module: ", modId);
        } else {
            LOG_WARN("Initialize failed for module: ", modId);
        }
    }
    return initialized;
}

void EFModLoader::Loader::send(const std::string &modId) {
    LOG_TRACE("Sending to module: ", modId);
    std::lock_guard lock(mutex_);

    if (const auto it = modules_.find(modId); it != modules_.end()) {
        if (it->second.in_use) {
            LOG_WARN("Module is in use: ", modId);
            return;
        }
        it->second.in_use = true;
        it->second.instance->Send(it->second.private_path, EFModLoader::LoaderMultiChannel::GetInstance());
        it->second.in_use = false;
    } else {
        LOG_WARN("Module not found: ", modId);
    }
}

void EFModLoader::Loader::receive(const std::string &modId) {
    LOG_TRACE("Receiving from module: ", modId);
    std::lock_guard lock(mutex_);

    if (const auto it = modules_.find(modId); it != modules_.end()) {
        if (it->second.in_use) {
            LOG_WARN("Module is in use: ", modId);
            return;
        }
        it->second.in_use = true;
        it->second.instance->Receive(it->second.private_path, EFModLoader::LoaderMultiChannel::GetInstance());
        it->second.in_use = false;
    } else {
        LOG_WARN("Module not found: ", modId);
    }
}

bool EFModLoader::Loader::unload(const std::string &modId) {
    LOG_DEBUG("Unloading module: ", modId);
    std::lock_guard lock(mutex_);

    const auto it = modules_.find(modId);
    if (it == modules_.end()) {
        LOG_WARN("Module not found: ", modId);
        return false;
    }

    if (it->second.in_use) {
        LOG_ERROR("Module is in use: ", modId);
        return false;
    }

    it->second.instance->UnLoad(it->second.private_path, EFModLoader::LoaderMultiChannel::GetInstance());
    unloadFunc_(it->second.handle);
    modules_.erase(it);

    LOG_INFO("Module unloaded: ", modId);
    return true;
}

std::vector<std::string> EFModLoader::Loader::unloadBatch(const std::vector<std::string> &modIds) {
    LOG_INFO("Batch unloading ", modIds.size(), " modules");
    std::lock_guard lock(mutex_);

    std::vector<std::string> unloaded;
    for (const auto &modId: modIds) {
        auto it = modules_.find(modId);
        if (it == modules_.end()) {
            LOG_WARN("Module not found: ", modId);
            continue;
        }

        if (!it->second.in_use) {
            it->second.instance->UnLoad(it->second.private_path, EFModLoader::LoaderMultiChannel::GetInstance());
            unloadFunc_(it->second.handle);
            modules_.erase(it);
            unloaded.push_back(modId);
            LOG_DEBUG("Unloaded module: ", modId);
        } else {
            LOG_WARN("Module is in use, skip unloading: ", modId);
        }
    }
    return unloaded;
}

// ==================== 异步操作实现 ====================

std::future<std::string> EFModLoader::Loader::loadAsync(const std::string &path, const std::string &private_path) {
    return std::async(std::launch::async, [this, path, private_path]() {
        try {
            return this->load(path, private_path);
        } catch (const std::exception &e) {
            LOG_CRITICAL("Exception in async load: ", e.what());
            return std::string();
        }
    });
}

std::future<std::vector<std::string> > EFModLoader::Loader::loadBatchAsync(
    const std::unordered_map<std::string, std::string> &modMap) {
    return std::async(std::launch::async, [this, modMap]() {
        try {
            return this->loadBatch(modMap);
        } catch (const std::exception &e) {
            LOG_CRITICAL("Exception in async batch load: ", e.what());
            return std::vector<std::string>();
        }
    });
}

std::future<bool> EFModLoader::Loader::initializeAsync(const std::string &modId) {
    return std::async(std::launch::async, [this, modId]() {
        try {
            return this->initialize(modId);
        } catch (const std::exception &e) {
            LOG_CRITICAL("Exception in async initialize: ", e.what());
            return false;
        }
    });
}

std::future<std::vector<std::string> >
EFModLoader::Loader::initializeBatchAsync(const std::vector<std::string> &modIds) {
    return std::async(std::launch::async, [this, modIds]() {
        try {
            return this->initializeBatch(modIds);
        } catch (const std::exception &e) {
            LOG_CRITICAL("Exception in async batch initialize: ", e.what());
            return std::vector<std::string>();
        }
    });
}

std::future<void> EFModLoader::Loader::sendAsync(const std::string &modId) {
    return std::async(std::launch::async, [this, modId]() {
        try {
            this->send(modId);
        } catch (const std::exception &e) {
            LOG_CRITICAL("Exception in async send: ", e.what());
        }
    });
}

std::future<void> EFModLoader::Loader::receiveAsync(const std::string &modId) {
    return std::async(std::launch::async, [this, modId]() {
        try {
            this->receive(modId);
        } catch (const std::exception &e) {
            LOG_CRITICAL("Exception in async receive: ", e.what());
        }
    });
}

std::future<bool> EFModLoader::Loader::unloadAsync(const std::string &modId) {
    return std::async(std::launch::async, [this, modId]() {
        try {
            return this->unload(modId);
        } catch (const std::exception &e) {
            LOG_CRITICAL("Exception in async unload: ", e.what());
            return false;
        }
    });
}

std::future<std::vector<std::string> > EFModLoader::Loader::unloadBatchAsync(const std::vector<std::string> &modIds) {
    return std::async(std::launch::async, [this, modIds]() {
        try {
            return this->unloadBatch(modIds);
        } catch (const std::exception &e) {
            LOG_CRITICAL("Exception in async batch unload: ", e.what());
            return std::vector<std::string>();
        }
    });
}

// ==================== 全局操作实现 ====================

std::vector<std::string> EFModLoader::Loader::loadAll(const std::string &private_path_base,
                                                      const std::vector<std::string> &excludeIds) {
    LOG_INFO("Loading all modules (excluding ", excludeIds.size(), ")");
    std::lock_guard lock(mutex_);

    std::vector<std::string> loaded;
    for (const auto &[path, handle]: modules_) {
        if (std::find(excludeIds.begin(), excludeIds.end(), path) == excludeIds.end()) {
            std::string private_path = private_path_base.empty()
                                           ? handle.private_path
                                           : private_path_base + "/" + handle.metadata.name;
            handle.instance->Load(handle.private_path, EFModLoader::LoaderMultiChannel::GetInstance());
            loaded.push_back(path);
        }
    }
    return loaded;
}

std::vector<std::string> EFModLoader::Loader::initializeAll(const std::vector<std::string> &excludeIds) {
    LOG_INFO("Initializing all modules (excluding ", excludeIds.size(), ")");
    std::lock_guard lock(mutex_);

    std::vector<std::string> initialized;
    for (auto &[id, handle]: modules_) {
        if (std::find(excludeIds.begin(), excludeIds.end(), id) == excludeIds.end() &&
            !handle.initialized && !handle.in_use) {
            if (handle.instance->Initialize(handle.private_path, EFModLoader::LoaderMultiChannel::GetInstance()) == 0) {
                handle.initialized = true;
                initialized.push_back(id);
                LOG_DEBUG("Initialized module: ", id);
            }
        }
    }
    return initialized;
}

void EFModLoader::Loader::sendAll(const std::vector<std::string> &excludeIds) {
    LOG_DEBUG("Sending to all modules (excluding ", excludeIds.size(), ")");
    std::lock_guard lock(mutex_);

    for (auto &[id, handle]: modules_) {
        if (std::find(excludeIds.begin(), excludeIds.end(), id) == excludeIds.end() &&
            !handle.in_use) {
            handle.in_use = true;
            handle.instance->Send(handle.private_path, EFModLoader::LoaderMultiChannel::GetInstance());
            handle.in_use = false;
            LOG_TRACE("Sent to module: ", id);
        }
    }
}

void EFModLoader::Loader::receiveAll(const std::vector<std::string> &excludeIds) {
    LOG_DEBUG("Receiving from all modules (excluding ", excludeIds.size(), ")");
    std::lock_guard lock(mutex_);

    for (auto &[id, handle]: modules_) {
        if (std::find(excludeIds.begin(), excludeIds.end(), id) == excludeIds.end() &&
            !handle.in_use) {
            handle.in_use = true;
            handle.instance->Receive(handle.private_path, EFModLoader::LoaderMultiChannel::GetInstance());
            handle.in_use = false;
            LOG_TRACE("Received from module: ", id);
        }
    }
}

std::vector<std::string> EFModLoader::Loader::unloadAll(const std::vector<std::string> &excludeIds) {
    LOG_INFO("Unloading all modules (excluding ", excludeIds.size(), ")");
    std::lock_guard lock(mutex_);

    std::vector<std::string> unloaded;
    for (auto it = modules_.begin(); it != modules_.end();) {
        if (std::find(excludeIds.begin(), excludeIds.end(), it->first) == excludeIds.end() &&
            !it->second.in_use) {
            it->second.instance->UnLoad(it->second.private_path, EFModLoader::LoaderMultiChannel::GetInstance());
            unloadFunc_(it->second.handle);
            unloaded.push_back(it->first);
            LOG_DEBUG("Unloaded module: ", it->first);
            it = modules_.erase(it);
        } else {
            ++it;
        }
    }
    return unloaded;
}

// ==================== 辅助函数实现 ====================

std::vector<std::string> EFModLoader::Loader::getLoadedModules() const {
    std::lock_guard lock(mutex_);
    std::vector<std::string> modules;
    for (const auto &[fst, snd]: modules_) {
        modules.push_back(fst);
    }
    return modules;
}

Metadata EFModLoader::Loader::getMetadata(const std::string &modId) const {
    std::lock_guard lock(mutex_);
    if (const auto it = modules_.find(modId); it != modules_.end()) {
        return it->second.metadata;
    }
    return {};
}

EFMod *EFModLoader::Loader::getInstance(const std::string &modId) const {
    std::lock_guard lock(mutex_);
    if (const auto it = modules_.find(modId); it != modules_.end()) {
        return it->second.instance;
    }
    return nullptr;
}

// ==================== 私有函数实现 ====================

EFModLoader::Loader::ModuleHandle::ModuleHandle(ModuleHandle &&other) noexcept:
                handle(std::exchange(other.handle, nullptr)),
                path(std::move(other.path)),
                private_path(std::move(other.private_path)),
                instance(std::exchange(other.instance, nullptr)),
                metadata(std::move(other.metadata)),
                initialized(other.initialized.load()),
                in_use(other.in_use.load()) {}

EFModLoader::Loader::ModuleHandle & EFModLoader::Loader::ModuleHandle::operator=(ModuleHandle &&other) noexcept {
    if (this != &other) {
        handle = std::exchange(other.handle, nullptr);
        path = std::move(other.path);
        private_path = std::move(other.private_path);
        instance = std::exchange(other.instance, nullptr);
        metadata = std::move(other.metadata);
        initialized.store(other.initialized.load());
        in_use.store(other.in_use.load());
    }
    return *this;
}

std::string EFModLoader::Loader::loadModuleInternal(const std::string &path, const std::string &private_path) {
    // 检查是否已加载
    for (const auto &[id, module] : modules_) {
        if (module.path == path) {
            LOG_WARN("Module already loaded: ", path);
            return id;
        }
    }

    // 加载库
    void* handle = loadFunc_(path);
    if (!handle) {
        LOG_ERROR("Failed to load library: ", path);
        return "";
    }

    // 使用自定义删除器管理handle
    auto handle_deleter = [this](void* h) { this->unloadFunc_(h); };
    std::unique_ptr<void, decltype(handle_deleter)> handle_guard(handle, handle_deleter);

    // 获取CreateMod函数
    const auto createFunc = reinterpret_cast<EFMod*(*)()>(symbolFunc_(handle, "CreateMod"));
    if (!createFunc) {
        LOG_ERROR("Failed to find CreateMod symbol in: ", path);
        return "";
    }

    // 创建模块实例
    EFMod* mod = createFunc();
    if (!mod) {
        LOG_ERROR("CreateMod returned nullptr for: ", path);
        return "";
    }

    // 使用自定义删除器管理mod实例
    auto mod_deleter = [&private_path](EFMod* m) {
        if (m) {
            m->UnLoad(private_path, EFModLoader::LoaderMultiChannel::GetInstance());
            delete m;
        }
    };
    std::unique_ptr<EFMod, decltype(mod_deleter)> mod_guard(mod, mod_deleter);

    // 获取元数据
    const auto metadata = mod->GetMetadata();
    std::string modId = generateModuleId(metadata);

    if (modules_.find(modId) != modules_.end()) {
        LOG_WARN("Duplicate module ID: ", modId);
        return "";
    }

    // 构造ModuleHandle并转移所有权
    ModuleHandle module_handle;
    module_handle.handle = handle_guard.release();
    module_handle.path = path;
    module_handle.private_path = private_path;
    module_handle.instance = mod_guard.release();
    module_handle.metadata = metadata;
    module_handle.initialized = metadata.config.Initialize;
    module_handle.in_use = false;

    // 使用try_emplace插入
    if (auto [it, inserted] = modules_.try_emplace(modId, std::move(module_handle)); !inserted) {
        LOG_ERROR("Failed to insert module into registry: ", modId);
        LOG_DEBUG("try_emplace returned false, possible race condition");
        return "";
    }

    LOG_INFO("Module loaded successfully - ID: ", modId,
          ", Type: ", ModuleTypeToString(metadata.type),
          ", Standard: ", metadata.standard);
    LOG_DEBUG("Total modules now loaded: ", modules_.size());

    return modId;
}

std::string EFModLoader::Loader::generateModuleId(const Metadata &metadata) {
    auto sanitize = [](std::string str) {
        std::replace_if(str.begin(), str.end(),
                        [](const char c) { return !isalnum(c) && c != '_'; }, '_');
        return str;
    };

    return sanitize(metadata.name) + "_" +
           sanitize(metadata.version) + "_" +
           sanitize(metadata.author);
}

std::string EFModLoader::ModuleTypeToString(const ModuleType i) {
    switch(i) {
        case ModuleType::System: return "System";
        case ModuleType::Content: return "Content";
        case ModuleType::Game: return "Game";
        case ModuleType::Interface: return "Interface";
        case ModuleType::Library: return "Library";
    }
    return "Unknown";
}
