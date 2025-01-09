/*******************************************************************************
 * 文件名称: core
 * 项目名称: EFModLoader
 * 创建时间: 2025/01/09
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

#include <BNM/UserSettings/GlobalSettings.hpp>

//获取应用缓存
#include <jni.h>
#include <string>
#include <filesystem>
std::filesystem::path CacheDir;
std::string getCacheDir(JNIEnv *env) {
        jobject application = nullptr;
        jclass activity_thread_clz = env->FindClass("android/app/ActivityThread");
        if (activity_thread_clz != nullptr) {
                jmethodID get_Application = env->GetStaticMethodID(activity_thread_clz,
                                                                   "currentActivityThread",
                                                                   "()Landroid/app/ActivityThread;");
                if (get_Application != nullptr) {
                        jobject currentActivityThread = env->CallStaticObjectMethod(activity_thread_clz,
                                                                                    get_Application);
                        jmethodID getal = env->GetMethodID(activity_thread_clz, "getApplication",
                                                           "()Landroid/app/Application;");
                        application = env->CallObjectMethod(currentActivityThread, getal);

                        jclass application_clz = env->GetObjectClass(application);
                        jmethodID getCacheDir = env->GetMethodID(application_clz, "getCacheDir",
                                                                 "()Ljava/io/File;");
                        if (getCacheDir != nullptr) {
                                jobject cacheDir = env->CallObjectMethod(application, getCacheDir);
                                jclass file_clz = env->FindClass("java/io/File");
                                jmethodID getPath = env->GetMethodID(file_clz, "getPath", "()Ljava/lang/String;");
                                if (getPath != nullptr) {
                                        return env->GetStringUTFChars((jstring)env->CallObjectMethod(cacheDir, getPath),nullptr);;
                                }
                        }
                }
        }
        return "error";
}





//实现加载函数
#include <EFModLoader/Load/load.hpp>
#include <dlfcn.h>

int EFModLoader::Load::EFclose(void *handle) {
        return dlclose(handle);
}

char *EFModLoader::Load::EFerror() {
        return dlerror();
}

void *EFModLoader::Load::EFgetsym(void *handle, const char *symbol) {
        return dlsym(handle, symbol);
}

void *EFModLoader::Load::EFopen(const char *path) {
        return dlopen(path, RTLD_LAZY);
}


//自动获取API
#include <EFModLoader/Load/Loader.hpp>
#include <EFModLoader/Manager/API.hpp>
#include <BNM/Field.hpp>
#include <BNM/Class.hpp>
#include <EFModLoader/log.hpp>
#include <EFMod/EFMod.hpp>

void EFModLoader::Manager::API::processing() {
        auto ApiDescriptor = EFModAPI::getEFModAPI().getApiDescriptor();
        if (ApiDescriptor.empty()) EFLOG(WARNING, "自动创建API", "收集的数组为空");
        for (const auto& _: ApiDescriptor) {
                if (!_.File.empty()) {
                        if(_.Type == "Field") {
                            size_t dotPosition = _.Class.find('.');
                            BNM::FieldBase* a;
                            if (dotPosition != std::string::npos) {
                                EFLOG(INFO, "自动创建API", "字段于内部类");
                                a = new BNM::FieldBase(BNM::Class(_.Namespace, _.Class.substr(0, dotPosition), BNM::Image(_.File)).GetInnerClass(_.Class.substr(dotPosition + 1)).GetField(_.Name));
                            } else {
                                a = new BNM::FieldBase(BNM::Class(_.Namespace, _.Class, BNM::Image(_.File)).GetField(_.Name));
                            }
                            EFLOG(INFO, "自动创建API", "创建API:", _.getID(), "指针:", (uintptr_t)a);
                            EFModAPI::getEFModAPI().registerAPI(_.getID(), a);
                        }
                }
        }
}

//自动Hook
#include <EFModLoader/Manager/Extend.hpp>
#include <BNM/Method.hpp>
#include <BNM/BasicMonoStructures.hpp>
#include <random>
#include <map>
#include <unordered_map>
#include <memory>
#include <fstream>
#include <sstream>
#include <unistd.h>

typedef void (*createHookFunc)(int mode, int type, std::vector<void*> funPtrs, BNM::Class& c, BNM::MethodBase& method, size_t id, EFModAPI* efmodapi);

enum Type {
    LONG = 0,
    INT = 1,
    VOID = 2,
    BOOL = 3,
    STRING = 4
};

enum Mode {
    INLINE = 0,
    VIRTUAL = 1,
    INVOKE = 2
};

class SharedLibraryManager {
public:
    static SharedLibraryManager& getInstance() {
        static SharedLibraryManager instance;
        return instance;
    }

    void* loadUniqueCopy(const std::string& originalPath) {
        std::ostringstream uniqueName;
        uniqueName << "auxiliary_" << getpid() << "_" << nextIndex++ << ".so";
        std::filesystem::path uniquePath = loaderPath / uniqueName.str();

        if (!std::filesystem::exists(uniquePath)) {
            std::filesystem::copy_file(originalPath, uniquePath);
        }

        void* handle = dlopen(uniquePath.c_str(), RTLD_NOW | RTLD_LOCAL);
        if (!handle) {
            std::cerr << "Cannot open library: " << dlerror() << '\n';
            return nullptr;
        }

        loadedLibraries[uniquePath.string()] = std::shared_ptr<void>(handle, [](void* p) { /* 不关闭句柄 */ });

        if (std::filesystem::exists(uniquePath)) {
            std::filesystem::remove(uniquePath);
        }

        return handle;
    }

