/*******************************************************************************
 * 文件名称: manager
 * 项目名称: TEFModLoader
 * 创建时间: 2025/2/11
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


#include <TEFModLoader/manager.hpp>
#include <EFModLoader/EFMod/EFMod.hpp>
#include <BNM/UserSettings/GlobalSettings.hpp>
#include <BNM/Field.hpp>
#include <BNM/Method.hpp>

void TEFModLoader::Manager::API::autoProcessing() {
    auto ApiDescriptor = EFModAPI::getEFModAPI().getApiDescriptor();
    // if (ApiDescriptor.empty()) EFLOG(WARNING, "自动创建API", "收集的数组为空");
    for (const auto& _: ApiDescriptor) {
        if (!_.File.empty()) {
            if(_.Type == "Field") {
                size_t dotPosition = _.Class.find('.');
                BNM::FieldBase* field;
                if (dotPosition != std::string::npos) {
                    // EFLOG(INFO, "自动创建API", "字段于内部类");
                    field = new BNM::FieldBase(BNM::Class(_.Namespace, _.Class.substr(0, dotPosition), BNM::Image(_.File)).GetInnerClass(_.Class.substr(dotPosition + 1)).GetField(_.Name));
                } else {
                    field = new BNM::FieldBase(BNM::Class(_.Namespace, _.Class, BNM::Image(_.File)).GetField(_.Name));
                }
                // EFLOG(INFO, "自动创建API", "创建API:", _.getID(), "指针:", (uintptr_t)a);
                EFModAPI::getEFModAPI().registerAPI(_.getID(), field);
            } else if (_.Type == "Class") {
                size_t dotPosition = _.Class.find('.');
                BNM::Class* Class;
                if (dotPosition != std::string::npos) {
                    // EFLOG(INFO, "自动创建API", "字段于内部类");
                    Class = new BNM::Class(BNM::Class(_.Namespace, _.Class.substr(0, dotPosition)));
                } else {
                    Class = new BNM::Class(BNM::Class(_.Namespace, _.Class, BNM::Image(_.File)));
                }
                EFModAPI::getEFModAPI().registerAPI(_.getID(), Class);
            } else if (_.Type == "Method") {
                size_t dotPosition = _.Class.find('.');
                BNM::MethodBase* Method;
                if (dotPosition != std::string::npos) {
                    // EFLOG(INFO, "自动创建API", "字段于内部类");
                    Method = new BNM::MethodBase(BNM::Class(_.Namespace, _.Class.substr(0, dotPosition)).GetMethod(_.Name, _.Arg));
                } else {
                    Method = new BNM::MethodBase(BNM::Class(_.Namespace, _.Class, BNM::Image(_.File)).GetMethod(_.Name, _.Arg));
                }
                EFModAPI::getEFModAPI().registerAPI(_.getID(), Method);
            }
        }
    }
}