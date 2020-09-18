package com.growatt.chargingpile.view;


import android.content.Context;

import com.hjq.toast.style.ToastQQStyle;

/**
 * Created：2018/12/11 on 16:28
 * Author:gaideng on dg
 * Description:自定义toast样式
 */

public class MyToastBlackStyle extends ToastQQStyle {

    public MyToastBlackStyle(Context context) {
        super(context);
    }

    @Override
    public float getTextSize() {
        return 14;
    }

    @Override
    public int getCornerRadius() {
        return 10;
    }

    @Override
    public int getPaddingStart() {
        return 12;
    }

    @Override
    public int getPaddingTop() {
        return 10;
    }
}
