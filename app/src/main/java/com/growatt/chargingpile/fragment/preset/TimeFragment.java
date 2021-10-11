package com.growatt.chargingpile.fragment.preset;

import static com.growatt.chargingpile.util.T.toast;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.growatt.chargingpile.EventBusMsg.PreinstallEvent;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.fragment.BaseFragment;
import com.growatt.chargingpile.model.GunModel;
import com.growatt.chargingpile.view.TimeSetDialog;
import com.growatt.chargingpile.view.TypeSelectDialog;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created：2021/8/26 on 14:19:04
 * Author: on admin
 * Description:预设时间
 */

public class TimeFragment extends BaseFragment {
    private static String TAG = TimeFragment.class.getSimpleName();
    @BindView(R.id.ll_every_day)
    LinearLayout mLlEveryDay;
    @BindView(R.id.tv_duration)
    TextView mTvDuration;
    @BindView(R.id.tv_start_type)
    TextView mTvStartTime;
    @BindView(R.id.sw_day)
    Switch mSwitchEveryDay;

    private String mTvDurationHour = "";
    private String mTvDurationMinute = "";

    @Override
    protected Object setRootView() {
        return R.layout.fragment_time;
    }

    @Override
    protected void initWidget() {
        if (pPresetActivity.pReservationBean != null) {
            if (pPresetActivity.pReservationBean.getCKey().equals("G_SetTime") || pPresetActivity.pReservationBean.getCKey().isEmpty()) {
                String hour = pPresetActivity.pReservationBean.getExpiryDate().substring(11, 13);
                String minute = pPresetActivity.pReservationBean.getExpiryDate().substring(14, 16);
                mTvStartTime.setText(hour + ":" + minute);

                if (pPresetActivity.pReservationBean.getLoopType() == 0) {
                    mLlEveryDay.setVisibility(View.VISIBLE);
                    mSwitchEveryDay.setChecked(true);
                }

                int timeCharging = Integer.parseInt(pPresetActivity.pReservationBean.getCValue());

                if (timeCharging == 0) {
                    mTvDuration.setText(R.string.please_charging_duration);
                    return;
                }

                int hourCharging = timeCharging / 60;
                int minCharging = timeCharging % 60;

                mTvDurationHour = String.valueOf(hourCharging);
                mTvDurationMinute = String.valueOf(minCharging);

                mTvDuration.setText(String.format("%02d", hourCharging) + getString(R.string.m207时) + String.format("%02d", minCharging) + getString(R.string.m208分));
            }

        } else {
            mTvDuration.setText(R.string.please_charging_duration);
        }
    }

    @OnClick({R.id.rl_duration, R.id.btn_ok, R.id.rl_start_type})
    public void onClickListener(View view) {
        switch (view.getId()) {
            case R.id.btn_ok:
                requestPreinstall();
                break;
            case R.id.rl_duration:
                TimeSetDialog.newInstance(1, getString(R.string.charging_time), mTvDuration.getText().toString(), (hour, minute) -> {
                    if (hour.equals(getString(R.string.please_charging_duration))) {
                        mTvDurationHour = "";
                        mTvDurationMinute = "";
                        mTvDuration.setText(hour);
                    } else {
                        mTvDurationHour = hour;
                        mTvDurationMinute = minute;
                        mTvDuration.setText(hour + getString(R.string.m207时) + minute + getString(R.string.m208分));
                    }
                }).show(pPresetActivity.getSupportFragmentManager(), "");
                break;
            case R.id.rl_start_type:
                TypeSelectDialog.newInstance(str -> {
                    if (str.equals(getString(R.string.start_immediately))) {
                        mLlEveryDay.setVisibility(View.GONE);
                    } else {
                        mLlEveryDay.setVisibility(View.VISIBLE);
                    }
                    mTvStartTime.setText(str);
                }).show(pPresetActivity.getSupportFragmentManager(), "");
                break;
            default:
                break;
        }
    }

    private void requestPreinstall() {

        if (TextUtils.isEmpty(mTvDuration.getText())) {
            toast(getString(R.string.m129时长设置不能为空));
            return;
        }

        if (mTvDurationHour.equals("00") && mTvDurationMinute.equals("00")) {
            toast(R.string.m请选择正确的时间段);
            return;
        }

        if (TextUtils.isEmpty(mTvStartTime.getText())) {
            toast(getString(R.string.m130未设置开始时间));
            return;
        }

        Object cValue;

        String cKey;

        if (!mTvDurationHour.isEmpty() && !mTvDurationMinute.isEmpty()) {
            cValue = Integer.parseInt(mTvDurationHour) * 60 + Integer.parseInt(mTvDurationMinute);
            cKey = "G_SetTime";
        } else {
            cValue = "";
            cKey = "";
        }

        Log.d(TAG, "cValue:" + cValue);

        if (mTvStartTime.getText().equals(getString(R.string.start_immediately))) {
            GunModel.getInstance().requestCharging(cKey, cValue, pPresetActivity.pChargingId, pPresetActivity.pConnectorId, new GunModel.HttpCallBack() {
                @Override
                public void onSuccess(Object bean) {
                    try {
                        JSONObject object = new JSONObject(bean.toString());
                        int type = object.optInt("type");
                        if (type == 0) {
                            EventBus.getDefault().post(new PreinstallEvent());
                            pPresetActivity.finish();
                        }
                        toast(object.getString("data"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailed() {

                }
            });

        } else {
            int loop = mSwitchEveryDay.isChecked() ? 0 : -1;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String time = sdf.format(new Date()) + "T" + mTvStartTime.getText() + ":00.000Z";

            GunModel.getInstance().requestReserve(time, cKey, cValue, loop, pPresetActivity.pChargingId, pPresetActivity.pConnectorId, new GunModel.HttpCallBack() {
                @Override
                public void onSuccess(Object bean) {
                    try {
                        JSONObject object = new JSONObject(bean.toString());
                        String data = object.getString("data");
                        int type = object.optInt("type");
                        if (type == 0) {
                            pPresetActivity.finish();
                            EventBus.getDefault().post(new PreinstallEvent());
                        }
                        toast(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailed() {

                }
            });
        }
    }
}

