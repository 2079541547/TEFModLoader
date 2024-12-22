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


// 辅助函数，获取当前进程的内存使用情况
static std::string GetMemoryUsage() {
    struct mallinfo mi = mallinfo();
    return "Total allocated: " + std::to_string(mi.uordblks) + " bytes, Total free: " + std::to_string(mi.fordblks) + " bytes";
}

// 辅助函数，获取当前时间
static std::string GetCurrentTime() {
    auto now = std::chrono::system_clock::now();
    auto in_time_t = std::chrono::system_clock::to_time_t(now);
    std::stringstream ss;
    ss << std::put_time(std::localtime(&in_time_t), "%Y-%m-%d %X");
    return ss.str();
}

void EFModLoader::Loader::LoadELFMods::LoadMod(const std::string &LibPath) {
    // 记录加载前的内存使用情况
    EFLOG(LogLevel::INFO, "Loader", "LoadELFMods", "LoadMod", "加载前内存使用情况: " + GetMemoryUsage());

    auto start = std::chrono::high_resolution_clock::now();

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

    // 注册API
    EFLOG(LogLevel::INFO, "Loader", "LoadELFMods", "LoadMod", "开始注册API...");
    RegisterApi::Register(); // 注册API

    // 注册Hook
    mod->RegisterHooks();
    EFLOG(LogLevel::INFO, "Loader", "LoadELFMods", "LoadMod", "已获取Mod注册的Hook");

    // 初始化模组
    if (!mod->Initialize()) {
        const char* err = dlerror();
        EFLOG(LogLevel::ERROR, "Loader", "LoadELFMods", "LoadMod", "Mod初始化失败：" + std::string(err));
        dlclose(handle);
        return;
    }

    auto end = std::chrono::high_resolution_clock::now();
    auto duration = std::chrono::duration_cast<std::chrono::milliseconds>(end - start).count();

    // 记录加载后的内存使用情况
    EFLOG(LogLevel::INFO, "Loader", "LoadELFMods", "LoadMod", "加载后内存使用情况: " + GetMemoryUsage());
    EFLOG(LogLevel::INFO, "Loader", "LoadELFMods", "LoadMod", "已加载Mod：" + LibPath + ", 耗时: " + std::to_string(duration) + " ms");
}

