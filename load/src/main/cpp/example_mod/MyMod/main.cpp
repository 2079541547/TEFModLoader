//
// Created by eternalfuture on 2024/10/20.
//

#include <iostream>
#include <BNM/UserSettings/GlobalSettings.hpp>
#include <BNM/Class.hpp>
#include <BNM/Field.hpp>
#include <BNM/Method.hpp>
#include <jni.h>
#include "BNM/Loading.hpp"


int new_aaa() {
    __android_log_print(ANDROID_LOG_INFO, "MyMod", "独立Mod中的hook函数被调用");
    return 114514;
}


void OnLoaded_Example_01() {
    auto a = BNM::Class("Terraria", "Main", BNM::Image("Assembly-CSharp.dll")).GetMethod("DamageVar", 2);
    HOOK(a, new_aaa, nullptr);
}



JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, [[maybe_unused]] void *reserved) {
    JNIEnv *env;
    vm->GetEnv((void **) &env, JNI_VERSION_1_6);
    __android_log_print(ANDROID_LOG_INFO, "MyMod", "独立Mod正在尝试加载");

    // Load BNM by finding the path to libil2cpp.so
    BNM::Loading::TryLoadByJNI(env);

    // Or load using KittyMemory (as an example)
    // Example_07();

    BNM::Loading::AddOnLoadedEvent(OnLoaded_Example_01);

    return JNI_VERSION_1_6;
}
