package com.growatt.chargingpile.bean;

import java.util.List;

/**
 * Created by Administrator on 2019/12/26.
 */

public class NoConfigBean {

    /**
     * password : 000000
     * skey : ["G_ChargerID","G_ServerURL","G_MaxCurrent","G_CardPin","G_ChargerNetGateway","G_ChargerNetMac","G_ChargerNetMask","G_ChargerNetDNS","G_Authentication","G_HearbeatInterval","G_WebSocketPingInterval","G_MeterValueInterval","G_MaxTemperature","G_ExternalLimitPower","G_ExternalLimitPowerEnable","G_ExternalSamplingCurWring","G_SolarMode","G_SolarLimitPower","G_PeakValleyEnable","G_RCDProtection"]
     * sfield : ["chargeId","host","G_MaxCurrent","G_CardPin","gateway","mac","mask","dns","G_Authentication","G_HearbeatInterval","G_WebSocketPingInterval","G_MeterValueInterval","G_MaxTemperature","G_ExternalLimitPower","G_ExternalLimitPowerEnable","G_ExternalSamplingCurWring","G_SolarMode","G_SolarLimitPower","G_PeakValleyEnable","G_RCDProtection"]
     */

    private String password;
    private List<String> skey;
    private List<String> sfield;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getSkey() {
        return skey;
    }

    public void setSkey(List<String> skey) {
        this.skey = skey;
    }

    public List<String> getSfield() {
        return sfield;
    }

    public void setSfield(List<String> sfield) {
        this.sfield = sfield;
    }
}
