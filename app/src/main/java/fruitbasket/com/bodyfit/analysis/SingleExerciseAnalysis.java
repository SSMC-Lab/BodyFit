package fruitbasket.com.bodyfit.analysis;

import android.util.Log;

import java.io.IOException;

import fruitbasket.com.bodyfit.Conditions;
import fruitbasket.com.bodyfit.data.DataSetBuffer;
import fruitbasket.com.bodyfit.data.DataSet;
import fruitbasket.com.bodyfit.data.SelectedDataSet;
import fruitbasket.com.bodyfit.data.StorageData;


public class SingleExerciseAnalysis implements ExerciseAnalysis {
    private final String tag="SingleExerciseAnalysis";

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
    private final int MaxSamples=Conditions.MAX_SAMPLES_OF_ACTIONS;     //单个动作中允许的最大samples--500
    private int firstrun=0;     ///
    private double dataBuf[]=new double[span];  ///会随着firstrun的没设置好而发错误
    private ExerciseType exerciseType;
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

    /*
    1.用户未开始做健身动作：hasBegin=false,hasCollected=false；
    2.用户开始做第一个健身动作，但未做完：hasBegin=true,hasCollected=false;
    3.用户完成了第一个健身动作，但未开始处理刚收集的数据且未开始二个动作：hasBegin=false,hasCollected=true；
    4.用户完成了第一个健身动作，但已开始处理刚收集的数据，而未开始第二个动作：hasBegin=false,hasCollected=false；
     */
    private boolean hasBegin=false; //指示目前是否已经收集完一个完整动作的数据
    private boolean hasCollected=false; //指示目前是否已经收集完一个完整动作的数据

    public SingleExerciseAnalysis(){
        dataBuffer=new DataSetBuffer();
    }

    private void notBeginExercise(){
        hasBegin=false;
        hasCollected=false;
    }

    private void doingExercise(){
        hasBegin=true;
        hasCollected=false;
    }

    private void notBeginProcess(){
        hasBegin=false;
        hasCollected=true;
        setSegmentData();
    }

    private void setSegmentData(){
        isEndTimeFirst=true;
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

        double avg1,avg2,avg3;
        avg1=absAvg(dataSet.getGxSet());
        avg2=absAvg(dataSet.getGySet());
        avg3=absAvg(dataSet.getGzSet());
        Log.i(tag,"avg1="+avg1+" avg2="+avg2+" avg3="+avg3);
        Log.i(tag,"Avrthreshold1="+Avrthreshold1+" Avrthreshold2="+Avrthreshold2+" Avrthreshold3="+Avrthreshold3);
        if(avg1>Avrthreshold1 || avg2>Avrthreshold2 || avg3>Avrthreshold3)
            return true;

        if(avg1<Avrthreshold1 && avg2<Avrthreshold2 && avg3<Avrthreshold3 && hasBegin){
            if(isEndTimeFirst){
                start=System.currentTimeMillis();
                isEndTimeFirst=false;
            }
            end=System.currentTimeMillis();
            time=end-start;
            Log.i(tag,"time="+time);
            if(time<INTERVAL_OF_ONE_ACTION) return true;
        }

        return false;
    }

    /**
     * 从exerciseDataSet中筛选有效的数据,选择需要用到的维度的数据
     */
    private boolean dataSelect(){
        if(dataBuffer.isEmpty()==false){

            ///筛选指定维度的数据，然后存放在selectedDataSet

            dataBuffer.clear();
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
    private void ExerciseRecognition(){
        if(selectedDataSet==null){
            if(dataBuffer.isEmpty()==true){
                return;
            }
            else{
                dataSelect();
            }
        }

        ///这里需根据selectedDataSet填充算法
        //exerciseType=null;
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
        Log.i(this.toString(), "addToSet()");

        //若dataBuffer太长，则说明不是一个标准动作，所以清空
        if(dataBuffer.getCapacity()>MaxSamples)
            dataBuffer.clear();

        if(hasCollected==false){
            filter(dataSet);

            //利用一开始的静止，获取到静止时的平均值，以便于做切分
            if(tempIndex<tempLength){
                temp1=dataSet.getGxSet();
                temp2=dataSet.getGySet();
                temp3=dataSet.getGzSet();

                int i=0;
                for(i=0;i<temp1.length;i++){
                    Log.e(tag,"tempIndex="+tempIndex+"  temp1.length="+temp1.length);
                    gx[tempIndex]=temp1[i];
                    gy[tempIndex]=temp2[i];
                    gz[tempIndex]=temp3[i];
                    tempIndex++;
                }
                return true;
            }else{
                Avrthreshold1=absAvg(gx)*3;
                Avrthreshold2=absAvg(gy)*3;
                Avrthreshold3=absAvg(gz)*3;
            }

            //判断是否符合切割条件，若符合，则存数据，否则不断覆盖数据
            if(isbelongSegments(dataSet)==true){
                dataBuffer.add(dataSet);
                try {
                    out.outputData(dataSet);    //将一个完整动作储存到储存卡
                } catch (IOException e) {
                    e.printStackTrace();
                }
                doingExercise();
                return true;
            }
            else{
                notBeginProcess();
                return false;
            }
        }
        else{
            notBeginProcess();
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

        notBeginExercise();
        dataSelect();
        ExerciseRecognition();
        repetitionScore();
        setScore();
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
            sum+=a[i];
        }
        avg=sum/a.length;
        if(avg>=0)
            return avg;
        return -avg;
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
}
