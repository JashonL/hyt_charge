package com.growatt.chargingpile.scan;

import android.os.Handler;
import android.os.Looper;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.ResultPointCallback;
import com.growatt.chargingpile.scan.activity.MyCaptureActivity;

import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Administrator on 2018/11/2.
 */

public class MyDecodeThread extends Thread {
    private final MyCaptureActivity activity;
    private final Hashtable<DecodeHintType, Object> hints;
    private final Vector<BarcodeFormat> decodeFormats;
    private Handler handler;
    private final CountDownLatch handlerInitLatch;

    public MyDecodeThread(MyCaptureActivity activity, ResultPointCallback resultPointCallback) {

        this.activity = activity;
        handlerInitLatch = new CountDownLatch(1);

        hints = new Hashtable<>();


        decodeFormats = new Vector<com.google.zxing.BarcodeFormat>();


        /*是否解析有条形码（一维码）*/
        if (activity.config.isDecodeBarCode()) {
            decodeFormats.addAll(MyDecodeFormatManager.ONE_D_FORMATS);
        }

        decodeFormats.addAll(MyDecodeFormatManager.QR_CODE_FORMATS);
        decodeFormats.addAll(MyDecodeFormatManager.DATA_MATRIX_FORMATS);
        hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
        hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(DecodeHintType.NEED_RESULT_POINT_CALLBACK, resultPointCallback);

    }

    public Handler getHandler() {
        try {
            handlerInitLatch.await();
        } catch (InterruptedException ie) {
            // continue?
        }
        return handler;
    }

    @Override
    public void run() {
        Looper.prepare();
        handler = new MyDecodeHandler(activity, hints);
        handlerInitLatch.countDown();
        Looper.loop();
    }
}
