//
// Created by eternalfuture on 2024/9/28.
//

#pragma once

#include <iostream>
#include <android/log.h>

namespace EFModLoader {

    using namespace std;

    namespace Log {
        // 辅助函数用于构建位置信息字符串
        std::string buildPositionString(const std::string &part1, const std::string &part2);

        // 内部使用的辅助函数来减少重复代码
        void logInternal(const std::string &type, const std::string &position, const std::string &message);

        // 日志记录函数
        void LOG(const std::string &type, const std::string &message);
        void LOG(const std::string &type, const std::string &function, const std::string &message);
        void LOG(const std::string &type, const std::string &Class, const std::string &function, const std::string &message);
        void LOG(const std::string &type, const std::string &Namespace, const std::string &Class, const std::string &function, const std::string &message);
    }

}