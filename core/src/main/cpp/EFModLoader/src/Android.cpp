/*******************************************************************************
 * 文件名称: Android
 * 项目名称: EFModLoader
 * 创建时间: 2024/11/1
 * 作者: EternalFuture゙
 * Github: https://github.com/2079541547
 * 版权声明: Copyright © 2024 EternalFuture゙. All rights reserved.
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
 * 描述信息: 本文件为EFModLoader项目中的一部分。
 * 注意事项: 请严格遵守GNU AGPL v3.0协议使用本代码，任何未经授权的商业用途均属侵权行为。
 *******************************************************************************/


#include <iostream>
#include <EFModLoader/log.hpp>
#include <EFModLoader/Android.hpp>
#include <EFModLoader/getData.hpp>
#include <EFModLoader/EFMod/EFMod.hpp>
#include <EFModLoader/api/RegisterApi.hpp>
#include <EFModLoader/api/Redirect.hpp>
#include <EFModLoader/hook/unity/RegisterHook.hpp>
#include <EFModLoader/loader/LoadELFMods.hpp>
#include <filesystem>

namespace EFModLoader::Android {

    string* get_PackageName;
    string* get_cacheDir;

    void clearDirectory(const filesystem::path& dirPath) {
        if (filesystem::exists(dirPath)) {
            for (const auto& entry : filesystem::directory_iterator(dirPath)) {
                if (filesystem::is_regular_file(entry.status())) {
                    filesystem::remove(entry.path());
                }
            }
        } else {
            std::cerr << "Directory does not exist: " << dirPath << '\n';
        }
    }

    
    void copyFilesFromTo(const filesystem::path& sourceDir, const filesystem::path& destDir) {
        // 检查源目录是否存在
        if (filesystem::exists(sourceDir)) {
            // 确保目标目录存在，如果不存在则创建
            if (!filesystem::exists(destDir)) {
                filesystem::create_directories(destDir);
                EFLOG(LogLevel::INFO, "EFModLoader", "Android", "CopyFiles", "创建目标目录: " + destDir.string());
            }

            // 遍历源目录中的所有条目
            for (const auto& entry : filesystem::directory_iterator(sourceDir)) {
                const auto& sourcePath = entry.path();
                auto destPath = destDir / sourcePath.filename();

                // 如果文件是 .ogg 文件，则移除后缀
                if (sourcePath.extension() == ".ogg") {
                    destPath.replace_extension(""); // 移除 .ogg 后缀
                }

                // 在复制文件之后，设置目标文件的时间戳为源文件的时间戳
                if (!filesystem::is_directory(entry.status())) {
                    if (filesystem::exists(destPath)) {
                        // 比较时间戳和文件大小
                        auto sourceLastWriteTime = filesystem::last_write_time(sourcePath);
                        auto destLastWriteTime = filesystem::last_write_time(destPath);
                        auto sourceFileSize = filesystem::file_size(sourcePath);
                        auto destFileSize = filesystem::file_size(destPath);

                        if (sourceLastWriteTime != destLastWriteTime || sourceFileSize != destFileSize) {
                            // 文件不同，执行复制
                            try {
                                filesystem::copy(sourcePath, destPath, filesystem::copy_options::overwrite_existing);
                                // 设置目标文件的时间戳
                                filesystem::last_write_time(destPath, sourceLastWriteTime);
                                EFLOG(LogLevel::INFO, "EFModLoader", "Android", "CopyFiles", "复制文件: " + sourcePath.string() + " 到 " + destPath.string());
                            } catch (const filesystem::filesystem_error& e) {
                                EFLOG(LogLevel::ERROR, "EFModLoader", "Android", "CopyFiles", "复制文件失败: " + sourcePath.string() + " 错误: " + e.what());
                            }
                        } else {
                            EFLOG(LogLevel::INFO, "EFModLoader", "Android", "CopyFiles", "文件相同，跳过复制: " + sourcePath.string());
                        }
                    } else {
                        // 目标文件不存在，直接复制
                        try {
                            filesystem::copy(sourcePath, destPath, filesystem::copy_options::overwrite_existing);
                            // 设置目标文件的时间戳
                            filesystem::last_write_time(destPath, filesystem::last_write_time(sourcePath));
                            EFLOG(LogLevel::INFO, "EFModLoader", "Android", "CopyFiles", "复制文件: " + sourcePath.string() + " 到 " + destPath.string());
                        } catch (const filesystem::filesystem_error& e) {
                            EFLOG(LogLevel::ERROR, "EFModLoader", "Android", "CopyFiles", "复制文件失败: " + sourcePath.string() + " 错误: " + e.what());
                        }
                    }
                } else {
                    // 递归复制子目录
                    copyFilesFromTo(sourcePath, destPath);
                }
            }
        } else {
            EFLOG(LogLevel::ERROR, "EFModLoader", "Android", "CopyFiles", "源目录不存在: " + sourceDir.string());
        }
    }





