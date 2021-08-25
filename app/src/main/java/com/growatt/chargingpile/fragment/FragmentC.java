package com.growatt.chargingpile.fragment;

import android.os.Bundle;
import android.util.Log;

import com.growatt.chargingpile.R;
import com.growatt.chargingpile.bean.ChargingBean;

import androidx.annotation.Nullable;

/**
 * Created：2021/8/24 on 14:43:56
 * Author:gaideng on admin
 * Description:GUN C
 */

public class FragmentC extends BaseFragment {
    private static String TAG = FragmentC.class.getSimpleName();

    @Override
    protected Object setRootView() {
        return R.layout.fragment_a;
    }

    @Override
    protected void initWidget() {
        Log.d(TAG, "initWidget: C");
    }

    @Override
    protected void requestData() {
        Log.d(TAG, "requestData: "+pGunActivity.mDataBean.toString());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: C");
    }

}
