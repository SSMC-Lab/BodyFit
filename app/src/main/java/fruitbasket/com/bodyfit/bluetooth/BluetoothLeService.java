/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fruitbasket.com.bodyfit.bluetooth;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import fruitbasket.com.bodyfit.Conditions;
import fruitbasket.com.bodyfit.data.SourceDataUnit;
import fruitbasket.com.bodyfit.utilities.ExcelProcessor;

/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given BluetoothLE device.
 */
public class BluetoothLeService extends Service {

    //
    //Log data function
    //
    int Lognumber = 0;
    String [] LogTime = new String[20];
    ArrayList<double[]> LogData = new ArrayList<>();

    private final static String TAG = BluetoothLeService.class.getSimpleName();
    private int currentLoad=0;
    private SourceDataUnit[] sourceDataSet = new SourceDataUnit[Conditions.MAX_SAMPLE_NUMBER];
    private boolean isFull=false;

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    BluetoothGatt mBluetoothGatt;
    public String mBluetoothDeviceAddress;
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;
    public int mConnectionState = STATE_DISCONNECTED;
    //To tell the onCharacteristicWrite call back function that this is a new characteristic,
    //not the Write Characteristic to the device successfully.
    private static final int WRITE_NEW_CHARACTERISTIC = -1;
    //define the limited length of the characteristic.
    private static final int MAX_CHARACTERISTIC_LENGTH = 250;
    //Show that Characteristic is writing or not.
    private boolean mIsWritingCharacteristic=false;
    //ring buffer
    private RingBuffer<BluetoothGattCharacteristicHelper> mCharacteristicRingBuffer = new RingBuffer<BluetoothGattCharacteristicHelper>(8);
    public final static String ACTION_GATT_CONNECTED =
            "fruitbasket.com.bodyfit.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "fruitbasket.com.bodyfit.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "fruitbasket.com.bodyfit.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "fruitbasket.com.bodyfit.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "fruitbasket.com.bodyfit.le.EXTRA_DATA";

