/*******************************************************************************
 * 文件名称: debug_tool
 * 项目名称: TEFModLoader
 * 创建时间: 2025/5/17
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

#include "tefmod-api/debug_tool.hpp"

#include <unistd.h>
#include <sys/system_properties.h>
#include <chrono>
#include <iomanip>
#include <sstream>
#include <vector>
#include <android/log.h>
#include <sys/mman.h>
#include <cstring>
#include <linux/resource.h>
#include <sys/resource.h>

void TEFModLoader::DebugTool::printMemoryHexView(TEFMod::Logger* logger, const void* ptr, size_t range, size_t hex_width) {
    if (!logger || !ptr || range == 0) {
        logger->w("🛑 Invalid parameters: logger=%p, ptr=%p, range=%zu", logger, ptr, range);
        return;
    }

    const auto* data = reinterpret_cast<const uint8_t*>(ptr);
    std::ostringstream oss;

    oss << "\n🔍 Memory Hex Viewer @" << ptr
        << "\n📏 Range: " << range << " bytes"
        << "\n⚠️ Warning: Direct memory access mode\n\n";

    // 🏷️ Table header
    oss << "📍 Address      ┆ 🔢 Hex Data";
    for (size_t i = 0; i < hex_width; ++i) {
        if (i % 8 == 0) oss << " ";
    }
    oss << "┆ 🔤 Printable\n";
    oss << "──────────────╂───────────────────────────────────────────────╂──────────────\n";

    for (size_t offset = 0; offset < range; offset += hex_width) {
        oss << "0x" << std::setw(8) << std::setfill('0') << std::hex
            << (reinterpret_cast<uintptr_t>(ptr) + offset) << " ┆ ";

        for (size_t i = 0; i < hex_width; ++i) {
            if (offset + i < range) {
                oss << (i == hex_width/2 ? "✨" : " ")
                    << std::setw(2) << std::setfill('0') << std::hex
                    << static_cast<int>(data[offset + i]) << " ";
                if (i % 8 == 7) oss << "│ ";
            }
        }

        oss << "┆ ";
        for (size_t i = 0; i < hex_width; ++i) {
            if (offset + i < range) {
                char c = data[offset + i];
                oss << (i == hex_width/2 ? "✨" : "")
                    << (isprint(c) ? c : '.')
                    << (i == hex_width/2 ? "✨" : "");
            }
        }
        oss << "\n";

        if ((offset / hex_width) % 2 == 1) {
            oss << "──────────────┼───────────────────────────────────────────────┼──────────────\n";
        }
    }

    oss << "\n📊 Statistics:"
        << "\n• Start address: 0x" << std::hex << reinterpret_cast<uintptr_t>(ptr)
        << "\n• End address: 0x" << std::hex << (reinterpret_cast<uintptr_t>(ptr) + range - 1)
        << "\n• Non-zero bytes: " << std::dec << std::count_if(data, data + range, [](uint8_t b){ return b != 0; })
        << "/" << range << "\n";

    logger->i(oss.str());
}

void TEFModLoader::DebugTool::printSystemInfo(TEFMod::Logger *logger) {
    if (!logger) return;

    std::ostringstream oss;

    // Android系统信息
    char os_version[PROP_VALUE_MAX];
    __system_property_get("ro.build.version.release", os_version);

    char device_model[PROP_VALUE_MAX];
    __system_property_get("ro.product.model", device_model);

    oss << "📱 Android System Info:\n";
    oss << "├─ OS Version: " << os_version << "\n";
    oss << "├─ Device Model: " << device_model << "\n";

    // CPU信息
    long cpu_cores = sysconf(_SC_NPROCESSORS_CONF);
    oss << "├─ CPU Cores: " << cpu_cores << "\n";

    // 内存信息
    long page_size = sysconf(_SC_PAGESIZE);
    long phys_pages = sysconf(_SC_PHYS_PAGES);
    oss << "├─ Page Size: " << page_size / 1024 << " KB\n";
    oss << "├─ Total RAM: " << (phys_pages * page_size) / (1024 * 1024) << " MB\n";

    // 进程内存信息
    struct rusage usage;
    getrusage(RUSAGE_SELF, &usage);
    oss << "└─ Process Memory: "
        << (usage.ru_maxrss * 1024) / (1024 * 1024) << " MB resident\n";

    logger->i(oss.str());
}

void TEFModLoader::DebugTool::printProfile(TEFMod::Logger *logger, std::function<void()> func) {
    if (!logger) return;

    auto start = std::chrono::steady_clock::now();
    func();
    auto end = std::chrono::steady_clock::now();
    auto duration = std::chrono::duration_cast<std::chrono::microseconds>(end - start);

    std::ostringstream oss;
    oss << "⏱️ Execution: " << duration.count() << " μs (";

    // 自动选择最佳时间单位
    if (duration.count() < 1000) {
        oss << duration.count() << " μs)";
    } else if (duration.count() < 1000000) {
        oss << std::fixed << std::setprecision(2)
            << (duration.count() / 1000.0) << " ms)";
    } else {
        oss << std::fixed << std::setprecision(2)
            << (duration.count() / 1000000.0) << " s)";
    }

    logger->i(oss.str());
}