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

#include <utils.hpp>
#include <cstring>
#include <iostream>
#include <fstream>


void SilkCasket::Utils::Vuint8ToFile(const std::filesystem::path &Path, const std::vector<uint8_t> &Content,
                                     size_t chunkSize) {

    std::ofstream file(Path, std::ios::binary | std::ios::trunc);
    if (!file.is_open()) {
        std::cerr << "Failed to open file for writing." << std::endl;
    }

    try {
        size_t totalBytes = Content.size();
        for (size_t offset = 0; offset < totalBytes; offset += chunkSize) {
            size_t bytesToWrite = std::min(chunkSize, totalBytes - offset);
            file.write(reinterpret_cast<const char*>(&Content[offset]), static_cast<std::streamsize>(bytesToWrite));
            if (!file.good()) {
                std::cerr << "Error writing to file." << std::endl;
                return;
            }
        }
        file.flush();
    } catch (const std::exception& e) {
        file.close();
        std::cerr << e.what() << std::endl;
        return;
    }
    file.close();
}

void SilkCasket::Utils::stringToFile(const std::filesystem::path &Path, const std::string &Content) {
    std::ofstream file(Path, std::ios::binary);
    if (!file.is_open()) {
        std::cerr << "Unable to open file:" << Path <<  std::endl;
        return;
    }
    file.write(Content.data(), Content.size());
    if (!file) {
        std::cerr << "Unable to write out the file:" << Path << std::endl;
        return;
    }
    file.close();
}

void SilkCasket::Utils::copyFileTo(const std::filesystem::path &Source, const std::filesystem::path &Target,
                                   size_t chunkSize) {
    std::ifstream sourceFile(Source, std::ios::binary);
    if (!sourceFile.is_open()) {
        std::cerr << "Failed to open source file for reading." << std::endl;
        return;
    }

    std::ofstream targetFile(Target, std::ios::binary | std::ios::trunc);
    if (!targetFile.is_open()) {
        sourceFile.close();
        std::cerr << "Failed to open target file for writing." << std::endl;
        return;
    }

    try {
        std::vector<uint8_t> buffer(chunkSize);

        while (true) {
            sourceFile.read(reinterpret_cast<char*>(buffer.data()), static_cast<std::streamsize>(chunkSize));
            auto bytesRead = sourceFile.gcount();

            if (bytesRead == 0) {
                break;
            }

            targetFile.write(reinterpret_cast<const char*>(buffer.data()), bytesRead);

            if (!targetFile.good()) {
                std::cerr << "Error writing to target file." << std::endl;
                return;
            }

            if (bytesRead < chunkSize) {
                break;
            }
        }
        targetFile.flush();
    } catch (const std::exception& e) {
        sourceFile.close();
        targetFile.close();
        std::cerr << e.what() << std::endl;
        return;
    }

    sourceFile.close();
    targetFile.close();
}

void SilkCasket::Utils::copyDirectory(const std::filesystem::path &Source, const std::filesystem::path &Target,
                                      size_t chunkSize) {
    if (!std::filesystem::exists(Source) || !std::filesystem::is_directory(Source)) {
        std::cerr << "Source directory does not exist or is not a directory." << std::endl;
        return;
    }

    if (std::filesystem::exists(Target)) {
        if (!std::filesystem::is_directory(Target)) {
            std::cerr << "Target path exists and is not a directory." << std::endl;
            return;
        }
    } else {
        if (!std::filesystem::create_directories(Target)) {
            std::cerr << "Failed to create target directory." << std::endl;
            return;
        }
    }

    try {
        for (const auto &entry : std::filesystem::recursive_directory_iterator(Source)) {
            std::filesystem::path relativePath = std::filesystem::relative(entry.path(), Source);
            std::filesystem::path targetPath = Target / relativePath;

            if (std::filesystem::is_regular_file(entry.status())) {
                copyFileTo(entry.path(), targetPath, chunkSize);
            } else if (std::filesystem::is_directory(entry.status())) {
                if (!std::filesystem::exists(targetPath)) {
                    if (!std::filesystem::create_directory(targetPath)) {
                        throw std::runtime_error("Failed to create subdirectory in target directory.");
                    }
                }
            } else {
                continue;
            }
        }
    } catch (const std::filesystem::filesystem_error& e) {
        std::cerr << "Filesystem error:" << e.what() << std::endl;
    } catch (const std::exception& e) {
        std::cerr << "Error copying directory:" << e.what() << std::endl;
    }
}


std::vector<uint8_t> SilkCasket::Utils::readFile(const std::filesystem::path &Path) {
    if (!std::filesystem::exists(Path)) {
        std::cerr << "The file does not exist:" << Path << std::endl;
        return {};
    }

    auto fileSize = std::filesystem::file_size(Path);

    std::vector<uint8_t> buffer(fileSize);

    std::ifstream file(Path, std::ios::binary);
    if (!file) {
        std::cerr << "Unable to open file:" << Path << std::endl;
    }

    file.read(reinterpret_cast<char*>(buffer.data()), fileSize);
    if (!file) {
        std::cerr << "Unable to read file:" << Path << std::endl;
    }

    return buffer;
}

std::vector<uint8_t>
SilkCasket::Utils::readFileAddress(const std::filesystem::path &Path, const size_t &offset, const size_t &size) {
    std::ifstream file(Path, std::ios::binary | std::ios::in);
    if (!file.is_open()) {
        std::cerr << "Unable to open file:" << Path << std::endl;
        return {};
    }
    file.seekg( offset, std::ios::beg);
    std::vector<uint8_t> buffer(size);
    file.read(reinterpret_cast<char*>(buffer.data()), size);
    if (file.bad()) {
        std::cerr << "Unable to read data:" << Path << std::endl;
    }
    return buffer;
}