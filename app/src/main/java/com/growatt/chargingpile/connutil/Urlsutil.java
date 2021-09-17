package com.growatt.chargingpile.connutil;

import android.text.TextUtils;

import com.growatt.chargingpile.sqlite.SqliteUtil;

import java.io.Serializable;

public class Urlsutil implements Serializable {

    public static String serverurl = "http://server-api.growatt.com";
    public static String url = "http://server-api.growatt.com";
//    public static String url = "http://192.168.3.214:8081/ShineServer_2016";
    public static String url_cn = "http://server-cn-api.growatt.com";
    public static String url_host = "server-api.growatt.com";//注册时用
    public static String url_cn_host = "server-cn-api.growatt.com";//注册时用


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
                return "https://" + replaceUrl(u);
            } else {
                return serverurl;
            }
//        } else {//浏览账号
////			return Cons.guestyrl;
//            return getUrl_Full();
//        }
    }

    //注册
    public String creatAccount = GetUrl() + "/newRegisterAPI.do?op=creatAccount";
    //登录  密码由客户端用MD5加密
    public String cusLogin = GetUrl() + "/newLoginAPI.do";

    //	获取国家和城市列表
    public String getServerUrl = url + "/newLoginAPI.do?op=getServerUrl";

    //	修改用户信息
    public String updateUser = GetUrl() + "/newUserAPI.do?op=updateUser";

    //	修改用户密码
    public String updateUserPassword = GetUrl() + "/newUserAPI.do?op=updateUserPassword";


    //根据用户名或者采集器序列号获取服务器
    public String postGetServerUrlByParam = GetUrl() + "/newForgetAPI.do?op=getServerUrlByParam";

    //获取app错误信息
    public String postSaveAppErrorMsg = GetUrl() + "/newLoginAPI.do?op=saveAppErrorMsg";

    //根据条件进行验证邮箱和手机号码
    public String postValPhoneOrEmail = GetUrl() + "/newLoginAPI.do?op=validate";
    //根据条件修改是否已经验证信息
    public String postUpdateVal = GetUrl() + "/newLoginAPI.do?op=updateValidate";
    //新版获取用户对应的服务器不分GET，POST
    public static String getServerUrlByNameNew = url + "/newLoginAPI.do?op=getServerUrlByName";
    public static String getServerUrlByNameNew_cn = url_cn + "/newLoginAPI.do?op=getServerUrlByName";
    /**
     * 根据用户名获取用户id
     * http://192.168.3.214:8081/ShineServer_2016/QXRegisterAPI.do?op=getUserIdByID
     */
    public String postServerUserId = GetUrl() + "/QXRegisterAPI.do?op=getUserIdByID";
    /**
     * 注销账户
     */
    public String postLogoutUserByName=GetUrl()+"/newUserAPI.do?op=cancellationUser";
}