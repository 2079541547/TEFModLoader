/*******************************************************************************
 * 文件名称: utility
 * 项目名称: TEFModLoader
 * 创建时间: 2025/2/11
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

#include <TEFModLoader/utility.hpp>
#include <dlfcn.h>
#include <link.h>
#include <iostream>
#include <sstream>

std::filesystem::path TEFModLoader::Utility::getFilesDir(JNIEnv *env) {
    std::cout << "Starting getFilesDir function." << std::endl;

    jobject application = nullptr;
    jclass activity_thread_clz = env->FindClass("android/app/ActivityThread");

    if (activity_thread_clz != nullptr) {
        std::cout << "Found ActivityThread class." << std::endl;

        jmethodID currentApplicationThread = env->GetStaticMethodID(activity_thread_clz, "currentActivityThread", "()Landroid/app/ActivityThread;");
        if (currentApplicationThread != nullptr) {
            std::cout << "Found currentActivityThread method." << std::endl;

            jobject currentActivityThread = env->CallStaticObjectMethod(activity_thread_clz, currentApplicationThread);
            jmethodID getApplication = env->GetMethodID(activity_thread_clz, "getApplication", "()Landroid/app/Application;");
            application = env->CallObjectMethod(currentActivityThread, getApplication);

            if (application != nullptr) {
                std::cout << "Retrieved application object successfully." << std::endl;

                jclass context_clz = env->GetObjectClass(application);
                jmethodID getFilesDir = env->GetMethodID(context_clz, "getFilesDir", "()Ljava/io/File;");
                if (getFilesDir != nullptr) {
                    std::cout << "Found getFilesDir method." << std::endl;

                    jobject filesDir = env->CallObjectMethod(application, getFilesDir);
                    jclass file_clz = env->FindClass("java/io/File");
                    jmethodID getPath = env->GetMethodID(file_clz, "getPath", "()Ljava/lang/String;");
                    if (getPath != nullptr) {
                        std::cout << "Found getPath method." << std::endl;

                        jstring path = (jstring) env->CallObjectMethod(filesDir, getPath);
                        const char* filePath = env->GetStringUTFChars(path, nullptr);
                        std::string result(filePath);
                        env->ReleaseStringUTFChars(path, filePath);
                        std::cout << "Files directory path: " << result << std::endl;
                        return result;
                    } else {
                        std::cerr << "Failed to find getPath method." << std::endl;
                    }
                } else {
                    std::cerr << "Failed to find getFilesDir method." << std::endl;
                }
            } else {
                std::cerr << "Failed to retrieve application object." << std::endl;
            }
        } else {
            std::cerr << "Failed to find currentActivityThread method." << std::endl;
        }
    } else {
        std::cerr << "Failed to find ActivityThread class." << std::endl;
    }

    return "error";
}

std::filesystem::path TEFModLoader::Utility::getModDir() {
    std::cout << "Starting getModDir function." << std::endl;

    Dl_info dl_info;
    std::filesystem::path r;

    if (dladdr((void*)TEFModLoader::Utility::getModDir, &dl_info)) {
        if (dl_info.dli_fname) {
            std::cout << "Found module filename: " << dl_info.dli_fname << std::endl;

            std::filesystem::path d(dl_info.dli_fname);
            r = d.parent_path() / "Mod";
            std::cout << "Module directory path: " << r << std::endl;
        } else {
            std::cerr << "Failed to retrieve module filename." << std::endl;
        }
    } else {
        std::cerr << "Failed to retrieve module information." << std::endl;
    }

    if (r.empty()) {
        std::cerr << "Module directory path is empty. Returning error." << std::endl;
        return "error";
    }

    return r;
}

void TEFModLoader::Utility::printMemoryHexView(const void* ptr, size_t range, size_t hex_width) {
    if (!ptr) {
        std::cout << "Pointer is null" << std::endl;
        return;
    }

    std::vector<unsigned char> buffer(2 * range);
    const unsigned char* start = static_cast<const unsigned char*>(ptr) - range;

    if (start < reinterpret_cast<const unsigned char*>(0)) {
        std::cout << "Pointer underflow detected" << std::endl;
        return;
    }

    memcpy(buffer.data(), start, range);
    memcpy(buffer.data() + range, ptr, range);

#if defined(__LP64__)
    std::cout << "Hex View:         ";
#else
    std::cout << "Hex View: ";
#endif
    for (size_t i = 0; i < hex_width; ++i) {
        std::cout << std::setw(2) << std::setfill('0') << std::hex << i << " ";
    }
    std::cout << std::endl;

    std::string ascii(hex_width, '\0');

    for (size_t i = 0; i < 2 * range; i += hex_width) {
        std::stringstream line;

        line << std::setw(8) << std::setfill('0') << std::hex << (uintptr_t)(start + i) << ": ";

        for (size_t j = 0; j < hex_width && i + j < 2 * range; ++j) {
            if (i + j == range) {
                line << "^" << std::setw(2) << std::setfill('0') << std::hex << (int)buffer[i + j] << " ";
            } else {
                line << std::setw(2) << std::setfill('0') << std::hex << (int)buffer[i + j] << " ";
            }

            ascii[j] = (buffer[i + j] >= 32 && buffer[i + j] <= 126) ? buffer[i + j] : '.';
        }

        for (size_t j = i + hex_width; j < 2 * range && j < i + hex_width; ++j) {
            line << "   ";
        }

        line << "|";
        for (size_t j = 0; j < hex_width && i + j < 2 * range; ++j) {
            line << ascii[j];
        }
        line << "|";

        std::cout << line.str() << std::endl;
    }
}