package com.growatt.chargingpile.connutil;

import android.text.TextUtils;

import com.growatt.chargingpile.sqlite.SqliteUtil;

import java.io.Serializable;

public class Urlsutil implements Serializable {
//    public static String serverurl = "http://server.growatt.com";
//    public static String url = "http://server.growatt.com";
////    public static String url = "http://192.168.3.214:8081/ShineServer_2016";
//    public static String url_cn = "http://server-cn.growatt.com";
//    public static String url_host = "server.growatt.com";//注册时用
//    public static String url_cn_host = "server-cn.growatt.com";//注册时用
    /**
     * 服务器地址全局替换
     */
    public static String serverurl = "http://server-api.growatt.com";
    public static String url = "http://server-api.growatt.com";
//    public static String url = "http://192.168.3.214:8081/ShineServer_2016";
    public static String url_cn = "http://server-cn-api.growatt.com";
    public static String url_host = "server-api.growatt.com";//注册时用
    public static String url_cn_host = "server-cn-api.growatt.com";//注册时用
    /*测试地址*/
//    public static String serverurl = "http://ftp.growatt.com";
//    public static String url = "http://ftp.growatt.com";

    //public static String serverurl = "http://192.168.3.35:8081";
//	public  String serverurl="http://server-cn.growatt.com";url
//	public  String serverurl="http://server.smten.com";
//	public  String serverurl="http://192.168.3.213:8080/ShineServer_2016";
//	public  String serverurl="http://192.168.3.32:8080/ShineServer_2016";
//	public  String serverurl="http://192.168.3.32:8081/ShineServer_2016";
//	public  static String serverurl="http://120.76.77.124:8080/ShineServer_2016";
//	public  String serverurl="http://120.76.153.241:8080/ShineServer_2016";
//	public static String serverurl="http://47.88.0.98:8080/ShineServer_2016";
//	public static String serverurl="http://test.growatt.com";
//	public  String serverurl="http://odm.growatt.com";
//	public  String serverurl="http://112.74.206.222:8080/ShineServer_2016";
//	public  String serverurl="http://47.88.0.98:8080/ShineServer_2016";
//	public  String serverurl="http://120.76.153.241:8080/ShineServer_2016";
//    public static String serverurl = "http://192.168.3.214:8081/ShineServer_2016";
//	public  static String serverurl="http://112.74.129.27";
//	public  static String serverurl="http://192.168.3.35:8081";
//		public  static String serverurl="http://ftp.growatt.com";
//		public  static String serverurl="http://tyy.smten.com";
//	public  static String serverurl="http://192.168.3.117:8080/ShineServer_2016";

    //	Cons.url动态获取服务器临时变量:会变化，odm是固定：server.growatt.com
    public static String url_cons = url_host;

    public static void setUrl_cons(String url) {
        url_cons = url;
    }

    public static String getUrl_cons() {
        return url_cons;
    }

    //Cons.guestyrl动态获取服务器临时变量:会变化，odm是固定：http://server.growatt.com
    public static String url_full = serverurl;

    public static void setUrl_Full(String url) {
        if (TextUtils.isEmpty(url)) {
            url_full = "";
        } else {
            url_full = "http://" + replaceUrl(url);
        }
    }

    public static String getUrl_Full() {
        return url_full;
    }

    public static Urlsutil instance;

    public Urlsutil() {
    }

    public static Urlsutil getInstance() {
        if (instance == null) {
            instance = new Urlsutil();
        }
        return instance;
    }

    /**
     * 将服务器原有api替换成新api,
     * 为实现前后端分离，即日起APP/API实行新接口。
     * 原先调用接口server.growatt.com    改成  server-api.growatt.com
     * 原先调用接口server-cn.growatt.com  改成  server-cn-api.growatt.com
     *
     * @param preUrl
     * @return
     */
    public static String replaceUrl(String preUrl) {
        if ("server-cn.growatt.com".equals(preUrl)) {
            preUrl = url_cn_host;
        } else if ("server.growatt.com".equals(preUrl)) {
            preUrl = url_host;
        }
        return preUrl;
    }

    public String GetUrl() {
//        return Cons.server;
//        return serverurl;
//        if (Cons.isflag == false) {
            String u = SqliteUtil.inquiryurl();
            if (!TextUtils.isEmpty(u)) {
                return "http://" + replaceUrl(u);
            } else {
                return serverurl;
            }
//        } else {//浏览账号
////			return Cons.guestyrl;
//            return getUrl_Full();
//        }
    }

