//
// Created by eternalfuture on 2024/9/22.
//

#ifndef EFMODLOADER_REGISTER_HPP
#define EFMODLOADER_REGISTER_HPP

#include <iostream>
#include <vector>

namespace RegisterApi {

    using namespace std;

    struct API {
        string apiName;
        uintptr_t new_ptr;
    };

    extern vector<API> registerAPI;

    void RegisterAPI(const string& apiName, uintptr_t api_ptr);

    void Register();

}

#endif //EFMODLOADER_REGISTER_HPP
