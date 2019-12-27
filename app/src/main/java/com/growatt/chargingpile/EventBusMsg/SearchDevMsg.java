package com.growatt.chargingpile.EventBusMsg;

/**
 * Created by Administrator on 2019/12/26.
 */

public class SearchDevMsg {
    private String devSn;

    public SearchDevMsg() {
    }

    public SearchDevMsg(String devSn) {
        this.devSn = devSn;
    }

    public String getDevSn() {
        return devSn;
    }

    public void setDevSn(String devSn) {
        this.devSn = devSn;
    }
}
