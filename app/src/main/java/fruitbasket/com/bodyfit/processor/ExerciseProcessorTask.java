package fruitbasket.com.bodyfit.processor;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import fruitbasket.com.bodyfit.Conditions;
import fruitbasket.com.bodyfit.data.DataSetBuffer;
import fruitbasket.com.bodyfit.data.SourceDataSet;

public class ExerciseProcessorTask implements Runnable{

    private static final String TAG="ExerciseProcessorTask";
    private static int objectCounter=0;

    private SourceDataSet sourceDataSet;

    private int[] selectedIndex;
    private double[] selectedDimension1;
    private double[] selectedDimension2;
    private int exerciseType=DataProcessor.INITIAL_EXERCISE_TYPE;
    private int abnormalType=DataProcessor.INITIAL_ABNORMAL_TYPE;
    private double repetitionScore;
    private double[] scores;

    private Handler handler;
    private Bundle bundle=new Bundle();

    public ExerciseProcessorTask(SourceDataSet sourceDataSet){
        this.sourceDataSet=sourceDataSet;
    }

    public ExerciseProcessorTask(SourceDataSet sourceDataSet,Handler handler){
        this(sourceDataSet);
        this.handler=handler;

    }

    public void setHandler(Handler handler){
        this.handler=handler;
    }

    @Override
    public void run() {
        Log.i(TAG, "run()");
        //Log.i(TAG,"objectCounter="+(++objectCounter));
        process();
    }

    private void process(){
        selectedIndex =DataProcessor.dataSelect(sourceDataSet);
        selectedDimension1=sourceDataSet.getDimensionByIndex(selectedIndex[0]);
        selectedDimension2=sourceDataSet.getDimensionByIndex(selectedIndex[1]);

        exerciseType=DataProcessor.activityRecognition(
                selectedDimension1,
                selectedDimension2,
                selectedIndex[0],
                selectedIndex[1]);

        Message message=new Message();
        message.what=Conditions.MESSAGE_EXERCISE_TYPE;
        bundle.putInt(Conditions.JSON_KEY_EXERCISE_TYPE,exerciseType);

        message.setData(bundle);
        //handler.sendMessage(message);
    }
}
