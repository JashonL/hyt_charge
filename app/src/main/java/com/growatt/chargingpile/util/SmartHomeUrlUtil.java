package com.growatt.chargingpile.util;

/**
 * Created by Administrator on 2018/6/12.
 */

public class SmartHomeUrlUtil {

//    public static final String SMARTHOME_BASE_URL = "http://chat.growatt.com";

    //欧洲服务器
    public static final String SMARTHOME_BASE_URL = "https://charge.growatt.com";

    //测试地址
//    public static final String SMARTHOME_BASE_URL = "http://192.168.30.69:8080";

//    public static final String SMARTHOME_BASE_URL = "http://192.168.3.228";

    public static String getServer() {
        return "charge.growatt.com";
    }

    public static String postGetChargingList() {
        return SMARTHOME_BASE_URL + "/ocpp/api/list";
    }

    public static String postGetChargingGunNew() {
        return SMARTHOME_BASE_URL + "/ocpp/charge/info";
    }

    public static String postGetAuthorizationList() {
        return SMARTHOME_BASE_URL + "/ocpp/api/userList";
    }

    public static String postAddAuthorizationUser() {
        return SMARTHOME_BASE_URL + "/ocpp/api/author";
    }

    public static String postDeleteAuthorizationUser() {
        return SMARTHOME_BASE_URL + "/ocpp/api/deleteAuthor";
    }

    public static String postAddCharging() {
        return SMARTHOME_BASE_URL + "/ocpp/api/add";
    }

    public static String postUserChargingRecord() {
        return SMARTHOME_BASE_URL + "/ocpp/api/chargeRecord";
    }

    public static String postSetChargingParams() {
        return SMARTHOME_BASE_URL + "/ocpp/api/config";
    }

    public static String postRequestChargingParams() {
        return SMARTHOME_BASE_URL + "/ocpp/api/configInfo";
    }

    public static String postRequestChargingReserveList() {
        return SMARTHOME_BASE_URL + "/ocpp/api/reserveList";
    }

    public static String postUpdateChargingReservelist() {
        return SMARTHOME_BASE_URL + "/ocpp/api/updateReserve";
    }

    public static String postRequestReseerveCharging() {
        return SMARTHOME_BASE_URL + "/ocpp/cmd/";
    }

    public static String postRequestDeleteCharging() {
        return SMARTHOME_BASE_URL + "/ocpp/api/deleteAuthor";
    }

    public static String postRequestReserveNowList() {
        return SMARTHOME_BASE_URL + "/ocpp/api/ReserveNow";
    }

    public static String postGetDemoUser() {
        return SMARTHOME_BASE_URL + "/ocpp/user/glanceUser";
    }

    public static String postGetDemoCode() {
        return SMARTHOME_BASE_URL + "/ocpp/user/checkCode";
    }

    public static String postRequestSwitchAp() {
        return SMARTHOME_BASE_URL + "/ocpp/user/appMode";
    }

    public static String postByCmd() {
        return SMARTHOME_BASE_URL + "/ocpp/api/";
    }

}
