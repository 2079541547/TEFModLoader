//
// Created by eternalfuture on 2024/10/20.
//

#pragma once

#include <BNM/UserSettings/GlobalSettings.hpp>
#include <BNM/UnityStructures.hpp>
#include <iostream>
#include <string>

namespace UnityEngine::TextAsset {

    using namespace std;
    using namespace BNM;
    using namespace BNM::Structures;

    extern Class TextAsset;
    extern MethodBase get_text;
    extern BNM::Method<Mono::String *> ToString;


    void getHookPtr();
    void RegisterHook();

    extern Mono::String (*old_get_text)(BNM::UnityEngine::Object *);
    Mono::String new_get_text(BNM::UnityEngine::Object *instance);

}