package com.moac.android.palettedemo;

import android.content.Context;

/**
 * @author Peter Tackage
 * @since 27/04/15
 */
public class ScreenUtils {
    private ScreenUtils() {
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
