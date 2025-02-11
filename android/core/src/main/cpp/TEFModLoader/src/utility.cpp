/*******************************************************************************
 * 文件名称: utility
 * 项目名称: TEFModLoader
 * 创建时间: 2025/2/11
 * 作者: EternalFuture゙
 * Github: https://github.com/2079541547
 * 版权声明: Copyright © 2024 EternalFuture. All rights reserved.
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
 *
 * 注意事项: 请严格遵守GNU AGPL v3.0协议使用本代码，任何未经授权的商业用途均属侵权行为。
 *******************************************************************************/

#include <TEFModLoader/utility.hpp>
#include <dlfcn.h>
#include <link.h>

std::filesystem::path TEFModLoader::Utility::getFilesDir(JNIEnv *env) {
    jobject application = nullptr;
    jclass activity_thread_clz = env->FindClass("android/app/ActivityThread");
    if (activity_thread_clz != nullptr) {
        jmethodID currentApplicationThread = env->GetStaticMethodID(activity_thread_clz, "currentActivityThread", "()Landroid/app/ActivityThread;");
        if (currentApplicationThread != nullptr) {
            jobject currentActivityThread = env->CallStaticObjectMethod(activity_thread_clz, currentApplicationThread);
            jmethodID getApplication = env->GetMethodID(activity_thread_clz, "getApplication", "()Landroid/app/Application;");
            application = env->CallObjectMethod(currentActivityThread, getApplication);

            jclass context_clz = env->GetObjectClass(application);
            jmethodID getFilesDir = env->GetMethodID(context_clz, "getFilesDir", "()Ljava/io/File;");
            if (getFilesDir != nullptr) {
                jobject filesDir = env->CallObjectMethod(application, getFilesDir);
                jclass file_clz = env->FindClass("java/io/File");
                jmethodID getPath = env->GetMethodID(file_clz, "getPath", "()Ljava/lang/String;");
                if (getPath != nullptr) {
                    jstring path = (jstring) env->CallObjectMethod(filesDir, getPath);
                    const char* filePath = env->GetStringUTFChars(path, nullptr);
                    std::string result(filePath);
                    env->ReleaseStringUTFChars(path, filePath);
                    return result;
                }
            }
        }
    }
    return "error";
}

std::filesystem::path TEFModLoader::Utility::getModDir() {
    Dl_info dl_info;
    std::filesystem::path r;
    if (dladdr((void*)TEFModLoader::Utility::getModDir, &dl_info)) {
        if (dl_info.dli_fname) {
            std::filesystem::path d(dl_info.dli_fname);
            r = d.parent_path() / "Mod";
        }
    }
    return r;
}