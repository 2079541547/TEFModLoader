cmake_minimum_required(VERSION 3.22.1)
project(efmod)

set(CMAKE_CXX_STANDARD 17)

set(EFMODLAODER_ENABLE_LOGGING ON)
set(TEST OFF)

set(EFMOD_API_INCLUDES ${CMAKE_CURRENT_SOURCE_DIR}/efmod-api)
set(EFMODLOADER_INCLUDES ${CMAKE_CURRENT_SOURCE_DIR}/efmodloader/includes)
set(EFMODLOADER_SRC
        efmodloader/src/logger.cpp
        efmodloader/src/multi_channel.cpp
        efmodloader/src/loader.cpp)

add_library(efmodloader STATIC ${EFMODLOADER_SRC}
        efmodloader/test/test_efmod.cpp)

target_include_directories(efmodloader
        PUBLIC
        $<BUILD_INTERFACE:${EFMODLOADER_INCLUDES}>
        $<BUILD_INTERFACE:${EFMOD_API_INCLUDES}>
        PRIVATE
        ${CMAKE_CURRENT_SOURCE_DIR}
)


option(EFMODLAODER_ENABLE_LOGGING "Enable EFModLoader logging system" ON)

if(EFMODLAODER_ENABLE_LOGGING)
    add_definitions(-DEFMODLAODER_ENABLE_LOGGING)
    message(STATUS "Logging EFModLoader system enabled")
else()
    message(STATUS "Logging EFModLoader system disabled")
endif()

if (TEST)

    add_executable(test_loader
            efmodloader/test/test_loader.cpp
    )

    add_library(test_efmod
            SHARED
            efmodloader/test/test_efmod.cpp
    )

    target_link_libraries(test_loader
            PUBLIC
            efmodloader
            dl
    )

    target_include_directories(test_efmod
            PUBLIC
            $<BUILD_INTERFACE:${EFMOD_API_INCLUDES}>
    )

endif ()