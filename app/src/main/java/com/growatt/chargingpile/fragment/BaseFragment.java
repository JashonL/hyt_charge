package com.growatt.chargingpile.fragment;

import static com.growatt.chargingpile.constant.Constant.DELAYED_MINUTE;
import static com.growatt.chargingpile.util.T.toast;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.growatt.chargingpile.EventBusMsg.PreinstallEvent;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.activity.ChargingRecoderActivity;
import com.growatt.chargingpile.activity.GunActivity;
import com.growatt.chargingpile.bean.ChargingBean;
import com.growatt.chargingpile.bean.GunBean;
import com.growatt.chargingpile.bean.ReservationBean;
import com.growatt.chargingpile.fragment.preset.PresetActivity;
import com.growatt.chargingpile.model.GunModel;
import com.growatt.chargingpile.view.ChargingModeDialog;
import com.mylhyl.circledialog.CircleDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created：2021/8/24 on 14:45:32
 * Author: on admin
 * Description:
 */

public abstract class BaseFragment extends Fragment {

    private static String TAG = BaseFragment.class.getSimpleName();

    protected GunActivity pActivity;

    protected GunModel pModel;

    protected PresetActivity pPresetActivity;

    private Unbinder unbinder;

    protected int pTransactionId;    //充电编号，停止充电时用

    protected boolean pIsPreinstallType = false;

    protected String pCurrGunStatus = GunBean.NONE;

    protected ReservationBean.DataBean pReservationBean;

    //判断是否已进行过加载，避免重复加载
    private boolean isLoad = false;
    //判断当前fragment是否可见
    private boolean isVisibleToUser = false;
    //判断当前fragment是否回调了resume
    private boolean isResume = false;

    protected Handler pHandler = new Handler();

    protected abstract Object setRootView();

    protected abstract void initWidget();

    public ChargingBean.DataBean pDataBean;

    public int pConnectorId;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void freshGunInfo(PreinstallEvent msg) {
        Log.d(TAG, "freshGunInfo: ");
        pHandler.post(runnableGunInfo);
    }

