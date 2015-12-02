package fruitbasket.com.bodyfit.data;

public class Data {
    public static final String TAG="Data";

    private double[] axSet;
    private double[] aySet;
    private double[] azSet;
    private double[] gxSet;
    private double[] gySet;
    private double[] gzSet;

    public Data (){

    }

    public Data(SourceData[] sourceDatas){
        axSet=new double[sourceDatas.length];
        aySet=new double[sourceDatas.length];
        azSet=new double[sourceDatas.length];
        gxSet=new double[sourceDatas.length];
        gySet=new double[sourceDatas.length];
        gzSet=new double[sourceDatas.length];

        for(int i=0;i<sourceDatas.length;++i){
            axSet[i]=sourceDatas[i].getAx();
            aySet[i]=sourceDatas[i].getAy();
            azSet[i]=sourceDatas[i].getAz();
            gxSet[i]=sourceDatas[i].getGx();
            gySet[i]=sourceDatas[i].getGy();
            gzSet[i]=sourceDatas[i].getGz();
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

    public void fromSourceData(SourceData[] sourceDatas){
        axSet=new double[sourceDatas.length];
        aySet=new double[sourceDatas.length];
        azSet=new double[sourceDatas.length];
        gxSet=new double[sourceDatas.length];
        gySet=new double[sourceDatas.length];
        gzSet=new double[sourceDatas.length];

        for(int i=0;i<sourceDatas.length;++i){
            axSet[i]=sourceDatas[i].getAx();
            aySet[i]=sourceDatas[i].getAy();
            azSet[i]=sourceDatas[i].getAz();
            gxSet[i]=sourceDatas[i].getGx();
            gySet[i]=sourceDatas[i].getGy();
            gzSet[i]=sourceDatas[i].getGz();
        }
    }
}
