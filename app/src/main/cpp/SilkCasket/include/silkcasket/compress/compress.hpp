/*******************************************************************************
 * 文件名称: compress
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
#include "mode.hpp"
#include <vector>
#include <filesystem>

namespace SilkCasket::Compress {
    using namespace std;

    vector<uint8_t> smartCompress(const std::filesystem::path& inputFile, const SilkCasket::Compress::Mode::MODE& compress = SilkCasket::Compress::Mode::MODE(), size_t blockSize = 8096 * 1024);
    vector<uint8_t> smartCompress(const vector<uint8_t> &inputData, const SilkCasket::Compress::Mode::MODE& compress = SilkCasket::Compress::Mode::MODE(), size_t blockSize = 8096 * 1024);
    vector<uint8_t> smartDecompress(const vector<uint8_t> &compressedData);
}