    //	public static String url="http://192.168.3.32:8080/ShineServer_2016";
    //验证数据采集器效验码是否正确(注册时)
    public String checkDataLogSn = GetUrl() + "/registerAPI.do?op=checkDataLogSn";
    //验证用户名是否已被使用(注册时)
    public String regUserName = GetUrl() + "/userInfoAPI.do";
    //注册
    public String creatAccount = GetUrl() + "/newRegisterAPI.do?op=creatAccount";
    //登录  密码由客户端用MD5加密
    public String cusLogin = GetUrl() + "/newLoginAPI.do";
    //登录示例电站
    public String demoLogin = GetUrl() + "/demoLoginAPI.do";
    //电站列表
    public String PlantList = GetUrl() + "/PlantListAPI.do";
    //检测电站名是否已被使用(添加电站)
    public String plantAcheckPlantName = GetUrl() + "/plantA.do?op=checkPlantName";
    //修改电站
    public String plantAupdate = GetUrl() + "/plantA.do?op=update";
    //删除电站
    public String plantAdel = GetUrl() + "/plantA.do?op=del";
    //修改电站概貌图片
    public String plantAupdateImg = GetUrl() + "/newPlantAPI.do?op=updateImg";
    //	修改电站地图图片
    public String plantAupdatePlantMap = GetUrl() + "/newPlantAPI.do?op=updatePlantMap";
    //	输出电站概貌图片
    public String plantAgetImg = GetUrl() + "/newPlantAPI.do?op=getImg";
    //	获取电站概述
    public String plantA = GetUrl() + "/plantA.do";
    //	获取电站设备列表——逆变器
    public String plantAgetInvs = GetUrl() + "/plantA.do?op=getInvs";
    //	获取电站设备列表——环境监测仪
    public String plantAgetDlse = GetUrl() + "/plantA.do?op=getDlse";
    //	获取电站设备列表——智能电表
    public String plantAgetDlsa = GetUrl() + "/plantA.do?op=getDlsa";
    //	获取电站设备列表——汇流箱
    public String plantAgetDlsb = GetUrl() + "/plantA.do?op=getDlsb";
    //	获取电站其他设备列表——环境监测仪、智能电表、汇流箱
    public String plantAgetOtherDevices = GetUrl() + "/plantA.do?op=getOtherDevices";
    //	获取电站经纬度
    public String plantAgetLatLng = GetUrl() + "/plantA.do?op=getLatLng";
    //	修改电站经纬度
    public String plantAupdateSite = GetUrl() + "/plantA.do?op=updateSite";
    //	关于
    //	（1）公司信息与产品信息直接写入固定值
    //	（2）版本更新信息，为解析xml文件方式
    public String about = "http://server.growatt.com/app/xml/ShinePhone.xml";
    //	采集器是否需要校验
    public String isDatalogCheck = GetUrl() + "/registerAPI.do?op=isDatalogCheck";
    //	获取电站下采集器列表
    public String datalogAlist = GetUrl() + "/newDatalogAPI.do?op=datalogList";
    //	添加采集器
    public String datalogAadd = GetUrl() + "/newDatalogAPI.do?op=addDatalog";
    //	修改采集器
    public String datalogAupdate = GetUrl() + "/newDatalogAPI.do?op=updateDatalog";
    //	删除采集器
    public String datalogAdel = GetUrl() + "/newDatalogAPI.do?op=delDatalog";
    //	获取电站日志־
    public String getNotices = GetUrl() + "/DeviceEventAPI.do?op=getNotices";
    //	储能机设置 7种
    public String storageSet = GetUrl() + "/newTcpsetAPI.do?op=storageSet";


    //	public static String url1="http://192.168.3.32:8080/ShineServer_2016";
    //添加问题
    public String AddCQ = GetUrl() + "/questionAPI.do?op=addCustomerQuestion";


    //我的问题列表(get)
    public String questionList = GetUrl() + "/questionAPI.do?op=questionList";
    //我的某个问题详细信息(get)
    public String getQuestionInfo = GetUrl() + "/questionAPI.do?op=getQuestionInfo";
    //回复消息(get)
    public String replyMessage = GetUrl() + "/questionAPI.do?op=replyMessage";
    //	获取问题图片
    public String getQuestionIng = GetUrl() + "/questionAPI.do?op=getQuestionImg";

