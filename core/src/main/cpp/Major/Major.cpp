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

char* g_jsonContent = nullptr;

#include "Future/loadMod.cpp"

int (*old_DamageVar)(BNM::UnityEngine::Object *);
int new_DamageVar(BNM::UnityEngine::Object *instance){
    return loadMod_int(g_jsonContent, "Assembly-CSharp.Terraria.Main.DamageVar");
}



void OnLoaded_Class_Main(){
    auto Main = BNM::Class("Terraria", "Main", BNM::Image("Assembly-CSharp.dll"));


    auto DamageVar = Main.GetMethod("DamageVar", 2);

    int maxChecks = 10; //进行10次测试
    int checkCount = 0;
    while (checkCount < maxChecks) {
        if (loadMod_int(g_jsonContent, "Assembly-CSharp.Terraria.Main.DamageVar") != 0) {
            BNM_LOG_INFO("已触发Hook");
            HOOK(DamageVar, new_DamageVar, old_DamageVar);
            break;
        }
        BNM_LOG_INFO("无效不触发Hook");
        ++checkCount;
    }
}



bool (*old_CanFly)(BNM::UnityEngine::Object *);
bool new_CanFly(){
    return loadMod_bool(g_jsonContent, "Assembly-CSharp.Terraria.Mount.CanFly");
}

bool (*old_CanHover)(BNM::UnityEngine::Object *);
bool new_CanHover(){
    return loadMod_bool(g_jsonContent, "Assembly-CSharp.Terraria.Mount.CanHover");
}

float (*old_get_FallDamage)(BNM::UnityEngine::Object *);
int new_get_FallDamage(){
    return loadMod_int(g_jsonContent, "Assembly-CSharp.Terraria.Mount.get_FallDamage");
}

float (*old_get_DashSpeed)(BNM::UnityEngine::Object *);
int new_get_DashSpeed(){
    return loadMod_int(g_jsonContent, "Assembly-CSharp.Terraria.Mount.get_DashSpeed");
}


int (*old_get_HeightBoots)(BNM::UnityEngine::Object *);
int new_get_HeightBoots(){
    return loadMod_int(g_jsonContent, "Assembly-CSharp.Terraria.get_HeightBoots");
}

void OnLoaded_Class_Mount(){
    auto Mount = BNM::Class("Terraria", "Mount", BNM::Image("Assembly-CSharp.dll"));

    auto CanFly = Mount.GetMethod("CanFly", 0);
    auto CanHover = Mount.GetMethod("CanHover", 0);
    auto get_FallDamage = Mount.GetMethod("get_FallDamage", 0);
    auto get_DashSpeed = Mount.GetMethod("get_DashSpeed", 0);
    auto get_HeightBoots = Mount.GetMethod("get_HeightBoots", 0);

    int maxChecks = 10; //进行10次测试
    int checkCount = 0;
    std::vector<bool> triggeredHooks(5, false);
    while (checkCount < maxChecks) {
        // 检查第1个条件
        if (loadMod_bool(g_jsonContent, "Assembly-CSharp.Terraria.Mount.CanFly") != 0 && !triggeredHooks[0]) {
            BNM_LOG_INFO("已触发Hook - CanFly");
            HOOK(CanFly, new_CanFly, old_CanFly);
            triggeredHooks[0] = true; // 标记为已触发
        }

        // 检查第2个条件
        if (loadMod_bool(g_jsonContent, "Assembly-CSharp.Terraria.Mount.CanHover") != 0 && !triggeredHooks[1]) {
            BNM_LOG_INFO("已触发Hook - CanHover");
            HOOK(CanHover, new_CanHover, old_CanHover);
            triggeredHooks[1] = true; // 标记为已触发
        }

        //第3个
        if (loadMod_int(g_jsonContent, "Assembly-CSharp.Terraria.Mount.get_FallDamage") != 0 && !triggeredHooks[2]){
            BNM_LOG_INFO("已触发Hook - get_FallDamage");
            HOOK(get_FallDamage, new_get_FallDamage, old_get_FallDamage);
            triggeredHooks[2] = true; // 标记为已触发
        }

        //第4个
        if(loadMod_int(g_jsonContent, "Assembly-CSharp.Terraria.Mount.get_DashSpeed") != 0 && !triggeredHooks[3]){
            BNM_LOG_INFO("已触发Hook - get_DashSpeed");
            HOOK(get_DashSpeed, new_get_DashSpeed, old_get_DashSpeed);
            triggeredHooks[3] = true; // 标记为已触发
        }

        //第5个
        if (loadMod_int(g_jsonContent, "Assembly-CSharp.Terraria.get_HeightBoots") != 0 && !triggeredHooks[4]){
            BNM_LOG_INFO("已触发Hook - get_HeightBoots");
            HOOK(get_HeightBoots, new_get_HeightBoots, old_get_HeightBoots);
            triggeredHooks[4] = true; // 标记为已触发
        }

        ++checkCount;
    }
}





extern "C"
JNIEXPORT void JNICALL
Java_silkways_terraria_toolbox_core_Load_getJsonContent(JNIEnv *env, jobject thiz,
                                                           jstring content) {
    // 如果已经有先前的内容，则释放它
    if (g_jsonContent != nullptr) {
        free(g_jsonContent);
        g_jsonContent = nullptr;
    }

    // 将 jstring 转换为 C 字符串
    const char* c_content = env->GetStringUTFChars(content, nullptr);

    // 分配内存并复制字符串到全局变量
    g_jsonContent = (char*) malloc(strlen(c_content) + 1);
    if (g_jsonContent != nullptr) {
        strcpy(g_jsonContent, c_content);
    }
    // 释放 jstring 的临时引用
    env->ReleaseStringUTFChars(content, c_content);
}





JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, [[maybe_unused]] void *reserved) {
    JNIEnv *env;
    vm->GetEnv((void **) &env, JNI_VERSION_1_6);


    BNM::Loading::TryLoadByJNI(env);


    BNM::Loading::AddOnLoadedEvent(OnLoaded_Class_Main);
    BNM::Loading::AddOnLoadedEvent(OnLoaded_Class_Mount);


    return JNI_VERSION_1_6;
}

