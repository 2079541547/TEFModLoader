//
// Created by eternalfuture on 2024/9/22.
//

#ifndef TERRARIA_TOOLBOX_HOOK_HPP
#define TERRARIA_TOOLBOX_HOOK_HPP



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




int (*old_DamageVar)(BNM::UnityEngine::Object *);
int new_DamageVar() {
    auto hooks = EFModLoaderAPI::GetEFModLoader().FindHooks("Assembly-CSharp.dll.Terraria.Main.DamageVar");
    for (auto hook : hooks) {
        return Redirect::callFunction<int>(reinterpret_cast<void *>(hook));
    }
}



void LoadHook(){
    BNM::MethodBase DamageVar = BNM::Class("Terraria", "Main", BNM::Image("Assembly-CSharp.dll")).GetMethod("DamageVar", 2);
    RegisterHook::RegisterHOOK("Assembly-CSharp.dll.Terraria.Main.DamageVar", DamageVar, (void *) new_DamageVar,  (void **) old_DamageVar);
}




#endif //TERRARIA_TOOLBOX_HOOK_HPP
