/*******************************************************************************
 * 文件名称: recipe
 * 项目名称: ForgottenItem
 * 创建时间: 25-5-24
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
 
#pragma once

#include <unordered_set>

#include "Item.hpp"
#include "TEFMod.hpp"

namespace Recipe {

    inline TEFMod::Method<void>* ItemSetDefaults;
    inline TEFMod::Method<void>* AddRecipe;
    inline TEFMod::Field<int>* stack;
    inline TEFMod::Field<void*>* createItem;
    inline TEFMod::Field<void*>* currentRecipe;
    inline TEFMod::Field<void*>* requiredItem;
    inline TEFMod::Field<void*>* requiredTile;

    inline TEFMod::Field<void*>* IsAMaterial;

    struct RecipeConfig {
        int resultItemID;
        std::vector<std::pair<int, int>> materials; // {itemID, stack}
        int requiredTileID = -1;
        int resultStack = 1;
    };

    inline std::unordered_set<int> materialItems;

    void AddRecipeGroup(const TEFMod::recipe &config);

    void SetupRecipeGroups(TEFMod::TerrariaInstance instance);
    inline void (*original_SetupRecipeGroups)(TEFMod::TerrariaInstance) = nullptr;
    auto Template(TEFMod::TerrariaInstance instance) -> void;

    inline TEFMod::HookTemplate HookTemplate_SetupRecipeGroups = {
        reinterpret_cast<void*>(Template),
        {}
    };

    void init(TEFMod::TEFModAPI* api);
}