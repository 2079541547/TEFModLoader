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

#include "Logger.hpp"
#include "DebugTool.hpp"
#include "TEFMod.hpp"
#include "Item.hpp"
#include <random>


static EFMod* mod;
static TEFMod::ItemManager* ItemManager;
inline TEFMod::Field<void*>* (*ParseOtherField)(void*) = nullptr;
inline TEFMod::Array<int>* (*ParseIntArray)(void*) = nullptr;
inline TEFMod::Field<void*>* ShimmerTransformToItem = nullptr;

class MyItem: public TEFMod::Item {
public:

    inline void init_static() override {};

    inline void apply_equip_effects(TEFMod::TerrariaInstance instance) override {};

    inline void update_armor_sets(TEFMod::TerrariaInstance instance) override {};

    inline bool can_use(TEFMod::TerrariaInstance instance) override { return true;};

    void set_defaults(TEFMod::TerrariaInstance instance) override {
    }
    void set_text(const std::string &lang) override {
        if (lang == "en-US") {
            ItemManager->set_localized({ "MyMod-EternalFuture", "MyItem" }, { "Test Item", "The Item is from mod" });
        } else ItemManager->set_localized({ "MyMod-EternalFuture", "MyItem" }, { "测试物品", "此为Mod添加的测试物品，非原版" });
    }
    TEFMod::ImageData get_image() override {
        TEFMod::ImageData image;
        image.width = 100;
        image.height = 100;
        image.pixels.resize(100 * 100 * 4);

        std::random_device rd;
        std::mt19937 gen(rd());
        std::uniform_int_distribution<> dis(0, 255);

        for (size_t i = 0; i < image.pixels.size(); i += 4) {
            image.pixels[i] = dis(gen);     // R (随机)
            image.pixels[i+1] = dis(gen);   // G (随机)
            image.pixels[i+2] = dis(gen);   // B (随机)
            image.pixels[i+3] = 255;        // A
        }
        return image;
    }
};

class MyItem2: public TEFMod::Item {
public:

    inline void init_static() override {};

    inline void apply_equip_effects(TEFMod::TerrariaInstance instance) override {};

    inline void update_armor_sets(TEFMod::TerrariaInstance instance) override {};

    inline bool can_use(TEFMod::TerrariaInstance instance) override { return true;};

    void set_defaults(TEFMod::TerrariaInstance instance) override {
    }
    void set_text(const std::string &lang) override {
        ItemManager->set_localized({ "MyMod-EternalFuture", "MyItem2" }, { "测试物品2", "此为Mod添加的测试物品2，非原版" });
    }
    TEFMod::ImageData get_image() override {
        TEFMod::ImageData image;
        image.width = 128;  // 使用128x128分辨率更常见
        image.height = 128;
        image.pixels.resize(image.width * image.height * 4);

        // 基础颜色 - 木质或金属色
        uint8_t baseR = 160; // 木质棕色或金属灰色
        uint8_t baseG = 120;
        uint8_t baseB = 80;

        // 木板/金属板条纹参数
        int plankWidth = 16;
        int plankVariation = 4;

        // 边缘边框
        int borderWidth = 4;
        uint8_t borderR = 80;
        uint8_t borderG = 60;
        uint8_t borderB = 40;

        // 金属铆钉或木钉
        int rivetSpacing = 32;
        int rivetRadius = 3;

        std::random_device rd;
        std::mt19937 gen(rd());
        std::uniform_int_distribution<> colorVariation(-20, 20);
        std::uniform_int_distribution<> plankVariator(-plankVariation, plankVariation);

        for (int y = 0; y < image.height; ++y) {
            for (int x = 0; x < image.width; ++x) {
                int idx = (y * image.width + x) * 4;

                // 判断是否在边框内
                bool isBorder = x < borderWidth || x >= image.width - borderWidth ||
                                y < borderWidth || y >= image.height - borderWidth;

                // 边框颜色
                if (isBorder) {
                    image.pixels[idx] = borderR;
                    image.pixels[idx+1] = borderG;
                    image.pixels[idx+2] = borderB;
                    image.pixels[idx+3] = 255;
                    continue;
                }

                // 木板/金属板条纹效果
                int plankPos = (x + plankVariator(gen)) / plankWidth;
                int colorVar = colorVariation(gen);

                // 基础颜色加上变化
                image.pixels[idx] = static_cast<uint8_t>(baseR + colorVar);
                image.pixels[idx+1] = static_cast<uint8_t>(baseG + colorVar);
                image.pixels[idx+2] = static_cast<uint8_t>(baseB + colorVar);

                // 添加木板之间的缝隙
                if ((x % plankWidth) < 1) {
                    image.pixels[idx] /= 2;
                    image.pixels[idx+1] /= 2;
                    image.pixels[idx+2] /= 2;
                }

                // 添加铆钉/钉子
                int rivetX = x % rivetSpacing;
                int rivetY = y % rivetSpacing;
                if (rivetX > rivetSpacing/2 - rivetRadius && rivetX < rivetSpacing/2 + rivetRadius &&
                    rivetY > rivetSpacing/2 - rivetRadius && rivetY < rivetSpacing/2 + rivetRadius) {
                    image.pixels[idx] = 220;   // 铆钉颜色
                    image.pixels[idx+1] = 220;
                    image.pixels[idx+2] = 220;
                }

                // Alpha通道
                image.pixels[idx+3] = 255;
            }
        }

        return image;
    }
};

