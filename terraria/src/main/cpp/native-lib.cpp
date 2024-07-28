#include <jni.h>
#include <BNM/Loading.hpp>
#include <BNM/UserSettings/GlobalSettings.hpp>
#include <BNM/Class.hpp>
#include <BNM/Method.hpp>
#include <unistd.h>
#include <zconf.h>
#include <cstdio>
#include <BNM/Utils.hpp>
//#include "Future/loadAndInvokeFunctions.cpp"
#include <BNM/Property.hpp>
#include <BNM/Operators.hpp>
#include <BNM/BasicMonoStructures.hpp>

typedef int (*DamageVarFunc)();
DamageVarFunc old_DamageVar = nullptr;

int new_DamageVar(){
    std::vector<std::string> methodNames = {"func1"};
    std::string soName = "libMyMod.so";
    //std::vector<int> results = loadAndInvokeIntFunctions(soName, methodNames);


    return 114514;
}

void OnLoaded_Example_01(){
    auto DamageVar = BNM::Class("Terraria", "Main", BNM::Image("Assembly-CSharp.dll")).GetMethod("DamageVar", 2);
    HOOK(DamageVar, new_DamageVar, old_DamageVar);
}


void (*old_Deprecated)(BNM::UnityEngine::Object *);
void new_Deprecated(BNM::UnityEngine::Object *instance){
    old_Deprecated(instance);

    auto ItemID_Sets_c = BNM::Class("Terraria.ID", "ItemID").GetInnerClass("Sets");
    BNM::Field<BNM::Structures::Mono::Array<bool>> Deprecated = ItemID_Sets_c.GetField("Deprecated");
    Deprecated.Get()[8] = true;
    BNM_LOG_INFO("你成功把木头禁掉了qwq");

}



void OnLoaded_Example_02(){
    auto Update = BNM::Class("Terraria.ID", "ItemID", BNM::Image("Assembly-CSharp.dll")).GetMethod("Update");
    HOOK(Update, new_Deprecated, old_Deprecated);
}







[[noreturn]] JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, [[maybe_unused]] void *reserved) {
    JNIEnv *env;
    vm->GetEnv((void **) &env, JNI_VERSION_1_6);

    BNM::Loading::TryLoadByJNI(env);
    BNM::Loading::AddOnLoadedEvent(OnLoaded_Example_01);
    BNM::Loading::AddOnLoadedEvent(OnLoaded_Example_02);


    return JNI_VERSION_1_6;
}