package fruitbasket.com.bodyfit.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.GridView;

/**
 * Author: FruitBasket
 * Time: 2017/9/6
 * Email: FruitBasket@qq.com
 * Source code: github.com/DevelopersAssociation
 */
public class MyGridView extends GridView {
    public static final String TAG="MyGridView";
    public MyGridView(Context context) {
        super(context);
    }

    public MyGridView(Context con,AttributeSet attrs){
        super(con, attrs);
    }

    public MyGridView(Context con,AttributeSet attrs,int defStyle){
        super(con,attrs,defStyle);
    }

    protected void onMeasure(int widthMeasureSpec,int heightMeasureSpec){
        Log.i(TAG,"onMeasure");
        int expandSpec=MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}