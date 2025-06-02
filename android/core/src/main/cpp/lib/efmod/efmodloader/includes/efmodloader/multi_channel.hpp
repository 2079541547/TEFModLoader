/*******************************************************************************
 * 文件名称: multi_channel
 * 项目名称: EFMod
 * 创建时间: 25-5-10
 * 作者: EternalFuture゙
 * Gitlab: https://gitlab.com/2079541547/
 * 协议: MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *******************************************************************************/

#pragma once

#include <multi_channel.hpp>

#include <mutex>
#include <queue>
#include <unordered_map>

namespace EFModLoader {
    class LoaderMultiChannel final : public MultiChannel {
        std::unordered_map<std::string, void*> data_map_;
        mutable std::mutex map_mutex_;

        void* get(const std::string &id) override;

    public:
        void send(const std::string &id, void *data) override;

        bool contains(const std::string& id) const;
        bool remove(const std::string& id);
        size_t size() const;
        void clear();

        static LoaderMultiChannel* GetInstance();
    };
}
