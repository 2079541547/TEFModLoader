/*******************************************************************************
 * 文件名称: test_loader
 * 项目名称: EFMod
 * 创建时间: 25-5-9
 * 作者: EternalFuture゙
 * Gitlab: https://gitlab.com/2079541547/
 * 协议: Apache License 2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

#include <iostream>
#include <dlfcn.h>

#include "efmodloader/loader.hpp"
#include "efmodloader/logger.hpp"
#include "efmodloader/multi_channel.hpp"

void CustomLogOutput(const EFModLoader::Log::Record &record) {
    const auto time = std::chrono::system_clock::to_time_t(record.time);
    char timeStr[20];
    std::strftime(timeStr, sizeof(timeStr), "%Y-%m-%d %H:%M:%S", std::localtime(&time));

    std::cout << timeStr << " ["
            << EFModLoader::Log::LevelToString(record.level) << "] "
            << "[" << record.threadId << "] "
            << record.sourceFile << ":" << record.sourceLine << " ("
            << record.sourceFunction << ") - "
            << record.message << std::endl;
}

// 自定义加载函数
void* MyLoadFunc(const std::string& path) {
    std::cout << "Loading library: " << path << std::endl;
    // 这里使用系统特定的库加载函数
    return dlopen(path.c_str(), RTLD_LAZY);
}

// 自定义卸载函数
void MyUnloadFunc(void* handle) {
    std::cout << "Unloading library" << std::endl;
    dlclose(handle);
}

// 自定义符号查找函数
void* MySymbolFunc(void* handle, const std::string& name) {
    return dlsym(handle, name.c_str());
}

// 示例函数 - 有返回值
int calculateSum(const int a, const int b) {
    return a + b;
}

// 示例函数 - 无返回值(void)
void printMessage(const std::string& msg) {
    std::cout << "Message: " << msg << std::endl;
}

int main(int argc, char *argv[]) {

    EFModLoader::Log::Logger::SetOutputFunction(CustomLogOutput);
    EFModLoader::Log::Logger::SetMinLevel(EFModLoader::Log::Level::Debug);

    // 创建加载器实例
    EFModLoader::Loader loader(MyLoadFunc, MyUnloadFunc, MySymbolFunc);

    // 加载单个模块
    const std::string modId = loader.load("./libtest_efmod.so", "data/module1");
    if (modId.empty()) {
        std::cerr << "Failed to load module1" << std::endl;
        return 1;
    }

    // 初始化模块
    if (!loader.initialize(modId)) {
        std::cerr << "Failed to initialize module: " << modId << std::endl;
    }

    // 使用模块
    const auto channel = EFModLoader::LoaderMultiChannel::GetInstance();

    channel->send("sum_function", (void*)calculateSum);
    channel->send("print_function", (void*)printMessage);

    loader.send(modId);
    loader.receive(modId);
    loader.initializeAll();

}
