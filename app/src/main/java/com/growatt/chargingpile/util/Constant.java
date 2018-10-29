package com.growatt.chargingpile.util;

import android.content.Context;


public class Constant {
    //自动登录常量
    public static String AUTO_LOGIN = "com.growatt.chargingpile.auto_login";//0:代表不自动登录；1：代表自动登录
    public static String AUTO_LOGIN_TYPE = "com.growatt.chargingpile.auto_login_type";//0:oss用户；1：server用户
    //包名:用来识别 是否提示更新或是否显示更新
    public final static String google_package_name = "com.growatt.shinephones";

    //数据库版本
    public static String getSqliteName(Context context) {
        return "chargingpile.db";
    }

    //Max控制密码常量：
    public static boolean isOss2Server = false;

}
