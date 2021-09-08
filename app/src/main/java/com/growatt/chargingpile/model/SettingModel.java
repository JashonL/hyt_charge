package com.growatt.chargingpile.model;

import static com.growatt.chargingpile.util.Utils.getLanguage;

import com.google.gson.Gson;
import com.growatt.chargingpile.bean.NoConfigBean;
import com.growatt.chargingpile.connutil.PostUtil;
import com.growatt.chargingpile.util.Cons;
import com.growatt.chargingpile.util.SmartHomeUrlUtil;
import com.growatt.chargingpile.util.SmartHomeUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingModel {

    /**
     * 获取配置信息
     * @param chargingId
     * @param httpCallBack
     */
    public void requestChargingParams(String chargingId, GunModel.HttpCallBack httpCallBack) {
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        jsonMap.put("sn", chargingId);//测试id
        jsonMap.put("userId", SmartHomeUtil.getUserName());//测试id
        jsonMap.put("lan", getLanguage());//测试id
        String json = SmartHomeUtil.mapToJsonString(jsonMap);
        PostUtil.postJson(SmartHomeUrlUtil.postRequestChargingParams(), json, new PostUtil.postListener() {
            @Override
            public void Params(Map<String, String> params) {
            }

            @Override
            public void success(String json) {
                httpCallBack.onSuccess(json);
            }

            @Override
            public void LoginError(String str) {
                httpCallBack.onFailed();
            }
        });
    }

    /**
     * 修改信息
     * @param chargingId
     * @param key
     * @param value
     * @param httpCallBack
     */
    public void requestEditChargingParams(String chargingId, String key, Object value, GunModel.HttpCallBack httpCallBack) {
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("chargeId", chargingId);//测试id
        jsonMap.put("userId", SmartHomeUtil.getUserName());//测试id
        jsonMap.put("lan", getLanguage());//测试id
//        if ("unit".equals(key)) {
//            jsonMap.put("symbol", unitSymbol);
//        }
        jsonMap.put(key, value);
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
     * 获取国家
     * @param httpCallBack
     */
    public void requestCountry(GunModel.HttpCallBack httpCallBack) {
        List<String> countryList = new ArrayList<>();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("cmd", "countryList");
            jsonObject.put("lan", getLanguage());
            jsonObject.put("userId", SmartHomeUtil.getUserName());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String params = jsonObject.toString();
        PostUtil.postJson(SmartHomeUrlUtil.postByCmd(), params, new PostUtil.postListener() {
            @Override
            public void Params(Map<String, String> params) {
            }

            @Override
            public void success(String json) {
                try {
                    JSONObject respon = new JSONObject(json);
                    int code = respon.optInt("code");
                    if (code == 0) {
                        JSONArray jsonObject1 = respon.optJSONArray("data");
                        if (jsonObject1 != null) {
                            for (int i = 0; i < jsonObject1.length(); i++) {
                                JSONObject jsonObject = jsonObject1.getJSONObject(i);
                                String s = jsonObject.optString("country", " ");
                                countryList.add(s);
                            }
                            httpCallBack.onSuccess(countryList);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void LoginError(String str) {

            }
        });
    }

    /**
     * 获取需要密码的设置项
     */
    public void requestConfigParams() {
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("userId", SmartHomeUtil.getUserName());//测试id
        jsonMap.put("cmd", "noConfig");
        jsonMap.put("lan", getLanguage());
        String json = SmartHomeUtil.mapToJsonString(jsonMap);

        PostUtil.postJson(SmartHomeUrlUtil.postByCmd(), json, new PostUtil.postListener() {
            @Override
            public void Params(Map<String, String> params) {

            }

            @Override
            public void success(String json) {
                NoConfigBean bean = null;
                try {
                    JSONObject object = new JSONObject(json);
                    if (object.getInt("code") == 0) {
                        JSONObject jsonObject = object.optJSONObject("data");
                        bean = new Gson().fromJson(jsonObject.toString(), NoConfigBean.class);
                    }
                    Cons.setNoConfigBean(bean);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void LoginError(String str) {
            }
        });

    }


}
