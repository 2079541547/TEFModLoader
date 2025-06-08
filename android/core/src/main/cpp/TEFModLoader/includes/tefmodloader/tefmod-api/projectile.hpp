/*******************************************************************************
 * 文件名称: projectile
 * 项目名称: TEFModLoader
 * 创建时间: 2025/6/8
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

#include "projectile_api.hpp"

#include <string>
#include <unordered_map>
#include <unordered_set>

namespace TEFModLoader {

class ProjectileManager: public TEFMod::ProjectileManager {
    private:
        std::vector<std::pair<TEFMod::identifier, TEFMod::Projectile*>> m_projectiles;
        std::unordered_map<std::string, int> m_name_to_id;
        std::unordered_map<int, TEFMod::identifier> m_id_to_name;
        std::unordered_set<std::string> m_registered_names;
        std::unordered_map<int, TEFMod::Projectile*> m_projectiles_instance;

        std::unordered_map<std::string, std::string> m_localized_data;
        std::unordered_map<int, TEFMod::TerrariaInstance> m_localized_instance;

        int _count;
    public:
        void registered(const TEFMod::identifier &name, TEFMod::Projectile *projectile) override;
        int get_id(const TEFMod::identifier &name) override;
        int get_id_from_str(const std::string &name) override;
        TEFMod::identifier get_name(int id) override;
        void set_localized(TEFMod::identifier name, const std::string &localized) override;
        void flushed_localized() override;

        [[nodiscard]] inline std::unordered_map<int, TEFMod::Projectile*> get_m_projectiles_instance() const& { return m_projectiles_instance; }
        void assignment(int startID);
        int get_count();
        TEFMod::Projectile* get_projectile_instance(int id);
        void init_localized();
        inline static ProjectileManager* GetInstance() {
            static ProjectileManager projectile_manager;
            return &projectile_manager;
        }
    };

    class UnKnown_Projectile final : public TEFMod::Projectile {
    public:
        static UnKnown_Projectile* GetInstance() {
            static UnKnown_Projectile r;
            return &r;
        }

        void damage(TEFMod::TerrariaInstance instance) override {}
        void kill(TEFMod::TerrariaInstance instance) override {}
        void set_defaults(TEFMod::TerrariaInstance instance) override {}
        void init_static() override {}
        void set_text(const std::string &lang) override {}
        TEFMod::ImageData get_image() override { return {}; }
    };
}
