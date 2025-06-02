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

#include "recipe.hpp"

#include <sstream>

#include <tefmod-api/base_type.hpp>
#include <tefmod-api/item.hpp>
#include <logger.hpp>
#include <item_manager.hpp>


inline TEFMod::Field<int>* (*ParseIntField)(void*) = TEFModLoader::IL2CPP_Field<int>::ParseFromPointer;
inline TEFMod::Field<void*>* (*ParseOtherField)(void*) = TEFModLoader::IL2CPP_Field<void*>::ParseFromPointer;

inline TEFMod::Method<void>* (*ParseVoidMethod)(void*) = TEFModLoader::IL2CPP_Method<void>::ParseFromPointer;

inline TEFMod::Array<int>* (*ParseIntArray)(void*) = TEFModLoader::IL2CPP_Array<int>::ParseFromPointer;
inline TEFMod::Array<bool>* (*ParseBoolArray)(void*) = TEFModLoader::IL2CPP_Array<bool>::ParseFromPointer;
inline TEFMod::Array<void*>* (*ParseOtherArray)(void*) = TEFModLoader::IL2CPP_Array<void*>::ParseFromPointer;

void Recipe::AddRecipeGroup(const TEFMod::recipe &config) {{
        std::ostringstream oss;

        // 🏗️ 构建配方日志头部
        oss << "\n┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n"
            << "┃ 🛠️ 开始配置配方组 → 产出物品ID: " << config.result_item_id << "\n"
            << "┃   所需工作台: " << config.required_tile_id
            << " | 材料数量: " << config.materials.size() << "\n"
            << "┃ 🔧 正在收集材料物品ID...\n";
        for (const auto& [itemID, stackSize] : config.materials) {
            materialItems.insert(itemID); // 将材料ID添加到集合
            oss << "┃    ├─ 已标记物品ID: " << itemID << " 为材料\n";
        }

        // 🔧 技术细节日志（T级）
        oss << "┃ 🔧 [技术细节] 正在获取配方实例指针...\n";
        auto recipePtr = currentRecipe->Get();
        oss << "┃    ├─ currentRecipe 地址: 0x" << std::hex << reinterpret_cast<uintptr_t>(recipePtr) << "\n";

        requiredItem->SetInstance(recipePtr);
        createItem->SetInstance(recipePtr);
        requiredTile->SetInstance(recipePtr);
        oss << "┃    └─ 实例设置完成\n";

        // 📦 解析材料数组
        oss << "┃ 🔧 [技术细节] 正在解析材料数组...\n";
        const auto requiredItem_v = ParseOtherArray(requiredItem->Get(currentRecipe->Get()));
        const auto requiredTile_v = ParseIntArray(requiredTile->Get(currentRecipe->Get()));
        oss << "┃    ├─ requiredItem 数组大小: " << std::dec << requiredItem_v->Size() << "\n"
            << "┃    └─ requiredTile 数组大小: " << requiredTile_v->Size() << "\n";

        // 🎯 设置产出物品
        auto createItemPtr = createItem->Get();
        oss << "┃ 🎯 设置产出物品 → ID: " << config.result_item_id << ", 数量: " << config.result_stack << "\n"
            << "┃    ├─ createItem 实例地址: 0x" << std::hex << reinterpret_cast<uintptr_t>(createItemPtr) << "\n";
        stack->Set(config.result_stack, createItemPtr);
        ItemSetDefaults->Call(createItemPtr, 1, config.result_item_id);
        oss << "┃    └─ 已调用 ItemSetDefaults\n";

        // ⚙️ 设置材料
        oss << "┃ ⚙️ 材料配置 → 需要 " << std::dec << config.materials.size() << " 种材料\n";
        for (size_t i = 0; i < config.materials.size(); ++i) {
            const auto& [itemID, stackSize] = config.materials[i];
            auto materialSlotPtr = requiredItem_v->at(i);

            oss << "┃    ├─ 材料" << i+1 << ": ID=" << itemID << " × " << stackSize << "\n"
                << "┃    │   ├─ 槽位地址: 0x" << std::hex << reinterpret_cast<uintptr_t>(materialSlotPtr) << "\n";

            ItemSetDefaults->Call(materialSlotPtr, 1, itemID);
            oss << "┃    │   ├─ 已设置基础属性\n";

            if (stackSize > 1) {
                stack->SetInstance(materialSlotPtr);
                stack->Set(stackSize);
                oss << "┃    │   └─ 已设置堆叠数量: " << std::dec << stackSize << "\n";
            }
        }

        // 🪚 设置工作台
        oss << "┃ 🪚 需要工作台 → ID: " << std::dec << config.required_tile_id << "\n"
            << "┃    ├─ 原始工作台值: " << requiredTile_v->at(0) << "\n";
        requiredTile_v->at(0) = config.required_tile_id;
        oss << "┃    └─ 新工作台值: " << requiredTile_v->at(0) << "\n";

        // 📝 提交配方
        oss << "┃ 🔧 [技术细节] 正在提交配方...\n";
        AddRecipe->Call(nullptr, 0, 0);
        oss << "┃ ✅ 配方提交完成\n"
            << "┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n";

        LOGF_INFO("{}", oss.str());
    }}

