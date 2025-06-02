/*******************************************************************************
 * 文件名称: set_factory
 * 项目名称: TEFModLoader
 * 创建时间: 2025/5/31
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

#include <set_factory.hpp>
#include <tefmod-api/item.hpp>
#include <logger.hpp>
#include <BNM/UserSettings/GlobalSettings.hpp>
#include <BNM/Class.hpp>
#include <BNM/Field.hpp>
#include <BNM/Method.hpp>
#include <BNM/ComplexMonoStructures.hpp>
#include <tefmod-api/IL2CppArray.hpp>

void TEFModLoader::SetFactory::init() {
    auto SetFactoryClass = BNM::Class("Terraria.ID", "SetFactory");
    auto ctor = SetFactoryClass.GetMethod(".ctor", 1);
    BasicHook(ctor, _ctor, old__ctor);
}

void TEFModLoader::SetFactory::_ctor(void* instance, int i) {
    static int call_count = 1;
    LOGF_DEBUG("调用SetFactory构造函数，参数i: {}，第 {} 次", i, call_count);

    switch (call_count) {
        case 1: {
            auto manager = ItemManager::GetInstance();
            count.item = i;
            LOGF_INFO("获取到原版Item总数: {}", count.item);
            manager->assignment(count.item);
            new_count.item = manager->get_count() + 1;

        } break;

        case 2: {
            count.npc = i;
            LOGF_INFO("获取到原版NPC总数: {}", count.npc);
        } break;

        case 3: {
            count.buff = i;
            LOGF_INFO("获取到原版Buff总数: {}", count.buff);
        } break;

        case 4: {
            count.dust = i;
            LOGF_INFO("获取到原版Dust总数: {}", count.dust);
        } break;

        case 5: {
            count.gore = i;
            LOGF_INFO("获取到原版Gore总数: {}", count.gore);
        } break;

        case 6: {
            count.tile = i;
            LOGF_INFO("获取到原版Tile总数: {}", count.tile);
        } break;

        case 7: {
            count.tile = i;
            LOGF_INFO("获取到原版Wall总数: {}", count.wall);
        } break;

        case 8: {
            count.projectile = i;
            LOGF_INFO("获取到原版Projectile总数: {}", count.projectile);
        } break;

        case 9: {
            count.mount = i;
            LOGF_INFO("获取到原版Mount总数: {}", count.mount);
        } break;

        default:
            break;
    }
    call_count++;

    if (i == count.item) {
        LOGF_TRACE("调用原始构造函数，参数i设置为自定义物品总数: {}", new_count.item);
        old__ctor(instance, new_count.item);
    } else {
        LOGF_TRACE("处理普通物品ID: {}", i);
        old__ctor(instance, i);
    }
}

void TEFModLoader::SetFactory::set_item() {
    LOGF_INFO("===== 开始调整物品相关数组大小 =====");
    size_t new_size = new_count.item;
    LOGF_DEBUG("目标数组大小: {}", new_size);

    auto safe_resize_array = [new_size](const std::string& namespaceName,
                                        const std::string& className,
                                        const std::string& fieldName,
                                        const std::string& typeName) -> bool {
        LOGF_TRACE("正在安全调整 {}.{} ({}[])...", className, fieldName, typeName);

        // 获取类和字段
        auto klass = BNM::Class(namespaceName, className);
        if (!klass) {
            LOGF_ERROR("类 {}.{} 不存在!", namespaceName, className);
            return false;
        }

        BNM::Field<void*> field = klass.GetField(fieldName);
        if (!field) {
            LOGF_ERROR("字段 {}.{} 不存在!", className, fieldName);
            return false;
        }

        // 获取旧数组
        void* oldArray = field.Get();
        if (!oldArray) {
            LOGF_ERROR("{}.{} 数组为空!", className, fieldName);
            return false;
        }

        // 获取旧数组大小
        size_t old_size = 0;
        if (typeName == "bool") {
            old_size = IL2CppArray<bool>(oldArray).Size();
        } else if (typeName == "int") {
            old_size = IL2CppArray<int>(oldArray).Size();
        } else {
            old_size = IL2CppArray<void*>(oldArray).Size();
        }
        LOGF_DEBUG("当前 {}.{} 大小: {} (目标: {})", className, fieldName, old_size, new_size);

        // 创建新数组（根据类型）
        void* newArray = nullptr;
        if (typeName == "bool") {
            bool* new_data = new bool[new_size];
            auto old_data = IL2CppArray<bool>(oldArray).ToVector();
            for (size_t i = 0; i < old_size && i < new_size; ++i) {
                new_data[i] = old_data[i];
            }
            newArray = BNM::Structures::Mono::Array<bool>::Create(new_data, new_size);
            delete[] new_data;
        }
        else if (typeName == "int") {
            std::vector<int> new_data(new_size, 0);  // 默认填充0
            auto old_data = IL2CppArray<int>(oldArray).ToVector();
            for (size_t i = 0; i < old_size && i < new_size; ++i) {
                new_data[i] = old_data[i];
            }
            newArray = BNM::Structures::Mono::Array<int>::Create(new_data);
            new_data.clear();
            new_data.shrink_to_fit();
        }
        else {
            std::vector<void*> new_data(new_size, nullptr);  // 默认填充nullptr
            auto old_data = IL2CppArray<void*>(oldArray).ToVector();
            for (size_t i = 0; i < old_size && i < new_size; ++i) {
                new_data[i] = old_data[i];
            }
            newArray = BNM::Structures::Mono::Array<void*>::Create(new_data);
            new_data.clear();
            new_data.shrink_to_fit();
        }

        if (!newArray) {
            LOGF_ERROR("创建新的 {}.{} 数组失败!", className, fieldName);
            return false;
        }

        // 替换原数组
        field.Set(newArray);
        LOGF_INFO("{}.{} 已安全调整为 {} 大小", className, fieldName, new_size);
        return true;
    };

    safe_resize_array("Terraria", "Player", "ItemUsesRightFire", "bool");
    safe_resize_array("", "VirtualControllerInputState", "ItemCategories", "int");
    safe_resize_array("Terraria", "Item", "claw", "bool");
    safe_resize_array("Terraria", "Item", "staff", "bool");
    safe_resize_array("Terraria.GameContent", "TextureAssets", "Item", "void*");
    safe_resize_array("Terraria.GameContent", "TextureAssets", "ItemFlame", "void*");

    LOGF_INFO("===== 物品数组安全调整完成 =====");
}