package com.growatt.chargingpile.bean;

import android.text.TextUtils;

import java.util.List;

/**
 * Created by Administrator on 2018/7/17.
 */

public class ChargingRecordBean {
    private int code;
    private List<DataBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {

        private String name="";
        private long sysEndTime;
        private double cost;
        private double rate;
        private int connectorId;
        private String chargeId;
        private long sysStartTime;
        private int ctime;
        private double energy;
        private String status;

        public long getSysEndTime() {
            return sysEndTime;
        }

        public void setSysEndTime(long sysEndTime) {
            this.sysEndTime = sysEndTime;
        }

        public double getCost() {
            return cost;
        }

        public void setCost(int cost) {
            this.cost = cost;
        }

        public double getRate() {
            return rate;
        }

        public void setRate(int rate) {
            this.rate = rate;
        }

        public int getConnectorId() {
            return connectorId;
        }

        public void setConnectorId(int connectorId) {
            this.connectorId = connectorId;
        }

        public String getChargeId() {
            return chargeId;
        }

        public void setChargeId(String chargeId) {
            this.chargeId = chargeId;
        }

        public long getSysStartTime() {
            return sysStartTime;
        }

        public void setSysStartTime(long sysStartTime) {
            this.sysStartTime = sysStartTime;
        }

        public int getCtime() {
            return ctime;
        }

        public void setCtime(int ctime) {
            this.ctime = ctime;
        }

        public double getEnergy() {
            return energy;
        }

        public void setEnergy(int energy) {
            this.energy = energy;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getName() {
            if (TextUtils.isEmpty(name)){
                return chargeId;
            }
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
