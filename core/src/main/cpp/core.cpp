#include <BNM/UserSettings/GlobalSettings.hpp>

//获取应用缓存
#include <jni.h>
#include <string>
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

class voidHook {
private:
    inline static std::vector<void *> func;
    inline static void (*old_func)(BNM::UnityEngine::Object *, ...);
    inline static size_t modId;
    inline static void hookedFunction(BNM::UnityEngine::Object * instance, ...) {
        va_list args, args_copy;
        va_start(args, instance);
        va_copy(args_copy, args);
        old_func(instance, args_copy);
        for (auto _: func) {
            EFModLoader::Manager::Extend::callFunction<void>(_, instance, args_copy);
        }
        va_end(args_copy);
        va_end(args);
    }
public:
    voidHook(size_t i, std::vector<void *> f, BNM::MethodBase* h) {
        modId = i;
        func = std::move(f);
        HOOK(*h, hookedFunction, old_func);
        EFLOG(INFO, "hook", "hooked!");
        EFModAPI::getEFModAPI().registerAPI(i, &old_func);
    }
};

class intHook {
private:
    inline static std::vector<void *> func;
    inline static int (*old_func)(BNM::UnityEngine::Object *, ...);
    inline static size_t modId;
    inline static int hookedFunction(BNM::UnityEngine::Object * instance, ...) {
        va_list args, args_copy;
        va_start(args, instance);
        va_copy(args_copy, args);
        for (auto _: func) {
            if (EFModLoader::Manager::Extend::callFunction<int>(_, instance, args_copy) != old_func(instance, args_copy)) return EFModLoader::Manager::Extend::callFunction<int>(_,instance, args_copy);
        }

        return old_func(instance, args_copy);
        va_end(args_copy);
        va_end(args);

        return 0;
    }
public:
    intHook(size_t i, std::vector<void *> f, BNM::MethodBase* h) {
        modId = i;
        func = std::move(f);
        HOOK(*h, hookedFunction, old_func);
        EFLOG(INFO, "hook", "hooked!");
        EFModAPI::getEFModAPI().registerAPI(i, &old_func);
    }
};

class boolHook {
private:
    inline static std::vector<void *> func;
    inline static bool (*old_func)(BNM::UnityEngine::Object *, ...);
    inline static size_t modId;
    inline static bool hookedFunction(BNM::UnityEngine::Object * instance, ...) {
        va_list args, args_copy;
        va_start(args, instance);
        va_copy(args_copy, args);
        for (auto _: func) {
            if (EFModLoader::Manager::Extend::callFunction<bool>(_,instance, args_copy) != old_func(instance, args_copy)) return EFModLoader::Manager::Extend::callFunction<bool>(_,instance, args_copy);
        }

        return old_func(instance, args_copy);
        va_end(args_copy);
        va_end(args);

        return true;
    }
public:
    boolHook(size_t i, std::vector<void *> f, BNM::MethodBase* h) {
        modId = i;
        func = std::move(f);
        HOOK(*h, hookedFunction, old_func);
        EFLOG(INFO, "hook", "hooked!");
        EFModAPI::getEFModAPI().registerAPI(i, &old_func);
    }
};

class stringHook {
private:
    inline static std::vector<void *> func;
    inline static BNM::Structures::Mono::String* (*old_func)(BNM::UnityEngine::Object *, ...);
    inline static size_t modId;
    inline static BNM::Structures::Mono::String* hookedFunction(BNM::UnityEngine::Object * instance, ...) {
        va_list args, args_copy;
        va_start(args, instance);
        va_copy(args_copy, args);
        for (auto _: func) {
            if (EFModLoader::Manager::Extend::callFunction<BNM::Structures::Mono::String*>(_,instance, args_copy) != old_func(instance, args_copy)) return EFModLoader::Manager::Extend::callFunction<BNM::Structures::Mono::String*>(_,instance, args_copy);
        }

        return old_func(instance, args_copy);
        va_end(args_copy);
        va_end(args);
        return BNM::CreateMonoString("Error");
    }
public:
    stringHook(size_t i, std::vector<void *> f, BNM::MethodBase* h) {
        modId = i;
        func = std::move(f);
        HOOK(*h, hookedFunction, old_func);
        EFLOG(INFO, "hook", "hooked!");
        EFModAPI::getEFModAPI().registerAPI(i, &old_func);
    }
};

