/*******************************************************************************
 * 文件名称: example_1
 * 项目名称: TEFModLoader
 * 创建时间: 2025/1/1
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
 * 描述信息: 本文件为TEFModLoader项目中的一部分。
 * 注意事项: 请严格遵守GNU AGPL v3.0协议使用本代码，任何未经授权的商业用途均属侵权行为。
 *******************************************************************************/
 
#include "../EFModLoader/includes/EFModLoader/EFMod/EFMod.hpp"
#include <android/log.h>
#include <map>
#include <BNM/Field.hpp>
#include "igc.hpp"

#define LOG_TAG "MyMod"
#define LOG(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__);

template<typename T>
void redirectPointer(uintptr_t originalPtrAddress, uintptr_t newPtrAddress) {
    T** originalPtr = reinterpret_cast<T**>(originalPtrAddress);
    *originalPtr = reinterpret_cast<T*>(newPtrAddress);
}

std::map<std::string, void*> MODAPI;

void a(BNM::UnityEngine::Object * instance) {
    BNM::Field<BNM::Structures::Mono::Array<bool>>* Deprecated = (BNM::Field<BNM::Structures::Mono::Array<bool>>*)MODAPI["Deprecated"];
    size_t size = 5456;
    bool* trueArray = new bool[size];
    for (size_t i = 0; i < size; ++i) {
        trueArray[i] = false;
    }
    BNM::Structures::Mono::Array<bool>* myArray = BNM::Structures::Mono::Array<bool>::Create(trueArray, size);
    redirectPointer<void*>(reinterpret_cast<uintptr_t>(Deprecated->GetPointer()), reinterpret_cast<uintptr_t>(myArray));
}

int b() {
    LOG("伤害已修改为%d", Limit_Damage());
    return Limit_Damage();
}

int c() {
    LOG("跳跃高度已修改为114514");
    return 114514;
}



class MyMod: public EFMod {
    
    ModMetadata getInfo() override {
            return {
                    "EXAMPLE_MOD_1",
                    "EternalFuture",
                    "1.0.0",
                    {{1, 20250101}}
            };
    }
    
    int run(EFModAPI *mod) override {
            LOG("获取的API的值:%p",
                mod->getAPI({
                                    "Assembly-CSharp.dll",
                                    "Terraria.ID",
                                    "ItemID",
                                    "None",
                                    "Field"
                            })
                );

        MODAPI["Deprecated"] =  mod->getAPI({
                                                    "Assembly-CSharp.dll",
                                                    "Terraria.ID",
                                                    "ItemID.Sets",
                                                    "Deprecated",
                                                    "Field"
                                            });

        return 0;
    }
    
    void RegisterAPI(EFModAPI *mod) override {
            mod->registerModApiDescriptor({
                                                  "Assembly-CSharp.dll",
                                                  "Terraria.ID",
                                                  "ItemID.Sets",
                                                  "Deprecated",
                                                  "Field"
                                          });
    }
    
    void RegisterExtend(EFModAPI *mod) override {
        mod->registerModFuncDescriptor({
            "Assembly-CSharp.dll",
            "Terraria.ID",
            "ItemID.Sets",
            ".cctor",
            "hook>>void",
            1,
            {
                    (void*)a
            }
        });

        mod->registerModFuncDescriptor({
                                               "Assembly-CSharp.dll",
                                               "Terraria",
                                               "Main",
                                               "DamageVar",
                                               "hook>>int",
                                               2,
                                               {
                                                       (void*)b
                                               }
        });


        mod->registerModFuncDescriptor({
                                               "Assembly-CSharp.dll",
                                               "Terraria",
                                               "Player",
                                               "get_jumpHeight",
                                               "hook>>int",
                                               0,
                                               {
                                                       (void*)c
                                               }
                                       });
    }
};

EFMod* CreateMod() {
        return new MyMod();
}