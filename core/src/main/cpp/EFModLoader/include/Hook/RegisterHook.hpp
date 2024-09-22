//
// Created by eternalfuture on 2024/9/22.
//

#ifndef EFMODLOADER_REGISTERHOOK_HPP
#define EFMODLOADER_REGISTERHOOK_HPP

#include <BNM/MethodBase.hpp>
#include <iostream>
#include <vector>

namespace RegisterHook {

    using namespace std;

    struct hooks {
        string hookName;
        BNM::MethodBase ptr;
        void* new_ptr;
        void** old_ptr;
    };

    extern vector<hooks> registerHooks;

    void RegisterHOOK(string hookName, BNM::MethodBase Ptr, void* new_ptr, void** old_Ptr);

    void Register();

}

#endif //EFMODLOADER_REGISTERHOOK_HPP
