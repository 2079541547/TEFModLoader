/*******************************************************************************
 * 文件名称: encryption
 * 项目名称: SilkCasket
 * 创建时间: 2025/1/4
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

#include <string>
#include <vector>
#include <cstdint>

namespace SilkCasket {
    class RC4 {
    public:
        static std::vector<unsigned char> rc4_encrypt(const std::vector<unsigned char>& data, const std::string& keyStr);
        static std::vector<unsigned char> rc4_decrypt(const std::vector<unsigned char>& data, const std::string& keyStr);
    private:
        static void rc4_ksa(const unsigned char *key, size_t key_len, std::vector<unsigned char> &S);
        static unsigned char rc4_prga(std::vector<unsigned char> &S, size_t &i, size_t &j);
    };
}