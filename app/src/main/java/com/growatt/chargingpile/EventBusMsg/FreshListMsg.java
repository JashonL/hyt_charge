package com.growatt.chargingpile.EventBusMsg;

/**
 * Created by Administrator on 2019/7/29.
 */

public class FreshListMsg {
    private String devSn;

    public FreshListMsg() {
    }

    public FreshListMsg(String devSn) {
        this.devSn = devSn;
    }

    public String getDevSn() {
        return devSn;
    }

    public void setDevSn(String devSn) {
        this.devSn = devSn;
    }
}
