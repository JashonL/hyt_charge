package com.growatt.chargingpile.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/10/20.
 */

public class GunBean implements Parcelable {
    public static final String NONE="None";
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
    public static final String SUSPENDEEV = "SuspendedEV";
    public static final String SUSPENDEDEVSE = "SuspendedEVSE";
    public static final String RESERVENOW = "ReserveNow";


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

    public static class DataBean implements Parcelable {
        private String order_status = "";
        private double current = 0;
        private double cost = 0;
        private int ctype = 0;
        private double rate = 0;
        private int ctime = 0;
        private int transactionId = 0;
        private String status = "";
        private double energy = 0;
        private double voltage = 0;
        private int connectorId = 1;
        private String name = "";
        private String cKey = "";
        private String cValue = "0";
        private String symbol = "";
        private String loopValue;


        @Override
        public String toString() {
            return "DataBean{" +
                    "order_status='" + order_status + '\'' +
                    ", current=" + current +
                    ", cost=" + cost +
                    ", ctype=" + ctype +
                    ", rate=" + rate +
                    ", ctime=" + ctime +
                    ", transactionId=" + transactionId +
                    ", status='" + status + '\'' +
                    ", energy=" + energy +
                    ", voltage=" + voltage +
                    ", connectorId=" + connectorId +
                    ", name='" + name + '\'' +
                    ", cKey='" + cKey + '\'' +
                    ", cValue='" + cValue + '\'' +
                    ", symbol='" + symbol + '\'' +
                    ", loopValue='" + loopValue + '\'' +
                    '}';
        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
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

        public void setcValue(String cValue) {
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


        public String getName() {
            return name;
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

        public String getLoopValue() {
            return loopValue;
        }

        public void setLoopValue(String loopValue) {
            this.loopValue = loopValue;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.order_status);
            dest.writeDouble(this.current);
            dest.writeDouble(this.cost);
            dest.writeInt(this.ctype);
            dest.writeDouble(this.rate);
            dest.writeInt(this.ctime);
            dest.writeInt(this.transactionId);
            dest.writeString(this.status);
            dest.writeDouble(this.energy);
            dest.writeDouble(this.voltage);
            dest.writeInt(this.connectorId);
            dest.writeString(this.name);
            dest.writeString(this.cKey);
            dest.writeString(this.cValue);
            dest.writeString(this.symbol);
            dest.writeString(this.loopValue);
        }

        public void readFromParcel(Parcel source) {
            this.order_status = source.readString();
            this.current = source.readDouble();
            this.cost = source.readDouble();
            this.ctype = source.readInt();
            this.rate = source.readDouble();
            this.ctime = source.readInt();
            this.transactionId = source.readInt();
            this.status = source.readString();
            this.energy = source.readDouble();
            this.voltage = source.readDouble();
            this.connectorId = source.readInt();
            this.name = source.readString();
            this.cKey = source.readString();
            this.cValue = source.readString();
            this.symbol = source.readString();
            this.loopValue = source.readString();
        }

        public DataBean() {
        }

        protected DataBean(Parcel in) {
            this.order_status = in.readString();
            this.current = in.readDouble();
            this.cost = in.readDouble();
            this.ctype = in.readInt();
            this.rate = in.readDouble();
            this.ctime = in.readInt();
            this.transactionId = in.readInt();
            this.status = in.readString();
            this.energy = in.readDouble();
            this.voltage = in.readDouble();
            this.connectorId = in.readInt();
            this.name = in.readString();
            this.cKey = in.readString();
            this.cValue = in.readString();
            this.symbol = in.readString();
            this.loopValue = in.readString();
        }

        public static final Parcelable.Creator<DataBean> CREATOR = new Parcelable.Creator<DataBean>() {
            @Override
            public DataBean createFromParcel(Parcel source) {
                return new DataBean(source);
            }

            @Override
            public DataBean[] newArray(int size) {
                return new DataBean[size];
            }
        };
    }

    public static class ReserveNowBean implements Parcelable {
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


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.expiryDate);
            dest.writeString(this.cKey);
            dest.writeInt(this.reservationId);
            dest.writeString(this.endDate);
            dest.writeInt(this.connectorId);
            dest.writeString(this.chargeId);
            dest.writeDouble(this.cValue);
            dest.writeString(this.userId);
            dest.writeInt(this.loopType);
            dest.writeString(this.loopValue);
        }

