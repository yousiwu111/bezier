package com.zkl.bezier;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

/**
 * @author Zhangkelu
 * @date 2019/11/23 9:42
 * @Description:
 */
class DisplayUtils {
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 获取屏幕宽度
     */
    public static int getScreenWidth(Context context) {
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        return localDisplayMetrics.widthPixels;
    }
}
