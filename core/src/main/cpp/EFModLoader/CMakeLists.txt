cmake_minimum_required(VERSION 3.22.1)

project("EFModLoader")

set(CMAKE_CXX_STANDARD 20)

set(INCLUDE_DIRS
        includes
        includes/EFModLoader
)

add_library(EFModLoader STATIC
        src/Load/Loader.cpp
        src/log.cpp
        src/Manager/API.cpp
        src/Manager/Extend.cpp
        src/EFMod/ModApi.cpp
)

target_include_directories(
        EFModLoader
        PUBLIC
        ${INCLUDE_DIRS}
)

target_link_libraries(EFModLoader)
