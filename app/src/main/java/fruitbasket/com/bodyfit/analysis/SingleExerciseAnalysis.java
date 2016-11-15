package fruitbasket.com.bodyfit.analysis;

import android.content.Context;
import android.util.Log;

import fruitbasket.com.bodyfit.Conditions;
import fruitbasket.com.bodyfit.data.DataSet;
import fruitbasket.com.bodyfit.data.DataSetBuffer;
import fruitbasket.com.bodyfit.data.ModelData;
import fruitbasket.com.bodyfit.data.SelectedDataSet;


public class SingleExerciseAnalysis implements ExerciseAnalysis {
    private static final String TAG="SingleExerciseAnalysis";

    private Context context;
    private SelectedDataSet selectedDataSet;
    private DataSetBuffer dataBuffer;  //用于收集存储单个完整动作的数据

    private int currentTimes=-1;   //指示用户当前做了多少个健身动作
    private int Nsamples=0;   //传进来的数组的长度
    private final int span=Conditions.MID_SPAN;     //滑动均值滤波的区间
    private  static double Avrthreshold1=0;    //平均值阈值1
    private  static double Avrthreshold2=0;    //平均值阈值2
    private  static double Avrthreshold3=0;    //平均值阈值3
    private static final int MaxSamples=Conditions.MAX_SAMPLES_OF_ACTIONS;     //单个动作中允许的最大samples
    private static final int MinSamples=Conditions.MIN_SAMPLES_OF_ACTIONS;     //单个动作中允许的最小samples
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
    private boolean isEndTimeFirst=true;
    private double start,end,time;
    private final static double INTERVAL_OF_ONE_ACTION=500; //单位ms

    private int SIX_OR_SEVEN=0;//表示属于动作6还是动作7，因为6和7很容易搞混，但在ay数据有很大区别，故以此区分
    private int TWO_OR_SIX=0;   //区分2和6
    private int ONE_OR_TWO=0;  //区分1 2
    private int EIGHT_OR_FOURTEEN=0;    //区分8 14
    private int NUM_OF_ACTION=0;
    private DynamicTimeWarping dtw=new DynamicTimeWarping();

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
    private static final int exercise_num=17;
    private static double[][]ax_mol;
    private static double[][]ay_mol;
    private static double[][]az_mol;
    private static double[][]gx_mol;
    private static double[][]gy_mol;
    private static double[][]gz_mol;
    private static double[][]mx_mol;
    private static double[][]my_mol;
    private static double[][]mz_mol;
    private static double[][]p1_mol;
    private static double[][]p2_mol;
    private static double[][]p3_mol;
    //存储测试数据
    private double ax_test[]=new double[MaxSamples];
    private double ay_test[]=new double[MaxSamples];
    private double az_test[]=new double[MaxSamples];
    private double gx_test[]=new double[MaxSamples];
    private double gy_test[]=new double[MaxSamples];
    private double gz_test[]=new double[MaxSamples];
    private double mx_test[]=new double[MaxSamples];
    private double my_test[]=new double[MaxSamples];
    private double mz_test[]=new double[MaxSamples];
    private double p1_test[]=new double[MaxSamples];
    private double p2_test[]=new double[MaxSamples];
    private double p3_test[]=new double[MaxSamples];
    private double Dist[]=new double[exercise_num];

    //此标志使得程序只读取一次模板数据
    private boolean hasReadModelData=false;

    public SingleExerciseAnalysis(){
        dataBuffer=new DataSetBuffer();
    }

    private void notBeginExercise(){
        Log.i(TAG,"notBeginExercise");
        hasBegin=false;
        hasCollected=false;
        isDoing=false;
    }

    private void doingExercise(){
        Log.i(TAG,"doingExercise");
        hasBegin=true;
        hasCollected=false;
        isDoing=true;
    }

