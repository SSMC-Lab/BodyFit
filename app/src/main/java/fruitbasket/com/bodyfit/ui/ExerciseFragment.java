package fruitbasket.com.bodyfit.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import fruitbasket.com.bodyfit.Conditions;
import fruitbasket.com.bodyfit.R;
import fruitbasket.com.bodyfit.bluetooth.BluetoothFragment;

public class ExerciseFragment extends BluetoothFragment {
    public static final String TAG="ExerciseFragment";

    private TextView exerciseNumber;    //显示动作次数
    private TextView exerciseType;  //显示运动类型
    private TextView exerciseTotalNumber;   //累计次数
    private TextView exerciseAssess;
    private LinearLayout infoLayout;
    private ToggleButton toggleButton;  //连接按钮
    private Button selectExercise;

    private String type;    //运动类型
    private int singleNum,oldNum=0;    //运动次数
    private static int totalNum=0;  //累计运动次数
    private double assess;

    private Context context;

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
        initView(view);

        toggleButton.setOnClickListener(new ToggleClickListener());
        infoLayout.setOnClickListener(new myOnClickListener());
        selectExercise.setOnClickListener(new myOnClickListener());
        return view;
    }

     private void initView(View view){
         context=this.getContext();
         exerciseAssess= (TextView) view.findViewById(R.id.exercise_assess);
         exerciseType=(TextView)view.findViewById(R.id.exercise_type);
        toggleButton=(ToggleButton)view.findViewById(R.id.start_doing);
        selectExercise= (Button) view.findViewById(R.id.setExercise);
        exerciseNumber= (TextView) view.findViewById(R.id.exercise_num);
        exerciseTotalNumber= (TextView) view.findViewById(R.id.total_num);
        infoLayout= (LinearLayout) view.findViewById(R.id.info_layout);
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.i(TAG, "onResume()");
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.i(TAG, "onPause()");
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.i(TAG,"onStop()");
    }

    @Override
    public void onDestroy(){
        Log.i(TAG, "onDestroy()");
        super.onDestroy();
    }

    @Override
    protected void updateUI(int what, Bundle bundle) {
        switch(what){
            case Conditions.MESSAGE_BLUETOOTH_TEST:
                break;

            case Conditions.MESSAGE_EXERCESE_STATUS:
                type=String.valueOf(bundle.getString(Conditions.JSON_KEY_EXERCISE_TYPE));
                singleNum=bundle.getInt(Conditions.ACTION_NUM);
                assess=bundle.getDouble(Conditions.SET_SCORE);
                setExerciseAssess();
                setExerciseType(type);
                setActionNum(singleNum);
                break;

            case Conditions.MESSAGE_ERROR_JSON:
                break;

            default:
        }
    }

    private void setActionNum(int num) {
        exerciseNumber.setText(num+"");
        /*if(num!=oldNum || num==1) {
            oldNum=num;
            exerciseTotalNumber.setText((++totalNum) + "");
        }*/
    }
    private void setExerciseAssess(){
        switch((int)assess){
            case 0:
                exerciseAssess.setText(assess+"");
                break;
            case 1:
                exerciseAssess.setText(assess+"");
                break;
            case 2:
                exerciseAssess.setText(assess+"");
                break;
            default:
                exerciseAssess.setText("空");
                break;
        }
    }

    private void setExerciseType(String type){
        if(type.equals("Alternate_Dumbbell_Curl_1")){
            exerciseType.setText(Conditions.exercise_1);
        }
        else if(type.equals("Cable_Crossovers_2")){
            exerciseType.setText(Conditions.exercise_2);
        }
        else if(type.equals("Dumbbells_Alternate_Aammer_Curls_3")){
            exerciseType.setText(Conditions.exercise_3);
        }
        else if(type.equals("Data_4")){
            exerciseType.setText(Conditions.exercise_4);
        }
        else if(type.equals("Flat_Bench_Barbell_Press_5")){
            exerciseType.setText(Conditions.exercise_5);
        }
        else if(type.equals("Flat_Bench_Dumbbell_Flye_6")){
            exerciseType.setText(Conditions.exercise_6);
        }
        else if(type.equals("Bent_Over_Lateral_Raise_7")){
            exerciseType.setText(Conditions.exercise_7);
        }
        else if(type.equals("Barbell_Bent_Over_Row_8")){
            exerciseType.setText(Conditions.exercise_8);
        }
        else if(type.equals("Barbell_Neck_After_Bending_9")){
            exerciseType.setText(Conditions.exercise_9);
        }
        else if(type.equals("Machine_Curls_10")){
            exerciseType.setText(Conditions.exercise_10);
        }
        else if(type.equals("Pec_Deck_Flye_11")){
            exerciseType.setText(Conditions.exercise_11);
        }
        else if(type.equals("Instruments_Made_Thoracic_Mobility_12")){
            exerciseType.setText(Conditions.exercise_12);
        }
        else if(type.equals("Reverse_Grip_Pulldown_13")){
            exerciseType.setText(Conditions.exercise_13);
        }
        else if(type.equals("One_Arm_Dumbell_Row_14")){
            exerciseType.setText(Conditions.exercise_14);
        }
        else if(type.equals("Dumbbell_Is_The_Shoulder_15")){
            exerciseType.setText(Conditions.exercise_15);
        }
        else if(type.equals("Birds_Standing_16")){
            exerciseType.setText(Conditions.exercise_16);
        }
        else if(type.equals("Sitting_On_Shoulder_17")){
            exerciseType.setText(Conditions.exercise_17);
        }
        else if(type.equals("TOO_SLOW")){
            exerciseType.setText(Conditions.too_slow);
        }
        else if(type.equals("TOO_FAST")){
            exerciseType.setText(Conditions.too_fast);
        }
        else
            exerciseType.setText("无动作");
    }



    private class ToggleClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.start_doing:
                    if(((ToggleButton)view).isChecked()==true){
                        startWork();
                    }
                    else{
                        stopWork();
                    }
                    break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==Conditions.EXERCISE_R_CODE && resultCode==0){
            Toast.makeText(context,"return from select exercise type",Toast.LENGTH_SHORT).show();
        }
    }

    private class myOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.info_layout:
                    Toast.makeText(context, "click on info_layout", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.setExercise:
                    Intent intent=new Intent(context,SelectExeActivity.class);
                    startActivityForResult(intent, Conditions.EXERCISE_R_CODE);
                    break;
            }
        }
    }
}
