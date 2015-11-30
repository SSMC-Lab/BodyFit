package fruitbasket.com.bodyfit.data;

public class SourceData {
    private String time;
    private double ax;
    private double ay;
    private double az;
    private double gx;
    private double gy;
    private double gz;

    public SourceData(String time,
                      double ax,
                      double ay,
                      double az,
                      double gx,
                      double gy,
                      double gz){
        this.time=time;
        this.ax=ax;
        this.ay=ay;
        this.az=az;
        this.gx=gx;
        this.gy=gy;
        this.gz=gz;
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
}
