#pragma once

#include <iostream>
#include <vector>
#include <fstream>

using namespace std;

namespace EFModCpp_struct
{
    struct target_cpp
    {
        string target;          // 被hook的位置
        vector<string> funName; // 调用的函数
        int mode;               // 返回的引索值（如果有），-1为全部并按顺序返回
    };

    struct LibData
    {
        string ExecuteFile;      // mod执行文件名称
        vector<target_cpp> data; // 指定位置和执行的函数
    };

    struct efMod_cpp
    {
        vector<LibData> Data;
    };

    bool loadFromBinaryFile(const std::string &filename, efMod_cpp &mod)
    {
        std::ifstream file(filename, std::ios::binary | std::ios::in);
        if (!file.is_open())
        {
            std::cerr << "Failed to open file for reading." << std::endl;
            return false;
        }

        // 读取EFMod_cpp的Data大小
        size_t data_size = 0;
        file.read(reinterpret_cast<char *>(&data_size), sizeof(data_size));

        // 清空mod.Data以避免数据污染
        mod.Data.clear();

        // 根据读取的大小创建LibData对象并填充
        mod.Data.resize(data_size);
        for (auto &libData : mod.Data)
        {
            // 读取ExecuteFile的长度
            size_t exec_file_len = 0;
            file.read(reinterpret_cast<char *>(&exec_file_len), sizeof(exec_file_len));
            // 读取ExecuteFile的内容
            libData.ExecuteFile.resize(exec_file_len);
            file.read(&libData.ExecuteFile[0], exec_file_len);

            // 读取data的大小
            size_t targets_size = 0;
            file.read(reinterpret_cast<char *>(&targets_size), sizeof(targets_size));

            // 根据读取的大小创建target_cpp对象并填充
            libData.data.resize(targets_size);
            for (auto &target : libData.data)
            {
                // 读取target的长度
                size_t target_len = 0;
                file.read(reinterpret_cast<char *>(&target_len), sizeof(target_len));
                // 读取target的内容
                target.target.resize(target_len);
                file.read(&target.target[0], target_len);

                // 读取funName的大小
                size_t fun_names_size = 0;
                file.read(reinterpret_cast<char *>(&fun_names_size), sizeof(fun_names_size));

                // 根据读取的大小创建funName对象并填充
                target.funName.resize(fun_names_size);
                for (auto &fun_name : target.funName)
                {
                    // 读取fun_name的长度
                    size_t fun_name_len = 0;
                    file.read(reinterpret_cast<char *>(&fun_name_len), sizeof(fun_name_len));
                    // 读取fun_name的内容
                    fun_name.resize(fun_name_len);
                    file.read(&fun_name[0], fun_name_len);
                }

                // 读取mode
                file.read(reinterpret_cast<char *>(&target.mode), sizeof(target.mode));
            }
        }
        file.close();
        return true;
    }

    void saveToBinaryFile(const std::string &filename, const efMod_cpp &mod)
    {
        std::ofstream file(filename, std::ios::binary | std::ios::out);
        if (!file.is_open())
        {
            std::cerr << "Failed to open file for writing." << std::endl;
            return;
        }

        // 写入EFMod_cpp的Data大小
        size_t data_size = mod.Data.size();
        file.write(reinterpret_cast<const char *>(&data_size), sizeof(data_size));

        // 对每个LibData进行处理
        for (const auto &libData : mod.Data)
        {
            // 写入ExecuteFile的长度
            size_t exec_file_len = libData.ExecuteFile.size();
            file.write(reinterpret_cast<const char *>(&exec_file_len), sizeof(exec_file_len));
            // 写入ExecuteFile的内容
            file.write(libData.ExecuteFile.c_str(), exec_file_len);

            // 写入data的大小
            size_t targets_size = libData.data.size();
            file.write(reinterpret_cast<const char *>(&targets_size), sizeof(targets_size));

            // 对每个target_cpp进行处理
            for (const auto &target : libData.data)
            {
                // 写入target的长度
                size_t target_len = target.target.size();
                file.write(reinterpret_cast<const char *>(&target_len), sizeof(target_len));
                // 写入target的内容
                file.write(target.target.c_str(), target_len);

                // 写入funName的大小
                size_t fun_names_size = target.funName.size();
                file.write(reinterpret_cast<const char *>(&fun_names_size), sizeof(fun_names_size));

                // 对每个funName进行处理
                for (const auto &fun_name : target.funName)
                {
                    // 写入fun_name的长度
                    size_t fun_name_len = fun_name.size();
                    file.write(reinterpret_cast<const char *>(&fun_name_len), sizeof(fun_name_len));
                    // 写入fun_name的内容
                    file.write(fun_name.c_str(), fun_name_len);
                }

                // 写入mode
                file.write(reinterpret_cast<const char *>(&target.mode), sizeof(target.mode));
            }
        }
        file.close();
    }
};
