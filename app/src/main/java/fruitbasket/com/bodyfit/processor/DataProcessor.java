package fruitbasket.com.bodyfit.processor;

import fruitbasket.com.bodyfit.data.Data;

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
    public static final int NORMAL=1003;

    private DataProcessor(){}

    public DataProcessor getInstance(){
        return dataProcessor;
    }

    /**
     *
     * @param inputSignal
     * @param span
     * @return
     */
    public static double[] filter(double[] inputSignal,int span){
        return null;
    }

    /**
     * Is the the person moving?
     * @param filteredSignal
     * @return
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
     * @param m signalSegmentX的维度编号
     * @param n signalSegmentY的维度编号
     * @return
     */
    public static int activityRecognition(double[] signalSegmentX,double[] signalSegmentY,int m,int n){
        return 0;
    }


    /**
     * check whether it is a abnormal behavior in the activity
     * @param signalSegmentX
     * @param signalSegmentY
     * @param m signalSegmentX的维度编号
     * @param n signalSegmentY的维度编号
     * @return
     */
    public static int abnormalDetection(double[] signalSegmentX,double[] signalSegmentY,int m,int n){
        return 0;
    }

    /**
     *
     * @param signalSegmentX
     * @param signalSegmentY
     * @param m signalSegmentX的维度编号
     * @param n signalSegmentY的维度编号
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




    public static void filter(Data data,int span){
        filter(data.getAxSet(),span);
        filter(data.getAySet(),span);
        filter(data.getAzSet(),span);

        filter(data.getGxSet(),span);
        filter(data.getGySet(),span);
        filter(data.getGzSet(),span);
    }

    public static boolean isbelongSegments(Data data){
        int trueCounter=0,falseCounter=0;
        if(isbelongSegments(data.getAxSet())==true){
            ++trueCounter;
        }
        else{
            ++falseCounter;
        }
        if(isbelongSegments(data.getAySet())==true){
            ++trueCounter;
        }
        else{
            ++falseCounter;
        }
        if(isbelongSegments(data.getAzSet())==true){
            ++trueCounter;
        }
        else{
            ++falseCounter;
        }
        if(isbelongSegments(data.getGxSet())==true){
            ++trueCounter;
        }
        else{
            ++falseCounter;
        }
        if(isbelongSegments(data.getGySet())==true){
            ++trueCounter;
        }
        else{
            ++falseCounter;
        }
        if(isbelongSegments(data.getGzSet())==true){
            ++trueCounter;
        }
        else{
            ++falseCounter;
        }
        if(trueCounter>=falseCounter){
            return true;
        }
        else{
            return false;
        }
    }

    public static int[] dataSelect(Data data){
        double[][] input=new double[Data.DIMENSION][];
        input[0]=data.getAxSet();
        input[1]=data.getAySet();
        input[2]=data.getAzSet();
        input[3]=data.getGxSet();
        input[4]=data.getGySet();
        input[5]=data.getGzSet();
        return dataSelect(input);
    }
}