    //获取电站日曲线图数据
    public String PlantDetailAPI1 = GetUrl() + "/PlantDetailAPI.do?type=1";

    //获取电站月曲线图数据
    public String PlantDetailAPI2 = GetUrl() + "/PlantDetailAPI.do?type=2";

    //	获取电站年曲线图数据
    public String PlantDetailAPI3 = GetUrl() + "/PlantDetailAPI.do?type=3";

    //	获取电站总曲线图数据(前至今6年)
    public String PlantDetailAPI4 = GetUrl() + "/PlantDetailAPI.do?type=4";

    //	获取逆变器日曲线图数据
    public String inverterAgetDps = GetUrl() + "/newInverterAPI.do?op=getInverterData";

    //	获取逆变器月曲线图数据
    public String inverterAgetgetMps = GetUrl() + "/newInverterAPI.do?op=getMonthPac";

    //	获取逆变器年曲线图数据
    public String inverterAgetgetYps = GetUrl() + "/newInverterAPI.do?op=getYearPac";

    //	获取逆变器总曲线图数据
    public String inverterAgetgetTps = GetUrl() + "/newInverterAPI.do?op=getTotalPac";

    //	获取储能机日曲线图数据
    public String storageAgetgetDls = GetUrl() + "/newStorageAPI.do?op=getDayLineStorage";

    //	获取尚科储能机日曲线图数据
    public String skStorageAgetgetDls = GetUrl() + "/newStorageAPI.do?op=getDayLineStorage_sacolar";

    //	获取储能机月曲线图数据
    public String storageAgetgetMls = GetUrl() + "/newStorageAPI.do?op=getMonthLineStorage";

    //	获取尚科储能机月曲线图数据
    public String skStorageAgetgetMls = GetUrl() + "/newStorageAPI.do?op=getMonthLineStorage_sacolar";

    //	获取储能机年曲线图数据
    public String storageAgetgetYls = GetUrl() + "/newStorageAPI.do?op=getYearLineStorage";

    //	获取储能机年曲线图数据
    public String skStorageAgetgetYls = GetUrl() + "/newStorageAPI.do?op=getYearLineStorage_sacolar";

    //	获取储能机总曲线图数据
    public String storageAgetgetTls = GetUrl() + "/newStorageAPI.do?op=getTotalLineStorage";

    //	获取储能机总曲线图数据
    public String skStorageAgetgetTls = GetUrl() + "/newStorageAPI.do?op=getTotalLineStorage_sacolar";

    //	一次性获取所有设备
    public String questionAPI = GetUrl() + "/newPlantAPI.do?op=getAllDeviceList";

    //一次性获取所有设备区分尚科
    public String getAllDevice = GetUrl() + "/newPlantAPI.do?op=getAllDeviceListThree";

    //	密码找回
    public String sendResetEmail = GetUrl() + "/newForgetAPI.do?op=sendResetEmailByAccount";

    //	通过采集器找回密码
    public String sendResetSn = GetUrl() + "/newForgetAPI.do?op=sendResetEmailBySn";

    //	添加采集器
    public String addDatalog = GetUrl() + "/newDatalogAPI.do?op=addDatalog";

    //	获取质保信息
    public String getQualityInformation = GetUrl() + "/newQualityAPI.do?op=getQualityInformation";

    //	设置逆变器参数
    public String newinverterSet = GetUrl() + "/newTcpsetAPI.do?op=inverterSet";

    //	删除逆变器
    public String deleteInverter = GetUrl() + "/newInverterAPI.do?op=deleteInverter";

    //	修改逆变器信息
    public String updateInvInfo = GetUrl() + "/newInverterAPI.do?op=updateInvInfo";

    //	获取逆变器基础参数
    public String getInverterParams = GetUrl() + "/newInverterAPI.do?op=getInverterParams";


    //	获取逆变器信息
    public String getInverterData = GetUrl() + "/newInverterAPI.do?op=getInverterData";

    //	删除储能机
    public String deleteStorage = GetUrl() + "/newStorageAPI.do?op=deleteStorage";