class floatHook {
private:
    inline static std::vector<void *> func;
    inline static float (*old_func)(BNM::UnityEngine::Object *, ...);
    inline static size_t modId;
    inline static float hookedFunction(BNM::UnityEngine::Object * instance, ...) {
        va_list args, args_copy;
        va_start(args, instance);
        va_copy(args_copy, args);
        for (auto _: func) {
            if (EFModLoader::Manager::Extend::callFunction<float>(_,instance, args_copy) != old_func(instance, args_copy)) return EFModLoader::Manager::Extend::callFunction<float>(_,instance, args_copy);
        }

        return old_func(instance, args_copy);
        va_end(args_copy);
        va_end(args);
        return 0.00;
    }
public:
    floatHook(size_t i, std::vector<void *> f, BNM::MethodBase* h) {
        modId = i;
        func = std::move(f);
        HOOK(*h, hookedFunction, old_func);
        EFLOG(INFO, "hook", "hooked!");
        EFModAPI::getEFModAPI().registerAPI(i, &old_func);
    }
};

class longHook {
private:
    inline static std::vector<void *> func;
    inline static long (*old_func)(BNM::UnityEngine::Object *, ...);
    inline static size_t modId;
    inline static long hookedFunction(BNM::UnityEngine::Object * instance, ...) {
        va_list args, args_copy;
        va_start(args, instance);
        va_copy(args_copy, args);
        for (auto _: func) {
            if (EFModLoader::Manager::Extend::callFunction<long>(_,instance, args_copy) != old_func(instance, args_copy)) return EFModLoader::Manager::Extend::callFunction<long>(_,instance, args_copy);
        }

        return old_func(instance, args_copy);
        va_end(args_copy);
        va_end(args);
        return 0;
    }
public:
    longHook(size_t i, std::vector<void *> f, BNM::MethodBase* h) {
        modId = i;
        func = std::move(f);
        HOOK(*h, hookedFunction, old_func);
        EFLOG(INFO, "hook", "hooked!");
        EFModAPI::getEFModAPI().registerAPI(i, &old_func);
    }
};

class shortHook {
private:
    inline static std::vector<void *> func;
    inline static short (*old_func)(BNM::UnityEngine::Object *, ...);
    inline static size_t modId;
    inline static short hookedFunction(BNM::UnityEngine::Object * instance, ...) {
        va_list args, args_copy;
        va_start(args, instance);
        va_copy(args_copy, args);
        for (auto _: func) {
            if (EFModLoader::Manager::Extend::callFunction<short>(_,instance, args_copy) != old_func(instance, args_copy)) return EFModLoader::Manager::Extend::callFunction<short>(_,instance, args_copy);
        }

        return old_func(instance, args_copy);
        va_end(args_copy);
        va_end(args);
        return 0;
    }
public:
    shortHook(size_t i, std::vector<void *> f, BNM::MethodBase* h) {
        modId = i;
        func = std::move(f);
        HOOK(*h, hookedFunction, old_func);
        EFLOG(INFO, "hook", "hooked!");
        EFModAPI::getEFModAPI().registerAPI(i, &old_func);
    }
};

