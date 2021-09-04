package com.growatt.chargingpile.fragment.gun;

import static com.growatt.chargingpile.util.T.toast;

import android.content.Intent;
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
import com.growatt.chargingpile.EventBusMsg.PreinstallEvent;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.activity.ChargingRecoderActivity;
import com.growatt.chargingpile.bean.ChargingBean;
import com.growatt.chargingpile.bean.GunBean;
import com.growatt.chargingpile.bean.ReservationBean;
import com.growatt.chargingpile.fragment.BaseFragment;
import com.growatt.chargingpile.fragment.preset.PresetActivity;
import com.growatt.chargingpile.model.GunModel;
import com.growatt.chargingpile.util.MathUtil;
import com.growatt.chargingpile.util.MyUtil;
import com.growatt.chargingpile.view.ChargingModeDialog;
import com.growatt.chargingpile.view.RoundProgressBar;
import com.mylhyl.circledialog.CircleDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created：2021/8/24 on 14:43:56
 * Author: on admin
 * Description: GUN A
 */

public class FragmentA extends BaseFragment {
    private static String TAG = FragmentA.class.getSimpleName();
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

    private String mCurrGunStatus = GunBean.NONE;
    private int mTransactionId;    //充电编号，停止充电时用

    private ReservationBean.DataBean mReservationBean;

    public boolean mIsPreinstallType = false;


