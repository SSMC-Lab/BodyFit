package fruitbasket.com.bodyfit.ui;

import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TabWidget;
import android.widget.TextView;

import fruitbasket.com.bodyfit.R;

public class MainActivity extends BaseTabActivity {

    private String TAG="MainActivity";

    private TabWidget mTabWidget;
    /*private FrameLayout mLeftContext;
    private FrameLayout mRightContext;*/
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        setContentView(R.layout.activity_main);

        mTabWidget = (TabWidget) findViewById(R.id.tabWidget_xamarket);

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

        addTab(targetTab,targetFragment,"TARGET");
        addTab(exerciseTab,exerciseFragment,"EXERCISE");
        addTab(profileTab,profileFragment,"PROFILE");

        setCurrentTab(1);
    }

    @Override
    protected void onDestroy(){
        Log.d(TAG,"onDestroy()");
        super.onDestroy();
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
