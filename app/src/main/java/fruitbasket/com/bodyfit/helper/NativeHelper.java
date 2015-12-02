package fruitbasket.com.bodyfit.helper;

/**
 * this method is out of date , you must change it before use.
 */
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
    public static native double[] filter(double[] inputSignal,int length);

    /**
     * Is the the person moving?
     * @param filteredSignal
     * @return true: activity;false:static
     */
    private static native boolean isbelongSegments(double[] filteredSignal,int length);

    /**
     * select two vector of the inputSignal
     * @param input
     * @return
     */
    public static native  int[] dataSelect(double[][] input,int length_2d,int length_1d);

    /**
     * identify the type of activity
     * @param signalSegmentX
     * @param signalSegmentY
     * @return
     */
    public static native int activityRecognition(double[] signalSegmentX,double[] signalSegmentY,int length);

    /**
     * check whether it is a abnormal behavior in the activity
     * @param signalSegmentX
     * @param signalSegmentY
     * @return
     */
    public static native int abnormalDetection(double[] signalSegmentX,double[] signalSegmentY,int length);

    /**
     *
     * @param signalSegmentX
     * @param signalSegmentY
     * @return
     */
    public static native int zoomSegment(double[] signalSegmentX,double[] signalSegmentY,int length);

    /**
     * signalSegments
     * @param signalSegmentX
     * @param signalSegmentY
     * @return
     */
    public static native double timeBalan(double[] signalSegmentX,double[] signalSegmentY,int length);

    /**
     *
     * @param signalSegmentX
     * @param signalSegmentY
     * @return
     */
    public static native double amplitudeBalan(double[] signalSegmentX,double[] signalSegmentY,int length);

    /**
     * calculate the score of one repetition
     * @param signalSegmentX
     * @param signalSegmentY
     * @return
     */
    public static native double repetitionScore(double[] signalSegmentX,double[] signalSegmentY,int length);

    /**
     * calculate the score of a set of exercise
     * @param repetitionScore a list of scores
     * @return
     */
    public static native double setScore(double repetitionScore[],int length);

    /**
     * Is the the person moving?
     * @param filteredSignal
     * @return
     */
    public static boolean isbelongSegments(double[][] filteredSignal){
        final int dimension=6;
        if(filteredSignal.length!=dimension){
            return false;
        }
        else{
            int trueCounter=0,falseCounter=0;
            for(int i=0;i<dimension;++i){
                if(isbelongSegments(filteredSignal[i],filteredSignal[i].length)==true){
                    ++trueCounter;
                }
                else{
                    ++falseCounter;
                }
            }
            if(trueCounter>=falseCounter){
                return true;
            }
            else{
                return false;
            }
        }
    }
}