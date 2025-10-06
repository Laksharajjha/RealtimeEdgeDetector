#include <jni.h>
#include <string>
#include <opencv2/core.hpp>
#include <opencv2/imgproc.hpp>

extern "C" JNIEXPORT void JNICALL
Java_com_example_realtimeedgedetector_NativeBridge_processFrame(
        JNIEnv* env,
        jclass clazz,
        jint width,
        jint height,
        jbyteArray yuvData,
        jintArray outputPixels) {

    // 1. Get the YUV data from Java
    jbyte* yuv = env->GetByteArrayElements(yuvData, 0);

    // 2. Convert the YUV data to an OpenCV Mat
    // The camera preview format is NV21, which has a Y (grayscale) plane followed by a V/U plane
    cv::Mat yuvMat(height + height / 2, width, CV_8UC1, (unsigned char *)yuv);
    cv::Mat grayMat(height, width, CV_8UC1, (unsigned char *)yuv); // The Y plane is the grayscale image

    // 3. Process the frame with OpenCV
    cv::Mat edgesMat;
    // Apply Canny edge detection
    cv::Canny(grayMat, edgesMat, 50, 150);

    // 4. Convert the black-and-white edge image to a color RGBA Mat
    // so it can be displayed in our Android view.
    cv::Mat rgbaMat;
    cv::cvtColor(edgesMat, rgbaMat, cv::COLOR_GRAY2RGBA);

    // 5. Copy the processed pixel data back to the Java output array
    jint* output = env->GetIntArrayElements(outputPixels, 0);
    memcpy(output, rgbaMat.data, rgbaMat.total() * rgbaMat.elemSize());
    env->ReleaseIntArrayElements(outputPixels, output, 0);

    // Clean up
    env->ReleaseByteArrayElements(yuvData, yuv, 0);
}
