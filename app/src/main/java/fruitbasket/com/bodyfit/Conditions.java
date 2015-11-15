package fruitbasket.com.bodyfit;

public class Conditions {
    private static final Conditions conditions=new Conditions();

    private Conditions(){}

    public static Conditions getInstance(){
        return conditions;
    }
}
