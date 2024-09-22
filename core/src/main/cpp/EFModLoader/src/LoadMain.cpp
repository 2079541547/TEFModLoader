//
// Created by eternalfuture on 2024/9/22.
//

#include <jni.h>
#include <API/redirect.hpp>
#include <API/register.hpp>
#include <Loader/Loadcpp.hpp>
#include <Hook/RegisterHook.hpp>
#include <BNM/Loading.hpp>
#include <BNM/UserSettings/GlobalSettings.hpp>
#include <EFMod/EFMod.hpp>
#include <API/redirect.hpp>
#include <BNM/UnityStructures.hpp>
#include <jni.h>
#include <BNM/Loading.hpp>
#include <BNM/UserSettings/GlobalSettings.hpp>
#include <BNM/Class.hpp>
#include <BNM/Method.hpp>
#include <unistd.h>
#include <zconf.h>
#include <cstdio>
#include <BNM/Utils.hpp>
#include <BNM/Property.hpp>
#include <BNM/Operators.hpp>
#include <BNM/BasicMonoStructures.hpp>
#include <random>
#include <ctime>


int (*old_hello)(BNM::UnityEngine::Object *);
int hello() {



    auto hooks = EFModLoaderAPI::GetEFModLoader().FindHooks("hook_point_1");


    for (auto hook : hooks) {
        return Redirect::callFunction<int>(reinterpret_cast<void *>(hook));
    }

}



void LoadHook(){

    BNM::MethodBase hook_point_1 = BNM::Class("Terraria", "Main", BNM::Image("Assembly-CSharp.dll")).GetMethod("DamageVar", 2);

    HOOK(hook_point_1, hello, old_hello);
    //RegisterHook::RegisterHOOK("hook_point_1", hook_point_1, (void *) hello,  (void **) old_hello);

}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, [[maybe_unused]] void *reserved) {
    JNIEnv *env;
    vm->GetEnv((void **) &env, JNI_VERSION_1_6);

    BNM::Loading::TryLoadByJNI(env);


    BNM::Loading::AddOnLoadedEvent(LoadHook);


    std::string GetStorage_org = "神秘的东方文字";
    std::string * GetStorage = &GetStorage_org;


    RegisterApi::RegisterAPI("GetStorage", Redirect::getPtr(GetStorage));

    Loadcpp::LoadMod("libexample_mod.so");



    auto hooks = EFModLoaderAPI::GetEFModLoader().FindHooks("hook_point_1");
    for (auto hook : hooks)
    {
        RegisterHook::Register();
        Redirect::callFunction<int>(reinterpret_cast<void *>(hook));
    }

    return JNI_VERSION_1_6;
}