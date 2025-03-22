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
#include <iostream>

void EFModLoader::Loader::loadAMod(const std::filesystem::path& filePath, std::filesystem::path privateDir) {
    std::cout << "Starting to load mod from file: " << filePath << std::endl;

    if (!std::filesystem::exists(filePath)) {
        std::cerr << "Error: The file does not exist: " << filePath << std::endl;
        return;
    }

    void* handle = efopen(filePath.c_str());
    if (!handle) {
        std::cerr << "Error: Failed to open the file: " << filePath << std::endl;
        return;
    } else {
        std::cout << "Successfully opened file: " << filePath << std::endl;

        EFMod* (*CreateMod)();
        CreateMod = (EFMod* (*)())efsym(handle, "CreateMod");
        if (!CreateMod) {
            std::cerr << "Error: Symbol 'CreateMod' not found in file: " << filePath << std::endl;
            efclose(handle);
            return;
        }
        auto modStandard = CreateMod()->standard;

        std::cout << "Mod standard version: " << modStandard << std::endl;

        if (modStandard == 20250316) {
            auto modInfo = CreateMod()->getInfo();
            auto ModID = std::hash<std::string>{}(modInfo.name + modInfo.author);

            std::shared_ptr<Mod> newMod(new Mod());
            bool modExists = false;
            for (const auto& m : mod) {
                if (ModID == m.id) {
                    std::cerr << "Error: Mod with ID " << ModID << " already exists." << std::endl;
                    modExists = true;
                    break;
                }
            }

            if (modExists) {
                efclose(handle);
                return;
            }

            CreateMod()->Register(&EFModAPI::getEFModAPI(), privateDir);
            std::cout << "Registered mod successfully." << std::endl;

            newMod->id = ModID;
            newMod->loaded = handle;
            newMod->loadPath = filePath;
            newMod->Instance = CreateMod();
            newMod->info = modInfo;

            mod.push_back(*newMod);
            std::cout << "Successfully loaded and added mod with ID: " << ModID << std::endl;
        } else {
            std::cerr << "Error: Unsupported mod standard version: " << modStandard << std::endl;
            free(CreateMod());
            efclose(handle);
        }
    }
}

void EFModLoader::Loader::loadMod(const std::filesystem::path &path, const std::filesystem::path& privateDir) {
    std::cout << "Starting to load mods from directory: " << path << std::endl;

    if (!std::filesystem::exists(path)) {
        std::cerr << "Error: The directory does not exist: " << path << std::endl;
        return;
    }

    for (const auto& entry : std::filesystem::recursive_directory_iterator(path)) {
        if (entry.is_regular_file()) {
            std::cout << "Found regular file: " << entry.path() << std::endl;

            void* handle = efopen(entry.path().c_str());
            if (!handle) {
                std::cerr << "Error: Failed to open the file: " << entry.path() << std::endl;
                continue;
            } else {
                std::cout << "Successfully opened file: " << entry.path() << std::endl;

                EFMod* (*CreateMod)();
                CreateMod = (EFMod* (*)())efsym(handle, "CreateMod");
                if (!CreateMod) {
                    std::cerr << "Error: Symbol 'CreateMod' not found in file: " << entry.path() << std::endl;
                    efclose(handle);
                    continue;
                }
                auto modStandard = CreateMod()->standard;

                std::cout << "Mod standard version: " << modStandard << std::endl;

                if (modStandard == 20250316) {
                    auto modInfo = CreateMod()->getInfo();
                    auto ModID = std::hash<std::string>{}(modInfo.name + modInfo.author);

                    std::shared_ptr<Mod> newMod(new Mod());
                    bool modExists = false;
                    for (const auto& m : mod) {
                        if (ModID == m.id) {
                            std::cerr << "Error: Mod with ID " << ModID << " already exists." << std::endl;
                            modExists = true;
                            break;
                        }
                    }

                    if (modExists) {
                        efclose(handle);
                        continue;
                    }

                    CreateMod()->Register(&EFModAPI::getEFModAPI(), privateDir);
                    std::cout << "Registered mod successfully." << std::endl;

                    newMod->loaded = handle;
                    newMod->loadPath = entry.path();
                    newMod->Instance = CreateMod();
                    newMod->info = modInfo;

                    mod.push_back(*newMod);
                    std::cout << "Successfully loaded and added mod with ID: " << ModID << std::endl;
                } else {
                    std::cerr << "Error: Unsupported mod standard version: " << modStandard << std::endl;
                    free(CreateMod());
                    efclose(handle);
                }
            }
        }
    }
}

