/*******************************************************************************
 * 文件名称: LoadELFMods
 * 项目名称: TEFModLoader
 * 创建日期: 2024/9/28
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
 * 描述信息: 本文件为EFModLoader项目中的一部分。
 * 注意事项: 请严格遵守GNU AGPL v3.0协议使用本代码，任何未经授权的商业用途均属侵权行为。
 *******************************************************************************/


#include <EFModLoader/loader/LoadELFMods.hpp>
#include <EFModLoader/api/RegisterApi.hpp>
#include <EFModLoader/log.hpp>

void EFModLoader::Loader::LoadELFMods::LoadMod(const std::string &LibPath) {
    void* handle = dlopen(LibPath.c_str(), RTLD_LAZY);
    if (!handle) {
        const char* err = dlerror();
        EFLOG(LogLevel::ERROR, "Loader", "LoadELFMods", "LoadMod", "无法加载Mod：" + LibPath + ", 错误信息：" + std::string(err));
        return;
    }

    // 获取模组实例
    EFMod* (*getModInstance)();
    getModInstance = (EFMod* (*)())dlsym(handle, "GetModInstance");
    if (!getModInstance) {
        const char* err = dlerror();
        EFLOG(LogLevel::ERROR, "Loader", "LoadELFMods", "LoadMod", "Mod中没有GetModInstance函数, 错误信息：" + std::string(err));
        dlclose(handle);
        return;
    }

    EFMod* mod = getModInstance();
    assert(mod && "Mod instance is null");

    // 提供API集合给模组
    mod->LoadEFMod(&EFModLoaderAPI::GetEFModLoader());
    EFLOG(LogLevel::INFO, "Loader", "LoadELFMods", "LoadMod", "正在获取Mod信息...");

    // 存储模组实例
    loadedMods[mod->GetIdentifier()] = mod;
    EFLOG(LogLevel::INFO, "Loader", "LoadELFMods", "LoadMod", "已获取Mod标志");

    // 自动注册扩展函数
    mod->RegisterAPIs();
    EFLOG(LogLevel::INFO, "Loader", "LoadELFMods", "LoadMod", "已获取Mod注册的API");


    EFLOG(LogLevel::INFO, "Loader", "LoadELFMods", "LoadMod", "开始注册API...");
    RegisterApi::Register(); //注册API



    mod->RegisterHooks();
    EFLOG(LogLevel::INFO, "Loader", "LoadELFMods", "LoadMod", "已获取Mod注册的Hook");


    if (!mod->Initialize()) {
        const char* err = dlerror();
        EFLOG(LogLevel::ERROR, "Loader", "LoadELFMods", "LoadMod", "Mod初始化失败：" + std::string(err));
        dlclose(handle);
        return;
    }

    EFLOG(LogLevel::INFO, "Loader", "LoadELFMods", "LoadMod", "已加载Mod：" + LibPath);
}


void EFModLoader::Loader::LoadELFMods::LoadALLMod(const std::string &LibPath) {
    // 检查路径是否为空
    if (LibPath.empty()) {
        EFLOG(LogLevel::WARN, "Loader", "LoadELFMods", "LoadALLMod", "提供的路径为空");
        return;
    }

    // 检查路径是否存在
    if (!filesystem::exists(LibPath)) {
        EFLOG(LogLevel::WARN, "Loader", "LoadELFMods", "LoadALLMod", "提供的路径不存在: " + LibPath);
        return;
    }

    // 如果路径是一个文件而不是目录，也可以选择处理这种情况
    if (!filesystem::is_directory(LibPath)) {
        EFLOG(LogLevel::ERROR, "Loader", "LoadELFMods", "LoadALLMod", "提供的路径不是一个目录: " + LibPath);
        return;
    }

    // 记录开始遍历目录
    EFLOG(LogLevel::INFO, "Loader", "LoadELFMods", "LoadALLMod", "开始遍历目录: " + LibPath);

    // 获取目录中的文件总数
    std::uintmax_t totalFiles = std::distance(filesystem::directory_iterator(LibPath), filesystem::directory_iterator());
    if (totalFiles == 0) {
        EFLOG(LogLevel::INFO, "Loader", "LoadELFMods", "LoadALLMod", "目录中没有文件");
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
            EFLOG(LogLevel::INFO, "Loader", "LoadELFMods", "LoadALLMod", progress);

            // 尝试加载Mod
            LoadMod(filePath);

        }
    }

    // 记录遍历结束
    EFLOG(LogLevel::INFO, "Loader", "LoadELFMods", "LoadALLMod", "目录遍历结束");
}