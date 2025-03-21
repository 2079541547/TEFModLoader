/*******************************************************************************
 * 文件名称: analysis
 * 项目名称: SilkCasket
 * 创建时间: 2025/1/15
 * 作者: EternalFuture゙
 * Github: https://github.com/2079541547
 * 版权声明: Copyright © 2024 EternalFuture. All rights reserved.
 * 许可证: Licensed under the Apache License, Version 2.0 (the "License");
 *         you may not use this file except in compliance with the License.
 *         You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *         Unless required by applicable law or agreed to in writing, software
 *         distributed under the License is distributed on an "AS IS" BASIS,
 *         WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *         See the License for the specific language governing permissions and
 *         limitations under the License.
 *
 * 描述信息: 本文件为Silk Casket项目中的一部分，允许在遵守Apache License 2.0的条件下自由用于商业用途。
 * 注意事项: 请严格遵守Apache License 2.0协议使用本代码。Apache License 2.0允许商业用途，无需额外授权。
 *******************************************************************************/

#include <analysis.hpp>
#include <utility>
#include <utils.hpp>
#include <iostream>
#include <encryption.hpp>
#include <sstream>

SilkCasket::analysis::analysis(const std::filesystem::path &File, std::string KEY) {
    key = std::move(KEY);
    if (!exists(File)) {
        return;
    }
    file_path = File;
    header = FileStructure::header::deserialize(Utils::readFileAddress(File, 0, 48));

    if (header.versionNumber != 20250115) return;

    std::vector<uint8_t> temp = Utils::readFileAddress(File, header.entry.offset, header.entry.size);
    if (temp.empty()) {
        return;
    }
    entry = FileStructure::deserialize_entries(SilkCasket::Compress::smartDecompress(temp));
    temp = Utils::readFileAddress(File, header.entryData.offset, header.entryData.size);
    data = FileStructure::data::deserialize(SilkCasket::Compress::smartDecompress(temp));
}

void SilkCasket::analysis::printALL() {
    std::ostringstream headerOss;

    headerOss << "file header：\n"
              << "  Identification: " << header.id << "\n"
              << "  Version: " << header.versionNumber << "\n"
              << "  Entry Address: Offset: " << header.entry.offset << ", Size: " << header.entry.size << "\n"
              << "  Entry Data Address: Offset: " << header.entryData.offset << ", Size: " << header.entryData.size << "\n"
              << "  Encryption: " << (header.encryption ? "Yes" : "No") << "\n"
              << "entries：\n";

    std::cout << headerOss.str() << std::endl;

    for (const auto& e : entry) {
        std::ostringstream entryOss;
        entryOss << "\nName: " << e.name
                 << ",\n Is File: " << (e.isFile ? "Yes" : "No")
                 << ",\n Data: " << e.data;
        std::cout << entryOss.str() << std::endl;
    }

    for (const auto& addr : data.Address) {
        std::ostringstream entryDataOss;
        entryDataOss << "\nOffset: " << addr.offset
                     << ",\n Size: " << addr.size;
        std::cout << entryDataOss.str() << std::endl;
    }
}

SilkCasket::FileStructure::entry SilkCasket::analysis::getEntry(const std::string& Name) {
    for (auto _a: entry) {
        if (_a.name == Name) {
            return _a;
        }
    }
    return {};
}

SilkCasket::FileStructure::address SilkCasket::analysis::getAddress(const SilkCasket::FileStructure::entry& Name) {
    if (!getIsFile(Name)) {
        return {};
    }
    return data.Address.at(Name.data);
}


bool SilkCasket::analysis::getIsFile(const SilkCasket::FileStructure::entry& Name) {
    return Name.isFile;
}

bool SilkCasket::analysis::getEncryption() const {
    return header.encryption;
}

std::vector <uint8_t> SilkCasket::analysis::getData(const std::string& Name) {
    auto _a = getEntry(Name);
    auto _b = getAddress(_a);
    auto org_data = Utils::readFileAddress(file_path, _b.offset, _b.size);
    if (getEncryption()) {
        return SilkCasket::Compress::smartDecompress(SilkCasket::RC4::rc4_decrypt(org_data, key));
    }
    return SilkCasket::Compress::smartDecompress(org_data);
}

void SilkCasket::analysis::releaseFile(const std::string& Name, const std::filesystem::path& targetPath) {
    auto _ = getEntry(Name);
    if (_.name.empty()) {
        std::cerr << "No entries found:" << Name << std::endl;
        return;
    }
    if (!getIsFile(_)) {
        std::cerr << "Try to free a non-file entry:" << Name << std::endl;
        return;
    }

    std::filesystem::path fullPath = targetPath / _.name;

    if (!std::filesystem::exists(fullPath.parent_path())) {
        try {
            std::filesystem::create_directories(fullPath.parent_path());
        } catch (const std::filesystem::filesystem_error& e) {
            std::cerr << e.what() << std::endl;
            return;
        }
    }

    std::vector<uint8_t> Data = getData(Name);

    try {
        Utils::Vuint8ToFile(fullPath, Data);
    } catch (const std::exception& e) {
        std::cerr << e.what() << std::endl;
    }
}

void SilkCasket::analysis::releaseFolder(std::string Name, const std::filesystem::path& targetPath) {

    if (!std::filesystem::exists(targetPath)) {
        std::filesystem::create_directories(targetPath);
    }

    Name += "/";

    for (const auto& _ : entry) {
        std::string adjustedName = _.name;

        if (adjustedName.find(Name) != 0) {
            adjustedName = Name + adjustedName;
        }

        std::filesystem::path entryPath = targetPath / adjustedName;

        if (getIsFile(_)) {
            releaseFile(adjustedName, targetPath);
        }
    }
}

void SilkCasket::analysis::releaseEntry(const std::filesystem::path& targetPath) {
    if (!std::filesystem::exists(targetPath)) {
        std::filesystem::create_directories(targetPath);
    }

    for (const auto& _ : entry) {
        std::filesystem::path entryPath = targetPath / _.name;

        if (getIsFile(_)) {
            releaseFile(_.name, targetPath);
        } else {
            std::filesystem::create_directories(entryPath);
        }
    }

}