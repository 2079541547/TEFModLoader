//
// Created by eternalfuture on 2024/7/28.
//
#include <iostream>
#include <fstream>
#include "json.hpp"
#include "loadAndInvokeFunctions.cpp"

using json = nlohmann::json;



/*
void loadMod(const std::string data_path){

    auto j = json::parse(json_str);

    for (const auto& array : j) {
        const std::string& libname = array[0]["libname"];
        //std::cout << "libname: " << libname << std::endl;
        for (const auto& item : array[1]) {
            if (array[0]["enable"] == true && item["position"] == "Position1") {

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
*/



int loadMod_int(const std::string& json_content, const std::string& position) {
    try {
        auto j = json::parse(json_content); // 直接解析传入的 JSON 字符串

        int result = 0; // 初始化结果变量

        for (const auto& array : j) {
            const std::string& libname = array[0]["libname"];
            for (const auto& item : array[1]) {
                if (array[0]["enable"] == true && item["position"] == position) {
                    const std::vector<std::string>& functions = item["function"];
                    int arrays = item["arrays"];
                    std::vector<int> loaded_data = loadAndInvokeIntFunctions(libname, functions);
                    if (item["type"] != "void") {
                        result = getElement(loaded_data, arrays);
                        break;
                    }
                }
            }
        }

        return result;
    } catch (json::exception& e) {
        LOGE("JSON parsing error: %s", e.what());
        return 0;
    }
}


bool loadMod_bool(const std::string& json_content, const std::string& position) {
    try {
        auto j = json::parse(json_content); // 直接解析传入的 JSON 字符串

        int result = 0; // 初始化结果变量

        for (const auto &array: j) {
            const std::string &libname = array[0]["libname"];
            for (const auto &item: array[1]) {
                if (array[0]["enable"] == true && item["position"] == position) {
                    const std::vector<std::string> &functions = item["function"];
                    int arrays = item["arrays"];
                    std::vector<bool> loaded_data = loadAndInvokeBoolFunctions(libname, functions);
                    if (item["type"] != "void") {
                        result = getElement(loaded_data, arrays);
                        break;
                    }
                }
            }
        }

        return result;
    } catch (json::exception &e) {
        LOGE("JSON parsing error: %s", e.what());
        return false;
    }
}



/*
int main(){
    loadMod("/home/eternalfuture/测试/mod_data/Mod_data.json");
    return 0;
}
*/