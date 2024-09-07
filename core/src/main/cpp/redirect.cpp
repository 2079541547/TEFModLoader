#include <jni.h>
#include <iostream>
#include <fstream>
#include <cstring>




using namespace std;

// 通用函数声明
void dumpPointerToFile(const void* ptr, size_t size, const string& filename);
bool is_pointer_valid(const void* ptr);

// 将指针内容转储到文件
void dumpPointerToFile(const void* ptr, size_t size, const string& filename) {
    ofstream file(filename, ios::binary | ios::out);
    if (!file.is_open()) {
        cerr << "无法打开文件: " << filename << endl;
        return;
    }

    // 写入指针内容
    file.write(reinterpret_cast<const char*>(ptr), size);
    file.close();

    cout << "已将指针内容转储到文件: " << filename << endl;
}

// 检查指针是否有效
bool is_pointer_valid(const void* ptr) {
    // 检查指针是否为nullptr
    return ptr != nullptr;
}

// JNI 函数实现
extern "C"
JNIEXPORT void JNICALL
Java_silkways_terraria_toolbox_ui_debug_LoadDebug_dumpzhiz(JNIEnv *env, jobject thiz,
                                                           jstring string, jobject size) {
    // 获取指针地址字符串
    const char* pointerAddress = env->GetStringUTFChars(string, nullptr);

    // 获取大小
    jint sizeValue = env->CallIntMethod(size, env->GetMethodID(env->GetObjectClass(size), "intValue", "()I"));

    // 将字符串转换为指针
    void* ptr = reinterpret_cast<void*>(strtoull(pointerAddress, nullptr, 16));

    // 检查指针是否有效
    if (!is_pointer_valid(ptr)) {

        cerr << "警告: 指定的指针地址无效或无法访问。\n";
        env->ReleaseStringUTFChars(string, pointerAddress);
        return;
    }

    //文件路径
    std::string filePath = "/data/data/silkways.terraria.toolbox/";

    // 创建文件名
    std::string filename = filePath + std::string("dump_") + pointerAddress + ".bin";

    // 调用函数转储指针内容到文件
    dumpPointerToFile(ptr, sizeValue, filename);

    // 释放字符串资源
    env->ReleaseStringUTFChars(string, pointerAddress);
}

// 0x150b9f5d0