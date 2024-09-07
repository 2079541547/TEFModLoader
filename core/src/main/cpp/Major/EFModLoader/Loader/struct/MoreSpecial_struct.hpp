#pragma once

#include <iostream>
#include <fstream>
#include <vector>
#include <string>

using namespace std;

namespace MoreSpecial_struct
{
    struct target_MoreSpecial
    {
        string target;          // 被hook的位置
        vector<string> funName; // 调用的函数
        int mode;               // 返回的引索值（如果有），-1为全部并按顺序返回
    };

    struct ExecuteFile
    {
        int type; // mod语言， 0为java, 1为kotlin
        vector<target_MoreSpecial> data;
    };

    struct moreSpecial
    {
        vector<ExecuteFile> Data;
    };


       // 将moreSpecial结构体保存到二进制文件中
    void saveBinary(const MoreSpecial_struct::moreSpecial &ms, const string &filename)
    {
        ofstream file(filename, ios::out | ios::binary);
        if (!file.is_open())
        {
            cerr << "打开文件失败" << endl;
            return;
        }

        // 写入ExecuteFile的数量
        size_t executeFileCount = ms.Data.size();
        file.write(reinterpret_cast<const char*>(&executeFileCount), sizeof(executeFileCount));

        for (const auto &ef : ms.Data)
        {
            // 写入ExecuteFile的类型
            file.write(reinterpret_cast<const char*>(&ef.type), sizeof(ef.type));

            // 写入target_MoreSpecial的数量
            size_t targetCount = ef.data.size();
            file.write(reinterpret_cast<const char*>(&targetCount), sizeof(targetCount));

            for (const auto &target : ef.data)
            {
                // 写入target字符串的长度
                size_t targetLen = target.target.length();
                file.write(reinterpret_cast<const char*>(&targetLen), sizeof(targetLen));

                // 写入target字符串
                file.write(target.target.c_str(), targetLen);

                // 写入funName的数量
                size_t funNameCount = target.funName.size();
                file.write(reinterpret_cast<const char*>(&funNameCount), sizeof(funNameCount));

                for (const auto &fn : target.funName)
                {
                    // 写入每个funName字符串的长度
                    size_t fnLen = fn.length();
                    file.write(reinterpret_cast<const char*>(&fnLen), sizeof(fnLen));

                    // 写入每个funName字符串
                    file.write(fn.c_str(), fnLen);
                }

                // 写入mode
                file.write(reinterpret_cast<const char*>(&target.mode), sizeof(target.mode));
            }
        }

        file.close();
    }

    // 从二进制文件中加载moreSpecial结构体
    bool loadBinary(MoreSpecial_struct::moreSpecial &ms, const string &filename)
    {
        ifstream file(filename, ios::in | ios::binary);
        if (!file.is_open())
        {
            cerr << "打开文件失败" << endl;
            return false;
        }

        size_t executeFileCount;
        file.read(reinterpret_cast<char*>(&executeFileCount), sizeof(executeFileCount));
        ms.Data.resize(executeFileCount);

        for (size_t i = 0; i < executeFileCount; ++i)
        {
            file.read(reinterpret_cast<char*>(&ms.Data[i].type), sizeof(ms.Data[i].type));

            size_t targetCount;
            file.read(reinterpret_cast<char*>(&targetCount), sizeof(targetCount));
            ms.Data[i].data.resize(targetCount);

            for (size_t j = 0; j < targetCount; ++j)
            {
                size_t targetLen;
                file.read(reinterpret_cast<char*>(&targetLen), sizeof(targetLen));
                char *targetBuf = new char[targetLen + 1];
                file.read(targetBuf, targetLen);
                targetBuf[targetLen] = '\0'; // null 终止符
                ms.Data[i].data[j].target = string(targetBuf);
                delete[] targetBuf;

                size_t funNameCount;
                file.read(reinterpret_cast<char*>(&funNameCount), sizeof(funNameCount));
                ms.Data[i].data[j].funName.resize(funNameCount);

                for (size_t k = 0; k < funNameCount; ++k)
                {
                    size_t fnLen;
                    file.read(reinterpret_cast<char*>(&fnLen), sizeof(fnLen));
                    char *fnBuf = new char[fnLen + 1];
                    file.read(fnBuf, fnLen);
                    fnBuf[fnLen] = '\0'; // null 终止符
                    ms.Data[i].data[j].funName[k] = string(fnBuf);
                    delete[] fnBuf;
                }

                file.read(reinterpret_cast<char*>(&ms.Data[i].data[j].mode), sizeof(ms.Data[i].data[j].mode));
            }
        }

        file.close();
        return true;
    }
};