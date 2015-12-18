package fruitbasket.com.bodyfit;

import android.os.Environment;

public class Conditions {
    private static final Conditions conditions=new Conditions();

    public static final String APP_FILE_DIR= Environment.getExternalStorageDirectory()+"/BodyFit";

    public static final int MAX_SAMPLE_NUMBER=5;
    public static final int MID_SPAN=MAX_SAMPLE_NUMBER/2+1;

    //Massage.what
    public static final int EXERCISE_TYPE=0x123;

    private Conditions(){}

    public static Conditions getInstance(){
        return conditions;
    }
}
