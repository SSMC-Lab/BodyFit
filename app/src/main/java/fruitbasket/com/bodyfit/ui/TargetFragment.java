package fruitbasket.com.bodyfit.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fruitbasket.com.bodyfit.R;

public class TargetFragment extends Fragment {
    public static final String TAG="TargetFragment";

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.layout_target, container, false);

        final SharedPreferences preferences = getActivity().getSharedPreferences("user_target", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();

        ListView list = (ListView)view.findViewById(R.id.target_list);
        final ArrayList<Map<String,String>> al = refresh();
        final SimpleAdapter adapter = new SimpleAdapter(this.getActivity(),al,
                R.layout.layout_profileandtarget_listext,
                new String[]{"target","data"},
                new int[]{R.id.profile_items,R.id.profile_info});

        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:    //modify the nickname
                       int action = preferences.getInt("action", 0);
                       final String[] actions = getResources().getStringArray(R.array.action_type);
                        final NumberPicker actionPicker = new NumberPicker(getContext());
                        actionPicker.setMinValue(0);
                        actionPicker.setMaxValue(actions.length-1);
                        actionPicker.setDisplayedValues(actions);
                        actionPicker.setValue(action);

                        new AlertDialog.Builder(getActivity()).
                                setView(actionPicker).
                                setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        editor.putInt("action", actionPicker.getValue());
                                        editor.apply();

                                        al.clear();
                                        al.addAll(refresh());
                                        adapter.notifyDataSetChanged();
                                    }
                                }).
                                setNegativeButton("取消", null).
                                create().show();
                        break;
                    case 1:
                        int sets = preferences.getInt("target_sets",
                                Integer.parseInt(getContext().getResources().getString(R.string.default_target_sets)));

                        final NumberPicker mSetPicker = new NumberPicker(getContext());
                        mSetPicker.setMinValue(0);
                        mSetPicker.setMaxValue(5);
                        mSetPicker.setValue(sets);


                        new AlertDialog.Builder(getActivity()).
                                setView(mSetPicker).
                                setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        editor.putInt("target_sets", mSetPicker.getValue());
                                        editor.apply();

                                        al.clear();
                                        al.addAll(refresh());
                                        adapter.notifyDataSetChanged();
                                    }
                                }).
                                setNegativeButton("取消", null).
                                create().show();
                        break;
                    case 2:
                        int times = preferences.getInt("target_times",
                                Integer.parseInt(getContext().getResources().getString(R.string.default_target_sets)));

                        final NumberPicker mTimePicker = new NumberPicker(getContext());
                        mTimePicker.setMinValue(10);
                        mTimePicker.setMaxValue(15);
                        mTimePicker.setValue(times);

                        new AlertDialog.Builder(getActivity()).
                                setView(mTimePicker).
                                setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        editor.putInt("target_times", mTimePicker.getValue());
                                        editor.apply();

                                        al.clear();
                                        al.addAll(refresh());
                                        adapter.notifyDataSetChanged();
                                    }
                                }).
                                setNegativeButton("取消", null).
                                create().show();
                        break;
                }
            }
        });
        return view;
    }




    private ArrayList<Map<String,String>> refresh(){

        SharedPreferences preferences = getActivity().getSharedPreferences("user_target", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        ArrayList<Map<String,String>> listItems = new ArrayList<>();

        String []target_array =  getResources().getStringArray(R.array.target_array);
        String []target_array_default =  getResources().getStringArray(R.array.target_array_default);


        int getTargetType = preferences.getInt("action", 0);
        String getTargetSets;
        String getTargetTimes;
        if(getTargetType==0){
            editor.putInt("target_sets", 0);
            editor.putInt("target_times", 0);
            editor.apply();

            getTargetSets="0";
            getTargetTimes="0";
        }
        else {
            getTargetSets = String.valueOf(preferences.getInt("target_sets", 0));
            getTargetTimes = String.valueOf(preferences.getInt("target_times", 0));
        }
        String[] data = new String[]{getResources().getStringArray(R.array.action_type)[getTargetType],getTargetSets,getTargetTimes};

        for(int i=0; i<target_array.length; i++){
            Map<String,String> mapItems = new HashMap<>();
            mapItems.put("target",target_array[i]);
            if(getTargetType!=0)
                mapItems.put("data",data[i]);
            else
                mapItems.put("data",target_array_default[i]);

            listItems.add(mapItems);
        }
        return listItems;
    }
}
