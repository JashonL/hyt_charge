package com.growatt.chargingpile.jpush;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.growatt.chargingpile.util.AppUtils;

import org.greenrobot.eventbus.EventBus;
import org.xutils.common.util.LogUtil;

import cn.jpush.android.api.JPushInterface;


/**
 * 极光推送自定义接收器
 */
public class MyReceiverV2 extends BroadcastReceiver {
    private NotificationManager nm;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (null == nm) {
            nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        try {
            //接收的通知消息
            String action = intent.getAction();
            if (TextUtils.isEmpty(action)) return;
            Bundle bundle = intent.getExtras();
            if (bundle == null) return;
            switch (action) {
                //接收通知
//				case JPushInterface.ACTION_MESSAGE_RECEIVED:
                //接收自定义通知
                case JPushInterface.ACTION_NOTIFICATION_RECEIVED:
                    break;
                //用户点击了通知
                case JPushInterface.ACTION_NOTIFICATION_OPENED:
                    if (bundle.containsKey(JPushInterface.EXTRA_EXTRA)) {
                        //设置自定义消息
                        Object extra = bundle.get(JPushInterface.EXTRA_EXTRA);
                        Gson gson = new Gson();
//                        //没有运行在前台
                        if (!AppUtils.isRunningForeground(context)){
                            Intent inLogin = context.getPackageManager()
                                    .getLaunchIntentForPackage(context.getPackageName());
                            inLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                            context.startActivity(inLogin);
                        }

                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
