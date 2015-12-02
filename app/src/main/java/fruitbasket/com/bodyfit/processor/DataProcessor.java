package fruitbasket.com.bodyfit.processor;

import java.util.ArrayList;

import fruitbasket.com.bodyfit.data.Data;
import fruitbasket.com.bodyfit.data.SourceData;

public class DataProcessor{
    private static final String TAG="DataProcessor";



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
