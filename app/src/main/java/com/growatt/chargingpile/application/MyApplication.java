package com.growatt.chargingpile.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import org.xutils.x;

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
    private List<Activity> mList = new ArrayList<Activity>();

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        context = this;
        // 初始化
        x.Ext.init(this);
        // 设置是否输出debug
        x.Ext.setDebug(true);
    }

    // add Activity
    public void addActivity(Activity activity) {
        mList.add(activity);
    }


    //关闭每一个list内的activity
    public void exit() {
        try {
            for (Activity activity : mList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }
}
