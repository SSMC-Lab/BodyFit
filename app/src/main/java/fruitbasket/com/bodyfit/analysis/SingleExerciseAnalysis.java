package fruitbasket.com.bodyfit.analysis;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import fruitbasket.com.bodyfit.Conditions;
import fruitbasket.com.bodyfit.data.DataSet;
import fruitbasket.com.bodyfit.data.DataSetBuffer;
import fruitbasket.com.bodyfit.data.SelectedDataSet;
import fruitbasket.com.bodyfit.data.StorageData;


public class SingleExerciseAnalysis implements ExerciseAnalysis {
    private final String tag="SingleExerciseAnalysis";

    private Context context;
    private SelectedDataSet selectedDataSet;
    private DataSetBuffer dataBuffer;  //用于收集存储单个完整动作的数据

    private int currentTimes=-1;   //指示用户当前做了多少个健身动作
    private int Nsamples=0;   //传进来的数组的长度
    private final int span=Conditions.MID_SPAN;     //滑动均值滤波的区间
//    private final double Varthreshold=Conditions.VALUE_OF_VARTHRESHOLD;    //方差阈值
//    private final double Avrthreshold=Conditions.VALUE_OF_AVRTHRESHOLD;    //平均值阈值
    private  double Avrthreshold1=0;    //平均值阈值
    private  double Avrthreshold2=0;    //平均值阈值
    private  double Avrthreshold3=0;    //平均值阈值
    private final int MaxSamples=Conditions.MAX_SAMPLES_OF_ACTIONS;     //单个动作中允许的最大samples
    private final int MinSamples=Conditions.MIN_SAMPLES_OF_ACTIONS;     //单个动作中允许的最小samples
    private int firstrun=0;     ///
    private double dataBuf[]=new double[span];  ///会随着firstrun的没设置好而发错误
    private ExerciseType exerciseType,lastType;
    private double[][] repetitionScore=new double[Conditions.NUM_PRE_EXERCISE][];
    private double setScore;

    private int tempLength=50;
    private int tempIndex=0;
    private double gx[]=new double[tempLength];
    private double gy[]=new double[tempLength];
    private double gz[]=new double[tempLength];
    private double temp1[]=new double[5];
    private double temp2[]=new double[5];
    private double temp3[]=new double[5];
    private StorageData out=new StorageData();
    private boolean isEndTimeFirst=true;
    private double start,end,time;
    private final static double INTERVAL_OF_ONE_ACTION=500; //单位ms

    private int SIX_OR_SEVEN=0;//表示属于动作6还是动作7，因为6和7很容易搞混，但在ay数据有很大区别，故以此区分
    private int TWO_OR_SIX=0;   //区分2和6
    private int NUM_OF_ACTION=0;

    /*
    1.用户未开始做健身动作：hasBegin=false,hasCollected=false；
    2.用户开始做第一个健身动作，但未做完：hasBegin=true,hasCollected=false;
    3.用户完成了第一个健身动作，但未开始处理刚收集的数据且未开始二个动作：hasBegin=false,hasCollected=true；
    4.用户完成了第一个健身动作，但已开始处理刚收集的数据，而未开始第二个动作：hasBegin=false,hasCollected=false；
     */

    private boolean hasBegin=false; //指示目前是否已经收集完一个完整动作的数据
    private boolean hasCollected=false; //指示目前是否已经收集完一个完整动作的数据
    private boolean isDoing=false;  //指示是否正处于做动作期间
    private boolean hasCollectedStaticData=false;   //指示是否已经在一开始收集了静止时的数据
//存储模板数据
    private double[][]ax_mol=new double[10][];
    private double[][]ay_mol=new double[10][];
    private double[][]az_mol=new double[10][];
    private double[][]gx_mol=new double[10][];
    private double[][]gy_mol=new double[10][];
    private double[][]gz_mol=new double[10][];
    //存储测试数据
    private double ax_test[]=new double[MaxSamples];
    private double ay_test[]=new double[MaxSamples];
    private double az_test[]=new double[MaxSamples];
    private double gx_test[]=new double[MaxSamples];
    private double gy_test[]=new double[MaxSamples];
    private double gz_test[]=new double[MaxSamples];
    //此标志使得程序只读取一次模板数据
    private boolean isFirstRecognized=true;
    public SingleExerciseAnalysis(){
        dataBuffer=new DataSetBuffer();
    }

    private void notBeginExercise(){
        Log.i(tag,"notBeginExercise");
        hasBegin=false;
        hasCollected=false;
        isDoing=false;
    }

