cmake_minimum_required(VERSION 3.10)
project(ZXingCppAndroid)

# The zxing-cpp requires at least C++17 to build
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

set(BUILD_READERS ON)
set(BUILD_WRITERS ON)

add_subdirectory(${CMAKE_CURRENT_SOURCE_DIR}/../../../../core ZXing EXCLUDE_FROM_ALL)

find_library(log-lib log)

add_library(zxingcpp_android SHARED ZxingCpp.cpp)

target_link_libraries(zxingcpp_android PRIVATE ZXing::ZXing jnigraphics ${log-lib})