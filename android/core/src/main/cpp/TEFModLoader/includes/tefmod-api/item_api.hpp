/*******************************************************************************
 * 文件名称: item_api
 * 项目名称: TEFMod-API
 * 创建时间: 25-5-30
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

#include "base_type_api.hpp"
#include <unordered_map>

namespace TEFMod {

    struct item_prefix {
        int id;
        uint8_t prefix;
    };

    enum class prefix_type {
        SwordsHammersAxesPicks = 0,     // 剑/锤/斧/镐类近战武器
        SpearsMacesChainsawsDrills = 1, // 长矛/链锯/钻头/拳炮类
        GunsBows = 2,                   // 枪械/弓类远程武器
        MagicAndSummon = 3,             // 魔法武器和召唤武器
        BoomerangsChakrams = 4,         // 回旋镖/溜溜球
        LegendaryWeapons = 5,           // 可拥有传奇前缀的特殊武器
        Accessories = 6,                // 可前缀化的饰品
    };

    struct item_localized {
        std::string name;
        std::string tool_tip;
    };

    struct recipe {
        int result_item_id;
        std::vector<std::pair<int, int>> materials; // {itemID, stack}
        int required_tile_id = -1;
        int result_stack = 1;
    };

    class Item {
    public:
        /*
         * 静态设置，如添加配方
         * */
        virtual void init_static() = 0;

        /*
         * 被装备时的加成
         * */
        virtual void apply_equip_effects(TerrariaInstance player, TerrariaInstance armorPiece) = 0;

        /*
         * 套装奖励
         * */
        virtual void update_armor_sets(TerrariaInstance player) = 0;

        /*
         *  使用条件
         **/
        virtual bool can_use(TerrariaInstance player, TerrariaInstance instance) = 0;

        /*
         * 属性设置
         */
        virtual void set_defaults(TerrariaInstance instance) = 0;

        virtual void set_text(const std::string& lang) = 0;
        virtual ImageData get_image() = 0;
    };

    class ItemManager {
    public:
        virtual void registered(const identifier& name, Item* item) = 0;
        virtual void add_recipe(const recipe& item) = 0;
        virtual void add_animation(animation _animation) = 0;
        virtual void add_prefix(item_prefix prefix) = 0;
        virtual int get_id(const identifier& name) = 0;
        virtual int get_id_from_str(const std::string& name) = 0;
        virtual identifier get_name(int id) = 0;

        virtual std::unordered_map<std::string, item_localized> get_all_localized() = 0;
        virtual item_localized* get_localized(const identifier& name) = 0;
        virtual void set_localized(identifier name, const item_localized& localized) = 0;
        virtual void flushed_localized() = 0;
    };

}