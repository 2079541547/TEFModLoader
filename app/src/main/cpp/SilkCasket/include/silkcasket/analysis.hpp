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

#pragma once

#include <iostream>
#include <silkcasket/structure/header.hpp>
#include <silkcasket/structure/data.hpp>
#include <silkcasket/structure/entry.hpp>
#include <filesystem>
#include <silkcasket/log.hpp>
#include <utility>
#include <silkcasket/utils/file.hpp>
#include <silkcasket/encryption.hpp>
#include <silkcasket/compress/compress.hpp>


namespace SilkCasket {

    using namespace std;


    class analysis {
    private:
        FileStructure::header Header;
        vector<FileStructure::entry> Entry;
        FileStructure::entryData EntryData;
        string key;
        filesystem::path file_path;
    public:
        explicit analysis(const filesystem::path& File, string KEY = "") {
            key = std::move(KEY);

            if (!exists(File)) {
                LOG(LogLevel::ERROR, "SilkCasket", "analysis", "analysis", "文件不存在！");
                return;
            }

            file_path = File;
            Header = FileStructure::deserializeHeader( Utils::File::readFileAddress(File, 0, 46));


            vector<uint8_t> temp = Utils::File::readFileAddress(File, Header.entry.offset, Header.entry.size);
            if (temp.empty()) {
                LOG(LogLevel::ERROR, "SilkCasket", "analysis", "analysis", "条目表为空！");
                return;
            }

            LOG(LogLevel::INFO, "SilkCasket", "analysis", "analysis", "开始解析条目表");
            Entry = FileStructure::deserializeEntrys(Compress::smartDecompress(temp));
            LOG(LogLevel::INFO, "SilkCasket", "analysis", "analysis", "解析条目表完成");


            LOG(LogLevel::INFO, "SilkCasket", "analysis", "analysis", "开始解析条目地址表");
            temp = Utils::File::readFileAddress(File, Header.entryData.offset, Header.entryData.size);
            EntryData = FileStructure::deserializeEntryData(Compress::smartDecompress(temp));

            LOG(LogLevel::INFO, "SilkCasket", "analysis", "analysis", "解析条目地址表完成");

            LOG(LogLevel::INFO, "SilkCasket", "analysis", "analysis", "解析成功");
        }

        void printALL();
        FileStructure::entry getEntry(const string& Name);
        FileStructure::address getAddress(const SilkCasket::FileStructure::entry& Name);
        static bool getIsFile(const SilkCasket::FileStructure::entry& Name);
        static bool getEncryption(const SilkCasket::FileStructure::entry& Name);
        vector<uint8_t> getData(const string& Name);
        void releaseFile(const string& Name, const filesystem::path& targetPath);
        void releaseFolder(string Name, const filesystem::path& targetPath);
        void releaseEntry(const filesystem::path& targetPath);
    };

}