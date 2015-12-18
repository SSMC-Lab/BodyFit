package fruitbasket.com.bodyfit;

import android.os.Environment;

public class Conditions {
    private static final Conditions conditions=new Conditions();

    public static final String APP_FILE_DIR= Environment.getExternalStorageDirectory()+"/BodyFit";

    public static final int MAX_SAMPLE_NUMBER=5;
    public static final int MID_SPAN=MAX_SAMPLE_NUMBER/2+1;

    //Massage.what
    public static final int EXERCISE =0x123;

    //json keys
    public static final String TIME="time";
    public static final String AX="ax";
    public static final String AY="ay";
    public static final String AZ="az";
    public static final String GX="gx";
    public static final String GY="gy";
    public static final String GZ="gz";
    public static final String MX="mx";
    public static final String MY="my";
    public static final String MZ="mz";
    public static final String P1="p1";
    public static final String P2="p2";
    public static final String P3="p3";

    private Conditions(){}

    public static Conditions getInstance(){
        return conditions;
    }
}
