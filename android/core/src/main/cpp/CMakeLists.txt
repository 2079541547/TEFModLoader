cmake_minimum_required(VERSION 3.22.1)

project("TEFLoader")

add_library(TEFLoader SHARED
        future.cpp)

target_link_libraries(TEFLoader
        android
        log)