package com.growatt.chargingpile.fragment.gun;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.bean.GunBean;
import com.growatt.chargingpile.bean.ReservationBean;
import com.growatt.chargingpile.fragment.BaseFragment;
import com.growatt.chargingpile.model.GunModel;
import com.growatt.chargingpile.util.MathUtil;
import com.growatt.chargingpile.util.MyUtil;
import com.growatt.chargingpile.view.RoundProgressBar;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created：2021/8/24 on 14:43:56
 * Author: on admin
 * Description:GUN D
 */

public class FragmentD extends BaseFragment {
    private static String TAG = FragmentD.class.getSimpleName();
    @BindView(R.id.iv_handheld_state)
    ImageView mIvHandheldStatus;
    @BindView(R.id.tv_ac)
    TextView mTvModel;
    @BindView(R.id.tv_status)
    TextView mTvStatus;
    @BindView(R.id.tv_solar)
    TextView mTvSolarMode;
    @BindView(R.id.iv_pile_model)
    ImageView mIvPileModel;
    @BindView(R.id.iv_circle_status)
    ImageView mIvCircleStatus;
    @BindView(R.id.iv_switch)
    ImageView mIvSwitchStatus;
    @BindView(R.id.tv_switch_status)
    TextView mTvSwitchStatus;
    @BindView(R.id.iv_gif)
    ImageView mIvChargingGif;
    @BindView(R.id.tv_time)//预设 开始时间~结束时间
    TextView mTvPreinstallTime;
    @BindView(R.id.iv_preinstall_type)
    ImageView mIvPreinstallType;
    @BindView(R.id.cb_everyday)
    CheckBox mCbEveryDay;
    @BindView(R.id.ll_default_charging)//普通充电UI
    LinearLayout mLlDefaultCharging;
    @BindView(R.id.tv_electricity_kwh)
    TextView mTvElectricity;          //电费
    @BindView(R.id.tv_rate_value)
    TextView mTvRate;          //费率
    @BindView(R.id.tv_hour)
    TextView mTvDurationHour;  //时长
    @BindView(R.id.tv_min)
    TextView mTvDurationMin;   //时分
    @BindView(R.id.tv_money_value)
    TextView mTvMoney;         //金额
    @BindView(R.id.tv_current)
    TextView mTvCurrent;       //电流
    @BindView(R.id.tv_voltage_values)
    TextView mTvVoltage;       //电压
    @BindView(R.id.tv_charging_finish)
    TextView mTvChargingFinish;
    @BindView(R.id.ll_default_av)
    LinearLayout mLlDefaultAV;
    @BindView(R.id.ll_preinstall_charging)//预约充电UI
    LinearLayout mLlPreinstallCharging;
    @BindView(R.id.tv_electricity_kwh_preinstall)
    TextView mTvPreinstallElectricity;   //电费
    @BindView(R.id.tv_rate_value_preinstall)
    TextView mTvPreinstallRate;          //费率
    @BindView(R.id.tv_hour_preinstall)
    TextView mTvPreinstallDurationHour;  //时长
    @BindView(R.id.tv_min_preinstall)
    TextView mTvPreinstallDurationMin;   //时分
    @BindView(R.id.tv_money_value_preinstall)
    TextView mTvPreinstallMoney;         //金额
    @BindView(R.id.tv_current_preinstall)
    TextView mTvPreinstallCurrent;       //电流
    @BindView(R.id.tv_voltage_values_preinstall)
    TextView mTvPreinstallVoltage;       //电压
    @BindView(R.id.rl_preinstall)
    RelativeLayout mRlPreinstallBg;
    @BindView(R.id.ll_preinstall_a_v)
    LinearLayout mLlPreinstallAV;
    @BindView(R.id.tv_preinstall_charging_finish)
    TextView mTvPreinstallChargingFinish;
    @BindView(R.id.pb_type)
    RoundProgressBar mProgressBar;
    @BindView(R.id.tv_progress_value)
    TextView mTvProgressValue;
    @BindView(R.id.tv_preinstall_type)
    TextView mTvPreinstallType;
    @BindView(R.id.tv_1)
    TextView mTv1;
    @BindView(R.id.tv_2)
    TextView mTv2;
    @BindView(R.id.tv_3)
    TextView mTv3;
    @BindView(R.id.tv_4)
    TextView mTv4;
    @BindView(R.id.ll_exception)//异常提示
    LinearLayout mLlException;
    @BindView(R.id.iv_exception_icon)
    ImageView mIvExceptionIcon;
    @BindView(R.id.tv_exception_status)
    TextView mTvExceptionStatus;
    @BindView(R.id.tv_exception_status_hint)
    TextView mTvExceptionStatusHint;
    @BindView(R.id.ll_preinstall)//预设
    LinearLayout mLlPreinstall;
    @BindView(R.id.ll_please_vehicle)//连接车辆
    LinearLayout mLlPleaseVehicle;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected Object setRootView() {
        return R.layout.fragment_gun;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pConnectorId = 4;
    }

