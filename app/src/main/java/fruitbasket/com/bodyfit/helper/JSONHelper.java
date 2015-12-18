package fruitbasket.com.bodyfit.helper;

import org.json.JSONException;
import org.json.JSONObject;

import fruitbasket.com.bodyfit.data.SourceDataUnit;

public class JSONHelper {
    private static final JSONHelper jsonHelper=new JSONHelper();

    private JSONHelper(){}

    public static JSONHelper getInstance(){
        return jsonHelper;
    }

    public static SourceDataUnit parser(String jsonString)
            throws JSONException {
        JSONObject jsonObject=new JSONObject(jsonString);
        SourceDataUnit sourceDataUnit=new SourceDataUnit(
                jsonObject.getString("time"),
                jsonObject.getDouble("ax"),
                jsonObject.getDouble("ay"),
                jsonObject.getDouble("az"),
                jsonObject.getDouble("gx"),
                jsonObject.getDouble("gy"),
                jsonObject.getDouble("gz"),
                jsonObject.getDouble("mx"),
                jsonObject.getDouble("my"),
                jsonObject.getDouble("mz"),
                jsonObject.getDouble("p1"),
                jsonObject.getDouble("p2"),
                jsonObject.getDouble("p3")
        );
        return sourceDataUnit;
    }
}
