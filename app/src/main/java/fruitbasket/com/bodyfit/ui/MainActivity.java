package fruitbasket.com.bodyfit.ui;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TabWidget;
import android.widget.TextView;


import java.io.File;
import java.io.IOException;
import java.util.concurrent.locks.Condition;

import fruitbasket.com.bodyfit.R;
import fruitbasket.com.bodyfit.processor.DataProcessor;
import fruitbasket.com.bodyfit.utilities.ExcelProcessor;

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
            ExcelProcessor.createFileWithHeader( dataLine);
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

        TextView targetTab=(TextView)inflater.inflate(R.layout.layout_tab,mTabWidget,false);
        targetTab.setText(resources.getString(R.string.tab_target));

        TextView exerciseTab=(TextView)inflater.inflate(R.layout.layout_tab,mTabWidget,false);
        exerciseTab.setText(resources.getString(R.string.tab_exercise));

        TextView profileTab=(TextView)inflater.inflate(R.layout.layout_tab,mTabWidget,false);
        profileTab.setText(resources.getString(R.string.tab_profile));

        addTab(targetTab, targetFragment, "TARGET");
        addTab(exerciseTab,exerciseFragment,"EXERCISE");
        addTab(profileTab, profileFragment, "PROFILE");

        setCurrentTab(1);
    }

}