        public void readFromParcel(Parcel source) {
            this.expiryDate = source.readString();
            this.cKey = source.readString();
            this.reservationId = source.readInt();
            this.endDate = source.readString();
            this.connectorId = source.readInt();
            this.chargeId = source.readString();
            this.cValue = source.readDouble();
            this.userId = source.readString();
            this.loopType = source.readInt();
            this.loopValue = source.readString();
        }

        public ReserveNowBean() {
        }

        protected ReserveNowBean(Parcel in) {
            this.expiryDate = in.readString();
            this.cKey = in.readString();
            this.reservationId = in.readInt();
            this.endDate = in.readString();
            this.connectorId = in.readInt();
            this.chargeId = in.readString();
            this.cValue = in.readDouble();
            this.userId = in.readString();
            this.loopType = in.readInt();
            this.loopValue = in.readString();
        }

        public static final Parcelable.Creator<ReserveNowBean> CREATOR = new Parcelable.Creator<ReserveNowBean>() {
            @Override
            public ReserveNowBean createFromParcel(Parcel source) {
                return new ReserveNowBean(source);
            }

            @Override
            public ReserveNowBean[] newArray(int size) {
                return new ReserveNowBean[size];
            }
        };
    }

    public static class LastActionBean implements Parcelable {
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


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.action);
            dest.writeString(this.expiryDate);
            dest.writeInt(this.connectorId);
            dest.writeString(this.chargeId);
            dest.writeString(this.userId);
            dest.writeString(this.loopType);
            dest.writeString(this.loopValue);
            dest.writeLong(this.actionTime);
            dest.writeString(this.transactionId);
            dest.writeString(this.cKey);
            dest.writeDouble(this.cValue);
            dest.writeInt(this.lan);
        }

        public void readFromParcel(Parcel source) {
            this.action = source.readString();
            this.expiryDate = source.readString();
            this.connectorId = source.readInt();
            this.chargeId = source.readString();
            this.userId = source.readString();
            this.loopType = source.readString();
            this.loopValue = source.readString();
            this.actionTime = source.readLong();
            this.transactionId = source.readString();
            this.cKey = source.readString();
            this.cValue = source.readDouble();
            this.lan = source.readInt();
        }

        public LastActionBean() {
        }

        protected LastActionBean(Parcel in) {
            this.action = in.readString();
            this.expiryDate = in.readString();
            this.connectorId = in.readInt();
            this.chargeId = in.readString();
            this.userId = in.readString();
            this.loopType = in.readString();
            this.loopValue = in.readString();
            this.actionTime = in.readLong();
            this.transactionId = in.readString();
            this.cKey = in.readString();
            this.cValue = in.readDouble();
            this.lan = in.readInt();
        }

        public static final Parcelable.Creator<LastActionBean> CREATOR = new Parcelable.Creator<LastActionBean>() {
            @Override
            public LastActionBean createFromParcel(Parcel source) {
                return new LastActionBean(source);
            }

            @Override
            public LastActionBean[] newArray(int size) {
                return new LastActionBean[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.code);
        dest.writeParcelable(this.data, flags);
        dest.writeTypedList(this.ReserveNow);
        dest.writeParcelable(this.LastAction, flags);
    }

    public void readFromParcel(Parcel source) {
        this.code = source.readInt();
        this.data = source.readParcelable(DataBean.class.getClassLoader());
        this.ReserveNow = source.createTypedArrayList(ReserveNowBean.CREATOR);
        this.LastAction = source.readParcelable(LastActionBean.class.getClassLoader());
    }

    public GunBean() {
    }

    protected GunBean(Parcel in) {
        this.code = in.readInt();
        this.data = in.readParcelable(DataBean.class.getClassLoader());
        this.ReserveNow = in.createTypedArrayList(ReserveNowBean.CREATOR);
        this.LastAction = in.readParcelable(LastActionBean.class.getClassLoader());
    }

    public static final Parcelable.Creator<GunBean> CREATOR = new Parcelable.Creator<GunBean>() {
        @Override
        public GunBean createFromParcel(Parcel source) {
            return new GunBean(source);
        }

        @Override
        public GunBean[] newArray(int size) {
            return new GunBean[size];
        }
    };
}
