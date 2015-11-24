package fruitbasket.com.bodyfit;

import android.app.Application;


public class App extends Application {
    private static final String TAG="APP";

    static{
        System.loadLibrary("NativeHelper");
    }



}
