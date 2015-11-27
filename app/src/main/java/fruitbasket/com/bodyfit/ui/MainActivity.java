package fruitbasket.com.bodyfit.ui;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import fruitbasket.com.bodyfit.R;
import fruitbasket.com.bodyfit.bluetooth.Bluetooth;
import fruitbasket.com.bodyfit.helper.NativeHelper;

public class MainActivity extends BaseTabActivity {

    private String TAG="MainActivity";

    private TabWidget mTabWidget;
    /*private FrameLayout mLeftContext;
    private FrameLayout mRightContext;*/
    private ViewPager mViewPager;

    private Bluetooth bluetooth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        setContentView(R.layout.activity_main);

        initViews();
        bluetooth = new Bluetooth(this);
        //double[] array={1.0,2.0};
        //Toast.makeText(this, "boolean=" + NativeHelper.isbelongSegments(array), Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
        bluetooth.unregisterBluetoothReceiver();
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.d(TAG, "onPause()");
        bluetooth.unregisterBluetoothReceiver();
    }

    protected void initViews(){
        mTabWidget = (TabWidget) findViewById(R.id.tabWidget_bodyfit);

        /*mLeftContext = (FrameLayout) findViewById(R.id.contextbar_left);
        mRightContext = (FrameLayout) findViewById(R.id.contextbar_right);*/

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

    @Override
    protected TabWidget getTabWidget() {
        return mTabWidget;
    }
    
    /*@Override
    protected View getLeftContext() {
        return mLeftContext;
    }

    @Override
    protected View getRighttContext() {
        return mRightContext;
    }*/

    @Override
    protected ViewPager getViewPager() {
        return mViewPager;
    }
}