    private final IBinder mBinder = new LocalBinder();





    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        close();
        return super.onUnbind(intent);
    }

    public boolean isFull(){
        return isFull;
    }

    public SourceDataUnit[] getSourceDataSet(){
        if(isFull==true){
            isFull=false;
            return sourceDataSet;
        }
        else{
            return null;
        }
    }

    /**
     * send broadcast when gets a update
     * @param action
     */
    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    /**
     *
     * @param action
     * @param characteristic
     */
    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
            final Intent intent = new Intent(action);
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                intent.putExtra(EXTRA_DATA, new String(data));
        		sendBroadcast(intent);
            }
    }

    /**
     * Initializes a reference to the local Bluetooth adapter.
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
    	System.out.println("BluetoothLeService initialize"+mBluetoothManager);
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }
        return true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     * @param address The device address of the destination device.
     * @return Return true if the connection is initiated successfully. The connection result
     *         is reported asynchronously through the
     *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     *         callback.
     */
    public boolean connect(final String address) {
        System.out.println("BluetoothLeService connect"+address+mBluetoothGatt);
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }
        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        System.out.println("device.connectGatt connect");
		synchronized(this)
		{
			mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
		}
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
    	System.out.println("BluetoothLeService disconnect");
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
    	System.out.println("BluetoothLeService close");
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }
    

    /**
     * Write information to the device on a given {@code BluetoothGattCharacteristic}. The content string and characteristic is 
     * only pushed into a ring buffer. All the transmission is based on the {@code onCharacteristicWrite} call back function, 
     * which is called directly in this function
     * @param characteristic The characteristic to write to.
     */
    public void writeCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        
    	//The character size of TI CC2540 is limited to 17 bytes, otherwise characteristic can not be sent properly,
    	//so String should be cut to comply this restriction. And something should be done here:
        String writeCharacteristicString;
        try {
        	writeCharacteristicString = new String(characteristic.getValue(),"ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            // this should never happen because "US-ASCII" is hard-coded.
            throw new IllegalStateException(e);
        }
        System.out.println("allwriteCharacteristicString:"+writeCharacteristicString);
        
        //As the communication is asynchronous content string and characteristic should be pushed into an ring buffer for further transmission
    	mCharacteristicRingBuffer.push(new BluetoothGattCharacteristicHelper(characteristic,writeCharacteristicString) );
    	System.out.println("mCharacteristicRingBufferlength:"+mCharacteristicRingBuffer.size());


    	//The progress of onCharacteristicWrite and writeCharacteristic is almost the same. So callback function is called directly here
    	//for details see the onCharacteristicWrite function
    	mGattCallback.onCharacteristicWrite(mBluetoothGatt, characteristic, WRITE_NEW_CHARACTERISTIC);
    }    
    
    /**
     * Enables or disables notification on a give characteristic.
     * @param characteristic Characteristic to act on.
     * @param enabled If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;
        return mBluetoothGatt.getServices();
    }




    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        private static final String TAG="BluetoothGattCallback";

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            System.out.println("BluetoothGattCallback----onConnectionStateChange"+newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                Log.i(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                if(mBluetoothGatt.discoverServices())
                {
                    Log.i(TAG, "Attempting to start service discovery:");

                }
                else{
                    Log.i(TAG, "Attempting to start service discovery:not success");

                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
                broadcastUpdate(intentAction);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            System.out.println("onServicesDiscovered "+status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
        {
            //this block should be synchronized to prevent the function overloading
            synchronized(this)
            {
                //CharacteristicWrite success
                if(status == BluetoothGatt.GATT_SUCCESS)
                {
                    System.out.println("onCharacteristicWrite success:"+ new String(characteristic.getValue()));
                    if(mCharacteristicRingBuffer.isEmpty())
                    {
                        mIsWritingCharacteristic = false;
                    }
                    else
                    {
                        BluetoothGattCharacteristicHelper bluetoothGattCharacteristicHelper = mCharacteristicRingBuffer.next();
                        if(bluetoothGattCharacteristicHelper.mCharacteristicValue.length() > MAX_CHARACTERISTIC_LENGTH)
                        {
                            try {
                                bluetoothGattCharacteristicHelper.mCharacteristic.setValue(bluetoothGattCharacteristicHelper.mCharacteristicValue.substring(0, MAX_CHARACTERISTIC_LENGTH).getBytes("ISO-8859-1"));

                            } catch (UnsupportedEncodingException e) {
                                // this should never happen because "US-ASCII" is hard-coded.
                                throw new IllegalStateException(e);
                            }


                            if(mBluetoothGatt.writeCharacteristic(bluetoothGattCharacteristicHelper.mCharacteristic))
                            {
                                System.out.println("writeCharacteristic init "+new String(bluetoothGattCharacteristicHelper.mCharacteristic.getValue())+ ":success");
                            }else{
                                System.out.println("writeCharacteristic init "+new String(bluetoothGattCharacteristicHelper.mCharacteristic.getValue())+ ":failure");
                            }
                            bluetoothGattCharacteristicHelper.mCharacteristicValue = bluetoothGattCharacteristicHelper.mCharacteristicValue.substring(MAX_CHARACTERISTIC_LENGTH);
                        }
                        else
                        {
                            try {
                                bluetoothGattCharacteristicHelper.mCharacteristic.setValue(bluetoothGattCharacteristicHelper.mCharacteristicValue.getBytes("ISO-8859-1"));
                            } catch (UnsupportedEncodingException e) {
                                // this should never happen because "US-ASCII" is hard-coded.
                                throw new IllegalStateException(e);
                            }

                            if(mBluetoothGatt.writeCharacteristic(bluetoothGattCharacteristicHelper.mCharacteristic))
                            {
                                System.out.println("writeCharacteristic init "+new String(bluetoothGattCharacteristicHelper.mCharacteristic.getValue())+ ":success");
                            }else{
                                System.out.println("writeCharacteristic init "+new String(bluetoothGattCharacteristicHelper.mCharacteristic.getValue())+ ":failure");
                            }
                            bluetoothGattCharacteristicHelper.mCharacteristicValue = "";

                            mCharacteristicRingBuffer.pop();
//	            			System.out.print("after pop:");
//	            			System.out.println(mCharacteristicRingBuffer.size());
                        }
                    }
                }
                //WRITE a NEW CHARACTERISTIC
                else if(status == WRITE_NEW_CHARACTERISTIC)
                {
                    if((!mCharacteristicRingBuffer.isEmpty()) && mIsWritingCharacteristic==false)
                    {
                        BluetoothGattCharacteristicHelper bluetoothGattCharacteristicHelper = mCharacteristicRingBuffer.next();
                        if(bluetoothGattCharacteristicHelper.mCharacteristicValue.length() > MAX_CHARACTERISTIC_LENGTH)
                        {

                            try {
                                bluetoothGattCharacteristicHelper.mCharacteristic.setValue(bluetoothGattCharacteristicHelper.mCharacteristicValue.substring(0, MAX_CHARACTERISTIC_LENGTH).getBytes("ISO-8859-1"));
                            } catch (UnsupportedEncodingException e) {
                                // this should never happen because "US-ASCII" is hard-coded.
                                throw new IllegalStateException(e);
                            }

                            if(mBluetoothGatt.writeCharacteristic(bluetoothGattCharacteristicHelper.mCharacteristic))
                            {
                                System.out.println("writeCharacteristic init "+new String(bluetoothGattCharacteristicHelper.mCharacteristic.getValue())+ ":success");
                            }else{
                                System.out.println("writeCharacteristic init "+new String(bluetoothGattCharacteristicHelper.mCharacteristic.getValue())+ ":failure");
                            }
                            bluetoothGattCharacteristicHelper.mCharacteristicValue = bluetoothGattCharacteristicHelper.mCharacteristicValue.substring(MAX_CHARACTERISTIC_LENGTH);
                        }
                        else
                        {
                            try {
                                bluetoothGattCharacteristicHelper.mCharacteristic.setValue(bluetoothGattCharacteristicHelper.mCharacteristicValue.getBytes("ISO-8859-1"));
                            } catch (UnsupportedEncodingException e) {
                                // this should never happen because "US-ASCII" is hard-coded.
                                throw new IllegalStateException(e);
                            }


                            if(mBluetoothGatt.writeCharacteristic(bluetoothGattCharacteristicHelper.mCharacteristic))
                            {
                                System.out.println("writeCharacteristic init "+new String(bluetoothGattCharacteristicHelper.mCharacteristic.getValue())+ ":success");
/*
	            	        	System.out.println((byte)bluetoothGattCharacteristicHelper.mCharacteristic.getValue()[0]);
	            	        	System.out.println((byte)bluetoothGattCharacteristicHelper.mCharacteristic.getValue()[1]);
	            	        	System.out.println((byte)bluetoothGattCharacteristicHelper.mCharacteristic.getValue()[2]);
	            	        	System.out.println((byte)bluetoothGattCharacteristicHelper.mCharacteristic.getValue()[3]);
	            	        	System.out.println((byte)bluetoothGattCharacteristicHelper.mCharacteristic.getValue()[4]);
	            	        	System.out.println((byte)bluetoothGattCharacteristicHelper.mCharacteristic.getValue()[5]);
*/

                            }else{
                                System.out.println("writeCharacteristic init "+new String(bluetoothGattCharacteristicHelper.mCharacteristic.getValue())+ ":failure");
                            }
                            bluetoothGattCharacteristicHelper.mCharacteristicValue = "";

