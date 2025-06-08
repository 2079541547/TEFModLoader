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
#include "item_api.hpp"
#include "projectile_api.hpp"
#include <random>


inline TEFMod::Field<int>* (*ParseIntField)(void*) = nullptr;
inline TEFMod::Field<bool>* (*ParseBoolField)(void*) = nullptr;
inline TEFMod::Field<void*>* (*ParseOtherField)(void*) = nullptr;
inline TEFMod::Field<float>* (*ParseFloatField)(void*) = nullptr;

inline TEFMod::Method<int>* (*ParseIntMethod)(void*) = nullptr;
inline TEFMod::Method<void>* (*ParseVoidMethod)(void*) = nullptr;

inline TEFMod::Array<int>* (*ParseIntArray)(void*) = nullptr;
inline TEFMod::Array<bool>* (*ParseBoolArray)(void*) = nullptr;
inline TEFMod::Array<void*>* (*ParseOtherArray)(void*) = nullptr;

inline TEFMod::String* (*ParseString)(void*) = nullptr;

static EFMod* mod;
static TEFMod::ItemManager* ItemManager;
static TEFMod::ProjectileManager* ProjectileManager;

inline TEFMod::Field<void*>* ShimmerTransformToItem = nullptr;

inline TEFMod::Field<int>* useStyle;
inline TEFMod::Field<int>* useAnimation;
inline TEFMod::Field<int>* useTime;
inline TEFMod::Field<int>* width;
inline TEFMod::Field<int>* height;
inline TEFMod::Field<int>* shoot;
inline TEFMod::Field<int>* useAmmo;
inline TEFMod::Field<int>* damage;
inline TEFMod::Field<float>* shootSpeed;
inline TEFMod::Field<bool>* noMelee;
inline TEFMod::Field<int>* value;
inline TEFMod::Field<bool>* ranged;
inline TEFMod::Field<bool>* channel;
inline TEFMod::Field<int>* rare;
inline TEFMod::Field<bool>* autoReuse;
inline TEFMod::Field<int>* mana;
inline TEFMod::Field<bool>* magic;
inline TEFMod::Field<bool>* noUseGraphic;

inline TEFMod::Field<void*>* SwordsHammersAxesPicks;

class MyProjectile : public TEFMod::Projectile {
public:
    void kill(TEFMod::TerrariaInstance instance) override {}
    void damage(TEFMod::TerrariaInstance instance) override {}
    TEFMod::ImageData get_image() override { return {}; }
    void init_static() override {}
    void set_defaults(TEFMod::TerrariaInstance instance) override {}
    void set_text(const std::string &lang) override {}
};

class MyItem: public TEFMod::Item {
public:

    inline void init_static() override {
        ItemManager->add_recipe({
            ItemManager->get_id({ "MyMod-EternalFuture", "MyItem" }),
            { { 9, 1 } }
        });
        ItemManager->add_animation({
            ItemManager->get_id({ "MyMod-EternalFuture", "MyItem" }),
            60,
            4,
            true
        });
        ItemManager->add_prefix({
            ItemManager->get_id({ "MyMod-EternalFuture", "MyItem" }),
            static_cast<uint8_t>(TEFMod::prefix_type::GunsBows)
        });
    };

    inline void apply_equip_effects(TEFMod::TerrariaInstance , TEFMod::TerrariaInstance instance) override {};

    inline void update_armor_sets(TEFMod::TerrariaInstance instance) override {};

    inline bool can_use(TEFMod::TerrariaInstance, TEFMod::TerrariaInstance instance) override { return true;};

    void set_defaults(TEFMod::TerrariaInstance instance) override {
        useStyle->Set(1, instance);
        useAnimation->Set(20, instance);
        useTime->Set(20, instance);
        damage->Set(100, instance);
        rare->Set(4, instance);
        noMelee->Set(false, instance);
        autoReuse->Set(true, instance);
        shoot->Set(ProjectileManager->get_id({ "MyMod-EternalFuture", "MyProjectile" }), instance);
        shootSpeed->Set(10, instance);
    }
    void set_text(const std::string &lang) override {
        ItemManager->set_localized({ "MyMod-EternalFuture", "MyItem" },
                                   { "一把普通的剑", "此为Mod添加的测试物品，非原版" });
    }


