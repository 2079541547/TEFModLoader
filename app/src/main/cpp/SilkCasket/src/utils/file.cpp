/*******************************************************************************
 * 文件名称: file
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

#include <utils/file.hpp>
#include <log.hpp>


// 异步写入任务
void AsyncWriteToFile(int fd, const uint8_t* data, size_t size, size_t offset) {
    // 映射文件到内存
    void* map = mmap(nullptr, size, PROT_WRITE, MAP_SHARED, fd, offset);
    if (map == MAP_FAILED) {
        LOG(SilkCasket::LogLevel::ERROR, "SilkCasket::Utils::File", "AsyncWriteToFile", "内存映射失败");
        return;
    }

    // 将数据写入内存映射区域
    memcpy(map, data, size);

    // 同步内存映射区域到文件
    if (msync(map, size, MS_SYNC) == -1) {
        LOG(SilkCasket::LogLevel::ERROR, "SilkCasket::Utils::File", "AsyncWriteToFile", "同步内存映射区域失败");
    }

    // 解除内存映射
    if (munmap(map, size) == -1) {
        LOG(SilkCasket::LogLevel::ERROR, "SilkCasket::Utils::File", "AsyncWriteToFile", "解除内存映射失败");
    }
}

// 异步复制任务
void AsyncCopyChunk(int srcFd, int dstFd, size_t offset, size_t size) {
    char buffer[4096];
    while (size > 0) {
        ssize_t bytesRead = read(srcFd, buffer, std::min(size, sizeof(buffer)));
        if (bytesRead <= 0) {
            if (bytesRead == -1) {
                LOG(SilkCasket::LogLevel::ERROR, "SilkCasket::Utils::File", "AsyncCopyChunk", "读取文件失败");
            }
            return;
        }

        ssize_t bytesWritten = 0;
        while (bytesWritten < bytesRead) {
            ssize_t result = write(dstFd, buffer + bytesWritten, bytesRead - bytesWritten);
            if (result <= 0) {
                if (result == -1) {
                    LOG(SilkCasket::LogLevel::ERROR, "SilkCasket::Utils::File", "AsyncCopyChunk", "写入文件失败");
                }
                return;
            }
            bytesWritten += result;
        }

        size -= bytesRead;
        offset += bytesRead;
    }
}


void SilkCasket::Utils::File::Vuint8ToFile(const filesystem::path &Path,
                                           const vector<uint8_t> &Content,
                                           size_t chunkSize) {
    int fd = open(Path.c_str(), O_RDWR | O_CREAT | O_TRUNC, S_IRUSR | S_IWUSR | S_IRGRP | S_IROTH);
    if (fd == -1) {
        LOG(SilkCasket::LogLevel::ERROR, "SilkCasket::Utils::File", "Vuint8ToFile", "无法打开文件：" + Path.string());
        return;
    }

    // 预分配文件大小
    if (ftruncate(fd, Content.size()) == -1) {
        LOG(SilkCasket::LogLevel::ERROR, "SilkCasket::Utils::File", "Vuint8ToFile", "预分配文件大小失败：" + Path.string());
        close(fd);
        return;
    }

    // 分割数据并启动异步写入任务
    std::vector<std::future<void>> futures;
    for (size_t i = 0; i < Content.size(); i += chunkSize) {
        size_t size = std::min(chunkSize, Content.size() - i);
        futures.push_back(std::async(std::launch::async, AsyncWriteToFile, fd, &Content[i], size, i));
    }

    // 等待所有异步任务完成
    for (auto& fut : futures) {
        fut.get();
    }

    // 关闭文件描述符
    close(fd);
}


void
SilkCasket::Utils::File::stringToFile(const filesystem::path &Path, const std::string &Content) {
    // 打开文件，以二进制模式写入
    std::ofstream file(Path, std::ios::binary);
    if (!file.is_open()) {
        LOG(SilkCasket::LogLevel::ERROR, "SilkCasket::Utils::File", "stringToFile", "无法打开文件：" + Path.string());
    }

    // 写入内容
    file.write(Content.data(), Content.size());

    // 检查写入是否成功
    if (!file) {
        LOG(SilkCasket::LogLevel::ERROR, "SilkCasket::Utils::File", "stringToFile", "无法写出文件：" + Path.string());
    }

    // 关闭文件
    file.close();
}

void SilkCasket::Utils::File::copyFileTo(const filesystem::path &Source,
                                         const filesystem::path &Target,
                                         size_t chunkSize) {
    int srcFd = open(Source.c_str(), O_RDONLY);
    if (srcFd == -1) {
        LOG(SilkCasket::LogLevel::ERROR, "SilkCasket::Utils::File", "copyFileTo", "无法打开源文件：" + Source.string());
        return;
    }

    struct stat fileInfo{};
    if (fstat(srcFd, &fileInfo) == -1) {
        LOG(SilkCasket::LogLevel::ERROR, "SilkCasket::Utils::File", "copyFileTo", "获取文件信息失败：" + Source.string());
        close(srcFd);
        return;
    }

    int dstFd = open(Target.c_str(), O_WRONLY | O_CREAT | O_TRUNC, S_IRUSR | S_IWUSR | S_IRGRP | S_IROTH);
    if (dstFd == -1) {
        LOG(SilkCasket::LogLevel::ERROR, "SilkCasket::Utils::File", "copyFileTo", "无法打开目标文件：" + Target.string());
        close(srcFd);
        return;
    }

    // 预分配目标文件大小
    if (ftruncate(dstFd, fileInfo.st_size) == -1) {
        LOG(SilkCasket::LogLevel::ERROR, "SilkCasket::Utils::File", "copyFileTo", "预分配文件大小失败：" + Target.string());
        close(srcFd);
        close(dstFd);
        return;
    }

    // 分割文件并启动异步复制任务
    std::vector<std::future<void>> futures;
    for (size_t i = 0; i < fileInfo.st_size; i += chunkSize) {
        size_t size = std::min(chunkSize, static_cast<size_t>(fileInfo.st_size - i));
        futures.push_back(std::async(std::launch::async, AsyncCopyChunk, srcFd, dstFd, i, size));
    }

    // 等待所有异步任务完成
    for (auto& fut : futures) {
        fut.get();
    }

    // 关闭文件描述符
    close(srcFd);
    close(dstFd);
}


void SilkCasket::Utils::File::copyDirectory(const filesystem::path &Source,
                                            const filesystem::path &Target, size_t chunkSize) {
    if (!filesystem::exists(Source)) {
        LOG(SilkCasket::LogLevel::ERROR, "SilkCasket::Utils::File", "copyDirectory", "源目录不存在：" + Source.string());
        return;
    }

    if (filesystem::is_directory(Source)) {
        // 创建目标目录
        if (!filesystem::exists(Target)) {
            if (!filesystem::create_directories(Target)) {
                LOG(SilkCasket::LogLevel::ERROR, "SilkCasket::Utils::File", "copyDirectory", "无法创建目标目录：" + Target.string());
                return;
            }
        }

        // 递归复制目录内容
        for (const auto& entry : filesystem::directory_iterator(Source)) {
            if (filesystem::is_regular_file(entry.status())) {
                copyFileTo(entry.path(), Target / entry.path().filename(), chunkSize);
            } else if (filesystem::is_directory(entry.status())) {
                copyDirectory(entry.path(), Target / entry.path().filename(), chunkSize);
            }
        }
    } else {
        LOG(SilkCasket::LogLevel::ERROR, "SilkCasket::Utils::File", "copyDirectory", "源路径不是目录：" + Source.string());
    }
}


std::vector <uint8_t> SilkCasket::Utils::File::readFile(const filesystem::path &Path) {
    // 检查文件是否存在
    if (!filesystem::exists(Path)) {
        LOG(LogLevel::ERROR, "SilkCasket::Utils::File", "readFile", "文件不存在：" + Path.string());
    }

    // 获取文件大小
    auto fileSize = filesystem::file_size(Path);

    // 创建一个足够大的vector来存放文件数据
    std::vector<uint8_t> buffer(fileSize);

    // 打开文件
    std::ifstream file(Path, std::ios::binary);
    if (!file) {
        LOG(LogLevel::ERROR, "SilkCasket::Utils::File", "readFile", "无法打开文件：" + Path.string());
    }

    // 读取文件内容到vector
    file.read(reinterpret_cast<char*>(buffer.data()), fileSize);
    if (!file) {
        LOG(LogLevel::ERROR, "SilkCasket::Utils::File", "readFile", "无法读取文件：" + Path.string());
    }

    return buffer;
}

std::vector <uint8_t>
SilkCasket::Utils::File::readFileAddress(const filesystem::path &Path, const long &offset,
                                         const long &size) {
    std::ifstream file(Path, std::ios::binary | std::ios::in);
    if (!file.is_open()) {
        LOG(LogLevel::ERROR, "SilkCasket::Utils::File", "readFileAddress", "无法打开文件：" + Path.string());
    }
    file.seekg( offset, std::ios::beg);
    std::vector<uint8_t> buffer(size);
    file.read(reinterpret_cast<char*>(buffer.data()), size);

    if (file.bad()) {
        LOG(LogLevel::ERROR, "SilkCasket::Utils::File", "readFileAddress", "无法读取数据：" + Path.string());
    }
    return buffer;
}