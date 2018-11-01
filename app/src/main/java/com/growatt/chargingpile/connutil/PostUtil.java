package com.growatt.chargingpile.connutil;


import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.growatt.chargingpile.R;
import com.growatt.chargingpile.activity.LoginActivity;
import com.growatt.chargingpile.application.MyApplication;
import com.growatt.chargingpile.util.LoginUtil;
import com.growatt.chargingpile.util.Mydialog;
import com.growatt.chargingpile.util.T;

import org.xutils.common.Callback.Cancelable;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.common.util.LogUtil;
import org.xutils.ex.HttpException;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class PostUtil {

    public static void post(final String url, final postListener httpListener) {
        final Map<String, String> params = new HashMap<String, String>();
        LogUtil.i("post_utl:" + url);
        httpListener.Params(params);
        LogUtil.i("params:" + params.toString());
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String a = (String) msg.obj;
                switch (msg.what) {
                    case 0:
                        httpListener.success(a);
                        break;
                    case 1:
                        httpListener.LoginError(a);
                        break;
                    case 2:    //超时重新登录
                        LoginUtil.serverTimeOutLogin();
                }
            }
        };
        try {
            Cancelable cancle = XUtil.Post(url, params, new CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    LogUtil.i("post_result_load:" + result);
                    if (TextUtils.isEmpty(result)) {
                        Message msg = new Message();
                        msg.what = 1;
                        handler.sendMessage(msg);
                    } else if (result.contains("<!DOCTYPE")) {
                        //重新做登陆操作
                        Message.obtain(handler, 2, url).sendToTarget();
                    } else {
                        Message msg = new Message();
                        msg.what = 0;
                        msg.obj = result;
                        handler.sendMessage(msg);
                    }
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
//					 Toast.makeText(x.app(), ShineApplication.context.getString(R.string.Xutil_network_err)+":2"+ex.getMessage(), Toast.LENGTH_LONG).show();
                    if (ex instanceof HttpException) {
                        T.make(R.string.m网络错误, MyApplication.context);
                    } else if (ex instanceof SocketTimeoutException) {
                        T.make(R.string.m网络超时, MyApplication.context);
                    } else if (ex instanceof UnknownHostException) {
                        T.make(R.string.m服务器连接失败, MyApplication.context);
                    } else {
                        T.make(R.string.m服务器连接失败, MyApplication.context);
                    }

                    Message.obtain(handler, 1, ex.getMessage()).sendToTarget();
                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {

                }


            });
            if (cancle == null) {
                Message.obtain(handler, 1, url).sendToTarget();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Message msg = new Message();
            msg.what = 2;
            msg.obj = e.toString();
            handler.sendMessage(msg);
        }
    }

    public static void post(final String url, final boolean isLogErr, final postListener httpListener) {
        final Map<String, String> params = new HashMap<String, String>();
        LogUtil.i("post_utl:" + url);
        httpListener.Params(params);
        LogUtil.i("params:" + params.toString());
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String a = (String) msg.obj;
                switch (msg.what) {
                    case 0:
                        httpListener.success(a);
                        break;
                    case 1:
                        httpListener.LoginError(a);
                        break;
                    case 2:    //超时重新登录
                        LoginUtil.serverTimeOutLogin();
                }
            }
        };
        try {
            Cancelable cancle = XUtil.Post(url, params, new CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    LogUtil.i("post_result_load:" + result);
                    if (TextUtils.isEmpty(result)) {
                        Message msg = new Message();
                        msg.what = 1;
                        handler.sendMessage(msg);
                    } else if (result.contains("<!DOCTYPE")) {
                        //重新做登陆操作
                        Message.obtain(handler, 2, url).sendToTarget();
                    } else {
                        Message msg = new Message();
                        msg.what = 0;
                        msg.obj = result;
                        handler.sendMessage(msg);
                    }
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    if (!isLogErr) return;
//					 Toast.makeText(x.app(), ShineApplication.context.getString(R.string.Xutil_network_err)+":2"+ex.getMessage(), Toast.LENGTH_LONG).show();
                    if (ex instanceof HttpException) {
                        T.make(R.string.m网络错误, MyApplication.context);
                    } else if (ex instanceof SocketTimeoutException) {
                        T.make(R.string.m网络超时, MyApplication.context);
                    } else if (ex instanceof UnknownHostException) {
                        T.make(R.string.m服务器连接失败, MyApplication.context);
                    } else {
                        T.make(R.string.m服务器连接失败, MyApplication.context);
                    }

                    Message.obtain(handler, 1, ex.getMessage()).sendToTarget();
                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {

                }


            });
            if (cancle == null) {
                Message.obtain(handler, 1, url).sendToTarget();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Message msg = new Message();
            msg.what = 2;
            msg.obj = e.toString();
            handler.sendMessage(msg);
        }
    }

    public static void postJson(final String url, final String json, final postListener httpListener) {
        LogUtil.i("postJson_url:" + url);
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String a = (String) msg.obj;
                switch (msg.what) {
                    case 0:
                        httpListener.success(a);
                        break;
                    case 1:
                        httpListener.LoginError(a);
                        break;
                    case 2:    //超时重新登录
                        LoginUtil.serverTimeOutLogin();
                }
            }
        };
        try {
            LogUtil.i("postJson_params:" + json);
            Cancelable cancle = XUtil.postJson(url, json, new CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    LogUtil.i("post_result_load:" + result);
                    if (TextUtils.isEmpty(result)) {
                        Message msg = new Message();
                        msg.what = 1;
                        handler.sendMessage(msg);
                    } else if (result.contains("<!DOCTYPE")) {
                        //重新做登陆操作
                        Message.obtain(handler, 2, url).sendToTarget();
                    } else {
                        Message msg = new Message();
                        msg.what = 0;
                        msg.obj = result;
                        handler.sendMessage(msg);
                    }
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    Mydialog.Dismiss();
//					 Toast.makeText(x.app(), ShineApplication.context.getString(R.string.Xutil_network_err)+":2"+ex.getMessage(), Toast.LENGTH_LONG).show();
                    if (ex instanceof HttpException) {
                        T.make(R.string.m网络错误, MyApplication.context);
                    } else if (ex instanceof SocketTimeoutException) {
                        T.make(R.string.m网络超时, MyApplication.context);
                    } else if (ex instanceof UnknownHostException) {
                        T.make(R.string.m服务器连接失败, MyApplication.context);
                    } else {
                        T.make(R.string.m服务器连接失败, MyApplication.context);
                    }

                    Message.obtain(handler, 1, ex.getMessage()).sendToTarget();
                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {

                }


            });
            if (cancle == null) {
                Message.obtain(handler, 1, url).sendToTarget();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Message msg = new Message();
            msg.what = 2;
            msg.obj = e.toString();
            handler.sendMessage(msg);
        }
    }



    /**
     * oss登录接口设置超时时间：
     */
    public static void postOssLoginTimeOut(final String url, final postListener httpListener) {
        final Map<String, String> params = new HashMap<String, String>();
        LogUtil.i("post_utl:" + url);
        httpListener.Params(params);
        LogUtil.i("params:" + params.toString());
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String a = (String) msg.obj;
                switch (msg.what) {
                    case 0:
                        httpListener.success(a);
                        break;
                    case 1:
                        httpListener.LoginError(a);
                        break;
                    case 2:
                        //登陆超时
//					SqliteUtil.url("");
//					SqliteUtil.plant("");
//					Cons.guestyrl="";
//					Cons.isflag=false;
//					Cons.plants.clear();
//					SqliteUtil.time("0");
                        Intent intent = new Intent(MyApplication.context, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        MyApplication.context.startActivity(intent);
                }
            }
        };
        try {
            Cancelable cancle = XUtil.postOssLoginTimeOut(url, params, new CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    LogUtil.i("post_result_load:" + result);
                    if (TextUtils.isEmpty(result)) {
                        Message msg = new Message();
                        msg.what = 1;
                        handler.sendMessage(msg);
                    } else if (result.contains("<!DOCTYPE")) {
                        //重新做登陆操作
                        Message.obtain(handler, 2, url).sendToTarget();
                    } else {
                        Message msg = new Message();
                        msg.what = 0;
                        msg.obj = result;
                        handler.sendMessage(msg);
                    }
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
//					 Toast.makeText(x.app(), ShineApplication.context.getString(R.string.Xutil_network_err)+":2"+ex.getMessage(), Toast.LENGTH_LONG).show();
                    if (ex instanceof HttpException) {
                        T.make(R.string.m网络错误, MyApplication.context);
                    } else if (ex instanceof SocketTimeoutException) {
                        T.make(R.string.m网络超时, MyApplication.context);
                    } else if (ex instanceof UnknownHostException) {
                        T.make(R.string.m服务器连接失败, MyApplication.context);
                    } else {
                        T.make(R.string.m服务器连接失败, MyApplication.context);
                    }

                    Message.obtain(handler, 1, ex.getMessage()).sendToTarget();
                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {

                }


            });
            if (cancle == null) {
                Message.obtain(handler, 1, url).sendToTarget();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Message msg = new Message();
            msg.what = 2;
            msg.obj = e.toString();
            handler.sendMessage(msg);
        }
    }

    /**
     * server 获取服务器地址超时时间自定义：
     */
    public static void postTimeOut(final String url, int time, final postListener httpListener) {
        final Map<String, String> params = new HashMap<String, String>();
        LogUtil.i("post_utl:" + url);
        httpListener.Params(params);
        LogUtil.i("params:" + params.toString());
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String a = (String) msg.obj;
                switch (msg.what) {
                    case 0:
                        httpListener.success(a);
                        break;
                    case 1:
                        httpListener.LoginError(a);
                        break;
                    case 2:
                        //登陆超时
//					SqliteUtil.url("");
//					SqliteUtil.plant("");
//					Cons.guestyrl="";
//					Cons.isflag=false;
//					Cons.plants.clear();
//					SqliteUtil.time("0");
                        Intent intent = new Intent(MyApplication.context, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        MyApplication.context.startActivity(intent);
                }
            }
        };
        try {
            Cancelable cancle = XUtil.postTimeOut(url, params, time, new CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    LogUtil.i("post_result_load:" + result);
                    if (TextUtils.isEmpty(result)) {
                        Message msg = new Message();
                        msg.what = 1;
                        handler.sendMessage(msg);
                    } else if (result.contains("<!DOCTYPE")) {
                        //重新做登陆操作
                        Message.obtain(handler, 2, url).sendToTarget();
                    } else {
                        Message msg = new Message();
                        msg.what = 0;
                        msg.obj = result;
                        handler.sendMessage(msg);
                    }
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
//					 Toast.makeText(x.app(), ShineApplication.context.getString(R.string.Xutil_network_err)+":2"+ex.getMessage(), Toast.LENGTH_LONG).show();
                    if (ex instanceof HttpException) {
                        T.make(R.string.m网络错误, MyApplication.context);
                    } else if (ex instanceof SocketTimeoutException) {
                        T.make(R.string.m网络超时, MyApplication.context);
                    } else if (ex instanceof UnknownHostException) {
                        T.make(R.string.m服务器连接失败, MyApplication.context);
                    } else {
                        T.make(R.string.m服务器连接失败, MyApplication.context);
                    }

                    Message.obtain(handler, 1, ex.getMessage()).sendToTarget();
                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {

                }


            });
            if (cancle == null) {
                Message.obtain(handler, 1, url).sendToTarget();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Message msg = new Message();
            msg.what = 2;
            msg.obj = e.toString();
            handler.sendMessage(msg);
        }
    }

    public interface postListener {
        //����
        void Params(Map<String, String> params);

        //������ȷ
        void success(String json);

        //		//��¼ʧ�ܵķ���
        void LoginError(String str);
    }


    public static void postObj(final String url, final PostListenerObj httpListener) {
        final Map<String, Object> params = new HashMap<String, Object>();
        LogUtil.i("post_utl:" + url);
        httpListener.Params(params);
        LogUtil.i("params:" + params.toString());
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String a = (String) msg.obj;
                switch (msg.what) {
                    case 0:
                        httpListener.success(a);
                        break;
                    case 1:
                        httpListener.LoginError(a);
                        break;
                    case 2:    //超时重新登录
                        LoginUtil.serverTimeOutLogin();
                }
            }
        };
        try {
            Cancelable cancle = XUtil.PostObj(url, params, new CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    LogUtil.i("post_result_load:" + result);
                    if (TextUtils.isEmpty(result)) {
                        Message msg = new Message();
                        msg.what = 1;
                        handler.sendMessage(msg);
                    } else if (result.contains("<!DOCTYPE")) {
                        //重新做登陆操作
                        Message.obtain(handler, 2, url).sendToTarget();
                    } else {
                        Message msg = new Message();
                        msg.what = 0;
                        msg.obj = result;
                        handler.sendMessage(msg);
                    }
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
//					 Toast.makeText(x.app(), ShineApplication.context.getString(R.string.Xutil_network_err)+":2"+ex.getMessage(), Toast.LENGTH_LONG).show();
                    if (ex instanceof HttpException) {
                        T.make(R.string.m网络错误, MyApplication.context);
                    } else if (ex instanceof SocketTimeoutException) {
                        T.make(R.string.m网络超时, MyApplication.context);
                    } else if (ex instanceof UnknownHostException) {
                        T.make(R.string.m服务器连接失败, MyApplication.context);
                    } else {
                        T.make(R.string.m服务器连接失败, MyApplication.context);
                    }

                    Message.obtain(handler, 1, ex.getMessage()).sendToTarget();
                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {

                }


            });
            if (cancle == null) {
                Message.obtain(handler, 1, url).sendToTarget();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Message msg = new Message();
            msg.what = 2;
            msg.obj = e.toString();
            handler.sendMessage(msg);
        }
    }

    public interface PostListenerObj {
        void Params(Map<String, Object> params);

        void success(String json);

        void LoginError(String str);
    }
}