//		            			System.out.print("before pop:");
//		            			System.out.println(mCharacteristicRingBuffer.size());
                            mCharacteristicRingBuffer.pop();
//		            			System.out.print("after pop:");
//		            			System.out.println(mCharacteristicRingBuffer.size());
                        }
                    }

                    mIsWritingCharacteristic = true;

                    //clear the buffer to prevent the lock of the mIsWritingCharacteristic
                    if(mCharacteristicRingBuffer.isFull())
                    {
                        mCharacteristicRingBuffer.clear();
                        mIsWritingCharacteristic = false;
                    }
                }
                else
                //CharacteristicWrite fail
                {
                    mCharacteristicRingBuffer.clear();
                    System.out.println("onCharacteristicWrite fail:"+ new String(characteristic.getValue()));
                    System.out.println(status);
                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                System.out.println("onCharacteristicRead  "+characteristic.getUuid().toString());
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }
        @Override
        public void  onDescriptorWrite(BluetoothGatt gatt,
                                       BluetoothGattDescriptor characteristic,
                                       int status) {
            System.out.println("onDescriptorWrite  " + characteristic.getUuid().toString() + " " + status);
        }

        //写数据
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            String receiveData = new String(characteristic.getValue());
            if(receiveData.length()==20) {
                //排除异常情况
                try{Double.parseDouble(receiveData.substring(1,19));}catch (Exception e){return;}
                //获取数据
                double ax, ay, az, gx, gy, gz;
                ax = dealA(receiveData.substring(1, 3));
                ay = dealA(receiveData.substring(3, 5));
                az = dealA(receiveData.substring(5, 7));
                gx = dealG(receiveData.substring(7, 11));
                gy = dealG(receiveData.substring(11, 15));
                gz = dealG(receiveData.substring(15, 19));



                Log.i(TAG,"ax="+ax+";ay="+ay + ";az=" + az);
                Log.i(TAG, "gx=" + gx + ";gy=" + gy + ";gz=" + gz);
                if(isFull==false&&currentLoad<sourceDataSet.length){
                    sourceDataSet[currentLoad]=new SourceDataUnit(null,ax,ay,az,gx,gy,gz);
                    ++currentLoad;
                }
                else{
                    currentLoad=0;
                    isFull=true;
                }

                //
                //Log data function
                //
                String time = System.currentTimeMillis()+"";
                time = time.substring(5,time.length());
                LogTime[Lognumber]=time;
                LogData.add(new double []{ax,ay,az,gx,gy,gz});
                Lognumber++;


                if(Lognumber == LogTime.length) {
                    Lognumber=0;
                    String APP_FILE_DIR = Environment.getExternalStorageDirectory() + "/SensorData";
                    try {
                        ExcelProcessor.appendDataQuickly(new File(APP_FILE_DIR + "/sixAixs.xlsx"), LogTime, LogData);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    LogData.clear();
                }
            }
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }

        private double dealA(String s){
            int tem = Integer.parseInt(s);
            if(tem<10){
                return (double)tem/10;
            }
            else{
                return -(double)(tem-10)/10;
            }
        }

        private double dealG(String s ){
            int n = Integer.parseInt(s);
            if(n>=1000)
                return -(n-1000);
            return n;
        }
    };

    private class BluetoothGattCharacteristicHelper{
        BluetoothGattCharacteristic mCharacteristic;
        String mCharacteristicValue;
        BluetoothGattCharacteristicHelper(BluetoothGattCharacteristic characteristic, String characteristicValue){
            mCharacteristic=characteristic;
            mCharacteristicValue=characteristicValue;
        }
    }

    public class LocalBinder extends Binder {
        BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }
}
