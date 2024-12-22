cmake_minimum_required(VERSION 3.22.1)

project("EFMod")

add_subdirectory(SilkCasket)
add_library(${CMAKE_PROJECT_NAME} SHARED
        EFModLoader.cpp
)

target_link_libraries(${CMAKE_PROJECT_NAME}
    android silkcasket log)
