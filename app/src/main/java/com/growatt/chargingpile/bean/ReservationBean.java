package com.growatt.chargingpile.bean;

import java.util.List;

/**
 * Created by Administrator on 2018/10/23.
 */

public class ReservationBean {

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
        private String endDate;
        private int connectorId;
        private String msgId;
        private String userId;
        private int transactionId;
        private String expiryDate;
        private int loopType;
        private String loopValue;
        private String cKey;
        private int reservationId;
        private String chargeId;
        private int cValue;
        private long ctime;
        private int cid;
        private String status;

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        public int getConnectorId() {
            return connectorId;
        }

        public void setConnectorId(int connectorId) {
            this.connectorId = connectorId;
        }

        public String getMsgId() {
            return msgId;
        }

        public void setMsgId(String msgId) {
            this.msgId = msgId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public int getTransactionId() {
            return transactionId;
        }

        public void setTransactionId(int transactionId) {
            this.transactionId = transactionId;
        }

        public String getExpiryDate() {
            return expiryDate;
        }

        public void setExpiryDate(String expiryDate) {
            this.expiryDate = expiryDate;
        }

        public int getLoopType() {
            return loopType;
        }

        public void setLoopType(int loopType) {
            this.loopType = loopType;
        }

        public String getLoopValue() {
            return loopValue;
        }

        public void setLoopValue(String loopValue) {
            this.loopValue = loopValue;
        }

        public String getCKey() {
            return cKey;
        }

        public void setCKey(String cKey) {
            this.cKey = cKey;
        }

        public int getReservationId() {
            return reservationId;
        }

        public void setReservationId(int reservationId) {
            this.reservationId = reservationId;
        }

        public String getChargeId() {
            return chargeId;
        }

        public void setChargeId(String chargeId) {
            this.chargeId = chargeId;
        }

        public int getCValue() {
            return cValue;
        }

        public void setCValue(int cValue) {
            this.cValue = cValue;
        }

        public long getCtime() {
            return ctime;
        }

        public void setCtime(long ctime) {
            this.ctime = ctime;
        }

        public int getCid() {
            return cid;
        }

        public void setCid(int cid) {
            this.cid = cid;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
