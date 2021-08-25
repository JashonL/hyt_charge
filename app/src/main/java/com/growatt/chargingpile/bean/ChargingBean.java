package com.growatt.chargingpile.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2018/10/18.
 */

public class ChargingBean implements Parcelable {
    public static final int CHARGING_PILE = 1;
    public static final int ADD_DEVICE = 2;

    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean implements Parcelable {

        @Override
        public String toString() {
            return "DataBean{" +
                    "connectors=" + connectors +
                    ", chargeId='" + chargeId + '\'' +
                    ", userPhone='" + userPhone + '\'' +
                    ", name='" + name + '\'' +
                    ", model='" + model + '\'' +
                    ", time=" + time +
                    ", userName='" + userName + '\'' +
                    ", type=" + type +
                    ", userId='" + userId + '\'' +
                    ", status=" + status +
                    ", solar=" + solar +
                    ", isChecked=" + isChecked +
                    ", devType=" + devType +
                    ", G_ExternalLimitPower=" + G_ExternalLimitPower +
                    ", code='" + code + '\'' +
                    ", G_HearbeatInterval=" + G_HearbeatInterval +
                    ", G_CardPin='" + G_CardPin + '\'' +
                    ", G_MeterValueInterval=" + G_MeterValueInterval +
                    ", mac='" + mac + '\'' +
                    ", G_MaxTemperature=" + G_MaxTemperature +
                    ", G_MaxCurrent=" + G_MaxCurrent +
                    ", vendor='" + vendor + '\'' +
                    ", host='" + host + '\'' +
                    ", mask='" + mask + '\'' +
                    ", G_Authentication='" + G_Authentication + '\'' +
                    ", G_WebSocketPingInterval=" + G_WebSocketPingInterval +
                    ", ip='" + ip + '\'' +
                    ", G_ChargerMode='" + G_ChargerMode + '\'' +
                    ", dns='" + dns + '\'' +
                    ", site='" + site + '\'' +
                    ", G_ChargerLanguage='" + G_ChargerLanguage + '\'' +
                    ", online=" + online +
                    ", status_2='" + status_2 + '\'' +
                    ", status_4='" + status_4 + '\'' +
                    ", gateway='" + gateway + '\'' +
                    ", status_3='" + status_3 + '\'' +
                    ", priceConf=" + priceConf +
                    ", G_SolarMode=" + G_SolarMode +
                    ", G_SolarLimitPower=" + G_SolarLimitPower +
                    ", symbol='" + symbol + '\'' +
                    ", status_1='" + status_1 + '\'' +
                    '}';
        }

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
        private String symbol;

        public String getStatus_1() {
            return status_1;
        }

        public void setStatus_1(String status_1) {
            this.status_1 = status_1;
        }

        private String status_1;


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

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public static class PriceConfBean extends ParamsBeanLeveItem1 implements MultiItemEntity ,Parcelable{
            private double price;
            @SerializedName("name")
            private String nameX;
            @SerializedName("time")
            private String timeX;

            private int itemType;
            private String symbol;
            private String startTime;
            private String endTime;
            private String sfield;
            private boolean isAuthority;


            public PriceConfBean() {
            }


            protected PriceConfBean(Parcel in) {
                price = in.readDouble();
                nameX = in.readString();
                timeX = in.readString();
                itemType = in.readInt();
                symbol = in.readString();
                startTime = in.readString();
                endTime = in.readString();
                sfield = in.readString();
                isAuthority = in.readByte() != 0;
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

            public String getStartTime() {
                return startTime;
            }

            public void setStartTime(String startTime) {
                this.startTime = startTime;
            }

            public String getEndTime() {
                return endTime;
            }

            public void setEndTime(String endTime) {
                this.endTime = endTime;
            }

            public String getSfield() {
                return sfield;
            }

            public void setSfield(String sfield) {
                this.sfield = sfield;
            }

            public boolean isAuthority() {
                return isAuthority;
            }

            public void setAuthority(boolean authority) {
                isAuthority = authority;
            }

            @Override
            public int getItemType() {
                return itemType;
            }


            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel parcel, int i) {

                parcel.writeDouble(price);
                parcel.writeString(nameX);
                parcel.writeString(timeX);
                parcel.writeInt(itemType);
                parcel.writeString(symbol);
                parcel.writeString(startTime);
                parcel.writeString(endTime);
                parcel.writeString(sfield);
                parcel.writeByte((byte) (isAuthority ? 1 : 0));
            }


        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.connectors);
            dest.writeString(this.chargeId);
            dest.writeString(this.userPhone);
            dest.writeString(this.name);
            dest.writeString(this.model);
            dest.writeLong(this.time);
            dest.writeString(this.userName);
            dest.writeInt(this.type);
            dest.writeString(this.userId);
            dest.writeStringList(this.status);
            dest.writeInt(this.solar);
            dest.writeByte(this.isChecked ? (byte) 1 : (byte) 0);
            dest.writeInt(this.devType);
            dest.writeInt(this.G_ExternalLimitPower);
            dest.writeString(this.code);
            dest.writeInt(this.G_HearbeatInterval);
            dest.writeString(this.G_CardPin);
            dest.writeInt(this.G_MeterValueInterval);
            dest.writeString(this.mac);
            dest.writeInt(this.G_MaxTemperature);
            dest.writeInt(this.G_MaxCurrent);
            dest.writeString(this.vendor);
            dest.writeString(this.host);
            dest.writeString(this.mask);
            dest.writeString(this.G_Authentication);
            dest.writeInt(this.G_WebSocketPingInterval);
            dest.writeString(this.ip);
            dest.writeString(this.G_ChargerMode);
            dest.writeString(this.dns);
            dest.writeString(this.site);
            dest.writeString(this.G_ChargerLanguage);
            dest.writeInt(this.online);
            dest.writeString(this.status_2);
            dest.writeString(this.status_4);
            dest.writeString(this.gateway);
            dest.writeString(this.status_3);
            dest.writeTypedList(this.priceConf);
            dest.writeInt(this.G_SolarMode);
            dest.writeFloat(this.G_SolarLimitPower);
            dest.writeString(this.symbol);
            dest.writeString(this.status_1);
        }

