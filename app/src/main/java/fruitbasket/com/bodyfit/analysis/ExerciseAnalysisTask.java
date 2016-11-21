package fruitbasket.com.bodyfit.analysis;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import fruitbasket.com.bodyfit.Conditions;

public class ExerciseAnalysisTask implements Runnable {

    private SingleExerciseAnalysis singleAnalysis;
    private GroupExerciseScore groupAnalysis;
    private Handler handler;

    public ExerciseAnalysisTask(SingleExerciseAnalysis analysis,GroupExerciseScore groupAnalysis){
        this.singleAnalysis=analysis;
        this.groupAnalysis=groupAnalysis;
    }

    public ExerciseAnalysisTask(SingleExerciseAnalysis analysis,GroupExerciseScore groupAnalysis,Handler handler){
        this(analysis,groupAnalysis);
        this.handler=handler;
    }

    public void setHandler(Handler handler){
        this.handler=handler;
    }

    public void setContext(Context context){
        singleAnalysis.setContext(context);
    }

    @Override
    public void run() {
        Log.i(this.toString(), "run()");
        singleAnalysis.analysis();

        Message message=new Message();
        message.what=Conditions.MESSAGE_EXERCESE_STATUS;

        Bundle data=new Bundle();
        if(singleAnalysis.getExerciseType()!=null){//判断出运动类型之后传到UI
            data.putString(Conditions.JSON_KEY_EXERCISE_TYPE, singleAnalysis.getExerciseType().toString());
        }
        if(groupAnalysis.getExerciseAssess()!=null){
            data.putString(Conditions.GROUP_EXERCISE_ASSESS,groupAnalysis.getExerciseAssess());
        }
        data.putDoubleArray(Conditions.REPETITION_SCORE, singleAnalysis.getRepetitionScore());
        data.putDouble(Conditions.SET_SCORE, singleAnalysis.getSetScore());
        data.putInt(Conditions.ACTION_NUM,singleAnalysis.getActionNum());

        message.setData(data);
        if(handler!=null){
            //Log.i(this.toString(),"handler!=null");
            handler.sendMessage(message);
        }
        else{
            Log.e(this.toString(), "handler==null");
        }
    }

}
