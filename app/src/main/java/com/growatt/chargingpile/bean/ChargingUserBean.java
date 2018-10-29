package com.growatt.chargingpile.bean;

import java.util.List;

/**
 * Created by Administrator on 2018/6/5.
 */

public class ChargingUserBean {


    /**
     * code : 0
     * data : [{"time":1539938398000,"userName":"Nameuser01","userId":"user01"},{"time":1539938189000,"userName":"ustest021","userId":"test02"}]
     */

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
        /**
         * time : 1539938398000
         * userName : Nameuser01
         * userId : user01
         */

        private long time;
        private String userName;
        private String userId;

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

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
}
