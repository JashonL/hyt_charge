package com.growatt.chargingpile.util;

import static com.growatt.chargingpile.application.MyApplication.context;
import static com.growatt.chargingpile.jpush.TagAliasOperatorHelper.sequence;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.growatt.chargingpile.MainActivity;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.activity.LoginActivity;
import com.growatt.chargingpile.application.MyApplication;
import com.growatt.chargingpile.bean.UserBean;
import com.growatt.chargingpile.connutil.GetUtil;
import com.growatt.chargingpile.connutil.PostUtil;
import com.growatt.chargingpile.connutil.Urlsutil;
import com.growatt.chargingpile.jpush.TagAliasOperatorHelper;
import com.growatt.chargingpile.listener.OnViewEnableListener;
import com.growatt.chargingpile.sqlite.SqliteUtil;

import org.json.JSONObject;

import java.lang.ref.SoftReference;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2018/10/17.
 */

public class LoginUtil {

    /**
     * 自动登录
     */
    public static void autoLogin(Context context, String userName, String password) {
        ossErrAutoLogin(context, userName, password, new OnViewEnableListener() {
        });
    }

    /**
     * 无感重新登录
     */
    public static void serverTimeOutLogin() {
//        T.make(R.string.login_expired, MyApplication.context);
//        SharedPreferencesUnit.getInstance(context).putInt(Constant.AUTO_LOGIN, 0);
//        SharedPreferencesUnit.getInstance(context).putInt(Constant.AUTO_LOGIN_TYPE, 0);
//        jumpActivity(context, LoginActivity.class);
//    /*
        Map<String, Object> map = SqliteUtil.inquirylogin();
//        String url = SqliteUtil.inquiryurl();
//        if (map != null && map.size() > 0 && (!TextUtils.isEmpty(url))) {
//          *//*  serverLogin(1, context, url, map.get("name").toString().trim(), map.get("pwd").toString().trim(), new OnViewEnableListener() {
//            });*//*
//            LoginUtil.login(context, map.get("name").toString().trim(), map.get("pwd").toString().trim(), new OnViewEnableListener() {
//                @Override
//                public void onViewEnable() {
//
//                }
//            });
//        } else {
//            SharedPreferencesUnit.getInstance(context).putInt(Constant.AUTO_LOGIN, 0);
//            SharedPreferencesUnit.getInstance(context).putInt(Constant.AUTO_LOGIN_TYPE, 0);
//            jumpActivity(context, LoginActivity.class);
//        }*/

        LoginUtil.notInductiveLogin((String) map.get("name"), (String) map.get("pwd"));

    }

