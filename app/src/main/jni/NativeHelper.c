#include "header.h"

/*
 * Class:     NativeHelper
 * Method:    filter
 * Signature: ([D)[D
 */
JNIEXPORT jdoubleArray JNICALL Java_fruitbasket_com_bodyfit_helper_NativeHelper_filter
        (JNIEnv *env, jclass class, jdoubleArray inputSignal){
    return 0;
}

/*
 * Class:     NativeHelper
 * Method:    isbelongSegments
 * Signature: ([D)Z
 */
JNIEXPORT jboolean JNICALL Java_fruitbasket_com_bodyfit_helper_NativeHelper_isbelongSegments
        (JNIEnv *env, jclass class, jdoubleArray filteredSignal){
    return 0;
}

/*
 * Class:     NativeHelper
 * Method:    dataSelect
 * Signature: ([[D)[I
 */
JNIEXPORT jintArray JNICALL Java_fruitbasket_com_bodyfit_helper_NativeHelper_dataSelect
        (JNIEnv *env, jclass class, jobjectArray input){
    return 0;
}

/*
 * Class:     NativeHelper
 * Method:    activityRecognition
 * Signature: ([[D)I
 */
JNIEXPORT jint JNICALL Java_fruitbasket_com_bodyfit_helper_NativeHelper_activityRecognition
        (JNIEnv *env, jclass class, jobjectArray signalSegments){
    return 0;
}

/*
 * Class:     NativeHelper
 * Method:    abnormalDetection
 * Signature: ([D)I
 */
JNIEXPORT jint JNICALL Java_fruitbasket_com_bodyfit_helper_NativeHelper_abnormalDetection
        (JNIEnv *env, jclass class, jdoubleArray signalSegments){
    return 0;
}

/*
 * Class:     NativeHelper
 * Method:    zoomSegment
 * Signature: ([D)I
 */
JNIEXPORT jint JNICALL Java_fruitbasket_com_bodyfit_helper_NativeHelper_zoomSegment
        (JNIEnv *env, jclass class, jdoubleArray signalSegments){
    return 0;
}

/*
 * Class:     NativeHelper
 * Method:    timeBalan
 * Signature: ([D)D
 */
JNIEXPORT jdouble JNICALL Java_fruitbasket_com_bodyfit_helper_NativeHelper_timeBalan
        (JNIEnv *env, jclass class, jdoubleArray signalSegments){
    return 0;
}

/*
 * Class:     NativeHelper
 * Method:    amplitudeBalan
 * Signature: ([D)D
 */
JNIEXPORT jdouble JNICALL Java_fruitbasket_com_bodyfit_helper_NativeHelper_amplitudeBalan
        (JNIEnv *env, jclass class, jdoubleArray signalSegments){
    return 0;
}

/*
 * Class:     NativeHelper
 * Method:    repetitionScore
 * Signature: ([D)D
 */
JNIEXPORT jdouble JNICALL Java_fruitbasket_com_bodyfit_helper_NativeHelper_repetitionScore
        (JNIEnv *env, jclass class, jdoubleArray signalSegment){
    return 0;
}

/*
 * Class:     NativeHelper
 * Method:    setScore
 * Signature: ([D)D
 */
JNIEXPORT jdouble JNICALL Java_fruitbasket_com_bodyfit_helper_NativeHelper_setScore
        (JNIEnv *env, jclass class, jdoubleArray repetitionScore){
    return 0;
}