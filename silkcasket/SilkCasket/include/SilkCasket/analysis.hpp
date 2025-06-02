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

#pragma once

#include "structure.hpp"
#include <filesystem>
#include "compress.hpp"

namespace SilkCasket {

    class analysis {
    private:
        FileStructure::header header;
        std::vector<FileStructure::entry> entry;
        FileStructure::data data;
        std::string key;
        std::filesystem::path file_path;
        SilkCasket::Compress Compress;
    public:
        explicit analysis(const std::filesystem::path& File, std::string KEY = "");
        void printALL();
        FileStructure::entry getEntry(const std::string& Name);
        FileStructure::address getAddress(const SilkCasket::FileStructure::entry& Name);
        static bool getIsFile(const SilkCasket::FileStructure::entry& Name);
        bool getEncryption() const;
        std::vector<uint8_t> getData(const std::string& Name);
        void releaseFile(const std::string& Name, const std::filesystem::path& targetPath);
        void releaseFolder(std::string Name, const std::filesystem::path& targetPath);
        void releaseEntry(const std::filesystem::path& targetPath);
    };

}