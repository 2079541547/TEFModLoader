/*******************************************************************************
 * 文件名称: log
 * 项目名称: TEFModLoader
 * 创建时间: 2025/2/12
 * 作者: EternalFuture゙
 * Github: https://github.com/2079541547
 * 版权声明: Copyright © 2024 EternalFuture. All rights reserved.
 * 许可证: This program is free software: you can redistribute it and/or modify
 *         it under the terms of the GNU Affero General Public License as published
 *         by the Free Software Foundation, either version 3 of the License, or
 *         (at your option) any later version.
 *
 *         This program is distributed in the hope that it will be useful,
 *         but WITHOUT ANY WARRANTY; without even the implied warranty of
 *         MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *         GNU Affero General Public License for more details.
 *
 *         You should have received a copy of the GNU Affero General Public License
 *         along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * 注意事项: 请严格遵守GNU AGPL v3.0协议使用本代码，任何未经授权的商业用途均属侵权行为。
 *******************************************************************************/

#include <TEFModLoader/log.hpp>
#include <android/log.h>
#include <iostream>
#include <cstdio>
#include <unistd.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <pthread.h>

TEFModLoader::Log::AndroidLogStreamBuffer::~AndroidLogStreamBuffer() {
    flushBuffer();
}

TEFModLoader::Log::AndroidLogStreamBuffer::int_type TEFModLoader::Log::AndroidLogStreamBuffer::overflow(int_type v) {
    if (v != EOF) {
        char c = traits_type::to_char_type(v);
        buffer += c;
        if (c == '\n') {
            flushBuffer();
        }
    }
    return v;
}

std::streamsize TEFModLoader::Log::AndroidLogStreamBuffer::xsputn(const char *s, std::streamsize n) {
    buffer.append(s, n);
    return n;
}

void TEFModLoader::Log::AndroidLogStreamBuffer::flushBuffer() {
    if (!buffer.empty()) {
        __android_log_print(logLevel, "TEFModLoader", "%s", buffer.c_str());
        buffer.clear();
    }
}

void TEFModLoader::Log::redirectStdStreams() {
    static TEFModLoader::Log::AndroidLogStreamBuffer logBufferCout(ANDROID_LOG_INFO);
    static TEFModLoader::Log::AndroidLogStreamBuffer logBufferCerr(ANDROID_LOG_ERROR);

    static std::streambuf* originalCoutBuffer = std::cout.rdbuf();
    static std::streambuf* originalCerrBuffer = std::cerr.rdbuf();

    std::cout.rdbuf(&logBufferCout);
    std::cerr.rdbuf(&logBufferCerr);

    int pipefd_stdout[2];
    int pipefd_stderr[2];

    if (pipe(pipefd_stdout) == -1 || pipe(pipefd_stderr) == -1) {
        __android_log_print(ANDROID_LOG_ERROR, "TEFModLoader", "Failed to create pipe");
        return;
    }

    dup2(pipefd_stdout[1], STDOUT_FILENO);
    dup2(pipefd_stderr[1], STDERR_FILENO);

    close(pipefd_stdout[1]);
    close(pipefd_stderr[1]);

    auto readPipe = [](void* arg) -> void* {
        int fd = *static_cast<int*>(arg);
        char buffer[1024];
        ssize_t bytesRead;
        while ((bytesRead = read(fd, buffer, sizeof(buffer) - 1)) > 0) {
            buffer[bytesRead] = '\0';
            __android_log_print(ANDROID_LOG_INFO, "TEFModLoader", "%s", buffer);
        }
        return nullptr;
    };

    pthread_t thread_stdout;
    pthread_t thread_stderr;

    pthread_create(&thread_stdout, nullptr, readPipe, &pipefd_stdout[0]);
    pthread_create(&thread_stderr, nullptr, readPipe, &pipefd_stderr[0]);
}