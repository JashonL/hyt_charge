package com.growatt.chargingpile.fragment;

import static com.growatt.chargingpile.constant.Constant.DELAYED_MINUTE;
import static com.growatt.chargingpile.util.T.toast;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.growatt.chargingpile.EventBusMsg.PreinstallEvent;
import com.growatt.chargingpile.EventBusMsg.UnitMsg;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.activity.ChargingRecoderActivity;
import com.growatt.chargingpile.activity.GunActivity;
import com.growatt.chargingpile.activity.PresetActivity;
import com.growatt.chargingpile.bean.ChargingBean;
import com.growatt.chargingpile.bean.GunBean;
import com.growatt.chargingpile.bean.ReservationBean;
import com.growatt.chargingpile.model.GunModel;
import com.growatt.chargingpile.view.ChargingModeDialog;
import com.mylhyl.circledialog.CircleDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
    public void refreshSymbol(UnitMsg msg) {
        Log.d(TAG, "refreshSymbol: " + msg.getSymbol());
        if (msg.getSymbol() != null) {
            pActivity.pSymbol = msg.getSymbol();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void freshGunInfo(PreinstallEvent msg) {
        Log.d(TAG, "freshGunInfo: ");
        pHandler.postDelayed(runnableGunInfo, 3000);
    }

    protected Runnable runnableGunInfo = () -> {
        Log.d(TAG, "runnableGun");
        requestGunInfoData();
    };

    //1分钟获取
    protected Runnable runnableLoopGunInfo = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "runnableDelayedGun");
            requestGunInfoData();
            pHandler.postDelayed(runnableLoopGunInfo, DELAYED_MINUTE);
        }
    };

    public void startRunnable(boolean isStart) {
        if (isStart) {
            pHandler.postDelayed(runnableLoopGunInfo, DELAYED_MINUTE);
        } else {
            pHandler.removeCallbacks(runnableLoopGunInfo);
        }
    }

    public void showTips() {
        if (pCurrGunStatus.equals(GunBean.CHARGING)) {
            toast(getString(R.string.while_charging));
            return;
        } else if (pCurrGunStatus.equals(GunBean.UNAVAILABLE) || pCurrGunStatus.equals(GunBean.FAULTED)) {
            toast(getString(R.string.not_available));
            return;
        }
        startPresetActivity(0);
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
            pDataBean = pActivity.pDataBean;
        } else if (getActivity() instanceof PresetActivity) {
            pPresetActivity = (PresetActivity) getActivity();
        }
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
    public void onPause() {
        super.onPause();
        startRunnable(false);
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
            intent.putExtra("symbol", pActivity.pSymbol);

            if (pReservationBean != null) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("reservation", pReservationBean);
                intent.putExtras(bundle);
            }

            intent.putParcelableArrayListExtra("rate", (ArrayList<? extends Parcelable>) pActivity.pDataBean.getPriceConf());
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
                    GunModel.getInstance().requestGunUnlock(pDataBean.getUserName(), pDataBean.getChargeId(), pConnectorId, new GunModel.HttpCallBack() {
                        @Override
                        public void onSuccess(Object json) {
                            try {
                                JSONObject object = new JSONObject(json.toString());
                                String data = object.getString("data");
                                toast(data);
                                pHandler.postDelayed(runnableGunInfo, 3000);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailed() {

                        }
                    });
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
                        GunModel.getInstance().setLimit(pDataBean.getChargeId(), index, new GunModel.HttpCallBack() {
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

    public void requestSolarModeStatus() {
        GunModel.getInstance().getPileStatus(pActivity.pDataBean.getChargeId(), new GunModel.HttpCallBack() {
            @Override
            public void onSuccess(Object bean) {
                List<ChargingBean.DataBean> dataBean = (List<ChargingBean.DataBean>) bean;
                handleSolarMode(dataBean.get(0).getG_SolarMode());
                pActivity.pDataBean.setG_SolarMode(dataBean.get(0).getG_SolarMode());
            }

            @Override
            public void onFailed() {

            }
        });
    }

    public void requestCharging() {
        GunModel.getInstance().requestCharging(pDataBean.getChargeId(), pConnectorId, new GunModel.HttpCallBack() {
            @Override
            public void onSuccess(Object bean) {
                try {
                    JSONObject object = new JSONObject(bean.toString());
                    int type = object.optInt("type");
                    if (type == 0) {
                        pHandler.postDelayed(runnableGunInfo, 3000);
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
    }

    public void requestStopCharging() {

        GunModel.getInstance().requestStopCharging(pDataBean.getChargeId(), pConnectorId, pTransactionId, new GunModel.HttpCallBack() {
            @Override
            public void onSuccess(Object json) {
                try {
                    JSONObject object = new JSONObject(json.toString());
                    int type = object.optInt("type");
                    if (type == 0) {
                        pHandler.postDelayed(runnableGunInfo, 3000);
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
    }

    public void requestDeleteReservationNow() {
        if (pReservationBean != null) {
            GunModel.getInstance().deleteReservationNow(pReservationBean, new GunModel.HttpCallBack() {
                @Override
                public void onSuccess(Object json) {
                    try {
                        JSONObject object = new JSONObject(json.toString());
                        int code = object.getInt("code");
                        if (code == 0) {
                            pHandler.postDelayed(runnableGunInfo, 3000);
                            pReservationBean = null;
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
        pHandler.removeCallbacks(runnableLoopGunInfo);
        isLoad = false;
        isVisibleToUser = false;
        isResume = false;
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
        Log.d(TAG, "onDestroyView: ");
    }

    private String mNumber = "(^[1-9](\\d+)?(\\.\\d{1,2})?$)|(^0$)|(^\\d\\.\\d{1,2}$)";

    public boolean ifCanChargeMoney(String str) {
        // 判断格式是否符合金额
        if (!str.matches(mNumber)) return false;
        //判断金额不能小于等于 0
        if (Double.parseDouble(str) <= 0) {
            return false;
        }
        return true;
    }

}
