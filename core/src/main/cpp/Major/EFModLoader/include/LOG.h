//
// Created by eternalfuture on 2024/9/17.
//

#ifndef EFMODLOADER_LOG_H
#define EFMODLOADER_LOG_H

#include <android/log.h>
#include <string>

namespace MainLOGS
{

#define LOG_TAG "EFModLoader"

    // 辅助函数用于构建位置信息字符串
    std::string buildPositionString(const std::string &part1, const std::string &part2)
    {
        if (part2.empty())
            return part1;
        return part1 + "::" + part2;
    }

    // 内部使用的辅助函数来减少重复代码
    void logInternal(const std::string &type, const std::string &position, const std::string &message)
    {
        if (message.empty())
            return;
        std::string messageStr = message + "\n";
        __android_log_print(ANDROID_LOG_INFO, LOG_TAG, "[%s] %s %s", type.c_str(), position.c_str(), messageStr.c_str());
    }

    // 日志记录函数
    void LOG(const std::string &type, const std::string &message)
    {
        if (type.empty() || message.empty())
            return;
        logInternal(type, "", message);
    }

    void LOG(const std::string &type, const std::string &function, const std::string &message)
    {
        if (type.empty() || function.empty() || message.empty())
            return;
        std::string position = "[" + function + "]";
        logInternal(type, position, message);
    }

    void LOG(const std::string &type, const std::string &Class, const std::string &function, const std::string &message)
    {
        if (type.empty() || Class.empty() || function.empty() || message.empty())
            return;
        std::string position = "[" + buildPositionString(Class, function) + "]";
        logInternal(type, position, message);
    }

    void LOG(const std::string &type, const std::string &Namespace, const std::string &Class, const std::string &function, const std::string &message)
    {
        if (type.empty() || Namespace.empty() || Class.empty() || function.empty() || message.empty())
            return;
        std::string position = "[" + buildPositionString(buildPositionString(Namespace, Class), function) + "]";
        logInternal(type, position, message);
    }

}

#endif // EFMODLOADER_LOG_H
