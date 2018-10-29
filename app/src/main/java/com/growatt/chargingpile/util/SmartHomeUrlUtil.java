package com.growatt.chargingpile.util;

/**
 * Created by Administrator on 2018/6/12.
 */

public class SmartHomeUrlUtil {

    public static final String SMARTHOME_BASE_URL = "http://chat.growatt.com";

    //测试地址
//    public static final String SMARTHOME_BASE_URL = "http://192.168.30.69:8080";

//    public static final String SMARTHOME_BASE_URL = "http://192.168.3.228";

    //获取用户所有家庭能源设备数据总接口
    public static final String USER_TOTAL_AllLIST = SMARTHOME_BASE_URL + "/eic_web/tuya/totalList";

    //获取所有插座/温控器列表  0：插座 1：温控器
    public static final String GET_DEVICE_ONE_TYPE_LIST = SMARTHOME_BASE_URL + "/eic_web/tuya/devList";

    //获取单个插座数据详情
    public static final String GET_ONE_SOCKET_DATA = SMARTHOME_BASE_URL + "/eic_web/tuya/socketInfo";

    //获取单个温控器数据详情
    public static final String GET_ONE_THERMOSTAT_DATA = SMARTHOME_BASE_URL + "/eic_web/tuya/thermostatInfo";

    //注册到涂鸦成功告诉服务器
    public static final String REGIST_TUYA_SUCCESS = SMARTHOME_BASE_URL + "/eic_web/tuya/regist";

    //成功将设备添加到涂鸦
    public static final String ADD_DEVICE_SUCCESS = SMARTHOME_BASE_URL + "/eic_web/tuya/addDevice";

    //删除设备
    public static final String DELETE_DEVICE_SUCCESS = SMARTHOME_BASE_URL + "/eic_web/tuya/removeDevice";

    //下发指令成功
    public static final String SEND_COMMAND_SUCCESS = SMARTHOME_BASE_URL + "/eic_web/tuya/setting";

    //查询充电桩列表
    public static final String GET_MY_ADD_CHARGING_LIST = SMARTHOME_BASE_URL + "/ocpp/api/list";

    //查询充电枪详情
    public static final String GET_CHARGING_GUN_DATA = SMARTHOME_BASE_URL + "/ocpp/api/info";

    //获取已授权的用户列表
    public static final String GET_AUTHORIZATION_USEER_LIST = SMARTHOME_BASE_URL + "/ocpp/api/userList";

    //添加授权用户
    public static final String ADD_AUTHORIZATION_USERE = SMARTHOME_BASE_URL + "/ocpp/api/author";

    //注册并授权新用户
    public static final String REGISTER_AND_AUTHORIZATION_USERE = SMARTHOME_BASE_URL + "/ocpp/api/registerAuthor";

    //删除用户
    public static final String DELETE_UTHORIZATION_USERE = SMARTHOME_BASE_URL + "/ocpp/api/deleteAuthor";

    //添加充电桩
    public static final String ADD_CHARGING = SMARTHOME_BASE_URL + "/ocpp/api/add";

    //充电记录
    public static final String USER_CHARGING_RECORD = SMARTHOME_BASE_URL + "/ocpp/api/chargeRecord";

    //设置参数
    public static final String SET_CHARGING_PARAMS = SMARTHOME_BASE_URL + "/ocpp/api/config";

    //请求充电桩设置
    public static final String REQUEST_CHARGING_PARAMS = SMARTHOME_BASE_URL + "/ocpp/api/configInfo";

    //请求预约列表
    public static final String REQUEST_CHARGING_RESERVELIST = SMARTHOME_BASE_URL + "/ocpp/api/reserveList";

    //修改预约
    public static final String UPDATE_CHARGING_RESERVELIST = SMARTHOME_BASE_URL + "/ocpp/api/updateReserve";

    //充电指令
    public static final String REQUEST_RESEERVE_CHARGING = SMARTHOME_BASE_URL + "/ocpp/cmd/";

    //删除充电桩
    public static final String REQUEST_DELETE_CHARGING = SMARTHOME_BASE_URL + "/ocpp/api/deleteAuthor";

}
