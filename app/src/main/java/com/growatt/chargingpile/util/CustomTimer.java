package com.growatt.chargingpile.util;

import android.os.Message;


import com.growatt.chargingpile.listener.BaseHandlerCallBack;
import com.growatt.chargingpile.listener.NoLeakHandler;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 自定义计时器：
 * 实现暂停 继续的功能，内部维护一个任务
 * 当计时器销毁时同时销毁
 */

public class CustomTimer extends Timer implements BaseHandlerCallBack {

    //使用一个flag来控制暂停和继续
    private boolean isPause = false;

    //刷新任务的间隔时间
    private long period = 60 * 1000;

    //延迟多少秒
    private int delay = 0;

    //判断定时器是否已启动
    private boolean isRunning = false;


    private TimerTask mTimerTask;


    private NoLeakHandler mHandler;


    private TimerRunable timerRunable;


    public CustomTimer(TimerRunable runable) {
        this.timerRunable = runable;
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                isRunning = true;
                if (!isPause) {
                    mHandler.sendEmptyMessage(0);
                }
            }
        };

        mHandler = new NoLeakHandler<>(this);
    }


    public CustomTimer(TimerRunable runable, long period, int delay) {
        this(runable);
        this.period = period;
        this.delay = delay;
    }

    /**
     * 启动定时器
     */
    public void timerStart() {
        if (!isRunning){
            schedule(mTimerTask, delay, period);
        }
    }


    /**
     * 暂停 定时器
     */
    public void timerPause() {
        this.isPause = true;
    }


    /**
     * 继续 定时器
     */
    public void timerRuning() {
        this.isPause = false;
    }


    public long getPeriod() {
        return period;
    }

    public void setPeriod(long period) {
        this.period = period;
    }

    /**
     * 销毁定时器
     */

    public void timerDestroy() {
        this.cancel();
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
        if (mHandler!=null){
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }

    }


    /**
     * 获取当前的状态
     */
    public boolean isPause() {
        return isPause;
    }

    @Override
    public void callBack(Message msg) {
        this.timerRunable.run();
    }


    public interface TimerRunable {
        void run();
    }

}
