package fruitbasket.com.bodyfit.ui;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fruitbasket.com.bodyfit.R;
import fruitbasket.com.bodyfit.bluetooth.BlunoLibrary;

public class ExerciseFragment extends BlunoLibrary {
    public static final String TAG="ExerciseFragment";

    public ExerciseFragment(){

    }
    @SuppressLint("ValidFragment")
    public ExerciseFragment(Context context){
        super(context);
    }
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        return inflater.inflate(R.layout.layout_exercise,container,false);

    }
}
