//
// Created by eternalfuture on 2024/10/20.
//

#pragma once

#include <BNM/UserSettings/GlobalSettings.hpp>
#include <iostream>
#include <BNM/UnityStructures.hpp>

namespace Terraria::Main {

    using namespace BNM;

    extern Class Main;
    extern MethodBase DamageVar;

    void getHookPtr();
    void RegisterHook();

    extern int (*old_DamageVar)(UnityEngine::Object *, float dmg, float luck);
    int new_DamageVar(UnityEngine::Object *instance, float dmg, float luck);

}