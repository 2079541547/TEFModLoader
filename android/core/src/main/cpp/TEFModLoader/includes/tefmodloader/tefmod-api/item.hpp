/*******************************************************************************
 * 文件名称: item
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

#pragma once

#include "Item.hpp"
#include <unordered_set>
#include <map>
#include <TEFMod.hpp>

namespace TEFModLoader {

    inline bool operator==(const TEFMod::identifier& a, const TEFMod::identifier& b) {
        return a.Namespace == b.Namespace && a.Name == b.Name;
    }

    inline bool operator<(const TEFMod::identifier& a, const TEFMod::identifier& b) {
        if (a.Namespace != b.Namespace) return a.Namespace < b.Namespace;
        return a.Name < b.Name;
    }

    class ItemManager: public TEFMod::ItemManager {
    private:
        std::vector<std::pair<TEFMod::identifier, TEFMod::Item*>> m_items;
        std::unordered_map<std::string, int> m_name_to_id;
        std::unordered_map<int, TEFMod::identifier> m_id_to_name;
        std::unordered_set<std::string> m_registered_names;
        std::map<int, TEFMod::Item*> m_items_instance;
        inline static bool m_needs_sorting = true;

        std::unordered_map<std::string, TEFMod::item_localized> m_localized_data;
        std::unordered_map<int, std::pair<TEFMod::TerrariaInstance, TEFMod::TerrariaInstance>> m_localized_instance;

        std::vector<TEFMod::recipe> m_recipe;
    public:
        void registered(const TEFMod::identifier &name, TEFMod::Item *item) override;
        int get_id(const TEFMod::identifier &name) override;
        TEFMod::identifier get_name(int id) override;
        int get_id_from_str(const std::string& name) override;

        void add_recipe(const TEFMod::recipe& item) override;
        inline std::vector<TEFMod::recipe> get_all_recipe() { return m_recipe; }

        TEFMod::item_localized * get_localized(const TEFMod::identifier &name) override;
        std::unordered_map<std::string, TEFMod::item_localized> get_all_localized() override;
        void set_localized(TEFMod::identifier name, const TEFMod::item_localized &localized) override;
        void flushed_localized() override;

        void assignment(int startID);
        TEFMod::Item* get_item_instance(int id);
        int get_count();
        void init_localized();
        std::pair<TEFMod::TerrariaInstance, TEFMod::TerrariaInstance>& get_localized_instance(int id);
        void registered_unknown(const std::string &name);
        inline std::map<int, TEFMod::Item*> get_m_items_instance() const& { return m_items_instance; }

        inline static ItemManager* GetInstance() {
            static ItemManager item_manager;
            return &item_manager;
        }
    };


    class UnKnown_Item: public TEFMod::Item {
    private:
        inline static auto manager = ItemManager::GetInstance();
        TEFMod::identifier itemName;
    public:
        inline explicit UnKnown_Item(const TEFMod::identifier& i): itemName(i) {}

        inline void init_static() override {};

        inline void apply_equip_effects(TEFMod::TerrariaInstance, TEFMod::TerrariaInstance) override {};

        inline void update_armor_sets(TEFMod::TerrariaInstance) override {};

        inline bool can_use(TEFMod::TerrariaInstance, TEFMod::TerrariaInstance) override { return true;};

        inline void set_defaults(TEFMod::TerrariaInstance instance) override {};
        inline TEFMod::ImageData get_image() override { return  {}; }
        inline void set_text(const std::string &lang) override {
            std::string all_langs_desc =
                    "[EN] ID: " + itemName.GetID() +
                    " | Name: " + itemName.Name +
                    " | Unloaded from: " + itemName.Namespace + " Namespace\n" +

                    // 中文简体
                    "[中文简体] ID: " + itemName.GetID() +
                    " | 名称: " + itemName.Name +
                    " | 来源: " + itemName.Namespace + " 命名空间\n" +

                    // 中文繁體
                    "[中文繁體] ID: " + itemName.GetID() +
                    " | 名稱: " + itemName.Name +
                    " | 來源: " + itemName.Namespace + " 命名空間\n" +

                    // 日本語
                    "[日本語] ID: " + itemName.GetID() +
                    " | 名前: " + itemName.Name +
                    " | ソース: " + itemName.Namespace + " ネームスペース\n" +

                    // Русский
                    "[Русский] ID: " + itemName.GetID() +
                    " | Название: " + itemName.Name +
                    " | Источник: " + itemName.Namespace + " пространство\n" +

                    // Español
                    "[Español] ID: " + itemName.GetID() +
                    " | Nombre: " + itemName.Name +
                    " | Origen: " + itemName.Namespace + " espacio\n" +

                    // Deutsch
                    "[Deutsch] ID: " + itemName.GetID() +
                    " | Name: " + itemName.Name +
                    " | Quelle: " + itemName.Namespace + " Namensraum\n" +

                    // Français
                    "[Français] ID: " + itemName.GetID() +
                    " | Nom: " + itemName.Name +
                    " | Origine: " + itemName.Namespace + " espace\n" +

                    // Português
                    "[Português] ID: " + itemName.GetID() +
                    " | Nome: " + itemName.Name +
                    " | Origem: " + itemName.Namespace + " espaço\n" +

                    // Polski
                    "[Polski] ID: " + itemName.GetID() +
                    " | Nazwa: " + itemName.Name +
                    " | Źródło: " + itemName.Namespace + " przestrzeń\n" +

                    // 한국어
                    "[한국어] ID: " + itemName.GetID() +
                    " | 이름: " + itemName.Name +
                    " | 출처: " + itemName.Namespace + " 네임스페이스\n" +

                    // Türkçe
                    "[Türkçe] ID: " + itemName.GetID() +
                    " | Ad: " + itemName.Name +
                    " | Kaynak: " + itemName.Namespace + " alan\n" +

                    // Magyar
                    "[Magyar] ID: " + itemName.GetID() +
                    " | Név: " + itemName.Name +
                    " | Forrás: " + itemName.Namespace + " névtér\n" +

                    // Tiếng Việt
                    "[Tiếng Việt] ID: " + itemName.GetID() +
                    " | Tên: " + itemName.Name +
                    " | Nguồn: " + itemName.Namespace + " không gian";

            manager->set_localized(itemName, { "UnKnown", all_langs_desc });
        }
    };

}