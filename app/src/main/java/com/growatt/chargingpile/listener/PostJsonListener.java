package com.growatt.chargingpile.listener;

/**
 * Created：2018/10/26 on 14:43
 * Author:gaideng on dg
 * Description:参数为Json的回调监听
 */

public interface PostJsonListener {

    void success(String json);

    void error(String err);
}
