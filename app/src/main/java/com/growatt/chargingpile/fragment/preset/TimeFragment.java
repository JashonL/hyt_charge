package com.growatt.chargingpile.fragment.preset;

import static com.growatt.chargingpile.util.T.toast;

import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.growatt.chargingpile.EventBusMsg.PreinstallEvent;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.constant.Constant;
import com.growatt.chargingpile.fragment.BaseFragment;
import com.growatt.chargingpile.model.GunModel;
import com.growatt.chargingpile.view.TimeSetDialog;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
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

    @BindView(R.id.cb_time)
    TextView mCheckTime;
    @BindView(R.id.cb_duration)
    TextView mCheckDuration;

    @BindView(R.id.rl_duration)
    RelativeLayout mRlDuration;
    @BindView(R.id.rl_duration_start)
    RelativeLayout mRlDurationStart;
    @BindView(R.id.tv_duration)
    TextView mTvDuration;
    @BindView(R.id.tv_duration_start_time)
    TextView mTvDurationStartTime;

    @BindView(R.id.rl_time)
    RelativeLayout mRlTime;

    @BindView(R.id.tv_start_hour)
    TextView mTvStartHour;
    @BindView(R.id.tv_start_minute)
    TextView mTvStartMinute;

    @BindView(R.id.tv_end_hour)
    TextView mTvEndHour;
    @BindView(R.id.tv_end_minute)
    TextView mTvEndMinute;


    @BindView(R.id.sw_day)
    Switch mSwitchEveryDay;

    private int mCurrTimeType = Constant.TIME_TYPE;

    private String mTvDurationHour = "";
    private String mTvDurationMinute = "";

    @Override
    protected Object setRootView() {
        Log.d(TAG, "setRootView: ");
        return R.layout.fragment_time;
    }

    @Override
    protected void initWidget() {
        if (pPresetActivity.pReservationBean != null&& pPresetActivity.pReservationBean.getCKey().equals("G_SetTime")) {
            mTvStartHour.setText(pPresetActivity.pReservationBean.getExpiryDate().substring(11, 13));
            mTvStartMinute.setText(pPresetActivity.pReservationBean.getExpiryDate().substring(14, 16));
            mTvEndHour.setText(pPresetActivity.pReservationBean.getEndDate().substring(11, 13));
            mTvEndMinute.setText(pPresetActivity.pReservationBean.getEndDate().substring(14, 16));
            if (pPresetActivity.pReservationBean.getLoopType() == 0) {
                mSwitchEveryDay.setChecked(true);
            }
        }
        handleViewHide(Constant.TIME_TYPE);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @OnClick({R.id.cb_time, R.id.cb_duration, R.id.ll_start_time, R.id.ll_end_time,
            R.id.rl_duration, R.id.btn_ok, R.id.rl_duration_start})
    public void onClickListener(View view) {
        switch (view.getId()) {
            case R.id.btn_ok:
                requestPreinstall();
                break;
            case R.id.cb_time:
                if (mCheckTime.isSelected()) {
                    return;
                }
                handleViewHide(Constant.TIME_TYPE);
                break;
            case R.id.cb_duration:
                if (mCheckDuration.isSelected()) {
                    return;
                }
                handleViewHide(Constant.DURATION_TYPE);
                break;
            case R.id.ll_start_time:
                TimeSetDialog.newInstance(getString(R.string.m204开始时间), mTvStartHour.getText().toString() + ":" + mTvStartMinute.getText().toString(), new TimeSetDialog.TimeCallBack() {
                    @Override
                    public void confirm(String hour, String minute) {
                        mTvStartHour.setText(hour);
                        mTvStartMinute.setText(minute);
                        Log.d(TAG, "confirm: hour:" + hour + "  minute:" + minute);
                    }

                }).show(pPresetActivity.getSupportFragmentManager(), "startDialog");
                break;
            case R.id.ll_end_time:
                TimeSetDialog.newInstance(getString(R.string.m282结束时间), mTvEndHour.getText().toString() + ":" + mTvEndMinute.getText().toString(), new TimeSetDialog.TimeCallBack() {
                    @Override
                    public void confirm(String hour, String minute) {
                        mTvEndHour.setText(hour);
                        mTvEndMinute.setText(minute);
                        Log.d(TAG, "confirm: hour:" + hour + "  minute:" + minute);
                    }
                }).show(pPresetActivity.getSupportFragmentManager(), "endDialog");
                break;
            case R.id.rl_duration:
                TimeSetDialog.newInstance(getString(R.string.charging_time), mTvDuration.getText().toString(), new TimeSetDialog.TimeCallBack() {
                    @Override
                    public void confirm(String hour, String minute) {
                        mTvDurationHour = hour;
                        mTvDurationMinute = minute;
                        mTvDuration.setText(hour + "小时" + minute + "分钟");
                    }
                }).show(pPresetActivity.getSupportFragmentManager(), "endDialog");
                break;
            case R.id.rl_duration_start:
                TimeSetDialog.newInstance(getString(R.string.m204开始时间), mTvDurationStartTime.getText().toString(), new TimeSetDialog.TimeCallBack() {
                    @Override
                    public void confirm(String hour, String minute) {

                        mTvDurationStartTime.setText(hour + ":" + minute);

                        Log.d(TAG, "mTvDurationStartTime: hour:" + hour + "  minute:" + minute);
                    }
                }).show(pPresetActivity.getSupportFragmentManager(), "durationStartDialog");
                break;
            default:
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void requestPreinstall() {

        int loop = mSwitchEveryDay.isChecked() ? 0 : -1;

        if (mCurrTimeType == Constant.TIME_TYPE) {

            int startHours = Integer.parseInt(mTvStartHour.getText().toString());
            int startMinute = Integer.parseInt(mTvStartMinute.getText().toString());

            int endHours = Integer.parseInt(mTvEndHour.getText().toString());
            int endMinute = Integer.parseInt(mTvEndMinute.getText().toString());

            if (startHours == 00 && startMinute == 00 && endHours == 00 && endMinute == 00) {
                return;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String time = sdf.format(new Date()) + "T" + startHours + ":" + startMinute + ":00.000Z";

            LocalTime startTime = LocalTime.of(startHours, startMinute),
                    endTime = LocalTime.of(endHours, endMinute);

            long cValue = startTime.until(endTime, ChronoUnit.MINUTES);


            Log.d(TAG, "cValue:" + cValue);
            pModel.requestReserve(3, time, "G_SetTime", cValue, loop, pPresetActivity.pChargingId, pPresetActivity.pConnectorId, new GunModel.HttpCallBack() {
                @Override
                public void onSuccess(Object bean) {
                    try {
                        JSONObject object = new JSONObject(bean.toString());
                        String data = object.getString("data");
                        int code = object.optInt("type");
                        if (code == 0) {
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
        } else if (mCurrTimeType == Constant.DURATION_TYPE) {

            if (mTvDurationHour.equals("") && mTvDurationMinute.equals("")) {
                return;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String startTime = sdf.format(new Date()) + "T" + mTvDurationStartTime.getText().toString() + ":00.000Z";

            int cValue = Integer.parseInt(mTvDurationHour) * 60 + Integer.parseInt(mTvDurationMinute);
            Log.d(TAG, "cValue:" + cValue);

            pModel.requestReserve(3, startTime, "G_SetTime", cValue, loop, pPresetActivity.pChargingId, pPresetActivity.pConnectorId, new GunModel.HttpCallBack() {
                @Override
                public void onSuccess(Object json) {
                    try {
                        JSONObject object = new JSONObject(json.toString());
                        String data = object.getString("data");
                        int code = object.optInt("type");
                        if (code == 0) {
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

    private void handleViewHide(int type) {
        if (type == Constant.TIME_TYPE) {
            mRlDuration.setVisibility(View.GONE);
            mRlDurationStart.setVisibility(View.GONE);
            mRlTime.setVisibility(View.VISIBLE);
            mCheckTime.setSelected(true);
            mCheckDuration.setSelected(false);
            mCurrTimeType = Constant.TIME_TYPE;
        } else if (type == Constant.DURATION_TYPE) {
            mRlTime.setVisibility(View.GONE);
            mRlDuration.setVisibility(View.VISIBLE);
            mRlDurationStart.setVisibility(View.VISIBLE);
            mCheckDuration.setSelected(true);
            mCheckTime.setSelected(false);
            mCurrTimeType = Constant.DURATION_TYPE;
        }
    }
}
