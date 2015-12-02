package fruitbasket.com.bodyfit.data;

import java.util.ArrayList;

public class DataBuffer {
    private ArrayList<Data>  dataBuffer;//数据缓冲区

    public DataBuffer(){
        dataBuffer=new ArrayList<>();
    }

    /**
     * 若新采集到的数据需暂存，则将数据保存到缓冲区
     */
    public void addData(Data data){
        if(data!=null){
            dataBuffer.add(data);
        }
    }

    /**
     * 清空数据缓冲区
     */
    public void clearBuffer(){
        dataBuffer.clear();
    }
}