    @Override
    protected void initWidget() {
        Log.d(TAG, "initWidget:");
        handleModel(pActivity.mDataBean.getModel());
        handleSolarMode(pActivity.mDataBean.getG_SolarMode());
        initChargingGif();
        startRunnable(true);
        initPullView();
    }

    @OnClick({R.id.tv_time, R.id.ll_pile_status, R.id.ll_lock, R.id.ll_record, R.id.ll_preset,
            R.id.iv_preinstall_delete, R.id.iv_switch})
    public void onClickListener(View view) {
        switch (view.getId()) {
            case R.id.iv_switch:
                if (pCurrGunStatus.equals(GunBean.CHARGING)) {
                    requestStopCharging();
                } else if (pCurrGunStatus.equals(GunBean.AVAILABLE) || pCurrGunStatus.equals(GunBean.PREPARING) || pCurrGunStatus.equals(GunBean.FINISHING)) {
                    requestCharging();
                }
                break;
            case R.id.iv_preinstall_delete:
                showDeleteReservationNowDialog();
                break;
            case R.id.ll_pile_status:
                showChargingModeDialog(mTvSolarMode.getText().toString());
                break;
            case R.id.ll_lock:
                requestUnLock();
                break;
            case R.id.ll_record:
                startChargingRecordActivity();
                break;
            case R.id.tv_time:
                startPresetActivity(1);
                break;
            case R.id.ll_preset:
                if (pCurrGunStatus.equals(GunBean.UNAVAILABLE) || pCurrGunStatus.equals(GunBean.FAULTED)) {
                    return;
                }
                startPresetActivity(0);
                break;
            default:
                break;
        }
    }

