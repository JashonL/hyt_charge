package com.growatt.chargingpile.fragment.gun;

import android.util.Log;

import com.growatt.chargingpile.R;
import com.growatt.chargingpile.fragment.BaseFragment;

/**
 * Created：2021/8/24 on 14:43:56
 * Author:gaideng on admin
 * Description:GUN B
 */

public class FragmentB extends BaseFragment {
    private static String TAG = FragmentB.class.getSimpleName();

    @Override
    protected Object setRootView() {
        return R.layout.fragment_gun;
    }

    @Override
    protected void initWidget() {
        Log.d(TAG, "initWidget: B");
    }

    @Override
    protected void requestData() {
        Log.d(TAG, "requestData: "+ pActivity.mDataBean.toString());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: B");
    }
}