    //	修改储能机信息
    public String updateStorageInfo = GetUrl() + "/newStorageAPI.do?op=updateStorageInfo";

    //	获取储能机基础参数
    public String getStorageParams = GetUrl() + "/newStorageAPI.do?op=getStorageParams";

    //	获取尚科储能机基础参数
    public String getSkStorageParams = GetUrl() + "/newStorageAPI.do?op=getStorageParams_sacolar";

    //	获取国家和城市列表
    public String getServerUrl = url + "/newLoginAPI.do?op=getServerUrl";

    //	修改用户信息
    public String updateUser = GetUrl() + "/newUserAPI.do?op=updateUser";

    //	修改用户密码
    public String updateUserPassword = GetUrl() + "/newUserAPI.do?op=updateUserPassword";

    //	获取逆变器报警日志־
    public String getStorageAlarm = GetUrl() + "/newStorageAPI.do?op=getStorageAlarm";

    //获取尚科设备报警日志
    public String getSacolarStorageAlarm = GetUrl() + "/newStorageAPI.do?op=getStorageAlarm_sacolar";

    //	获取储能机报警日志
    public String getInverterAlarm = GetUrl() + "/newInverterAPI.do?op=getInverterAlarm";

    //	设置逆变器参数
    public String inverterSet = GetUrl() + "/newTcpsetAPI.do?op=inverterSet";

    //	获取用户对应的服务器
    public String getUserServerUrl = url + "/newLoginAPI.do?op=getUserServerUrl";


    //	获取逆变器详细数据
    public String getInverterDetailData = GetUrl() + "/newInverterAPI.do?op=getInverterDetailData";

    //	获取逆变器详细数据新版
    public String getInverterDetailData_new = GetUrl() + "/newInverterAPI.do?op=getInverterDetailData_two";


    //	删除问题
    public String deleteQuestion = GetUrl() + "/questionAPI.do?op=deleteQuestion";

    //	获取电站光伏日曲线图数据
    public String photovoltaic = GetUrl() + "/newPlantDetailAPI.do?";

    //	获取电站储能机日曲线图数据
    public String getStroageAllEnergy = GetUrl() + "/newPlantDetailAPI.do?op=getStroageAllEnergy&type=";

    //	获取电站储能机日曲线图数据
    public String getStorageInfo = GetUrl() + "/newStorageAPI.do?op=getStorageInfo";


    //	获取尚科储能机详细数据
    public String getSkStorageInfo = GetUrl() + "/newStorageAPI.do?op=getStorageInfo_sacolar";


    //	获取更多产品列表
    public String getMoreProductList = GetUrl() + "/newProductAPI.do?op=getProductList";

    //	获取产品图片
    public String getProductImage = GetUrl() + "/newProductAPI.do?op=getProductImage&imageName=";

    //	获取产品参数图片
    public String getProductParamImage = GetUrl() + "/newProductAPI.do?op=getProductParamImage&imageName=";

    //	获取电站信息
    public String getPlant = GetUrl() + "/newPlantAPI.do?op=getPlant";

    //	修改电站
    public String updatePlant = GetUrl() + "/newPlantAPI.do?op=updatePlant";

    //	获取国家和地区
    public String getCountryCity = GetUrl() + "/newCountryCityAPI.do?op=getCountryCity";

    //	常见问题列表
    public String getUsualQuestionList = GetUrl() + "/questionAPI.do?op=getUsualQuestionList";

    //	常见问题详细信息
    public String getUsualQuestionInfo = GetUrl() + "/questionAPI.do?op=getUsualQuestionInfo";

    //	增值服务列表
    public String getExtensionList = GetUrl() + "/newExtensionAPI.do?op=getExtensionList&language=";

    //	增值服务详细信息
    public String getAppreciationInfo = GetUrl() + "/newExtensionAPI.do?op=getExtensionInfo";

    //	电话号码
    public String getServicePhoneNum = GetUrl() + "/newUserAPI.do?op=getServicePhoneNum&language=";

    //	广告
    public String getAdvertisingList = GetUrl() + "/newPlantAPI.do?op=getAdvertisingList&language=";

    //	广告图片
    public String getAdvertisingImages = GetUrl() + "/newPlantAPI.do?op=getAdvertisingImages&name=";


    //	获取能源状况
    public String getEnergyList = GetUrl() + "/newEnergyAPI.do?op=getEnergyList&plantId=";

