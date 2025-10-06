package com.example.realtimeedgedetector;

public class NativeBridge {

    // Load the C++ library that we defined in CMakeLists.txt
    static {
        System.loadLibrary("realtimedgedetector");
    }

    /**
     * This is the native function that will receive the camera frame,
     * process it with OpenCV, and return the result as an array of pixels.
     * @param width the width of the camera frame
     * @param height the height of the camera frame
     * @param yuvData the raw YUV byte data from the camera
     * @param outputPixels the integer array to store the output RGBA pixels
     */
    public static native void processFrame(int width, int height, byte[] yuvData, int[] outputPixels);
}
