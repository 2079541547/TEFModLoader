/*******************************************************************************
 * 文件名称: utils
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

#pragma once

#include <vector>
#include <filesystem>

namespace SilkCasket::Utils {
    void Vuint8ToFile(const std::filesystem::path& Path, const std::vector<uint8_t> &Content, size_t chunkSize = 1024 * 1024);
    void stringToFile(const std::filesystem::path& Path, const std::string& Content);
    void copyFileTo(const std::filesystem::path& Source, const std::filesystem::path& Target, size_t chunkSize = 1024 * 1024);
    void copyDirectory(const std::filesystem::path& Source, const std::filesystem::path& Target, size_t chunkSize = 1024 * 1024);
    std::vector<uint8_t> readFile(const std::filesystem::path& Path);
    std::vector<uint8_t> readFileAddress(const std::filesystem::path& Path, const size_t& offset, const size_t& size);
}