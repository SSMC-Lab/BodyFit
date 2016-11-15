package fruitbasket.com.bodyfit.analysis;

import android.util.Log;

/**
 * Created by Administrator on 2016/11/15.
 */
public class DynamicTimeWarping {
    public static final String TAG="DynamicTimeWarping";

    //dtw算法,t为模式曲线，r为测试曲线
    public double getDtwValue(double[] t, double[] r) {
        if(t==null || r==null){
            Log.e(TAG, "array null");
            return 10000000;
        }
        int N = t.length;
        int M = r.length;
        double[][] d = new double[M][];
        for (int i = 0; i < M; i++)
            d[i] = new double[N];

        for (int i = 0; i < M; i++)
            for (int j = 0; j < N; j++)
                d[i][j] = (r[i] - t[j]) * (r[i] - t[j]);// 计算两点之间的距离

        double[][] D = new double[M][];
        for (int i = 0; i < M; i++)
            D[i] = new double[N];
        for (int i = 0; i < M; i++)
            for (int j = 0; j < N; j++)
                D[i][j] = 0;

        D[0][0] = d[0][0];
        for (int i = 1; i < M; i++)
            D[i][0] = d[i][0] + D[i - 1][0];
        for (int i = 1; i < N; i++)
            D[0][i] = d[0][i] + D[0][i - 1];
        for (int n = 1; n < M; n++)
            for (int m = 1; m < N; m++)
                D[n][m] = d[n][m]
                        + min(D[n - 1][m], D[n - 1][m - 1], D[n][m - 1]);
        double Dist = D[M - 1][N - 1];
        return Dist;
    }

    //求三个数的最小值
    private double min(double x1, double x2, double x3) {
        if (x1 <= x2 && x1 <= x3)
            return x1;
        else if (x2 <= x1 && x2 <= x3)
            return x2;
        else
            return x3;
    }
}
