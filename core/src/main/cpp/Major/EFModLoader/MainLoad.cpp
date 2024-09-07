#include <iostream>
#include "Loader/struct/EFModCpp_struct.hpp"
#include "Loader/LoadFunction/Pointer.hpp"
#include "Loader/struct/MoreSpecial_struct.hpp"
#include "Loader/struct/Special_struct.hpp"
#include <stdarg.h>
#include <cassert>

using namespace std;

void printHelloWorld(int a, double b, const char *c)
{
    std::cout << "Called with: " << a << ", " << b << ", " << c << std::endl;
}

void EFModCpp()
{
    EFModCpp_struct::efMod_cpp mod;
    mod.Data.push_back({"example.exe", {{"hook_point1", {"func1", "func2"}, 1}, {"hook_point2", {"func3"}, -1}}});
    mod.Data.push_back({"another_example.exe", {{"hook_point3", {}, 0}}});

    EFModCpp_struct::saveToBinaryFile("mod_data.bin", mod);

    if (EFModCpp_struct::loadFromBinaryFile("mod_data.bin", mod))
    {
        // 打印加载的数据
        for (const auto &libData : mod.Data)
        {
            cout << "ExecuteFile: " << libData.ExecuteFile << endl;
            for (const auto &target : libData.data)
            {
                cout << "  Target: " << target.target << endl;
                for (const auto &fun_name : target.funName)
                {
                    cout << "    Function: " << fun_name << endl;
                }
                cout << "  Mode: " << target.mode << endl;
            }
        }
    }
    else
    {
        cout << "Failed to load data from binary file." << endl;
    }
}

void FunctionPointer()
{
    // 获取函数指针作为 uintptr_t
    uintptr_t funcPtr = reinterpret_cast<uintptr_t>(&printHelloWorld);
    uintptr_t funPtr1 = reinterpret_cast<uintptr_t>(&Pointer::readPointerFromFile);


    Pointer::writePointersToFile({funcPtr, funPtr1}, "api_data.bin");
    

    cout << hex << Pointer::readPointerFromFile("api_data.bin", 1) << dec << endl;
}

void moreSpecial()
{
    // 创建一个moreSpecial实例并填充数据
    MoreSpecial_struct::moreSpecial ms;
    ms.Data.push_back({0, {{"target1", {"fn1", "fn2"}, 1}, {"target2", {"fn3"}, -1}}});
    ms.Data.push_back({1, {{"target3", {"fn4"}, 0}, {"target4", {}, -1}}});

    // 保存数据到二进制文件
    MoreSpecial_struct::saveBinary(ms, "more_special.bin");

    // 清空数据以便测试读取
    ms.Data.clear();

    // 从文件中读取数据
    if (MoreSpecial_struct::loadBinary(ms, "more_special.bin"))
    {
        // 打印读取的数据
        for (const auto &ef : ms.Data)
        {
            cout << "Type: " << ef.type << endl;
            for (const auto &target : ef.data)
            {
                cout << "Target: " << target.target << endl;
                cout << "Mode: " << target.mode << endl;
                cout << "FunNames: ";
                for (const auto &fn : target.funName)
                {
                    cout << fn << " ";
                }
                cout << endl;
            }
        }
    }
}

void special_cpp()
{
    // 创建一个special_cpp实例并填充数据
    Special_struct::special_cpp scp;
    scp.Data.push_back({"example_mod_file1"});
    scp.Data.push_back({"example_mod_file2"});

    // 保存数据到二进制文件
    Special_struct::saveBinary(scp, "special_cpp.bin");

    // 创建一个新的special_cpp实例，用于读取
    Special_struct::special_cpp loadedSpecialCpp;

    // 从文件中读取数据
    if (Special_struct::loadBinary(loadedSpecialCpp, "special_cpp.bin"))
    {
        // 打印读取的数据
        for (const auto &sp : loadedSpecialCpp.Data)
        {
            cout << "ExecuteFile: " << sp.ExecuteFile << endl;
        }
    }
}

int main()
{
    EFModCpp();
    FunctionPointer();
    moreSpecial();
    special_cpp();

    auto funcPtr = Pointer::readPointerFromFile("api_data.bin", 0);
    Pointer::callFunction<void>(reinterpret_cast<void*>(funcPtr), 10, 20.5, "Hello World");    


    return 0;
}