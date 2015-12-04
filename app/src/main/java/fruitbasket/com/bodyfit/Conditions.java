package fruitbasket.com.bodyfit;

public class Conditions {
    private static final Conditions conditions=new Conditions();

    public static final int MAX_SAMPLE_NUMBER=5;
    public static final int MID_SPAN=MAX_SAMPLE_NUMBER/2+1;

    private Conditions(){}

    public static Conditions getInstance(){
        return conditions;
    }
}
