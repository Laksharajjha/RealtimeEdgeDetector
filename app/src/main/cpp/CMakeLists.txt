# Sets the minimum version of CMake required.
cmake_minimum_required(VERSION 3.18.1)

# --- STEP 1: DEFINE THE PATH TO YOUR OPENCV SDK ---
# Path updated to remove the space in "Androidsdk".
set(OpenCV_DIR "/Users/laksharajjha/Documents/Androidsdk/OpenCV-android-sdk/sdk/native/jni")


# --- STEP 2: FIND THE OPENCV PACKAGE ---
# This command looks for the OpenCV configuration files in the directory specified above.
find_package(OpenCV REQUIRED)


# Defines our native library, named "realtimedgedetector".
# It is a SHARED library and is built from the source file "native-lib.cpp".
add_library(realtimedgedetector SHARED native-lib.cpp)


# Specifies a path to the directory containing the JNI headers.
# This is needed to find files like <jni.h>.
find_library(log-lib log)


# --- STEP 3: LINK OPENCV TO YOUR LIBRARY ---
# This tells CMake to link our "realtimedgedetector" library against the OpenCV libraries
# and the Android logging library. This is the crucial connection step.
target_link_libraries(
        realtimedgedetector
        ${OpenCV_LIBS}
        ${log-lib}
)
