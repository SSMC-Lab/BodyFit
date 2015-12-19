package fruitbasket.com.bodyfit.data;

/**
 * 用于存储多条记录。此类会将多条记录中同一维度数据整合到一起。
 */
public class SourceDataSet {
    public static final String TAG="SourceDataSet";
    public static final int DIMENSION=6;

    private int size;

    private double[] axSet;
    private double[] aySet;
    private double[] azSet;
    private double[] gxSet;
    private double[] gySet;
    private double[] gzSet;

    public SourceDataSet(){
    }

    public SourceDataSet(SourceDataUnit[] sourceDatas){
        fromSourceData(sourceDatas);
    }

    public int size(){return size;}

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

    public double[] getDimensionByIndex(int index){
        switch(index){
            case 0:
                return axSet;
            case 1:
                return aySet;
            case 2:
                return azSet;
            case 3:
                return gxSet;
            case 4:
                return gySet;
            case 5:
                return gzSet;
            default:
                return null;
        }
    }

    public void fromSourceData(SourceDataUnit[] sourceDatas){
        size=sourceDatas.length;

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
