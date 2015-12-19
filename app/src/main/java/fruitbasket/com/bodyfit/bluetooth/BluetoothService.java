package fruitbasket.com.bodyfit.bluetooth;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;

import fruitbasket.com.bodyfit.Conditions;
import fruitbasket.com.bodyfit.R;
import fruitbasket.com.bodyfit.data.SourceDataSet;
import fruitbasket.com.bodyfit.data.SourceDataUnit;
import fruitbasket.com.bodyfit.helper.JSONHelper;

public class BluetoothService extends Service {

    private static final String TAG="BluetoothService";
    private static final UUID MY_UUID=UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothAdapter bluetoothAdapter;
    private ArrayList<BluetoothDevice> deviceArrayList;
    private ArrayList<String> deviceNameList;///
    private ArrayAdapter arrayAdapter;

    private String bluetoothAddress;
    private BluetoothSocket bluetoothSocket;
    private Thread bluetoothDataReadThread;

    private Handler handler;

    public BluetoothService(){}

    @Override
    public void onCreate(){
        super.onCreate();
        Log.i(TAG, "onCreate()");
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        deviceArrayList=new ArrayList<BluetoothDevice>();
        deviceNameList=new ArrayList<String>();
        arrayAdapter=new ArrayAdapter<String>(this,
                R.layout.bluetooth_device_list_item, deviceNameList);
    }

    @Override
    public void onDestroy(){
        Log.i(TAG, "onDestroy()");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind()");
        turnOnBluetooth();
        startDiscovery();
        return new MyBinder();
    }

    @Override
    public boolean onUnbind(Intent intent){
        Log.i(TAG, "onUnbind()");
        disconnectToDevice();
        stopDiscovery();
        turnOffBluetooth();
        return super.onUnbind(intent);
    }

    public ArrayList<BluetoothDevice> getDeviceArrayList(){
        return deviceArrayList;
    }

    public ArrayAdapter getArrayAdapter(){
        return arrayAdapter;
    }

    public void setHandler(Handler handler){
        this.handler=handler;
    }

    public void setBluetoothAddress(String bluetoothAddress){
        this.bluetoothAddress=bluetoothAddress;
    }

    public String getBluetoothAddress(){
        return bluetoothAddress;
    }

    public void turnOnBluetooth(){
        Log.d(TAG, "turnOnBluetooth()");
        if(bluetoothAdapter==null){
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        if(bluetoothAdapter.isEnabled()==false) {
            ///应修改提示
            //请求用户开启蓝牙。
            Intent intent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    public void turnOffBluetooth(){
        Log.d(TAG,"turnOffBluetooth()");
        if(bluetoothAdapter!=null && bluetoothAdapter.isEnabled()==false){
            bluetoothAdapter.disable();
        }
    }

    public void startDiscovery(){
        Log.d(TAG, "startDiscovery()");
        if(bluetoothAdapter.isEnabled()==true && bluetoothAdapter.isDiscovering()==false)
        {
            deviceArrayList.clear();
            deviceNameList.clear();
            arrayAdapter.notifyDataSetChanged();

            registerReceiver(discoveryResult, new IntentFilter(
                    BluetoothDevice.ACTION_FOUND));
            bluetoothAdapter.startDiscovery();
        }
    }

    public void stopDiscovery(){
        if(bluetoothAdapter.isDiscovering()==true){
            bluetoothAdapter.cancelDiscovery();
            unregisterReceiver(discoveryResult);
        }

    }

    public void connectToDevice(){
        if(bluetoothAddress!=null){
            final BluetoothDevice device = bluetoothAdapter.getRemoteDevice(this.bluetoothAddress);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        bluetoothSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
                        bluetoothSocket.connect();
                        Log.d(TAG, "bluetoothSocket.connect()");
                        //donot close the socket
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    finally {
                        readData();
                    }
                }
            }).start();
        }
        else{
            Log.e(TAG, "bluetoothAddress==null.at connectToDevice()");
        }
    }

    public void connectToDevice(String bluetoothAddress){
        this.bluetoothAddress=bluetoothAddress;
        connectToDevice();
    }

