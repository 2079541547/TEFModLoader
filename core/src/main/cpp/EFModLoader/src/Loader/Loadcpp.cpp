//
// Created by eternalfuture on 2024/9/22.
//
#include <Loader/Loadcpp.hpp>
#include "LOG/MainLOGS.hpp"
#include <API/register.hpp>

void Loadcpp::LoadMod(const string &LibPath) {
    void* handle = dlopen(LibPath.c_str(), RTLD_LAZY);
    if (!handle) {
        MainLOGS::LOG("Error", "DynamicLibrary", "LoadMod", "无法加载Mod：" + LibPath);
        return;
    }

    // 获取模组实例
    EFMod* (*getModInstance)();
    getModInstance = (EFMod* (*)())dlsym(handle, "GetModInstance");
    if (!getModInstance) {
        MainLOGS::LOG("Error", "DynamicLibrary", "LoadMod", "Mod中没有GetModInstance函数");
        dlclose(handle);
        return;
    }

    EFMod* mod = getModInstance();
    assert(mod && "Mod instance is null");

    // 提供API集合给模组
    mod->LoadEFMod(&EFModLoaderAPI::GetEFModLoader());

    // 存储模组实例
    loadedMods[mod->GetIdentifier()] = mod;

    // 自动注册扩展函数
    mod->RegisterAPIs();
    mod->RegisterHooks();

    RegisterApi::Register(); //注册API，（加载之前的API不能使用BNM库！）

    if (!mod->Initialize()) {
        MainLOGS::LOG("Error", "DynamicLibrary", "LoadMod", "Mod初始化失败！");
        dlclose(handle);
        return;
    }

    MainLOGS::LOG("Info", "DynamicLibrary", "LoadMod", "已加载Mod：" + LibPath);
}



// 加载所有Mod
void Loadcpp::LoadALLMod(const string& LibPath) {
    // 遍历目录下的所有文件
    for (const auto& entry : std::filesystem::directory_iterator(LibPath)) {
        if (entry.is_regular_file()) {

            string filePath = entry.path().string();

            MainLOGS::LOG("Info", "DynamicLibrary", "LoadALLMod", "正在尝试加载Mod：" + filePath);
            LoadMod(filePath);
        }
    }
}
