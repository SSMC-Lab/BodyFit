package fruitbasket.com.bodyfit.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fruitbasket.com.bodyfit.R;

public class ProfileFragment extends Fragment {
    public static final String TAG="ProfileFragment";

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        return inflater.inflate(R.layout.layout_profile,container,false);
    }
}
