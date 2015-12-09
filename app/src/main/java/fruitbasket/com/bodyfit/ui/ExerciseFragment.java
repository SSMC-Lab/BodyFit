package fruitbasket.com.bodyfit.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import fruitbasket.com.bodyfit.Conditions;
import fruitbasket.com.bodyfit.R;
import fruitbasket.com.bodyfit.bluetooth.BlunoLibrary;
import fruitbasket.com.bodyfit.data.Data;
import fruitbasket.com.bodyfit.data.DataBuffer;
import fruitbasket.com.bodyfit.data.SourceData;
import fruitbasket.com.bodyfit.processor.DataProcessor;

public class ExerciseFragment extends BlunoLibrary {
    public static final String TAG="ExFragment";

    private TextView groupNumber;
    private TextView timesNumber;
    private TextView exerciseType;
    private ToggleButton toggleButton;

    private Handler handler=new Handler();
    private ExerciseProcessorTask task =new ExerciseProcessorTask();

    public ExerciseFragment(){
        this(null);
    }

    @SuppressLint("ValidFragment")
    public ExerciseFragment(Context context){
        super(context);
        Log.i(TAG, "initialize()");
    }


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate()");
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        Log.i(TAG,"onCreateView()");
        View view=inflater.inflate(R.layout.layout_exercise,container,false);
        //initialize Views
        groupNumber =(TextView)view.findViewById(R.id.group_number);
        timesNumber=(TextView)view.findViewById(R.id.times_number);
        exerciseType=(TextView)view.findViewById(R.id.exercise_type);
        toggleButton=(ToggleButton)view.findViewById(R.id.start_doing);

        toggleButton.setOnClickListener(new ToggleClickListener());
        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.i(TAG, "onResume()");
        //super.onResumeProcess();
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.i(TAG, "onPause()");
        //onPauseProcess();
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.i(TAG,"onStop()");
        onStopProcess();
    }

    @Override
    public void onDestroy(){
        super.onStop();
        onDestroyProcess();
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

                        Thread thread=new Thread(task);
                        thread.start();
                    }
                    else{
                        task.stopDoing();
                    }
                    break;
            }
        }
    }

    private class ExerciseProcessorTask implements Runnable {

        private static final String TAG="ExerciseProcessorTask";

        private boolean isDoing=false;
        private SourceData[] sourceDatas;
        private Data data;
        private DataBuffer dataBuffer;

        private int[] selectedIndex;
        private double[] selectedDimension1;
        private double[] selectedDimension2;

        private int exerciseType=DataProcessor.INITIAL_EXERCISE_TYPE;
        private int abnormalType=DataProcessor.INITIAL_ABNORMAL_TYPE;

        private double repetitionScore;
        private double[] scores;

        public ExerciseProcessorTask(){
            data=new Data();
            dataBuffer=new DataBuffer();
        }

        @Override
        public void run() {
            Log.i(TAG,"run()");
            isDoing=true;
            while(isDoing==true){
                Log.i(TAG,"isDoing==true");
                if(mBluetoothLeService.isFull()==true){
                    Log.i(TAG,"mBluetoothLeService.isFull()==true");
                    sourceDatas=mBluetoothLeService.getSourceDataSet();
                    if(sourceDatas!=null){
                        data.fromSourceData(sourceDatas);
                        DataProcessor.filter(data, Conditions.MID_SPAN);
                        if(DataProcessor.isbelongSegments(data)==true){
                            dataBuffer.add(data);
                        }
                        else{
                            if(dataBuffer.isEmpty()==false){
                                dataBuffer.transferData();
                                selectedIndex =DataProcessor.dataSelect(dataBuffer.getDataSet());
                                selectedDimension1=data.getDimensionByIndex(selectedIndex[0]);
                                selectedDimension2=data.getDimensionByIndex(selectedIndex[1]);
                                exerciseType=DataProcessor.activityRecognition(
                                        selectedDimension1,
                                        selectedDimension2,
                                        selectedIndex[0],
                                        selectedIndex[1]);
                                handler.post(new DoUpdateUI(exerciseType));
                                abnormalType=DataProcessor.abnormalDetection(
                                        selectedDimension1,
                                        selectedDimension2,
                                        selectedIndex[0],
                                        selectedIndex[1]);

                                dataBuffer.clear();
                            }
                            else{
                            }
                        }
                    }
                }
                else{
                    Log.i(TAG,"mBluetoothLeService.isFull()==false");
                }
            }
        }

        public void stopDoing(){
            isDoing=false;
        }
    }

    private class DoUpdateUI implements Runnable{

        private int exerciseType=DataProcessor.INITIAL_EXERCISE_TYPE;

        private DoUpdateUI(int exerciseType){
            this.exerciseType=exerciseType;
        }

        @Override
        public void run() {
            //Toast.makeText(getContext(),"type="+exerciseType,Toast.LENGTH_SHORT).show();
            showExerciseType(exerciseType);
        }
    }
}
