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
        private String endDate = "";
        private int connectorId = 0;
        private String msgId = "";
        private String userId = "";
        private int transactionId = 0;
        private String expiryDate = "";
        private int loopType = 0;
        private String loopValue = "";
        private String cKey = "";
        private int reservationId = 0;
        private String chargeId = "";
        private String cValue = "";
        private long ctime = 0;
        private int cid = 0;
        private String status = "";
        private String symbol = "";
        private String cValue2="0.0";
        private double rate=0;
        private double cost=0;


        public double getCost() {
            return cost;
        }

        public void setCost(double cost) {
            this.cost = cost;
        }

        public double getRate() {
            return rate;
        }

        public void setRate(double rate) {
            this.rate = rate;
        }

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

        public String getCValue() {
            return cValue;
        }

        public void setCValue(String cValue) {
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

        public String getcKey() {
            return cKey;
        }

        public void setcKey(String cKey) {
            this.cKey = cKey;
        }

        public String getcValue() {
            return cValue;
        }


        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public void setcValue(String cValue) {
            this.cValue = cValue;
        }

        public String getcValue2() {
            return cValue2;
        }

        public void setcValue2(String cValue2) {
            this.cValue2 = cValue2;
        }
    }
}