    //预设后3秒获取
    protected Runnable runnableGunInfo = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "runnableGun");
            requestGunInfoData();
            pHandler.postDelayed(runnableGunInfo, 3 * 1000);
        }
    };
    //进入枪1分钟获取
    protected Runnable runnableDelayedGun = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "runnableDelayedGun");
            pHandler.postDelayed(runnableDelayedGun, DELAYED_MINUTE);
            requestGunInfoData();
        }
    };

    public void startRunnable(boolean isStart) {
        if (isStart) {
            pHandler.postDelayed(runnableDelayedGun, DELAYED_MINUTE);
        } else {
            pHandler.removeCallbacks(runnableDelayedGun);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView;
        EventBus.getDefault().register(this);
        if (setRootView() instanceof Integer) {
            rootView = inflater.inflate((Integer) setRootView(), container, false);
        } else if (setRootView() instanceof View) {
            rootView = (View) setRootView();
        } else {
            throw new ClassCastException("type of setLayout() must be int or View!");
        }
        unbinder = ButterKnife.bind(this, rootView);

        if (getActivity() instanceof GunActivity) {
            pActivity = (GunActivity) getActivity();
            pDataBean = pActivity.mDataBean;

        } else if (getActivity() instanceof PresetActivity) {
            pPresetActivity = (PresetActivity) getActivity();
        }
        pModel = new GunModel();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        isResume = true;
        lazyLoad();
        initWidget();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        //Log.i(TAG, "setUserVisibleHint:" + isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        lazyLoad();
    }

    private void lazyLoad() {
        if (!isLoad && isVisibleToUser && isResume) {
            //懒加载。。。
            isLoad = true;
            Log.d(TAG, "lazyLoad: 懒加载");
            requestGunInfoData();
        }
    }

    protected void requestGunInfoData() {

    }

    protected void handleSolarMode(int mode) {

    }

    protected void handleGunStatus(GunBean bean) {

    }

    public void startPresetActivity(int type) {
        Intent intent = new Intent(pActivity, PresetActivity.class);
        if (type == 0) {
            intent.putExtra("chargingId", pDataBean.getChargeId());
            intent.putExtra("connectorId", 1);
            intent.putExtra("symbol", pDataBean.getSymbol());
        } else if (type == 1) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("reservation", pReservationBean);
            intent.putExtras(bundle);
        }
        startRunnable(false);
        startActivity(intent);
    }

    public void startChargingRecordActivity() {
        Intent intent = new Intent(pActivity, ChargingRecoderActivity.class);
        intent.putExtra("sn", pDataBean.getChargeId());
        intent.putExtra("symbol", pDataBean.getSymbol());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pActivity.jumpTo(intent, false);
        startRunnable(false);
    }

    public void requestUnLock() {
        new CircleDialog.Builder().setTitle(getString(R.string.m27温馨提示))
                .setText(getString(R.string.m是否解除该枪电子锁))
                .setWidth(0.75f)
                .setPositive(getString(R.string.m9确定), view1 -> {
                    pModel.requestGunUnlock(pDataBean.getChargeId(), pConnectorId);
                })
                .setNegative(getString(R.string.m7取消), view1 -> {

                })
                .setOnDismissListener(dialogInterface -> {

                })
                .show(getFragmentManager());
    }

    public void showChargingModeDialog(String typeMode) {
        ChargingModeDialog.newInstance(typeMode
                , new ChargingModeDialog.CallBack() {
                    @Override
                    public void confirm(int index) {
                        Log.d(TAG, "confirm: index:" + index);
                        pModel.setLimit(pDataBean.getChargeId(), index, new GunModel.HttpCallBack() {
                            @Override
                            public void onSuccess(Object json) {
                                Log.d(TAG, "onSuccess: " + json.toString());
                                try {
                                    JSONObject object = new JSONObject(json.toString());
                                    int code = object.getInt("code");
                                    if (code == 0) {
                                        requestSolarModeStatus();
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
    }

    private void requestSolarModeStatus() {
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

    /**
     * 请求充电
     */
    public void requestCharging() {
        pModel.requestCharging(pDataBean.getChargeId(), pConnectorId, new GunModel.HttpCallBack() {
            @Override
            public void onSuccess(Object bean) {
                try {
                    JSONObject object = new JSONObject(bean.toString());
                    int code = object.optInt("code");
                    if (code == 0) {
                        requestGunInfoData();
                    }
                    toast(object.getString("data"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //handleGunStatus(mBean, GunBean.CHARGING);
            }

            @Override
            public void onFailed() {

            }
        });
    }

    /**
     * 请求停止充电
     */
    public void requestStopCharging() {
        pModel.requestStopCharging(pDataBean.getChargeId(), pConnectorId, pTransactionId, new GunModel.HttpCallBack() {
            @Override
            public void onSuccess(Object json) {
                try {
                    JSONObject object = new JSONObject(json.toString());
                    int code = object.optInt("code");
                    if (code == 0) {
                        requestGunInfoData();
                    }
                    toast(object.getString("data"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //handleGunStatus(mBean, GunBean.FINISHING);

            }

            @Override
            public void onFailed() {

            }
        });
    }

    public void requestDeleteReservationNow() {
        if (pReservationBean != null) {
            pModel.deleteReservationNow(pReservationBean, new GunModel.HttpCallBack() {
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

    public void showDeleteReservationNowDialog() {
        new CircleDialog.Builder().setTitle(getString(R.string.m27温馨提示))
                .setText(getString(R.string.m340取消预约))
                .setWidth(0.75f)
                .setPositive(getString(R.string.m9确定), view1 -> {
                    requestDeleteReservationNow();
                })
                .setNegative(getString(R.string.m7取消), view1 -> {

                })
                .setOnDismissListener(dialogInterface -> {
                })
                .show(getFragmentManager());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        pHandler.removeCallbacks(runnableGunInfo);
        pHandler.removeCallbacks(runnableDelayedGun);
        isLoad = false;
        isVisibleToUser = false;
        isResume = false;
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
        Log.d(TAG, "onDestroyView: ");
    }
}
