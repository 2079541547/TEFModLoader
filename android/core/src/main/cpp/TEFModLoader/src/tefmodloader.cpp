/*******************************************************************************
 * 文件名称: tefmodloader
 * 项目名称: TEFModLoader
 * 创建时间: 2025/5/17
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
 * 注意事项: 请严格遵守GNU AGPL v3.0协议使用本代码，任何未经授权的商业用途均属侵权行为。
 *******************************************************************************/

#include <jni.h>
#include <filesystem>

#include "BNM/Loading.hpp"

#include "efmodloader/loader.hpp"
#include "efmodloader/multi_channel.hpp"
#include "efmodloader/logger.hpp"

#include "api_manager.hpp"
#include "hook_manager.hpp"
#include "logger.hpp"

#include "item_manager.hpp"
#include "set_factory.hpp"
#include "texture_assets.hpp"
#include "initialize_almost_everything.hpp"
#include "save_player.hpp"

#include "tefmod-api/base_type.hpp"
#include "tefmod-api/debug_tool.hpp"
#include "tefmod-api/mod_logger.hpp"
#include "tefmod-api/tefmod.hpp"
#include "tefmod-api/item.hpp"

static EFModLoader::Loader loader(
        [](const std::string& path) -> void* {
            LOGF_INFO("加载so文件: {}", path);
            return dlopen(path.c_str(), RTLD_LAZY | RTLD_LOCAL);
        },
        [](void* handle) {
            LOGF_INFO("关闭句柄: {}", handle);
            dlclose(handle);
        },
        [](void* handle, const std::string& name) -> void* {
            LOGF_INFO("从 {} 查找函数 {}", handle, name);
            return dlsym(handle, name.c_str());
        }
);

std::filesystem::path getModDir() {
    Dl_info dl_info;
    std::filesystem::path r;

    if (dladdr((void*)getModDir, &dl_info)) {
        if (dl_info.dli_fname) {

            std::filesystem::path d(dl_info.dli_fname);
            r = d.parent_path() / "Mod";
        }
    }

    if (r.empty()) {
        return "error";
    }

    return r;
}

void init(JNIEnv *env) {
    TEFModLoader::Logger::Init();

    jclass stateClass = env->FindClass("eternal/future/State");
    if (stateClass != nullptr) {
        jfieldID modeFieldID = env->GetStaticFieldID(stateClass, "Mode", "I");
        jfieldID efmodcFieldID = env->GetStaticFieldID(stateClass, "EFMod_c", "Ljava/lang/String;");
        jint modeValue = env->GetStaticIntField(stateClass, modeFieldID);
        auto efmodcValue = (jstring) env->GetStaticObjectField(stateClass, efmodcFieldID);
        std::string modDataPath = env->GetStringUTFChars(efmodcValue, nullptr);

        if (modeValue == 0) {
            TEFModLoader::Logger::Init("TEFModLoader", TEFModLoader::Logger::Level::TRACE, "/sdcard/Documents/TEFModLoader/tefmodloader-runtime.log");
        }

        std::unordered_map<std::string, std::string> modDir;
        for (const auto& entry : std::filesystem::directory_iterator(getModDir())) {
            if (std::filesystem::is_directory(entry.status())) {
                auto privateDir = modDataPath / entry.path().filename() / "private";
                for (const auto& _entry : std::filesystem::recursive_directory_iterator(entry.path())) {
                    if (_entry.is_regular_file()) {
                        modDir[_entry.path().string()] = privateDir.string();
                    }
                }
            }
        }

        EFModLoader::Log::Logger::SetOutputFunction([](const EFModLoader::Log::Record &record) -> void {
            switch (record.level) {
                case EFModLoader::Log::Level::Trace: spdlog::trace("[{}:{} ({})] {}", record.sourceFile, record.sourceLine, record.sourceFunction, record.message);
                    break;
                case EFModLoader::Log::Level::Debug: spdlog::debug("[{}:{} ({})] {}", record.sourceFile, record.sourceLine, record.sourceFunction, record.message);
                    break;
                case EFModLoader::Log::Level::Info: spdlog::info("[{}:{} ({})] {}", record.sourceFile, record.sourceLine, record.sourceFunction, record.message);
                    break;
                case EFModLoader::Log::Level::Warning: spdlog::warn("[{}:{} ({})] {}", record.sourceFile, record.sourceLine, record.sourceFunction, record.message);
                    break;
                case EFModLoader::Log::Level::Error: spdlog::error("[{}:{} ({})] {}", record.sourceFile, record.sourceLine, record.sourceFunction, record.message);
                    break;
                case EFModLoader::Log::Level::Critical: spdlog::critical("[{}:{} ({})] {}", record.sourceFile, record.sourceLine, record.sourceFunction, record.message);
                    break;
                default:
                    spdlog::info("[{}:{} ({})] {}", record.sourceFile, record.sourceLine, record.sourceFunction, record.message);
                    break;
            }
        });
        EFModLoader::Log::Logger::SetMinLevel(EFModLoader::Log::Level::Trace);


        loader.loadBatchAsync(modDir);
    }
}

