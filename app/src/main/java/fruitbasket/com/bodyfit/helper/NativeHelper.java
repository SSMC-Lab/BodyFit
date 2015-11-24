package fruitbasket.com.bodyfit.helper;

public class NativeHelper {
    private static NativeHelper nativeHelper=new NativeHelper();

    private NativeHelper(){}

    public static NativeHelper getInstance(){
        return nativeHelper;
    }

    /**
     * filte signal
     * @param inputSignal
     * @return
     */
    public static native double filter(double inputSignal[]);

    /**
     *?
     * @param filteredSignal
     * @return
     */
    public static native boolean isbelongSegments(double filteredSignal[]);

    /**
     *？
     * @return
     */
    public static native  int dataSelect();

    /**
     * identify the type of activity
     * @param signalSegments
     * @return type of activity
     */
    public static native int activityRecognition(double signalSegments[]);

    /**
     * check whether it is a abnormal behavior in the activity
     * @param signalSegments type of abnormal behavior
     * @return
     */
    public static native int abnormalDetection(double signalSegments[]);

    /**
     *？
     * @param signalSegments
     * @return
     */
    public static native int zoomSegment(double signalSegments[]);

    /**
     *
     * @param signalSegments
     * @return
     */
    public static native double timeBalan(double signalSegments[]);

    /**
     *
     * @param signalSegments
     * @return
     */
    public static native double amplitudeBalan(double signalSegments[]);

    /**
     *calculate the overall score of a repetition
     * @param signalSegment
     * @return
     */
    public static native double repetitionScore(double signalSegment[]);

    /**
     * calculate the overall score each set of exercise
     * @param repetitionScore
     * @return
     */
    public static native double setScore(double repetitionScore[]);
}