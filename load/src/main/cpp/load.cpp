#include <jni.h>
#include <string>
#include "agreement.h"

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
        return nullptr;
    }
    jclass activity = env->GetObjectClass(context);
    jmethodID methodId_pack = env->GetMethodID(activity, "getPackageName", "()Ljava/lang/String;");
    auto name_str = (jstring)(env->CallObjectMethod(context, methodId_pack));
    return name_str;
}


extern "C"
JNIEXPORT jstring JNICALL
Java_eternal_future_efmodloader_load_Loader_getPackName(JNIEnv *env, jclass clazz) {
    return getPackageName(env);
}
extern "C"
JNIEXPORT jstring JNICALL
Java_eternal_future_efmodloader_load_Loader_agreement(JNIEnv *env, jclass clazz) {
    return env->NewStringUTF(agreement_str.c_str());
}