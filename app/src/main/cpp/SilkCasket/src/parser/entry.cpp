/*******************************************************************************
 * 文件名称: entry
 * 项目名称: SilkCasket
 * 创建时间: 2024/11/24
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

#include <parser/entry.hpp>
#include <utils/file.hpp>
#include <log.hpp>


SilkCasket::FileStructure::entry SilkCasket::Parser::Entry::GetEntry(std::string name) {
    if (!Entrys.empty()) {
        for (auto _a: Entrys) {
            if (_a.name == name) {
                return _a;
            }
        }
    } else {
        LOG(LogLevel::ERROR, "SilkCasket::Parser::Entry", "GetEntry", "Entrys为空！");
        return {};
    }
    return {};
}

std::vector <uint8_t> SilkCasket::Parser::Entry::GetVerification(std::string name, int Hash) {
    auto _a = GetEntry(name);
    auto __a = _a.verification;
    std::vector<uint8_t> ___a = {};
    try {
        switch (Hash) {
            case 32:
                ___a = SilkCasket::Utils::File::readFileAddress(FilePath, Verification.at(
                        __a).SilkHash32.offset, Verification.at(__a).SilkHash32.size);
                break;

            case 64:
                ___a = SilkCasket::Utils::File::readFileAddress(FilePath, Verification.at(
                        __a).SilkHash64.offset, Verification.at(__a).SilkHash64.size);
                break;
            case 128:
                ___a = SilkCasket::Utils::File::readFileAddress(FilePath, Verification.at(
                        __a).SilkHash128.offset, Verification.at(__a).SilkHash128.size);
                break;
            case 256:
                ___a = SilkCasket::Utils::File::readFileAddress(FilePath, Verification.at(
                        __a).SilkHash256.offset, Verification.at(__a).SilkHash256.size);
                break;

            case 512:
                ___a = SilkCasket::Utils::File::readFileAddress(FilePath, Verification.at(
                        __a).SilkHash512.offset, Verification.at(__a).SilkHash512.size);
                break;
            case 1024:
                ___a = SilkCasket::Utils::File::readFileAddress(FilePath, Verification.at(
                        __a).SilkHash1024.offset, Verification.at(__a).SilkHash1024.size);
                break;
        }
    } catch (...) {
        LOG(LogLevel::ERROR, "SilkCasket::Parser::Entry", "GetVerification", "错误！");
        return ___a;
    }
    return ___a;
}

std::vector <uint8_t> SilkCasket::Parser::Entry::GetEntryData(std::string name) {
    auto _a = GetEntry(name);
    return SilkCasket::Utils::File::readFileAddress(FilePath, Data.Address.at(_a.data).offset, Data.Address.at(_a.data).size);
}

bool SilkCasket::Parser::Entry::GetEncryption(std::string name) {
    return GetEntry(name).encryption;
}

bool SilkCasket::Parser::Entry::GetIsFile(std::string name) {
    return GetEntry(name).isFile;
}