    public String getQualityModelImage = GetUrl() + "/newQualityAPI.do?op=getQualityModelImage&model=";


    /**
     * by gai
     */
    //	获取服务器
    public String getServerUrlList = url + "/newLoginAPI.do?op=getServerUrlList";
    // 根据采集器序列号获取采集器信息
    public String getDatalogInfo = GetUrl() + "/newDatalogAPI.do?op=getDatalogInfo";
    //获取图片
    public String getImageInfo = "http://cdn.growatt.com/onlineservice/";
    ////	public  String getImageDetialInfo="http://cdn.growatt.com/onlineservice/"+SqliteUtil.inquirylogin().get("name")+"/";
    //更多产品图片
    public String getProductImageInfo = "http://cdn.growatt.com/publish/";
    //电站图片
    public static String getPlantImageInfo = "http://cdn.growatt.com/plantimg/";
    //添加电站
    public String postAddPlant = GetUrl() + "/newPlantAPI.do?op=addPlant";

    //根据用户名或者采集器序列号获取服务器
    public String postGetServerUrlByParam = GetUrl() + "/newForgetAPI.do?op=getServerUrlByParam";

    //获得视频目录列表
    public String getVideoDirList = GetUrl() + "/newVideoAPI.do?op=getVideoDirList";

    //获取视频信息
    public String getVideoInfoList = GetUrl() + "/newVideoAPI.do?op=getVideoInfoList";
    //获取常见问题通过类型
    public String getUsQuestionListByType = GetUrl() + "/questionAPI.do?op=getUsQuestionListByType";

    //获取APP版本和信息
    public String getAPPMessage = GetUrl() + "/newLoginAPI.do?op=getAPPMessage";
    //新版问题列表
    public String getQuestionInfoNew = GetUrl() + "/questionAPI.do?op=getQuestionInfoNew";
    //获取我的问题详情新接口（html)
    public String getQuestionInfoDetail = GetUrl() + "/questionAPI.do?op=getQuestionInfoDetail";
    //获取app错误信息
    public String postSaveAppErrorMsg = GetUrl() + "/newLoginAPI.do?op=saveAppErrorMsg";
    //根据用户名获取服务器地址
    public String getUserServerUrlPost = GetUrl() + "/newLoginAPI.do?op=getUserServerUrlPost";
    //RFStick配对
    public String postSinglepairRFStick = GetUrl() + "/newFtpAPI.do?op=singlepairRFStick";
    //新版使用手机号注册用户
    public String postMobileRegister = GetUrl() + "/newRegisterAPI.do?op=mobileRegister";
    //根据手机号发送验证码并获取验证码（国内）
    public String postSmsVerification = GetUrl() + "/newForgetAPI.do?op=smsVerification";
    //根据手机号发送验证码并获取验证码（国外）
    public String postSmsIntlVerification = GetUrl() + "/newForgetAPI.do?op=smsIntlVerification";
    //根据手机号和验证码重置密码
    public String postRestartUserPassword = GetUrl() + "/newForgetAPI.do?op=restartUserPassword";
    //根据手机号和验证码重置密码
    public String postRestartUserPassword2 = "/newForgetAPI.do?op=restartUserPassword";
    //获取储能机系统状态
    public String postStorageSystemStatusData = GetUrl() + "/newStorageAPI.do?op=getSystemStatusData";
    //获取尚科设备系统状态
    public String postSacolarStorageStatusData = GetUrl() + "/newStorageAPI.do?op=getSystemStatusData_sacolar";
    //获取储能机能源产耗
    public String postEnergyProdAndConsData = GetUrl() + "/newStorageAPI.do?op=getEnergyProdAndConsData";
    //获取尚科储能机能源产耗
    public String postSkEnergyProdAndConsData = GetUrl() + "/newStorageAPI.do?op=getEnergyProdAndConsData_sacolar";
    //获取储能机能源信息
    public String postStorageEnergyData = GetUrl() + "/newStorageAPI.do?op=getStorageEnergyData";
    //获取尚科设备能源信息
    public String postSkStorageEnergyData = GetUrl() + "/newStorageAPI.do?op=getStorageEnergyData_sacolar";
    //获取储能机能源概览
    public String postEnergyOverviewData = GetUrl() + "/newStorageAPI.do?op=getEnergyOverviewData";
    //获取尚科储能机能源概览
    public String postSkEnergyOverviewData = GetUrl() + "/newStorageAPI.do?op=getEnergyOverviewData_sacolar";
    //根据条件进行验证邮箱和手机号码
    public String postValPhoneOrEmail = GetUrl() + "/newLoginAPI.do?op=validate";
    //根据条件修改是否已经验证信息
    public String postUpdateVal = GetUrl() + "/newLoginAPI.do?op=updateValidate";
    //根据类型和日期获取设置密码
    public String postGetSetPassword = GetUrl() + "/newLoginAPI.do?op=getSetPass";
    //新版获取用户对应的服务器不分GET，POST
    public static String getServerUrlByNameNew = url + "/newLoginAPI.do?op=getServerUrlByName";
    public static String getServerUrlByNameNew_cn = url_cn + "/newLoginAPI.do?op=getServerUrlByName";
    //根据条件把问题设为已解决
    public String postSolveQuestion = GetUrl() + "/questionAPI.do?op=solveQuestion";
    //	储能机SPF5000设置
    public String postStorageSpf5kSet = GetUrl() + "/newTcpsetAPI.do?op=storageSPF5000Set";
    //尚科储能机设置
    public String sacolarStorageSet = GetUrl() + "/newTcpsetAPI.do?op=storageSacolarSet";

