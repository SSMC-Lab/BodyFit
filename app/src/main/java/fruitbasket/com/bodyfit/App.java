package fruitbasket.com.bodyfit;

import android.app.Application;
import android.util.Log;


public class App extends Application {
    private static final String TAG="APP";

    static{
        System.loadLibrary("NativeHelper");
    }

    @Override
    public void onCreate(){
        super.onCreate();
        Log.i(TAG, "onCreate()");
    }


}
