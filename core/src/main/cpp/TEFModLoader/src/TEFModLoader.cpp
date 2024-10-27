//
// Created by eternalfuture on 2024/9/22.
//



#include <jni.h>
#include <BNM/Loading.hpp>
#include <zconf.h>
#include <EFModLoader/hook/unity/RegisterHook.hpp>
#include <TEFModLoader/Register.hpp>
#include <EFModLoader/getData.hpp>
#include <EFModLoader/loader/LoadELFMods.hpp>
#include <EFModLoader/api/Redirect.hpp>


void LoadMod() {
    EFModLoader::Loader::LoadELFMods::LoadALLMod(*TEFModLoader::Register::API::get_cacheDir + "runEFMod/");
}




JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, [[maybe_unused]] void *reserved) {
    JNIEnv *env;
    vm->GetEnv((void **) &env, JNI_VERSION_1_6);

    TEFModLoader::Register::API::get_PackageName = new std::string(EFModLoader::getPackageNameAsString(env));
    TEFModLoader::Register::API::get_cacheDir = new std::string("data/data/" + *TEFModLoader::Register::API::get_PackageName + "/cache/");
    TEFModLoader::Register::API::get_ExternalDir = new std::string("/sdcard/Android/data/" + *TEFModLoader::Register::API::get_PackageName +"/files/EFMod-Private/");

    BNM::Loading::TryLoadByJNI(env);

    TEFModLoader::Register::RegisterAPI();

    BNM::Loading::AddOnLoadedEvent(LoadMod); //加载Mod（可使用BNM库的内容）

    TEFModLoader::Register::RegisterPtr();
    TEFModLoader::Register::RegisterHook();

    EFModLoader::Log::LOG("Debug", "JNI_OnLoad", "尝试加载函数...");
    for (auto& a: EFModLoader::RegisterHook::Unity::registerLoad) {
        BNM::Loading::AddOnLoadedEvent(a);
    }

    BNM::Loading::AddOnLoadedEvent(EFModLoader::RegisterHook::Unity::Register);


    return JNI_VERSION_1_6;
}


