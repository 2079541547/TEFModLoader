//
// Created by eternalfuture on 2024/9/28.
//

#pragma once

#include <iostream>

#include <iostream>
#include <vector>
#include "../EFMod/EFMod.hpp"
#include <string>
#include <dlfcn.h>
#include <dirent.h>
#include <sys/stat.h>
#include <unistd.h>
#include <filesystem>
#include <cassert>

namespace EFModLoader::Loader::LoadELFMods {

    using namespace std; //使用std命名空间

    static unordered_map<string, EFMod*> loadedMods; //存储加载的Mod

    void LoadMod(const string& LibPath); //加载单个Mod

    void LoadALLMod(const string& LibPath); //加载一个目录下的所有Mod

}