    /**
     * 用户被挤下线  返回到登录界面
     */
    public static void pressOutLogin() {
        T.make(R.string.login_expired, MyApplication.context);
        SharedPreferencesUnit.getInstance(context).putInt(Constant.AUTO_LOGIN, 0);
        SharedPreferencesUnit.getInstance(context).putInt(Constant.AUTO_LOGIN_TYPE, 0);
        jumpActivity(context, LoginActivity.class);
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
            }
        });
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
                context = context;
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
                int app_code = jsonObject.optInt("app_code", 0);
                SqliteUtil.setService("", app_code);
                JSONObject jsonObject2 = jsonObject.getJSONObject("user");
                UserBean userBean = new Gson().fromJson(jsonObject2.toString(), UserBean.class);
                //设置帐号是否是浏览帐号
                if (Cons.isflagId.equals(SmartHomeUtil.getUserName())) {
                    userBean.setAuthnum(1);
                } else {
                    userBean.setAuthnum(0);
                }
                Cons.userBean = userBean;
                if (loginType != 2) {
                    SqliteUtil.login(userName, password);
                }
                int autoLogin;
                int autoLoginType;
                if (SmartHomeUtil.isFlagUser()) {
                    autoLogin = 0;
                    autoLoginType = 0;
                } else {
                    autoLogin = 1;
                    autoLoginType = 1;
                }
                SharedPreferencesUnit.getInstance(context).putInt(Constant.AUTO_LOGIN, autoLogin);
                SharedPreferencesUnit.getInstance(context).putInt(Constant.AUTO_LOGIN_TYPE, autoLoginType);
                enableListener.onViewEnable();
                if (loginType == 0 || loginType == 2) {
                    jumpActivity(context, MainActivity.class);
                }
                Mydialog.Dismiss();
            } else {
                Mydialog.Dismiss();
                enableListener.onViewEnable();
                T.make(R.string.m20用户名密码错误, context);
//                jumpLoginActivity(context);
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
        Cons.setNoConfigBean(null);
//        JPushInterface.setAlias(act,1,"");
        setJpushAlias(act.getApplication());
        //设置不自动登录
        SharedPreferencesUnit.getInstance(act).putInt(Constant.AUTO_LOGIN, 0);
        SharedPreferencesUnit.getInstance(act).putInt(Constant.AUTO_LOGIN_TYPE, 0);
        List<SoftReference<Activity>> activityStack = MyApplication.getInstance().getmList();
        for (SoftReference<Activity> activity : activityStack) {
            if (activity != null && activity.get() != null) {
                Activity activity1 = activity.get();
//                if (activity1 instanceof MainActivity)continue;
                if ((activity1.getClass().equals(act.getClass()))) continue;//这里要忽略掉，要不然会闪屏
                activity1.finish();
            }
        }
        activityStack.clear();
        act.startActivity(new Intent(act, LoginActivity.class));
        act.finish();
    }


    public static void setJpushAlias(Context context) {
        Set<String> tags = new HashSet<String>();
        tags.add(SmartHomeUtil.getUserName());
        //删除别名和Tag
        TagAliasOperatorHelper.TagAliasBean tagAliasBean = new TagAliasOperatorHelper.TagAliasBean();
        tagAliasBean.action = TagAliasOperatorHelper.ACTION_DELETE;
        tagAliasBean.isAliasAction = true;
        tagAliasBean.alias = SmartHomeUtil.getUserName();
        tagAliasBean.tags = tags;
        sequence++;
        TagAliasOperatorHelper.getInstance().handleAction(context, sequence, tagAliasBean);

     /*   tagAliasBean.action = TagAliasOperatorHelper.ACTION_CLEAN;
        tagAliasBean.isAliasAction = false;
        sequence++;
        TagAliasOperatorHelper.getInstance().handleAction(context.getApplicationContext(), sequence, tagAliasBean);*/
    }


    public static void notInductiveLogin(final String userName, final String password) {
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "login");//cmd  注册
            object.put("userId", userName);//用户名
            object.put("password", password);//密码
            object.put("lan", getLanguage(context));
            object.put("version", Utils.getVersionName(context));
        } catch (Exception e) {
            e.printStackTrace();
        }

        PostUtil.postJson(SmartHomeUrlUtil.postByCmd(), object.toString(), new PostUtil.postListener() {
            @Override
            public void Params(Map<String, String> params) {

            }

            @Override
            public void success(String json) {
                try {
                    JSONObject object = new JSONObject(json);
                    int code = object.getInt("code");
                    if (code == 0) {
                        JSONObject jsonObject = object.optJSONObject("data");
                        UserBean userBean = new Gson().fromJson(jsonObject.toString(), UserBean.class);
                        //设置帐号是否是浏览帐号
                        if (Cons.isflagId.equals(userBean.getName())) {
                            userBean.setAuthnum(1);
                        } else {
                            userBean.setAuthnum(0);
                        }
                        Cons.userBean = userBean;
                        SqliteUtil.login(userName, password);
                        int autoLogin;
                        int autoLoginType;
                        if (SmartHomeUtil.isFlagUser()) {
                            autoLogin = 0;
                            autoLoginType = 0;
                        } else {
                            autoLogin = 1;
                            autoLoginType = 1;
                        }
                        SharedPreferencesUnit.getInstance(context).putInt(Constant.AUTO_LOGIN, autoLogin);
                        SharedPreferencesUnit.getInstance(context).putInt(Constant.AUTO_LOGIN_TYPE, autoLoginType);
                    } else {
                        //设置不自动登录
                        SharedPreferencesUnit.getInstance(context).putInt(Constant.AUTO_LOGIN, 0);
                        SharedPreferencesUnit.getInstance(context).putInt(Constant.AUTO_LOGIN_TYPE, 0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void LoginError(String str) {
                Mydialog.Dismiss();
            }
        });

    }


    /**
     * demo帐号登录
     */

    public static void demoLogin(Context context, String userName, String password) {
 /*       ossErrAutoLogin(context, userName, password, new OnViewEnableListener() {
            @Override
            public void onViewEnable() {
                super.onViewEnable();
            }
        });*/
        LoginUtil.login(context, userName, password, new OnViewEnableListener() {
            @Override
            public void onViewEnable() {

            }
        });
    }

    /**
     * 正常终端用户登录
     */
    public static void login(final Context context, final String userName, final String password, final OnViewEnableListener enableListener) {
        SqliteUtil.url(SmartHomeUrlUtil.getServer());
        Mydialog.Show(context);
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "login");//cmd  注册
            object.put("userId", userName);//用户名
            object.put("password", password);//密码
            object.put("lan", getLanguage(context));
            object.put("version", Utils.getVersionName(context));
        } catch (Exception e) {
            e.printStackTrace();
        }
        PostUtil.postJson(SmartHomeUrlUtil.postByCmd(), object.toString(), new PostUtil.postListener() {
            @Override
            public void Params(Map<String, String> params) {

            }

            @Override
            public void success(String json) {
                Mydialog.Dismiss();
                try {
                    JSONObject object = new JSONObject(json);
                    int code = object.getInt("code");
                    if (code == 0) {
                        T.make(R.string.m登录成功, context);
                        JSONObject jsonObject = object.optJSONObject("data");
                        UserBean userBean = new Gson().fromJson(jsonObject.toString(), UserBean.class);
                        //设置帐号是否是浏览帐号
                        if (Cons.isflagId.equals(userBean.getName())) {
                            userBean.setAuthnum(1);
                        } else {
                            userBean.setAuthnum(0);
                        }
                        Cons.userBean = userBean;
                        SqliteUtil.login(userName, password);
                        int autoLogin;
                        int autoLoginType;
                        if (SmartHomeUtil.isFlagUser()) {
                            autoLogin = 0;
                            autoLoginType = 0;
                        } else {
                            autoLogin = 1;
                            autoLoginType = 1;
                        }
                        SharedPreferencesUnit.getInstance(context).putInt(Constant.AUTO_LOGIN, autoLogin);
                        SharedPreferencesUnit.getInstance(context).putInt(Constant.AUTO_LOGIN_TYPE, autoLoginType);
                        enableListener.onViewEnable();
                        jumpActivity(context, MainActivity.class);
                    } else {
                        String errorMsg = object.optString("data");
                        enableListener.onFail();
                        T.make(errorMsg, context);
                        //设置不自动登录
                        SharedPreferencesUnit.getInstance(context).putInt(Constant.AUTO_LOGIN, 0);
                        SharedPreferencesUnit.getInstance(context).putInt(Constant.AUTO_LOGIN_TYPE, 0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void LoginError(String str) {
                Mydialog.Dismiss();
                enableListener.onFail();
            }
        });
    }

}
