package fruitbasket.com.bodyfit.ui;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TabWidget;


import java.io.IOException;

import fruitbasket.com.bodyfit.R;
import fruitbasket.com.bodyfit.processor.DataProcessor;
import fruitbasket.com.bodyfit.helper.ExcelHelper;

public class MainActivity extends BaseTabActivity {

    private String TAG="MainActivity";

    private TabWidget mTabWidget;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        setContentView(R.layout.activity_main);
        DataProcessor.context=getApplicationContext();
        startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 0);

        String [] dataLine=new String[]{"Time","accX", "accY", "accZ", "gyrX", "gyrY", "gyrZ"};
        try {
            ExcelHelper.createFileWithHeader(dataLine);
        } catch (IOException e) {}
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.i(TAG, "onPause()");

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode, Intent data){
        if(requestCode==0) {
            if (resultCode == RESULT_OK){
                BluetoothAdapter.getDefaultAdapter().enable();
                initViews();
            }
            else
                finish();
            //else 关闭整个程序
        }

    }


    @Override
    protected TabWidget getTabWidget() {
        return mTabWidget;
    }

    @Override
    protected ViewPager getViewPager() {
        return mViewPager;
    }

    protected void initViews(){
        mTabWidget = (TabWidget) findViewById(R.id.tabWidget_bodyfit);
        mViewPager = (ViewPager) findViewById(R.id.viewpager_bodyfit);
        mTabWidget.setStripEnabled(false);

        Fragment targetFragment=new TargetFragment();
        Fragment exerciseFragment=new ExerciseFragment();
        Fragment profileFragment=new ProfileFragment();

        LayoutInflater inflater=getLayoutInflater();
        Resources resources=getResources();

        //使用setImageResource就不会变形，用setBackgroundResource就会
        ImageView targetTab=(ImageView)inflater.inflate(R.layout.layout_tab,mTabWidget,false);
        targetTab.setImageResource(R.drawable.target_seletor);

        ImageView exerciseTab=(ImageView)inflater.inflate(R.layout.layout_tab,mTabWidget,false);
        exerciseTab.setImageResource(R.drawable.sport_seletor);

        ImageView profileTab=(ImageView)inflater.inflate(R.layout.layout_tab,mTabWidget,false);
        profileTab.setImageResource(R.drawable.profile_seletor);

        addTab(targetTab, targetFragment, "TARGET");
        addTab(exerciseTab,exerciseFragment,"EXERCISE");
        addTab(profileTab, profileFragment, "PROFILE");

        setCurrentTab(1);
       /* //测试代码
        Fragment BluetoothTestFragment=new BluetoothTestFragment();
        TextView testTab=(TextView)inflater.inflate(R.layout.layout_tab,mTabWidget,false);
        testTab.setText("测试");
        addTab(testTab, BluetoothTestFragment, "TEST");;

        setCurrentTab(0);*/
    }

}
