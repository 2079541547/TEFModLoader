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

#include "efmodloader/multi_channel.hpp"
#include "efmodloader/logger.hpp"

void* EFModLoader::LoaderMultiChannel::get(const std::string &id) {
    LOG_DEBUG("Attempting to get data with key: ", id);

    std::lock_guard lock(map_mutex_);
    auto it = data_map_.find(id);
    if (it == data_map_.end()) {
        LOG_DEBUG("Data not found for key: ", id);
        return nullptr;
    }

    void* data = it->second;
    LOG_TRACE("Retrieved data for key: ", id, ", data ptr: ", data);
    return data;
}

void EFModLoader::LoaderMultiChannel::send(const std::string &id, void *data) {
    LOG_DEBUG("Storing data with key: ", id, ", data ptr: ", data);

    std::lock_guard lock(map_mutex_);
    data_map_[id] = data;

    LOG_TRACE("Data stored for key: ", id, ", data ptr: ", data);
}

bool EFModLoader::LoaderMultiChannel::contains(const std::string& id) const {
    LOG_TRACE("Checking if key exists: ", id);

    std::lock_guard lock(map_mutex_);
    bool exists = data_map_.count(id) > 0;

    LOG_TRACE("Key ", id, " exists: ", exists);
    return exists;
}

bool EFModLoader::LoaderMultiChannel::remove(const std::string& id) {
    LOG_DEBUG("Attempting to remove data with key: ", id);

    std::lock_guard lock(map_mutex_);
    size_t erased = data_map_.erase(id);
    bool success = erased > 0;

    LOG_TRACE("Data ", (success ? "removed" : "not found"), " for key: ", id);
    return success;
}

size_t EFModLoader::LoaderMultiChannel::size() const {
    LOG_TRACE("Getting current data count");

    std::lock_guard lock(map_mutex_);
    size_t count = data_map_.size();

    LOG_TRACE("Current data count: ", count);
    return count;
}

void EFModLoader::LoaderMultiChannel::clear() {
    LOG_INFO("Clearing all data");

    std::lock_guard lock(map_mutex_);
    data_map_.clear();

    LOG_INFO("All data cleared");
}

EFModLoader::LoaderMultiChannel* EFModLoader::LoaderMultiChannel::GetInstance() {
    static LoaderMultiChannel instance;
    return &instance;
}