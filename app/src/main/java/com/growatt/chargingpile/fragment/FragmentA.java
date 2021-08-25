package com.growatt.chargingpile.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.growatt.chargingpile.R;
import com.growatt.chargingpile.bean.ChargingBean;

import androidx.annotation.Nullable;
import butterknife.BindView;

/**
 * Created：2021/8/24 on 14:43:56
 * Author:gaideng on admin
 * Description: GUN A
 */

public class FragmentA extends BaseFragment {

    private static String TAG = FragmentA.class.getSimpleName();

    @BindView(R.id.tv_ac)
    TextView mModel;

    @Override
    protected Object setRootView() {
        return R.layout.fragment_a;
    }

    @Override
    protected void initWidget() {
        Log.d(TAG, "initWidget: A");
        mModel.setText("DC");
    }

    @Override
    protected void requestData() {
        Log.d(TAG, "requestData: "+pGunActivity.mDataBean.toString());
    }
}
