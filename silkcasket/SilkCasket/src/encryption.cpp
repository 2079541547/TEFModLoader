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

#include <encryption.hpp>
#include <ctime>


std::vector<unsigned char> hashToBytes(const std::string &keyStr) {
    std::vector<unsigned char> key(keyStr.begin(), keyStr.end());
    if (key.size() > 256) {
        key.resize(256);
    }
    return key;
}


void SilkCasket::RC4::rc4_ksa(const unsigned char *key, size_t key_len, std::vector<unsigned char> &S) {
    for (size_t i = 0; i < 256; ++i) {
        S[i] = static_cast<unsigned char>(i);
    }
    size_t j = 0;
    for (size_t i = 0; i < 256; ++i) {
        j = (j + S[i] + key[i % key_len]) % 256;
        std::swap(S[i], S[j]);
    }

    for (size_t i = 0; i < 256; i += 7) {
        size_t idx1 = (i + S[i]) % 256;
        size_t idx2 = (idx1 + 123) % 256;
        std::swap(S[idx1], S[idx2]);
    }
}

unsigned char SilkCasket::RC4::rc4_prga(std::vector<unsigned char> &S, size_t &i, size_t &j) {
    i = (i + 1) % 256;
    j = (j + S[i]) % 256;
    std::swap(S[i], S[j]);

    size_t t = (S[i] + S[j]) % 256;
    size_t u = (t + 1) % 256;
    std::swap(S[t], S[u]);

    return S[(S[i] + S[j]) % 256];
}

std::vector<unsigned char>
SilkCasket::RC4::rc4_encrypt(const std::vector<unsigned char> &data, const std::string &keyStr) {
    auto keyBytes = hashToBytes(keyStr);

    std::vector<unsigned char> S(256);
    rc4_ksa(keyBytes.data(), keyBytes.size(), S);

    size_t i = 0, j = 0;
    for (size_t k = 0; k < 256; ++k) {
        rc4_prga(S, i, j);
    }

    std::vector<unsigned char> ciphertext(data.size());
    for (size_t k = 0; k < data.size(); ++k) {
        ciphertext[k] = data[k] ^ rc4_prga(S, i, j);
    }

    return ciphertext;
}

std::vector<unsigned char>
SilkCasket::RC4::rc4_decrypt(const std::vector<unsigned char> &data, const std::string &keyStr) {
    auto keyBytes = hashToBytes(keyStr);

    std::vector<unsigned char> S(256);
    rc4_ksa(keyBytes.data(), keyBytes.size(), S);

    size_t i = 0, j = 0;
    for (size_t k = 0; k < 256; ++k) {
        rc4_prga(S, i, j);
    }

    std::vector<unsigned char> plaintext(data.size());
    for (size_t k = 0; k < data.size(); ++k) {
        plaintext[k] = data[k] ^ rc4_prga(S, i, j);
    }

    return plaintext;
}
