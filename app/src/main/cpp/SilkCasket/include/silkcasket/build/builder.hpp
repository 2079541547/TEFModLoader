/*******************************************************************************
 * 文件名称: builder
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

#pragma once

#include <iostream>
#include <vector>
#include "../structure/header.hpp"
#include "../structure/data.hpp"
#include "../structure/entry.hpp"
#include <array>
#include "../compress/mode.hpp"
#include <map>
#include <algorithm>
#include <random>
#include <chrono>
#include <filesystem>

namespace SilkCasket::Build::Builder {

    using namespace std;



    class Build{
    private:
        SilkCasket::FileStructure::header Header;
        vector<SilkCasket::FileStructure::entry> Entry;
        SilkCasket::FileStructure::entryData EntryData;
        vector<uint8_t> tempData;
        SilkCasket::FileStructure::Data Data;


        void configureEntries(const filesystem::path& Path,  bool Encryption = false, const std::string& parentRelativePath = "") {
            // 使用 std::map 来存储已知数据及其对应的地址索引
            std::map<std::vector<uint8_t>, int> dataToIndexMap;

            for (const auto& entry : filesystem::directory_iterator(Path)) {
                std::string relativePath = parentRelativePath.empty() ? entry.path().lexically_relative(Path).string() : (parentRelativePath + "/" + entry.path().filename().string());

                if (is_directory(entry)) {
                    SilkCasket::FileStructure::entry e = {
                            relativePath,
                            is_regular_file(entry),
                            Encryption,
                            0
                    };
                    Entry.push_back(e);
                    configureEntries(entry.path(), Encryption, relativePath);
                } else if (is_regular_file(entry)) {
                    std::ifstream file(entry.path(), std::ios::binary);
                    std::vector<uint8_t> data((std::istreambuf_iterator<char>(file)), std::istreambuf_iterator<char>());
                    file.close();

                    // 查找数据是否已经存在
                    auto it = dataToIndexMap.find(data);
                    int addressIndex;
                    if (it != dataToIndexMap.end()) {
                        // 如果数据已存在，使用已有的索引
                        addressIndex = it->second;
                    } else {
                        // 如果数据不存在，添加到 tempData 并创建新的地址条目
                        tempData.insert(tempData.end(), data.begin(), data.end());
                        SilkCasket::FileStructure::address addr = {
                                (long)(tempData.size() - data.size()) + 46,  // 计算正确的偏移量
                                (long)data.size()
                        };
                        addressIndex = (int)EntryData.Address.size();
                        EntryData.Address.push_back(addr);
                        dataToIndexMap[data] = addressIndex;  // 更新映射
                    }

                    SilkCasket::FileStructure::entry e = {
                            relativePath,
                            true,
                            Encryption,
                            addressIndex
                    };
                    Entry.push_back(e);
                }
            }
        }


    public:
        void build(const std::filesystem::path &Path,
                   const std::filesystem::path &OutPath,
                   SilkCasket::Compress::Mode::MODE Mode,
                   size_t blockSize = 8096 * 1024,
                   bool entryEncryption = false,
                   const string &key = "");
    };

}
