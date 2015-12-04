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

    private int exerciseType=DataProcessor.INITIAL_EXERCISE_TYPE;
    private int abnormalType=DataProcessor.INITIAL_ABNORMAL_TYPE;

    private BluetoothLeService mBluetoothLeService;

    public ExerciseProcessor(BluetoothLeService bluetoothLeService){
        mBluetoothLeService=mBluetoothLeService;
        dataBuffer=new DataBuffer();
    }

    public void startDoing(){
        isDoing=true;
        while(isDoing==true){
            if(mBluetoothLeService.isFull()==true){
                sourceDatas=mBluetoothLeService.getSourceDataSet();
                if(sourceDatas!=null){
                    data.fromSourceData(sourceDatas);
                    DataProcessor.filter(data);
                    if(DataProcessor.isbelongSegments(data)==true){
                        dataBuffer.add(data);
                    }
                    else{
                        if(dataBuffer.isEmpty()==false){

                        }
                    }
                }
            }
        }
    }

    public void stopDoing(){
        isDoing=false;
    }

    public int getExerciseType(){
        return exerciseType;
    }

    public int getAbnormalType(){
        return abnormalType;
    }
}
