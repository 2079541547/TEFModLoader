//
// Created by eternalfuture on 2024/9/22.
//

#ifndef TERRARIA_TOOLBOX_API_HPP
#define TERRARIA_TOOLBOX_API_HPP

#include <API/redirect.hpp>
#include <API/register.hpp>
#include <iostream>
#include <vector>

std::vector<uintptr_t> org_FuncPtr = {
        Redirect::getPtr(RegisterHook::RegisterHOOK),
};


std::vector<uintptr_t>* FuncPtr = &org_FuncPtr;

void regAPI()
{
    RegisterApi::RegisterAPI("FuncPtr", Redirect::getPtr(FuncPtr));
}


#endif //TERRARIA_TOOLBOX_API_HPP