template <typename T>
void RegisterBaseTypeFunctions(const std::string& typeName) {
    EFModLoader::LoaderMultiChannel::GetInstance()->send(
            "TEFMod::Array<" + typeName + ">::CreateFromVector",
            reinterpret_cast<void*>(TEFModLoader::IL2CPP_Array<T>::CreateFromVector));

    EFModLoader::LoaderMultiChannel::GetInstance()->send(
            "TEFMod::Array<" + typeName + ">::CreateFromPointer",
            reinterpret_cast<void*>(TEFModLoader::IL2CPP_Array<T>::CreateFromPointer));

    EFModLoader::LoaderMultiChannel::GetInstance()->send(
            "TEFMod::Array<" + typeName + ">::ParseFromPointer",
            reinterpret_cast<void*>(TEFModLoader::IL2CPP_Array<T>::ParseFromPointer));

    EFModLoader::LoaderMultiChannel::GetInstance()->send(
            "TEFMod::Field<" + typeName + ">::ParseFromPointer",
            reinterpret_cast<void*>(TEFModLoader::IL2CPP_Field<T>::ParseFromPointer));

    EFModLoader::LoaderMultiChannel::GetInstance()->send(
            "TEFMod::Method<" + typeName + ">::ParseFromPointer",
            reinterpret_cast<void*>(TEFModLoader::IL2CPP_Method<T>::ParseFromPointer));
}

void send_api_to_mod() {
    EFModLoader::LoaderMultiChannel::GetInstance()->send("TEFMod::CreateString", reinterpret_cast<void*>(TEFModLoader::IL2CPP_String::Create));
    EFModLoader::LoaderMultiChannel::GetInstance()->send("TEFMod::ParseStringFromPointer", reinterpret_cast<void*>(TEFModLoader::IL2CPP_String::ParseFromPointer));

    RegisterBaseTypeFunctions<bool>("Bool");
    RegisterBaseTypeFunctions<int8_t>("Byte");
    RegisterBaseTypeFunctions<uint8_t>("SByte");
    RegisterBaseTypeFunctions<int16_t>("Short");
    RegisterBaseTypeFunctions<uint16_t>("UShort");
    RegisterBaseTypeFunctions<int>("Int");
    RegisterBaseTypeFunctions<uint>("UInt");
    RegisterBaseTypeFunctions<long>("Long");
    RegisterBaseTypeFunctions<u_long>("ULong");
    RegisterBaseTypeFunctions<float>("Float");
    RegisterBaseTypeFunctions<double>("Double");
    RegisterBaseTypeFunctions<char>("Char");
    RegisterBaseTypeFunctions<void*>("Other");

    EFModLoader::LoaderMultiChannel::GetInstance()->send(
            "TEFMod::Method<void>::ParseFromPointer",
            reinterpret_cast<void*>(TEFModLoader::IL2CPP_Method<void>::ParseFromPointer));

    EFModLoader::LoaderMultiChannel::GetInstance()->send(
            "TEFMod::Class::ParseFromPointer",
            reinterpret_cast<void*>(TEFModLoader::IL2CPP_Class::ParseFromPointer));


    EFModLoader::LoaderMultiChannel::GetInstance()->send(
            "TEFMod::DebugTool",
            reinterpret_cast<void*>(new TEFModLoader::DebugTool()));

    EFModLoader::LoaderMultiChannel::GetInstance()->send(
            "TEFMod::CreateLogger",
            reinterpret_cast<void*>(TEFModLoader::ModLogger::Logger::CreateLogger));

    EFModLoader::LoaderMultiChannel::GetInstance()->send(
            "TEFMod::TEFModAPI",
            reinterpret_cast<void*>(TEFModLoader::TEFModAPI::GetInstance()));

    EFModLoader::LoaderMultiChannel::GetInstance()->send("TEFMod::Method<Void>::ParseFromPointer",
            reinterpret_cast<void*>(TEFModLoader::IL2CPP_Method<void>::ParseFromPointer));

    EFModLoader::LoaderMultiChannel::GetInstance()->send("TEFMod::ItemManager", TEFModLoader::ItemManager::GetInstance());
}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, [[maybe_unused]] void *reserved) {
    JNIEnv *env;
    vm->GetEnv((void **) &env, JNI_VERSION_1_6);

    init(env);

    BNM::Loading::TryLoadByJNI(env);

    send_api_to_mod();

    TEFModLoader::ItemManager::GetInstance()->registered_unknown("MyMod-EternalFuture::MyItem");

    loader.loadAll();
    TEFModLoader::item_manager::init(TEFModLoader::TEFModAPI::GetInstance());
    TEFModLoader::Initialize_AlmostEverything::init(TEFModLoader::TEFModAPI::GetInstance());
    TEFModLoader::SavePlayer::init(TEFModLoader::TEFModAPI::GetInstance());
    loader.sendAll();

    BNM::Loading::AddOnLoadedEvent(TEFModLoader::APIManager::auto_processing);
    BNM::Loading::AddOnLoadedEvent(TEFModLoader::HookManager::auto_hook);
    BNM::Loading::AddOnLoadedEvent([]() -> void {
        loader.receiveAll();
        TEFModLoader::SetFactory::init();
        TEFModLoader::item_manager::init(TEFModLoader::TEFModAPI::GetInstance());
        TEFModLoader::Initialize_AlmostEverything::init(TEFModLoader::TEFModAPI::GetInstance());
        TEFModLoader::SavePlayer::init(TEFModLoader::TEFModAPI::GetInstance());
    });

    loader.initializeAll();

    return JNI_VERSION_1_6;
}