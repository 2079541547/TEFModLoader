#include <jni.h>
#include <SilkCasket/SilkCasket.hpp>
#include <filesystem>

extern "C"
JNIEXPORT void JNICALL
Java_eternal_future_tefmodloader_utility_SilkCasket_release(JNIEnv *env, jobject /* this */, jstring temp, jstring path, jstring target) {
    const char *tempStr = env->GetStringUTFChars(temp, nullptr);
    const char *pathStr = env->GetStringUTFChars(path, nullptr);
    const char *targetStr = env->GetStringUTFChars(target, nullptr);

    std::filesystem::create_directories(tempStr);
    SilkCasket::temp_path = tempStr;


    releaseAllEntry(pathStr, targetStr, "EFMod");

    env->ReleaseStringUTFChars(temp, tempStr);
    env->ReleaseStringUTFChars(path, pathStr);
    env->ReleaseStringUTFChars(target, targetStr);
}

extern "C"
JNIEXPORT void JNICALL
Java_eternal_future_tefmodloader_utility_SilkCasket_compress(JNIEnv *env, jobject /* this */, jstring temp, jstring path, jstring target) {
    const char *tempStr = env->GetStringUTFChars(temp, nullptr);
    const char *pathStr = env->GetStringUTFChars(path, nullptr);
    const char *targetStr = env->GetStringUTFChars(target, nullptr);

    std::filesystem::create_directories(tempStr);
    SilkCasket::temp_path = tempStr;


    SilkCasket_compressDirectory(false, targetStr, targetStr, {true, true, true, true, true}, 11451409, true, "EFMod");

    env->ReleaseStringUTFChars(temp, tempStr);
    env->ReleaseStringUTFChars(path, pathStr);
    env->ReleaseStringUTFChars(target, targetStr);
}