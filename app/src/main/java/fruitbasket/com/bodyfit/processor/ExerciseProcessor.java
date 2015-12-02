package fruitbasket.com.bodyfit.processor;

import fruitbasket.com.bodyfit.bluetooth.BluetoothLeService;
import fruitbasket.com.bodyfit.data.Data;
import fruitbasket.com.bodyfit.data.DataBuffer;
import fruitbasket.com.bodyfit.data.SourceData;

public class ExerciseProcessor {

    private boolean isDoing=false;
    private SourceData[] sourceDatas;
    private Data data;
    private DataBuffer dataBuffer;

    private int exerciseType;
    private int abnormalType;

    private BluetoothLeService mBluetoothLeService;

    public ExerciseProcessor(BluetoothLeService bluetoothLeService){
        mBluetoothLeService=mBluetoothLeService;
    }

    private void startDoing(){
        isDoing=true;
        while(isDoing==true){
            if(mBluetoothLeService.isFull()==true){
                sourceDatas=mBluetoothLeService.getSourceDataSet();
                if(sourceDatas!=null){
                    data.fromSourceData(sourceDatas);
                    //
                }
            }
        }
    }

    private void stopDoing(){
        isDoing=false;
    }

    public int getExerciseType(){
        return exerciseType;
    }

    public int getAbnormalType(){
        return abnormalType;
    }
}
