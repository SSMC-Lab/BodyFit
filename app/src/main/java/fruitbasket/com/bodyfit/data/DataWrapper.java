package fruitbasket.com.bodyfit.data;

import java.util.ArrayList;

public class DataWrapper {
    private static final String TAG="DataWrapper";

    Data dataFromSource;//存放新采集到的数据
    private ArrayList<Data>  dataBuffer;//数据缓冲区

    public DataWrapper(){
        dataBuffer=new ArrayList<>();
    }

    /**
     * 添加新采集到的数据
     * @param sourceDatas
     */
    public void addSourceDatas(SourceData[] sourceDatas){
        dataFromSource=new Data(sourceDatas);
    }

    /**
     * 丢弃新采集到的数据
     */
    public void deleteSourceData(){
        dataFromSource=null;
    }

    /**
     * 若新采集到的数据需暂存，则将数据保存到缓冲区
     */
    public void addDataToBuffer(){
        if(dataFromSource!=null){
            dataBuffer.add(dataFromSource);
        }
    }

    /**
     * 清空数据缓冲区
     */
    public void clearBuffer(){
        dataBuffer.clear();
    }
}
