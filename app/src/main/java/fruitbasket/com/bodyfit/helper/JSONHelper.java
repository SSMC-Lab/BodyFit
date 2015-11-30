package fruitbasket.com.bodyfit.helper;

import org.json.JSONException;
import org.json.JSONObject;

import fruitbasket.com.bodyfit.data.Data;

public class JSONHelper {
    private static final JSONHelper jsonHelper=new JSONHelper();

    private JSONHelper(){}

    public static JSONHelper getInstance(){
        return jsonHelper;
    }

    public static Data parser(String jsonString)
            throws JSONException {
        JSONObject jsonObject=new JSONObject(jsonString);
        Data sourceData=new Data(
                jsonObject.getString("time"),
                jsonObject.getDouble("ax"),
                jsonObject.getDouble("ay"),
                jsonObject.getDouble("az"),
                jsonObject.getDouble("gx"),
                jsonObject.getDouble("gy"),
                jsonObject.getDouble("gz")
        );
        return sourceData;
    }
}
