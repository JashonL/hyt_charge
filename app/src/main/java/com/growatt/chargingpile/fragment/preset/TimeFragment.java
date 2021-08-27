package com.growatt.chargingpile.fragment.preset;

import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.growatt.chargingpile.R;
import com.growatt.chargingpile.constant.Constant;
import com.growatt.chargingpile.fragment.BaseFragment;
import com.growatt.chargingpile.view.TimeSetDialog;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created：2021/8/26 on 14:19:04
 * Author: on admin
 * Description:预设时间
 */

public class TimeFragment extends BaseFragment {

    private static String TAG = TimeFragment.class.getSimpleName();

    @BindView(R.id.cb_time)
    TextView mCheckTime;
    @BindView(R.id.cb_duration)
    TextView mCheckDuration;

    @BindView(R.id.rl_duration)
    RelativeLayout mRlDuration;
    @BindView(R.id.rl_duration_start)
    RelativeLayout mRlDurationStart;

    @BindView(R.id.rl_time)
    RelativeLayout mRlTime;

    @BindView(R.id.tv_start_time_values)
    TextView mTvStartTime;
    @BindView(R.id.tv_end_time_values)
    TextView mTvEndTime;
    @BindView(R.id.tv_duration)
    TextView mTvDuration;


    @Override
    protected Object setRootView() {
        Log.d(TAG, "setRootView: ");
        return R.layout.fragment_time;
    }

    @Override
    protected void initWidget() {
        handldeViewHide(Constant.TIME_TYPE);
    }

    @OnClick({R.id.cb_time, R.id.cb_duration, R.id.tv_start_time_values, R.id.tv_end_time_values,R.id.rl_duration})
    public void onClickListener(View view) {
        switch (view.getId()) {
            case R.id.cb_time:
                if (mCheckTime.isSelected()) {
                    return;
                }
                handldeViewHide(Constant.TIME_TYPE);
                break;
            case R.id.cb_duration:
                if (mCheckDuration.isSelected()) {
                    return;
                }
                handldeViewHide(Constant.DURATION_TYPE);
                break;
            case R.id.tv_start_time_values:
                TimeSetDialog.newInstance(getString(R.string.m204开始时间), mTvStartTime.getText().toString(), new TimeSetDialog.TimeCallBack() {
                    @Override
                    public void confirm(String startTime) {
                        Log.d(TAG, "confirm: startTime:"+startTime);
                        mTvStartTime.setText(startTime);
                    }

                    @Override
                    public void cancel() {

                    }
                }).show(mPresetActivity.getSupportFragmentManager(), "startDialog");
                break;
            case R.id.tv_end_time_values:
                TimeSetDialog.newInstance(getString(R.string.m282结束时间), mTvEndTime.getText().toString(), new TimeSetDialog.TimeCallBack() {
                    @Override
                    public void confirm(String endTime) {
                        Log.d(TAG, "confirm: endTime:"+endTime);
                        mTvEndTime.setText(endTime);
                    }

                    @Override
                    public void cancel() {

                    }
                }).show(mPresetActivity.getSupportFragmentManager(), "endDialog");
                break;

            case R.id.rl_duration:
                TimeSetDialog.newInstance(getString(R.string.charging_time), mTvDuration.getText().toString(), new TimeSetDialog.TimeCallBack() {
                    @Override
                    public void confirm(String str) {
                        String time=str.substring(0,2)+"小时"+str.substring(3,5)+"分钟";
                        Log.d(TAG, "confirm: time:"+time);
                        mTvDuration.setText(time);
                    }
                    @Override
                    public void cancel() {

                    }
                }).show(mPresetActivity.getSupportFragmentManager(), "endDialog");
                break;
            default:
                break;
        }
    }

    private void handldeViewHide(int type) {
        if (type == Constant.TIME_TYPE) {
            mRlDuration.setVisibility(View.GONE);
            mRlDurationStart.setVisibility(View.GONE);
            mRlTime.setVisibility(View.VISIBLE);
            mCheckTime.setSelected(true);
            mCheckDuration.setSelected(false);
        } else if (type == Constant.DURATION_TYPE) {
            mRlTime.setVisibility(View.GONE);
            mRlDuration.setVisibility(View.VISIBLE);
            mRlDurationStart.setVisibility(View.VISIBLE);
            mCheckDuration.setSelected(true);
            mCheckTime.setSelected(false);
        }
    }
}