    @Override
    protected void handleGunStatus(GunBean bean) {
        String status = bean.getData().getStatus();
        String key = bean.getData().getcKey();//G_SetTime  G_SetAmount  G_SetEnergy
        Log.d(TAG, "handleGunStatus: status:" + status + "  key:" + key);
        pCurrGunStatus = status;
        switch (status) {
            case GunBean.AVAILABLE:// 空闲
                mTvStatus.setText(getString(R.string.m117空闲));
                mLlException.setVisibility(View.GONE);
                mLlDefaultCharging.setVisibility(View.GONE);
                mLlPreinstallCharging.setVisibility(View.GONE);
                mLlPleaseVehicle.setVisibility(View.GONE);

                pHandler.removeCallbacks(runnableGunInfo);
                mIvCircleStatus.setImageResource(R.drawable.ic_green_circle);
                mIvSwitchStatus.setImageResource(R.drawable.ic_green_on);
                mTvSwitchStatus.setTextColor(ContextCompat.getColor(pActivity, R.color.charging_start));

                mTvPreinstallChargingFinish.setVisibility(View.GONE);
                mTvChargingFinish.setVisibility(View.GONE);

                break;
            case GunBean.RESERVENOW://有预约
                pHandler.removeCallbacks(runnableGunInfo);
                mTvStatus.setText(getString(R.string.m339预约));
                mIvCircleStatus.setImageResource(R.drawable.ic_green_circle);
                mIvSwitchStatus.setImageResource(R.drawable.ic_charging_off);
                mTvSwitchStatus.setTextColor(ContextCompat.getColor(pActivity, R.color.charging_start));
                mLlPreinstall.setVisibility(View.VISIBLE);
                //获取预约
                pModel.getReservationNow(pActivity.mDataBean.getChargeId(), 1, new GunModel.HttpCallBack() {
                    @Override
                    public void onSuccess(Object bean) {
                        pReservationBean = (ReservationBean.DataBean) bean;
                        handleReservationInfo(pReservationBean);
                    }

                    @Override
                    public void onFailed() {

                    }
                });

                mTvPreinstallChargingFinish.setVisibility(View.GONE);
                mTvChargingFinish.setVisibility(View.GONE);
                break;
            case GunBean.PREPARING://m119准备中
                mLlException.setVisibility(View.GONE);
                mLlDefaultCharging.setVisibility(View.GONE);
                mLlPreinstallCharging.setVisibility(View.GONE);
                mTvStatus.setText(getString(R.string.m119准备中));
                mLlPreinstall.setVisibility(View.GONE);
                mIvCircleStatus.setImageResource(R.drawable.ic_green_circle);
                mIvSwitchStatus.setImageResource(R.drawable.ic_green_on);
                mTvSwitchStatus.setTextColor(ContextCompat.getColor(pActivity, R.color.charging_start));
                mLlPleaseVehicle.setVisibility(View.VISIBLE);

                mTvPreinstallChargingFinish.setVisibility(View.GONE);
                mTvChargingFinish.setVisibility(View.GONE);
                break;
            case GunBean.CHARGING://充电中 1.普通充电 2.其他充电
                mLlException.setVisibility(View.GONE);
                mLlPleaseVehicle.setVisibility(View.GONE);
                mTvPreinstallChargingFinish.setVisibility(View.GONE);
                mTvChargingFinish.setVisibility(View.GONE);
                mTvStatus.setText(getString(R.string.m118充电中));
                pTransactionId = bean.getData().getTransactionId();
                int timeCharging = bean.getData().getCtime();
                int hourCharging = timeCharging / 60;
                int minCharging = timeCharging % 60;
                if (TextUtils.isEmpty(key)) {
                    pIsPreinstallType = false;
                    mLlDefaultCharging.setVisibility(View.VISIBLE);
                    mLlDefaultAV.setVisibility(View.VISIBLE);
                    setChargingInfoUi(0, bean.getData().getEnergy(), bean.getData().getRate(),
                            hourCharging, minCharging, bean.getData().getCost(), bean.getData().getCurrent(), bean.getData().getVoltage());
                    Log.d(TAG, "普通充电");
                } else {
                    Log.d(TAG, "预约充电");
                    pIsPreinstallType = true;
                    mLlPreinstallCharging.setVisibility(View.VISIBLE);
                    mLlPreinstallAV.setVisibility(View.VISIBLE);
                    mRlPreinstallBg.setVisibility(View.VISIBLE);
                    setChargingInfoUi(1, bean.getData().getEnergy(), bean.getData().getRate(),
                            hourCharging, minCharging, bean.getData().getCost(), bean.getData().getCurrent(), bean.getData().getVoltage());
                    switch (key) {
                        case "G_SetTime":
                            mTv1.setText(hourCharging);
                            mTv2.setText(getString(R.string.m207时));
                            mTv3.setText(minCharging);
                            mTv4.setText(getString(R.string.m208分));
                            mTvPreinstallType.setText(R.string.time);

                            mProgressBar.setCricleColor(Color.parseColor("#FFDDDD"));
                            mProgressBar.setCricleProgressColor(Color.parseColor("#FF8B8B"));

                            double presetValue_value = Double.parseDouble(bean.getData().getcValue());
                            double chargedValue_value = bean.getData().getCtime();

                            if (presetValue_value > 0) {
                                mProgressBar.setMax((float) presetValue_value);

                            }
                            mProgressBar.setProgress((float) chargedValue_value);

                            double value;
                            if (presetValue_value == 0.0) {
                                value = 0.0;
                            } else {
                                value = chargedValue_value * 100 / presetValue_value;
                            }

                            double percent = MyUtil.divide(value, 2);
                            mTvProgressValue.setText(percent + "%");


                            break;
                        case "G_SetAmount":
                            mTv1.setText(bean.getData().getcValue());
                            if (TextUtils.isEmpty(bean.getData().getSymbol())) {
                                mTv2.setText("£");
                            } else {
                                mTv2.setText(bean.getData().getSymbol());
                            }
                            mTv3.setVisibility(View.GONE);
                            mTv4.setVisibility(View.GONE);
                            mTvPreinstallType.setText(getString(R.string.m200金额));

                            mProgressBar.setCricleColor(Color.parseColor("#FDF6E4"));
                            mProgressBar.setCricleProgressColor(Color.parseColor("#FFDD8B"));

                            double presetValue_value_Amount = Double.parseDouble(bean.getData().getcValue());
                            double chargedValue_value_Amount = bean.getData().getCost();

                            if (presetValue_value_Amount > 0) {
                                mProgressBar.setMax((float) presetValue_value_Amount);

                            }
                            mProgressBar.setProgress((float) chargedValue_value_Amount);

                            double amountValue;
                            if (presetValue_value_Amount == 0.0) {
                                amountValue = 0.0;
                            } else {
                                amountValue = chargedValue_value_Amount * 100 / presetValue_value_Amount;
                            }

                            double percent1 = MyUtil.divide(amountValue, 2);
                            mTvProgressValue.setText(percent1 + "%");

                            break;
                        case "G_SetEnergy":
                            mTv1.setText(bean.getData().getcValue());
                            mTv2.setText("kwh");
                            mTv3.setVisibility(View.GONE);
                            mTv4.setVisibility(View.GONE);
                            mTvPreinstallType.setText(getString(R.string.m201电量));

                            mProgressBar.setCricleColor(Color.parseColor("#D6FFE6"));
                            mProgressBar.setCricleProgressColor(Color.parseColor("#30E578"));

                            double presetValue_value_Energy = Double.parseDouble(bean.getData().getcValue());
                            double chargedValue_value_Energy = bean.getData().getEnergy();

                            if (presetValue_value_Energy > 0) {
                                mProgressBar.setMax((float) presetValue_value_Energy);
                            }
                            mProgressBar.setProgress((float) chargedValue_value_Energy);

                            double energyValue;
                            if (presetValue_value_Energy == 0.0) {
                                energyValue = 0.0;
                            } else {
                                energyValue = chargedValue_value_Energy * 100 / presetValue_value_Energy;
                            }

                            double percent2 = MyUtil.divide(energyValue, 2);
                            mTvProgressValue.setText(percent2 + "%");

                            break;
                        default:
                            break;
                    }
                }
                mIvCircleStatus.setImageResource(R.drawable.ic_green_circle);
                mIvChargingGif.setVisibility(View.VISIBLE);
                mIvSwitchStatus.setImageResource(R.drawable.ic_charging_off);
                mTvSwitchStatus.setTextColor(ContextCompat.getColor(pActivity, R.color.charging_off));
                mTvSwitchStatus.setText(getString(R.string.m108停止充电));
                GifDrawable startDrawable = (GifDrawable) mIvChargingGif.getDrawable();
                if (!startDrawable.isRunning()) {
                    startDrawable.start();
                }
                break;
            case GunBean.FINISHING://m120充电结束
                mLlException.setVisibility(View.GONE);
                mIvChargingGif.setVisibility(View.GONE);
                mTvStatus.setText(getString(R.string.m120充电结束));
                mIvCircleStatus.setImageResource(R.drawable.ic_green_circle);
                mIvSwitchStatus.setImageResource(R.drawable.ic_charging_finish);
                mTvSwitchStatus.setTextColor(ContextCompat.getColor(pActivity, R.color.charging_start));
                mTvSwitchStatus.setText(getString(R.string.m120充电结束));
                GifDrawable endDrawable = (GifDrawable) mIvChargingGif.getDrawable();
                if (endDrawable.isRunning()) {
                    endDrawable.stop();
                }

                if (pIsPreinstallType) {//预约充电结束
                    mRlPreinstallBg.setVisibility(View.GONE);
                    mLlPreinstallAV.setVisibility(View.GONE);
                    mTvPreinstallChargingFinish.setVisibility(View.VISIBLE);
                } else {
                    mLlDefaultAV.setVisibility(View.GONE);
                    mTvChargingFinish.setVisibility(View.VISIBLE);
                }

                break;
            case GunBean.SUSPENDEEV://m133车拒绝充电
                break;
            case GunBean.SUSPENDEDEVSE://m292桩拒绝充电
                break;
            case GunBean.FAULTED://m121故障
                mLlPleaseVehicle.setVisibility(View.GONE);
                mLlDefaultCharging.setVisibility(View.GONE);
                mLlPreinstallCharging.setVisibility(View.GONE);
                mIvCircleStatus.setImageResource(R.drawable.ic_gray_circle);
                mIvSwitchStatus.setImageResource(R.drawable.ic_gray_on);
                mIvHandheldStatus.setImageResource(R.drawable.ic_handheld_overvoltage);
                mTvSwitchStatus.setTextColor(ContextCompat.getColor(pActivity, R.color.charging_default));
                mTvStatus.setText(getString(R.string.m121故障));
                mIvExceptionIcon.setImageResource(R.drawable.ic_exception_overvoltage);
                mTvExceptionStatus.setText(getString(R.string.m121故障));
                mTvExceptionStatus.setTextColor(ContextCompat.getColor(pActivity, R.color.charging_off));
                mTvExceptionStatusHint.setText(getString(R.string.pile_failure));
                mLlException.setVisibility(View.VISIBLE);
                break;
            case GunBean.UNAVAILABLE://m122不可用
                pHandler.removeCallbacks(runnableGunInfo);
                mLlPleaseVehicle.setVisibility(View.GONE);
                mLlDefaultCharging.setVisibility(View.GONE);
                mLlPreinstallCharging.setVisibility(View.GONE);
                mIvCircleStatus.setImageResource(R.drawable.ic_gray_circle);
                mIvSwitchStatus.setImageResource(R.drawable.ic_gray_on);
                mTvSwitchStatus.setTextColor(ContextCompat.getColor(pActivity, R.color.charging_default));
                mTvStatus.setText(getString(R.string.m122不可用));
                mIvExceptionIcon.setImageResource(R.drawable.ic_exception_unavailable);
                mTvExceptionStatus.setText(getString(R.string.m122不可用));
                mTvExceptionStatusHint.setText(getString(R.string.pile_available));
                mLlException.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }

    }

