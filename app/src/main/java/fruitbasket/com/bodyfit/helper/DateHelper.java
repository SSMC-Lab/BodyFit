package fruitbasket.com.bodyfit.helper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateHelper {
    private static final DateHelper dateHelper=new DateHelper();

    private DateHelper(){}

    public static DateHelper getInstance(){
        return dateHelper;
    }

    public static String getTime(String timeString){
        return new SimpleDateFormat(timeString).format(new Date());
    }
}
