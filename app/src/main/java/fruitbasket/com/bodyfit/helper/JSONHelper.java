package fruitbasket.com.bodyfit.helper;

import org.json.JSONException;
import org.json.JSONObject;

import fruitbasket.com.bodyfit.data.SourceData;

public class JSONHelper {
    private static final JSONHelper jsonHelper=new JSONHelper();

    private JSONHelper(){}

    public static JSONHelper getInstance(){
        return jsonHelper;
    }

    public static SourceData parser(String jsonString)
            throws JSONException {
        JSONObject jsonObject=new JSONObject(jsonString);
        SourceData sourceData=new SourceData(
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