/*******************************************************************************
 * 文件名称: Loader
 * 项目名称: EFModLoader
 * 创建时间: 2024/12/28
 * 作者: EternalFuture゙
 * Github: https://github.com/2079541547 
 * 版权声明: Copyright © 2024 EternalFuture. All rights reserved.
 * 许可证: This program is free software: you can redistribute it and/or modify
 *         it under the terms of the GNU Affero General Public License as published
 *         by the Free Software Foundation, either version 3 of the License, or
 *         (at your option) any later version.
 *
 *         This program is distributed in the hope that it will be useful,
 *         but WITHOUT ANY WARRANTY; without even the implied warranty of
 *         MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *         GNU Affero General Public License for more details.
 *
 *         You should have received a copy of the GNU Affero General Public License
 *         along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * 描述信息: 本文件为EFModLoader项目中的一部分。
 * 注意事项: 请严格遵守GNU AGPL v3.0协议使用本代码，任何未经授权的商业用途均属侵权行为。
 *******************************************************************************/
 
#include <Load/Loader.hpp>
#include <log.hpp>
#include <vector>
#include <unordered_set>
#include <memory>
#include <string>
#include <filesystem>
#include <thread>
#include <future>
#include <mutex>
#include <Manager/API.hpp>

void EFModLoader::Load::loadMod(const std::filesystem::path& Path) {
        if (!exists(Path)) {
                EFLOG(ERROR, "获取Mod", "路径不存在：", Path);
                return;
        }
        for (const auto& entry : std::filesystem::recursive_directory_iterator(Path / "lib")) {
                if (entry.is_regular_file()) {
                        EFLOG(INFO, "加载Mod", "尝试加载Mod:", entry);
                        void* handle = EFopen(entry.path().c_str());
                        if (!handle) {
                                EFLOG(ERROR, "获取Mod句柄", "句柄为空指针！");
                                return;
                        } else {
                                EFMod* (*CreateMod)();
                                CreateMod = (EFMod* (*)())EFgetsym(handle, "CreateMod");
                                EFLOG(INFO, "创建Mod", "已获取Mod构建函数并尝试创建Mod:", entry);
                                if (!CreateMod) {
                                        EFLOG(ERROR, "创建Mod", "构建函数为空指针！\n正在尝试关闭Mod:", Path);
                                        EFclose(handle);
                                        return;
                                }
                                auto modStandard = CreateMod()->standard;
                                EFLOG(INFO, "创建Mod", "Mod开发标准:", modStandard);
                                
                                if (modStandard >= 20250101) {
                                        
                                        auto ModID = std::hash<std::string>{}(CreateMod()->getInfo().name + CreateMod()->getInfo().author);
                                        
                                        std::shared_ptr<Mod> newMod(new Mod());
                                        for (const auto& _: mod) {
                                                if (ModID == _.id) {
                                                        EFLOG(ERROR, "创建Mod", "已创建相同的Mod:", _.loadPath, "\n停止加载Mod:", entry);
                                                        return;
                                                }
                                        }
                                        
                                        CreateMod()->Data = Path / "private";
                                        EFLOG(INFO, "创建Mod", "已赋予Mod私有目录");
                                        CreateMod()->RegisterAPI(&EFModAPI::getEFModAPI());
                                        EFLOG(INFO, "创建Mod", "已调用Mod注册API");
                                        CreateMod()->RegisterExtend(&EFModAPI::getEFModAPI());
                                        EFLOG(INFO, "创建Mod", "已调用Mod注册扩展");
                                        newMod->id = ModID;
                                        EFLOG(INFO, "创建Mod", "已赋予id");
                                        newMod->loaded = handle;
                                        EFLOG(INFO, "创建Mod", "已收集句柄");
                                        newMod->loadPath = Path;
                                        EFLOG(INFO, "创建Mod", "已收集加载路径");
                                        newMod->Instance = CreateMod();
                                        EFLOG(INFO, "创建Mod", "已收集Mod实例");
                                        newMod->info = CreateMod()->getInfo();
                                        EFLOG(INFO, "创建Mod", "已收集Mod元数据信息");
                                        mod.push_back(*newMod);
                                        EFLOG(INFO, "创建Mod", "创建Mod成功:", entry);
                                } else {
                                        EFLOG(ERROR, "创建Mod", "未知开发标准");
                                        free(CreateMod());
                                        EFclose(handle);
                                }
                        }
                }
        }
}

void EFModLoader::Load::unMod(const size_t &id) {
        EFLOG(INFO, "卸载Mod", "卸载的ModID:", id);
        auto& modList = EFModLoader::Load::mod;
        modList.erase(
                std::remove_if(modList.begin(), modList.end(),
                               [id](const auto& mod) {
                                   if (id == mod.id) {
                                           EFLOG(INFO, "搜索Mod", "已找到目标Mod");
                                           if (mod.Instance != nullptr) {
                                                   delete(mod.Instance);
                                                   EFLOG(INFO, "卸载Mod", "已释放类指针");
                                           }
                                           EFclose(mod.loaded);
                                           EFLOG(INFO, "卸载Mod", "已关闭Mod句柄");
                                           return true;
                                   }
                                   return false;
                               }),
                modList.end()
        );
        EFLOG(INFO, "卸载Mod", "已卸载Mod:", id);
}


void EFModLoader::Load::addLoadedMod(EFModLoader::Load::Mod &&newMod, std::mutex &mtx) {
        std::lock_guard<std::mutex> lock(mtx);
        mod.push_back(std::move(newMod));
}

