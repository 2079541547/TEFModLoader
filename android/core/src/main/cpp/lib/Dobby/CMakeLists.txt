cmake_minimum_required(VERSION 3.22.1)

project("Dobby")

set(DOBBY_LIB "${CMAKE_SOURCE_DIR}/lib/Dobby/lib/${CMAKE_ANDROID_ARCH_ABI}/libdobby.a")

add_library(Dobby STATIC
        dobby.cpp)

target_link_libraries(Dobby
        ${DOBBY_LIB}
)

target_include_directories(
        Dobby
        PUBLIC
        include
)