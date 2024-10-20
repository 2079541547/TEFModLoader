//
// Created by eternalfuture on 2024/9/28.
//

#pragma once

#include <iostream>
#include <vector>

namespace EFModLoader::RegisterApi {
    using namespace std;

    struct API {
        string apiName;
        uintptr_t new_ptr;
    };

    extern vector<API> registerAPI;

    void RegisterAPI(const string& apiName, uintptr_t api_ptr);

    void Register();
}