package fruitbasket.com.bodyfit.analysis;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import fruitbasket.com.bodyfit.Conditions;

public class ExerciseAnalysisTask implements Runnable {

    private SingleExerciseAnalysis analysis;
    private Handler handler;

    public ExerciseAnalysisTask(SingleExerciseAnalysis analysis){
        this.analysis=analysis;
    }

    public ExerciseAnalysisTask(SingleExerciseAnalysis analysis,Handler handler){
        this(analysis);
        this.handler=handler;
    }

    public void setHandler(Handler handler){
        this.handler=handler;
    }

    @Override
    public void run() {
        Log.i(this.toString(), "run()");
        analysis.analysis();

        Message message=new Message();
        message.what=Conditions.MESSAGE_EXERCESE_STATUS;

        Bundle data=new Bundle();
        if(analysis.getExerciseType()!=null){
            data.putString(Conditions.JSON_KEY_EXERCISE_TYPE, analysis.getExerciseType().toString());
        }
        data.putDoubleArray(Conditions.REPETITION_SCORE, analysis.getRepetitionScore());
        data.putDouble(Conditions.SET_SCORE, analysis.getSetScore());

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
