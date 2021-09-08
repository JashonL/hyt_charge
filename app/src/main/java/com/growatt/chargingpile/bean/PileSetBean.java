package com.growatt.chargingpile.bean;

/**
 * Created by Administrator on 2018/10/22.
 */

public class PileSetBean {

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private int connectors;
        private String address = "";
        private String country = "";
        private String code = "";
        private String ip = "";
        private String dns = "";
        private String userId = "";
        private String version = "";
        private String mac = "";
        private String password = "";
        private String SerialNumber = "";
        private double rate = 0;
        private String chargeId = "";
        private String vendor = "";
        private String name = "";
        private String host = "";
        private long ctime = 0;
        private int online = 0;
        private String model = "";
        private int power = 0;
        private String status_1 = "";
        private String gateway = "";
        private int cid = 0;
        private String mask = "";
        private String site = "";
        private String G_Authentication = "";
        private String G_ChargerMode = "";
        private String unit;
        private String G_MaxCurrent;
        private String G_ExternalLimitPower;
        private String symbol;
        private int G_ExternalLimitPowerEnable;
        private int G_ExternalSamplingCurWring;
        private int G_SolarMode;
        private float G_SolarLimitPower;
        private int G_PeakValleyEnable;
        private String G_AutoChargeTime = "";
        private String G_RCDProtection = "";
        private String G_PowerMeterType = "";
        private String UnlockConnectorOnEVSideDisconnect = "";
        private String G_LowPowerReserveEnable;
        private String G_NetworkMode = "";
        private String G_LCDCloseEnable;

        private String G_WifiSSID = "";
        private String G_WifiPassword = "";

        public String getG_WifiSSID() {
            return G_WifiSSID;
        }

        public void setG_WifiSSID(String g_WifiSSID) {
            G_WifiSSID = g_WifiSSID;
        }

        public String getG_WifiPassword() {
            return G_WifiPassword;
        }

        public void setG_WifiPassword(String g_WifiPassword) {
            G_WifiPassword = g_WifiPassword;
        }

        public int getConnectors() {
            return connectors;
        }

        public void setConnectors(int connectors) {
            this.connectors = connectors;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getDns() {
            return dns;
        }

        public void setDns(String dns) {
            this.dns = dns;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getMac() {
            return mac;
        }

        public void setMac(String mac) {
            this.mac = mac;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getSerialNumber() {
            return SerialNumber;
        }

        public void setSerialNumber(String SerialNumber) {
            this.SerialNumber = SerialNumber;
        }

        public double getRate() {
            return rate;
        }

        public void setRate(double rate) {
            this.rate = rate;
        }

        public String getChargeId() {
            return chargeId;
        }

        public void setChargeId(String chargeId) {
            this.chargeId = chargeId;
        }

        public String getVendor() {
            return vendor;
        }

        public void setVendor(String vendor) {
            this.vendor = vendor;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public long getCtime() {
            return ctime;
        }

        public void setCtime(long ctime) {
            this.ctime = ctime;
        }

        public int getOnline() {
            return online;
        }

        public void setOnline(int online) {
            this.online = online;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public int getPower() {
            return power;
        }

        public void setPower(int power) {
            this.power = power;
        }

        public String getStatus_1() {
            return status_1;
        }

        public void setStatus_1(String status_1) {
            this.status_1 = status_1;
        }

        public String getGateway() {
            return gateway;
        }

        public void setGateway(String gateway) {
            this.gateway = gateway;
        }

        public int getCid() {
            return cid;
        }

        public void setCid(int cid) {
            this.cid = cid;
        }

        public String getMask() {
            return mask;
        }

        public void setMask(String mask) {
            this.mask = mask;
        }

        public String getSite() {
            return site;
        }

        public void setSite(String site) {
            this.site = site;
        }

        public String getG_Authentication() {
            return G_Authentication;
        }

        public void setG_Authentication(String g_Authentication) {
            G_Authentication = g_Authentication;
        }

        public String getG_ChargerMode() {
            return G_ChargerMode;
        }

        public void setG_ChargerMode(String g_ChargerMode) {
            G_ChargerMode = g_ChargerMode;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public String getG_MaxCurrent() {
            return G_MaxCurrent;
        }

        public void setG_MaxCurrent(String g_MaxCurrent) {
            G_MaxCurrent = g_MaxCurrent;
        }

        public String getG_ExternalLimitPower() {
            return G_ExternalLimitPower;
        }

        public void setG_ExternalLimitPower(String g_ExternalLimitPower) {
            G_ExternalLimitPower = g_ExternalLimitPower;
        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public int getG_ExternalLimitPowerEnable() {
            return G_ExternalLimitPowerEnable;
        }

        public void setG_ExternalLimitPowerEnable(int g_ExternalLimitPowerEnable) {
            G_ExternalLimitPowerEnable = g_ExternalLimitPowerEnable;
        }

        public int getG_ExternalSamplingCurWring() {
            return G_ExternalSamplingCurWring;
        }

        public void setG_ExternalSamplingCurWring(int g_ExternalSamplingCurWring) {
            G_ExternalSamplingCurWring = g_ExternalSamplingCurWring;
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

        public int getG_PeakValleyEnable() {
            return G_PeakValleyEnable;
        }

        public void setG_PeakValleyEnable(int g_PeakValleyEnable) {
            G_PeakValleyEnable = g_PeakValleyEnable;
        }

        public String getG_AutoChargeTime() {
            return G_AutoChargeTime;
        }

        public void setG_AutoChargeTime(String g_AutoChargeTime) {
            G_AutoChargeTime = g_AutoChargeTime;
        }

        public String getG_RCDProtection() {
            return G_RCDProtection;
        }

        public void setG_RCDProtection(String g_RCDProtection) {
            G_RCDProtection = g_RCDProtection;
        }

        public String getG_PowerMeterType() {
            return G_PowerMeterType;
        }

        public void setG_PowerMeterType(String g_PowerMeterType) {
            G_PowerMeterType = g_PowerMeterType;
        }

        public String getUnlockConnectorOnEVSideDisconnect() {
            return UnlockConnectorOnEVSideDisconnect;
        }

        public void setUnlockConnectorOnEVSideDisconnect(String unlockConnectorOnEVSideDisconnect) {
            UnlockConnectorOnEVSideDisconnect = unlockConnectorOnEVSideDisconnect;
        }


        public String getG_LowPowerReserveEnable() {
            return G_LowPowerReserveEnable;
        }

        public void setG_LowPowerReserveEnable(String g_LowPowerReserveEnable) {
            G_LowPowerReserveEnable = g_LowPowerReserveEnable;
        }

        public String getNetMode() {
            return G_NetworkMode;
        }

        public void setNetMode(String netMode) {
            G_NetworkMode = netMode;
        }

        public String getG_NetworkMode() {
            return G_NetworkMode;
        }

        public void setG_NetworkMode(String g_NetworkMode) {
            G_NetworkMode = g_NetworkMode;
        }

        public String getG_LCDCloseEnable() {
            return G_LCDCloseEnable;
        }

        public void setG_LCDCloseEnable(String g_LCDCloseEnable) {
            G_LCDCloseEnable = g_LCDCloseEnable;
        }
    }
}
