package com.growatt.chargingpile.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/10/20.
 */

public class GunBean {
    public static final String UNAVAILABLE = "Unavailable";
    public static final String FAULTED = "Faulted";
    public static final String AVAILABLE = "Available";
    public static final String PREPARING = "Preparing";
    public static final String CHARGING = "Charging";
    public static final String FINISHING = "Finishing";
    public static final String EXPIRY = "expiry";
    public static final String ACCEPTED = "Accepted";
    public static final String WORK = "work";
    public static final String RESERVED = "Reserved";
    public static final String SUSPENDEEV="SuspendedEV";


    private int code;
    private DataBean data;
    private List<ReserveNowBean> ReserveNow = new ArrayList<>();
    private LastActionBean LastAction;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public List<ReserveNowBean> getReserveNow() {
        return ReserveNow;
    }

    public void setReserveNow(List<ReserveNowBean> ReserveNow) {
        this.ReserveNow = ReserveNow;
    }

    public LastActionBean getActionBean() {
        return LastAction;
    }

    public void setActionBean(LastActionBean actionBean) {
        this.LastAction = actionBean;
    }

    public static class DataBean {
        private String order_status;
        private double current;
        private double cost;
        private int ctype;
        private double rate;
        private int ctime;
        private int transactionId;
        private String status;
        private double energy;
        private double voltage;
        private int connectorId = 1;
        private String name;
        private String cKey;
        private double cValue;


        public String getcKey() {
            return cKey;
        }

        public void setcKey(String cKey) {
            this.cKey = cKey;
        }

        public double getcValue() {
            return cValue;
        }

        public void setcValue(int cValue) {
            this.cValue = cValue;
        }

        public String getOrder_status() {
            return order_status;
        }

        public void setOrder_status(String order_status) {
            this.order_status = order_status;
        }

        public double getCurrent() {
            return current;
        }

        public void setCurrent(double current) {
            this.current = current;
        }

        public double getCost() {
            return cost;
        }

        public void setCost(int cost) {
            this.cost = cost;
        }

        public int getCtype() {
            return ctype;
        }

        public void setCtype(int ctype) {
            this.ctype = ctype;
        }

        public double getRate() {
            return rate;
        }

        public void setRate(double rate) {
            this.rate = rate;
        }

        public int getCtime() {
            return ctime;
        }

        public void setCtime(int ctime) {
            this.ctime = ctime;
        }

        public int getTransactionId() {
            return transactionId;
        }

        public void setTransactionId(int transactionId) {
            this.transactionId = transactionId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public double getEnergy() {
            return energy;
        }

        public void setEnergy(int energy) {
            this.energy = energy;
        }

        public double getVoltage() {
            return voltage;
        }

        public void setVoltage(double voltage) {
            this.voltage = voltage;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setCost(double cost) {
            this.cost = cost;
        }

        public void setEnergy(double energy) {
            this.energy = energy;
        }

        public int getConnectorId() {
            return connectorId;
        }

        public void setConnectorId(int connectorId) {
            this.connectorId = connectorId;
        }

        public String getName() {
            if (connectorId == 1) {
                setName("A枪");
            } else {
                setName("B枪");
            }
            return name;
        }
    }

    public static class ReserveNowBean {
        private String expiryDate;
        private String cKey;
        private int reservationId;
        private String endDate;
        private int connectorId;
        private String chargeId;
        private double cValue;
        private String userId;
        private int loopType;
        private String loopValue;

        public String getExpiryDate() {
            return expiryDate;
        }

        public void setExpiryDate(String expiryDate) {
            this.expiryDate = expiryDate;
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

        public String getChargeId() {
            return chargeId;
        }

        public void setChargeId(String chargeId) {
            this.chargeId = chargeId;
        }

        public double getCValue() {
            return cValue;
        }

        public void setCValue(int cValue) {
            this.cValue = cValue;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getcKey() {
            return cKey;
        }

        public void setcKey(String cKey) {
            this.cKey = cKey;
        }

        public double getcValue() {
            return cValue;
        }

        public void setcValue(double cValue) {
            this.cValue = cValue;
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
    }

    public static class LastActionBean {
        private String action;
        private String expiryDate;
        private int connectorId;
        private String chargeId;
        private String userId;
        private String loopType;
        private String loopValue;
        private long actionTime;
        private String transactionId;
        private String cKey;
        private double cValue;
        private int lan;


        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public String getExpiryDate() {
            return expiryDate;
        }

        public void setExpiryDate(String expiryDate) {
            this.expiryDate = expiryDate;
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

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getLoopType() {
            return loopType;
        }

        public void setLoopType(String loopType) {
            this.loopType = loopType;
        }

        public String getLoopValue() {
            return loopValue;
        }

        public void setLoopValue(String loopValue) {
            this.loopValue = loopValue;
        }

        public long getActionTime() {
            return actionTime;
        }

        public void setActionTime(long actionTime) {
            this.actionTime = actionTime;
        }

        public String getTransactionId() {
            return transactionId;
        }

        public void setTransactionId(String transactionId) {
            this.transactionId = transactionId;
        }

        public String getcKey() {
            return cKey;
        }

        public void setcKey(String cKey) {
            this.cKey = cKey;
        }

        public double getcValue() {
            return cValue;
        }

        public void setcValue(double cValue) {
            this.cValue = cValue;
        }

        public int getLan() {
            return lan;
        }

        public void setLan(int lan) {
            this.lan = lan;
        }
    }

}