    @OnClick({R.id.tv_time, R.id.ll_pile_status, R.id.ll_lock, R.id.ll_record, R.id.ll_preset,
            R.id.iv_preinstall_delete, R.id.iv_switch})
    public void onClickListener(View view) {
        switch (view.getId()) {
            case R.id.iv_switch:
                if (mCurrGunStatus.equals(GunBean.CHARGING)) {
//                    pModel.requestStopCharging(pActivity.mDataBean.getChargeId(), 1, mTransactionId, new GunModel.HttpCallBack() {
//                        @Override
//                        public void onSuccess(Object json) {
//                            try {
//                                JSONObject object = new JSONObject(json.toString());
//                                int type = object.optInt("type");
//                                if (type == 0) {
//                                    getGunInfoData();
//                                }
//                                toast(object.getString("data"));
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                        @Override
//                        public void onFailed() {
//
//                        }
//                    });
                    handleGunStatus(mBean, GunBean.FINISHING);

                } else if (mCurrGunStatus.equals(GunBean.AVAILABLE) || mCurrGunStatus.equals(GunBean.PREPARING) || mCurrGunStatus.equals(GunBean.FINISHING) || mCurrGunStatus.equals(GunBean.UNAVAILABLE)) {
                    handleGunStatus(mBean, GunBean.CHARGING);
                }
                break;
            case R.id.iv_preinstall_delete:
                new CircleDialog.Builder().setTitle(getString(R.string.m27温馨提示))
                        .setText(getString(R.string.m340取消预约))
                        .setWidth(0.75f)
                        .setPositive(getString(R.string.m9确定), view1 -> {
                            deleteReservationNow();
                        })
                        .setNegative(getString(R.string.m7取消), view1 -> {

                        })
                        .setOnDismissListener(dialogInterface -> {
                        })
                        .show(getFragmentManager());
                break;
            case R.id.tv_time:
                Intent intent = new Intent(pActivity, PresetActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("reservation", mReservationBean);
                intent.putExtras(bundle);
                startActivity(intent);
                startRunnable(false);
                break;
            case R.id.ll_preset:
                if (mCurrGunStatus.equals(GunBean.UNAVAILABLE) || mCurrGunStatus.equals(GunBean.FAULTED)) {
                    return;
                }
                Intent intentPreset = new Intent(pActivity, PresetActivity.class);
                intentPreset.putExtra("chargingId", pActivity.mDataBean.getChargeId());
                intentPreset.putExtra("connectorId", 1);
                intentPreset.putExtra("symbol", pActivity.mDataBean.getSymbol());
                startActivity(intentPreset);
                startRunnable(false);
                break;
            case R.id.ll_pile_status:
                ChargingModeDialog.newInstance(mTvSolarMode.getText().toString()
                        , new ChargingModeDialog.CallBack() {
                            @Override
                            public void confirm(int index) {
                                Log.d(TAG, "confirm: index:" + index);
                                pModel.setLimit(pActivity.mDataBean.getChargeId(), index, new GunModel.HttpCallBack() {
                                    @Override
                                    public void onSuccess(Object json) {
                                        Log.d(TAG, "onSuccess: " + json.toString());
                                        try {
                                            JSONObject object = new JSONObject(json.toString());
                                            int code = object.getInt("code");
                                            if (code == 0) {
                                                getChargingStatus();
                                            }
                                            String data = object.getString("data");
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

                            @Override
                            public void cancel() {

                            }
                        }).show(pActivity.getSupportFragmentManager(), "ModeDialog");

                break;
            case R.id.ll_lock:
                new CircleDialog.Builder().setTitle(getString(R.string.m27温馨提示))
                        .setText(getString(R.string.m是否解除该枪电子锁))
                        .setWidth(0.75f)
                        .setPositive(getString(R.string.m9确定), view1 -> {
                            pModel.requestGunUnlock(pActivity.mDataBean.getChargeId(), 1);
                        })
                        .setNegative(getString(R.string.m7取消), view1 -> {

                        })
                        .setOnDismissListener(dialogInterface -> {

                        })
                        .show(getFragmentManager());
                break;
            case R.id.ll_record:
                Intent intent4 = new Intent(pActivity, ChargingRecoderActivity.class);
                intent4.putExtra("sn", pActivity.mDataBean.getChargeId());
                intent4.putExtra("symbol", pActivity.mDataBean.getSymbol());
                intent4.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                pActivity.jumpTo(intent4, false);
                startRunnable(false);
                break;
            default:
                break;
        }
    }

    private void deleteReservationNow() {
        if (mReservationBean != null) {
            pModel.deleteReservationNow(mReservationBean, new GunModel.HttpCallBack() {
                @Override
                public void onSuccess(Object json) {
                    try {
                        JSONObject object = new JSONObject(json.toString());
                        int code = object.getInt("code");
                        if (code == 0) {
                            pHandler.post(runnableGunInfo);
                        }
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void freshGunInfo(PreinstallEvent msg) {
        Log.d(TAG, "freshGunInfo: ");
        pHandler.post(runnableGunInfo);
    }

    private void getChargingStatus() {
        pModel.getPileStatus(pActivity.mDataBean.getChargeId(), new GunModel.HttpCallBack() {
            @Override
            public void onSuccess(Object bean) {
                ChargingBean.DataBean dataBean = (ChargingBean.DataBean) bean;
                handleSolarMode(dataBean.getG_SolarMode());
            }

            @Override
            public void onFailed() {

            }
        });
    }

    @Override
    protected Object setRootView() {
        return R.layout.fragment_gun;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
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

    private void initPullView() {
        mSwipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(pActivity, R.color.maincolor_1));
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            getGunInfoData();
        });
    }

    private void initChargingGif() {
        Glide.with(getContext())
                .asGif()
                .load(R.drawable.ic_charging_gif)
                .into(mIvChargingGif);
    }

    GunBean mBean;

    @Override
    protected void getGunInfoData() {
        mSwipeRefreshLayout.setRefreshing(true);
        Log.d(TAG, "requestData: " + pActivity.mDataBean.toString());
        pModel.getChargingGunStatus(pActivity.mDataBean.getChargeId(), 1, new GunModel.HttpCallBack<GunBean>() {
            @Override
            public void onSuccess(GunBean bean) {
                Log.d(TAG, "onSuccess: " + bean.getData().toString());
                mBean = bean;
                if (bean.getData() != null) {
                    handleGunStatus(bean, GunBean.UNAVAILABLE);
                    //handleGunStatus(bean.getData().getStatus());
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailed() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void handleGunStatus(GunBean bean, String state) {
        String status = bean.getData().getStatus();
        String key = "G_SetEnergy";//G_SetTime  G_SetAmount  G_SetEnergy
        Log.d(TAG, "handleGunStatus: status:" + status + "  key:" + key);
        mCurrGunStatus = state;
        switch (state) {
            case GunBean.AVAILABLE:// 可用状态
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
            case GunBean.RESERVENOW://正在预约
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
                        mReservationBean = (ReservationBean.DataBean) bean;
                        handleReservationInfo(mReservationBean);
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
                mTransactionId = bean.getData().getTransactionId();
                int timeCharging = bean.getData().getCtime();
                int hourCharging = timeCharging / 60;
                int minCharging = timeCharging % 60;
                if (TextUtils.isEmpty(key)) {
                    mLlDefaultCharging.setVisibility(View.VISIBLE);
                    mLlDefaultAV.setVisibility(View.VISIBLE);
                    mIsPreinstallType = false;
                    setChargingInfoUi(0, bean.getData().getEnergy(), bean.getData().getRate(),
                            hourCharging, minCharging, bean.getData().getCost(), bean.getData().getCurrent(), bean.getData().getVoltage());
                    Log.d(TAG, "handleGunStatus: " + "普通充电");
                } else {
                    mLlPreinstallCharging.setVisibility(View.VISIBLE);
                    mLlPreinstallAV.setVisibility(View.VISIBLE);
                    mRlPreinstallBg.setVisibility(View.VISIBLE);
                    setChargingInfoUi(1, bean.getData().getEnergy(), bean.getData().getRate(),
                            hourCharging, minCharging, bean.getData().getCost(), bean.getData().getCurrent(), bean.getData().getVoltage());
                    mIsPreinstallType = true;
                    switch (key) {
                        case "G_SetTime":
                            mTv1.setText("1");
                            mTv2.setText("h");
                            mTv3.setText("2");
                            mTv4.setText("min");
                            mTvPreinstallType.setText("时间");

                            mProgressBar.setCricleColor(Color.parseColor("#FFDDDD"));
                            mProgressBar.setCricleProgressColor(Color.parseColor("#FF8B8B"));

                            double presetValue_value = Double.parseDouble(bean.getData().getcValue());
                            double chargedValue_value = bean.getData().getCtime();

                            if (presetValue_value > 0) {
                                mProgressBar.setMax((float) presetValue_value);

                            }
                            mProgressBar.setProgress((float) chargedValue_value);
                            double v = chargedValue_value * 100 / presetValue_value;
                            double percent = MyUtil.divide(0.0, 2);
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
                            mTvPreinstallType.setText("金额");

                            mProgressBar.setCricleColor(Color.parseColor("#FDF6E4"));
                            mProgressBar.setCricleProgressColor(Color.parseColor("#FFDD8B"));

                            double presetValue_value_Amount = Double.parseDouble(bean.getData().getcValue());
                            double chargedValue_value_Amount = bean.getData().getCost();

                            if (presetValue_value_Amount > 0) {
                                mProgressBar.setMax((float) chargedValue_value_Amount);

                            }
                            mProgressBar.setProgress((float) chargedValue_value_Amount);
                            double v1 = chargedValue_value_Amount * 100 / presetValue_value_Amount;
                            double percent1 = MyUtil.divide(0.0, 2);
                            mTvProgressValue.setText(percent1 + "%");

                            break;
                        case "G_SetEnergy":
                            mTv1.setText(bean.getData().getcValue());
                            mTv2.setText("kwh");
                            mTv3.setVisibility(View.GONE);
                            mTv4.setVisibility(View.GONE);
                            mTvPreinstallType.setText("电量");

                            mProgressBar.setCricleColor(Color.parseColor("#D6FFE6"));
                            mProgressBar.setCricleProgressColor(Color.parseColor("#30E578"));

                            double presetValue_value_Energy = Double.parseDouble(bean.getData().getcValue());
                            double chargedValue_value_Energy = bean.getData().getEnergy();

                            if (presetValue_value_Energy > 0) {
                                mProgressBar.setMax((float) presetValue_value_Energy);

                            }
                            mProgressBar.setProgress((float) chargedValue_value_Energy);
                            double v2 = chargedValue_value_Energy * 100 / presetValue_value_Energy;
                            double percent2 = MyUtil.divide(0.0, 2);
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

                if (mIsPreinstallType) {//预约充电结束
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
                mTvExceptionStatusHint.setText("Pile failure, please switch to \n" +
                        "other charging piles.\n");
                mLlException.setVisibility(View.VISIBLE);
                break;
            case GunBean.UNAVAILABLE://m122不可用
                mLlPleaseVehicle.setVisibility(View.GONE);
                mLlDefaultCharging.setVisibility(View.GONE);
                mLlPreinstallCharging.setVisibility(View.GONE);
                mIvCircleStatus.setImageResource(R.drawable.ic_gray_circle);
                mIvSwitchStatus.setImageResource(R.drawable.ic_gray_on);
                mTvSwitchStatus.setTextColor(ContextCompat.getColor(pActivity, R.color.charging_default));
                mTvStatus.setText(getString(R.string.m122不可用));
                mIvExceptionIcon.setImageResource(R.drawable.ic_exception_unavailable);
                mTvExceptionStatus.setText(getString(R.string.m122不可用));
                mTvExceptionStatusHint.setText("Pile is not available. \n" +
                        "Please switch to other charging piles.\n" +
                        "\n");
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

    private void handleSolarMode(int mode) {
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
