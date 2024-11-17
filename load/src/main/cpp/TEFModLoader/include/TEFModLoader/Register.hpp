//
// Created by eternalfuture on 2024/10/20.
//

#pragma once

#include <iostream>

namespace TEFModLoader::Register {

    using namespace std;

    namespace API {
        extern string* get_PackageName;
        extern string* get_ExternalDir;
        extern string* get_cacheDir;
    }

    void RegisterAPI();
    void RegisterPtr();
    void RegisterHook();
}