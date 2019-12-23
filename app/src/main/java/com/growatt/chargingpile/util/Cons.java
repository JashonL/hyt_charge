package com.growatt.chargingpile.util;

import android.text.TextUtils;

import com.growatt.chargingpile.bean.RegisterMap;
import com.growatt.chargingpile.bean.UserBean;
import com.growatt.chargingpile.sqlite.SqliteUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Cons {
    //监控用户服务器url:server.growatt.com;
    public static String account_url;

    //获取account_url
    public static String getAccount_url() {
        if (!TextUtils.isEmpty(account_url)) {
            return account_url;
        }
        String url = SqliteUtil.inquiryurl();
        if (!TextUtils.isEmpty(url)) {
            return url;
        }
        return "";
    }

    public static String hour;
    public static String min;
    public static String userId = "";
    public static String plant;
    public static String url = "";
    public static String isflagId="ceshi00701";
    public static ArrayList<Map<String, Object>> plants = new ArrayList<Map<String, Object>>();
    public static RegisterMap regMap = new RegisterMap();
    public static UserBean userBean;
    public static boolean addQuestion = false;
    public static boolean isFirst = false;
    public static boolean isExit = false;
    public static int num = 0;

    public static List<Map<String, String>> videoList = new ArrayList<Map<String, String>>();
    public static boolean isCodeUpdate;
    public static String server = "http://server.growatt.com";


    //oss服务器列表map集合
    public static List<Map<String, String>> ossServerList;


    //登录接口中是否通过手机或邮箱验证
    public static boolean isValiEmail;
    public static boolean isValiPhone;
}
