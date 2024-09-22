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
#include "Hook.hpp"
#include "API.hpp"





JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, [[maybe_unused]] void *reserved) {
    JNIEnv *env;
    vm->GetEnv((void **) &env, JNI_VERSION_1_6);


    BNM::Loading::TryLoadByJNI(env);

    regAPI();

    //Loadcpp::LoadALLMod("/data/data/silkways.terraria.efmodloader/cache/runEFMod/cpp");

    Loadcpp::LoadMod("libexample_mod.so");

    BNM::Loading::AddOnLoadedEvent(LoadHook);

    sleep(1);
    BNM::Loading::AddOnLoadedEvent(RegisterHook::Register);

    return JNI_VERSION_1_6;
}