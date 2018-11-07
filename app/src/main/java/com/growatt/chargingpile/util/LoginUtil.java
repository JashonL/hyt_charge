package com.growatt.chargingpile.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.activity.ChargingPileActivity;
import com.growatt.chargingpile.activity.LoginActivity;
import com.growatt.chargingpile.application.MyApplication;
import com.growatt.chargingpile.bean.UserBean;
import com.growatt.chargingpile.connutil.GetUtil;
import com.growatt.chargingpile.connutil.PostUtil;
import com.growatt.chargingpile.connutil.Urlsutil;
import com.growatt.chargingpile.listener.OnViewEnableListener;
import com.growatt.chargingpile.sqlite.SqliteUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.Map;

/**
 * Created by Administrator on 2018/10/17.
 */

public class LoginUtil {

    /**
     * 自动登录
     */
    public static void autoLogin( Context context,  String userName, String password) {
        ossErrAutoLogin(context, userName, password, new OnViewEnableListener(){});
    }


    /**
     * server登录超时，重新登录
     */
    public static void serverTimeOutLogin() {
        Map<String, Object> map = SqliteUtil.inquirylogin();
        String url = SqliteUtil.inquiryurl();
        if (map != null && map.size() > 0 && (!TextUtils.isEmpty(url))) {
            serverLogin(1, MyApplication.context, url, map.get("name").toString().trim(), map.get("pwd").toString().trim(), new OnViewEnableListener() {
            });
        } else {
            jumpLoginActivity(MyApplication.context);
        }
    }



    /**
     * server 用户直接登录无需获取服务器地址
     *
     * @param context
     * @param userName
     * @param password
     */
    public static void serverLogin(Context context, String userServerUrl, String userName, String password, OnViewEnableListener enableListener) {
        serverLogin(0, context, userServerUrl, userName, password, enableListener);
    }


    /**
     * server 用户直接登录无需获取服务器地址
     *
     * @param context
     * @param userName
     * @param password
     * @param loginType :代表登录类型：0：代表正常登录；1：代表server登录超时重新登录;
     */
    public static void serverLogin(final int loginType, final Context context, String userServerUrl, final String userName, final String password, final OnViewEnableListener enableListener) {
        Cons.account_url = userServerUrl;
        SqliteUtil.url(userServerUrl);
        Urlsutil.setUrl_Full(userServerUrl);
        if (loginType != 1) {
            Mydialog.Show(context);
        }
        PostUtil.post(new Urlsutil().cusLogin, new PostUtil.postListener() {
            @Override
            public void success(String json) {
                serverLoginParse(json, userName, password, context, enableListener, loginType, null);
            }

            @Override
            public void Params(Map<String, String> params) {
                params.put("userName", userName);
                params.put("password", MD5andKL.encryptPassword(password));
            }

            @Override
            public void LoginError(String str) {
                Mydialog.Dismiss();
                enableListener.onViewEnable();
                jumpLoginActivity(context);
            }
        });
    }


    /**
     * 跳转到登录界面
     *
     * @param context
     */
    public static void jumpLoginActivity(Context context) {
        jumpActivity(context, LoginActivity.class);
    }

