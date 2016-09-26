package fruitbasket.com.bodyfit;

import android.os.Environment;

public class Conditions {
    private static final Conditions conditions=new Conditions();

    public static final String APP_FILE_DIR= Environment.getExternalStorageDirectory()+"/BodyFit";

    public static final int MAX_SAMPLE_NUMBER=5;
    public static final int MID_SPAN=MAX_SAMPLE_NUMBER/2+1;

    public static final int NUM_PRE_EXERCISE=15;    //规定每组动作的最大次数
    public static final double VALUE_OF_VARTHRESHOLD=0.002;   //判断切割时方差的阈值
    public static final double VALUE_OF_AVRTHRESHOLD=0.15+0.8;   //判断切割时平均值的阈值
    public static final int MAX_SAMPLES_OF_ACTIONS=1000; //单个动作中最大的samples数

    //Massage.what
    public static final int MESSAGE_BLUETOOTH_TEST =0x100;
    public static final int MESSAGE_ERROR_JSON =0x101;
    public static final int MESSAGE_EXERCISE_TYPE=0x102;
    public static final int MESSAGE_EXERCESE_STATUS=0x103;

    //Bundle keys
    public static final String JSON_KEY_ITEMS_PRE_SECOND ="items_pre_second";
    public static final String JSON_KEY_JOSNERROR ="error_json_string";
    public static final String JSON_KEY_EXERCISE_TYPE="exercise_type";
    public static final String REPETITION_SCORE="repetition_score";
    public static final String SET_SCORE="set_score";

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
