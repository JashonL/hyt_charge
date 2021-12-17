package com.growatt.chargingpile.listener;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 2019/7/1.
 */

public class NoLeakHandler<T extends BaseHandlerCallBack>extends Handler {
    private WeakReference<T> wr;

    public NoLeakHandler(T t) {
        wr = new WeakReference<>(t);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        T t = wr.get();
        if (t != null) {
            t.callBack(msg);
        }
    }
}
