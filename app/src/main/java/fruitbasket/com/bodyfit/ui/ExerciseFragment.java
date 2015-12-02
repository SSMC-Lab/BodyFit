package fruitbasket.com.bodyfit.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import fruitbasket.com.bodyfit.R;
import fruitbasket.com.bodyfit.bluetooth.BlunoLibrary;
import fruitbasket.com.bodyfit.data.Data;
import fruitbasket.com.bodyfit.data.DataBuffer;
import fruitbasket.com.bodyfit.data.SourceData;
import fruitbasket.com.bodyfit.processor.DataProcessor;
import fruitbasket.com.bodyfit.processor.ExerciseProcessor;

public class ExerciseFragment extends BlunoLibrary {
    public static final String TAG="ExFragment";

    private TextView groupNumber;
    private TextView timesNumber;
    private TextView exerciseType;
    private ToggleButton toggleButton;

    public ExerciseFragment(){
        this(null);
    }

    @SuppressLint("ValidFragment")
    public ExerciseFragment(Context context){
        super(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.layout_exercise,container,false);
        //initialize Views
        groupNumber =(TextView)view.findViewById(R.id.group_number);
        timesNumber=(TextView)view.findViewById(R.id.times_number);
        exerciseType=(TextView)view.findViewById(R.id.exercise_type);
        toggleButton=(ToggleButton)view.findViewById(R.id.start_doing);

        toggleButton.setOnClickListener(new ToggleClickListener());
        return view;
    }

    private void showExerciseType(int type){
        if(type == DataProcessor.FLAT_BENCH_BRABELL_PASS)
            exerciseType.setText("平板杠铃卧推");
        else  if(type == DataProcessor.FLAT_BENCH_DUMBBELL_FLYE)
            exerciseType.setText("平板哑铃飞鸟");
        else  if(type == DataProcessor.FLAT_BENCH_DUMBBELL_PRESS)
            exerciseType.setText("平板哑铃卧推");
        else  if(type == DataProcessor.INCLINE_DUMBBELL_FLYE)
            exerciseType.setText("上斜板哑铃飞鸟");
        else  if(type == DataProcessor.REVERSE_GRIP_PULLDOWN)
            exerciseType.setText("阔背肌下拉");
        else  if(type == DataProcessor.MACHINE_GURLS)
            exerciseType.setText("器械弯举");
        else  if(type == DataProcessor.ALTERNATE_DUMBBELL_CURL)
            exerciseType.setText("哑铃交替弯举");
        else  if(type == DataProcessor.PEC_DECK_FLYE)
            exerciseType.setText("器械夹胸");
        else  if(type == DataProcessor.INCLINE_DUMBBEL_PRESS)
            exerciseType.setText("上斜板哑铃卧推");
        else  if(type == DataProcessor.CABLE_CROSSOVERS)
            exerciseType.setText("十字夹胸");
        else if(type == DataProcessor.INITIAL_EXERCISE_TYPE)
            exerciseType.setText("未设置");
    }



    private class ToggleClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.start_doing:
                    if(((ToggleButton)view).isChecked()==true){

                    }
                    else{

                    }
                    break;
            }
        }
    }

}
