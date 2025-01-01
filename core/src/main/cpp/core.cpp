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
                        EFLOG(INFO, "自动创建API", "创建API:", _.getID(), "指针:", BNM::Class(_.Namespace, _.Class, BNM::Image(_.File)).GetField(_.Name).GetOffset());
                        EFModAPI::getEFModAPI().registerAPI(_.getID(), BNM::Class(_.Namespace, _.Class, BNM::Image(_.File)).GetField(_.Name).GetFieldPointer());
                }
        }
}


//入口
#include <BNM/Loading.hpp>

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, [[maybe_unused]] void *reserved) {
        JNIEnv *env;
        vm->GetEnv((void **) &env, JNI_VERSION_1_6);
        
        logger.setCacheSize(-1);
        logger.setOutput([](const std::string& msg) {
            __android_log_print(ANDROID_LOG_ERROR, "EFModLoader", "%s", msg.c_str());
        });
        logger.setSourceCodeMode(true);
        logger.setLoggingEnabled(true);
        logger.setAutoDumpToFile(true);
        logger.setLogFile("/storage/emulated/0/Documents/TEFModLoader/load.log");
        
        
        EFModLoader::Load::loadModsAsync("/data/user/0/com.and.games505.TerrariaPaid/cache/EFMod/Mod"); //加载Mod
        BNM::Loading::TryLoadByJNI(env);
        BNM::Loading::AddOnLoadedEvent(EFModLoader::Manager::API::processing);
        BNM::Loading::AddOnLoadedEvent(EFModLoader::Load::initiate);
        
        
        return JNI_VERSION_1_6;
}