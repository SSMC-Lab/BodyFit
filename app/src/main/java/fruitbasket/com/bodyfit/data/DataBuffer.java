package fruitbasket.com.bodyfit.data;

import java.util.ArrayList;

public class DataBuffer {
    private ArrayList<Data> buffer;//数据缓冲区

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
}
