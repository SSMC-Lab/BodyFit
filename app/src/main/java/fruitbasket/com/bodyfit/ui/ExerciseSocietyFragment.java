package fruitbasket.com.bodyfit.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fruitbasket.com.bodyfit.R;

/**
 * Created by Administrator on 2016/11/10.
 *
 */
public class ExerciseSocietyFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.layout_exercise_society,container,false);

        return view;
    }
}