bool EFModLoader::Load::isModIDLoaded(const std::unordered_set<size_t> &loadedModIDs, size_t modID,
                                      std::mutex &mtx) {
        std::lock_guard<std::mutex> lock(mtx);
        return loadedModIDs.find(modID) != loadedModIDs.end();
}

void EFModLoader::Load::loadSingleMod(const std::filesystem::path &Path,
                                      std::unordered_set<size_t> &loadedModIDs,
                                      std::mutex &modsMtx) {
        
        for (const auto& entry : std::filesystem::recursive_directory_iterator(Path / "lib")) {
                if (entry.is_regular_file()) {
                        EFLOG(INFO, "加载Mod", "尝试加载Mod:", entry);
                        {
                                void *handle = EFopen(entry.path().c_str());
                                if (!handle) {
                                        EFLOG(ERROR, "获取Mod句柄", "句柄为空指针！");
                                        return;
                                }
                                
                                EFMod *(*CreateMod)();
                                CreateMod = (EFMod *(*)()) EFgetsym(handle, "CreateMod");
                                if (!CreateMod) {
                                        EFLOG(ERROR, "创建Mod",
                                              "构建函数为空指针！\n正在尝试关闭Mod:",
                                              entry);
                                        EFclose(handle);
                                        return;
                                }
                                
                                auto modInstance = CreateMod();
                                auto modStandard = modInstance->standard;
                                EFLOG(INFO, "创建Mod", "Mod开发标准:", modStandard);
                                
                                if (modStandard >= 20250101) {
                                        auto modInfo = modInstance->getInfo();
                                        auto ModID = std::hash<std::string>{}(
                                                modInfo.name + modInfo.author);
                                        
                                        if (isModIDLoaded(loadedModIDs, ModID, modsMtx)) {
                                                EFLOG(ERROR, "创建Mod",
                                                      "已创建相同的Mod\n停止加载Mod:", entry);
                                                EFclose(handle);
                                                return;
                                        }
                                        
                                        {
                                                std::lock_guard<std::mutex> lock(modsMtx);
                                                loadedModIDs.insert(ModID);
                                        }
                                        
                                        modInstance->Data = Path / "private";
                                        EFLOG(INFO, "创建Mod", "已赋予Mod私有目录");
                                        
                                        modInstance->RegisterAPI(&EFModAPI::getEFModAPI());
                                        EFLOG(INFO, "创建Mod", "已调用Mod注册API");
                                        
                                        modInstance->RegisterExtend(&EFModAPI::getEFModAPI());
                                        EFLOG(INFO, "创建Mod", "已调用Mod注册扩展");
                                        
                                        Mod newMod = {
                                                .id = ModID,
                                                .loaded = handle,
                                                .Instance = modInstance,
                                                .loadPath = entry,
                                                .modData = Path / "private",
                                                .info = modInfo
                                        };
                                        addLoadedMod(std::move(newMod), modsMtx);
                                        EFLOG(INFO, "创建Mod", "创建Mod成功:", entry);
                                } else {
                                        EFLOG(ERROR, "创建Mod", "未知开发标准");
                                        free(modInstance);
                                        EFclose(handle);
                                }
                        }
                }
        }
}

void EFModLoader::Load::loadModsAsync(const std::filesystem::path &rootDir) {
        std::vector<std::filesystem::path> entries;
        unsigned int num_threads = std::thread::hardware_concurrency();
        if (num_threads == 0) num_threads = 2;
        for (const auto& entry : std::filesystem::directory_iterator(rootDir)) {
                if (std::filesystem::is_directory(entry.status())) {
                        entries.push_back(entry.path());
                }
        }
        
        if (entries.size() <= num_threads) {
                for (const auto& path : entries) {
                        loadMod(path);
                }
                return;
        }
        
        std::unordered_set<size_t> loadedModIDs;
        std::mutex modsMtx;
        
        std::vector<std::future<void>> futures;
        futures.reserve(num_threads);
        
        for (unsigned int i = 0; i < num_threads; ++i) {
                futures.emplace_back(std::async(std::launch::async, [&entries, &loadedModIDs, &modsMtx, i, num_threads]() {
                    for (size_t j = i; j < entries.size(); j += num_threads) {
                            loadSingleMod(entries[j], loadedModIDs, modsMtx);
                    }
                }));
        }
        
        for (auto& fut : futures) {
                fut.get();
        }
}

void EFModLoader::Load::initiate() {
        
        unsigned int num_threads = std::thread::hardware_concurrency();
        if (num_threads == 0) num_threads = 2;
        
        if (mod.size() <= num_threads) {
                for (const auto& m : mod) {
                        EFLOG(INFO, "Mod初始化", "运行Mod:", m.loadPath, "\n返回值:", m.Instance->run(&EFModAPI::getEFModAPI()));
                }
                return;
        }
        
        std::mutex modsMtx;
        std::vector<std::future<void>> futures;
        futures.reserve(num_threads);
        
        for (unsigned int i = 0; i < num_threads; ++i) {
                futures.emplace_back(std::async(std::launch::async, [i, num_threads]() {
                    for (size_t j = i; j < mod.size(); j += num_threads) {
                            auto& m = mod[j];
                            EFLOG(INFO, "Mod初始化", "初始化的Mod:", m.loadPath, "\n返回值:", m.Instance->run(&EFModAPI::getEFModAPI()));
                    }
                }));
        }
        
        for (auto& fut : futures) {
                fut.get();
        }
}