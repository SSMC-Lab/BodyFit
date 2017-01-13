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
    private SingleExerciseScore score;

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
    private double singleScore;

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

    private int NUM_OF_ACTION=0;//做了多少个相同的动作，若动作类型发生变化，则会清0
    private int SIX_OR_SEVEN=-1;
    private DynamicTimeWarping dtw;
    private int exerciseTypeNum=-1;

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
    private static final int exercise_num=Conditions.EXERCISE_NUM;
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
//    private double mx_test[]=new double[MaxSamples];
//    private double my_test[]=new double[MaxSamples];
    private double mz_test[]=new double[MaxSamples];
//    private double p1_test[]=new double[MaxSamples];
//    private double p2_test[]=new double[MaxSamples];
//    private double p3_test[]=new double[MaxSamples];
    private double Dist[]=new double[exercise_num];

    //此标志使得程序只读取一次模板数据
    private boolean hasReadModelData=false;

    public SingleExerciseAnalysis(){
        dataBuffer=new DataSetBuffer();
        dtw=new DynamicTimeWarping();
        score=new SingleExerciseScore();
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
            Log.i(TAG,"interval_of_every_action="+time);
            if(time<INTERVAL_OF_ONE_ACTION)
                return true;
        }

        return false;
    }

    /**
     * 从exerciseDataSet中筛选有效的数据,选择需要用到的维度的数据
     */
    private boolean dataSelect(){
        Log.i(TAG,"beforeSelect,dataBuffer.capacity="+dataBuffer.getCapacity());

        //若收到的一个动作的数据小于MinSamples，则丢弃，即清空dataBuffer
        if(dataBuffer.getCapacity()<MinSamples)
            dataBuffer.clear();

        if(dataBuffer.isEmpty()==false){
            ///筛选指定维度的数据，然后存放在selectedDataSet,
            selectedDataSet=new SelectedDataSet(dataBuffer.toDataSet(),0,1,2,3,4,5,6,7,8,9,10,11,12);
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
        ax_test=selectedDataSet.getDataByIndex(0);//获取测试数据的ax
        ay_test=selectedDataSet.getDataByIndex(1);//获取测试数据的ay
        az_test=selectedDataSet.getDataByIndex(2);//获取测试数据的az
        gx_test=selectedDataSet.getDataByIndex(3);
        gy_test=selectedDataSet.getDataByIndex(4);
        gz_test=selectedDataSet.getDataByIndex(5);

        //将模板数据从asset读取到程序储存
        //只有判断第一个动作之前会执行
        if(hasReadModelData==false) {
            if(loadModelData()==true) {
                hasReadModelData = true;
            }
            else{
                Log.e(TAG,"load modelData failure");
                return;
            }
        }

        /*for(int i=0;i<ax_test.length && i<ax_mol[0].length;i++){
            Log.i(TAG,"ax_test[i]="+ax_test[i]+" ay_test[i]="+ay_test[i]+" az_test[i]="+az_test[i]);
            Log.e(TAG,"ax_mol[i]="+ax_mol[0][i]+" ay_mol[i]="+ay_mol[0][i]+" az_mol[i]="+az_mol[0][i]);
        }*/

        //设置两个最小值的原因是同时找出两个最小值
        //为了区分两个容易搞混的动作
        double minDis1=10000000.0,minDis2=10000000.0;//记录Dist最小的动作标号
        int minIndex1=1,minIndex2=1;
      for(int i=0;i<exercise_num;i++)
      {
          //第3 4 5种,也就是i-1种，动作暂时不判断
          if(i==2 || i==3 || i==4)
              continue;

          Dist[i]=0;
          Dist[i]+=dtw.getDtwValue(ax_mol[i], ax_test);
          Dist[i]+=dtw.getDtwValue(ay_mol[i], ay_test);
          Dist[i]+=dtw.getDtwValue(az_mol[i], az_test);
          Dist[i]+=dtw.getDtwValue(gx_mol[i], gx_test);
          Dist[i]+=dtw.getDtwValue(gy_mol[i], gy_test);
          Dist[i]+=dtw.getDtwValue(gz_mol[i], gz_test);

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
        if(minIndex1==6 && minIndex2==7 || minIndex1==7 && minIndex2==6){
            //处理方法
        }

        exerciseTypeNum=minIndex1;
        setExerciseType(minIndex1);
        setActionNum();
        Log.e(TAG,"ExerciseRecognition,ExerciseType="+minIndex1+" minDis="+minDis1);
    }

    /**
     * 计算单个动作的评分
     * @return
     */
    private void repetitionScore() {
        if(selectedDataSet!=null && exerciseTypeNum>0 && exerciseTypeNum<=exercise_num){
        }
        else{
            Log.e(TAG,"repetitionScore()->selectedDataSet=null");
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
                if((singleScore=ExerciseSpeed())==0) {//速度正常
                    ExerciseRecognition();
                    repetitionScore();
                    setScore();
                    notBeginExercise();
                }
                else if ((singleScore=ExerciseSpeed())==-1){//速度太慢
                    setExerciseType(18);
                }
                else if ((singleScore=ExerciseSpeed())==1){//速度太快
                    setExerciseType(19);
                }
            }
            else{
                notBeginExercise();
            }
        }
    }

    /**
     *返回值代表运动速度是否正常，-1太慢，0正常，1太快
     */
    private int ExerciseSpeed() {
        double []ax=selectedDataSet.getDataByIndex(0);
        double []ay=selectedDataSet.getDataByIndex(1);
        double []az=selectedDataSet.getDataByIndex(2);
        double []time=selectedDataSet.getDataByIndex(12);
        double ax_vm,ay_vm,az_vm,maxSpeed;
        int timeLen=time.length;

        ax_vm=(int)(absAvg(ax)*(1.0/timeLen)*1000);
        ay_vm=(int)(absAvg(ay)*(1.0/timeLen)*1000);
        az_vm=(int)(absAvg(az)*(1.0/timeLen)*1000);

        if(ax_vm>=ay_vm && ax_vm>=az_vm)
            maxSpeed=ax_vm;
        else if(ay_vm>=ax_vm && ay_vm>=az_vm)
            maxSpeed=ay_vm;
        else
            maxSpeed=az_vm;
        //maxSpeed 0-20:太慢，20-35:正常，35-:太快
        //暂时这么区分，后续需要改进才行
        Log.i(TAG,"maxSpeed="+maxSpeed);
        if(maxSpeed<25)
            return -1;
        else if(maxSpeed>20 && maxSpeed<40)
            return 0;
        else
            return 1;
    }

    /**
     *获取一个数组中的最大值
     */
    private int getMaxIndex(double t[]){
        int i,len,maxIndex;
        double max;
        len=t.length;
        max=abs(t[0]);
        maxIndex=0;
        for(i=0;i<len;i++) {
            if(abs(t[i])>max){
                max=abs(t[i]);
                maxIndex=i;
            }
        }
        return maxIndex;
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
        return singleScore;
    }

    /**
     * 绝对值的平均值
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
        model.readModelData();//读取模板数据,下面是返回模板数据
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
        model=null;

        if(ax_mol!=null&&ay_mol!=null&&az_mol!=null&&
                gx_mol!=null&&gy_mol!=null&&gz_mol!=null&&
                mx_mol!=null&&my_mol!=null&&mz_mol!=null&&
                p1_mol!=null&&p2_mol!=null&&p3_mol!=null){
            score.setModelLength(ax_mol);
            score.setModelData(ax_mol,ay_mol,az_mol,gx_mol,gy_mol,gz_mol,
                                mx_mol,my_mol,mz_mol,p1_mol,p2_mol,p3_mol);
            return true;
        }
        else
            return false;
    }


    private void setExerciseType(int type){

        switch(type){
            case 1:
                exerciseType=ExerciseType.Flat_bench_Barbell_Press_1;
                break;
            case 2:
                exerciseType=ExerciseType.Flat_bench_Dumbbell_Flye_2;
                break;
            case 3:
                exerciseType=ExerciseType.Flat_bench_Dumbbell_Press_3;
                break;
            case 4:
                exerciseType=ExerciseType.Incline_Dumbbell_Flye_4;
                break;
            case 5:
                exerciseType=ExerciseType.Reverse_Grid_Pulldown_5;
                break;
            case 6:
                exerciseType=ExerciseType.Machine_Curls_6;
                break;
            case 7:
                exerciseType=ExerciseType.Alternate_Dumbbell_Curl_7;
                break;
            case 8:
                exerciseType=ExerciseType.Pec_Deck_Flye_8;
                break;
            case 9:
                exerciseType=ExerciseType.Incline_Dumbbell_Press_9;
                break;
            case 10:
                exerciseType=ExerciseType.Cable_Crossovers_10;
                break;
            default:
                Log.e(TAG,"SingleExerciseAbalysis->setExerciseType():enter wrong type,type<1 or typr>10");
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