    private void notBeginProcess(){
        Log.i(TAG,"notBeginProcess");
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
//        Log.i(TAG, "isbelongSegments()");

        double avg1,avg2,avg3;
        avg1=absAvg(dataSet.getGxSet());
        avg2=absAvg(dataSet.getGySet());
        avg3=absAvg(dataSet.getGzSet());
//        Log.i(TAG, "avg1=" + avg1 + " avg2=" + avg2 + " avg3=" + avg3);
//        Log.i(TAG, "Avrthreshold1=" + Avrthreshold1 + " Avrthreshold2=" + Avrthreshold2 + " Avrthreshold3=" + Avrthreshold3);
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
            Log.i(TAG,"interval_of_one_action="+time);
            if(time<INTERVAL_OF_ONE_ACTION)
                return true;
        }

        return false;
    }

    /**
     * 从exerciseDataSet中筛选有效的数据,选择需要用到的维度的数据
     */
    private boolean dataSelect(){
        Log.i(TAG,"dataSelect()");
        Log.i(TAG,"beforeSelect,dataBuffer.capacity="+dataBuffer.getCapacity());

        //若收到的一个动作的数据小于MinSamples，则丢弃，即清空dataBuffer
        if(dataBuffer.getCapacity()<MinSamples)
            dataBuffer.clear();

        if(dataBuffer.isEmpty()==false){

            ///筛选指定维度的数据，然后存放在selectedDataSet,筛选呢些维度较好？
            selectedDataSet=new SelectedDataSet(dataBuffer.toDataSet(),0,1,2,3,4,5,6,7,8,9,10,11);
            dataBuffer.clear();
            Log.i(TAG, "afterSelect,dataBuffer.capacity=" + dataBuffer.getCapacity());
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
        Log.i(TAG,"ExerciseRecognition()");
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
            Log.i(TAG,"ax_test[i]="+ax_test[i]+" ay_test[i]="+ay_test[i]+" az_test[i]="+az_test[i]);
        }*/

        if(hasReadModelData==false) {
            if(loadModelData()==true) {
                hasReadModelData = true;
            }
            else{
                Log.e(TAG,"load modelData failure");
                return;
            }
        }

        ///这里需根据selectedDataSet填充算法
        //exerciseType=null;
        double minDis1=10000000.0,minDis2=10000000.0;//记录Dist最小的动作标号
        int minIndex1=1,minIndex2=1;
      for(int i=0;i<exercise_num;i++)
      {
          //第5 6 9 13 17种动作暂时不判断
          if(i==4 || i==5 || i==8 || i==12 || i==16)
              continue;

          Dist[i]=0;
          Dist[i]+=dtw.getDtwValue(ax_mol[i], ax_test);
          Dist[i]+=dtw.getDtwValue(ay_mol[i], ay_test);
          Dist[i]+=dtw.getDtwValue(az_mol[i], az_test);
          Dist[i]+=dtw.getDtwValue(gx_mol[i], gx_test);
          Dist[i]+=dtw.getDtwValue(gy_mol[i], gy_test);
          Dist[i]+=dtw.getDtwValue(gz_mol[i],gz_test);

          Log.i(TAG, "Dist[" + (i+1) + "]=" + Dist[i]);
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
        Log.i(TAG,"ExerciseRecognition,ExerciseType,minIndex1="+minIndex1+" minIndex2="+minIndex2);
        /*以下用于处理容易混淆的两种动作*/
        if(minIndex1==8&&minIndex2==14 || minIndex1==14&&minIndex2==8){
            mz_test=selectedDataSet.getDataByIndex(8);
            EIGHT_OR_FOURTEEN=0;
            double sum=0,avg;
            for(int i=0;i<50;i++){
                sum+=mz_test[i];
            }
            avg=sum/50;
            Log.i(TAG,"ExerciseRecognition,ExerciseType,avg="+avg);
            if(avg>100)
                EIGHT_OR_FOURTEEN=14;
            else
                EIGHT_OR_FOURTEEN=8;

            minIndex1=EIGHT_OR_FOURTEEN;
            minDis1=Dist[EIGHT_OR_FOURTEEN-1];
        }

        setExerciseType(minIndex1);
        setActionNum();
        Log.e(TAG,"ExerciseRecognition,ExerciseType="+minIndex1+" minDis="+minDis1);
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
        //若dataBuffer太长，则说明不是一个标准动作，所以清空
        if(dataBuffer.getCapacity()>MaxSamples) {
            dataBuffer.clear();
            return true;
        }

        if(hasCollected==false){
            filter(dataSet);    //虑噪

            //收集一开始的50组数据作为阈值
            if(hasCollectedStaticData==false) {
                collectStaticData(dataSet);
                return true;
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
        }
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

            if (tempIndex < tempLength) {
                temp1=dataSet.getGxSet();
                temp2=dataSet.getGySet();
                temp3=dataSet.getGzSet();

                for(int i=0;i<temp1.length;i++){
                    gx[tempIndex]=temp1[i];
                    gy[tempIndex]=temp2[i];
                    gz[tempIndex]=temp3[i];
                    tempIndex++;
                }
            } else {
                Avrthreshold1 = absAvg(gx) * 3;
                Avrthreshold2 = absAvg(gy) * 3;
                Avrthreshold3 = absAvg(gz) * 3;
                hasCollectedStaticData = true;
                Log.e(TAG, "ExerciseType ======================================");
            }

    }

    private boolean loadModelData(){
        ModelData model=new ModelData(context);
        model.readModelData();
        ax_mol=model.getAx_mol();
        ay_mol=model.getAy_mol();
        az_mol=model.getAz_mol();
        gx_mol=model.getGx_mol();
        gy_mol=model.getGy_mol();
        gz_mol=model.getGz_mol();
        mx_mol=model.getMx_mol();
        my_mol=model.getMy_mol();
        mz_mol=model.getMz_mol();
        p1_mol=model.getP1_mol();
        p2_mol=model.getP2_mol();
        p3_mol=model.getP3_mol();
        if(ax_mol!=null&&ay_mol!=null&&az_mol!=null&&
                gx_mol!=null&&gy_mol!=null&&gz_mol!=null&&
                mx_mol!=null&&my_mol!=null&&mz_mol!=null&&
                p1_mol!=null&&p2_mol!=null&&p3_mol!=null){
            return true;
        }
        else
            return false;
    }


    private void setExerciseType(int type){
        if(type<1 || type>17){
            Log.e(TAG,"setExerciseType(),enter wrong type,type<1 or typr>17");
            return;
        }
        switch(type){
            case 1:
                exerciseType=ExerciseType.Alternate_Dumbbell_Curl_1;
                break;
            case 2:
                exerciseType=ExerciseType.Cable_Crossovers_2;
                break;
            case 3:
                exerciseType=ExerciseType.Dumbbells_Alternate_Aammer_Curls_3;
                break;
            case 4:
                exerciseType=ExerciseType.Data_4;
                break;
            case 5:
                exerciseType=ExerciseType.Flat_Bench_Barbell_Press_5;
                break;
            case 6:
                exerciseType=ExerciseType.Flat_Bench_Dumbbell_Flye_6;
                break;
            case 7:
                exerciseType=ExerciseType.Bent_Over_Lateral_Raise_7;
                break;
            case 8:
                exerciseType=ExerciseType.Barbell_Bent_Over_Row_8;
                break;
            case 9:
                exerciseType=ExerciseType.Barbell_Neck_After_Bending_9;
                break;
            case 10:
                exerciseType=ExerciseType.Machine_Curls_10;
                break;
            case 11:
                exerciseType=ExerciseType.Pec_Deck_Flye_11;
                break;
            case 12:
                exerciseType=ExerciseType.Instruments_Made_Thoracic_Mobility_12;
                break;
            case 13:
                exerciseType=ExerciseType.Reverse_Grip_Pulldown_13;
                break;
            case 14:
                exerciseType=ExerciseType.One_Arm_Dumbell_Row_14;
                break;
            case 15:
                exerciseType=ExerciseType.Dumbbell_Is_The_Shoulder_15;
                break;
            case 16:
                exerciseType=ExerciseType.Birds_Standing_16;
                break;
            case 17:
                exerciseType=ExerciseType.Sitting_On_Shoulder_17;
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


