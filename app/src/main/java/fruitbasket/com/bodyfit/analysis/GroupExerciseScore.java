package fruitbasket.com.bodyfit.analysis;

import android.util.Log;

import fruitbasket.com.bodyfit.data.DataSet;
import fruitbasket.com.bodyfit.data.DataSetBuffer;
import fruitbasket.com.bodyfit.data.GroupDataSetBuffer;
import fruitbasket.com.bodyfit.data.SelectedDataSet;

/**
 * Created by Administrator on 2016/11/21.
 */
public class GroupExerciseScore {
    public static final String TAG="GroupExerciseScore";

    GroupDataSetBuffer groupBuffer; //储存一整组的数据，运动+静止+运动+静止+...,第一个动作之前的数据不会储存
    private DataSetBuffer dataBuffer;   //储存运动数据
    private SelectedDataSet selectedData;   //选择需要的维度之后储存
    private DynamicTimeWarping dtw;     //模式匹配算法类对象

    GroupExerciseScore(){
        groupBuffer=new GroupDataSetBuffer();
        dataBuffer=new DataSetBuffer();
        selectedData=new SelectedDataSet();
        dtw=new DynamicTimeWarping();
    }

    public boolean addData(DataSet data){

        if(data==null || data.size()==0){
            Log.e(TAG,"addData()->data=null");
            return false;
        }
        dataBuffer.add(data);
        return true;
    }

    public void finishAction(){
        groupBuffer.add(dataBuffer);
    }

    public void finishStatic(){
        groupBuffer.add(dataBuffer);
    }

}
