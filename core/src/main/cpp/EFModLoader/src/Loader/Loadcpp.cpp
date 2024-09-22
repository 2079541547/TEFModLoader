//
// Created by eternalfuture on 2024/9/22.
//
#include <Loader/Loadcpp.hpp>
#include "LOG/MainLOGS.hpp"
#include <API/register.hpp>

void Loadcpp::LoadMod(const std::string &LibPath) {
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

    RegisterApi::Register();


    mod->RegisterHooks();


    if (!mod->Initialize()) {
        MainLOGS::LOG("Error", "DynamicLibrary", "LoadMod", "Mod初始化失败！");
        dlclose(handle);
        return;
    }

    MainLOGS::LOG("Info", "DynamicLibrary", "LoadMod", "已加载Mod：" + LibPath);
}