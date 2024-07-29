//
// Created by eternalfuture on 2024/7/28.
//
#include <iostream>
#include <fstream>
#include "json.hpp"

using json = nlohmann::json;

// 模拟 LoadHH 函数
void LoadHH(const std::string& libname, const std::vector<std::string>& functions) {
    std::cout << "Loading library: " << libname << " with functions: ";
    for (const auto& func : functions) {
        std::cout << func << ", ";
    }
    std::cout << std::endl;
}

void getSZ(int arrays) {
    std::cout << "Array size: " << arrays << std::endl;
}



void loadMod(const std::string data_path){
    std::ifstream infile(data_path);
    if (!infile.is_open()) {
        std::cerr << "Error opening input file." << std::endl;
    }

    std::string json_str((std::istreambuf_iterator<char>(infile)),
                         std::istreambuf_iterator<char>());

    auto j = json::parse(json_str);

    for (const auto& array : j) {
        const std::string& libname = array[0]["libname"];
        std::cout << "libname: " << libname << std::endl;
        for (const auto& item : array[1]) {
            if (item["position"] == "Position1") {
                const std::vector<std::string>& functions = item["function"];
                int arrays = item["arrays"];
                LoadHH(libname, functions);
                if(item["type"] != "void"){
                    getSZ(arrays);
                }
                std::cout << "Function: ";
                for (const auto& func : functions) {
                    std::cout << func << ", ";
                }
                std::cout << " Type: " << item["type"] << " Arrays: " << arrays << std::endl;
            }
        }
    }
    infile.close();
}