void Recipe::SetupRecipeGroups(TEFMod::TerrariaInstance instance) {

    for (const auto& recipe : TEFModLoader::ItemManager::GetInstance()->get_all_recipe()) {
        AddRecipeGroup(recipe);
    }

    const auto is_a_material = ParseBoolArray(IsAMaterial->Get());
    for (const auto item: materialItems) {
        is_a_material->set(item, true);
    }

    is_a_material->clear();

    LOGF_INFO("已设置材料属性");

    TEFModLoader::item_manager::need_flush_localized = true;
}

void Recipe::Template(const TEFMod::TerrariaInstance instance) {
    original_SetupRecipeGroups(instance);
    for (const auto fun : HookTemplate_SetupRecipeGroups.FunctionArray) {
        reinterpret_cast<void(*)(TEFMod::TerrariaInstance)>(fun)(instance);
    }
}

void Recipe::init(TEFMod::TEFModAPI *api) {
    static bool inited = false;

    if (!inited) {

        api->registerFunctionDescriptor({
            "Terraria",
            "Recipe",
            "SetupRecipeGroups",
            "hook>>void",
            0,
            &HookTemplate_SetupRecipeGroups,
            { reinterpret_cast<void*>(SetupRecipeGroups) }
        });

        const char* fields[] = {
            "createItem",
            "currentRecipe",
            "requiredItem",
            "requiredTile"
        };

        api->registerApiDescriptor({ "Terraria", "Item", "stack", "Field" });

        for (auto& name : fields) {
            TEFMod::ModApiDescriptor fieldDesc = {
                "Terraria",
            "Recipe",
            name,
            "Field"
            };
            api->registerApiDescriptor(fieldDesc);
        }

        api->registerApiDescriptor({"Terraria", "Recipe", "AddRecipe", "Method", 0});
        api->registerApiDescriptor({"Terraria", "Item", "SetDefaults", "Method", 1});

        api->registerApiDescriptor({"Terraria.ID", "ItemID.Sets", "IsAMaterial", "Field"});
        inited = true;
    } else {

        original_SetupRecipeGroups = api->GetAPI<decltype(original_SetupRecipeGroups)>({ "Terraria", "Recipe", "SetupRecipeGroups", "old_fun", 0 });

        AddRecipe = ParseVoidMethod(api->GetAPI<void*>({ "Terraria", "Recipe", "AddRecipe", "Method", 0 }));
        ItemSetDefaults = ParseVoidMethod(api->GetAPI<void*>({ "Terraria", "Item", "SetDefaults", "Method", 1 }));

        stack = ParseIntField(api->GetAPI<void*>({ "Terraria", "Item", "stack", "Field" }));
        createItem = ParseOtherField(api->GetAPI<void*>({ "Terraria", "Recipe", "createItem", "Field" }));
        currentRecipe = ParseOtherField(api->GetAPI<void*>({ "Terraria", "Recipe", "currentRecipe", "Field" }));
        requiredItem = ParseOtherField(api->GetAPI<void*>({ "Terraria", "Recipe", "requiredItem", "Field" }));
        requiredTile = ParseOtherField(api->GetAPI<void*>({ "Terraria", "Recipe", "requiredTile", "Field" }));

        IsAMaterial = ParseOtherField(api->GetAPI<void*>({"Terraria.ID", "ItemID.Sets", "IsAMaterial", "Field"}));
    }
}
