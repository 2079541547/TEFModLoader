/*******************************************************************************
 * 文件名称: loader
 * 项目名称: EFModLoader
 * 创建时间: 2025/2/11
 * 作者: EternalFuture
 * Github: https://github.com/2079541547
 * 版权声明: Copyright © 2024 EternalFuture. All rights reserved.
 * 许可证: Licensed under the AGPLv3 License (the "License");
 *         you may not use this file except in compliance with the License.
 *         You may obtain a copy of the License at
 *
 *         http://www.gnu.org/licenses/agpl-3.0.html
 *
 *         Unless required by applicable law or agreed to in writing, software
 *         distributed under the License is distributed on an "AS IS" BASIS,
 *         WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *         See the License for the specific language governing permissions and
 *         limitations under the License.
 *
 * 描述信息: 本文件为EFModLoader项目中的一部分，允许在遵守AGPLv3许可的条件下自由用于商业用途。
 * 注意事项: 请严格遵守AGPLv3协议使用本代码。AGPLv3要求您公开任何对原始软件的修改版本，并让这些修改也受到相同的许可证约束，即使是在通过网络交互的情况下。
 *******************************************************************************/

#include "EFModLoader/loader.hpp"
#include <utility>
#include <vector>
#include <unordered_set>
#include <memory>
#include <string>
#include <filesystem>
#include <thread>
#include <future>
#include <mutex>
#include <algorithm>

void EFModLoader::Loader::loadAMod(const std::filesystem::path& filePath, std::filesystem::path privateDir) {

    if (!exists(filePath)) {
        return;
    }

    void* handle = efopen(filePath.c_str());
    if (!handle) {
        return;
    } else {
        EFMod* (*CreateMod)();
        CreateMod = (EFMod* (*)())efsym(handle, "CreateMod");
        if (!CreateMod) {
            efclose(handle);
            return;
        }
        auto modStandard = CreateMod()->standard;

        if (modStandard == 20250211) {

            auto ModID = std::hash<std::string>{}(CreateMod()->getInfo().name + CreateMod()->getInfo().author);

            std::shared_ptr<Mod> newMod(new Mod());
            for (const auto& _: mod) {
                if (ModID == _.id) {
                    return;
                }
            }

            CreateMod()->Register(&EFModAPI::getEFModAPI(), privateDir);
            newMod->id = ModID;
            newMod->loaded = handle;
            newMod->loadPath = filePath;
            newMod->Instance = CreateMod();
            newMod->info = CreateMod()->getInfo();
            mod.push_back(*newMod);
        } else {
            free(CreateMod());
            efclose(handle);
        }
    }
}

void EFModLoader::Loader::loadMod(const std::filesystem::path &path, const std::filesystem::path& privateDir) {
    if (!exists(path)) {
        return;
    }
    for (const auto& entry : std::filesystem::recursive_directory_iterator(path)) {
        if (entry.is_regular_file()) {
            void* handle = efopen(entry.path().c_str());
            if (!handle) {
                return;
            } else {
                EFMod* (*CreateMod)();
                CreateMod = (EFMod* (*)())efsym(handle, "CreateMod");
                if (!CreateMod) {
                    efclose(handle);
                    return;
                }
                auto modStandard = CreateMod()->standard;

                if (modStandard == 20250211) {

                    auto ModID = std::hash<std::string>{}(CreateMod()->getInfo().name + CreateMod()->getInfo().author);

                    std::shared_ptr<Mod> newMod(new Mod());
                    for (const auto& _: mod) {
                        if (ModID == _.id) {
                            efclose(handle);
                            return;
                        }
                    }

                    CreateMod()->Register(&EFModAPI::getEFModAPI(), privateDir);
                    newMod->loaded = handle;
                    newMod->loadPath = entry.path();
                    newMod->Instance = CreateMod();
                    newMod->info = CreateMod()->getInfo();
                    mod.push_back(*newMod);
                } else {
                    free(CreateMod());
                    efclose(handle);
                }
            }
        }
    }
}