        public void readFromParcel(Parcel source) {
            this.connectors = source.readInt();
            this.chargeId = source.readString();
            this.userPhone = source.readString();
            this.name = source.readString();
            this.model = source.readString();
            this.time = source.readLong();
            this.userName = source.readString();
            this.type = source.readInt();
            this.userId = source.readString();
            this.status = source.createStringArrayList();
            this.solar = source.readInt();
            this.isChecked = source.readByte() != 0;
            this.devType = source.readInt();
            this.G_ExternalLimitPower = source.readInt();
            this.code = source.readString();
            this.G_HearbeatInterval = source.readInt();
            this.G_CardPin = source.readString();
            this.G_MeterValueInterval = source.readInt();
            this.mac = source.readString();
            this.G_MaxTemperature = source.readInt();
            this.G_MaxCurrent = source.readInt();
            this.vendor = source.readString();
            this.host = source.readString();
            this.mask = source.readString();
            this.G_Authentication = source.readString();
            this.G_WebSocketPingInterval = source.readInt();
            this.ip = source.readString();
            this.G_ChargerMode = source.readString();
            this.dns = source.readString();
            this.site = source.readString();
            this.G_ChargerLanguage = source.readString();
            this.online = source.readInt();
            this.status_2 = source.readString();
            this.status_4 = source.readString();
            this.gateway = source.readString();
            this.status_3 = source.readString();
            this.priceConf = source.createTypedArrayList(PriceConfBean.CREATOR);
            this.G_SolarMode = source.readInt();
            this.G_SolarLimitPower = source.readFloat();
            this.symbol = source.readString();
            this.status_1 = source.readString();
        }

        public DataBean() {
        }

        protected DataBean(Parcel in) {
            this.connectors = in.readInt();
            this.chargeId = in.readString();
            this.userPhone = in.readString();
            this.name = in.readString();
            this.model = in.readString();
            this.time = in.readLong();
            this.userName = in.readString();
            this.type = in.readInt();
            this.userId = in.readString();
            this.status = in.createStringArrayList();
            this.solar = in.readInt();
            this.isChecked = in.readByte() != 0;
            this.devType = in.readInt();
            this.G_ExternalLimitPower = in.readInt();
            this.code = in.readString();
            this.G_HearbeatInterval = in.readInt();
            this.G_CardPin = in.readString();
            this.G_MeterValueInterval = in.readInt();
            this.mac = in.readString();
            this.G_MaxTemperature = in.readInt();
            this.G_MaxCurrent = in.readInt();
            this.vendor = in.readString();
            this.host = in.readString();
            this.mask = in.readString();
            this.G_Authentication = in.readString();
            this.G_WebSocketPingInterval = in.readInt();
            this.ip = in.readString();
            this.G_ChargerMode = in.readString();
            this.dns = in.readString();
            this.site = in.readString();
            this.G_ChargerLanguage = in.readString();
            this.online = in.readInt();
            this.status_2 = in.readString();
            this.status_4 = in.readString();
            this.gateway = in.readString();
            this.status_3 = in.readString();
            this.priceConf = in.createTypedArrayList(PriceConfBean.CREATOR);
            this.G_SolarMode = in.readInt();
            this.G_SolarLimitPower = in.readFloat();
            this.symbol = in.readString();
            this.status_1 = in.readString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.data);
    }

    public void readFromParcel(Parcel source) {
        this.data = source.createTypedArrayList(DataBean.CREATOR);
    }

    public ChargingBean() {
    }

    protected ChargingBean(Parcel in) {
        this.data = in.createTypedArrayList(DataBean.CREATOR);
    }

    public static final Parcelable.Creator<ChargingBean> CREATOR = new Parcelable.Creator<ChargingBean>() {
        @Override
        public ChargingBean createFromParcel(Parcel source) {
            return new ChargingBean(source);
        }

        @Override
        public ChargingBean[] newArray(int size) {
            return new ChargingBean[size];
        }
    };
}
