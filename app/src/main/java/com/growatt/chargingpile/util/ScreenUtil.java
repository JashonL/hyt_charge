package com.growatt.chargingpile.util;

import android.app.Activity;
import android.util.DisplayMetrics;

/**
 * Created by Administrator on 2018/10/16.
 */

public class ScreenUtil {

    public static DisplayMetrics getScreen(Activity act) {
        DisplayMetrics dm = new DisplayMetrics();
        act.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm;
    }

    public static int getScreenWidth(Activity act) {
        return getScreen(act).widthPixels;
    }

    public static int getScreenHeight(Activity act) {
        return getScreen(act).heightPixels;
    }
}
