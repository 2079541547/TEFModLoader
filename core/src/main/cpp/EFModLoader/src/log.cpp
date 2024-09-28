//
// Created by eternalfuture on 2024/9/28.
//

#include <log.hpp>


std::string
EFModLoader::Log::buildPositionString(const std::string &part1, const std::string &part2) {
    if (part2.empty())
        return part1;
    return part1 + "::" + part2;
}

void EFModLoader::Log::logInternal(const std::string &type, const std::string &position,
                                   const std::string &message) {
    if (message.empty())
        return;
    std::string messageStr = message + "\n";
    __android_log_print(ANDROID_LOG_INFO, "EFModLoader", "[%s] %s %s", type.c_str(), position.c_str(), messageStr.c_str());
}

void EFModLoader::Log::LOG(const std::string &type, const std::string &message) {
    if (type.empty() || message.empty())
        return;
    logInternal(type, "", message);
}

void EFModLoader::Log::LOG(const std::string &type, const std::string &function,
                           const std::string &message) {
    if (type.empty() || function.empty() || message.empty())
        return;
    std::string position = "[" + function + "]";
    logInternal(type, position, message);
}

void EFModLoader::Log::LOG(const std::string &type, const std::string &Class,
                           const std::string &function, const std::string &message) {
    if (type.empty() || Class.empty() || function.empty() || message.empty())
        return;
    std::string position = "[" + buildPositionString(Class, function) + "]";
    logInternal(type, position, message);
}

void EFModLoader::Log::LOG(const std::string &type, const std::string &Namespace,
                           const std::string &Class, const std::string &function,
                           const std::string &message) {
    if (type.empty() || Namespace.empty() || Class.empty() || function.empty() || message.empty())
        return;
    std::string position = "[" + buildPositionString(buildPositionString(Namespace, Class), function) + "]";
    logInternal(type, position, message);
}