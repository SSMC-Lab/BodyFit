package fruitbasket.com.bodyfit.bluetooth;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import fruitbasket.com.bodyfit.R;

/**
 * Usage:connecting to the device via bluetooth
 */
public class Bluetooth {
    public static BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    ArrayList<String> deviceList = new ArrayList<>();
    HashMap<String,String> deviceMap = new HashMap<>();
    myHandler handler;
    Dialog dialog;
    UUID iuuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    BluetoothReceiver mReceiver;
    Context context;
    String deviceName="";
    ArrayAdapter<String> adapter;
    //initial
    public Bluetooth(Context c){
        this.context = c;

        adapter = new ArrayAdapter<>(context, R.layout.layout_bluetooth_receiver, deviceList);

        handler  = new myHandler();
        showDialog();
        registerReceiver();
        new startBluetooth().start();

    }


    private void showDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("配对蓝牙选择").setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int position) {
                deviceName = deviceList.get(position);
                dialog.cancel();
                bluetoothAdapter.cancelDiscovery();//取消发现
                unregisterBluetoothReceiver();
                new bluetoothThread().start();
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int position) {
                unregisterBluetoothReceiver();
            }
        }).create();
        dialog = builder.show();
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
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String receiveDeviceName = device.getName();
                String receiveDeviceAddress = device.getAddress();
                dialog.setTitle("刷新中....");
                if(deviceList.indexOf(receiveDeviceName)==-1&&receiveDeviceName!=null&&receiveDeviceAddress!=null){ //如果不存在就添加，防止重复添加
                    deviceList.add(receiveDeviceName);
                    deviceMap.put(receiveDeviceName, receiveDeviceAddress);
                    handler.sendEmptyMessage(0x123);
                }
            }
        }
    }

    class myHandler extends Handler{
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x123 && deviceName.equals("")) {
                adapter.notifyDataSetChanged();
                dialog.setTitle("配对蓝牙选择");
            }
        }
    }


    class startBluetooth extends  Thread{
        public void run(){
            bluetoothAdapter.startDiscovery();

            Set<BluetoothDevice> sDevice = bluetoothAdapter.getBondedDevices();
            Iterator<BluetoothDevice> it = sDevice.iterator();
            while (it.hasNext()) {
                deviceList.add(it.next().getName());
                deviceMap.put(it.next().getName(),it.next().getAddress());
            }
        }
    }
    class bluetoothThread extends Thread{
        public void run(){
            String chosenAddress=deviceMap.get(deviceName);
            Log.i("bluetoothThread", "deviceName" + deviceName+" deviceAddress"+chosenAddress);
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(chosenAddress);
            BluetoothSocket socket=null;
            try {
                socket = device.createRfcommSocketToServiceRecord(iuuid);
                socket.connect();
                InputStream in = socket.getInputStream();

                int i=0;
                while(i<100){
                    Log.i("222","222222222");
                    i++;
                    int count = in.available();
                    byte[] b = new byte[count];
                    in.read(b);
                    Log.i("111111",new String(b));
                }
                in.close();
            } catch (IOException e) {
                 Log.e("inputStream","failed");
            }
            finally {
                try {
                    if(socket!=null)
                        socket.close();
                } catch (IOException e) {
                    Log.e("socket","socket close failed");
                }
            }
        }
    }
}
