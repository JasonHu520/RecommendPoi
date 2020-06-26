package com.example.jasonhu.recommendpoi.BaseClass.picture_util;
import android.content.Context;
public class SizeUtil {
    /**
     * dp转px
     * @param context 上下文
     * @param dpValue dp值
     * @return px值
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