    /**
     * 跳转到指定界面
     *
     * @param context
     * @param clazz
     */
    public static void jumpActivity(Context context, Class<?> clazz) {
        try {
            if (context == null) {
                context = MyApplication.context;
            }
            if (context instanceof Activity) {
                Activity act = (Activity) context;
                act.startActivity(new Intent(act, clazz));
            } else {
                Intent intent = new Intent(context, clazz);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void serverLoginParse(String json, String userName, String password, Context context, OnViewEnableListener enableListener, int loginType, String plantId) {
        try {
            JSONObject jsonObject10 = new JSONObject(json);
            JSONObject jsonObject = jsonObject10.getJSONObject("back");
            if (jsonObject.opt("success").toString().equals("true")) {
                if ("1".equals(jsonObject.opt("service").toString())) {
                    Cons.addQuestion = true;
                } else {
                    Cons.addQuestion = false;
                }
                int app_code = jsonObject.optInt("app_code", 0);
                if (app_code > SqliteUtil.getApp_Code() || SqliteUtil.getApp_Code() == -1) {
                    Cons.isCodeUpdate = true;
                } else {
                    Cons.isCodeUpdate = false;
                }
                SqliteUtil.setService("", app_code);
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                JSONObject jsonObject2 = jsonObject.getJSONObject("user");
                UserBean userBean = new Gson().fromJson(jsonObject2.toString(), UserBean.class);
                Cons.userId = userBean.getId();
                //浏览账号
                if ("2".equals(userBean.rightlevel) || userBean.parentUserId > 0) {
                    Cons.isflag = true;
                }
                if (userBean.getIsValiEmail() == 1) {
                    Cons.isValiEmail = true;
                } else {
                    Cons.isValiEmail = false;
                }
                if (userBean.getIsValiPhone() == 1) {
                    Cons.isValiPhone = true;
                } else {
                    Cons.isValiPhone = false;
                }
                Cons.userBean = userBean;
                if (loginType != 2) {
                    SqliteUtil.login(userName, password);
                }
                SharedPreferencesUnit.getInstance(context).putInt(Constant.AUTO_LOGIN, 1);
                SharedPreferencesUnit.getInstance(context).putInt(Constant.AUTO_LOGIN_TYPE, 1);
                enableListener.onViewEnable();
                if (loginType == 0 || loginType == 2) {
                    jumpActivity(context, ChargingPileActivity.class);
                }
                Mydialog.Dismiss();
            } else {
                Mydialog.Dismiss();
                enableListener.onViewEnable();
                T.make(R.string.m20用户名密码错误, context);
//                jumpLoginActivity(context);
                Constant.isOss2Server = false;
                //设置不自动登录
                SharedPreferencesUnit.getInstance(context).putInt(Constant.AUTO_LOGIN, 0);
                SharedPreferencesUnit.getInstance(context).putInt(Constant.AUTO_LOGIN_TYPE, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Mydialog.Dismiss();
            enableListener.onViewEnable();
//            jumpLoginActivity(context);
        }
    }

    /**
     * 当登陆oss发送错误时直接在server登陆，根据语言从server或-cn获取服务器地址，获取失败后交换服务器访问
     *
     * @param context
     * @param userName
     * @param password
     */
    public static int serverNum = 1;

    public static void ossErrAutoLogin(final Context context, final String userName, final String password, final OnViewEnableListener enableListener) {
        Mydialog.Show(context);
        String serverUrl = Urlsutil.getServerUrlByNameNew;
        if (getLanguage(context) == 0) {
            serverUrl = Urlsutil.getServerUrlByNameNew_cn;
        }
        if (serverNum == 2) {
            if (Urlsutil.getServerUrlByNameNew_cn.equals(serverUrl)) {
                serverUrl = Urlsutil.getServerUrlByNameNew;
            } else {
                serverUrl = Urlsutil.getServerUrlByNameNew_cn;
            }
        }
        GetUtil.getParamsServerUrl(serverUrl, new PostUtil.postListener() {
            @Override
            public void success(String json) {
                Mydialog.Dismiss();
                serverNum = 1;
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String success = jsonObject.get("success").toString();
                    if (success.equals("true")) {
                        String msg = jsonObject.getString("msg");
                        serverLogin(context, msg, userName, password, enableListener);
                    } else {
                        Mydialog.Dismiss();
                        enableListener.onViewEnable();
                      /*  if (context instanceof WelcomeActivity){
                            jumpLoginActivity(context);
                        }*/
                        T.make(R.string.m20用户名密码错误, context);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Mydialog.Dismiss();
                    enableListener.onViewEnable();
                   /* if (context instanceof WelcomeActivity){
                        jumpLoginActivity(context);
                    }*/
                }
            }

            @Override
            public void Params(Map<String, String> params) {
                params.put("userName", userName);
            }

            @Override
            public void LoginError(String str) {
                Mydialog.Dismiss();
                switch (serverNum) {
                    case 1:
                        serverNum = 2;
                        ossErrAutoLogin(context, userName, password, enableListener);
                        break;
                    case 2:
                        serverNum = 1;
                        enableListener.onViewEnable();
                        jumpLoginActivity(context);
                        break;
                }
            }
        });
    }

    /**
     * 获取是中文还是其他：0：代表中国；1：代表外国
     *
     * @param context
     * @return
     */
    public static int getLanguage(Context context) {
        int lan = 1;
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.toLowerCase().contains("zh")) {
            lan = 0;
        }
        return lan;
    }


    /**
     * 所有登出操作：添加电站做登出
     *
     * @param act
     */
    public static void logout(Activity act) {
        SqliteUtil.url("");
        SqliteUtil.plant("");
        Urlsutil.setUrl_Full("");
        act.startActivity(new Intent(act, LoginActivity.class));
        act.finish();

        //设置不自动登录
        SharedPreferencesUnit.getInstance(act).putInt(Constant.AUTO_LOGIN, 0);
        SharedPreferencesUnit.getInstance(act).putInt(Constant.AUTO_LOGIN_TYPE, 0);
    }


}
