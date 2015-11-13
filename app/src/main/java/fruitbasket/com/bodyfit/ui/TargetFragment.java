package fruitbasket.com.bodyfit.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fruitbasket.com.bodyfit.R;

public class TargetFragment extends Fragment {
    public static final String TAG="TargetFragment";

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        return inflater.inflate(R.layout.layout_target,container,false);
    }
}