    private void doingExercise(){
        Log.i(tag,"doingExercise");
        hasBegin=true;
        hasCollected=false;
        isDoing=true;
    }

    private void notBeginProcess(){
        Log.i(tag,"notBeginProcess");
        hasBegin=true;
        hasCollected=true;
        isDoing=false;
        setSegmentData();
    }

    private void setSegmentData(){
        isEndTimeFirst=true;///
    }

    /**
     * 虑噪函数，滑动均值滤波算法
     * @param sensorDatas
     * @param span
     * @return
     */
    private double[] filter(double[] sensorDatas,int span){
        int i,j;
        double temp;
        double afterFilted[] = new double[sensorDatas.length];
        Nsamples=sensorDatas.length;

        for(i=0;i<Nsamples;i++){
            temp=sensorDatas[i];
            double sum =0, avg;

            if (firstrun == 0) {
                for (j = 0; j < span; j++)
                    dataBuf[j] = temp;
                firstrun = 1;
            }

            for (j = 0; j < span- 1; j++)
                dataBuf[j] = dataBuf[j + 1];
            dataBuf[span - 1] = temp;

            sum=sum(dataBuf);

            avg = sum / span;
            afterFilted[i]=avg;
//            Log.e("filter","sum="+sum+" "+"sensorDatas="+sensorDatas[i]+" "+"afterFilted="+afterFilted[i]);
        }
        return afterFilted;
    }

    /**
     * 对数据记录集合进行预处理
     * @param dataSet
     * @return
     */
    private DataSet filter(DataSet dataSet){
        ///span
        dataSet.setAxSet(filter(dataSet.getAxSet(), span));
        dataSet.setAySet(filter(dataSet.getAySet(), span));
        dataSet.setAzSet(filter(dataSet.getAzSet(), span));
        dataSet.setGxSet(filter(dataSet.getGxSet(), span));
        dataSet.setGySet(filter(dataSet.getGySet(), span));
        dataSet.setGzSet(filter(dataSet.getGzSet(), span));
        dataSet.setMxSet(filter(dataSet.getMxSet(), span));
        dataSet.setMySet(filter(dataSet.getMySet(), span));
        dataSet.setMzSet(filter(dataSet.getMzSet(), span));
        dataSet.setP1Set(filter(dataSet.getP1Set(), span));
        dataSet.setP2Set(filter(dataSet.getP2Set(), span));
        dataSet.setP3Set(filter(dataSet.getP3Set(), span));
        return dataSet;
    }

    /**
     *判断是否符合切割条件；true，开始存数据；false，暂停存数据；
     * 则表明已经收集到一组完整动作的数据
     * @param dataSet
     * @return
     */
    private boolean isbelongSegments(DataSet dataSet) {
//        Log.i(tag, "isbelongSegments()");

        double avg1,avg2,avg3;
        avg1=absAvg(dataSet.getGxSet());
        avg2=absAvg(dataSet.getGySet());
        avg3=absAvg(dataSet.getGzSet());
//        Log.i(tag, "avg1=" + avg1 + " avg2=" + avg2 + " avg3=" + avg3);
//        Log.i(tag, "Avrthreshold1=" + Avrthreshold1 + " Avrthreshold2=" + Avrthreshold2 + " Avrthreshold3=" + Avrthreshold3);
        if(avg1>Avrthreshold1 && avg2>Avrthreshold2
                || avg1>Avrthreshold1 && avg3>Avrthreshold3
                || avg2>Avrthreshold2 && avg3>Avrthreshold3) {
            isEndTimeFirst=true;
            return true;
        }

        else  if(isDoing){
            if(isEndTimeFirst){
                start=System.currentTimeMillis();
                isEndTimeFirst=false;
            }
            end=System.currentTimeMillis();
            time=end-start;
            Log.i(tag,"interval_of_one_action="+time);
            if(time<INTERVAL_OF_ONE_ACTION)
                return true;
        }

        return false;
    }

    /**
     * 从exerciseDataSet中筛选有效的数据,选择需要用到的维度的数据
     */
    private boolean dataSelect(){
        Log.i(tag,"dataSelect()");
        Log.i(tag,"beforeSelect,dataBuffer.capacity="+dataBuffer.getCapacity());

        //若收到的一个动作的数据小于MinSamples，则丢弃，即清空dataBuffer
        if(dataBuffer.getCapacity()<MinSamples)
            dataBuffer.clear();

        if(dataBuffer.isEmpty()==false){

            ///筛选指定维度的数据，然后存放在selectedDataSet,筛选呢些维度较好？
            selectedDataSet=new SelectedDataSet(dataBuffer.toDataSet(),0,1,2,3,4,5);
            dataBuffer.clear();
            Log.i(tag, "afterSelect,dataBuffer.capacity=" + dataBuffer.getCapacity());
            return true;
        }
        else{
            return false;
        }
    }

