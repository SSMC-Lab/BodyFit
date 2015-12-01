package fruitbasket.com.bodyfit.helper;

import fruitbasket.com.bodyfit.data.SourceData;

public class SourceStringHelper {
    private static final SourceStringHelper sourceStringHelper=new SourceStringHelper();

    private SourceStringHelper(){}

    public SourceStringHelper getInstance(){
        return sourceStringHelper;
    }

    public SourceData parser(String sourceString){
        return null;
    }
}