class uint8_tHook {
private:
    inline static std::vector<void *> func;
    inline static uint8_t (*old_func)(BNM::UnityEngine::Object *, ...);
    inline static size_t modId;
    inline static uint8_t hookedFunction(BNM::UnityEngine::Object * instance, ...) {
        va_list args, args_copy;
        va_start(args, instance);
        va_copy(args_copy, args);
        for (auto _: func) {
            if (EFModLoader::Manager::Extend::callFunction<uint8_t>(_,instance, args_copy) != old_func(instance, args_copy)) return EFModLoader::Manager::Extend::callFunction<uint8_t>(_,instance, args_copy);
        }

        return old_func(instance, args_copy);
        va_end(args_copy);
        va_end(args);
        return 0;
    }
public:
    uint8_tHook(size_t i, std::vector<void *> f, BNM::MethodBase* h) {
        modId = i;
        func = std::move(f);
        HOOK(*h, hookedFunction, old_func);
        EFLOG(INFO, "hook", "hooked!");
        EFModAPI::getEFModAPI().registerAPI(i, &old_func);
    }
};

void EFModLoader::Manager::Extend::processing() {
    for(const auto& _: EFModAPI::getEFModAPI().getFuncDescriptor()) {
        if (!_.File.empty()) {

            size_t dotPosition = _.Class.find('.');
            BNM::MethodBase* a;
            if (dotPosition != std::string::npos) {
                a = new BNM::MethodBase(BNM::Class(_.Namespace, _.Class.substr(0, dotPosition), BNM::Image(_.File)).GetInnerClass(_.Class.substr(dotPosition + 1)).GetMethod(_.Name));
            } else {
                a = new BNM::MethodBase(BNM::Class(_.Namespace, _.Class, BNM::Image(_.File)).GetMethod(_.Name));
            }
            EFLOG(INFO, "自动创建hook", "创建hook:", _.getID(), "被hook的函数:", (uintptr_t)a);

            if (_.Type == "hook>>void") {
                voidHook(_.getID(), _.FunPtr, a);
            } else if(_.Type == "hook>>int") {
                intHook(_.getID(), _.FunPtr, a);
            } else if (_.Type == "hook>>bool") {
                boolHook(_.getID(), _.FunPtr, a);
            } else if (_.Type == "hook>>string") {
                stringHook(_.getID(), _.FunPtr, a);
            } else if(_.Type == "hook>>float") {
                floatHook(_.getID(), _.FunPtr, a);
            } else if(_.Type == "hook>>long") {
                longHook(_.getID(), _.FunPtr, a);
            } else if(_.Type == "hook>>short") {
                shortHook(_.getID(), _.FunPtr, a);
            } else if(_.Type == "hook>>uint8_t") {
                uint8_tHook(_.getID(), _.FunPtr, a);
            } else {
                EFLOG(ERROR, "自动创建hook", "未知类型:", _.Type);
            }
        }
    }
}


//入口
#include <EFModLoader/EFMod/ModApi.hpp>
#include <BNM/Loading.hpp>

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, [[maybe_unused]] void *reserved) {
        JNIEnv *env;
        vm->GetEnv((void **) &env, JNI_VERSION_1_6);

        logger.setCacheSize(4096 * 1024);
        logger.setOutput([](const std::string& msg) {
            __android_log_print(ANDROID_LOG_ERROR, "EFModLoader", "%s", msg.c_str());
        });
        logger.setSourceCodeMode(true);
        logger.setLoggingEnabled(true);
        logger.setAutoDumpToFile(true);
        logger.setLogFile("/storage/emulated/0/Documents/TEFModLoader/load.log");

        EFModLoader::ModApi::initialize();

        EFModLoader::Load::loadModsAsync("/data/user/0/com.and.games505.TerrariaPaid/cache/EFMod/Mod"); //加载Mod
        BNM::Loading::TryLoadByJNI(env);
        BNM::Loading::AddOnLoadedEvent(EFModLoader::Manager::API::processing);
        BNM::Loading::AddOnLoadedEvent(EFModLoader::Load::initiate);
        BNM::Loading::AddOnLoadedEvent(EFModLoader::Manager::Extend::processing);


    return JNI_VERSION_1_6;
}