    /**
     * 识别健身动作的类型
     * @return
     */
    private void ExerciseRecognition(){//参数为测试数据
        Log.i(tag,"ExerciseRecognition()");
        if(selectedDataSet==null){
            if(dataBuffer.isEmpty()==true){
                return;
            }
            else{
                dataSelect();
            }
        }
        ax_test=selectedDataSet.getDataByIndex(0);
        ay_test=selectedDataSet.getDataByIndex(1);
        az_test=selectedDataSet.getDataByIndex(2);
        gx_test=selectedDataSet.getDataByIndex(3);
        gy_test=selectedDataSet.getDataByIndex(4);
        gz_test=selectedDataSet.getDataByIndex(5);

        /*for(int i=0;i<ax_test.length;i++){
            Log.i(tag,"ax_test[i]="+ax_test[i]+" ay_test[i]="+ay_test[i]+" az_test[i]="+az_test[i]);
        }*/

        if(isFirstRecognized) {
            readModuleData();
            isFirstRecognized=false;
        }

        ///这里需根据selectedDataSet填充算法
        //exerciseType=null;
        double Dist[]=new double[ax_mol.length];
        double minDis1=10000000.0,minDis2=10000000.0;//记录Dist最小的动作标号
        int minIndex1=1,minIndex2=1;
      for(int i=0;i<ax_mol.length;i++)
      {
          //第3、4、5种动作暂时不判断
          if(i==2 || i==3 || i==4)
              continue;

          Dist[i]=0;
          Dist[i]+=dtw_yp(ax_mol[i],ax_test);
          Dist[i]+=dtw_yp(ay_mol[i],ay_test);
          Dist[i]+=dtw_yp(az_mol[i],az_test);
          Dist[i]+=dtw_yp(gx_mol[i],gx_test);
          Dist[i]+=dtw_yp(gy_mol[i],gy_test);
          Dist[i]+=dtw_yp(gz_mol[i],gz_test);

          Log.i(tag, "Dist[" + i + "]=" + Dist[i]);
          //同时找出两个最小的dtw值，并且记录下对应的动作
          if(Dist[i]<minDis1) {
              minIndex2=minIndex1;
              minDis2=minDis1;
              minIndex1 = i+1;//从minIndex.txt文件中读取的数据与测试数据最可能匹配上
              minDis1 = Dist[i];
          }
          else if(Dist[i]>minDis1 && Dist[i]<minDis2){
              minIndex2=i+1;
              minDis2=Dist[i];
          }
      }
        /*以下用于处理容易混淆的两种动作*/
        //6和7搞混的情况
        if(minIndex1==6&&minIndex2==7 || minIndex1==7&&minIndex2==6){
            SIX_OR_SEVEN=6;
            for(int i=0;i<ay_test.length;i++){
                if(ay_test[i]<(-5)){
                    SIX_OR_SEVEN=7;
                    break;
                }
            }
            minIndex1=SIX_OR_SEVEN;
            minDis1=Dist[SIX_OR_SEVEN-1];
        }
        //2和6搞混的情况
        if(minIndex1==2&&minIndex2==6 || minIndex1==6&&minIndex2==2){
            TWO_OR_SIX=2;
            for(int i=0;i<ay_test.length;i++){
                if(ay_test[i]<2){
                    SIX_OR_SEVEN=6;
                    break;
                }
            }
            minIndex1=TWO_OR_SIX;
            minDis1=Dist[TWO_OR_SIX-1];
        }
        setExerciseType(minIndex1);
        setActionNum();
        Log.e(tag,"ExerciseType="+minIndex1+" minDis="+minDis1);
    }
    //dtw算法,t为模式曲线，r为测试曲线
    private double dtw_yp(double[] t, double[] r) {
        int N = t.length;
        int M = r.length;
        double[][] d = new double[M][];
        for (int i = 0; i < M; i++)
            d[i] = new double[N];

        for (int i = 0; i < M; i++)
            for (int j = 0; j < N; j++)
                d[i][j] = (r[i] - t[j]) * (r[i] - t[j]);// 计算两点之间的距离

        double[][] D = new double[M][];
        for (int i = 0; i < M; i++)
            D[i] = new double[N];
        for (int i = 0; i < M; i++)
            for (int j = 0; j < N; j++)
                D[i][j] = 0;

        D[0][0] = d[0][0];
        for (int i = 1; i < M; i++)
            D[i][0] = d[i][0] + D[i - 1][0];
        for (int i = 1; i < N; i++)
            D[0][i] = d[0][i] + D[0][i - 1];
        for (int n = 1; n < M; n++)
            for (int m = 1; m < N; m++)
                D[n][m] = d[n][m]
                        + min(D[n - 1][m], D[n - 1][m - 1], D[n][m - 1]);
        double Dist = D[M - 1][N - 1];
        return Dist;
    }
    //求三个数的最小值
    private double min(double x1, double x2, double x3) {
        if (x1 <= x2 && x1 <= x3)
            return x1;
        else if (x2 <= x1 && x2 <= x3)
            return x2;
        else
            return x3;
    }
    /**
     * 计算单个动作的评分
     * @return
     */
    private void repetitionScore() {
        if(selectedDataSet==null){
            if(dataBuffer.isEmpty()==true){

                return;
            }
            else{
                dataSelect();
            }
        }

        ///这里需根据selectedDataSet填充算法
        //repetitionScore[currentTimes];
    }

