package fruitbasket.com.bodyfit.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fruitbasket.com.bodyfit.R;

public class ProfileFragment extends Fragment {
    public static final String TAG="ProfileFragment";


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.layout_profile, container, false);


        final SharedPreferences preferences = getActivity().getSharedPreferences("user_info", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();

        ListView list = (ListView)view.findViewById(R.id.profile_list);

        final ArrayList<Map<String,String>> al = refresh();
        final SimpleAdapter adapter = new SimpleAdapter(this.getActivity(),al,
                R.layout.layout_profile_listext,
                new String[]{"info","data"},
                new int[]{R.id.profile_items,R.id.profile_info});

        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:    //modify the nickname
                    {
                        final EditText text = new EditText(getContext());
                        //获取设置过的nickname，如果找不到则用默认的
                        text.setText(preferences.getString("nickname", getContext().getResources().getString(R.string.default_nickname)));
                        new AlertDialog.Builder(getActivity()).
                                setView(text).
                                setTitle("修改昵称").
                                setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        editor.putString("nickname", text.getText().toString());
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

                    case 1:   //modify sex
                        RadioGroup mRadioGroup = new RadioGroup(getContext());
                        RadioButton RB1 = new RadioButton(getContext());
                        RB1.setText("男");

                        RadioButton RB2 = new RadioButton(getContext());
                        RB2.setText("女");

                        mRadioGroup.addView(RB1);
                        mRadioGroup.addView(RB2);
                        mRadioGroup.setOrientation(LinearLayout.HORIZONTAL);
                        mRadioGroup.setGravity(View.TEXT_ALIGNMENT_CENTER);
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setView(LayoutInflater.from(getContext()).inflate(R.layout.layout_profile_sex,null));
                        builder.create().show();
                        break;
                    case 2:  //modify height
                        break;
                    case 3:  //modify weight
                        break;
                }
            }
        });

        return  view;
    }

    private ArrayList<Map<String,String>> refresh(){

        SharedPreferences preferences = getActivity().getSharedPreferences("user_info", Context.MODE_PRIVATE);

        ArrayList<Map<String,String>> listItems = new ArrayList<>();

        String []profile_array =  getResources().getStringArray(R.array.profile_array);
        String []profile_array_default =  getResources().getStringArray(R.array.profile_array_default);


        String getNickname = preferences.getString("nickname", getContext().getResources().getString(R.string.default_nickname));
        String getSex = preferences.getString("sex", "男");
        String getHeight = preferences.getString("height", "0");
        String getWeight = preferences.getString("weight", "0");
        String[] data = new String[]{getNickname,getSex,getHeight,getWeight};

        for(int i=0; i<profile_array.length; i++){
            Map<String,String> mapItems = new HashMap<>();
            mapItems.put("info",profile_array[i]);
            if(getNickname.length()>0)
                mapItems.put("data",data[i]);
            else
                mapItems.put("data",profile_array_default[i]);

            listItems.add(mapItems);
        }
        return listItems;
    }
 }
