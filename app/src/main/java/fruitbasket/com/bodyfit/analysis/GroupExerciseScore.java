package fruitbasket.com.bodyfit.analysis;

import android.util.Log;

import fruitbasket.com.bodyfit.data.DataSet;
import fruitbasket.com.bodyfit.data.DataSetBuffer;
import fruitbasket.com.bodyfit.data.SelectedDataSet;

/**
 * Created by Administrator on 2016/11/21.
 */
public class GroupExerciseScore {
    public static final String TAG="GroupExerciseScore";

    /////beginActionIndex,finishActionIndex要在一组结束后重置为0
    private int beginActionIndex=0; //在数组中，每一个开始做动作的指针
    private int beginArray[];
    private int finishActionIndex=0;    //在数组中，每一个结束做动作的指针
    private int finishArray[];

    private DataSetBuffer dataBuffer;   //储存运动数据
    private SelectedDataSet selectedData;   //选择需要的维度之后储存

    private DynamicTimeWarping dtw;     //模式匹配算法类对象

    GroupExerciseScore(){
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

    public add
}