    /**
     * 计算一组动作的评分
     * @return
     */
    private double[] setScore() {
        if(selectedDataSet==null){
            if(dataBuffer.isEmpty()==true){
                return null;
            }
            else{
                dataSelect();
            }
        }

        ///这里需根据selectedDataSet填充算法
        return null;
    }

    /**
     * 增加数据记录集合到单个完整的动作数据记录集合中
     * @param dataSet
     * @return
     */
    public boolean addToSet(DataSet dataSet){
//        Log.i(this.toString(), "addToSet(),before add");

        //储存过滤后的数据
        filter(dataSet);
        try {
            out.outputData(dataSet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
/*
        //若dataBuffer太长，则说明不是一个标准动作，所以清空
        if(dataBuffer.getCapacity()>MaxSamples)
            dataBuffer.clear();

        if(hasCollected==false){
            filter(dataSet);

            //利用一开始的静止，获取到静止时的平均值，以便于做切分,存取50组数据
            if(hasCollectedStaticData==false) {
                if (tempIndex < tempLength) {
                    collectStaticData(dataSet);
                    return true;
                } else {
                    Avrthreshold1 = absAvg(gx) * 5;
                    Avrthreshold2 = absAvg(gy) * 5;
                    Avrthreshold3 = absAvg(gz) * 5;
                    hasCollectedStaticData=true;
                    Log.e(tag,"======================================");
                }
            }

            //判断是否符合切割条件，若符合，则存数据，否则不断覆盖数据
            if(isbelongSegments(dataSet)==true){
                dataBuffer.add(dataSet);
                doingExercise();
                return true;
            }
            else if(hasBegin==false){//做第一个动作之前会到这
                notBeginExercise();
                return false;
            }
            else{//每一个动作结束之后会到这
                notBeginProcess();
                return false;
            }
        }
        else{
//            notBeginProcess();
            return false;
        }*/
    }

    @Override
    public void analysis() {
        ///这里会存在线程不安全问题
        currentTimes++;
        if(currentTimes>=Conditions.NUM_PRE_EXERCISE-1){
            ///给出相应的处理
            currentTimes=0;
        }


        if(hasCollected==true) {
            if(dataSelect()==true) {

                ExerciseRecognition();
                repetitionScore();
                setScore();
                notBeginExercise();
            }
            else{
                notBeginExercise();
            }
        }
    }

    /**
     * 返回动作的类型
     */
    public ExerciseType getExerciseType(){
        return exerciseType;
    }

    /**
     * 返回指定健身动作的评分
     * @param index
     * @return
     */
    public double[] getRepetitionScore(int index){
        return repetitionScore[index];
    }

    public double[] getRepetitionScore(){
        return repetitionScore[currentTimes];
    }

    public double getSetScore(){
        return setScore;
    }

    /**
     * 方差
     * @param a
     * @return
     */
    private double absVar(double a[]){
        double sum=0,var;
        int i;
        for(i=0;i<a.length;i++)
        {
            sum+=a[i];
        }
        sum=sum/a.length;
        double result=0.0;
        for(i=0;i<a.length;i++)
        {
            double temp=a[i]-sum;
            result+=temp*temp;
        }
        var=result/a.length;
        return var;
    }

    /**
     * 平均值
     * @param a
     * @return
     */
    private double absAvg(double[]a){
        double sum=0,avg;
        int i;
        for(i=0;i<a.length;i++)
        {
            sum+=abs(a[i]);
        }
        avg=sum/a.length;

        return avg;
    }

    /**
     * 绝对值
     * @param a
     * @return
     */
    private double abs(double a){
        if(a<0)
            a=-a;
        return a;
    }

    /**
     * 求和
     * @param a
     * @return
     */
    private double sum(double []a){
        int i;
        double res=0;
        for(i=0;i<a.length;i++)
            res+=a[i];
        return res;
    }

    /**
     * 最小值
     * @param a
     * @return
     */
    private double min(double[] a){
        int i;
        double res=10000000;
        for(i=0;i<a.length;i++)
            if(a[i]<res)
                res=a[i];
        return res;
    }

    /**
     * 利用一开始的静止，获取到静止时的平均值，以便于做切分,存取50组数据
     */
    private void collectStaticData(DataSet dataSet){
        temp1=dataSet.getGxSet();
        temp2=dataSet.getGySet();
        temp3=dataSet.getGzSet();

        int i=0;
        for(i=0;i<temp1.length;i++){
            gx[tempIndex]=temp1[i];
            gy[tempIndex]=temp2[i];
            gz[tempIndex]=temp3[i];
            tempIndex++;
        }
    }

    /**
     * 读取模板数据
     */
    private void readModuleData(){
        //读取模板数据,先将模板数据定为MaxSample大小
        for(int i=0;i<10;i++)
        {
            ax_mol[i]=new double[MaxSamples];
            ay_mol[i]=new double[MaxSamples];
            az_mol[i]=new double[MaxSamples];
            gx_mol[i]=new double[MaxSamples];
            gy_mol[i]=new double[MaxSamples];
            gz_mol[i]=new double[MaxSamples];
        }

        try {
            String Path[]= {
                    "1.txt","2.txt", "3.txt","4.txt", "5.txt","6.txt", "7.txt","8.txt", "9.txt","10.txt"
            };
            int count=0;//记录第count-1个动作的数据
            if(context!=null){
                while(count<Path.length) {
                    Log.i(tag,"count="+count+" Path.length="+Path.length);
                    InputStreamReader inputReader = new InputStreamReader(context.getResources().getAssets().open(Path[count]),"GBK" );
                    BufferedReader bufReader = new BufferedReader(inputReader);
                    Log.i(tag,"load successfully");

                    String line = "";
                    String str[];
                    int num = 0;//记录第count-1个动作的第num-1个ax/ay/az
                    while ((line = bufReader.readLine()) != null) {
//                        Log.i(tag,"line="+line);
                        str = line.split(" ");
                        ax_mol[count][num]=Double.parseDouble(str[1]);
                        ay_mol[count][num]=Double.parseDouble(str[2]);
                        az_mol[count][num]=Double.parseDouble(str[3]);
                        gx_mol[count][num]=Double.parseDouble(str[4]);
                        gy_mol[count][num]=Double.parseDouble(str[5]);
                        gz_mol[count][num]=Double.parseDouble(str[6]);
                        num++;
                        if(num>=MaxSamples)
                            break;
                    }
                    count++;
                }
            }
            else{
                //若context==null
                Log.e(tag,"SingleExerciseAnalysis-->context=null,error");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void setExerciseType(int type){
        if(type<1 || type>10){
            Log.e(tag,"setExerciseType(),enter wrong type");
            return;
        }
        switch(type){
            case 1:
                exerciseType=ExerciseType.FLAT_BENCH_BRABELL_PASS_1;
                break;
            case 2:
                exerciseType=ExerciseType.FLAT_BENCH_DUMBBELL_FLYE_2;
                break;
            case 3:
                exerciseType=ExerciseType.FLAT_BENCH_DUMBBELL_PRESS_3;
                break;
            case 4:
                exerciseType=ExerciseType.INCLINE_DUMBBELL_FLYE_4;
                break;
            case 5:
                exerciseType=ExerciseType.REVERSE_GRIP_PULLDOWN_5;
                break;
            case 6:
                exerciseType=ExerciseType.MACHINE_GURLS_6;
                break;
            case 7:
                exerciseType=ExerciseType.ALTERNATE_DUMBBELL_CURL_7;
                break;
            case 8:
                exerciseType=ExerciseType.PEC_DECK_FLYE_8;
                break;
            case 9:
                exerciseType=ExerciseType.INCLINE_DUMBBEL_PRESS_9;
                break;
            case 10:
                exerciseType=ExerciseType.CABLE_CROSSOVERS_10;
                break;
        }
    }

    private void setActionNum(){
        if(exerciseType==lastType)
            NUM_OF_ACTION++;
        else{
            lastType=exerciseType;
            NUM_OF_ACTION=1;
        }
    }
    public int getActionNum(){
        return NUM_OF_ACTION;
    }

    public void setContext(Context context){
        this.context=context;
    }
}