void EFModLoader::Loader::loadSingleMod(const std::filesystem::path &path, const std::filesystem::path &privateDir,
                                        std::unordered_set<size_t> &loadedModIDs, std::mutex &modsMtx) {

    std::cout << "Starting to load a single mod from directory: " << path << std::endl;

    for (const auto& entry : std::filesystem::recursive_directory_iterator(path)) {
        if (entry.is_regular_file()) {
            std::cout << "Found regular file: " << entry.path() << std::endl;

            void* handle = efopen(entry.path().c_str());
            if (!handle) {
                std::cerr << "Error: Failed to open the file: " << entry.path() << std::endl;
                return;
            }

            std::cout << "Successfully opened file: " << entry.path() << std::endl;

            EFMod* (*CreateMod)();
            CreateMod = (EFMod* (*)())efsym(handle, "CreateMod");
            if (!CreateMod) {
                std::cerr << "Error: Symbol 'CreateMod' not found in file: " << entry.path() << std::endl;
                efclose(handle);
                return;
            }

            auto modInstance = CreateMod();
            auto modStandard = modInstance->standard;

            std::cout << "Mod standard version: " << modStandard << std::endl;

            if (modStandard == 20250316) {
                auto modInfo = modInstance->getInfo();
                auto ModID = std::hash<std::string>{}(modInfo.name + modInfo.author);

                if (isModIDLoaded(loadedModIDs, ModID, modsMtx)) {
                    std::cerr << "Error: Mod with ID " << ModID << " already exists." << std::endl;
                    efclose(handle);
                    return;
                }

                {
                    std::lock_guard<std::mutex> lock(modsMtx);
                    loadedModIDs.insert(ModID);
                    std::cout << "Inserted mod ID " << ModID << " into loaded set." << std::endl;
                }

                modInstance->Register(&EFModAPI::getEFModAPI(), privateDir);
                std::cout << "Registered mod successfully." << std::endl;

                Mod newMod = {
                        .id = ModID,
                        .loaded = handle,
                        .Instance = modInstance,
                        .loadPath = entry.path(),
                        .modData = privateDir,
                        .info = modInfo
                };

                addLoadedMod(std::move(newMod), modsMtx);
                std::cout << "Successfully loaded and added mod with ID: " << ModID << std::endl;
            } else {
                std::cerr << "Error: Unsupported mod standard version: " << modStandard << std::endl;
                free(modInstance);
                efclose(handle);
            }
        }
    }
}

void EFModLoader::Loader::unMod(const size_t &id) {
    auto& modList = EFModLoader::Loader::mod;
    std::cout << "Attempting to unload mod with ID: " << id << std::endl;

    modList.erase(
            std::remove_if(modList.begin(), modList.end(),
                           [id](const auto& mod) {
                               if (id == mod.id) {
                                   std::cout << "Found mod with ID: " << id << std::endl;
                                   if (mod.Instance != nullptr) {
                                       std::cout << "Deleting mod instance." << std::endl;
                                       delete(mod.Instance);
                                   }
                                   std::cout << "Closing file handle for mod." << std::endl;
                                   efclose(mod.loaded);
                                   return true;
                               }
                               return false;
                           }),
            modList.end()
    );

    std::cout << "Finished unloading mod with ID: " << id << std::endl;
}


void EFModLoader::Loader::addLoadedMod(EFModLoader::Loader::Mod &&newMod, std::mutex &mtx) {
    std::lock_guard<std::mutex> lock(mtx);
    std::cout << "Adding loaded mod with ID: " << newMod.id << std::endl;
    mod.push_back(std::move(newMod));
}

bool EFModLoader::Loader::isModIDLoaded(const std::unordered_set<size_t> &loadedModIDs, size_t modID, std::mutex &mtx) {
    std::lock_guard<std::mutex> lock(mtx);
    bool isLoaded = loadedModIDs.find(modID) != loadedModIDs.end();
    std::cout << "Checking if mod ID " << modID << " is loaded: " << (isLoaded ? "Yes" : "No") << std::endl;
    return isLoaded;
}

void EFModLoader::Loader::loadMods(const std::filesystem::path& dirPath, const std::filesystem::path& privateDirs) {
    if (!std::filesystem::exists(dirPath)) {
        std::cerr << "Error: Directory does not exist: " << dirPath << std::endl;
        return;
    }

    std::vector<std::pair<std::filesystem::path, std::filesystem::path>> entries;
    unsigned int num_threads = std::thread::hardware_concurrency();
    if (num_threads == 0) num_threads = 2;

    std::cout << "Scanning directory for mods: " << dirPath << std::endl;

    for (const auto& entry : std::filesystem::directory_iterator(dirPath)) {
        if (std::filesystem::is_directory(entry.status())) {
            auto privateDir = privateDirs / entry.path().filename() / "private";
            std::cout << "Found directory: " << entry.path() << " with private directory: " << privateDir << std::endl;
            entries.emplace_back(entry.path(), privateDir);
        }
    }

    if (entries.size() <= num_threads) {
        std::cout << "Loading mods sequentially." << std::endl;
        for (const auto& path : entries) {
            loadMod(path.first, path.second);
        }
        return;
    }

    std::unordered_set<size_t> loadedModIDs;
    std::mutex modsMtx;

    std::vector<std::future<void>> futures;
    futures.reserve(num_threads);

    std::cout << "Loading mods in parallel using " << num_threads << " threads." << std::endl;

    for (unsigned int i = 0; i < num_threads; ++i) {
        futures.emplace_back(std::async(std::launch::async, [&entries, &loadedModIDs, &modsMtx, i, num_threads]() {
            std::cout << "Thread " << i << " starting." << std::endl;
            for (size_t j = i; j < entries.size(); j += num_threads) {
                loadSingleMod(entries.at(j).first, entries.at(j).second, loadedModIDs, modsMtx);
            }
            std::cout << "Thread " << i << " finished." << std::endl;
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
            std::cout << "Instance: " << m.Instance << " run the results: " << m.Instance->initialize(&EFModAPI::getEFModAPI(), m.modData) << std::endl;
        }
        return;
    }

    std::vector<std::future<void>> futures;
    futures.reserve(num_threads);

    std::cout << "Initializing mods in parallel using " << num_threads << " threads." << std::endl;

    for (unsigned int i = 0; i < num_threads; ++i) {
        futures.emplace_back(std::async(std::launch::async, [i, num_threads]() {
            std::cout << "Thread " << i << " starting initialization." << std::endl;
            for (size_t j = i; j < mod.size(); j += num_threads) {
                auto& m = mod[j];
                std::cout << "Instance: " << m.Instance << " run the results: " << m.Instance->initialize(&EFModAPI::getEFModAPI(), m.modData) << std::endl;
            }
            std::cout << "Thread " << i << " finished initialization." << std::endl;
        }));
    }

    for (auto& fut : futures) {
        fut.get();
    }
}