    /*Mix部分*/
//	获取MIX系统状态
    public String postMixSystemStatus = GetUrl() + "/newMixApi.do?op=getSystemStatus";
    //	获取mix的能源概览数据
    public String postMixEnergyOverview = GetUrl() + "/newMixApi.do?op=getEnergyOverview";
    //	获取mix的能源产耗
    public String postMixEnergyProdAndCons = GetUrl() + "/newMixApi.do?op=getEnergyProdAndCons";
    //	获取MIX能源信息
    public String postMixEnergy = GetUrl() + "/newMixApi.do?op=getMixEnergy";
    //	获取mix基础参数
    public String postMixParams = GetUrl() + "/newMixApi.do?op=getMixParams";
    //	获取MIX 信息
    public String postMixInfo = GetUrl() + "/newMixApi.do?op=getMixInfo";


    //	获取mix日曲线图数据(功率单位：W，电流单位：A，电压单位：V)
    public String postDayLineMix = GetUrl() + "/newMixApi.do?op=getDayLineMix";
    //	获取MiX月曲线图数据(单位：kWh)
    public String getMixMonthPac = GetUrl() + "/newMixApi.do?op=getMixMonthPac";
    //	获取mix年曲线图数据(单位：kWh)
    public String getMixYearPac = GetUrl() + "/newMixApi.do?op=getMixYearPac";
    //	获取mix总曲线图数据(单位：kWh)
    public String getMixTotalPac = GetUrl() + "/newMixApi.do?op=getMixTotalPac";
    //	获取mix报警日志
    public String getMixAlarm = GetUrl() + "/newMixApi.do?op=getMixAlarm";
    //	设置mix参数
    public String postMixSetApi = GetUrl() + "/newTcpsetAPI.do?op=mixSetApi";
    /**
     * 新版设置接口
     */
    public String postMixSetApiNew = GetUrl() + "/newTcpsetAPI.do?op=mixSetApiNew";

    //	修改Mix信息
    public String postUpdateMixInfoAPI = GetUrl() + "/newMixApi.do?op=updateMixInfoAPI";
    //	删除mix
    public String postDeleteMixAPI = GetUrl() + "/newMixApi.do?op=deleteMixAPI";
    /**
     * 获取各设备设置参数
     */
    public String postDeviceSetParams = GetUrl() + "/newMixApi.do?op=getMixSetParams";


    /*
    Max部分：等同于逆变器，接口不同
     */
    //获取max基础参数
    public String getInverterParams_max = GetUrl() + "/newInverterAPI.do?op=getInverterParams_max";
    //获取max详细数据
    public String getInverterDetailData_max = GetUrl() + "/newInverterAPI.do?op=getInverterDetailData_max";
    //获取max详细数据新版
    public String getInverterDetailData_max_new = GetUrl() + "/newInverterAPI.do?op=getInverterDetailData_max_two";


