package fruitbasket.com.bodyfit.data;

/**
 * 用于存储一条记录
 */
public class SourceDataUnit {
    private String time;
    private double ax;
    private double ay;
    private double az;
    private double gx;
    private double gy;
    private double gz;
    private double mx;
    private double my;
    private double mz;
    private double p1;
    private double p2;
    private double p3;

    public SourceDataUnit(double ax,
                          double ay,
                          double az,
                          double gx,
                          double gy,
                          double gz){
        this.ax=ax;
        this.ay=ay;
        this.az=az;
        this.gx=gx;
        this.gy=gy;
        this.gz=gz;
    }

    public SourceDataUnit(String time,
                          double ax,
                          double ay,
                          double az,
                          double gx,
                          double gy,
                          double gz){
        this(ax,ay,az,gx,gy,gz);
        this.time=time;
    }

    public SourceDataUnit(String time,
                          double ax,
                          double ay,
                          double az,
                          double gx,
                          double gy,
                          double gz,
                          double mx,
                          double my,
                          double mz,
                          double p1,
                          double p2,
                          double p3){
        this(time,ax,ay,az,gx,gy,gz);
        this.mx=mx;
        this.my=my;
        this.mz=mz;
        this.p1=p1;
        this.p2=p2;
        this.p3=p3;
    }

    public String getTime(){
        return time;
    }

    public double getAx(){
        return ax;
    }

    public double getAy(){
        return ay;
    }

    public double getAz(){
        return az;
    }

    public double getGx(){
        return gx;
    }

    public double getGy(){
        return gy;
    }

    public double getGz(){
        return gz;
    }

    public double getMx(){
        return mx;
    }

    public double getMy(){
        return my;
    }

    public double getMz(){
        return mz;
    }

    public double getP1(){
        return p1;
    }

    public double getP2(){
        return p2;
    }

    public double getP3(){
        return p3;
    }
}
