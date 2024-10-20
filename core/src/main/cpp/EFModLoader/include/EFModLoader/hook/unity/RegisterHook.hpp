//
// Created by eternalfuture on 2024/9/28.
//

#pragma once

#include <iostream>
#include "BNM/MethodBase.hpp"
#include <vector>
#include <functional>
#include "BNM/UserSettings/GlobalSettings.hpp"

namespace EFModLoader::RegisterHook::Unity {

    using namespace std;

    struct hooks {
        string hookName;
        BNM::MethodBase ptr;
        void* new_ptr;
        void** old_ptr;
    };

    typedef void (* EventCallback)();

    extern vector<hooks> registerHooks;
    extern vector<EventCallback> registerLoad;


    void RegisterLoad(EventCallback ptr);

    void RegisterHOOK(string hookName, BNM::MethodBase Ptr, void* new_ptr, void** old_Ptr);

    void Register();

    bool check_InvokeHook(string hookName);

}


#define RegisterIHOOK(hookName, ptr, new_ptr, old_ptr) if(EFModLoader::RegisterHook::Unity::check_InvokeHook(hookName)) BNM::InvokeHook(ptr, new_ptr, old_ptr)