/*******************************************************************************
 * 文件名称: test_efmod
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

#include "efmod_core.hpp"

#include <iostream>

void hello() {
    std::cout << "MyMod Hello!" << std::endl;
}

class MyMod final : public EFMod {
public:
    int Initialize(const std::string &path, MultiChannel *multiChannel) override { return 0; }

    void Send(const std::string &path, MultiChannel *multiChannel) override {
        multiChannel->send("Hello函数", (void*)hello);
    }

    void Receive(const std::string &path, MultiChannel *multiChannel) override {
        // multiChannel->receiveFunction<void>("print_function", "Hello from multi-channel!");

        const int result = multiChannel->receive<int(*)(int, int)>("sum_function")(10, 20);
        std::cout << "Sum result: " << result << std::endl;
    }

    int Load(const std::string &path, MultiChannel *multiChannel) override { return 0; }
    int UnLoad(const std::string &path, MultiChannel *multiChannel) override { return 0; }

    Metadata GetMetadata() override {
        return {
            "MyMod",
            "EternalFuture゙",
            "1.0.0",
            20250509,
            ModuleType::Library,
            {
                false
            }
        };
    };
};

EFMod *CreateMod() {
    static MyMod Mod;
    return &Mod;
}
