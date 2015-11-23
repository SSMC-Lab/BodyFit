package fruitbasket.com.bodyfit.helper;

public class NativeHelper {
    private static NativeHelper nativeHelper=new NativeHelper();

    private NativeHelper(){}

    public static NativeHelper getInstance(){
        return nativeHelper;
    }

    /**
     *
     * @param inputSignal
     * @return
     */
    public static native double filter(double inputSignal[]);

    /**
     *
     * @param filteredSignal
     * @return
     */
    public static native boolean isbelongSegments(double filteredSignal[]);

    /**
     *
     * @return
     */
    public static native  int dataSelect();

    /**
     *
     * @param signalSegments
     * @return
     */
    public static native int activityRecognition(double signalSegments[]);

    /**
     *
     * @param signalSegments
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
     *
     * @param signalSegment
     * @return
     */
    public static native double repetitionScore(double signalSegment[]);

    /**
     *
     * @param repetitionScore
     * @return
     */
    public static native double setScore(int repetitionScore[]);
}