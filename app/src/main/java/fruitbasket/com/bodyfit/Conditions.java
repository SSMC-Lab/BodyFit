package fruitbasket.com.bodyfit;

public class Conditions {
    private static final Conditions conditions=new Conditions();

    public static final int MAX_SAMPLE_NUMBER=5;

    private Conditions(){}

    public static Conditions getInstance(){
        return conditions;
    }
}
