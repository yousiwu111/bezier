package com.zkl.bezier;

import android.content.Context;

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
}
