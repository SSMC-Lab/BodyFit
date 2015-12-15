package fruitbasket.com.bodyfit.data;

import java.util.ArrayList;

/**
 * 用于缓冲收集到的数据。缓冲的过程应该要更优化。
 */
public class DataSetBuffer {
    private ArrayList<SourceDataSet> buffer;//数据缓冲区

    private SourceDataSet sourceDataSet;

    public DataSetBuffer(){
        buffer =new ArrayList<SourceDataSet>();
    }

    /**
     * 若新采集到的数据需暂存，则将数据保存到缓冲区
     */
    public void add(SourceDataSet sourceDataSet){
        if(sourceDataSet!=null){
            buffer.add(sourceDataSet);
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

    /**
     * 转换缓冲区的数据到数据集
     */
    public void transferBuffer(){
        if(buffer==null||buffer.isEmpty()==true){
            return;
        }
        else{
            int bufferSize = buffer.size();
            int totalSize = 0;
            for(int i = 0; i<bufferSize; i++){
                totalSize += buffer.get(i).size();
            }

            SourceDataUnit[] sourceDataUnit = new SourceDataUnit[totalSize];
            int count = 0;
            for(int i = 0; i<bufferSize; i++){
                SourceDataSet item = buffer.get(i);
                for(int j = 0; j<item.size(); j++){
                    sourceDataUnit[count++] = new SourceDataUnit(
                            null,
                            item.getAxSet()[j],
                            item.getAySet()[j],
                            item.getAzSet()[j],
                            item.getGxSet()[j],
                            item.getGySet()[j],
                            item.getGzSet()[j]);
                }
            }

            sourceDataSet = new SourceDataSet(sourceDataUnit);
            buffer.clear();
        }
    }

    public SourceDataSet getSourceDataSet(){
        return sourceDataSet;
    }
}
