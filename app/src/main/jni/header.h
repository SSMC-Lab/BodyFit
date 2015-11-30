/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class NativeHelper */

#ifndef _Included_NativeHelper
#define _Included_NativeHelper
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     NativeHelper
 * Method:    filter
 * Signature: ([D)[D
 */
JNIEXPORT jdoubleArray JNICALL Java_fruitbasket_com_bodyfit_helper_NativeHelper_filter
        (JNIEnv *, jclass, jdoubleArray);

/*
 * Class:     NativeHelper
 * Method:    isbelongSegments
 * Signature: ([D)Z
 */
JNIEXPORT jboolean JNICALL Java_fruitbasket_com_bodyfit_helper_NativeHelper_isbelongSegments
        (JNIEnv *, jclass, jdoubleArray);

/*
 * Class:     NativeHelper
 * Method:    dataSelect
 * Signature: ([[D)[I
 */
JNIEXPORT jintArray JNICALL Java_fruitbasket_com_bodyfit_helper_NativeHelper_dataSelect
        (JNIEnv *, jclass, jobjectArray);

/*
 * Class:     NativeHelper
 * Method:    activityRecognition
 * Signature: ([[D)I
 */
JNIEXPORT jint JNICALL Java_fruitbasket_com_bodyfit_helper_NativeHelper_activityRecognition
        (JNIEnv *, jclass, jobjectArray, jdoubleArray);

/*
 * Class:     NativeHelper
 * Method:    abnormalDetection
 * Signature: ([D)I
 */
JNIEXPORT jint JNICALL Java_fruitbasket_com_bodyfit_helper_NativeHelper_abnormalDetection
        (JNIEnv *, jclass, jdoubleArray, jdoubleArray);

/*
 * Class:     NativeHelper
 * Method:    zoomSegment
 * Signature: ([D)I
 */
JNIEXPORT jint JNICALL Java_fruitbasket_com_bodyfit_helper_NativeHelper_zoomSegment
        (JNIEnv *, jclass, jdoubleArray, jdoubleArray);

/*
 * Class:     NativeHelper
 * Method:    timeBalan
 * Signature: ([D)D
 */
JNIEXPORT jdouble JNICALL Java_fruitbasket_com_bodyfit_helper_NativeHelper_timeBalan
        (JNIEnv *, jclass, jdoubleArray, jdoubleArray);

/*
 * Class:     NativeHelper
 * Method:    amplitudeBalan
 * Signature: ([D)D
 */
JNIEXPORT jdouble JNICALL Java_fruitbasket_com_bodyfit_helper_NativeHelper_amplitudeBalan
        (JNIEnv *, jclass, jdoubleArray, jdoubleArray);

/*
 * Class:     NativeHelper
 * Method:    repetitionScore
 * Signature: ([D)D
 */
JNIEXPORT jdouble JNICALL Java_fruitbasket_com_bodyfit_helper_NativeHelper_repetitionScore
        (JNIEnv *, jclass, jdoubleArray, jdoubleArray);

/*
 * Class:     NativeHelper
 * Method:    setScore
 * Signature: ([D)D
 */
JNIEXPORT jdouble JNICALL Java_fruitbasket_com_bodyfit_helper_NativeHelper_setScore
        (JNIEnv *, jclass, jdoubleArray);

#ifdef __cplusplus
}
#endif
#endif