    private void setChargingInfoUi(int type, double electricity, double rate, int hour, int min, double money, double current, double voltage) {
        Log.d(TAG, "setChargingInfoUi: electricity:" + electricity + " rate:" + rate + " hour:" + hour + " min:" + min + " money:" + money +
                " current" + current + " voltage:" + voltage);
        if (type == 0) {
            mTvElectricity.setText(MathUtil.roundDouble2String(electricity, 3));
            mTvRate.setText(String.valueOf(rate));

            mTvDurationHour.setText(String.valueOf(hour));
            mTvDurationMin.setText(String.valueOf(min));

            mTvMoney.setText(MathUtil.roundDouble2String(money, 3));

            mTvCurrent.setText(String.valueOf(current));
            mTvVoltage.setText(String.valueOf(voltage));
        } else {
            mTvPreinstallElectricity.setText(MathUtil.roundDouble2String(electricity, 3));
            mTvPreinstallRate.setText(String.valueOf(rate));

            mTvPreinstallDurationHour.setText(String.valueOf(hour));
            mTvPreinstallDurationMin.setText(String.valueOf(min));

            mTvPreinstallMoney.setText(MathUtil.roundDouble2String(money, 3));

            mTvPreinstallCurrent.setText(String.valueOf(current));
            mTvPreinstallVoltage.setText(String.valueOf(voltage));
        }

    }

