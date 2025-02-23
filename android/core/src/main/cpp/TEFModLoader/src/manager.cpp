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
#include <iostream>

void TEFModLoader::Manager::API::autoProcessing() {
    auto ApiDescriptor = EFModAPI::getEFModAPI().getApiDescriptor();

    if (ApiDescriptor.empty()) {
        std::cout << "Auto-create API: The collected array is empty" << std::endl;
        return;
    }

    std::cout << "Starting autoProcessing with " << ApiDescriptor.size() << " API descriptors." << std::endl;

    for (const auto& descriptor : ApiDescriptor) {
        if (!descriptor.File.empty()) {
            std::cout << "Processing API descriptor with File: " << descriptor.File
                      << ", Namespace: " << descriptor.Namespace
                      << ", Class: " << descriptor.Class
                      << ", Name: " << descriptor.Name
                      << ", Type: " << descriptor.Type << std::endl;

            size_t dotPosition = descriptor.Class.find('.');

            if (descriptor.Type == "Field") {
                BNM::FieldBase* field;
                if (dotPosition != std::string::npos) {
                    std::cout << "Field is in an inner class. Processing inner class." << std::endl;
                    field = new BNM::FieldBase(BNM::Class(descriptor.Namespace, descriptor.Class.substr(0, dotPosition), BNM::Image(descriptor.File)).GetInnerClass(descriptor.Class.substr(dotPosition + 1)).GetField(descriptor.Name));
                } else {
                    std::cout << "Field is not in an inner class. Processing regular class." << std::endl;
                    field = new BNM::FieldBase(BNM::Class(descriptor.Namespace, descriptor.Class, BNM::Image(descriptor.File)).GetField(descriptor.Name));
                }
                std::cout << "Field created successfully. Registering API..." << std::endl;
                EFModAPI::getEFModAPI().registerAPI(descriptor.getID(), field);
            } else if (descriptor.Type == "Class") {
                BNM::Class* Class;
                if (dotPosition != std::string::npos) {
                    std::cout << "Class is in an inner class. Processing inner class." << std::endl;
                    Class = new BNM::Class(BNM::Class(descriptor.Namespace, descriptor.Class.substr(0, dotPosition)));
                } else {
                    std::cout << "Class is not in an inner class. Processing regular class." << std::endl;
                    Class = new BNM::Class(BNM::Class(descriptor.Namespace, descriptor.Class, BNM::Image(descriptor.File)));
                }
                std::cout << "Class created successfully. Registering API..." << std::endl;
                EFModAPI::getEFModAPI().registerAPI(descriptor.getID(), Class);
            } else if (descriptor.Type == "Method") {
                BNM::MethodBase* Method;
                if (dotPosition != std::string::npos) {
                    std::cout << "Method is in an inner class. Processing inner class." << std::endl;
                    Method = new BNM::MethodBase(BNM::Class(descriptor.Namespace, descriptor.Class.substr(0, dotPosition)).GetMethod(descriptor.Name, descriptor.Arg));
                } else {
                    std::cout << "Method is not in an inner class. Processing regular class." << std::endl;
                    Method = new BNM::MethodBase(BNM::Class(descriptor.Namespace, descriptor.Class, BNM::Image(descriptor.File)).GetMethod(descriptor.Name, descriptor.Arg));
                }
                std::cout << "Method created successfully. Registering API..." << std::endl;
                EFModAPI::getEFModAPI().registerAPI(descriptor.getID(), Method);
            }
        } else {
            std::cerr << "Skipping API descriptor due to empty file path." << std::endl;
        }
    }

    std::cout << "Finished autoProcessing." << std::endl;
}