void EFModLoader::Loader::loadSingleMod(const std::filesystem::path &path, const std::filesystem::path &privateDir, std::unordered_set <size_t> &loadedModIDs,
                                        std::mutex &modsMtx) {

    for (const auto& entry : std::filesystem::recursive_directory_iterator(path)) {
        if (entry.is_regular_file()) {
            {
                void *handle = efopen(entry.path().c_str());
                if (!handle) {
                    return;
                }

                EFMod *(*CreateMod)();
                CreateMod = (EFMod *(*)()) efsym(handle, "CreateMod");
                if (!CreateMod) {
                    efclose(handle);
                    return;
                }

                auto modInstance = CreateMod();
                auto modStandard = modInstance->standard;

                if (modStandard == 20250211) {
                    auto modInfo = modInstance->getInfo();
                    auto ModID = std::hash<std::string>{}(
                            modInfo.name + modInfo.author);

                    if (isModIDLoaded(loadedModIDs, ModID, modsMtx)) {
                        efclose(handle);
                        return;
                    }

                    {
                        std::lock_guard<std::mutex> lock(modsMtx);
                        loadedModIDs.insert(ModID);
                    }

                    modInstance->Register(&EFModAPI::getEFModAPI(), privateDir);

                    Mod newMod = {
                            .id = ModID,
                            .loaded = handle,
                            .Instance = modInstance,
                            .loadPath = entry,
                            .modData = privateDir,
                            .info = modInfo
                    };
                    addLoadedMod(std::move(newMod), modsMtx);
                } else {
                    free(modInstance);
                    efclose(handle);
                }
            }
        }
    }
}

void EFModLoader::Loader::unMod(const size_t &id) {
    auto& modList = EFModLoader::Loader::mod;
    modList.erase(
            std::remove_if(modList.begin(), modList.end(),
                           [id](const auto& mod) {
                               if (id == mod.id) {
                                   if (mod.Instance != nullptr) {
                                       delete(mod.Instance);
                                   }
                                   efclose(mod.loaded);
                                   return true;
                               }
                               return false;
                           }),
            modList.end()
    );
}


void EFModLoader::Loader::addLoadedMod(EFModLoader::Loader::Mod &&newMod, std::mutex &mtx) {
    std::lock_guard<std::mutex> lock(mtx);
    mod.push_back(std::move(newMod));
}

bool EFModLoader::Loader::isModIDLoaded(const std::unordered_set<size_t> &loadedModIDs, size_t modID,
                                      std::mutex &mtx) {
    std::lock_guard<std::mutex> lock(mtx);
    return loadedModIDs.find(modID) != loadedModIDs.end();
}

void EFModLoader::Loader::loadMods(const std::filesystem::path& dirPath, const std::filesystem::path& privateDirs) {
    if (!std::filesystem::exists(dirPath)) return;

    std::vector<std::pair<std::filesystem::path, std::filesystem::path>> entries;
    unsigned int num_threads = std::thread::hardware_concurrency();
    if (num_threads == 0) num_threads = 2;
    for (const auto& entry : std::filesystem::directory_iterator(dirPath)) {
        if (std::filesystem::is_directory(entry.status())) {
            entries.emplace_back(entry.path() , privateDirs / entry.path().filename() / "private" );
        }
    }

    if (entries.size() <= num_threads) {
        for (const auto& path : entries) {
            loadMod(path.first, path.second);
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
                loadSingleMod(entries.at(j).first, entries.at(j).second, loadedModIDs, modsMtx);
            }

        }));
    }

    for (auto& fut : futures) {
        fut.get();
    }
}

void EFModLoader::Loader::initiate() {

    unsigned int num_threads = std::thread::hardware_concurrency();
    if (num_threads == 0) num_threads = 2;

    if (mod.size() <= num_threads) {
        for (const auto& m : mod) {
            m.Instance->initialize(&EFModAPI::getEFModAPI(), m.modData);
        }
        return;
    }

    std::vector<std::future<void>> futures;
    futures.reserve(num_threads);

    for (unsigned int i = 0; i < num_threads; ++i) {
        futures.emplace_back(std::async(std::launch::async, [i, num_threads]() {
            for (size_t j = i; j < mod.size(); j += num_threads) {
                auto& m = mod[j];
                m.Instance->initialize(&EFModAPI::getEFModAPI(), m.modData);
            }
        }));
    }

    for (auto& fut : futures) {
        fut.get();
    }
}