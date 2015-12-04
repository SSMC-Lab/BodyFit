package fruitbasket.com.bodyfit.processor;

import android.util.Log;

import fruitbasket.com.bodyfit.Conditions;
import fruitbasket.com.bodyfit.bluetooth.BluetoothLeService;
import fruitbasket.com.bodyfit.data.Data;
import fruitbasket.com.bodyfit.data.DataBuffer;
import fruitbasket.com.bodyfit.data.SourceData;

public class ExerciseProcessor {

    private boolean isDoing=false;
    private SourceData[] sourceDatas;
    private Data data;
    private DataBuffer dataBuffer;

    private int[] selectedIndex;
    private double[] selectedDimension1;
    private double[] selectedDimension2;

    private int exerciseType=DataProcessor.INITIAL_EXERCISE_TYPE;
    private int abnormalType=DataProcessor.INITIAL_ABNORMAL_TYPE;

    private double repetitionScore;
    private double[] scores;

    private BluetoothLeService mBluetoothLeService;

    public ExerciseProcessor(BluetoothLeService bluetoothLeService){
        mBluetoothLeService=bluetoothLeService;
        dataBuffer=new DataBuffer();
        data = new Data();
    }

    public void startDoing(){
        isDoing=true;
        while(isDoing==true){
            if(mBluetoothLeService.isFull()==true){
                sourceDatas=mBluetoothLeService.getSourceDataSet();
                if(sourceDatas!=null){
                    data.fromSourceData(sourceDatas);

                    DataProcessor.filter(data, Conditions.MID_SPAN);///
                    if(DataProcessor.isbelongSegments(data)==true){
                        dataBuffer.add(data);
                    }
                    else{
                        if(dataBuffer.isEmpty()==false){
                            dataBuffer.transferData();
                            selectedIndex =DataProcessor.dataSelect(dataBuffer.getDataSet());
                            selectedDimension1=data.getDimensionByIndex(selectedIndex[0]);
                            selectedDimension2=data.getDimensionByIndex(selectedIndex[1]);
                            exerciseType=DataProcessor.activityRecognition(
                                    selectedDimension1,
                                    selectedDimension2,
                                    selectedIndex[0],
                                    selectedIndex[1]);
                            abnormalType=DataProcessor.abnormalDetection(
                                    selectedDimension1,
                                    selectedDimension2,
                                    selectedIndex[0],
                                    selectedIndex[1]);
                            /*repetitionScore=DataProcessor.repetitionScore(
                                    selectedDimension1,
                                    selectedDimension2,
                                    selectedIndex[0],
                                    selectedIndex[1]
                            );*/
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
