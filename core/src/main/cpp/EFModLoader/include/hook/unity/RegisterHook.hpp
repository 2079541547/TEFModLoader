//
// Created by eternalfuture on 2024/9/28.
//

#pragma once

#include <iostream>
#include <BNM/MethodBase.hpp>
#include <iostream>
#include <vector>
#include <BNM/UserSettings/GlobalSettings.hpp>

namespace EFModLoader::RegisterHook::Unity {

    using namespace std;

    struct hooks {
        string hookName;
        BNM::MethodBase ptr;
        void* new_ptr;
        void** old_ptr;
    };

    struct Ihooks {
        string hookName;
        BNM::MethodBase ptr;
        void* new_ptr;
        void** old_ptr;
    };

    extern vector<hooks> registerHooks;
    extern vector<Ihooks> registerIHooks;


    void RegisterIHOOK(string hookName, BNM::MethodBase Ptr, void* new_ptr, void** old_Ptr);

    void RegisterHOOK(string hookName, BNM::MethodBase Ptr, void* new_ptr, void** old_Ptr);

    void Register();

}