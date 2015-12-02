package fruitbasket.com.bodyfit.ui;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import fruitbasket.com.bodyfit.R;
import fruitbasket.com.bodyfit.bluetooth.BlunoLibrary;
import fruitbasket.com.bodyfit.data.SourceData;
import fruitbasket.com.bodyfit.processor.DataProcessor;

public class ExerciseFragment extends BlunoLibrary {
    public static final String TAG="ExFragment";

    private TextView groupumber;
    //private TextView
    private ToggleButton toggleButton;

    public ExerciseFragment(){

    }

    @SuppressLint("ValidFragment")
    public ExerciseFragment(Context context){
        super(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.layout_exercise,container,false);
        //initialize Views
        toggleButton=(ToggleButton)view.findViewById(R.id.start_doing);

        return view;

    }




}
