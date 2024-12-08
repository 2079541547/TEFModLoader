#include <iostream>
#include "../SilkCasket.hpp"
#include <jni.h>


extern "C" {


// 辅助函数：从jstring转换为std::string
std::string getStringFromJString(JNIEnv* env, jstring jStr) {
    if (jStr == nullptr) return "";
    const char* cStr = env->GetStringUTFChars(jStr, NULL);
    std::string str(cStr);
    env->ReleaseStringUTFChars(jStr, cStr);
    return str;
}

// 辅助函数：将Java Map转换为C++ map
std::map<std::filesystem::path, std::filesystem::path> convertJavaMapToCppMap(JNIEnv* env, jobject javaMap) {
    std::map<std::filesystem::path, std::filesystem::path> cppMap;

    jclass hashMapClass = env->FindClass("java/util/HashMap");
    jmethodID getKeyMethod = env->GetMethodID(hashMapClass, "keySet", "()Ljava/util/Set;");
    jobject keySet = env->CallObjectMethod(javaMap, getKeyMethod);

    jclass setClass = env->FindClass("java/util/Set");
    jmethodID iteratorMethod = env->GetMethodID(setClass, "iterator", "()Ljava/util/Iterator;");
    jobject iterator = env->CallObjectMethod(keySet, iteratorMethod);

    jclass iteratorClass = env->FindClass("java/util/Iterator");
    jmethodID hasNextMethod = env->GetMethodID(iteratorClass, "hasNext", "()Z");
    jmethodID nextMethod = env->GetMethodID(iteratorClass, "next", "()Ljava/lang/Object;");

    while (env->CallBooleanMethod(iterator, hasNextMethod)) {
        jobject keyObj = env->CallObjectMethod(iterator, nextMethod);
        jstring keyStr = static_cast<jstring>(keyObj);
        jstring valueStr = static_cast<jstring>(env->CallObjectMethod(javaMap, env->GetMethodID(hashMapClass, "get", "(Ljava/lang/Object;)Ljava/lang/Object;"), keyObj));

        cppMap[std::filesystem::path(getStringFromJString(env, keyStr))] = std::filesystem::path(getStringFromJString(env, valueStr));
    }

    return cppMap;
}

// 辅助函数：从jbooleanArray转换为MODE结构
SilkCasket::Compress::Mode::MODE convertJBooleanArrayToMode(JNIEnv* env, jbooleanArray jBoolArray) {
    SilkCasket::Compress::Mode::MODE mode;
    jsize len = env->GetArrayLength(jBoolArray);
    if (len != 5) {
        // Handle error: mode array should have exactly 5 elements.
        // For now, we set default values.
        return mode;
    }

    jboolean* elements = env->GetBooleanArrayElements(jBoolArray, NULL);
    mode.Storage = static_cast<bool>(elements[0]);
    mode.LZMA2FAST = static_cast<bool>(elements[1]);
    mode.LZ4 = static_cast<bool>(elements[2]);
    mode.LZW = static_cast<bool>(elements[3]);
    mode.LIZARD = static_cast<bool>(elements[4]);
    env->ReleaseBooleanArrayElements(jBoolArray, elements, 0);

    return mode;
}

// 辅助函数：通用压缩函数调用
void callCompressFunction(JNIEnv* env, jboolean suffix, jstring targetPath, jstring outPath, jbooleanArray mode, jlong blockSize, jboolean entryEncryption, jstring key, void (*compressFunc)(bool, const std::filesystem::path&, std::filesystem::path, SilkCasket::Compress::Mode::MODE, size_t, bool, const std::string&)) {
    compressFunc(
            static_cast<bool>(suffix),
            std::filesystem::path(getStringFromJString(env, targetPath)),
            std::filesystem::path(getStringFromJString(env, outPath)),
            convertJBooleanArrayToMode(env, mode),
            static_cast<size_t>(blockSize),
            static_cast<bool>(entryEncryption),
            getStringFromJString(env, key)
    );
}

// JNI 桥接函数实现
JNIEXPORT void JNICALL Java_silkways_terraria_efmodloader_logic_efmod_SilkCasket_compressDirectory
        (JNIEnv *env, jclass clazz, jboolean suffix, jstring targetPath, jstring outPath, jbooleanArray mode, jlong blockSize, jboolean entryEncryption, jstring key) {
    callCompressFunction(env, suffix, targetPath, outPath, mode, blockSize, entryEncryption, key, &SilkCasket_compressDirectory);
}

JNIEXPORT void JNICALL Java_silkways_terraria_efmodloader_logic_efmod_SilkCasket_compressAFile
        (JNIEnv *env, jclass clazz, jboolean suffix, jstring targetPath, jstring outPath, jbooleanArray mode, jlong blockSize, jboolean entryEncryption, jstring key) {
    callCompressFunction(env, suffix, targetPath, outPath, mode, blockSize, entryEncryption, key, &SilkCasket_compress_A_File);
}

JNIEXPORT void JNICALL Java_silkways_terraria_efmodloader_logic_efmod_SilkCasket_compressFiles
        (JNIEnv *env, jclass clazz, jboolean suffix, jobject targetPathsObj, jstring outPath, jbooleanArray mode, jlong blockSize, jboolean entryEncryption, jstring key) {
    auto targetPaths = convertJavaMapToCppMap(env, targetPathsObj);
    auto modeStruct = convertJBooleanArrayToMode(env, mode);
    SilkCasket_compress_Files(
            static_cast<bool>(suffix),
            targetPaths,
            std::filesystem::path(getStringFromJString(env, outPath)),
            modeStruct,
            static_cast<size_t>(blockSize),
            static_cast<bool>(entryEncryption),
            getStringFromJString(env, key)
    );
}

JNIEXPORT void JNICALL Java_silkways_terraria_efmodloader_logic_efmod_SilkCasket_compress
        (JNIEnv *env, jclass clazz, jboolean suffix, jobject targetPathsObj, jstring outPath, jbooleanArray mode, jlong blockSize, jboolean entryEncryption, jstring key) {
    auto targetPaths = convertJavaMapToCppMap(env, targetPathsObj);
    auto modeStruct = convertJBooleanArrayToMode(env, mode);
    SilkCasket_compress(
            static_cast<bool>(suffix),
            targetPaths,
            std::filesystem::path(getStringFromJString(env, outPath)),
            modeStruct,
            static_cast<size_t>(blockSize),
            static_cast<bool>(entryEncryption),
            getStringFromJString(env, key)
    );
}

JNIEXPORT void JNICALL Java_silkways_terraria_efmodloader_logic_efmod_SilkCasket_releaseAllEntry
        (JNIEnv *env, jclass clazz, jstring filePath, jstring outPath, jstring key) {
    releaseAllEntry(
            std::filesystem::path(getStringFromJString(env, filePath)),
            std::filesystem::path(getStringFromJString(env, outPath)),
            getStringFromJString(env, key)
    );
}

JNIEXPORT void JNICALL Java_silkways_terraria_efmodloader_logic_efmod_SilkCasket_releaseEntry
        (JNIEnv *env, jclass clazz, jstring filePath, jstring entry, jstring outPath, jstring key) {
    releaseEntry(
            std::filesystem::path(getStringFromJString(env, filePath)),
            getStringFromJString(env, entry),
            std::filesystem::path(getStringFromJString(env, outPath)),
            getStringFromJString(env, key)
    );
}

JNIEXPORT void JNICALL Java_silkways_terraria_efmodloader_logic_efmod_SilkCasket_releaseFolder
        (JNIEnv *env, jclass clazz, jstring filePath, jstring entry, jstring outPath, jstring key) {
    releaseFolder(
            std::filesystem::path(getStringFromJString(env, filePath)),
            getStringFromJString(env, entry),
            std::filesystem::path(getStringFromJString(env, outPath)),
            getStringFromJString(env, key)
    );
}

JNIEXPORT jbyteArray JNICALL Java_silkways_terraria_efmodloader_logic_efmod_SilkCasket_getEntryData
        (JNIEnv *env, jclass clazz, jstring filePath, jstring entry, jstring key) {
    auto data = get_entry_data(
            std::filesystem::path(getStringFromJString(env, filePath)),
            getStringFromJString(env, entry),
            getStringFromJString(env, key)
    );

    jbyteArray result = env->NewByteArray(data.size());
    env->SetByteArrayRegion(result, 0, data.size(), reinterpret_cast<const jbyte*>(data.data()));
    return result;
}
}