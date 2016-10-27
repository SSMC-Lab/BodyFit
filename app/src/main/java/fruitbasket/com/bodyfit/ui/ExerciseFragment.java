package fruitbasket.com.bodyfit.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import fruitbasket.com.bodyfit.Conditions;
import fruitbasket.com.bodyfit.R;
import fruitbasket.com.bodyfit.bluetooth.BluetoothFragment;

public class ExerciseFragment extends BluetoothFragment {
    public static final String TAG="ExerciseFragment";

//    private TextView groupNumber;
    private TextView timesNumber;
    private TextView exerciseType;
    private LinearLayout infoLayout;
    private ToggleButton toggleButton;
    private String type;
    private int num;

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
        infoLayout.setOnClickListener(new LayoutOnClickListener());
        return view;
    }

    private void initView(View view){
        exerciseType=(TextView)view.findViewById(R.id.exercise_type);
        toggleButton=(ToggleButton)view.findViewById(R.id.start_doing);
        timesNumber= (TextView) view.findViewById(R.id.exercise_num);
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
                num=bundle.getInt(Conditions.ACTION_NUM);
                setExerciseType(type);
                setActionNum(num);
                break;

            case Conditions.MESSAGE_ERROR_JSON:
                break;

            default:
        }
    }

    private void setActionNum(int num) {
        timesNumber.setText(num+"");
    }

    private void showExerciseType(int type){
    }

    private void setExerciseType(String type){
        if(type.equals("FLAT_BENCH_BRABELL_PASS_1")){
            exerciseType.setText("平板杠铃卧推_1");
        }
        else if(type.equals("FLAT_BENCH_DUMBBELL_FLYE_2")){
            exerciseType.setText("平板哑铃飞鸟_2");
        }
        else if(type.equals("FLAT_BENCH_DUMBBELL_PRESS_3")){
            exerciseType.setText("平板哑铃卧推");
        }
        else if(type.equals("INCLINE_DUMBBELL_FLYE_4")){
            exerciseType.setText("上斜板哑铃飞鸟");
        }
        else if(type.equals("REVERSE_GRIP_PULLDOWN_5")){
            exerciseType.setText("阔背肌下拉");
        }
        else if(type.equals("MACHINE_GURLS_6")){
            exerciseType.setText("器械弯举_3");
        }
        else if(type.equals("ALTERNATE_DUMBBELL_CURL_7")){
            exerciseType.setText("哑铃交替弯举_4");
        }
        else if(type.equals("PEC_DECK_FLYE_8")){
            exerciseType.setText("器械夹胸_5");
        }
        else if(type.equals("INCLINE_DUMBBEL_PRESS_9")){
            exerciseType.setText("上斜板哑铃卧推_6");
        }
        else if(type.equals("CABLE_CROSSOVERS_10")){
            exerciseType.setText("十字夹胸_7");
        }
        else
            exerciseType.setText("无");
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

    private class LayoutOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            //这里跳转到另一个展示运动信息详情的页面
            Toast.makeText(getContext(),"click on info_layout",Toast.LENGTH_SHORT).show();
        }
    }
}
