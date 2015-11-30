package fruitbasket.com.bodyfit.helper;

public class NativeHelper {
    private static NativeHelper nativeHelper=new NativeHelper();

    private NativeHelper(){}

    public static NativeHelper getInstance(){
        return nativeHelper;
    }

    /**
     * @param inputSignal
     * @return
     */
    public static native double[] filter(double[] inputSignal);

    /**
     * Is the the person moving?
     * @param filteredSignal
     * @return true: activity;false:static
     */
    public static native boolean isbelongSegments(double[] filteredSignal);

    /**
     * select two vector of the inputSignal
     * @param input
     * @return
     */
    public static native  int[] dataSelect(double[][] input);

    /**
     * identify the type of activity
     * @param signalSegmentX
     * @param signalSegmentY
     * @return
     */
    public static native int activityRecognition(double[] signalSegmentX,double[] signalSegmentY);

    /**
     * check whether it is a abnormal behavior in the activity
     * @param signalSegmentX
     * @param signalSegmentY
     * @return
     */
    public static native int abnormalDetection(double[] signalSegmentX,double[] signalSegmentY);

    /**
     *
     * @param signalSegmentX
     * @param signalSegmentY
     * @return
     */
    public static native int zoomSegment(double[] signalSegmentX,double[] signalSegmentY);

    /**
     * signalSegments
     * @param signalSegmentX
     * @param signalSegmentY
     * @return
     */
    public static native double timeBalan(double[] signalSegmentX,double[] signalSegmentY);

    /**
     *
     * @param signalSegmentX
     * @param signalSegmentY
     * @return
     */
    public static native double amplitudeBalan(double[] signalSegmentX,double[] signalSegmentY);

    /**
     * calculate the score of one repetition
     * @param signalSegmentX
     * @param signalSegmentY
     * @return
     */
    public static native double repetitionScore(double[] signalSegmentX,double[] signalSegmentY);

    /**
     * calculate the score of a set of exercise
     * @param repetitionScore a list of scores
     * @return
     */
    public static native double setScore(double repetitionScore[]);
}