package fruitbasket.com.bodyfit.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import fruitbasket.com.bodyfit.R;
import fruitbasket.com.bodyfit.bluetooth.BluetoothFragment;

public class ExerciseFragment extends BluetoothFragment {
    public static final String TAG="ExerciseFragment";

    private TextView groupNumber;
    private TextView timesNumber;
    private TextView exerciseType;
    private ToggleButton toggleButton;

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
        Log.i(TAG,"onDestroy()");
        super.onDestroy();
    }

    @Override
    protected void updateUI(int what, Bundle bundle) {

    }

    private void showExerciseType(int type){
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
}
