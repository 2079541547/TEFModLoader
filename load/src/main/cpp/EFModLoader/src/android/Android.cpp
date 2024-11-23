/*******************************************************************************
 * 文件名称: Android
 * 项目名称: EFModLoader
 * 创建时间: 2024/11/1
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


#include <iostream>
#include "EFModLoader/log.hpp"
#include "EFModLoader/android/Android.hpp"
#include "EFModLoader/getData.hpp"
#include "EFModLoader/EFMod/EFMod.hpp"
#include "EFModLoader/api/RegisterApi.hpp"
#include "EFModLoader/api/Redirect.hpp"
#include "EFModLoader/hook/unity/RegisterHook.hpp"
#include "EFModLoader/loader/LoadELFMods.hpp"
#include <filesystem>
#include "EFModLoader/agreement.h"

namespace EFModLoader::Android {

    string* get_PackageName;
    string* get_cacheDir;


    void Load(JNIEnv *env) {

        EFLOG(LogLevel::INFO, "EFModLoader", "Android", "Load", agreement_str);

        get_PackageName = new std::string(EFModLoader::getPackageNameAsString(env));
        get_cacheDir = new std::string("data/data/" + *get_PackageName + "/cache/");

        auto *get_ExternalDir = new std::string(
                "/sdcard/Android/data/" + *get_PackageName + "/files/EFMod-Private/");

        EFModLoader::RegisterApi::RegisterAPI("JNI_OnLoad.env", (long) env); //提供java虚拟机给Mod
        EFModLoader::RegisterApi::RegisterAPI("get_PackageName", (long) get_PackageName); //包名
        EFModLoader::RegisterApi::RegisterAPI("get_ExternalDir", (long) get_ExternalDir); //私有目录
        EFModLoader::RegisterApi::RegisterAPI("get_cacheDir", (long) get_cacheDir); //缓存
    }

}
