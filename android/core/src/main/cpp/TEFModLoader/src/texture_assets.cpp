/*******************************************************************************
 * 文件名称: texture_assets
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

#include <texture_assets.hpp>
#include <tefmod-api/IL2CppArray.hpp>
#include <set_factory.hpp>
#include <tefmod-api/item.hpp>
#include <logger.hpp>

#include <BNM/Field.hpp>
#include <BNM/Method.hpp>
#include <BNM/UnityStructures.hpp>

#include <cmath>
#include <cstdint>

void *TEFModLoader::TextureAssets::CreateXNATexture2d(const TEFMod::ImageData &image_data) {

    static auto Texture2d_Unity_Class = BNM::Class("UnityEngine", "Texture2D");
    static auto Texture2d_XNA_Class = BNM::Class("Microsoft.Xna.Framework.Graphics", "Texture2D");
    static BNM::Method<void> Apply = Texture2d_Unity_Class.GetMethod("Apply", 0);
    static BNM::Method<void> SetPixel = Texture2d_Unity_Class.GetMethod("SetPixel", 3);

    static auto ProcessPixels = [](unsigned int width, unsigned int height,
                                   const std::vector<unsigned char> &image,
                                   BNM::IL2CPP::Il2CppObject* texture) {
        try {
            for (unsigned y = 0; y < height; ++y) {
                unsigned unity_y = (height - 1) - y;
                for (unsigned x = 0; x < width; ++x) {
                    unsigned index = (y * width + x) * 4;

                    float r = static_cast<float>(image[index]) / 255.0f;
                    float g = static_cast<float>(image[index + 1]) / 255.0f;
                    float b = static_cast<float>(image[index + 2]) / 255.0f;
                    float a = static_cast<float>(image[index + 3]) / 255.0f;

                    SetPixel[texture].Call(x, unity_y, BNM::Structures::Unity::Color(r, g, b, a));
                }
            }
        } catch (const std::exception& e) {
            throw;
        }
    };

    auto new_texture2d_unity = Texture2d_Unity_Class.CreateNewObjectParameters(
            image_data.width, image_data.height);

    ProcessPixels(image_data.width, image_data.height, image_data.pixels, new_texture2d_unity);

    Apply[new_texture2d_unity].Call();

    if (!new_texture2d_unity) {
        return nullptr;
    }

    static BNM::Method<void> set_filterMode = Texture2d_Unity_Class.GetMethod("set_filterMode", 1);
    static BNM::Method<void> set_wrapMode = Texture2d_Unity_Class.GetMethod("set_wrapMode", 1);
    static BNM::Field<bool> SharedBatching = Texture2d_XNA_Class.GetField("SharedBatching");
    static BNM::Field<bool> NonSharedHeadInsert = Texture2d_XNA_Class.GetField("NonSharedHeadInsert");

    set_filterMode[new_texture2d_unity].Call(0);
    set_wrapMode[new_texture2d_unity].Call(1);

    auto new_texture2d_xna = Texture2d_XNA_Class.CreateNewObjectParameters(new_texture2d_unity);
    SharedBatching[new_texture2d_xna].Set(false);
    NonSharedHeadInsert[new_texture2d_xna].Set(false);

    return new_texture2d_xna;
}

void TEFModLoader::TextureAssets::init_item() {
    LOGF_INFO("===== 开始加载物品纹理 =====");

    auto TextureAssetsClass = BNM::Class("Terraria.GameContent", "TextureAssets");
    if (!TextureAssetsClass) {
        LOGF_ERROR("TextureAssets类获取失败！");
        return;
    }

    auto AssetClass = BNM::Class("ReLogic.Content", "Asset`1").GetGeneric({BNM::Defaults::Get<void*>()});
    if (!AssetClass) {
        LOGF_ERROR("Asset`1类获取失败！");
        return;
    }


    BNM::Field<void*> Assets_Value = AssetClass.GetField("<Value>k__BackingField");

    LOGF_INFO(">>> 开始加载Item纹理数组");
    BNM::Field<void*> item_field = TextureAssetsClass.GetField("Item");
    if (item_field) {
        IL2CppArray<void*> item_array(item_field.Get());
        LOGF_DEBUG("Item数组大小: {}", item_array.Size());

        for (int i = SetFactory::count.item; i < item_array.Size(); ++i) {
            LOGF_TRACE("处理物品ID {}...", i);
            auto assets_instance = AssetClass.CreateNewObjectParameters(
                    BNM::CreateMonoString("TEFModLoader::NewItem_" + std::to_string(i))
            );

            if (auto item = ItemManager::GetInstance()->get_item_instance(i);
            !item->get_image().pixels.empty()) {
                LOGF_TRACE("物品ID {} 存在，生成自定义纹理...", i);
                auto texture = CreateXNATexture2d(item->get_image());
                Assets_Value[assets_instance].Set(texture);
            } else {
                LOGF_TRACE("物品ID {} 不存在，使用Unknown纹理", i);
                if (!Unknown_XNA_Texture2d) {
                    LOGF_TRACE("首次生成Unknown纹理...");
                    draw_Unknown();
                    Unknown_XNA_Texture2d = CreateXNATexture2d(Unknown);
                }
                Assets_Value[assets_instance].Set(Unknown_XNA_Texture2d);
            }
            item_array.Set(i, assets_instance);
        }
        LOGF_INFO("<<< Item纹理加载完成");
    } else {
        LOGF_ERROR("Item字段获取失败！");
    }

    LOGF_INFO(">>> 开始加载ItemFlame纹理数组");
    BNM::Field<void*> item_flame_field = TextureAssetsClass.GetField("ItemFlame");
    if (item_flame_field) {
        IL2CppArray<void*> item_flame_array(item_flame_field.Get());
        LOGF_DEBUG("ItemFlame数组大小: {}", item_flame_array.Size());

        for (int i = SetFactory::count.item; i < item_flame_array.Size(); ++i) {
            LOGF_TRACE("处理物品火焰ID {}...", i);
            auto assets_instance = AssetClass.CreateNewObjectParameters(
                    BNM::CreateMonoString("TEFModLoader::NewItemFlame_" + std::to_string(i))
            );

            if (auto item = ItemManager::GetInstance()->get_item_instance(i);
            !item->get_image().pixels.empty()) {
                LOGF_TRACE("物品火焰ID {} 存在，生成自定义纹理...", i);
                auto texture = CreateXNATexture2d(item->get_image());
                Assets_Value[assets_instance].Set(texture);
            } else {
                LOGF_TRACE("物品火焰ID {} 不存在，使用Unknown纹理", i);
                if (!Unknown_XNA_Texture2d) {
                    Unknown_XNA_Texture2d = CreateXNATexture2d(Unknown);
                }
                Assets_Value[assets_instance].Set(Unknown_XNA_Texture2d);
            }
            item_flame_array.Set(i, assets_instance);
        }
        LOGF_INFO("<<< ItemFlame纹理加载完成");
    } else {
        LOGF_ERROR("ItemFlame字段获取失败！");
    }

    LOGF_INFO("===== 物品纹理加载完成 =====");
}

void TEFModLoader::TextureAssets::draw_Unknown() {
    const int SIZE = 64;
    Unknown.width = SIZE;
    Unknown.height = SIZE;
    Unknown.pixels.resize(SIZE * SIZE * 4, 0);

    const uint8_t COLOR1[4] = {103, 80, 164, 255};  // 主色 (深紫)
    const uint8_t COLOR2[4] = {0,   0,   0,   255}; // 纯黑
    auto COLOR3 = COLOR2;
    auto COLOR4 = COLOR1;

    // 每个格子的大小 (64x64 分成 4个 32x32 的格子)
    const int CELL_SIZE = SIZE / 2;

    // 填充4个纯色格子（交错布局）
    for (int y = 0; y < SIZE; y++) {
        for (int x = 0; x < SIZE; x++) {
            size_t index = (y * SIZE + x) * 4;

            // 决定当前像素属于哪个格子
            bool isLeft = (x < CELL_SIZE);
            bool isTop = (y < CELL_SIZE);

            // 分配颜色（交错布局）
            if (isTop && isLeft) {
                memcpy(&Unknown.pixels[index], COLOR1, 4); // 左上：深紫
            } else if (isTop && !isLeft) {
                memcpy(&Unknown.pixels[index], COLOR2, 4); // 右上：纯黑
            } else if (!isTop && isLeft) {
                memcpy(&Unknown.pixels[index], COLOR3, 4); // 左下：浅灰
            } else {
                memcpy(&Unknown.pixels[index], COLOR4, 4);  // 右下：浅紫
            }
        }
    }

    // 添加细线分隔格子 (1像素宽的深色线)
    const uint8_t DIVIDER_COLOR[4] = {50, 50, 50, 255};
    for (int i = 0; i < SIZE; i++) {
        // 垂直线 (中间)
        size_t vLineIndex = (i * SIZE + CELL_SIZE) * 4;
        memcpy(&Unknown.pixels[vLineIndex], DIVIDER_COLOR, 4);

        // 水平线 (中间)
        size_t hLineIndex = (CELL_SIZE * SIZE + i) * 4;
        memcpy(&Unknown.pixels[hLineIndex], DIVIDER_COLOR, 4);
    }
}