package com.growatt.chargingpile.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.growatt.chargingpile.crashhandler.CrashHandler;
import com.mylhyl.circledialog.res.values.CircleColor;
import com.mylhyl.circledialog.res.values.CircleDimen;

import org.xutils.x;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/10/16.
 */

public class MyApplication extends Application {

    //为了实现每次使用该类时不创建新的对象而创建的静态对象
    private static MyApplication instance;

    //构造方法
    //实例化一次
    public synchronized static MyApplication getInstance() {
        return instance;
    }

    public static Context context;
    //运用list来保存们每一个activity是关键
    private List<SoftReference<Activity>> mList = new ArrayList<SoftReference<Activity>>();

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        context = getApplicationContext();
        CrashHandler.getInstance().init(this);
        // 初始化
        x.Ext.init(this);
        // 设置是否输出debug
        x.Ext.setDebug(true);
        initCirclerDialog();
    }


    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        MyApplication.context = context;
    }

    // add Activity
    public void addActivity(SoftReference<Activity> softReference) {
        mList.add(softReference);
    }


    //遍历所有Activity并finish
    public void exit() {
        try {
            for (int i = 0; i < mList.size(); i++) {
                Activity activity = mList.get(i).get();
                if (activity != null) {
                    activity.finish();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }

    }


    /**
     * 设置弹框按钮颜色等
     */
    private void initCirclerDialog() {
        CircleDimen.TITLE_HEIGHT = 130;
        CircleDimen.TITLE_TEXT_SIZE = 50;
        CircleDimen.FOOTER_BUTTON_TEXT_SIZE = 45;
        CircleDimen.INPUT_TEXT_SIZE = 40;
        CircleDimen.ITEM_CONTENT_TEXT_SIZE = 40;
        CircleDimen.CONTENT_TEXT_SIZE = 40;
        CircleDimen.INPUT_HEIGHT = 220;
        CircleDimen.DIALOG_RADIUS = 50;
        CircleColor.CONTENT= 0xFF999999;
    }

}
