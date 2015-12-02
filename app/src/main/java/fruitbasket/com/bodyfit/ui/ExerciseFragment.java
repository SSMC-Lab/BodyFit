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

public class ExerciseFragment extends BlunoLibrary {
    public static final String TAG="ExFragment";

    private TextView groupNumber;
    private TextView timesNumber;
    private TextView exerciseType;
    private ToggleButton toggleButton;

    private SourceData[] sourceDatas;
    private Data data;
    private DataBuffer dataBuffer;
    private boolean isDoing=false;

    public ExerciseFragment(){
        this(null);
    }

    @SuppressLint("ValidFragment")
    public ExerciseFragment(Context context){
        super(context);
        dataBuffer =new DataBuffer();
    }


    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.layout_exercise,container,false);
        //initialize Views
        groupNumber =(TextView)view.findViewById(R.id.group_number);
        timesNumber=(TextView)view.findViewById(R.id.times_number);
        exerciseType=(TextView)view.findViewById(R.id.exercise_type);
        toggleButton=(ToggleButton)view.findViewById(R.id.start_doing);

        return view;
    }

    private void startDoing(){
        isDoing=true;
        while(isDoing==true){
            if(super.mBluetoothLeService.isFull()==true){
                sourceDatas=super.mBluetoothLeService.getSourceDataSet();
                if(sourceDatas!=null){
                    //data.fromSourceData(sourceDatas);
                }
            }
        }
    }

    private void stopDoing(){
        isDoing=false;
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
