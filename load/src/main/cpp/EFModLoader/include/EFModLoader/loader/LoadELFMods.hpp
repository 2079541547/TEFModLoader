/*******************************************************************************
 * 文件名称: LoadELFMods
 * 项目名称: EFModLoader
 * 创建时间: 2024/9/28
 * 作者: EternalFuture゙
 * Github: https://github.com/2079541547
 * 版权声明: Copyright © 2024 EternalFuture゙. All rights reserved.
 * 许可证: This program is free software: you can redistribute it and/or modify
 *         it under the terms of the GNU Affero General Public License as published
 *         by the Free Software Foundation, either version 3 of the License, or
 *         (at your option) any later version.
 *
 *         This program is distributed in the hope that it will be useful,
 *         but WITHOUT ANY WARRANTY; without even the implied warranty of
 *         MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *         GNU Affero General Public License for more details.
 *
 *         You should have received a copy of the GNU Affero General Public License
 *         along with this program. If not, see <https://www.gnu.org/licenses/>.
 * 描述信息: 本文件为EFModLoader项目中的一部分。
 * 注意事项: 请严格遵守GNU AGPL v3.0协议使用本代码，任何未经授权的商业用途均属侵权行为。
 *******************************************************************************/

#pragma once

#include <iostream>
#include <vector>
#include <unordered_map>
#include <string>
#include <dlfcn.h>
#include <dirent.h>
#include <sys/stat.h>
#include <unistd.h>
#include <filesystem>
#include <cassert>
#include <EFModLoader/EFMod/EFMod.hpp>
#include <jni.h>
#include <EFModLoader/api/RegisterApi.hpp>
#include <EFModLoader/log.hpp>
#include <chrono>
#include <malloc.h>
#include <sys/resource.h>

namespace EFModLoader::Loader::LoadELFMods {

    using namespace std; // 使用std命名空间

    /**
     * @var loadedMods
     * @brief 存储加载的Mod。
     *
     * 键是Mod的唯一标识符，值是指向Mod对象的指针。
     */
    static unordered_map<string, EFMod*> loadedMods;

    /**
     * @fn LoadMod
     * @brief 加载单个Mod。
     *
     * @param LibPath Mod库文件的路径。
     */
    void LoadMod(const string& LibPath);

    /**
     * @fn LoadModX
     * @brief 加载单个独立Mod。
     *
     * @param LibPath 独立Mod库文件的路径。
     */
    void LoadModX(JNIEnv *env, const std::string &LibPath);

    /**
     * @fn LoadALLMod
     * @brief 加载一个目录下的所有Mod。
     *
     * @param LibPath 目录路径。
     */
    void LoadALLMod(const string& LibPath);

    /**
     * @fn LoadALLModX
     * @brief 加载一个目录下的所有独立Mod。
     *
     * @param LibPath 目录路径。
     */
    void LoadALLModX(JNIEnv *env, const string& LibPath);
}