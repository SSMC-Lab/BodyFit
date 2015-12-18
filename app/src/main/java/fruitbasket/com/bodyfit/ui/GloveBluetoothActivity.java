package fruitbasket.com.bodyfit.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.Timer;
import java.util.TimerTask;

import fruitbasket.com.bodyfit.R;
import fruitbasket.com.bodyfit.bluetooth.BluetoothService;

public class GloveBluetoothActivity extends Activity {
    private static final String TAG="GloveBluetoothActivity";

    private ToggleButton toggleButtonConnect;
    private TextView itemsPreSecond;
    private TextView runTime,time, ax, ay, az, gx, gy, gz, mx, my, mz, p1, p2, p3;

    private Intent intentToBluetootthService;
    private BluetoothService bluetoothService;

    private Timer timer;
    //private BroadcastReceiver connectionStateReceiver=new ConnectionStateReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate()");
        setContentView(R.layout.glove_bluetooth);
        initView();
        intentToBluetootthService=new Intent(GloveBluetoothActivity.this,BluetoothService.class);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //registerReceiver(connectionStateReceiver, new IntentFilter(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED));
    }

    @Override
    protected void onPause(){
        super.onPause();
        //unregisterReceiver(connectionStateReceiver);
    }

    private void initView(){
        ToggleClickListener toggleClickListener=new ToggleClickListener();

        toggleButtonConnect=(ToggleButton)findViewById(R.id.toggle_button_connect);
        runTime=(TextView)findViewById(R.id.run_time);
        itemsPreSecond = (TextView) findViewById(R.id.items_pre_second);
        time = (TextView) findViewById(R.id.time);
        ax = (TextView) findViewById(R.id.ax);
        ay = (TextView) findViewById(R.id.ay);
        az = (TextView) findViewById(R.id.az);
        gx = (TextView) findViewById(R.id.gx);
        gy = (TextView) findViewById(R.id.gy);
        gz = (TextView) findViewById(R.id.gz);
        mx = (TextView) findViewById(R.id.mx);
        my = (TextView) findViewById(R.id.my);
        mz = (TextView) findViewById(R.id.mz);
        p1 = (TextView) findViewById(R.id.p1);
        p2 = (TextView) findViewById(R.id.p2);
        p3 = (TextView) findViewById(R.id.p3);

        toggleButtonConnect.setOnClickListener(toggleClickListener);
    }

    private void showBluetootListDialog(){
        if(bluetoothService!=null){
            new AlertDialog.Builder(this).setTitle("Bluetooth List")
                    .setAdapter(bluetoothService.getArrayAdapter(), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, final int which) {
                            bluetoothService.stopDiscovery();

                            new Thread(new Runnable(){

                                @Override
                                public void run() {
                                    bluetoothService.connectToDevice(bluetoothService.getDeviceArrayList().get(which).getAddress());
                                }
                            }).start();
                            ///应使用广播
                            startTimeCounter();

                        }
                    }).setOnCancelListener(new DialogInterface.OnCancelListener() {

                @Override
                public void onCancel(DialogInterface arg0) {
                    bluetoothService.stopDiscovery();
                }
            }).show();
        }
    }

    private void startTimeCounter(){
        timer=new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int counter=0;
            @Override
            public void run() {
                ++counter;
                runTime.post(new Runnable() {
                    @Override
                    public void run() {
                        runTime.setText(String.valueOf(counter));
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


    private ServiceConnection serviceConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "onServiceConnected()");
            bluetoothService=((BluetoothService.MyBinder)service).getService();
            bluetoothService.setHandler(handler);
            showBluetootListDialog();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG,"onServiceDisconnected()");
            bluetoothService=null;
        }
    };

    final Handler handler= new Handler() {
        private Bundle bundle;
        public void handleMessage(Message msg) {
            Log.i(TAG,"handleMessage()");
            bundle=msg.getData();
            switch(msg.what){
                case 0x123:
                    //Log.d(TAG,"items_pre_second=="+msg.getData().getDouble("items_pre_second"));
                    itemsPreSecond.setText(String.valueOf(bundle.getDouble("items_pre_second")));
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

                default:
            }
        }
    };

    private class ToggleClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.toggle_button_connect:
                    if(((ToggleButton)view).isChecked()==true){
                        GloveBluetoothActivity.this.bindService(intentToBluetootthService, serviceConnection, Context.BIND_AUTO_CREATE);
                        //startTimeCounter();
                    }
                    else{
                        GloveBluetoothActivity.this.unbindService(serviceConnection);
                        stopTimeCounter();
                    }
                    break;
            }
        }
    }

    /*private class ConnectionStateReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG,"onReceive()");
            int state=intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE,-1);
            Log.i(TAG,"state=="+state);
            switch(state){
                case BluetoothAdapter.STATE_CONNECTED:
                    startTimeCounter();
                    break;

                case BluetoothAdapter.STATE_DISCONNECTED:
                    stopTimeCounter();
                    break;

                default:
            }
        }
    }*/

}