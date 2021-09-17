package com.growatt.chargingpile.connutil;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.growatt.chargingpile.R;
import com.growatt.chargingpile.application.MyApplication;
import com.growatt.chargingpile.util.LoginUtil;
import com.growatt.chargingpile.util.Mydialog;
import com.growatt.chargingpile.util.T;

import org.json.JSONException;
import org.json.JSONObject;
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
        final Handler handler = new Handler(Looper.getMainLooper(), msg -> {
            String a = (String) msg.obj;
            Mydialog.Dismiss();
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
            return false;
        });
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
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            String code = jsonObject.optString("code", "");
                            if ("501".equals(code)) {
                                //重新做登陆操作
                                Message.obtain(handler, 2, url).sendToTarget();
                            } else {
                                Message msg = new Message();
                                msg.what = 0;
                                msg.obj = result;
                                handler.sendMessage(msg);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


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

    public static void post(final String url, boolean isLogErr, final postListener httpListener) {
        final Map<String, String> params = new HashMap<String, String>();
        LogUtil.i("post_utl:" + url);
        httpListener.Params(params);
        LogUtil.i("params:" + params.toString());
        final Handler handler = new Handler(Looper.getMainLooper(), msg -> {
            String a = (String) msg.obj;
            Mydialog.Dismiss();
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
            return false;
        });
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
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            String code = jsonObject.optString("code", "");
                            if ("501".equals(code)) {
                                //重新做登陆操作
                                Message.obtain(handler, 2, url).sendToTarget();
                            } else {
                                Message msg = new Message();
                                msg.what = 0;
                                msg.obj = result;
                                handler.sendMessage(msg);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
        LogUtil.i("post_url:" + url + "\njson:" + json);
        final Handler handler = new Handler(Looper.getMainLooper(), msg -> {
            String a = (String) msg.obj;
            Mydialog.Dismiss();
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
            return false;
        });
        try {
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
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            String code = jsonObject.optString("code", "");
                            if ("501".equals(code)) {
                                //重新做登陆操作
                                Message.obtain(handler, 2, url).sendToTarget();
                            } else {
                                Message msg = new Message();
                                msg.what = 0;
                                msg.obj = result;
                                handler.sendMessage(msg);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
        void Params(Map<String, String> params);

        void success(String json);

        void LoginError(String str);
    }

}
