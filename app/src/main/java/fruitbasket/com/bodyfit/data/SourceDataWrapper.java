package fruitbasket.com.bodyfit.data;

public class SourceDataWrapper {
    public static final String TAG="SourceDataWrapper";

    private SourceData[] dataSet;
    private double[] axSet;
    private double[] aySet;
    private double[] azSet;
    private double[] gxSet;
    private double[] gySet;
    private double[] gzSet;

    public SourceDataWrapper(){}

    public void setDataSet(SourceData[] dataSet){
        this.dataSet=dataSet;
    }

    public SourceData[] getDataSet(){
        return dataSet;
    }

    public void initializeData(){
        if(dataSet==null){
            return;
        }
        else{
            for(int i=0;i<dataSet.length;++i){
                axSet[i]=dataSet[i].getAx();
                aySet[i]=dataSet[i].getAy();
                azSet[i]=dataSet[i].getAz();
                gxSet[i]=dataSet[i].getGx();
                gySet[i]=dataSet[i].getGy();
                gzSet[i]=dataSet[i].getGz();
            }
        }
    }

    public double[] getAxSet(){
        return axSet;
    }

    public double[] getAySet(){
        return aySet;
    }

    public double[] getAzSet(){
        return azSet;
    }

    public double[] getGxSet(){
        return gxSet;
    }

    public double[] getGySet(){
        return gySet;
    }

    public double[] getGzSet(){
        return gzSet;
    }

}
