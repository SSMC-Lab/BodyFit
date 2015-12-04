package fruitbasket.com.bodyfit.data;

import java.util.ArrayList;

public class DataBuffer {
    private ArrayList<Data> buffer;//数据缓冲区

    private Data dataSet;

    public DataBuffer(){
        buffer =new ArrayList<Data>();
    }

    /**
     * 若新采集到的数据需暂存，则将数据保存到缓冲区
     */
    public void add(Data data){
        if(data!=null){
            buffer.add(data);
        }
    }

    /**
     * 清空数据缓冲区
     */
    public void clear(){
        buffer.clear();
    }

    public boolean isEmpty(){
        return buffer.isEmpty();
    }

    public void transferData(){
        if(buffer==null)
            return;
        int bufferLength = buffer.size();

        int totalLength = 0;
        for(int i = 0; i<bufferLength; i++){
            totalLength += buffer.get(i).getAxSet().length;
        }

        SourceData [] sourceData = new SourceData[totalLength];
        int count = 0;
        for(int i = 0; i<bufferLength; i++){
            Data item = buffer.get(i);
            for(int j = 0; j<item.getAxSet().length; j++){
                sourceData[count++] = new SourceData(
                        null,
                        item.getAxSet()[j],
                        item.getAySet()[j],
                        item.getAzSet()[j],
                        item.getGxSet()[j],
                        item.getGySet()[j],
                        item.getGzSet()[j]);
            }
        }

        dataSet = new Data(sourceData);
        buffer.clear();
    }

    public Data getDataSet(){
        return dataSet;
    }
}
