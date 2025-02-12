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

#pragma once

#include <streambuf>
#include <string>

namespace TEFModLoader::Log {

    class AndroidLogStreamBuffer : public std::streambuf {
    public:
        AndroidLogStreamBuffer(int logLevel) : logLevel(logLevel), buffer("") {}
        ~AndroidLogStreamBuffer() override;

    protected:
        int_type overflow(int_type v) override;
        std::streamsize xsputn(const char *s, std::streamsize n) override;

    private:
        void flushBuffer();
        std::string buffer;
        int logLevel;
    };

    void redirectStdStreams();

}