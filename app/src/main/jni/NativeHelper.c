#include "NativeHelper.h"

JNIEXPORT jdouble JNICALL Java_fruitbasket_com_bodyfit_helper_NativeHelper_filter
        (JNIEnv *env, jclass class, jdoubleArray inputSignal){
    return 0;
}

JNIEXPORT jboolean JNICALL Java_fruitbasket_com_bodyfit_helper_NativeHelper_isbelongSegments
        (JNIEnv *env, jclass class, jdoubleArray filteredSignal){
    return 0;
}

JNIEXPORT jint JNICALL Java_fruitbasket_com_bodyfit_helper_NativeHelper_dataSelect
        (JNIEnv *env, jclass class, jobjectArray input){
    return 0;
}

JNIEXPORT jint JNICALL Java_fruitbasket_com_bodyfit_helper_NativeHelper_activityRecognition
        (JNIEnv *env, jclass class, jdoubleArray signalSegments){
    return 0;
}

JNIEXPORT jint JNICALL Java_fruitbasket_com_bodyfit_helper_NativeHelper_abnormalDetection
        (JNIEnv *env, jclass class, jdoubleArray signalSegments){
    return 0;
}

JNIEXPORT jint JNICALL Java_fruitbasket_com_bodyfit_helper_NativeHelper_zoomSegment
        (JNIEnv *env, jclass class, jdoubleArray signalSegments){
    return 0;
}

JNIEXPORT jdouble JNICALL Java_fruitbasket_com_bodyfit_helper_NativeHelper_timeBalan
        (JNIEnv *env, jclass class, jdoubleArray signalSegments){
    return 0;
}

JNIEXPORT jdouble JNICALL Java_fruitbasket_com_bodyfit_helper_NativeHelper_amplitudeBalan
        (JNIEnv *env, jclass class, jdoubleArray signalSegments){
    return 0;
}

JNIEXPORT jdouble JNICALL Java_fruitbasket_com_bodyfit_helper_NativeHelper_repetitionScore
        (JNIEnv *env, jclass class, jdoubleArray signalSegment){
    return 0;
}

JNIEXPORT jdouble JNICALL Java_fruitbasket_com_bodyfit_helper_NativeHelper_setScore
        (JNIEnv *env, jclass class, jdoubleArray repetitionScore){
    return 0;
}