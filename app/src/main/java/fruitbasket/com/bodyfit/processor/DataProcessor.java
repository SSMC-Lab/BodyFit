package fruitbasket.com.bodyfit.processor;

public class DataProcessor{
    private static final DataProcessor dataProcessor=new DataProcessor();

    //exercise type
    public static final int INITIAL_EXERCISE_TYPE=-1;
    public static final int FLAT_BENCH_BRABELL_PASS=0;
    public static final int FLAT_BENCH_DUMBBELL_FLYE=1;
    public static final int FLAT_BENCH_DUMBBELL_PRESS=2;
    public static final int INCLINE_DUMBBELL_FLYE=3;
    public static final int REVERSE_GRIP_PULLDOWN=4;
    public static final int MACHINE_GURLS=5;
    public static final int ALTERNATE_DUMBBELL_CURL=6;
    public static final int PEC_DECK_FLYE=7;
    public static final int INCLINE_DUMBBEL_PRESS=8;
    public static final int CABLE_CROSSOVERS=9;

    //abnormal type
    public static final int INITIAL_ABNORMAL_TYPE=-1;
    public static final int TOO_QUICK=1000;
    public static final int NOT_BALANCE=1001;
    public static final int NOT_STABLE=1002;

    private DataProcessor(){}

    public DataProcessor getInstance(){
        return dataProcessor;
    }

    /**
     * @param inputSignal
     * @return
     */
    public static double[] filter(double[] inputSignal){
        return null;
    }

    /**
     * Is the the person moving?
     * @param filteredSignal
     * @return true: activity;false:static
     */
    private static boolean isbelongSegments(double[] filteredSignal){
        return false;
    }

    /**
     * select two vector of the inputSignal
     * @param input
     * @return
     */
    public static int[] dataSelect(double[][] input){
        return null;
    }

    /**
     * identify the type of activity
     * @param signalSegmentX
     * @param signalSegmentY
     * @return
     */
    public static int activityRecognition(double[] signalSegmentX,double[] signalSegmentY,int m,int n){
        return 0;
    }


    /**
     * check whether it is a abnormal behavior in the activity
     * @param signalSegmentX
     * @param signalSegmentY
     * @return
     */
    public static int abnormalDetection(double[] signalSegmentX,double[] signalSegmentY){
        return 0;
    }

    /**
     *
     * @param signalSegmentX
     * @param signalSegmentY
     * @return
     */
    public static int zoomSegment(double[] signalSegmentX,double[] signalSegmentY){
        return 0;
    }

    /**
     * signalSegments
     * @param signalSegmentX
     * @param signalSegmentY
     * @return
     */
    public static double timeBalan(double[] signalSegmentX,double[] signalSegmentY){
        return 0;
    }

    /**
     *
     * @param signalSegmentX
     * @param signalSegmentY
     * @return
     */
    public static double amplitudeBalan(double[] signalSegmentX,double[] signalSegmentY){
        return 0;
    }

    /**
     * calculate the score of one repetition
     * @param signalSegmentX
     * @param signalSegmentY
     * @return
     */
    public static double repetitionScore(double[] signalSegmentX,double[] signalSegmentY,int m,int n){
        return 0;
    }

    /**
     * calculate the score of a set of exercise
     * @param repetitionScore a list of scores
     * @return
     */
    public static double setScore(double[] repetitionScore){
        return 0;
    }

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
            for (int i=0;i<dimension;++i){
                if(isbelongSegments(filteredSignal[i])==true){
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
