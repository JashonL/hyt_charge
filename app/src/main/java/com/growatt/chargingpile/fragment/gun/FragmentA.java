package com.growatt.chargingpile.fragment.gun;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.growatt.chargingpile.R;
import com.growatt.chargingpile.activity.PresetActivity;
import com.growatt.chargingpile.fragment.BaseFragment;
import com.growatt.chargingpile.view.ChrageModeDialog;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created：2021/8/24 on 14:43:56
 * Author:gaideng on admin
 * Description: GUN A
 */

public class FragmentA extends BaseFragment {

    private static String TAG = FragmentA.class.getSimpleName();

    @BindView(R.id.tv_ac)
    TextView mModel;

    @BindView(R.id.tv_time)//预设的时间
    TextView mPreinstallTime;

    @OnClick({R.id.tv_time,R.id.ll_fast,R.id.ll_lock,R.id.ll_record,R.id.ll_preset})
    public void onClickListener(View view) {
        switch (view.getId()) {
            case R.id.tv_time:
                Intent intent = new Intent(pActivity, PresetActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_preset:
                Intent intent1 = new Intent(pActivity, PresetActivity.class);
                startActivity(intent1);
                break;
            case R.id.ll_fast:
                ChrageModeDialog.newInstance("", new ChrageModeDialog.TimeCallBack() {
                    @Override
                    public void confirm(String time) {

                    }

                    @Override
                    public void cancel() {

                    }
                }).show(pActivity.getSupportFragmentManager(),"ddd");
                break;
            case R.id.ll_lock:
                break;
            case R.id.ll_record:
                break;
            default:
                break;
        }
    }

    @Override
    protected Object setRootView() {
        return R.layout.fragment_gun;
    }

    @Override
    protected void initWidget() {
        Log.d(TAG, "initWidget: A");
        mModel.setText("DC");
    }

    @Override
    protected void requestData() {
        Log.d(TAG, "requestData: " + pActivity.mDataBean.toString());
    }
}
