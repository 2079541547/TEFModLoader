/*******************************************************************************
 * 文件名称: auxiliary
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


#include <BNM/UserSettings/GlobalSettings.hpp>
#include <BNM/UnityStructures.hpp>
#include <random>
#include "../lib/EFModLoader/includes/EFModLoader/EFMod/EFMod.hpp"

template <typename R, typename... Args>
R callFunction(void *funcPtr, Args &&...args) {
    if (!funcPtr) {
        throw std::invalid_argument("函数指针不能为NULL");
    }
    using FuncPtr = R (*)(Args...);
    auto f = reinterpret_cast<FuncPtr>(funcPtr);
    return f(std::forward<Args>(args)...);
}

std::vector<void*> func;

template<typename T>
T (*old_fun)(BNM::UnityEngine::Object *, ...);

template<typename T>
T hooked(BNM::UnityEngine::Object * i, ...) {
    std::random_device rd;
    std::mt19937 gen(rd());
    std::uniform_int_distribution<size_t> dis(0, func.size() - 1);
    size_t randomIndex = dis(gen);

    va_list args, args_copy;
    va_start(args, i);
    va_copy(args_copy, args);

    return callFunction<T>(func.at(randomIndex), i, args_copy);

    va_end(args_copy);
    va_end(args);
}

void hooked_void(BNM::UnityEngine::Object * i, ...) {
    va_list args, args_copy;
    va_start(args, i);
    va_copy(args_copy, args);

    old_fun<void>(i, args_copy);
    for (auto _: func) {
        callFunction<void>(_, i, args_copy);
    }

    va_end(args_copy);
    va_end(args);
}

enum Type {
    LONG = 0,
    INT = 1,
    VOID = 2,
    BOOL = 3,
};

enum Mode {
    INLINE = 0,
    VIRTUAL = 1,
    INVOKE = 2
};


#define APPLY_HOOK(mode, c, m, n, o) \
    do { \
        switch (static_cast<Mode>(mode)) { \
            case Mode::INLINE: \
                HOOK(m, n, o); \
                break; \
            case Mode::INVOKE: \
                BNM::InvokeHook(m, n, o); \
                break; \
            case Mode::VIRTUAL: \
                BNM::VirtualHook(c, m, n, o); \
                break; \
            default: \
                HOOK(m, n, o); \
                break; \
        } \
    } while(0)


extern "C" void createHook(int mode, int t, std::vector<void*> f, BNM::Class c, BNM::MethodBase m, size_t i, EFModAPI* e) {
    func = std::move(f);

    switch (t) {
        case Type::LONG :
            APPLY_HOOK(mode, c, m, hooked<long>, old_fun<long>);
            e->registerAPI(i, (void*)old_fun<long>);
            break;
        case Type::INT:
            APPLY_HOOK(mode, c, m, hooked<int>, old_fun<int>);
            e->registerAPI(i, (void*)old_fun<int>);
            break;
        case Type::VOID:
            APPLY_HOOK(mode, c, m, hooked_void, old_fun<void>);
            e->registerAPI(i, (void*)old_fun<void>);
            break;
        case Type::BOOL:
            APPLY_HOOK(mode, c, m, hooked<bool>, old_fun<bool>);
            e->registerAPI(i, (void*)old_fun<bool>);
            break;
        default:
            break;
    }
}