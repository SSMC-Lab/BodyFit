package fruitbasket.com.bodyfit.bluetooth;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import fruitbasket.com.bodyfit.R;

/**
 * Usage:connecting to the device via bluetooth
 */
public class Bluetooth {
    BluetoothAdapter bluetoothAdapter;
    ArrayList<String> deviceList = new ArrayList<String>();
    myHandler handler;
    Dialog dialog;
    UUID iuuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    BluetoothReceiver mReceiver;
    Context context;
    String deviceName="";

    //initial
    public Bluetooth(Context c){
        this.context = c;
        if(bluetoothAdapter == null)
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        handler  = new myHandler();

        openBluetooth();
        registerReceiver();
    }

    public void openBluetooth(){
        if(!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
        }
        bluetoothAdapter.startDiscovery();

        Set<BluetoothDevice> sDevice = bluetoothAdapter.getBondedDevices();
        Iterator<BluetoothDevice> it = sDevice.iterator();
        while (it.hasNext()) {
            deviceList.add(it.next().getName());
        }
    }

    public void registerReceiver(){
        mReceiver = new BluetoothReceiver();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        context.registerReceiver(mReceiver, filter);
    }

    public void unregisterBluetoothReceiver(){
        context.unregisterReceiver(mReceiver);
    }


    public class BluetoothReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("BluetoothReceiver", "onReceive");
            if(BluetoothDevice.ACTION_FOUND.equals(intent.getAction())){
                String deviceName = intent.getStringExtra(BluetoothDevice.EXTRA_NAME);
                if(deviceList.indexOf(deviceName)==-1){ //如果不存在就添加，防止重复添加
                    deviceList.add(deviceName);
                    new myHandler().sendEmptyMessage(0x123);
                }
            }
        }
    }

    class myHandler extends Handler{
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x123 && deviceName.equals("")) {
                if (dialog != null)
                    dialog.dismiss();
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.layout_bluetooth_receiver, deviceList);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("配对蓝牙选择").setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int position) {
                        deviceName = deviceList.get(position);
                        dialog.cancel();
                        bluetoothAdapter.cancelDiscovery();//取消发现
                        new bluetoothThread().start();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        dialog.cancel();
                    }

                }).create();
                dialog = builder.show();
            }
        }
    }


    class bluetoothThread extends Thread{
        public void run(){
            try {
                Log.i("bluetoothThread", "deviceName" + deviceName);
                BluetoothServerSocket serverSocket  = bluetoothAdapter.listenUsingRfcommWithServiceRecord(deviceName, iuuid);
                Log.i("bluetoothThread", "deviceName" + deviceName);
            }
            catch (Exception e) {Log.e("bluetoothThread","unconnect");}
            try {
            } catch (Exception e) {}
        }
    }
}
