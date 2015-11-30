package fruitbasket.com.bodyfit.data;

public class DataProcessor {
    private static final String TAG="DataProcessor";

    DataWrapper dataWrapper;

    public DataProcessor(Data[] sourceDataSet){
        dataWrapper=new DataWrapper(sourceDataSet);
    }



}
