package com.growatt.chargingpile.jpush;


import android.content.Context;

import cn.jpush.android.api.JPushInterface;

/**
 * 推送设置工具类
 */

public class PushUtils {

    public static int sequence = 1;
    /**
     * 获取绑定的别名
     */
    public static void getAlias(Context context,int sequence){
        JPushInterface.getAlias(context, sequence);
    }


    /**
     * 设置别名
     */
    public static void setAlias(Context context,String alias,int sequence){
        JPushInterface.setAlias(context, sequence, alias);
    }


    /**
     * 删除别名
     */

    public static void deleteAlias(Context context,int sequence){
        JPushInterface.getAlias(context, sequence);
    }


    /**
     *获取极光的RegistrationId
     */

    public static String getRegistrationId(Context context){
        return JPushInterface.getRegistrationID(context);
    }

}