    private void handleReservationInfo(ReservationBean.DataBean data) {
        if (data.getLoopType() == 0) {
            mCbEveryDay.setChecked(true);
        } else {
            mCbEveryDay.setChecked(false);
        }
        String startTime = data.getExpiryDate().substring(11, 16);
        String endTime = data.getEndDate().substring(11, 16);

        switch (data.getCKey()) {
            case "G_SetTime":
                mTvPreinstallTime.setText(startTime + "~" + endTime);
                mIvPreinstallType.setImageResource(R.drawable.ic_time_preinstall);
                break;
            case "G_SetAmount":
                mTvPreinstallTime.setText(startTime);
                mIvPreinstallType.setImageResource(R.drawable.ic_amount_preinstall);
                break;
            case "G_SetEnergy":
                mTvPreinstallTime.setText(startTime);
                mIvPreinstallType.setImageResource(R.drawable.ic_energy_preinstall);
                break;
            default:
                break;
        }
        Log.d(TAG, "handleReservationInfo: startTime:" + startTime + "endTime:" + endTime + " isCheck:" + data.getLoopType());
    }

    private void handleModel(String model) {
        Log.d(TAG, "handleModel:" + model);
        String str = null;
        if (model.toLowerCase().contains("/")) {
            str = getString(R.string.m交直流);
        } else if ("ac".equalsIgnoreCase(model)) {
            str = getString(R.string.m112交流);
        } else {
            str = getString(R.string.m113直流);
        }
        mTvModel.setText(str);
    }

