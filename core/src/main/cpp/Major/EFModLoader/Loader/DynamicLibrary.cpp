//
// Created by eternalfuture on 2024/9/17.
//

#include <iostream>
#include <string>
#include <vector>
#include <dlfcn.h>
#include <dirent.h>
#include <sys/stat.h>
#include <unistd.h>
#include <filesystem>
#include "../include/IMod.h"
#include "../include/IModLoaderAPI.h"
#include "../include/LOG.h"
#include <cassert>

namespace DynamicLibrary {

    using namespace std;


    // 存储已经加载的模组实例
    static unordered_map<string, IMod*> loadedMods;

    // 加载单个Mod
    void LoadMod(const string& LibPath) {
        void* handle = dlopen(LibPath.c_str(), RTLD_LAZY);
        if (!handle) {
            MainLOGS::LOG("Error", "DynamicLibrary", "LoadMod", "无法加载Mod：" + LibPath);
            return;
        }

        // 获取模组实例
        IMod* (*getModInstance)();
        getModInstance = (IMod* (*)())dlsym(handle, "GetModInstance");
        if (!getModInstance) {
            MainLOGS::LOG("Error", "DynamicLibrary", "LoadMod", "Mod中没有GetModInstance函数");
            dlclose(handle);
            return;
        }

        IMod* mod = getModInstance();
        assert(mod && "Mod instance is null");

        // 提供API集合给模组
        mod->ProvideAPI(&ModLoaderAPI::GetAPI());

        if (!mod->Initialize()) {
            MainLOGS::LOG("Error", "DynamicLibrary", "LoadMod", "Mod初始化失败！");
            dlclose(handle);
            return;
        }

        // 存储模组实例
        loadedMods[mod->GetIdentifier()] = mod;

        // 自动注册扩展函数
        mod->RegisterHooks();

        MainLOGS::LOG("Info", "DynamicLibrary", "LoadMod", "已加载Mod：" + LibPath);
    }

    // 加载所有Mod
    void LoadALLMod(const string& LibPath) {
        // 遍历目录下的所有文件
        for (const auto& entry : std::filesystem::directory_iterator(LibPath)) {
            if (entry.is_regular_file()) {

                string filePath = entry.path().string();

                MainLOGS::LOG("Info", "DynamicLibrary", "LoadALLMod", "正在尝试加载Mod：" + filePath);
                LoadMod(filePath);
            }
        }
    }

    // 解除所有Mod
    void UnALLMod() {
        for (auto& pair : loadedMods) {
            pair.second->Shutdown();
            delete pair.second;
        }
        loadedMods.clear();

        MainLOGS::LOG("Info", "DynamicLibrary", "UnALLMod", "已关闭所有Mod");
    }

    // 解除单个Mod
    void UnMod(const string& libName) {
        auto it = loadedMods.find(libName);
        if (it != loadedMods.end()) {
            it->second->Shutdown();
            delete it->second;
            loadedMods.erase(it);
            MainLOGS::LOG("Info", "DynamicLibrary", "UnMod", "已关闭Mod：" + libName);
        } else {
            MainLOGS::LOG("Warn", "DynamicLibrary", "UnMod", "找不到需要关闭的Mod：" + libName);
        }
    }
}