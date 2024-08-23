//
// Created by eternalfuture on 2024/7/27.
//
#include <dlfcn.h>
#include <iostream>
#include <vector>
#include <string>
#include <BNM/Loading.hpp>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>
#include <dlfcn.h>
#include <stdexcept>

int getElement(const std::vector<int>& results, size_t index) {
    if (index >= results.size()) {
        throw std::out_of_range("无效索引");
    }
    return results[index];
}

bool getElement(const std::vector<bool>& results, size_t index) {
    if (index >= results.size()) {
        throw std::out_of_range("无效索引");
    }
    return results[index];
}

std::string getElement(const std::vector<std::string>& results, size_t index) {
    if (index >= results.size()) {
        throw std::out_of_range("无效索引");
    }
    return results[index];
}

float getElement(const std::vector<float>& results, size_t index) {
    if (index >= results.size()) {
        throw std::out_of_range("无效索引");
    }
    return results[index];
}