private:
    SharedLibraryManager() : loaderPath(CacheDir / "Loader"), nextIndex(1) {}
    ~SharedLibraryManager() = default;

    std::unordered_map<std::string, std::shared_ptr<void>> loadedLibraries;
    std::filesystem::path loaderPath;
    int nextIndex;

    SharedLibraryManager(const SharedLibraryManager&) = delete;
    SharedLibraryManager& operator=(const SharedLibraryManager&) = delete;
};


void EFModLoader::Manager::Extend::processing() {
    for(const auto& funcDesc : EFModAPI::getEFModAPI().getFuncDescriptor()) {
        if (!funcDesc.File.empty()) {
            size_t dotPosition = funcDesc.Class.find('.');
            BNM::MethodBase* a;
            BNM::Class* b;
            if (dotPosition != std::string::npos) {
                b = new BNM::Class(BNM::Class(funcDesc.Namespace, funcDesc.Class.substr(0, dotPosition), BNM::Image(funcDesc.File)).GetInnerClass(funcDesc.Class.substr(dotPosition + 1)));
            } else {
                b = new BNM::Class(BNM::Class(funcDesc.Namespace, funcDesc.Class, BNM::Image(funcDesc.File)));
            }
            a = new BNM::MethodBase(b->GetMethod(funcDesc.Name, funcDesc.Arg));

            void* handle = SharedLibraryManager::getInstance().loadUniqueCopy((CacheDir / "Loader" / "libauxiliary.so").string());
            if (!handle) {
                EFLOG(ERROR, "自动创建hook", "加载共享库失败");
                continue;
            }

            auto createHook = (createHookFunc)dlsym(handle, "createHook");
            if (!createHook) {
                EFLOG(ERROR, "自动创建hook", "解析符号 'createHook' 失败");
                continue;
            }

            std::vector<void*> funPtrs{funcDesc.FunPtr};
            Type type;
            Mode hookMode;

            auto hookT = funcDesc.Type.substr(0, funcDesc.Type.find(">>"));
            auto hookFt = funcDesc.Type.substr(funcDesc.Type.find(">>") + 2);
            if (hookT == "hook") {
                hookMode = Mode::INLINE;
                EFLOG(INFO, "自动创建hook", "内联Hook");
            } else if (hookT == "ihook") {
                hookMode = Mode::INVOKE;
                EFLOG(INFO, "自动创建hook", "引擎Hook");
            } else if (hookT == "vhook"){
                hookMode = Mode::VIRTUAL;
                EFLOG(INFO, "自动创建hook", "虚拟Hook");
            }

            if (hookFt == "void") {
                type = Type::VOID;
                EFLOG(INFO, "自动创建hook", "void类型");
            } else if (hookFt == "int") {
                type = Type::INT;
                EFLOG(INFO, "自动创建hook", "int类型");
            } else if (hookFt == "bool") {
                type = Type::BOOL;
                EFLOG(INFO, "自动创建hook", "bool类型");
            } else if (hookFt == "string") {
                type = Type::STRING;
                EFLOG(INFO, "自动创建hook", "string类型");
            } else if (hookFt == "long") {
                type = Type::LONG;
                EFLOG(INFO, "自动创建hook", "long类型");
            } else {
                EFLOG(ERROR, "自动创建hook", "未知类型:", hookFt);
                delete a;
                delete b;
                continue;
            }

            createHook(static_cast<int>(hookMode),
                       static_cast<int>(type),
                       funPtrs,
                       *b,
                       *a,
                       ModApiDescriptor{
                               funcDesc.File,
                               funcDesc.Namespace,
                               funcDesc.Class,
                               funcDesc.Name,
                               "old_fun"
                       }.getID(),
                       &EFModAPI::getEFModAPI());

            EFLOG(INFO, "自动创建hook", "HOOK创建成功:", funcDesc.getID());

            delete a;
            delete b;
        }
    }
}

//入口
#include <EFModLoader/EFMod/ModApi.hpp>
#include <BNM/Loading.hpp>

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, [[maybe_unused]] void *reserved) {
        JNIEnv *env;
        vm->GetEnv((void **) &env, JNI_VERSION_1_6);

        CacheDir = getCacheDir(env);

        logger.setCacheSize(4096 * 1024);
        logger.setOutput([](const std::string& msg) {
            __android_log_print(ANDROID_LOG_ERROR, "EFModLoader", "%s", msg.c_str());
        });
        logger.setSourceCodeMode(true);
        logger.setLoggingEnabled(true);
        logger.setAutoDumpToFile(false);
        //logger.setLogFile("/storage/emulated/0/Documents/TEFModLoader/load.log");

        EFModLoader::ModApi::initialize();

        EFModLoader::Load::loadModsAsync(CacheDir / "EFMod/Mod"); //加载Mod
        BNM::Loading::TryLoadByJNI(env);
        BNM::Loading::AddOnLoadedEvent(EFModLoader::Manager::API::processing);
        BNM::Loading::AddOnLoadedEvent(EFModLoader::Load::initiate);
        BNM::Loading::AddOnLoadedEvent(EFModLoader::Manager::Extend::processing);


    return JNI_VERSION_1_6;
}