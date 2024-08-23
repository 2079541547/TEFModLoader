//
// Created by eternalfuture on 2024/8/23.
//
#include <jni.h>
#include <string>
#include <android/asset_manager_jni.h>
#include <android/log.h>
#include <android/asset_manager.h>
#include "shadowhook.h"

#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, "redirect", __VA_ARGS__))
#define LOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR, "redirect", __VA_ARGS__))



// 定义原始函数指针
void * (*original_AAssetManager_open)(AAssetManager* mgr, const char* filename, int mode);

// 替换函数
void replaced_AAssetManager_open(AAssetManager* mgr, const char* filename, int mode) {
    LOGI("AAssetManager_open 被调用，文件名: %s", filename);
    void * asset = original_AAssetManager_open(mgr, filename, mode);
    LOGI("AAssetManager_open 返回的资产对象: %p", asset);
}



std::string jstringToString(JNIEnv* env, jstring jstr) {
    const jclass stringClass = env->FindClass("java/lang/String");
    const jmethodID getBytes = env->GetMethodID(stringClass, "getBytes", "(Ljava/lang/String;)[B");
    const jstring charsetName = env->NewStringUTF("UTF-8");
    const jbyteArray bytes = (jbyteArray) env->CallObjectMethod(jstr, getBytes, charsetName);
    const jsize alen = env->GetArrayLength(bytes);
    const jbyte* ba = env->GetByteArrayElements(bytes, JNI_FALSE);
    std::string result((char*)ba, (size_t)alen);
    env->ReleaseByteArrayElements(bytes, const_cast<jbyte *>(ba), 0);
    return result;
}


extern "C"
JNIEXPORT jboolean JNICALL
Java_silkways_terraria_toolbox_ToolBox_doHook(JNIEnv *env, jobject thiz) {


    if (shadowhook_hook_sym_addr(
            (void*)AAsset_read,
            (void*)replaced_AAssetManager_open,
            reinterpret_cast<void **>(&original_AAssetManager_open))){

        shadowhook_hook_sym_addr(
                (void*)AAssetManager_open,
                (void*)replaced_AAssetManager_open,
                reinterpret_cast<void **>(&original_AAssetManager_open));

        return JNI_TRUE;
    } else {

        shadowhook_hook_sym_addr(
                (void*)AAssetManager_open,
                (void*)replaced_AAssetManager_open,
                reinterpret_cast<void **>(&original_AAssetManager_open));


        return JNI_FALSE;
    }




    return JNI_FALSE;
}