    TEFMod::ImageData get_image() override {
        TEFMod::ImageData image;
        const int frameWidth = 32;
        const int frameHeight = 50;
        const int frameCount = 4; // 4帧动画
        const int totalHeight = frameHeight * frameCount;

        // 设置图集尺寸
        image.width = frameWidth;
        image.height = totalHeight; // 32x200 (4帧x50高)
        image.pixels.resize(frameWidth * totalHeight * 4, 0); // RGBA格式

        // 颜色定义
        const uint8_t bladeColor[] = {200, 200, 200, 255};  // 剑身银色
        const uint8_t edgeColor[] = {255, 255, 255, 255};   // 剑刃高光
        const uint8_t hiltColor[] = {101, 67, 33, 255};     // 剑柄棕色
        const uint8_t gemColor[] = {255, 50, 50, 255};      // 宝石红色

        // 为每一帧生成不同的剑形态
        for (int frame = 0; frame < frameCount; frame++) {
            int frameOffset = frame * frameHeight;
            float frameRatio = static_cast<float>(frame) / frameCount;

            // 剑的主要参数 (每帧略有不同)
            int hiltLength = 12;
            int bladeLength = 35;
            int hiltWidth = 4;
            int bladeWidth = 3 + frame % 2; // 让宽度有些变化
            int guardWidth = 6;

            // 计算当前帧的摆动偏移
            float sway = sin(frameRatio * 3.14f) * 3.0f;

            // 绘制剑柄 (垂直部分)
            for (int y = frameOffset + frameHeight - hiltLength; y < frameOffset + frameHeight; y++) {
                for (int x = frameWidth/2 - hiltWidth/2; x < frameWidth/2 + hiltWidth/2; x++) {
                    size_t index = (y * frameWidth + x) * 4;
                    std::copy(hiltColor, hiltColor + 4, &image.pixels[index]);
                }
            }

            // 绘制护手 (水平部分)
            for (int x = frameWidth/2 - guardWidth/2; x < frameWidth/2 + guardWidth/2; x++) {
                for (int y = frameOffset + frameHeight - hiltLength - 1;
                     y < frameOffset + frameHeight - hiltLength + 3; y++) {
                    size_t index = (y * frameWidth + x) * 4;
                    std::copy(hiltColor, hiltColor + 4, &image.pixels[index]);
                }
            }

            // 绘制剑身 (带帧变化效果)
            for (int y = frameOffset + frameHeight - hiltLength - bladeLength;
                 y < frameOffset + frameHeight - hiltLength; y++) {

                // 计算当前宽度 (剑尖变细)
                int currentWidth = bladeWidth;
                int distFromTip = (y - (frameOffset + frameHeight - hiltLength - bladeLength));
                if (distFromTip < 5) {
                    currentWidth = bladeWidth * distFromTip / 5;
                }

                // 添加摆动效果
                int centerX = frameWidth/2 + static_cast<int>(sway *
                                                              (1.0f - static_cast<float>(y - frameOffset) / frameHeight));

                for (int x = centerX - currentWidth/2; x < centerX + currentWidth/2; x++) {
                    if (x >= 0 && x < frameWidth && y >= frameOffset) {
                        size_t index = (y * frameWidth + x) * 4;
                        std::copy(bladeColor, bladeColor + 4, &image.pixels[index]);

                        // 剑刃边缘高光
                        if (x == centerX - currentWidth/2 || x == centerX + currentWidth/2 - 1) {
                            std::copy(edgeColor, edgeColor + 4, &image.pixels[index]);
                        }
                    }
                }
            }

            // 绘制剑柄宝石 (中心位置)
            for (int y = frameOffset + frameHeight - hiltLength/2 - 1;
                 y < frameOffset + frameHeight - hiltLength/2 + 1; y++) {
                for (int x = frameWidth/2 - 1; x < frameWidth/2 + 1; x++) {
                    size_t index = (y * frameWidth + x) * 4;
                    std::copy(gemColor, gemColor + 4, &image.pixels[index]);
                }
            }

            // 为每帧添加特效光点
            if (frame % 2 == 0) {
                int sparkleX = frameWidth/2 + static_cast<int>(sway * 0.7f);
                int sparkleY = frameOffset + 10;
                for (int y = sparkleY - 1; y <= sparkleY + 1; y++) {
                    for (int x = sparkleX - 1; x <= sparkleX + 1; x++) {
                        if (x >= 0 && x < frameWidth && y >= frameOffset && y < frameOffset + frameHeight) {
                            size_t index = (y * frameWidth + x) * 4;
                            const uint8_t sparkleColor[] = {255, 255, 200, 200};
                            std::copy(sparkleColor, sparkleColor + 4, &image.pixels[index]);
                        }
                    }
                }
            }
        }

        return image;
    }
};

