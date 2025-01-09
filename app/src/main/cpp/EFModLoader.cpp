/*******************************************************************************
 * 文件名称: EFModLoader
 * 项目名称: TEFModLoader
 * 创建时间: 2024/12/21
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
 * 描述信息: 本文件为TEFModLoader项目中的一部分。
 * 注意事项: 请严格遵守GNU AGPL v3.0协议使用本代码，任何未经授权的商业用途均属侵权行为。
 *******************************************************************************/

#include <jni.h>
#include "EFMod.cpp"
#include <silkcasket/temp_directory_path.hpp>

extern "C"
JNIEXPORT void JNICALL
Java_silkways_terraria_efmodloader_logic_efmod_LoaderManager_install(JNIEnv *env, jobject thiz,
                                                                     jstring inpu_path,
                                                                     jstring out_path) {
        auto inputPath = std::filesystem::path(env->GetStringUTFChars(inpu_path, nullptr));
        auto outPath = std::filesystem::path(env->GetStringUTFChars(out_path, nullptr));
        
        SilkCasket::analysis Analysis(inputPath, "EFModLoader");
        Analysis.releaseFile("loader.json", outPath);
        
        SilkCasket_compress_A_File(false,
                                   outPath / "loader.json",
                                   outPath / "loader",
                                   {false, true, true, true, true}
        );
        //std::filesystem::remove(outPath / "loader.json");
        std::filesystem::remove_all(outPath / "silk_casket_temp");
        
        Analysis.releaseFolder("lib/android", outPath);
        Analysis.releaseFile("loader.icon", outPath);
        
}

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_silkways_terraria_efmodloader_logic_efmod_LoaderManager_getLoaderInfo(JNIEnv *env,
                                                                           jobject thiz,
                                                                           jstring inpu_path) {
        auto inputPath = std::filesystem::path(env->GetStringUTFChars(inpu_path, nullptr)) / "loader";
        std::vector<uint8_t> data(get_entry_data(inputPath, "loader.json", "EFModLoader"));
        jbyteArray result = env->NewByteArray(data.size());
        env->SetByteArrayRegion(result, 0, data.size(), reinterpret_cast<const jbyte*>(&data[0]));
        return result;
}



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

std::string getExternalCacheDirPath(JNIEnv* env) {
        jobject context = getApplication(env);
        if (context == nullptr) {
                return "";
        }
        
        jclass contextClass = env->GetObjectClass(context);
        if (contextClass == nullptr) {
                return "";
        }
        
        jmethodID methodId_getExternalCacheDir = env->GetMethodID(contextClass, "getExternalCacheDir", "()Ljava/io/File;");
        if (methodId_getExternalCacheDir == nullptr) {
                return "";
        }
        
        jobject fileObject = env->CallObjectMethod(context, methodId_getExternalCacheDir);
        if (env->ExceptionCheck()) {
                env->ExceptionClear(); // Clear any exceptions that may have occurred
                return "";
        }
        
        if (fileObject == nullptr) {
                return "";
        }
        
        jclass fileClass = env->GetObjectClass(fileObject);
        jmethodID methodId_getPath = env->GetMethodID(fileClass, "getPath", "()Ljava/lang/String;");
        if (methodId_getPath == nullptr) {
                env->DeleteLocalRef(fileObject);
                return "";
        }
        
        jstring pathJString = (jstring)env->CallObjectMethod(fileObject, methodId_getPath);
        if (pathJString == nullptr) {
                env->DeleteLocalRef(fileObject);
                return "";
        }
        
        const char* pathChars = env->GetStringUTFChars(pathJString, nullptr);
        std::string result(pathChars);
        env->ReleaseStringUTFChars(pathJString, pathChars);
        
        // Clean up local references
        env->DeleteLocalRef(pathJString);
        env->DeleteLocalRef(fileObject);
        env->DeleteLocalRef(fileClass);
        env->DeleteLocalRef(contextClass);
        
        return result;
}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, [[maybe_unused]] void *reserved) {
        JNIEnv *env;
        vm->GetEnv((void **) &env, JNI_VERSION_1_6);
        
        tempPath = std::filesystem::path(getExternalCacheDirPath(env));
        
        return JNI_VERSION_1_6;
}