    private void initPullView() {
        mSwipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(pActivity, R.color.maincolor_1));
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            requestGunInfoData();
        });
    }

    private void initChargingGif() {
        Glide.with(getContext())
                .asGif()
                .load(R.drawable.ic_charging_gif)
                .into(mIvChargingGif);
    }

    @Override
    protected void handleSolarMode(int mode) {
        Log.d(TAG, "handleSolarMode:" + mode);
        if (mode == 0) {
            mTvSolarMode.setText("Fast");
            mTvSolarMode.setTextColor(ContextCompat.getColor(getContext(), R.color.tv_pile_fast_color));
            mIvPileModel.setImageResource(R.drawable.ic_fast_c);
        } else if (mode == 1) {
            mTvSolarMode.setText("ECO");
            mTvSolarMode.setTextColor(ContextCompat.getColor(getContext(), R.color.tv_pile_eco_color));
            mIvPileModel.setImageResource(R.drawable.ic_eco_c);
        } else if (mode == 2) {
            mTvSolarMode.setText("ECO +");
            mTvSolarMode.setTextColor(ContextCompat.getColor(getContext(), R.color.tv_pile_eco_plus_color));
            mIvPileModel.setImageResource(R.drawable.ic_eco_plus_c);
        }
    }

    @Override
    protected void requestGunInfoData() {
        Log.d(TAG, "requestGunInfoData: " + pConnectorId);
        mSwipeRefreshLayout.setRefreshing(true);
        pModel.getChargingGunStatus(pDataBean.getChargeId(), pConnectorId, new GunModel.HttpCallBack<GunBean>() {
            @Override
            public void onSuccess(GunBean bean) {
                Log.d(TAG, "onSuccess: " + bean.getData().toString());
                if (bean.getData() != null) {
                    if (mSwipeRefreshLayout != null) {
                        handleGunStatus(bean);
                    }
                }
                if (mSwipeRefreshLayout != null) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailed() {
                if (mSwipeRefreshLayout != null) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mIvChargingGif != null && mIvChargingGif.getVisibility() == View.VISIBLE) {
            GifDrawable endDrawable = (GifDrawable) mIvChargingGif.getDrawable();
            if (endDrawable.isRunning()) {
                endDrawable.stop();
            }
        }
    }
}
