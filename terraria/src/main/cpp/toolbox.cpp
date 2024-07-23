// Write C++ code here.
//
// Do not forget to dynamically load the C++ library into your application.
//
// For instance,
//
// In MainActivity.java:
//    static {
//       System.loadLibrary("toolbox");
//    }
//
// Or, in MainActivity.kt:
//    companion object {
//      init {
//         System.loadLibrary("toolbox")
//      }
//    }


#include <jni.h>
#include <BNM/Loading.hpp>
#include <BNM/UserSettings/GlobalSettings.hpp>
#include <BNM/Class.hpp>
#include <BNM/Field.hpp>
#include <BNM/Method.hpp>
#include <BNM/Property.hpp>
#include <BNM/Operators.hpp>
#include <BNM/BasicMonoStructures.hpp>
#include <unistd.h>
#include <zconf.h>
#include "shadowhook.h"


using namespace BNM::Structures;
using namespace BNM::Operators;



BNM::Field<Mono::Array<bool>> Deprecated_items{};

void (*old_DeprecatedUpdate)(BNM::UnityEngine::Object *);
void DeprecatedUpdate(BNM::UnityEngine::Object *instance){
    old_DeprecatedUpdate(instance);
    Deprecated_items.Get()[10] = true;

}



void OnLoaded_Example_01(){
    auto ItemID_Sets_c = BNM::Class("Terraria.ID", "ItemID").GetInnerClass("Sets");
    BNM::Field<Mono::Array<bool>> Deprecated_items = ItemID_Sets_c.GetField("Deprecated");
    auto Update = ItemID_Sets_c.GetMethod(OBFUSCATE_BNM("Update"));


    HOOK(Update, DeprecatedUpdate, old_DeprecatedUpdate);

}






JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, [[maybe_unused]] void *reserved) {
    JNIEnv *env;
    vm->GetEnv((void **) &env, JNI_VERSION_1_6);

    OnLoaded_Example_01();

    return JNI_VERSION_1_6;
}


