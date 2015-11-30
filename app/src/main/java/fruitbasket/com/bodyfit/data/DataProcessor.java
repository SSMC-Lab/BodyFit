package fruitbasket.com.bodyfit.data;

import java.util.ArrayList;

public class DataProcessor {
    private static final String TAG="DataProcessor";

    private ArrayList<Data>  dataSet;

    public DataProcessor(){
        dataSet=new ArrayList<>();
    }

    public void addSourceData(SourceData[] sourceDatas){
        Data data=new Data(sourceDatas);
        dataSet.add(data);
    }


}