    //获取max信息日曲线(功率单位为W，电压：V，电流：A，发电量单位为kWh)
    public String getInverterData_max = GetUrl() + "/newInverterAPI.do?op=getInverterData_max";
    //获取max信息月曲线
    public String getMaxMonthPac = GetUrl() + "/newInverterAPI.do?op=getMaxMonthPac";
    //获取max信息年曲线
    public String getMaxYearPac = GetUrl() + "/newInverterAPI.do?op=getMaxYearPac";
    //获取max信息总曲线
    public String getMaxTotalPac = GetUrl() + "/newInverterAPI.do?op=getMaxTotalPac";
    //获取max告警
    public String getInverterAlarm_max = GetUrl() + "/newInverterAPI.do?op=getInverterAlarm_max";
    //设置max参数
    public String postInverterSet_max = GetUrl() + "/newTcpsetAPI.do?op=maxSetApi";
    //	修改Max信息
    public String postUpdateMaxInfo = GetUrl() + "/newInverterAPI.do?op=updateMaxInfo";
    //	删除Max
    public String postDeletemax = GetUrl() + "/newInverterAPI.do?op=deletemax";

    /**
     * OSS切入server登陆校验
     */
    public String postOssServerLogin() {
        return GetUrl() + "/newLoginAPI.do?op=apiserverlogin";
    }

    /*
JLINV锦浪部分：等同于逆变器，接口不同
 */
    //获取JLINV基础参数
    public String getInverterParams_jlinv = GetUrl() + "/newJlnvAPI.do?op=getInverterParams_Jlinv";
    //获取JLINV详细数据
    public String getInverterDetailData_jlinv = GetUrl() + "/newJlnvAPI.do?op=getInverterDetailData_Jlinv";


    //获取maxJLINV信息日曲线(功率单位为W，电压：V，电流：A，发电量单位为kWh)
    public String getInverterData_jlinv = GetUrl() + "/newJlnvAPI.do?op=getInverterData_Jlinv";
    //获取JLINV信息月曲线
    public String getMonthPac_jlinv = GetUrl() + "/newJlnvAPI.do?op=getJlinvMonthPac";
    //获取JLINV信息年曲线
    public String getYearPac_jlinv = GetUrl() + "/newJlnvAPI.do?op=getJlinvYearPac";
    //获取JLINV信息总曲线
    public String getTotalPac_jlinv = GetUrl() + "/newJlnvAPI.do?op=getJlinvTotalPac";
    //获取JLINV告警
    public String getInverterAlarm_jlinv = GetUrl() + "/newJlnvAPI.do?op=getInverterAlarm_Jlinv";
    //设置JLINV参数
    public String postInverterSet_jlinv = GetUrl() + "/newTcpsetAPI.do?op=maxSetApi";
    //	修改JLINV信息
    public String postUpdateInfo_jlinv = GetUrl() + "/newJlnvAPI.do?op=updateJlinvInfo";
    //	删除JLINV
    public String postDelete_jlinv = GetUrl() + "/newJlnvAPI.do?op=deleteJlinv";

    /*设备页电站管理*/

    //分页获取电站列表
    public String getAllPlantList = GetUrl() + "/newPlantAPI.do?op=getAllPlantList";
    //请求删除一个电站
    public String deletePlant = GetUrl() + "/newPlantAPI.do?op=delplant";
    /**
     * QQ微信登录
     */
    public String postQXLogin = GetUrl() + "/QXnewLoginAPI.do";
    /**
     * 通过手机号注册QQ微信账户
     */
    public String postQXRegist = GetUrl() + "/QXRegisterAPI.do?op=creatAccount";
    /**
     * QQ微信绑定用户
     */
    public String postQXBindUser = GetUrl() + "/QXRegisterAPI.do?op=UserBinding";
    /**
     * 获取QQ微信uid所属url
     */
    public String postUrlByuidAndType = GetUrl() + "/QXnewLoginAPI.do?op=getUrlByuidAndType";
    /**
     * 获取用户url:微信QQ
     */
    public String postUserServerUrlTwo = GetUrl() + "/QXnewLoginAPI.do?op=getUserServerUrlTwo";

    /**
     * 根据用户名获取用户id
     * http://192.168.3.214:8081/ShineServer_2016/QXRegisterAPI.do?op=getUserIdByID
     */
    public String postServerUserId = GetUrl() + "/QXRegisterAPI.do?op=getUserIdByID";
}