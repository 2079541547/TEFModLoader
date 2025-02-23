/*******************************************************************************
 * 文件名称: compress
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
#include <cstring>
#include <filesystem>

namespace SilkCasket {

    enum Mode {
        Storage = 0,
        LZMA2FAST = 1,
        LZ4 = 2,
        LZW = 3,
        LIZARD = 4,
    };

    struct MODE {
        bool Storage = true;
        bool LZMA2FAST = true;
        bool LZ4 = false;
        bool LZW = false;
        bool LIZARD = false;
    };

    inline std::filesystem::path temp_path = std::filesystem::temp_directory_path();

    class Compress {
         public:
            static std::vector<uint8_t> smartCompress(const std::filesystem::path &inputFile, const MODE &compress = MODE(),
                                           size_t blockSize = 8096 * 1024);

            static std::vector<uint8_t> smartCompress(const std::vector<uint8_t> &inputData, const MODE &compress = MODE(),
                                           size_t blockSize = 8096 * 1024);

            static std::vector<uint8_t> smartDecompress(const std::vector<uint8_t> &compressedData);
    };

    struct LZ4 {
        static std::vector<uint8_t> compress(const std::vector<uint8_t> &data, size_t blockSize = 8096 * 1024);
        static std::vector<uint8_t> decompress(const std::vector<uint8_t> &compressed);
    };

    struct LZW {
        static std::vector<uint8_t> encodeVLE(uint32_t value);
        static uint32_t decodeVLE(const std::vector<uint8_t> &bytes, size_t &index);
        static std::vector<uint8_t> compress(const std::vector<uint8_t> &data);
        static std::vector<uint8_t> decompress(const std::vector<uint8_t> &compressed);
    };

    struct LZMA2Fast {
        static std::vector<uint8_t> compress(const std::vector<uint8_t> &data, size_t blockSize = 8096 * 1024);
        static std::vector<uint8_t> decompress(const std::vector<uint8_t> &compressed);
    };

    struct Lizard {
        static std::vector<uint8_t> compress(const std::vector<uint8_t> &data, size_t blockSize = 8096 * 1024);
        static std::vector<uint8_t> decompress(const std::vector<uint8_t> &compressed);
    };
}