    public void disconnectToDevice(){
        Log.i(TAG,"disconnectToDevice()");
        if(bluetoothDataReadThread!=null){
            bluetoothDataReadThread.interrupt();
            bluetoothDataReadThread=null;
        }
        ///
        /*if(bluetoothSocket!=null){
            if(bluetoothSocket.isConnected()==true){
                try {
                    bluetoothSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            bluetoothSocket=null;
        }*/
    }

    public void readData(){
        //从蓝牙中读取数据
        if(handler!=null
                &&bluetoothSocket.isConnected()==true){
            bluetoothDataReadThread=new Thread(new BluetoothDataReadTask(bluetoothSocket,handler));
            bluetoothDataReadThread.start();
        }
        else{
            Log.e(TAG, "readData() error");
        }
    }



    public class MyBinder extends Binder {
        public BluetoothService getService(){
            return BluetoothService.this;
        }
    }

    private BroadcastReceiver discoveryResult = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String name = intent.getStringExtra(BluetoothDevice.EXTRA_NAME);
            Log.d(TAG,"discoeried bluetooth device name:"+name);

            if(name!=null){///会接受到空的名字
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                deviceArrayList.add(device);
                deviceNameList.add(name);
                arrayAdapter.notifyDataSetChanged();
            }
        }
    };

    private class BluetoothDataReadTask implements Runnable{
        private static final String TAG="BluetoothDataReadTask";

        private BluetoothSocket bluetoothSocket;
        private Handler handler;
        private Bundle bundle=new Bundle();

        private StringBuilder stringBuilder =new StringBuilder();
        private int startOfLineIndex=-1;
        private int endOfLineIndex=-1;
        private String jsonString;
        private String errorJsonString;

        private int bufferSize=512;
        private byte[] buffer=new byte[bufferSize];
        private int bytesRead=-1;

        private long itemsNumber=0;//记录从蓝牙设备中已经读取的数据条目数量
        private int localItemsNumber=0;//记录从蓝牙设备中已经读取的数据条目数量。但值过大时，它会被清0；
        private double startTime;//记录从蓝牙接收数据的起始时间
        private double localStartTime;//记录从蓝牙接收数据的起始时间，但会定量清0
        private double currentTime;
        private double itemsPreSecond;

        private SourceDataUnit sourceDataUnit;
        private int loadSize=0;
        private SourceDataUnit[] sourceDataUnits=new SourceDataUnit[Conditions.MAX_SAMPLE_NUMBER];
        private SourceDataSet sourceDataSet=new SourceDataSet();

        private BluetoothDataReadTask(BluetoothSocket bluetoothSocket,Handler handler){
            this.bluetoothSocket=bluetoothSocket;
            this.handler=handler;
        }

        @Override
        public void run() {
            try {
                readData();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * 从蓝牙Socket中读取数据
         * @throws IOException
         */
        private void readData() throws IOException {
            startTime =System.currentTimeMillis();
            localStartTime=System.currentTimeMillis();
            if(bluetoothSocket.isConnected()==false){
                bluetoothSocket.connect();
            }
            InputStream input = bluetoothSocket.getInputStream();

            while (Thread.currentThread().isInterrupted() == false) {
                bytesRead = input.read(buffer);
                if (bytesRead != -1) {
                    //Log.d(TAG, "bytesRead==" + bytesRead);
                    //Log.d(TAG, "buffer=="+new String(buffer, 0, bytesRead));
                    stringBuilder.append(new String(buffer, 0, bytesRead));
                    //Log.d(TAG, "stringBuilder==" + stringBuilder.toString());


                    ///此处使用while
                    //获取stringBuffer中的一条json数据
                    startOfLineIndex= stringBuilder.indexOf("{");
                    //Log.d(TAG,"startOfLineIndex=="+startOfLineIndex);
                    ///如果数据发生错乱
                    if(startOfLineIndex != 0){
                        if(startOfLineIndex==-1){
                            sendErrorMessage(
                                    Conditions.MESSAGE_ERROR_JSON,
                                    Conditions.JSON_KEY_JOSNERROR,
                                    "itemsNumber=="+itemsNumber+"\nstartOfLineIndex==-1\n"+
                                            stringBuilder.substring(0, stringBuilder.length()));

                            stringBuilder.delete(0, stringBuilder.length());
                        }
                        else{
                            sendErrorMessage(
                                    Conditions.MESSAGE_ERROR_JSON,
                                    Conditions.JSON_KEY_JOSNERROR,
                                    "itemsNumber==" + itemsNumber + "\nstartOfLineIndex!=-1\n" +
                                            stringBuilder.substring(0, stringBuilder.length()));

                            stringBuilder.delete(0, startOfLineIndex + 1);
                            startOfLineIndex=0;
                        }
                    }

                    endOfLineIndex= stringBuilder.indexOf("}");
                    //Log.d(TAG,"endOfLineIndex=="+endOfLineIndex);
                    if(endOfLineIndex > 0){
                        jsonString = stringBuilder.substring(0, endOfLineIndex + 1);
                        //Log.d(TAG,"jsonString=="+jsonString);
                        stringBuilder.delete(0, endOfLineIndex + 1);

                        try {
                            sourceDataUnit= JSONHelper.parser(jsonString);
                        } catch (JSONException e) {
                            sendErrorMessage(
                                    Conditions.MESSAGE_ERROR_JSON,
                                    Conditions.JSON_KEY_JOSNERROR,
                                    "itemsNumber==" + itemsNumber + "\nJSONException\n" +
                                            jsonString);
                            e.printStackTrace();
                            continue;
                        }
                        if(loadSize<sourceDataUnits.length){
                            sourceDataUnits[loadSize]=sourceDataUnit;
                            ++loadSize;
                        }
                        else{
                            loadSize=0;
                            sourceDataSet.fromSourceData(sourceDataUnits);
                            ///
                        }

                        ++itemsNumber;
                        ++localItemsNumber;

                        currentTime=System.currentTimeMillis();
                        itemsPreSecond=localItemsNumber/((currentTime-localStartTime)/1000);///注意计算结果

                        if(localItemsNumber>500){
                            localItemsNumber=0;
                            localStartTime=currentTime;
                        }

                        //Log.d(TAG,"currentTime=="+currentTime);
                        //Log.d(TAG,"startTime=="+startTime);
                        //Log.d(TAG,"itemsNumber=="+itemsNumber);
                        //Log.d(TAG,"itemsPreSecond=="+itemsPreSecond);

                        Message message=new Message();
                        message.what= Conditions.MESSAGE_BLUETOOTH_TEST;

                        bundle.putDouble(Conditions.JSON_KEY_ITEMS_PRE_SECOND,itemsPreSecond);
                        bundle.putString(Conditions.TIME, sourceDataUnit.getTime());
                        bundle.putDouble(Conditions.AX, sourceDataUnit.getAx());
                        bundle.putDouble(Conditions.AY,sourceDataUnit.getAy());
                        bundle.putDouble(Conditions.AZ,sourceDataUnit.getAz());
                        bundle.putDouble(Conditions.GX,sourceDataUnit.getGx());
                        bundle.putDouble(Conditions.GY,sourceDataUnit.getGy());
                        bundle.putDouble(Conditions.GZ,sourceDataUnit.getGz());
                        bundle.putDouble(Conditions.MX,sourceDataUnit.getMx());
                        bundle.putDouble(Conditions.MY,sourceDataUnit.getMy());
                        bundle.putDouble(Conditions.MZ,sourceDataUnit.getMz());
                        bundle.putDouble(Conditions.P1,sourceDataUnit.getP1());
                        bundle.putDouble(Conditions.P2,sourceDataUnit.getP2());
                        bundle.putDouble(Conditions.P3,sourceDataUnit.getP3());

                        message.setData(bundle);
                        handler.sendMessage(message);

                        startOfLineIndex=-1;
                        endOfLineIndex=-1;
                    }
                    else{
                        Log.d(TAG,"startOfLineIndex != 0 || endOfLineIndex <= 0");
                    }
                }
            }
            input.close();///close()本身也会引发异常
            bluetoothSocket.close();
        }

        /**
         *
         * @param what
         * @param key
         * @param value
         */
        private void sendErrorMessage(int what,String key,String value){
            Bundle bundle=new Bundle();
            bundle.putString(key, value);
            Message errorMessage=new Message();
            errorMessage.what=what;
            errorMessage.setData(bundle);
            handler.sendMessage(errorMessage);
        }
    }
}
