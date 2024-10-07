//
// Created by eternalfuture on 2024/9/22.
//

#include <jni.h>
#include <BNM/Loading.hpp>
#include <BNM/UserSettings/GlobalSettings.hpp>
#include <iostream>
#include <loader/LoadELFMods.hpp>
#include <BNM/Class.hpp>
#include <BNM/MethodBase.hpp>
#include <BNM/UnityStructures.hpp>
#include <api/Redirect.hpp>
#include <hook/unity/RegisterHook.hpp>
#include <BNM/Field.hpp>
#include <vector>


int (*old_DamageVar)(BNM::UnityEngine::Object *);
int new_DamageVar() {
    auto hooks = EFModLoaderAPI::GetEFModLoader().FindHooks("Assembly-CSharp.dll.Terraria.Main.DamageVar");
    for (auto hook : hooks) {
        return EFModLoader::Redirect::callFunction<int>(reinterpret_cast<void *>(hook));
    }
}

void (*org_test)(BNM::UnityEngine::Object *);
void test(BNM::UnityEngine::Object *instance){
    org_test(instance);

    auto ItemID_Sets_c = BNM::Class("Terraria.ID", "ItemID").GetInnerClass("Sets");
    BNM::Field<BNM::Structures::Mono::Array<bool>> Deprecated = ItemID_Sets_c.GetField("Deprecated");

    size_t size = 5456; // 数组大小
    bool* trueArray = new bool[size]; // 创建一个临时数组，用于存储true值

    // 初始化临时数组
    for (size_t i = 0; i < size; ++i) {
        trueArray[i] = false;
    }

    // 创建Array对象
    BNM::Structures::Mono::Array<bool>* myArray = BNM::Structures::Mono::Array<bool>::Create(trueArray, size);


    //BNM_LOG_DEBUG("%lu", Deprecated[instance].GetOffset());
    BNM_LOG_DEBUG("已尝试获取禁用：%p", Deprecated.GetFieldPointer());
    BNM_LOG_DEBUG("已尝试获取禁用：%c", static_cast<char>(Deprecated.Get()[7]));

    EFModLoader::Redirect::redirectPointer<void*>(reinterpret_cast<uintptr_t>(Deprecated[instance].GetPointer()), reinterpret_cast<uintptr_t>(myArray));



}


void LoadHook(){



    BNM::MethodBase DamageVar = BNM::Class("Terraria", "Main", BNM::Image("Assembly-CSharp.dll")).GetMethod("DamageVar", 2);
    BNM::MethodBase ItemID = BNM::Class("Terraria.ID", "ItemID", BNM::Image("Assembly-CSharp.dll")).GetInnerClass("Sets").GetMethod(".cctor", 0);


    EFModLoader::RegisterHook::Unity::RegisterHOOK("Assembly-CSharp.dll.Terraria.Main.DamageVar", DamageVar, (void *) new_DamageVar,  (void **) old_DamageVar);
    //BNM::InvokeHook(ItemID, test, org_test);
    //EFModLoader::RegisterHook::Unity::RegisterIHOOK("", );
}






JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, [[maybe_unused]] void *reserved) {
    JNIEnv *env;
    vm->GetEnv((void **) &env, JNI_VERSION_1_6);


    BNM::Loading::TryLoadByJNI(env);


    //Loadcpp::LoadALLMod("/data/data/silkways.terraria.efmodloader/cache/runEFMod/");

    EFModLoader::Loader::LoadELFMods::LoadALLMod("/data/data/silkways.terraria.efmodloader/cache/runEFMod/");
    //EFModLoader::Loader::LoadELFMods::LoadMod("libexample_mod.so");

    BNM::Loading::AddOnLoadedEvent(LoadHook);

    sleep(1);

    BNM::Loading::AddOnLoadedEvent(EFModLoader::RegisterHook::Unity::Register);

    return JNI_VERSION_1_6;
}

//