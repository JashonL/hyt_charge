package com.growatt.chargingpile.bean;

import android.text.TextUtils;

import java.util.List;

/**
 * Created by Administrator on 2018/10/18.
 */

public class ChargingBean {
    public static final int CHARGING_PILE = 1;
    public static final int ADD_DEVICE = 2;

    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        private int connectors;
        private String chargeId;
        private String userPhone;
        private String name;
        private String model;
        private long time;
        private String userName;
        private int type;
        private String userId;
        private List<String> status;

        //是否被选中
        private boolean isChecked;
        //设备类型
        private int devType;


        public int getConnectors() {
            return connectors;
        }

        public void setConnectors(int connectors) {
            this.connectors = connectors;
        }

        public String getChargeId() {
            return chargeId;
        }

        public void setChargeId(String chargeId) {
            this.chargeId = chargeId;
        }

        public String getUserPhone() {
            return userPhone;
        }

        public void setUserPhone(String userPhone) {
            this.userPhone = userPhone;
        }

        public String getName() {
            if (TextUtils.isEmpty(name)) {
                return chargeId;
            }
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public List<String> getStatus() {
            return status;
        }

        public void setStatus(List<String> status) {
            this.status = status;
        }

        public boolean isChecked() {
            return isChecked;
        }

        public void setChecked(boolean checked) {
            isChecked = checked;
        }

        public int getDevType() {
            return devType;
        }

        public void setDevType(int devType) {
            this.devType = devType;
        }

    }
}