    void Load(JNIEnv *env, const std::string& EFModLoader) {

        get_PackageName = new std::string(EFModLoader::getPackageNameAsString(env));
        get_cacheDir = new std::string("data/data/" + *get_PackageName + "/cache/");

        auto *get_ExternalDir = new std::string(
                "/sdcard/Android/data/" + *get_PackageName + "/files/EFMod-Private/");


        EFModLoader::RegisterApi::RegisterAPI("get_PackageName", (long) get_PackageName);
        EFModLoader::RegisterApi::RegisterAPI("get_ExternalDir", (long) get_ExternalDir);
        EFModLoader::RegisterApi::RegisterAPI("get_cacheDir", (long) get_cacheDir);

        try {
            if (filesystem::exists("/sdcard/Documents/EFModLoader/" + EFModLoader)) {
                if (filesystem::is_directory("/sdcard/Documents/EFModLoader/" + EFModLoader)) {

                    if (filesystem::exists("/sdcard/Documents/EFModLoader/" + EFModLoader + "/kernel/libLoader.so.ogg")) {

                        std::filesystem::create_directories(*get_cacheDir + "EFModLoader");

                        EFLOG(LogLevel::INFO, "EFModLoader", "Android", "Load", "尝试更新内核");
                        filesystem::copy_file("/sdcard/Documents/EFModLoader/" + EFModLoader + "/kernel/libLoader.so.ogg",
                                              *get_cacheDir + "EFModLoader/libLoader.so",
                                              filesystem::copy_options::overwrite_existing);
                        EFLOG(LogLevel::INFO, "EFModLoader", "Android", "Load", "内核更新完成");
                    }


                    clearDirectory(*get_cacheDir + "EFMod/");
                    copyFilesFromTo("/sdcard/Documents/EFModLoader/" + EFModLoader + "/EFMod/",
                                    *get_cacheDir + "EFMod/");

                    //复制私有目录
                    copyFilesFromTo("/sdcard/Documents/EFModLoader/" + EFModLoader + "/Private/",
                                    *get_ExternalDir);

                    copyFilesFromTo("/sdcard/Documents/EFModLoader/" + EFModLoader + "/EFModX/",
                                    *get_cacheDir + "EFModX");

                } else {
                    EFLOG(LogLevel::ERROR, "EFModLoader", "Android", "Load",
                          "加载的目录不是文件夹！！！");
                }
            }
        } catch (const filesystem::filesystem_error& e) {
            EFLOG(LogLevel::ERROR, "EFModLoader", "Android", "Load", "文件系统错误: " + std::string(e.what()));
        } catch (const std::exception& e) {
            EFLOG(LogLevel::ERROR, "EFModLoader", "Android", "Load", "标准异常: " + std::string(e.what()));
        } catch (...) {
            EFLOG(LogLevel::ERROR, "EFModLoader", "Android", "Load", "未知异常");
        }
    }
}
