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
     * detects the change points of a action
     * @param filteredSignal
     * @return
     */
    public static native boolean isbelongSegments(double filteredSignal[]);

    /**
     *
     * @param input
     * @return
     */
    public static native  int dataSelect(double input[][]);

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
     *
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
     *calculate the  score of one repetition
     * @param signalSegment
     * @return
     */
    public static native double repetitionScore(double signalSegment[]);

    /**
     * calculate the score of a set of exercise
     * @param repetitionScore
     * @return
     */
    public static native double setScore(double repetitionScore[]);
}