class MyItem2: public TEFMod::Item {
public:

    inline void init_static() override {
        ItemManager->add_recipe({
            ItemManager->get_id({ "MyMod-EternalFuture", "MyItem2" }),
            { { 9, 1 } }
        });
    };

    inline void apply_equip_effects(TEFMod::TerrariaInstance, TEFMod::TerrariaInstance instance) override {};

    inline void update_armor_sets(TEFMod::TerrariaInstance instance) override {};

    inline bool can_use(TEFMod::TerrariaInstance, TEFMod::TerrariaInstance instance) override { return true;};

    void set_defaults(TEFMod::TerrariaInstance instance) override {}

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
static MyProjectile my_projectile;


inline void (*original_cctor)(TEFMod::TerrariaInstance);
void cctor_HookT(TEFMod::TerrariaInstance i);
void hook_cctor(TEFMod::TerrariaInstance i) {
    // ParseBoolArray(SwordsHammersAxesPicks->Get())->set(ItemManager->get_id({ "MyMod-EternalFuture", "MyItem" }), true);

  /*  if (auto *it = ParseIntArray(ShimmerTransformToItem->Get())) {
        // it->set(9, ItemManager->get_id({ "MyMod-EternalFuture", "MyItem" }));
        // it->set(ItemManager->get_id({ "MyMod-EternalFuture", "MyItem" }), ItemManager->get_id({ "MyMod-EternalFuture", "MyItem2" }));
    }*/
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
            "Terraria.GameContent.Prefixes",
            "PrefixLegacy.ItemSets",
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


        static const char* fields[] = { "useStyle", "useAnimation", "useTime", "width", "height", "shoot", "useAmmo", "damage",
                                        "shootSpeed", "noMelee", "value", "ranged", "channel", "rare", "autoReuse", "mana", "magic",
                                        "noUseGraphic"
        };

        for (auto& name : fields) {
            TEFMod::ModApiDescriptor fieldDesc = {
                    "Terraria",
                    "Item",
                    name,
                    "Field"
            };
            api->registerApiDescriptor(fieldDesc);
        }

        api->registerApiDescriptor({
            "Terraria.GameContent.Prefixes",
            "PrefixLegacy.ItemSets",
            "SwordsHammersAxesPicks",
            "Field"
        });
    }

