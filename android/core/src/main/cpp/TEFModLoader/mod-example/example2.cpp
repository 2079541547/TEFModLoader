/*******************************************************************************
 * 文件名称: example1
 * 项目名称: TEFModLoader
 * 创建时间: 2025/5/17
 * 作者: EternalFuture゙
 * Github: https://github.com/2079541547
 * 版权声明: Copyright © 2024 EternalFuture. All rights reserved.
 * 许可证: This program is free software: you can redistribute it and/or modify
 *         it under the terms of the GNU Affero General Public License as published
 *         by the Free Software Foundation, either version 3 of the License, or
 *         (at your option) any later version.
 *
 *         This program is distributed in the hope that it will be useful,
 *         but WITHOUT ANY WARRANTY; without even the implied warranty of
 *         MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *         GNU Affero General Public License for more details.
 *
 *         You should have received a copy of the GNU Affero General Public License
 *         along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * 注意事项: 请严格遵守GNU AGPL v3.0协议使用本代码，任何未经授权的商业用途均属侵权行为。
 *******************************************************************************/

#include <random>

#include "efmod_core.hpp"

#include "logger_api.hpp"
#include "debug_tool_api.hpp"
#include "tefmod_api.hpp"
#include "base_type_api.hpp"

TEFMod::Logger* g_log;
TEFMod::DebugTool* g_debug_tool;
TEFMod::TEFModAPI* g_api;
static EFMod* mod;

TEFMod::Field<int>*(*ParseFromPointer_Field_Int)(void*);
TEFMod::Field<int>* shoot;

TEFMod::Method<int>*(*ParseFromPointer_Method_Int)(void*);
TEFMod::Method<int>* buyPrice;

int count = 0;
void SetDefaults(TEFMod::TerrariaInstance i, int t, bool n, TEFMod::TerrariaInstance* v) {
    static std::random_device rd;
    static std::mt19937 gen(rd());
    static std::uniform_int_distribution<int> dist(1, 1021);
    int random_num = dist(gen);

    g_log->i("价值: ", buyPrice->Call(nullptr, 4, 1, 5, 6, 7));

    shoot->Set(random_num, i);
    g_log->i("已设置弹幕为: ", shoot->Get(i));


}

void (*old_SetDefaults)(TEFMod::TerrariaInstance, int, bool, TEFMod::TerrariaInstance);
void SetDefaults_T(TEFMod::TerrariaInstance i, int t, bool n, TEFMod::TerrariaInstance v);

inline TEFMod::HookTemplate T_SetDefaults {
        (void*) SetDefaults_T,
        {  }
};

void SetDefaults_T(TEFMod::TerrariaInstance i, int t, bool n, TEFMod::TerrariaInstance v) {
    old_SetDefaults(i, t, n, v);
    for (auto fun: T_SetDefaults.FunctionArray) {
        if(fun) ((void(*)(void*, int, bool, TEFMod::TerrariaInstance))fun)(i, t, n, v);
    }
}


class MyMod: public EFMod {
public:

    int Initialize(const std::string &path, MultiChannel *multiChannel) override {
        return 0;
    }

    void Send(const std::string &path, MultiChannel *multiChannel) override {
        g_api->registerFunctionDescriptor({
            "Terraria",
            "Item",
            "SetDefaults",
            "hook>>void",
            3,
            &T_SetDefaults,
            { (void*)SetDefaults }
        });

        g_api->registerApiDescriptor({
                                             "Terraria",
                                             "Item",
                                             "shoot",
                                             "Field"
        });

        g_api->registerApiDescriptor({
                                             "Terraria",
                                             "Item",
                                             "buyPrice",
                                             "Method",
                                             4
        });
    }

    void Receive(const std::string &path, MultiChannel *multiChannel) override {

        old_SetDefaults = g_api->GetAPI<void(*)(TEFMod::TerrariaInstance, int, bool, TEFMod::TerrariaInstance)>({
            "Terraria",
                    "Item",
                    "SetDefaults",
                    "old_fun",
                    3
        });

        buyPrice = ParseFromPointer_Method_Int(g_api->GetAPI<void*>({
            "Terraria",
            "Item",
            "buyPrice",
            "Method",
            4
        }));

        shoot = ParseFromPointer_Field_Int(g_api->GetAPI<void*>({
            "Terraria",
            "Item",
            "shoot",
            "Field"
        }));

    }

    int Load(const std::string &path, MultiChannel *multiChannel) override {
        g_debug_tool = multiChannel->receive<TEFMod::DebugTool*>("TEFMod::DebugTool");
        g_log = multiChannel->receive<TEFMod::Logger*(*)(const std::string& Tag, const std::string& filePath, const std::size_t maxCache)>("TEFMod::CreateLogger")("MyMod-EternalFuture", "", 114514);
        g_api = multiChannel->receive<TEFMod::TEFModAPI*>("TEFMod::TEFModAPI");
        ParseFromPointer_Field_Int = multiChannel->receive<TEFMod::Field<int>*(*)(void*)>("TEFMod::Field<Int>::ParseFromPointer");
        ParseFromPointer_Method_Int = multiChannel->receive<TEFMod::Method<int>*(*)(void*)>("TEFMod::Method<Int>::ParseFromPointer");


        g_log->init();
        g_log->i("已获取ParseFromPointer_Field_Int: ", (void*)ParseFromPointer_Field_Int);
        g_log->i("Hello, ", mod, "!");
        g_debug_tool->printSystemInfo(g_log);
        return 0;
    }

    int UnLoad(const std::string &path, MultiChannel *multiChannel) override {
        return 0;
    }

    Metadata GetMetadata() override {
        return {
                "MyMod",
                "EternalFuture゙",
                "1.0.0",
                20250517,
                ModuleType::Game,
                {
                        false
                }
        };
    }
};

EFMod* CreateMod() {
    if (!mod) {
        mod = new MyMod();
    }
    return mod;
}