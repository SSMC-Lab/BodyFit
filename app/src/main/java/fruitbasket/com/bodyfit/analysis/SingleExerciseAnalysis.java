package fruitbasket.com.bodyfit.analysis;

import android.util.Log;

import fruitbasket.com.bodyfit.Conditions;
import fruitbasket.com.bodyfit.data.DataSetBuffer;
import fruitbasket.com.bodyfit.data.DataSet;
import fruitbasket.com.bodyfit.data.SelectedDataSet;


public class SingleExerciseAnalysis implements ExerciseAnalysis {
    private SelectedDataSet selectedDataSet;
    private DataSetBuffer dataBuffer;  //用于收集存储单个完整动作的数据

    private int currentTimes=-1;   //指示用户当前做了多少个健身动作
    private ExerciseType exerciseType;
    private double[][] repetitionScore=new double[Conditions.NUM_PRE_EXERCISE][];
    private double setScore;

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
    }

    private double[] filter(double[] sensorDatas,int span){
        //需补充函数体
        return null;
    }

    /**
     * 对数据记录集合进行预处理
     * @param dataSet
     * @return
     */
    private DataSet filter(DataSet dataSet){
        int span=5;
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
     * 识别这些数据记录集合时候属于一个动作中
     * @param dataSet
     * @return
     */
    private boolean isbelongSegments(DataSet dataSet){
        //需补充函数体，返回正确的值
        return false;
    }

    /**
     * 从exerciseDataSet中筛选有效的数据
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
        if(hasCollected==false){
            filter(dataSet);
            if(isbelongSegments(dataSet)==true){
                dataBuffer.add(dataSet);
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
}
