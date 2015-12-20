package fruitbasket.com.bodyfit.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Timer;
import java.util.TimerTask;

import fruitbasket.com.bodyfit.Conditions;
import fruitbasket.com.bodyfit.R;
import fruitbasket.com.bodyfit.bluetooth.BluetoothFragment;

public class BluetoothTestFragment extends BluetoothFragment {
    public static final String TAG="BluetoothTestFragment";

    private ToggleButton toggleButtonConnect;
    private TextView itemsPreSecond;
    private TextView runTime,time, ax, ay, az, gx, gy, gz, mx, my, mz, p1, p2, p3,errorMessages;

    private Timer timer;
    private int errorStringNumber=0;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate()");
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        Log.i(TAG, "onCreateView()");
        View view=inflater.inflate(R.layout.glove_bluetooth,container,false);
        initView(view);
        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.i(TAG, "onResume()");
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.i(TAG, "onPause()");
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.i(TAG, "onStop()");
    }

    @Override
    public void onDestroy(){
        Log.i(TAG, "onDestroy()");
        super.onDestroy();
    }

    @Override
    protected void updateUI(int what, Bundle bundle) {
        switch(what){
            case Conditions.MESSAGE_BLUETOOTH_TEST:
                itemsPreSecond.setText(String.valueOf(bundle.getDouble(Conditions.JSON_KEY_ITEMS_PRE_SECOND)));
                time.setText(String.valueOf(bundle.getString("time")));
                ax.setText(String.valueOf(bundle.getDouble("ax")));
                ay.setText(String.valueOf(bundle.getDouble("ay")));
                az.setText(String.valueOf(bundle.getDouble("az")));
                gx.setText(String.valueOf(bundle.getDouble("gx")));
                gy.setText(String.valueOf(bundle.getDouble("gy")));
                gz.setText(String.valueOf(bundle.getDouble("gz")));
                mx.setText(String.valueOf(bundle.getDouble("mx")));
                my.setText(String.valueOf(bundle.getDouble("my")));
                mz.setText(String.valueOf(bundle.getDouble("mz")));
                p1.setText(String.valueOf(bundle.getDouble("p1")));
                p2.setText(String.valueOf(bundle.getDouble("p2")));
                p3.setText(String.valueOf(bundle.getDouble("p3")));
                break;

            case Conditions.MESSAGE_ERROR_JSON:
                ++errorStringNumber;
                errorMessages.append("\n\njson error "+errorStringNumber+" :\n"+String.valueOf(bundle.getString(Conditions.JSON_KEY_JOSNERROR)));

                break;

            default:
        }
    }

    private void initView(View view){
        ToggleClickListener toggleClickListener=new ToggleClickListener();

        toggleButtonConnect=(ToggleButton)view.findViewById(R.id.toggle_button_test_connect);
        runTime=(TextView)view.findViewById(R.id.run_time);
        itemsPreSecond = (TextView) view.findViewById(R.id.items_pre_second);
        time = (TextView) view.findViewById(R.id.time);
        ax = (TextView) view.findViewById(R.id.ax);
        ay = (TextView) view.findViewById(R.id.ay);
        az = (TextView) view.findViewById(R.id.az);
        gx = (TextView) view.findViewById(R.id.gx);
        gy = (TextView) view.findViewById(R.id.gy);
        gz = (TextView) view.findViewById(R.id.gz);
        mx = (TextView) view.findViewById(R.id.mx);
        my = (TextView) view.findViewById(R.id.my);
        mz = (TextView) view.findViewById(R.id.mz);
        p1 = (TextView) view.findViewById(R.id.p1);
        p2 = (TextView) view.findViewById(R.id.p2);
        p3 = (TextView) view.findViewById(R.id.p3);
        errorMessages=(TextView) view.findViewById(R.id.error_messages);

        toggleButtonConnect.setOnClickListener(toggleClickListener);
    }


    private void startTimeCounter(){
        timer=new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int counter=0;
            @Override
            public void run() {
                runTime.post(new Runnable() {
                    @Override
                    public void run() {
                        runTime.setText(String.valueOf(counter++));
                    }
                });
            }
        },0,1000);
    }

    private void stopTimeCounter(){
        if(timer!=null){
            timer.cancel();
            timer=null;
        }
    }





    private class ToggleClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.toggle_button_test_connect:
                    if(((ToggleButton)view).isChecked()==true){
                        startWork();
                        startTimeCounter();
                    }
                    else{
                        stopWork();
                        stopTimeCounter();
                    }
                    break;
            }
        }
    }
}