void EFModLoader::Loader::LoadELFMods::LoadModX(JNIEnv *env, const std::string &LibPath) {
    // 记录加载前的内存使用情况
    EFLOG(LogLevel::INFO, "Loader", "LoadELFMods", "LoadModX", "加载前内存使用情况: " + GetMemoryUsage());

    auto start = std::chrono::high_resolution_clock::now();

    // 查找System类
    jclass systemClass = env->FindClass("java/lang/System");
    if (systemClass == nullptr) {
        // 处理错误
        return;
    }

    // 获取System.load方法ID
    jmethodID loadMethod = env->GetStaticMethodID(systemClass, "load", "(Ljava/lang/String;)V");
    if (loadMethod == nullptr) {
        // 处理错误
        return;
    }

    // 创建一个Java String对象，代表库文件路径
    jstring libPathStr = env->NewStringUTF(LibPath.c_str());

    // 调用System.load方法
    env->CallStaticVoidMethod(systemClass, loadMethod, libPathStr);

    // 释放本地引用
    env->DeleteLocalRef(libPathStr);
    env->DeleteLocalRef(systemClass);

    auto end = std::chrono::high_resolution_clock::now();
    auto duration = std::chrono::duration_cast<std::chrono::milliseconds>(end - start).count();

    // 记录加载后的内存使用情况
    EFLOG(LogLevel::INFO, "Loader", "LoadELFMods", "LoadModX", "加载后内存使用情况: " + GetMemoryUsage());
    EFLOG(LogLevel::INFO, "Loader", "LoadELFMods", "LoadModX", "已加载Mod：" + LibPath + ", 耗时: " + std::to_string(duration) + " ms");
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
        
        // 如果路径不是一个目录，则报错并退出
        if (!filesystem::is_directory(LibPath)) {
                EFLOG(LogLevel::ERROR, "Loader", "LoadELFMods", "LoadALLMod", "提供的路径不是一个目录: " + LibPath);
                return;
        }
        
        // 记录开始遍历目录
        EFLOG(LogLevel::INFO, "Loader", "LoadELFMods", "LoadALLMod", "开始遍历目录: " + LibPath);
        
        // 使用递归迭代器遍历目录和所有子目录中的所有文件
        std::uintmax_t processedFiles = 0;
        try {
                for (const auto& entry : filesystem::recursive_directory_iterator(LibPath)) {
                        if (entry.is_regular_file() && entry.path().extension() == ".so") { // 只处理.so文件
                                std::string filePath = entry.path().string();
                                
                                // 更新进度
                                processedFiles++;
                                std::string progress = "正在加载Mod (" + std::to_string(processedFiles) + "): " + filePath;
                                EFLOG(LogLevel::INFO, "Loader", "LoadELFMods", "LoadALLMod", progress);
                                
                                // 尝试加载Mod
                                LoadMod(filePath);
                                
                                // 记录每个Mod加载后的内存使用情况
                                EFLOG(LogLevel::INFO, "Loader", "LoadELFMods", "LoadALLMod", "Mod加载后内存使用情况: " + GetMemoryUsage());
                        }
                }
        } catch (const filesystem::filesystem_error& e) {
                EFLOG(LogLevel::ERROR, "Loader", "LoadELFMods", "LoadALLMod", "遍历或加载过程中出现错误: " + std::string(e.what()));
        }
        
        // 记录遍历结束
        EFLOG(LogLevel::INFO, "Loader", "LoadELFMods", "LoadALLMod", "目录遍历结束，共加载了 " + std::to_string(processedFiles) + " 个Mod");
        
        // 如果没有找到任何.so文件，也记录一下
        if (processedFiles == 0) {
                EFLOG(LogLevel::INFO, "Loader", "LoadELFMods", "LoadALLMod", "未找到任何.so文件进行加载");
        }
}

void EFModLoader::Loader::LoadELFMods::LoadALLModX(JNIEnv *env, const std::string &LibPath) {
    // 检查路径是否为空
    if (LibPath.empty()) {
        EFLOG(LogLevel::WARN, "Loader", "LoadELFMods", "LoadALLModX", "提供的路径为空");
        return;
    }

    // 检查路径是否存在
    if (!filesystem::exists(LibPath)) {
        EFLOG(LogLevel::WARN, "Loader", "LoadELFMods", "LoadALLModX", "提供的路径不存在: " + LibPath);
        return;
    }

    // 如果路径是一个文件而不是目录，也可以选择处理这种情况
    if (!filesystem::is_directory(LibPath)) {
        EFLOG(LogLevel::ERROR, "Loader", "LoadELFMods", "LoadALLModX", "提供的路径不是一个目录: " + LibPath);
        return;
    }

    // 记录开始遍历目录
    EFLOG(LogLevel::INFO, "Loader", "LoadELFMods", "LoadALLModX", "开始遍历目录: " + LibPath);

    // 获取目录中的文件总数
    std::uintmax_t totalFiles = std::distance(filesystem::directory_iterator(LibPath), filesystem::directory_iterator());
    if (totalFiles == 0) {
        EFLOG(LogLevel::INFO, "Loader", "LoadELFMods", "LoadALLModX", "目录中没有文件");
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
            EFLOG(LogLevel::INFO, "Loader", "LoadELFMods", "LoadALLModX", progress);

            // 尝试加载Mod
            LoadModX(env, filePath);

            // 记录每个Mod加载后的内存使用情况
            EFLOG(LogLevel::INFO, "Loader", "LoadELFMods", "LoadALLModX", "Mod加载后内存使用情况: " + GetMemoryUsage());
        }
    }

    // 记录遍历结束
    EFLOG(LogLevel::INFO, "Loader", "LoadELFMods", "LoadALLModX", "目录遍历结束");
}