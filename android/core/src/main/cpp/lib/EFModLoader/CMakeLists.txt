cmake_minimum_required(VERSION 3.22.1)
project(EFModLoader)

set(CMAKE_CXX_STANDARD 17)

set(TEST OFF)

add_library(EFModLoader
        STATIC
        src/loader.cpp
        src/utility.cpp

)

target_include_directories(EFModLoader
        PUBLIC
        includes
)

if (${TEST})
    add_library(test_mod SHARED
            test/test_mod.cpp
    )

    target_link_libraries(test_mod stdc++)

    add_executable(Test
            test/test.cpp
    )

    target_link_libraries(Test
            PUBLIC
            dl
            EFModLoader
    )
endif ()