package com.growatt.chargingpile.bean;

/**
 * Created by Administrator on 2019/3/29.
 */

public class UdpSearchBean {
    private String devName;
    private String devIp;

    public UdpSearchBean() {
    }

    public UdpSearchBean(String devName, String devId) {
        this.devName = devName;
        this.devIp = devId;
    }

    @Override
    public int hashCode() {
        return devIp.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UdpSearchBean){
           return ((UdpSearchBean) obj).devIp.equals(this.getDevIp());
        }
        return super.equals(obj);
    }

    public String getDevName() {
        return devName;
    }

    public void setDevName(String devName) {
        this.devName = devName;
    }

    public String getDevIp() {
        return devIp;
    }

    public void setDevIp(String devIp) {
        this.devIp = devIp;
    }


}
