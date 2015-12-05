package fruitbasket.com.bodyfit.processor;

import android.content.res.AssetManager;
import android.os.Environment;

import fruitbasket.com.bodyfit.data.Data;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

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

    static int sampleBeRead=0;
    static double samplingRate=30;

    //TEN SAMPLEZS EACH ONE HAS 4 REPETITIONS
    static double[][] sample0=new double[24][250];
    static double[][] sample1=new double[24][250];
    static double[][] sample2=new double[24][250];
    static double[][] sample3=new double[24][250];
    static double[][] sample4=new double[24][250];
    static double[][] sample5=new double[24][250];
    static double[][] sample6=new double[24][250];
    static double[][] sample7=new double[24][250];
    static double[][] sample8=new double[24][250];
    static double[][] sample9=new double[24][250];
    /////END OF GLOBAL VARIABLES/////

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

        double[] result=new double[inputSignal.length];

        for(int i=0;i<inputSignal.length;i++){
            double temp=0;
            if(i<span-1){
                for(int j=i;j>=0;j--){
                    temp+=inputSignal[j];
                }
                inputSignal[i]=temp/(i+1);
            }
            else{
                for(int j=i;j>i-span;j--){
                    temp+=inputSignal[j];
                }
                inputSignal[i]=temp/span;
            }
        }
        result=inputSignal;
        return result;
    }

    /**
     * Is the the person moving?
     * @param filteredSignal
     * @return
     */
    public static boolean isbelongSegments(double[][] filteredSignal){

        int NumSamples=filteredSignal[1].length;
        double energyThreshold=5;
        double[] energy=new double[NumSamples];
        double[] SqrEnergy=new double[NumSamples];
        double energySum=0;
        double SqrEnergySum=0;
        double[] GyroSum={0,0,0};

        for(int j=0;j<NumSamples;j++){

            energy[j]=Math.pow(filteredSignal[0][j],2)+Math.pow(filteredSignal[1][j],2)+Math.pow(filteredSignal[2][j],2);
            energySum+=energy[j];
            SqrEnergy[j]=Math.sqrt(energy[j]);
            SqrEnergySum+=SqrEnergy[j];

            GyroSum[0]+=filteredSignal[3][j];
            GyroSum[1]+=filteredSignal[4][j];
            GyroSum[2]+=filteredSignal[5][j];

        }

        double energyAverage=energySum/NumSamples;
        double SqrEnergyAverage=SqrEnergySum/NumSamples;

        double[] GyroAverage={0,0,0};
        double[] GyroVariance={0,0,0};
        double[] GyroStd={0,0,0};

        for(int i=0;i<3;i++){

            GyroAverage[i]=GyroSum[i]/NumSamples;

            for(int j=0;j<NumSamples;j++){
                GyroVariance[i]+=Math.pow((filteredSignal[i][j]-GyroAverage[i]),2)/NumSamples;
            }
            GyroStd[i]=Math.sqrt(GyroVariance[i]);

        }

/**The following code is used for identify the starting and ending point based on extracted metric*/

        if(energyAverage>energyThreshold && SqrEnergyAverage<2)
            return true;
        else
            return false;
    }

    /**
     * select two vector of the inputSignal
     * @param input
     * @return
     */
    public static int[] dataSelect(double[][] repetitionSegment){
        double[][] params=new double[6][3];
        int[] dataIndx=new int[2];


        params=CalculateMetrics(repetitionSegment);

        double AccMaxVar=Math.max(Math.max(params[0][2],params[1][2]),Math.max(params[0][2],params[2][2]));
        double GyoMaxVar=Math.max(Math.max(params[3][2],params[4][2]),Math.max(params[3][2],params[5][2]));

        for(int i=0;i<6;i++){
            if(params[i][2]==AccMaxVar)
                dataIndx[0]=i;
            else if(params[i][2]==GyoMaxVar)
                dataIndx[1]=i;
        }
        return dataIndx;
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
        ///
        if(sampleBeRead==0){
            readSamples();
        }

        int i=0;


        double[] temp=new double[8];
        double[] result=new double[10];
        double average;
        /////CAL DTW AGAINST MOVEMENT0/////
        average=0;
        for(i=0;i<4;i++){
            temp[i]=dtw(signalSegmentX,sample0[m+i*6]);
            temp[i+4]=dtw(signalSegmentY,sample0[n+i*6]);
            average=average+temp[i]+temp[i+4];
        }
        average/=8;
        result[0]=average;

        /////CAL DTW AGAINST MOVEMENT1/////
        average=0;
        for(i=0;i<4;i++){
            temp[i]=dtw(signalSegmentX,sample1[m+i*6]);
            temp[i+4]=dtw(signalSegmentY,sample1[n+i*6]);
            average=average+temp[i]+temp[i+4];
        }
        average/=8;
        result[1]=average;

        /////CAL DTW AGAINST MOVEMENT2/////
        average=0;
        for(i=0;i<4;i++){
            temp[i]=dtw(signalSegmentX,sample2[m+i*6]);
            temp[i+4]=dtw(signalSegmentY,sample2[n+i*6]);
            average=average+temp[i]+temp[i+4];
        }
        average/=8;
        result[2]=average;

        /////CAL DTW AGAINST MOVEMENT3/////
        average=0;
        for(i=0;i<4;i++){
            temp[i]=dtw(signalSegmentX,sample3[m+i*6]);
            temp[i+4]=dtw(signalSegmentY,sample3[n+i*6]);
            average=average+temp[i]+temp[i+4];
        }
        average/=8;
        result[3]=average;

        /////CAL DTW AGAINST MOVEMENT4/////
        average=0;
        for(i=0;i<4;i++){
            temp[i]=dtw(signalSegmentX,sample4[m+i*6]);
            temp[i+4]=dtw(signalSegmentY,sample4[n+i*6]);
            average=average+temp[i]+temp[i+4];
        }
        average/=8;
        result[4]=average;

        /////CAL DTW AGAINST MOVEMENT5/////
        average=0;
        for(i=0;i<4;i++){
            temp[i]=dtw(signalSegmentX,sample5[m+i*6]);
            temp[i+4]=dtw(signalSegmentY,sample5[n+i*6]);
            average=average+temp[i]+temp[i+4];
        }
        average/=8;
        result[5]=average;

        /////CAL DTW AGAINST MOVEMENT6/////
        average=0;
        for(i=0;i<4;i++){
            temp[i]=dtw(signalSegmentX,sample6[m+i*6]);
            temp[i+4]=dtw(signalSegmentY,sample6[n+i*6]);
            average=average+temp[i]+temp[i+4];
        }
        average/=8;
        result[6]=average;

        /////CAL DTW AGAINST MOVEMENT7/////
        average=0;
        for(i=0;i<4;i++){
            temp[i]=dtw(signalSegmentX,sample7[m+i*6]);
            temp[i+4]=dtw(signalSegmentY,sample7[n+i*6]);
            average=average+temp[i]+temp[i+4];
        }
        average/=8;
        result[7]=average;

        /////CAL DTW AGAINST MOVEMENT8/////
        average=0;
        for(i=0;i<4;i++){
            temp[i]=dtw(signalSegmentX,sample8[m+i*6]);
            temp[i+4]=dtw(signalSegmentY,sample8[n+i*6]);
            average=average+temp[i]+temp[i+4];
        }
        average/=8;
        result[8]=average;

        /////CAL DTW AGAINST MOVEMENT9/////
        average=0;
        for(i=0;i<4;i++){
            temp[i]=dtw(signalSegmentX,sample9[m+i*6]);
            temp[i+4]=dtw(signalSegmentY,sample9[n+i*6]);
            average=average+temp[i]+temp[i+4];
        }
        average/=8;
        result[9]=average;

        int flag=0;
        for(i=1;i<10;i++){
            if(result[i]<result[0]){
                result[0]=result[i];
                flag=i;
            }
        }
        switch(flag){
            case 0:return FLAT_BENCH_BRABELL_PASS;
            case 1:return FLAT_BENCH_DUMBBELL_FLYE;
            case 2:return FLAT_BENCH_DUMBBELL_PRESS;
            case 3:return INCLINE_DUMBBELL_FLYE;
            case 4:return REVERSE_GRIP_PULLDOWN;
            case 5:return MACHINE_GURLS;
            case 6:return ALTERNATE_DUMBBELL_CURL;
            case 7:return PEC_DECK_FLYE;
            case 8:return INCLINE_DUMBBEL_PRESS;
            case 9:return CABLE_CROSSOVERS;
            default:return INITIAL_EXERCISE_TYPE;
        }
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
        if(sampleBeRead==0){
            readSamples();
        }
        int i=0;
        double[] temp=new double[8];
        double[] result=new double[10];
        double average;
        /////CAL DTW AGAINST MOVEMENT0/////
        average=0;
        for(i=0;i<4;i++){
            temp[i]=dtw(signalSegmentX,sample0[m+i*6]);
            temp[i+4]=dtw(signalSegmentY,sample0[n+i*6]);
            average=average+temp[i]+temp[i+4];
        }
        average/=8;
        result[0]=average;

        /////CAL DTW AGAINST MOVEMENT1/////
        average=0;
        for(i=0;i<4;i++){
            temp[i]=dtw(signalSegmentX,sample1[m+i*6]);
            temp[i+4]=dtw(signalSegmentY,sample1[n+i*6]);
            average=average+temp[i]+temp[i+4];
        }
        average/=8;
        result[1]=average;

        /////CAL DTW AGAINST MOVEMENT2/////
        average=0;
        for(i=0;i<4;i++){
            temp[i]=dtw(signalSegmentX,sample2[m+i*6]);
            temp[i+4]=dtw(signalSegmentY,sample2[n+i*6]);
            average=average+temp[i]+temp[i+4];
        }
        average/=8;
        result[2]=average;

        /////CAL DTW AGAINST MOVEMENT3/////
        average=0;
        for(i=0;i<4;i++){
            temp[i]=dtw(signalSegmentX,sample3[m+i*6]);
            temp[i+4]=dtw(signalSegmentY,sample3[n+i*6]);
            average=average+temp[i]+temp[i+4];
        }
        average/=8;
        result[3]=average;

        /////CAL DTW AGAINST MOVEMENT4/////
        average=0;
        for(i=0;i<4;i++){
            temp[i]=dtw(signalSegmentX,sample4[m+i*6]);
            temp[i+4]=dtw(signalSegmentY,sample4[n+i*6]);
            average=average+temp[i]+temp[i+4];
        }
        average/=8;
        result[4]=average;

        /////CAL DTW AGAINST MOVEMENT5/////
        average=0;
        for(i=0;i<4;i++){
            temp[i]=dtw(signalSegmentX,sample5[m+i*6]);
            temp[i+4]=dtw(signalSegmentY,sample5[n+i*6]);
            average=average+temp[i]+temp[i+4];
        }
        average/=8;
        result[5]=average;

        /////CAL DTW AGAINST MOVEMENT6/////

        average=0;
        for(i=0;i<4;i++){
            temp[i]=dtw(signalSegmentX,sample6[m+i*6]);
            temp[i+4]=dtw(signalSegmentY,sample6[n+i*6]);
            average=average+temp[i]+temp[i+4];
        }
        average/=8;
        result[6]=average;

        /////CAL DTW AGAINST MOVEMENT7/////
        average=0;
        for(i=0;i<4;i++){
            temp[i]=dtw(signalSegmentX,sample7[m+i*6]);
            temp[i+4]=dtw(signalSegmentY,sample7[n+i*6]);
            average=average+temp[i]+temp[i+4];
        }
        average/=8;
        result[7]=average;

        /////CAL DTW AGAINST MOVEMENT8/////
        average=0;
        for(i=0;i<4;i++){
            temp[i]=dtw(signalSegmentX,sample8[m+i*6]);
            temp[i+4]=dtw(signalSegmentY,sample8[n+i*6]);
            average=average+temp[i]+temp[i+4];
        }
        average/=8;
        result[8]=average;

        /////CAL DTW AGAINST MOVEMENT9/////
        average=0;
        for(i=0;i<4;i++){
            temp[i]=dtw(signalSegmentX,sample9[m+i*6]);
            temp[i+4]=dtw(signalSegmentY,sample9[n+i*6]);
            average=average+temp[i]+temp[i+4];
        }
        average/=8;
        result[9]=average;

        double totalAverage=0;
        for(i=0;i<10;i++){
            totalAverage+=result[i];
        }
        totalAverage/=10.0;
        return totalAverage;
    }

    /**
     * calculate the score of a set of exercise
     * @param repetitionScore a list of scores
     * @return
     */
    public static double setScore(double[] repetitionScore){
        int i=0;
        double average=0;
        for(i=0;i<repetitionScore.length;i++){
            average+=repetitionScore[i];
        }
        average/=repetitionScore.length;
        return average;
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
        /*int trueCounter=0,falseCounter=0;
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
        }*/
        double[][] input=new double[Data.DIMENSION][];
        input[0]=data.getAxSet();
        input[1]=data.getAySet();
        input[2]=data.getAzSet();
        input[3]=data.getGxSet();
        input[4]=data.getGySet();
        input[5]=data.getGzSet();
        return isbelongSegments(input);
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

    private static double[][] CalculateMetrics(double[][] yy){

        double[][] ReturnValues=new double[6][3];
        int NumSamples=yy[1].length;

        for(int i=0;i<6;i++){
            double sum=0;
            for(int j=0;j<NumSamples;j++){
                sum+=yy[i][j];
            }
            ReturnValues[i][0]=sum/NumSamples;      // Average value
            double sum1=0;

            for(int k=0;k<NumSamples;k++){
                sum1+=Math.pow((yy[i][k]-ReturnValues[i][0]),2);
            }
            ReturnValues[i][1]=sum1/(float)NumSamples;           // Variance
            ReturnValues[i][2]=Math.sqrt(ReturnValues[i][1]);    // Standard
        }
        return ReturnValues;
    }

    private static double dtw(double[] x, double[] y){

        double result=0;
        int M=0;
        int N=0;

        N=x.length;
        M=y.length;
        double[][] Dis=new double[N][M];
        double[][] D=new double[N][M];

        for(int n=0;n<N;n++){
            for(int m=0;m<M;m++){
                Dis[n][m]=Math.pow(x[n]-y[m],2);
            }
        }
        for(int n=0;n<N;n++)
        {
            for(int m=0;m<M;m++)
            {
                D[n][m]=0;
            }
        }
        D[0][0]=Dis[0][0];

        for(int n=1;n<N;n++){
            D[n][0]=Dis[n][0]+D[n-1][0];
        }
        for(int m=1;m<M;m++){
            D[0][m]=Dis[0][m]+D[0][m-1];
        }

        for(int n=1;n<N;n++){
            for(int m=1;m<M;m++){
                D[n][m]=Dis[n][m]+Math.min(Math.min(D[n-1][m],D[n-1][m-1]),Math.min(D[n-1][m],D[n][m-1]));
            }
        }
        result=D[N-1][M-1];
        return result;
    }

    private static void readSamples(){

        Scanner input=null;
        String line;
        String[] numbers;
        int i=0,j=0;
        try{
            input=new Scanner(new File("0.txt"));

        }
        catch(FileNotFoundException e){
            System.out.println("READ FILE ERROR");
        }
        j=0;
        while(input.hasNextLine()){
            line=input.nextLine();
            numbers=line.split(" ");
            System.out.println("# OF THIS LINE:"+numbers.length);
            for(i=0;i<numbers.length;i++){
                sample0[j][i]=Double.parseDouble(numbers[i]);
            }
            j++;
        }

        try{
            input=new Scanner(new File("1.txt"));
        }
        catch(FileNotFoundException e){
            System.out.println("READ FILE ERROR");
        }
        j=0;
        while(input.hasNextLine()){
            line=input.nextLine();
            numbers=line.split(" ");
            System.out.println("# OF THIS LINE:"+numbers.length);
            for(i=0;i<numbers.length;i++){
                sample1[j][i]=Double.parseDouble(numbers[i]);
            }
            j++;
        }

        try{
            input=new Scanner(new File("2.txt"));
        }
        catch(FileNotFoundException e){
            System.out.println("READ FILE ERROR");
        }
        j=0;
        while(input.hasNextLine()){
            line=input.nextLine();
            numbers=line.split(" ");
            System.out.println("# OF THIS LINE:"+numbers.length);
            for(i=0;i<numbers.length;i++){
                sample2[j][i]=Double.parseDouble(numbers[i]);
            }
            j++;
        }

        try{
            input=new Scanner(new File("3.txt"));
        }
        catch(FileNotFoundException e){
            System.out.println("READ FILE ERROR");
        }
        j=0;
        while(input.hasNextLine()){
            line=input.nextLine();
            numbers=line.split(" ");
            System.out.println("# OF THIS LINE:"+numbers.length);
            for(i=0;i<numbers.length;i++){
                sample3[j][i]=Double.parseDouble(numbers[i]);
            }
            j++;
        }

        try{
            input=new Scanner(new File("4.txt"));
        }
        catch(FileNotFoundException e){
            System.out.println("READ FILE ERROR");
        }
        j=0;
        while(input.hasNextLine()){
            line=input.nextLine();
            numbers=line.split(" ");
            System.out.println("# OF THIS LINE:"+numbers.length);
            for(i=0;i<numbers.length;i++){
                sample4[j][i]=Double.parseDouble(numbers[i]);
            }
            j++;
        }

        try{
            input=new Scanner(new File("5.txt"));
        }
        catch(FileNotFoundException e){
            System.out.println("READ FILE ERROR");
        }
        j=0;
        while(input.hasNextLine()){
            line=input.nextLine();
            numbers=line.split(" ");
            System.out.println("# OF THIS LINE:"+numbers.length);
            for(i=0;i<numbers.length;i++){
                sample5[j][i]=Double.parseDouble(numbers[i]);
            }
            j++;
        }

        try{
            input=new Scanner(new File("6.txt"));
        }
        catch(FileNotFoundException e){
            System.out.println("READ FILE ERROR");
        }
        j=0;
        while(input.hasNextLine()){
            line=input.nextLine();
            numbers=line.split(" ");
            System.out.println("# OF THIS LINE:"+numbers.length);
            for(i=0;i<numbers.length;i++){
                sample6[j][i]=Double.parseDouble(numbers[i]);
            }
            j++;
        }

        try{
            input=new Scanner(new File("7.txt"));
        }
        catch(FileNotFoundException e){
            System.out.println("READ FILE ERROR");
        }
        j=0;
        while(input.hasNextLine()){
            line=input.nextLine();
            numbers=line.split(" ");
            System.out.println("# OF THIS LINE:"+numbers.length);
            for(i=0;i<numbers.length;i++){
                sample7[j][i]=Double.parseDouble(numbers[i]);
            }
            j++;
        }

        try{
            input=new Scanner(new File("8.txt"));
        }
        catch(FileNotFoundException e){
            System.out.println("READ FILE ERROR");
        }
        j=0;
        while(input.hasNextLine()){
            line=input.nextLine();
            numbers=line.split(" ");
            System.out.println("# OF THIS LINE:"+numbers.length);
            for(i=0;i<numbers.length;i++){
                sample8[j][i]=Double.parseDouble(numbers[i]);
            }
            j++;
        }

        try{
            input=new Scanner(new File("9.txt"));
        }
        catch(FileNotFoundException e){
            System.out.println("READ FILE ERROR");
        }
        j=0;
        while(input.hasNextLine()){
            line=input.nextLine();
            numbers=line.split(" ");
            System.out.println("# OF THIS LINE:"+numbers.length);
            for(i=0;i<numbers.length;i++){
                sample9[j][i]=Double.parseDouble(numbers[i]);
            }
            j++;
        }
        input.close();
    }

    private static int zoomSegment(double[] Acc,double[] Gyo){

        int[] Index=new int[2];
        double[] diffAcc=new double[Acc.length];
        for(int i=0;i<Acc.length;i++){
            if(i==0)
                diffAcc[0]=Acc[0];
            else
                diffAcc[i]=Acc[i]-Acc[i-1];
            if(diffAcc[i]==0)
                Index[0]=i;

            if(Gyo[i]==0)
                Index[1]=i;
        }
        return (Index[0]+Index[1])/2;
    }

    private static double timeBalan(double[] signalSegmentX,double[] signalSegmentY){

        int mid=zoomSegment(signalSegmentX,signalSegmentY);
        int temp=signalSegmentX.length-mid-1;
        int result1;
        if(mid>temp){
            result1=mid/temp;
        }
        else{
            result1=temp/mid;
        }
        temp=signalSegmentY.length-mid-1;
        int result2;
        if(mid>temp){
            result2=mid/temp;
        }
        else{
            result2=temp/mid;
        }
        return (result1+result2)/2.0;
    }

    private static double amplitudeBalan(double[] signalSegmentX,double[] signalSegmentY){

        int mid=zoomSegment(signalSegmentX,signalSegmentY);
        double temp1=signalSegmentX[mid];
        temp1=Math.abs(temp1);
        int i;
        for(i=0;i<mid;i++){
            if(Math.abs(signalSegmentX[i])>temp1){
                temp1=Math.abs(signalSegmentX[i]);
            }
        }
        double temp2=signalSegmentX[mid];
        temp2=Math.abs(temp2);
        for(i=mid+1;i<signalSegmentX.length;i++){
            if(Math.abs(signalSegmentX[i])>temp2){
                temp2=Math.abs(signalSegmentX[i]);
            }
        }
        double result1;
        if(temp1>temp2)
            result1=temp1/temp2;
        else
            result1=temp2/temp1;

        /////GET TIME BALANCE OF THE FIRST DIMENSION/////
        temp1=signalSegmentY[mid];
        temp1=Math.abs(temp1);
        for(i=0;i<mid;i++){
            if(Math.abs(signalSegmentY[i])>temp1){
                temp1=Math.abs(signalSegmentY[i]);
            }
        }
        temp2=signalSegmentY[mid];
        temp2=Math.abs(temp2);
        for(i=mid+1;i<signalSegmentY.length;i++){
            if(Math.abs(signalSegmentY[i])>temp2){
                temp2=Math.abs(signalSegmentY[i]);
            }
        }
        double result2;
        if(temp1>temp2)
            result2=temp1/temp2;
        else
            result2=temp2/temp1;

        /////MERGE THE TWO RESULTS BY AVERAGE/////
        return (result1+result2)/2.0;
    }

    private static double[] dataNormalization(double[] d){
        double max,min;
        int i;
        max=min=d[0];
        for(i=1;i<d.length;i++){
            if(d[i]>max){
                max=d[i];
            }
            if(d[i]<min){
                min=d[i];
            }
        }
        for(i=0;i<d.length;i++){
            if(d[i]>0){
                d[i]/=max;
            }
            else if(d[i]<0){
                d[i]/=(0-min);
            }
        }
        return d;
    }
}