static MyItem my_item;
static MyItem2 my_item2;

inline void (*original_cctor)(TEFMod::TerrariaInstance);
void cctor_HookT(TEFMod::TerrariaInstance i);
void hook_cctor(TEFMod::TerrariaInstance i) {
    if (auto *it = ParseIntArray(ShimmerTransformToItem->Get())) {
        it->set(9, ItemManager->get_id({ "MyMod-EternalFuture", "MyItem" }));
        it->set(ItemManager->get_id({ "MyMod-EternalFuture", "MyItem" }), ItemManager->get_id({ "MyMod-EternalFuture", "MyItem2" }));
    }
}

inline TEFMod::HookTemplate HookTemplate_cctor {
        reinterpret_cast<void*>(cctor_HookT),
        {  }
};

void cctor_HookT(TEFMod::TerrariaInstance i) {
    original_cctor(i);
    for (const auto fun : HookTemplate_cctor.FunctionArray) {
        reinterpret_cast<void(*)(TEFMod::TerrariaInstance)>(fun)(i);
    }
}

class MyMod: public EFMod {
public:

    int Initialize(const std::string &path, MultiChannel *multiChannel) override {
        return 0;
    }

    void Send(const std::string &path, MultiChannel *multiChannel) override {
        auto api = multiChannel->receive<TEFMod::TEFModAPI*>("TEFMod::TEFModAPI");
        api->registerFunctionDescriptor({
            "Terraria.ID",
            "ItemID.Sets",
            ".cctor",
            "hook>>void",
            0,
            &HookTemplate_cctor,
            { reinterpret_cast<void*>(hook_cctor) }
        });
        api->registerApiDescriptor({
            "Terraria.ID",
            "ItemID.Sets",
            "ShimmerTransformToItem",
            "Field"
        });
    }

    void Receive(const std::string &path, MultiChannel *multiChannel) override {
        auto api = multiChannel->receive<TEFMod::TEFModAPI*>("TEFMod::TEFModAPI");
        ShimmerTransformToItem = ParseOtherField(api->GetAPI<void*>({
            "Terraria.ID",
            "ItemID.Sets",
            "ShimmerTransformToItem",
            "Field"
        }));
        original_cctor = api->GetAPI<void(*)(TEFMod::TerrariaInstance)>({
            "Terraria.ID",
                    "ItemID.Sets",
                    ".cctor",
                    "old_fun",
                    0,
        });
    }

    int Load(const std::string &path, MultiChannel *multiChannel) override {
        ItemManager = multiChannel->receive<TEFMod::ItemManager*>("TEFMod::ItemManager");
        ParseOtherField = multiChannel->receive<decltype(ParseOtherField)>("TEFMod::Field<Other>::ParseFromPointer");
        ParseIntArray = multiChannel->receive<decltype(ParseIntArray)>("TEFMod::Array<Int>::ParseFromPointer");
        ItemManager->registered({ "MyMod-EternalFuture", "MyItem" }, &my_item);
        ItemManager->registered({ "MyMod-EternalFuture", "MyItem2" }, &my_item2);
        // ItemManager->registered({ "MyMod-EternalFuture", "MyItem3" }, nullptr);
        // g_debug_tool = multiChannel->receive<TEFMod::DebugTool*>("TEFMod::DebugTool");
        // g_log = multiChannel->receive<TEFMod::Logger*(*)(const std::string& Tag, const std::string& filePath, const std::size_t maxCache)>("TEFMod::CreateLogger")("MyMod-EternalFuture", "", 114514);
        return 0;
    }

    int UnLoad(const std::string &path, MultiChannel *multiChannel) override {
        return 0;
    }

    Metadata GetMetadata() override {
        return {
            "d",
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