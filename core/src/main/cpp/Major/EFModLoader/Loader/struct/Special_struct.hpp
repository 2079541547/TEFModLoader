#pragma once

#include <iostream>
#include <fstream>
#include <vector>
#include <string>

using namespace std;

namespace Special_struct
{

    struct special
    {
        string ExecuteFile; // mod执行文件名称
    };

    struct special_cpp
    {
        vector<special> Data;
    };

    // 将special_cpp结构体保存到二进制文件中
    void saveBinary(const Special_struct::special_cpp &scp, const string &filename)
    {
        ofstream file(filename, ios::out | ios::binary);
        if (!file.is_open())
        {
            cerr << "打开文件失败" << endl;
            return;
        }

        // 写入Data向量的大小
        size_t dataCount = scp.Data.size();
        file.write(reinterpret_cast<const char *>(&dataCount), sizeof(dataCount));

        // 对每个special结构体进行写入
        for (const auto &sp : scp.Data)
        {
            // 写入ExecuteFile字符串的长度
            size_t executeFileLen = sp.ExecuteFile.length();
            file.write(reinterpret_cast<const char *>(&executeFileLen), sizeof(executeFileLen));

            // 写入ExecuteFile字符串
            file.write(sp.ExecuteFile.c_str(), executeFileLen);
        }

        file.close();
    }

    // 从二进制文件中加载special_cpp结构体
    bool loadBinary(Special_struct::special_cpp &scp, const string &filename)
    {
        ifstream file(filename, ios::in | ios::binary);
        if (!file.is_open())
        {
            cerr << "打开文件失败" << endl;
            return false;
        }

        // 读取Data向量的大小
        size_t dataCount;
        file.read(reinterpret_cast<char *>(&dataCount), sizeof(dataCount));
        scp.Data.resize(dataCount);

        // 对每个special结构体进行读取
        for (size_t i = 0; i < dataCount; ++i)
        {
            // 读取ExecuteFile字符串的长度
            size_t executeFileLen;
            file.read(reinterpret_cast<char *>(&executeFileLen), sizeof(executeFileLen));

            // 动态分配内存读取ExecuteFile字符串
            char *executeFileBuf = new char[executeFileLen + 1];
            file.read(executeFileBuf, executeFileLen);
            executeFileBuf[executeFileLen] = '\0'; // null 终止符
            scp.Data[i].ExecuteFile = string(executeFileBuf);

            delete[] executeFileBuf;
        }

        file.close();
        return true;
    }

};
