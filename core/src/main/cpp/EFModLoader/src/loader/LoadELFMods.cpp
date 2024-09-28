//
// Created by eternalfuture on 2024/9/28.
//

#include <loader/LoadELFMods.hpp>
#include <api/RegisterApi.hpp>
#include <log.hpp>

void EFModLoader::Loader::LoadELFMods::LoadMod(const std::string &LibPath) {
    void* handle = dlopen(LibPath.c_str(), RTLD_LAZY);
    if (!handle) {
        const char* err = dlerror();
        EFModLoader::Log::LOG("Error", "Loader", "LoadELFMods", "LoadMod", "无法加载Mod：" + LibPath + ", 错误信息：" + std::string(err));
        return;
    }

    // 获取模组实例
    EFMod* (*getModInstance)();
    getModInstance = (EFMod* (*)())dlsym(handle, "GetModInstance");
    if (!getModInstance) {
        const char* err = dlerror();
        EFModLoader::Log::LOG("Error", "Loader", "LoadELFMods", "LoadMod", "Mod中没有GetModInstance函数, 错误信息：" + std::string(err));
        dlclose(handle);
        return;
    }

    EFMod* mod = getModInstance();
    assert(mod && "Mod instance is null");

    // 提供API集合给模组
    mod->LoadEFMod(&EFModLoaderAPI::GetEFModLoader());
    EFModLoader::Log::LOG("Debug", "Loader", "LoadELFMods", "LoadMod", "正在获取Mod信息...");

    // 存储模组实例
    loadedMods[mod->GetIdentifier()] = mod;
    EFModLoader::Log::LOG("Debug", "Loader", "LoadELFMods", "LoadMod", "已获取Mod标志");

    // 自动注册扩展函数
    mod->RegisterAPIs();
    EFModLoader::Log::LOG("Debug", "Loader", "LoadELFMods", "LoadMod", "已获取Mod注册的API");
    mod->RegisterHooks();
    EFModLoader::Log::LOG("Debug", "Loader", "LoadELFMods", "LoadMod", "已获取Mod注册的Hook");


    EFModLoader::Log::LOG("Debug", "Loader", "LoadELFMods", "LoadMod", "开始注册API...");
    RegisterApi::Register(); //注册API，（加载之前的API不能使用BNM库！）

    if (!mod->Initialize()) {
        const char* err = dlerror();
        EFModLoader::Log::LOG("Error", "Loader", "LoadELFMods", "LoadMod", "Mod初始化失败：" + std::string(err));
        dlclose(handle);
        return;
    }

    EFModLoader::Log::LOG("Info", "Loader", "LoadELFMods", "LoadMod", "已加载Mod：" + LibPath);
}


void EFModLoader::Loader::LoadELFMods::LoadALLMod(const std::string &LibPath) {
    // 检查路径是否为空
    if (LibPath.empty()) {
        EFModLoader::Log::LOG("Warning", "Loader", "LoadELFMods", "LoadALLMod", "提供的路径为空");
        return;
    }

    // 检查路径是否存在
    if (!filesystem::exists(LibPath)) {
        EFModLoader::Log::LOG("Warning", "Loader", "LoadELFMods", "LoadALLMod", "提供的路径不存在: " + LibPath);
        return;
    }

    // 如果路径是一个文件而不是目录，也可以选择处理这种情况
    if (!filesystem::is_directory(LibPath)) {
        EFModLoader::Log::LOG("Error", "Loader", "LoadELFMods", "LoadALLMod", "提供的路径不是一个目录: " + LibPath);
        return;
    }

    // 记录开始遍历目录
    EFModLoader::Log::LOG("Info", "Loader", "LoadELFMods", "LoadALLMod", "开始遍历目录: " + LibPath);

    // 获取目录中的文件总数
    std::uintmax_t totalFiles = std::distance(filesystem::directory_iterator(LibPath), filesystem::directory_iterator());
    if (totalFiles == 0) {
        EFModLoader::Log::LOG("Info", "Loader", "LoadELFMods", "LoadALLMod", "目录中没有文件");
        return;
    }

    // 遍历目录下的所有文件
    std::uintmax_t processedFiles = 0;
    for (const auto& entry : filesystem::directory_iterator(LibPath)) {
        if (entry.is_regular_file()) {
            std::string filePath = entry.path().string();

            // 更新进度
            processedFiles++;
            std::string progress = "正在加载Mod (" + std::to_string(processedFiles) + "/" + std::to_string(totalFiles) + "): " + filePath;
            EFModLoader::Log::LOG("Info", "Loader", "LoadELFMods", "LoadALLMod", progress);

            // 尝试加载Mod
            LoadMod(filePath);

        }
    }

    // 记录遍历结束
    EFModLoader::Log::LOG("Info", "Loader", "LoadELFMods", "LoadALLMod", "目录遍历结束");
}