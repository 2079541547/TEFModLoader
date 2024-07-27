#include <jni.h>
#include <BNM/Loading.hpp>
#include <BNM/UserSettings/GlobalSettings.hpp>
#include <BNM/Class.hpp>
#include <BNM/Method.hpp>
#include <unistd.h>
#include <zconf.h>
#include <cstdio>
#include <BNM/Utils.hpp>
#include "Future/loadAndInvokeFunctions.cpp"
#include <BNM/Property.hpp>
#include <BNM/Operators.hpp>
#include <BNM/BasicMonoStructures.hpp>

typedef int (*DamageVarFunc)();
DamageVarFunc old_DamageVar = nullptr;

int new_DamageVar(){
    std::vector<std::string> methodNames = {"func1"};
    std::string soName = "libMyMod.so";
    std::vector<int> results = loadAndInvokeIntFunctions(soName, methodNames);


    return getElement(results, 0);
}

void OnLoaded_Example_01(){
    auto DamageVar = BNM::Class("Terraria", "Main", BNM::Image("Assembly-CSharp.dll")).GetMethod("DamageVar", 2);
    HOOK(DamageVar, new_DamageVar, old_DamageVar);
}









[[noreturn]] JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, [[maybe_unused]] void *reserved) {
    JNIEnv *env;
    vm->GetEnv((void **) &env, JNI_VERSION_1_6);

    BNM::Loading::TryLoadByJNI(env);
    BNM::Loading::AddOnLoadedEvent(OnLoaded_Example_01);

    return JNI_VERSION_1_6;
}