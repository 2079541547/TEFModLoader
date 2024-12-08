/*******************************************************************************
 * 文件名称: encryption
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
#include <string>
#include <cstring>
#include <cstdlib>
#include <ctime>
#include <cstdint>
#include <random>

// Assuming SilkHash256.hpp is in the same directory or included path
#include "SilkHash/SilkHash256.hpp"
#include <iostream>
#include <vector>
#include <cstdint>
#include <cstring>
#include <stdexcept>
#include <endian.h>
#include <fstream>
#include <random>
#include <ctime>



const uint32_t SIGMA[] = {0x61707865, 0x3320646e, 0x79622d32, 0x6b206574};


namespace SilkCasket {
    class Encryption {
    public:
        explicit Encryption(const std::string &key)
        {
            key_ = convertToByteArray(key);
            if (key_.size() != 32)
                throw std::runtime_error("Key must be 256 bits long.");
        }

        struct EncryptedData
        {
            std::vector<uint8_t> ciphertext;
            uint8_t tag[16]{};
            std::vector<uint8_t> nonce;
            std::vector<uint8_t> associatedData;
        };

        EncryptedData encrypt(const std::vector<uint8_t> &plaintext)
        {
            std::vector<uint8_t> nonce = generateRandomBytes(12);
            std::vector<uint8_t> associatedData = generateRandomBytes(16); // Example: 16 bytes of random data
            return encryptChaCha20Poly1305(key_, nonce, plaintext, associatedData);
        }

        bool decrypt(const EncryptedData &encryptedData, std::vector<uint8_t> &decryptedText)
        {
            return decryptChaCha20Poly1305(key_, encryptedData.nonce, encryptedData, encryptedData.associatedData, decryptedText);
        }

    private:
        std::vector<uint8_t> key_;

        static std::vector<uint8_t> convertToByteArray(const std::string& KEY) {
            SilkHash256::HashResult A = SilkHash256::hashString(KEY);
            std::vector<uint8_t> byteArray(sizeof(SilkHash256::HashResult));
            memcpy(byteArray.data(), &A, sizeof(SilkHash256::HashResult));
            return byteArray;
        }

        static void quarterRound(uint32_t &a, uint32_t &b, uint32_t &c, uint32_t &d)
        {
            a += b;
            d ^= a;
            d = (d << 16) | (d >> 16);
            c += d;
            b ^= c;
            b = (b << 12) | (b >> 20);
            a += b;
            d ^= a;
            d = (d << 8) | (d >> 24);
            c += d;
            b ^= c;
            b = (b << 7) | (b >> 25);
        }

        void chachaCore(uint32_t state[16])
        {
            for (int i = 0; i < 10; ++i)
            {
                quarterRound(state[0], state[4], state[8], state[12]);
                quarterRound(state[1], state[5], state[9], state[13]);
                quarterRound(state[2], state[6], state[10], state[14]);
                quarterRound(state[3], state[7], state[11], state[15]);

                quarterRound(state[0], state[5], state[10], state[15]);
                quarterRound(state[1], state[6], state[11], state[12]);
                quarterRound(state[2], state[7], state[8], state[13]);
                quarterRound(state[3], state[4], state[9], state[14]);
            }
        }

        std::vector<uint8_t> chacha20(const std::vector<uint8_t> &key, const std::vector<uint8_t> &nonce, const std::vector<uint8_t> &plaintext, uint32_t counter = 0)
        {
            if (key.size() != 32)
                throw std::runtime_error("Key must be 256 bits long.");

            std::vector<uint8_t> extendedNonce(12, 0);
            size_t nonceLength = nonce.size();
            memcpy(extendedNonce.data() + (12 - nonceLength), nonce.data(), nonceLength);

            std::vector<uint8_t> ciphertext;
            size_t blocks = (plaintext.size() + 63) / 64;

            for (size_t blockIndex = 0; blockIndex < blocks; ++blockIndex)
            {
                uint32_t state[16];
                memcpy(state, SIGMA, sizeof(SIGMA));
                memcpy(&state[4], key.data(), 32);
                state[12] = counter;
                memcpy(&state[13], extendedNonce.data(), 12);

                uint32_t originalState[16];
                memcpy(originalState, state, sizeof(state));

                chachaCore(state);

                uint8_t keystream[64];
                for (int i = 0; i < 16; ++i)
                {
                    keystream[i * 4 + 0] = ((uint8_t *)state)[i * 4 + 0] ^ ((uint8_t *)originalState)[i * 4 + 0];
                    keystream[i * 4 + 1] = ((uint8_t *)state)[i * 4 + 1] ^ ((uint8_t *)originalState)[i * 4 + 1];
                    keystream[i * 4 + 2] = ((uint8_t *)state)[i * 4 + 2] ^ ((uint8_t *)originalState)[i * 4 + 2];
                    keystream[i * 4 + 3] = ((uint8_t *)state)[i * 4 + 3] ^ ((uint8_t *)originalState)[i * 4 + 3];
                }

                size_t bytesToXOR = std::min<size_t>(64, plaintext.size() - blockIndex * 64);
                for (size_t j = 0; j < bytesToXOR; ++j)
                {
                    ciphertext.push_back(plaintext[blockIndex * 64 + j] ^ keystream[j]);
                }

                counter++;
            }

            return ciphertext;
        }

        static void poly1305_authenticate(uint8_t mac[16], const uint8_t *msg, size_t msgLen, const uint8_t *key) {
            // Initialize the state
            uint32_t h[5] = {0, 0, 0, 0, 0};
            uint32_t r[5] = {0, 0, 0, 0, 1};

            // Set the key values
            for (int i = 0; i < 4; ++i)
                r[i] = le32toh(*(uint32_t *)(key + i * 4)) & (((i == 3) ? 0xffffffc0 : 0xffffffff) - 0x5);

            // Pad the message to a multiple of 16 bytes
            size_t paddedSize = (msgLen + 15) & ~15;
            std::vector<uint8_t> pad(paddedSize, 0);
            memcpy(pad.data(), msg, msgLen);
            if (msgLen > 0) {
                pad[msgLen - 1] |= 0x01;  // Ensure the last byte is set to 0x01
            }

            // Process the message in 16-byte blocks
            for (size_t i = 0; i < paddedSize / 16; ++i) {
                uint32_t t[5] = {0, 0, 0, 0, 1};  // Initialize t to 0

                // Read 16 bytes from the padded message
                for (int j = 0; j < 4; ++j) {
                    if (i * 16 + j * 4 < msgLen) {
                        t[j] = le32toh(*(uint32_t *)(pad.data() + i * 16 + j * 4));
                    }
                }

                // Update the state
                for (int j = 0; j < 5; ++j)
                    h[j] += t[j];

                uint64_t u = 0;
                for (int j = 0; j < 5; ++j) {
                    u += (uint64_t)h[j] * r[j];
                    h[j] = (uint32_t)u;
                    u >>= 32;
                }
                h[4] += (uint32_t)(u * 5);
            }

            uint64_t finalCarry = 0;
            for (int j = 0; j < 5; ++j) {
                finalCarry += h[j];
                h[j] = (uint32_t)finalCarry;
                finalCarry >>= 32;
            }

            uint32_t g[5] = {(~r[0] + 1) & 0xffffffc0, (~r[1]) & 0xffffffff, (~r[2]) & 0xffffffff, (~r[3]) & 0xffffffc0, 0};
            bool overflow = false;
            for (int j = 0; j < 5; ++j) {
                uint64_t s = (uint64_t)h[j] + g[j] + overflow;
                h[j] = (uint32_t)s;
                overflow = s >> 32;
            }

            if (overflow)
                for (int j = 0; j < 5; ++j) {
                    uint64_t s = (uint64_t)h[j] + r[j];
                    h[j] = (uint32_t)s;
                    overflow = s >> 32;
                }

            // Store the result in the MAC
            for (int j = 0; j < 4; ++j)
                *(uint32_t *)(mac + j * 4) = htole32(h[j]);
        }

        static bool verifyMac(const uint8_t *computedMac, const uint8_t *receivedMac)
        {
            for (int i = 0; i < 16; ++i)
                if (computedMac[i] != receivedMac[i])
                    return false;
            return true;
        }

        EncryptedData encryptChaCha20Poly1305(const std::vector<uint8_t> &key, const std::vector<uint8_t> &nonce, const std::vector<uint8_t> &plaintext, const std::vector<uint8_t> &associatedData)
        {
            std::vector<uint8_t> subkey = chacha20(key, nonce, std::vector<uint8_t>(32), 0);

            std::vector<uint8_t> ciphertext = chacha20(subkey, nonce, plaintext, 1);

            size_t adPaddedSize = (associatedData.size() + 15) & ~15;
            uint8_t *adPadded = new uint8_t[adPaddedSize];
            memset(adPadded, 0, adPaddedSize);
            memcpy(adPadded, associatedData.data(), associatedData.size());
            adPadded[associatedData.size()] |= 0x01;

            size_t ctPaddedSize = (ciphertext.size() + 15) & ~15;
            uint8_t *ctPadded = new uint8_t[ctPaddedSize];
            memset(ctPadded, 0, ctPaddedSize);
            memcpy(ctPadded, ciphertext.data(), ciphertext.size());
            ctPadded[ciphertext.size()] |= 0x01;

            uint8_t authTag[16];
            poly1305_authenticate(authTag, adPadded, adPaddedSize, subkey.data() + 16);
            poly1305_authenticate(authTag, ctPadded, ctPaddedSize, subkey.data() + 16);

            uint64_t lenAd = htobe64((uint64_t)associatedData.size());
            uint64_t lenCt = htobe64((uint64_t)ciphertext.size());
            poly1305_authenticate(authTag, (uint8_t *)&lenAd, 8, subkey.data() + 16);
            poly1305_authenticate(authTag, (uint8_t *)&lenCt, 8, subkey.data() + 16);

            EncryptedData result;
            result.ciphertext = ciphertext;
            memcpy(result.tag, authTag, 16);
            result.nonce = nonce;
            result.associatedData = associatedData;

            delete[] adPadded;
            delete[] ctPadded;

            return result;
        }

        bool decryptChaCha20Poly1305(const std::vector<uint8_t> &key, const std::vector<uint8_t> &nonce, const EncryptedData &encryptedData, const std::vector<uint8_t> &associatedData, std::vector<uint8_t> &decryptedText)
        {
            std::vector<uint8_t> subkey = chacha20(key, nonce, std::vector<uint8_t>(32), 0);

            size_t adPaddedSize = (associatedData.size() + 15) & ~15;
            uint8_t *adPadded = new uint8_t[adPaddedSize];
            memset(adPadded, 0, adPaddedSize);
            memcpy(adPadded, associatedData.data(), associatedData.size());
            adPadded[associatedData.size()] |= 0x01;

            size_t ctPaddedSize = (encryptedData.ciphertext.size() + 15) & ~15;
            uint8_t *ctPadded = new uint8_t[ctPaddedSize];
            memset(ctPadded, 0, ctPaddedSize);
            memcpy(ctPadded, encryptedData.ciphertext.data(), encryptedData.ciphertext.size());
            ctPadded[encryptedData.ciphertext.size()] |= 0x01;

            uint8_t computedTag[16];
            poly1305_authenticate(computedTag, adPadded, adPaddedSize, subkey.data() + 16);
            poly1305_authenticate(computedTag, ctPadded, ctPaddedSize, subkey.data() + 16);

            uint64_t lenAd = htobe64((uint64_t)associatedData.size());
            uint64_t lenCt = htobe64((uint64_t)encryptedData.ciphertext.size());
            poly1305_authenticate(computedTag, (uint8_t *)&lenAd, 8, subkey.data() + 16);
            poly1305_authenticate(computedTag, (uint8_t *)&lenCt, 8, subkey.data() + 16);

            if (!verifyMac(computedTag, encryptedData.tag))
            {
                delete[] adPadded;
                delete[] ctPadded;
                return false;
            }

            decryptedText = chacha20(subkey, nonce, encryptedData.ciphertext, 1);

            delete[] adPadded;
            delete[] ctPadded;

            return true;
        }

        std::vector<uint8_t> generateRandomBytes(size_t length)
        {
            std::mt19937 rng(static_cast<unsigned int>(std::time(nullptr)));
            std::uniform_int_distribution<> dist(0, 255);
            std::vector<uint8_t> randomBytes(length);
            for (auto &byte : randomBytes)
            {
                byte = static_cast<uint8_t>(dist(rng));
            }
            return randomBytes;
        }
    };



    inline std::vector<uint8_t> encryptFile(const std::string &keyStr, const std::vector<uint8_t> &plaintext)
    {
        try
        {
            Encryption cipher(keyStr);
            Encryption::EncryptedData encrypted = cipher.encrypt(plaintext);

            std::vector<uint8_t> outputData;
            outputData.insert(outputData.end(), encrypted.nonce.begin(), encrypted.nonce.end());
            outputData.insert(outputData.end(), encrypted.associatedData.begin(), encrypted.associatedData.end());
            outputData.insert(outputData.end(), encrypted.ciphertext.begin(), encrypted.ciphertext.end());
            outputData.insert(outputData.end(), encrypted.tag, encrypted.tag + 16);

            return outputData;
        }
        catch (const std::exception &e)
        {
            std::cerr << "Error: " << e.what() << "\n";
            return {};
        }
    }

    inline std::vector<uint8_t> decryptFile(const std::string &keyStr, const std::vector<uint8_t> &inputData)
    {
        if (inputData.size() < 44)
        { // 12 bytes nonce + 16 bytes assocData + 16 bytes tag
            std::cerr << "Input data is too small to contain valid encrypted data and metadata.\n";
            return {};
        }

        try
        {
            Encryption::EncryptedData encrypted;
            encrypted.nonce.assign(inputData.begin(), inputData.begin() + 12);
            encrypted.associatedData.assign(inputData.begin() + 12, inputData.begin() + 28);
            encrypted.ciphertext.assign(inputData.begin() + 28, inputData.end() - 16);
            memcpy(encrypted.tag, inputData.data() + inputData.size() - 16, 16);

            Encryption cipher(keyStr);
            std::vector<uint8_t> decryptedText(inputData.size() - 44);
            if (cipher.decrypt(encrypted, decryptedText))
            {
                return decryptedText;
            }
            else
            {
                std::cout << "Decryption failed: MAC verification error.\n";
                return {};
            }
        }
        catch (const std::exception &e)
        {
            std::cerr << "Error: " << e.what() << "\n";
            return {};
        }
    }
}