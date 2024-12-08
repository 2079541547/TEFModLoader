/*******************************************************************************
 * 文件名称: analysis
 * 项目名称: SilkCasket
 * 创建时间: 2024/11/30
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
 * 描述信息: 本文件为SilkCasket项目中的一部分，允许在遵守Apache License 2.0的条件下自由用于商业用途。
 * 注意事项: 请严格遵守Apache License 2.0协议使用本代码。Apache License 2.0允许商业用途，无需额外授权。
 *******************************************************************************/

#include <analysis.hpp>
#include <log.hpp>
#include <utility>

void SilkCasket::analysis::printALL() {
    // 为不同部分创建独立的日志字符串流
    std::ostringstream headerOss;

    // 收集文件头信息
    headerOss << "文件头：\n"
              << "  Identification: " << Header.identification << "\n"
              << "  Version: " << Header.versionNumber << "\n"
              << "  Entry Address: Offset: " << Header.entry.offset << ", Size: " << Header.entry.size << "\n"
              << "  Entry Data Address: Offset: " << Header.entryData.offset << ", Size: " << Header.entryData.size << "\n"
              << "条目：\n";

    LOG(LogLevel::INFO, "SilkCasket", "analysis", "printALL", headerOss.str());

    for (const auto& e : Entry) {
        std::ostringstream entryOss;
        entryOss << "\nName: " << e.name
                 << ",\n Is File: " << (e.isFile ? "Yes" : "No")
                 << ",\n Encryption: " << (e.encryption ? "Enabled" : "Disabled")
                 << ",\n Data: " << e.data << "\n";
        LOG(LogLevel::INFO, "SilkCasket", "analysis", "printALL", entryOss.str());
    }

    // 收集条目数据信息
    LOG(LogLevel::INFO, "SilkCasket", "analysis", "printALL", "条目数据" + (string)"(" + to_string(EntryData.Address.size()) + (string) ")");
    std::ostringstream entryDataOss;
    for (const auto& addr : EntryData.Address) {
        entryDataOss << "\nOffset: " << addr.offset
                     << ",\n Size: " << addr.size;
    }
    LOG(LogLevel::INFO, "SilkCasket", "analysis", "printALL", entryDataOss.str());

}


SilkCasket::FileStructure::entry SilkCasket::analysis::getEntry(const std::string& Name) {
    for (auto _a: Entry) {
        if (_a.name == Name) {
            return _a;
        }
    }
    return {};
}


SilkCasket::FileStructure::address SilkCasket::analysis::getAddress(const SilkCasket::FileStructure::entry& Name) {
    if (!getIsFile(Name)) {
        LOG(LogLevel::WARN, "SilkCasket", "analysis" , "getAddress", "条目为文件夹！");
        return {};
    }
    return EntryData.Address.at(Name.data);
}


bool SilkCasket::analysis::getIsFile(const SilkCasket::FileStructure::entry& Name) {
    return Name.isFile;
}

bool SilkCasket::analysis::getEncryption(const SilkCasket::FileStructure::entry& Name) {
    return Name.encryption;
}

std::vector <uint8_t> SilkCasket::analysis::getData(const std::string& Name) {
    auto _a = getEntry(Name);
    auto _b = getAddress(_a);
    auto org_data = Utils::File::readFileAddress(file_path, _b.offset, _b.size);
    if (getEncryption(_a)) {
        return Compress::smartDecompress(SilkCasket::decryptFile(key, org_data));
    }
    return Compress::smartDecompress(org_data);
}


void SilkCasket::analysis::releaseFile(const std::string& Name, const filesystem::path& targetPath) {
    auto entry = getEntry(Name);
    if (entry.name.empty()) {
        LOG(LogLevel::ERROR, "SilkCasket", "analysis", "releaseFile", "未找到条目: " + Name);
        return;
    }
    if (!getIsFile(entry)) {
        LOG(LogLevel::WARN, "SilkCasket", "analysis", "releaseFile", "尝试释放一个非文件条目: " + Name);
        return;
    }

    // 获取目标文件的完整路径
    std::filesystem::path fullPath = targetPath / entry.name;

    // 检查并创建所有不存在的父目录
    if (!std::filesystem::exists(fullPath.parent_path())) {
        try {
            std::filesystem::create_directories(fullPath.parent_path());
        } catch (const std::filesystem::filesystem_error& e) {
            LOG(LogLevel::ERROR, "SilkCasket", "analysis", "releaseFile",
                "无法创建目录: " + fullPath.parent_path().string() + ", 错误: " + e.what());
            return;
        }
    }

    // 读取数据
    std::vector<uint8_t> data = getData(Name);

    // 将数据写入到文件
    try {
        Utils::File::Vuint8ToFile(fullPath, data);
        LOG(LogLevel::INFO, "SilkCasket", "analysis", "releaseFile", "已释放：" + fullPath.string());
    } catch (const std::exception& e) {
        LOG(LogLevel::ERROR, "SilkCasket", "analysis", "releaseFile",
            "无法写入文件: " + fullPath.string() + ", 错误: " + e.what());
    }
}


void SilkCasket::analysis::releaseFolder(std::string Name, const filesystem::path& targetPath) {
    // 确保目标路径存在
    if (!std::filesystem::exists(targetPath)) {
        std::filesystem::create_directories(targetPath);
        LOG(LogLevel::INFO, "SilkCasket", "analysis", "releaseEntry", "创建目标文件夹: " + targetPath.string());
    }

    Name += "/";

    // 遍历所有条目
    for (const auto& entry : Entry) {
        std::string adjustedName = entry.name;

        // 如果entry.name不以Name开始，则添加Name到前面
        if (adjustedName.find(Name) != 0) {  // 检查是否以Name开头
            adjustedName = Name + adjustedName;  // 添加Name到前面
        }

        std::filesystem::path entryPath = targetPath / adjustedName;

        // 根据条目类型处理
        if (getIsFile(entry)) {
            // 如果是文件，直接释放
            releaseFile(adjustedName, targetPath);  // 使用调整后的名称
        }
    }

    LOG(LogLevel::INFO, "SilkCasket", "analysis", "releaseEntry", "所有条目已释放到: " + targetPath.string());
}

void SilkCasket::analysis::releaseEntry(const filesystem::path& targetPath) {
    // 确保目标路径存在
    if (!std::filesystem::exists(targetPath)) {
        std::filesystem::create_directories(targetPath);
        LOG(LogLevel::INFO, "SilkCasket", "analysis", "releaseEntry", "创建目标文件夹: " + targetPath.string());
    }

    // 遍历所有条目
    for (const auto& entry : Entry) {
        std::filesystem::path entryPath = targetPath / entry.name;

        // 根据条目类型处理
        if (getIsFile(entry)) {
            // 如果是文件，直接释放
            releaseFile(entry.name, targetPath);
        } else {
            // 如果是文件夹，创建相应的目录
            std::filesystem::create_directories(entryPath);
            LOG(LogLevel::INFO, "SilkCasket", "analysis", "releaseEntry", "创建文件夹: " + entryPath.string());
        }
    }

    LOG(LogLevel::INFO, "SilkCasket", "analysis", "releaseEntry", "所有条目已释放到: " + targetPath.string());
}