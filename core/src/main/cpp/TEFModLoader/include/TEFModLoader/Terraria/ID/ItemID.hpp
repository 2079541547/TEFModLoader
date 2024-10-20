//
// Created by eternalfuture on 2024/10/20.
//

#pragma once

#include <BNM/UserSettings/GlobalSettings.hpp>
#include <BNM/UnityStructures.hpp>
#include <BNM/Field.hpp>
#include <BNM/FieldBase.hpp>

namespace Terraria::ID::ItemID::Sets {

    using namespace BNM;
    using namespace BNM::Structures;
    using namespace std;

    extern Class Sets;
    extern MethodBase cctor;

    void RegisterApi();
    void getHookPtr();
    void RegisterHook();

    extern void (*old_cctor)(UnityEngine::Object *);
    void new_cctor(UnityEngine::Object *instance);

}