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
        private String site="";
        private String G_Authentication="";
        private String G_ChargerMode="";

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
    }
}
