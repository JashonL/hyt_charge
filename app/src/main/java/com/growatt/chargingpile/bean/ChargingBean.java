package com.growatt.chargingpile.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.google.gson.annotations.SerializedName;

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
        private int solar;
        //是否被选中
        private boolean isChecked;
        //设备类型
        private int devType;

        private int G_ExternalLimitPower;
        private String code;
        private int G_HearbeatInterval;
        private String G_CardPin;
        private int G_MeterValueInterval;
        private String mac;
        private int G_MaxTemperature;
        private int G_MaxCurrent;
        private String vendor;
        private String host;
        private String mask;
        private String G_Authentication;
        private int G_WebSocketPingInterval;
        private String ip;
        private String G_ChargerMode;
        private String dns;
        private String site;
        private String G_ChargerLanguage;
        private int online;
        private String status_2;
        private String status_4;
        private String gateway;
        private String status_3;
        private List<PriceConfBean> priceConf;
        private int G_SolarMode;
        private float G_SolarLimitPower;


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

        public int getSolar() {
            return solar;
        }

        public void setSolar(int solar) {
            this.solar = solar;
        }

        public int getG_SolarMode() {
            return G_SolarMode;
        }

        public void setG_SolarMode(int g_SolarMode) {
            G_SolarMode = g_SolarMode;
        }

        public float getG_SolarLimitPower() {
            return G_SolarLimitPower;
        }

        public void setG_SolarLimitPower(float g_SolarLimitPower) {
            G_SolarLimitPower = g_SolarLimitPower;
        }

        public int getG_ExternalLimitPower() {
            return G_ExternalLimitPower;
        }

        public void setG_ExternalLimitPower(int G_ExternalLimitPower) {
            this.G_ExternalLimitPower = G_ExternalLimitPower;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public int getG_HearbeatInterval() {
            return G_HearbeatInterval;
        }

        public void setG_HearbeatInterval(int G_HearbeatInterval) {
            this.G_HearbeatInterval = G_HearbeatInterval;
        }

        public String getG_CardPin() {
            return G_CardPin;
        }

        public void setG_CardPin(String G_CardPin) {
            this.G_CardPin = G_CardPin;
        }

        public int getG_MeterValueInterval() {
            return G_MeterValueInterval;
        }

        public void setG_MeterValueInterval(int G_MeterValueInterval) {
            this.G_MeterValueInterval = G_MeterValueInterval;
        }

        public String getMac() {
            return mac;
        }

        public void setMac(String mac) {
            this.mac = mac;
        }

        public int getG_MaxTemperature() {
            return G_MaxTemperature;
        }

        public void setG_MaxTemperature(int G_MaxTemperature) {
            this.G_MaxTemperature = G_MaxTemperature;
        }

        public int getG_MaxCurrent() {
            return G_MaxCurrent;
        }

        public void setG_MaxCurrent(int G_MaxCurrent) {
            this.G_MaxCurrent = G_MaxCurrent;
        }

        public String getVendor() {
            return vendor;
        }

        public void setVendor(String vendor) {
            this.vendor = vendor;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public String getMask() {
            return mask;
        }

        public void setMask(String mask) {
            this.mask = mask;
        }

        public String getG_Authentication() {
            return G_Authentication;
        }

        public void setG_Authentication(String G_Authentication) {
            this.G_Authentication = G_Authentication;
        }

        public int getG_WebSocketPingInterval() {
            return G_WebSocketPingInterval;
        }

        public void setG_WebSocketPingInterval(int G_WebSocketPingInterval) {
            this.G_WebSocketPingInterval = G_WebSocketPingInterval;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getG_ChargerMode() {
            return G_ChargerMode;
        }

        public void setG_ChargerMode(String G_ChargerMode) {
            this.G_ChargerMode = G_ChargerMode;
        }

        public String getDns() {
            return dns;
        }

        public void setDns(String dns) {
            this.dns = dns;
        }

        public String getSite() {
            return site;
        }

        public void setSite(String site) {
            this.site = site;
        }

        public String getG_ChargerLanguage() {
            return G_ChargerLanguage;
        }

        public void setG_ChargerLanguage(String G_ChargerLanguage) {
            this.G_ChargerLanguage = G_ChargerLanguage;
        }

        public int getOnline() {
            return online;
        }

        public void setOnline(int online) {
            this.online = online;
        }

        public String getStatus_2() {
            return status_2;
        }

        public void setStatus_2(String status_2) {
            this.status_2 = status_2;
        }

        public String getStatus_4() {
            return status_4;
        }

        public void setStatus_4(String status_4) {
            this.status_4 = status_4;
        }

        public String getGateway() {
            return gateway;
        }

        public void setGateway(String gateway) {
            this.gateway = gateway;
        }

        public String getStatus_3() {
            return status_3;
        }

        public void setStatus_3(String status_3) {
            this.status_3 = status_3;
        }

        public List<PriceConfBean> getPriceConf() {
            return priceConf;
        }

        public void setPriceConf(List<PriceConfBean> priceConf) {
            this.priceConf = priceConf;
        }

        public static class PriceConfBean extends AbstractExpandableItem<Object> implements MultiItemEntity ,Parcelable{
            private double price;
            @SerializedName("name")
            private String nameX;
            @SerializedName("time")
            private String timeX;

            private int itemType;
            private String symbol;

            public PriceConfBean() {
            }

            protected PriceConfBean(Parcel in) {
                price = in.readDouble();
                nameX = in.readString();
                timeX = in.readString();
                itemType = in.readInt();
                symbol=in.readString();
            }

            public static final Creator<PriceConfBean> CREATOR = new Creator<PriceConfBean>() {
                @Override
                public PriceConfBean createFromParcel(Parcel in) {
                    return new PriceConfBean(in);
                }

                @Override
                public PriceConfBean[] newArray(int size) {
                    return new PriceConfBean[size];
                }
            };

            public void setItemType(int itemType) {
                this.itemType = itemType;
            }

            public double getPrice() {
                return price;
            }

            public void setPrice(double price) {
                this.price = price;
            }

            public String getNameX() {
                return nameX;
            }

            public void setNameX(String nameX) {
                this.nameX = nameX;
            }

            public String getTimeX() {
                return timeX;
            }

            public void setTimeX(String timeX) {
                this.timeX = timeX;
            }

            public String getSymbol() {
                return symbol;
            }

            public void setSymbol(String symbol) {
                this.symbol = symbol;
            }

            @Override
            public int getItemType() {
                return itemType;
            }

            @Override
            public int getLevel() {
                return 1;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeDouble(price);
                dest.writeString(nameX);
                dest.writeString(timeX);
                dest.writeInt(itemType);
                dest.writeString(symbol);
            }
        }
    }
}
