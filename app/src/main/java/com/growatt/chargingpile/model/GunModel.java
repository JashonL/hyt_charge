package com.growatt.chargingpile.model;

import static com.growatt.chargingpile.util.Utils.getLanguage;

import android.util.Log;

import com.google.gson.Gson;
import com.growatt.chargingpile.bean.ChargingBean;
import com.growatt.chargingpile.bean.GunBean;
import com.growatt.chargingpile.bean.ReservationBean;
import com.growatt.chargingpile.connutil.PostUtil;
import com.growatt.chargingpile.util.Mydialog;
import com.growatt.chargingpile.util.SmartHomeUrlUtil;
import com.growatt.chargingpile.util.SmartHomeUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.util.LogUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created：2021/9/1 on 11:26:03
 * Author: on admin
 * Description:
 */

public class GunModel {

    private static String TAG = GunModel.class.getSimpleName();

    /**
     * 获取当前GUN状态
     *
     * @param chargingId   充电桩的id
     * @param connectorId  充电枪的id
     * @param httpCallBack
     */
    public void getChargingGunStatus(final String chargingId, final int connectorId, HttpCallBack httpCallBack) {
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("sn", chargingId);//测试id
        jsonMap.put("connectorId", connectorId);//测试id
        jsonMap.put("userId", SmartHomeUtil.getUserName());//测试id
        jsonMap.put("lan", getLanguage());//测试id
        String json = SmartHomeUtil.mapToJsonString(jsonMap);
        //LogUtil.i(json);
        PostUtil.postJson(SmartHomeUrlUtil.postGetChargingGunNew(), json, new PostUtil.postListener() {
            @Override
            public void Params(Map<String, String> params) {

            }

            @Override
            public void success(String json) {
                try {
                    JSONObject object = new JSONObject(json);
                    if (object.getInt("code") == 0) {
                        GunBean gunBean = new Gson().fromJson(json, GunBean.class);
                        if (gunBean != null) {
                            httpCallBack.onSuccess(gunBean);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void LoginError(String str) {
                httpCallBack.onFailed();
            }

        });

    }


    /**
     * 设置当前桩的充电功率
     *
     * @param chargingId   充电桩的id
     * @param solar        type
     * @param httpCallBack
     */
    public void setLimit(final String chargingId, final int solar, HttpCallBack httpCallBack) {
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("chargeId", chargingId);//测试id
        jsonMap.put("userId", SmartHomeUtil.getUserName());//测试id
        jsonMap.put("lan", getLanguage());//测试id
        jsonMap.put("G_SolarMode", solar);
        String json = SmartHomeUtil.mapToJsonString(jsonMap);
        PostUtil.postJson(SmartHomeUrlUtil.postSetChargingParams(), json, new PostUtil.postListener() {
            @Override
            public void Params(Map<String, String> params) {

            }

            @Override
            public void success(String json) {
                httpCallBack.onSuccess(json);
            }

            @Override
            public void LoginError(String str) {

            }
        });
    }


    /**
     * 获取桩的状态
     *
     * @param chargingId
     * @param httpCallBack
     */
    public void getPileStatus(final String chargingId, HttpCallBack httpCallBack) {
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("userId", SmartHomeUtil.getUserName());//测试id
        jsonMap.put("chargeId", chargingId);
        jsonMap.put("lan", getLanguage());
        String json = SmartHomeUtil.mapToJsonString(jsonMap);

        PostUtil.postJson(SmartHomeUrlUtil.postGetChargingList(), json, new PostUtil.postListener() {
            @Override
            public void Params(Map<String, String> params) {

            }

            @Override
            public void success(String json) {
                Log.d(TAG, "getPileStatus: success" + json);
                JSONObject object = null;
                try {
                    object = new JSONObject(json);
                    if (object.getInt("code") == 0) {
                        ChargingBean chargingListBean = new Gson().fromJson(json, ChargingBean.class);
                        if (chargingListBean != null && chargingListBean.getData().size() != 0) {
                            httpCallBack.onSuccess(chargingListBean.getData());
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void LoginError(String str) {
                Log.d(TAG, "getPileStatus: Error" + str);
            }
        });

    }


    /**
     * 解锁
     *
     * @param chargingId  充电桩的id
     * @param connectorId 充电枪的id
     */
    public void requestGunUnlock(final String userID, final String chargingId, final int connectorId, HttpCallBack httpCallBack) {
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("cmd", "unlock");//测试id
        jsonMap.put("userId", userID);//用户id
        jsonMap.put("chargeId", chargingId);//测试id
        jsonMap.put("lan", getLanguage());//测试id
        jsonMap.put("connectorId", connectorId);
        String json = SmartHomeUtil.mapToJsonString(jsonMap);
        PostUtil.postJson(SmartHomeUrlUtil.postByCmd(), json, new PostUtil.postListener() {
            @Override
            public void Params(Map<String, String> params) {

            }

            @Override
            public void success(String json) {
                httpCallBack.onSuccess(json);
            }

            @Override
            public void LoginError(String str) {
                Log.d(TAG, "LoginError: ");
            }
        });
    }

    /**
     * 预约充电
     *
     * @param type
     * @param startTime
     * @param key
     * @param value
     * @param loopType  是否每天
     */
    public void requestReserve(int type, String startTime, String key, Object value, int loopType, final String chargingId, final int connectorId, HttpCallBack httpCallBack) {
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("action", "ReserveNow");
        jsonMap.put("expiryDate", startTime);
        jsonMap.put("connectorId", connectorId);
        jsonMap.put("chargeId", chargingId);
        jsonMap.put("userId", SmartHomeUtil.getUserName());
        jsonMap.put("loopType", loopType);
        jsonMap.put("lan", getLanguage());
        if (loopType == 0) {
            String loopValue = startTime.substring(11, 16);
            jsonMap.put("loopValue", loopValue);
        }
        if (type != 0) {
            jsonMap.put("cKey", key);
            jsonMap.put("cValue", value);
        }

        String json = SmartHomeUtil.mapToJsonString(jsonMap);
        LogUtil.i(json);
        PostUtil.postJson(SmartHomeUrlUtil.postRequestReseerveCharging(), json, new PostUtil.postListener() {
            @Override
            public void Params(Map<String, String> params) {

            }

            @Override
            public void success(String json) {
                httpCallBack.onSuccess(json);
            }

            @Override
            public void LoginError(String str) {
                Mydialog.Dismiss();
            }
        });
    }


    /**
     * 获取预约
     *
     * @param chargingId
     * @param connectorId
     * @param httpCallBack
     */
    public void getReservationNow(final String chargingId, final int connectorId, HttpCallBack httpCallBack) {
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("chargeId", chargingId);
        jsonMap.put("connectorId", connectorId);
        jsonMap.put("lan", getLanguage());
        jsonMap.put("userId", SmartHomeUtil.getUserName());
        String json = SmartHomeUtil.mapToJsonString(jsonMap);
        PostUtil.postJson(SmartHomeUrlUtil.postRequestReserveNowList(), json, new PostUtil.postListener() {
            @Override
            public void Params(Map<String, String> params) {

            }

            @Override
            public void success(String json) {
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    int code = jsonObject.getInt("code");
                    if (code == 0) {
                        ReservationBean bean = new Gson().fromJson(json, ReservationBean.class);
                        List<ReservationBean.DataBean> reserveNow = bean.getData();
                        Log.d(TAG, "success: reserveNow:" + reserveNow.size());
                        if (reserveNow.size() != 0) {
                            httpCallBack.onSuccess(reserveNow.get(0));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void LoginError(String str) {

            }
        });

    }

    /**
     * 删除预约
     *
     * @param bean
     * @param httpCallBack
     */
    public void deleteReservationNow(ReservationBean.DataBean bean, HttpCallBack httpCallBack) {
        JSONObject object = new JSONObject();
        try {
            object.put("cKey", bean.getcKey());
            object.put("cValue", bean.getcValue());
            object.put("connectorId", bean.getConnectorId());
            object.put("expiryDate", bean.getExpiryDate());
            object.put("loopValue", bean.getLoopValue());
            object.put("reservationId", bean.getReservationId());
            object.put("sn", bean.getChargeId());
            object.put("userId", SmartHomeUtil.getUserName());
            object.put("ctype", "2");
            object.put("lan", getLanguage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        PostUtil.postJson(SmartHomeUrlUtil.postUpdateChargingReservelist(), object.toString(), new PostUtil.postListener() {
            @Override
            public void Params(Map<String, String> params) {

            }

            @Override
            public void success(String json) {
                httpCallBack.onSuccess(json);
            }

            @Override
            public void LoginError(String str) {

            }
        });

    }


    public void requestStopCharging(final String chargingId, final int connectorId, int transactionId, HttpCallBack httpCallBack) {
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("action", "remoteStopTransaction");
        jsonMap.put("connectorId", connectorId);
        jsonMap.put("userId", SmartHomeUtil.getUserName());
        jsonMap.put("chargeId", chargingId);
        jsonMap.put("transactionId", transactionId);
        jsonMap.put("lan", getLanguage());
        String json = SmartHomeUtil.mapToJsonString(jsonMap);
        PostUtil.postJson(SmartHomeUrlUtil.postRequestReseerveCharging(), json, new PostUtil.postListener() {
            @Override
            public void Params(Map<String, String> params) {

            }

            @Override
            public void success(String json) {
                httpCallBack.onSuccess(json);
            }

            @Override
            public void LoginError(String str) {
                Mydialog.Dismiss();
            }

        });
    }

    /**
     * 正常请求充电
     *
     * @param chargingId
     * @param connectorId
     * @param httpCallBack
     */
    public void requestCharging(final String chargingId, final int connectorId, HttpCallBack httpCallBack) {
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("action", "remoteStartTransaction");
        jsonMap.put("connectorId", connectorId);
        jsonMap.put("userId", SmartHomeUtil.getUserName());
        jsonMap.put("chargeId", chargingId);
        jsonMap.put("lan", getLanguage());
        String json = SmartHomeUtil.mapToJsonString(jsonMap);
        PostUtil.postJson(SmartHomeUrlUtil.postRequestReseerveCharging(), json, new PostUtil.postListener() {
            @Override
            public void Params(Map<String, String> params) {
            }

            @Override
            public void success(String json) {
                httpCallBack.onSuccess(json);
            }

            @Override
            public void LoginError(String str) {
                Mydialog.Dismiss();
            }

        });
    }

    public interface HttpCallBack<T> {

        void onSuccess(T bean);

        void onFailed();
    }

}
