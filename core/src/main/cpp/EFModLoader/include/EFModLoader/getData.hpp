//
// Created by eternalfuture on 2024/9/28.
//

#pragma once

#include <iostream>
#include <jni.h>
#include "log.hpp"

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
            EFModLoader::Log::LOG("Error", "EFModLoader", "getPackageName", "context为空");
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