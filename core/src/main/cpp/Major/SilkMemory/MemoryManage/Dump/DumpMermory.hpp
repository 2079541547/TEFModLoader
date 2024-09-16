//
// Created by eternalfuture on 2024/9/16.
//

#ifndef SILKMEMORY_DUMPMERMORY_HPP
#define SILKMEMORY_DUMPMERMORY_HPP

#include <iostream>
#include <fstream>
#include <cstring>
#include <unistd.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <sys/mman.h>
#include <vector>
#include <string>
#include <sstream>
#include <filesystem>

namespace DumpMermory{

    using namespace std;

    // 将指针内容转储到文件
    void dumpMemoryToFile(void* address, size_t length, const std::string& filename) {
        std::ofstream file(filename, std::ios::binary | std::ios::out);
        if (!file.is_open()) {
            std::cerr << "无法打开文件: " << filename << std::endl;
            return;
        }

        // 写入内存区域的内容
        file.write(static_cast<const char*>(address), length);
        file.close();

        std::cout << "已将内存内容转储到文件: " << filename << std::endl;
    }

    template<typename T>
    void dumpMemoryVariableToFile(void* address, const std::string& filename) {
        std::ofstream file(filename, std::ios::binary | std::ios::out);
        if (!file.is_open()) {
            std::cerr << "无法打开文件: " << filename << std::endl;
            return;
        }

        // 计算数据的大小
        size_t length = sizeof(T);

        // 写入内存区域的内容
        file.write(static_cast<const char*>(address), length);
        file.close();

        std::cout << "已将内存内容转储到文件: " << filename << std::endl;
    }

    void dumpMemoryVariableToFile(void* address, size_t length, const std::string& filename) {
        std::ofstream file(filename, std::ios::binary | std::ios::out);
        if (!file.is_open()) {
            std::cerr << "无法打开文件: " << filename << std::endl;
            return;
        }

        // 写入内存区域的内容
        file.write(static_cast<const char*>(address), length);
        file.close();

        std::cout << "已将内存内容转储到文件: " << filename << std::endl;
    }


    // 创建目录（如果不存在）
    void createDirectoryIfNotExists(const std::string& directoryPath) {
        if (!std::filesystem::exists(directoryPath)) {
            if (!std::filesystem::create_directories(directoryPath)) {
                std::cerr << "无法创建目录: " << directoryPath << std::endl;
                return;
            }
            std::cout << "已创建目录: " << directoryPath << std::endl;
        }
    }


};

#endif //SILKMEMORY_DUMPMERMORY_HPP