    void Receive(const std::string &path, MultiChannel *multiChannel) override {
        auto api = multiChannel->receive<TEFMod::TEFModAPI*>("TEFMod::TEFModAPI");
        SwordsHammersAxesPicks = ParseOtherField(api->GetAPI<void*>({
            "Terraria.GameContent.Prefixes",
            "PrefixLegacy.ItemSets",
            "SwordsHammersAxesPicks",
            "Field"
        }));
        ShimmerTransformToItem = ParseOtherField(api->GetAPI<void*>({
            "Terraria.ID",
            "ItemID.Sets",
            "ShimmerTransformToItem",
            "Field"
        }));
        original_cctor = api->GetAPI<void(*)(TEFMod::TerrariaInstance)>({
            "Terraria.GameContent.Prefixes",
            "PrefixLegacy.ItemSets",
            ".cctor",
            "old_fun",
            0,
        });

        useStyle = ParseIntField(api->GetAPI<void*>({"Terraria", "Item", "useStyle", "Field"}));
        useAnimation = ParseIntField(api->GetAPI<void*>({"Terraria", "Item", "useAnimation", "Field"}));
        useTime = ParseIntField(api->GetAPI<void*>({"Terraria", "Item", "useTime", "Field"}));
        width = ParseIntField(api->GetAPI<void*>({"Terraria", "Item", "width", "Field"}));
        height = ParseIntField(api->GetAPI<void*>({"Terraria", "Item", "height", "Field"}));
        shoot = ParseIntField(api->GetAPI<void*>({"Terraria", "Item", "shoot", "Field"}));
        useAmmo = ParseIntField(api->GetAPI<void*>({"Terraria", "Item", "useAmmo", "Field"}));
        damage = ParseIntField(api->GetAPI<void*>({"Terraria", "Item", "damage", "Field"}));
        shootSpeed = ParseFloatField(api->GetAPI<void*>({"Terraria", "Item", "shootSpeed", "Field"}));
        noMelee = ParseBoolField(api->GetAPI<void*>({"Terraria", "Item", "noMelee", "Field"}));
        value = ParseIntField(api->GetAPI<void*>({"Terraria", "Item", "value", "Field"}));
        ranged = ParseBoolField(api->GetAPI<void*>({"Terraria", "Item", "ranged", "Field"}));
        channel = ParseBoolField(api->GetAPI<void*>({"Terraria", "Item", "channel", "Field"}));
        rare = ParseIntField(api->GetAPI<void*>({"Terraria", "Item", "rare", "Field"}));
        autoReuse = ParseBoolField(api->GetAPI<void*>({"Terraria", "Item", "autoReuse", "Field"}));
        mana = ParseIntField(api->GetAPI<void*>({"Terraria", "Item", "mana", "Field"}));
        magic = ParseBoolField(api->GetAPI<void*>({"Terraria", "Item", "magic", "Field"}));
        noUseGraphic = ParseBoolField(api->GetAPI<void*>({"Terraria", "Item", "noUseGraphic", "Field"}));
    }

    int Load(const std::string &path, MultiChannel *multiChannel) override {
        ItemManager = multiChannel->receive<TEFMod::ItemManager*>("TEFMod::ItemManager");
        ProjectileManager = multiChannel->receive<TEFMod::ProjectileManager*>("TEFMod::ProjectileManager");
        ParseOtherField = multiChannel->receive<decltype(ParseOtherField)>("TEFMod::Field<Other>::ParseFromPointer");
        ParseIntArray = multiChannel->receive<decltype(ParseIntArray)>("TEFMod::Array<Int>::ParseFromPointer");
        ItemManager->registered({ "MyMod-EternalFuture", "MyItem" }, &my_item);
        // ItemManager->registered({ "MyMod-EternalFuture", "MyItem2" }, &my_item2);
        // ItemManager->registered({ "MyMod-EternalFuture", "MyItem3" }, nullptr);
        ProjectileManager->registered({ "MyMod-EternalFuture", "MyProjectile" }, &my_projectile);

        ParseIntField = multiChannel->receive<decltype(ParseIntField)>("TEFMod::Field<Int>::ParseFromPointer");
        ParseBoolField = multiChannel->receive<decltype(ParseBoolField)>("TEFMod::Field<Bool>::ParseFromPointer");
        ParseOtherField = multiChannel->receive<decltype(ParseOtherField)>("TEFMod::Field<Other>::ParseFromPointer");
        ParseFloatField = multiChannel->receive<decltype(ParseFloatField)>("TEFMod::Field<Float>::ParseFromPointer");

        ParseIntMethod = multiChannel->receive<decltype(ParseIntMethod)>("TEFMod::Method<Int>::ParseFromPointer");
        ParseVoidMethod = multiChannel->receive<decltype(ParseVoidMethod)>("TEFMod::Method<Void>::ParseFromPointer");

        ParseIntArray = multiChannel->receive<decltype(ParseIntArray)>("TEFMod::Array<Int>::ParseFromPointer");
        ParseBoolArray = multiChannel->receive<decltype(ParseBoolArray)>("TEFMod::Array<Bool>::ParseFromPointer");
        ParseOtherArray = multiChannel->receive<decltype(ParseOtherArray)>("TEFMod::Array<Other>::ParseFromPointer");

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