package fruitbasket.com.bodyfit.bluetooth;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
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
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import fruitbasket.com.bodyfit.R;

/**
 * Usage:connecting to the device via bluetooth
 */
public class Bluetooth {
    final private String TAG="Bluetooth";
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

/*
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            Log.i(TAG, "bluetooth "+new String(characteristic.getValue()));
        }
    };*/
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
            Log.i(TAG, "deviceName" + deviceName+" deviceAddress"+chosenAddress);
/*
            //作为服务器端接受
            try {
                InputStream inputStream=null;
                BluetoothSocket socket=null;
                BluetoothServerSocket serverSocket  = bluetoothAdapter.listenUsingRfcommWithServiceRecord(deviceName, iuuid);
                while(inputStream == null){
                    Log.e("waiting","waitingForAccept");
                    socket = serverSocket.accept();
                    Log.e("1","accepted");
                    inputStream = socket.getInputStream();
                }
                for(int i =0;i<50;i++){
                    int count = 0;
                    while (count == 0) {
                        count = inputStream.available();
                    }
                    byte[] b = new byte[count];
                    inputStream.read(b);
                    Log.i("111111",new String(b));
                }
                inputStream.close();
                socket.close();
                serverSocket.close();
            }
            catch (Exception e) {Log.e(TAG,"connectFailed");}*/

/*

           //作为客户端接受
           final BluetoothDevice device = bluetoothAdapter.getRemoteDevice(chosenAddress);
            if (device == null) {
                Log.e(TAG, "Device not found.  Unable to connect.");
                return;
            }
            try {
                BluetoothSocket socket = device.createRfcommSocketToServiceRecord(iuuid);
                bluetoothAdapter.cancelDiscovery();
                Log.i(TAG, "connecting");
                socket.connect();
                Log.i(TAG, "connectedSuccessfully");

                InputStream in = socket.getInputStream();
                   for(int i =0;i<50;i++){
                        int count = 0;
                        while (count == 0) {
                            count = in.available();
                        }
                        byte[] b = new byte[count];
                        in.read(b);
                        Log.i("111111",new String(b));
                    }
            } catch (IOException e) {
                e.printStackTrace();
            }
*/

            /*synchronized(this)
            {
                BluetoothGatt mBluetoothGatt = device.connectGatt(context, false, mGattCallback);
                List<BluetoothGattService> listOfBluetoothGattService = mBluetoothGatt.getServices();

                Log.i(TAG, "connectingSucceeded");
            }*/


        }
    }

}



