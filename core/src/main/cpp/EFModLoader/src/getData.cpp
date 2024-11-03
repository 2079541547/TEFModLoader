/*******************************************************************************
 * 文件名称: getData
 * 项目名称: EFModLoader
 * 创建时间: 2024/11/2
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
#include <EFModLoader/getData.hpp>

namespace EFModLoader {

    jobject getApplication(JNIEnv *env) {
        jobject application = nullptr;
        jclass activity_thread_clz = env->FindClass("android/app/ActivityThread");
        if (activity_thread_clz != nullptr) {
            jmethodID get_Application = env->GetStaticMethodID(activity_thread_clz,
                                                               "currentActivityThread",
                                                               "()Landroid/app/ActivityThread;");
            if (get_Application != nullptr) {
                jobject currentActivityThread = env->CallStaticObjectMethod(activity_thread_clz,
                                                                            get_Application);
                jmethodID getal = env->GetMethodID(activity_thread_clz, "getApplication",
                                                   "()Landroid/app/Application;");
                application = env->CallObjectMethod(currentActivityThread, getal);
            }
            return application;
        }
        return application;
    }

    jstring getPackageName(JNIEnv *env) {
        jobject context = getApplication(env);
        if (context == nullptr) {
            EFLOG(LogLevel::ERROR, "EFModLoader", "getPackageName", "context为空");
            return nullptr;
        }
        jclass activity = env->GetObjectClass(context);
        jmethodID methodId_pack = env->GetMethodID(activity, "getPackageName", "()Ljava/lang/String;");
        auto name_str = (jstring)(env->CallObjectMethod(context, methodId_pack));
        return name_str;
    }

    std::string getPackageNameAsString(JNIEnv *env) {
        jstring packageNameJString = getPackageName(env);
        if (packageNameJString == nullptr) {
            return "";
        }
        const char* packageNameChars = env->GetStringUTFChars(packageNameJString, nullptr);
        if (packageNameChars == nullptr) {
            return "";
        }
        std::string packageName(packageNameChars);
        env->ReleaseStringUTFChars(packageNameJString, packageNameChars);
        return packageName;
    }

}