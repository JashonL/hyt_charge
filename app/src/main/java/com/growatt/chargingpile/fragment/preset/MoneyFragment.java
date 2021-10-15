package com.growatt.chargingpile.fragment.preset;

import static com.growatt.chargingpile.util.T.toast;

import android.content.Intent;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.growatt.chargingpile.EventBusMsg.DeletePreinstallEvent;
import com.growatt.chargingpile.EventBusMsg.PreinstallEvent;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.fragment.BaseFragment;
import com.growatt.chargingpile.model.GunModel;
import com.growatt.chargingpile.setting.PileSettingActivity;
import com.growatt.chargingpile.view.TypeSelectDialog;
import com.mylhyl.circledialog.CircleDialog;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created：2021/8/26 on 14:19:04
 * Author: on admin
 * Description:预设金额
 */

public class MoneyFragment extends BaseFragment {

    private static String TAG = MoneyFragment.class.getSimpleName();
    @BindView(R.id.ll_every_day)
    LinearLayout mLlEveryDay;
    @BindView(R.id.tv_start_time)
    TextView mTvStartTime;
    @BindView(R.id.edit_money)
    EditText mEditMoney;
    @BindView(R.id.sw_day)
    Switch mSwitchEveryDay;
    @BindView(R.id.tv_money_type)
    TextView mTvMoneyType;

    @Override
    protected Object setRootView() {
        return R.layout.fragment_money;
    }

    @Override
    protected void initWidget() {
        if (pPresetActivity.pReservationBean != null && pPresetActivity.pReservationBean.getCKey().equals("G_SetAmount")) {
            mEditMoney.setText(pPresetActivity.pReservationBean.getCValue());
            mTvStartTime.setText(pPresetActivity.pReservationBean.getExpiryDate().substring(11, 16));
            if (pPresetActivity.pReservationBean.getLoopType() == 0) {
                mLlEveryDay.setVisibility(View.VISIBLE);
                mSwitchEveryDay.setChecked(true);
            }
        }

        if (TextUtils.isEmpty(pPresetActivity.pSymbol)) {
            new CircleDialog.Builder()
                    .setText(getString(R.string.you_need_set_rate))
                    .setWidth(0.75f)
                    .setPositive(getString(R.string.to_set_up), view1 -> {
                        Intent intent = new Intent(pPresetActivity, PileSettingActivity.class);
                        intent.putExtra("chargingId", pPresetActivity.pChargingId);
                        intent.putParcelableArrayListExtra("rate", (ArrayList<? extends Parcelable>) pPresetActivity.pPriceConfBeanList);
                        pPresetActivity.jumpTo(intent, false);
                    })
                    .setNegative(getString(R.string.m7取消), view1 -> {

                    })
                    .setOnDismissListener(dialogInterface -> {

                    })
                    .show(getFragmentManager());


        } else {
            mTvMoneyType.setText(pPresetActivity.pSymbol);
        }


    }

    public void deleteReservationNow() {
        if (pPresetActivity.pReservationBean != null) {
            GunModel.getInstance().deleteReservationNow(pPresetActivity.pReservationBean, new GunModel.HttpCallBack() {
                @Override
                public void onSuccess(Object json) {
                    try {
                        JSONObject object = new JSONObject(json.toString());
                        int code = object.getInt("code");
                        if (code == 0) {
                            //pHandler.postDelayed(runnableGunInfo, 3000);
                            EventBus.getDefault().post(new DeletePreinstallEvent());
                            pPresetActivity.pReservationBean = null;
                            toast("删除成功:" + object.getString("data"));
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

    @OnClick({R.id.rl_start_time, R.id.btn_ok})
    public void onClickListener(View view) {
        switch (view.getId()) {
            case R.id.btn_ok:
                requestPreinstall();
                break;
            case R.id.rl_start_time:
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

        if (!ifCanChargeMoney(mEditMoney.getText().toString())) {
            toast(R.string.m输入金额不正确);
            return;
        }

        if (TextUtils.isEmpty(mEditMoney.getText().toString())) {
            toast(R.string.m210请输入金额);
            return;
        }

        if (TextUtils.isEmpty(mTvStartTime.getText())) {
            toast(getString(R.string.m130未设置开始时间));
            return;
        }


        double cValue = Double.parseDouble(mEditMoney.getText().toString());

        if (mTvStartTime.getText().equals(getString(R.string.start_immediately))) {

            GunModel.getInstance().requestCharging("G_SetAmount", cValue, pPresetActivity.pChargingId, pPresetActivity.pConnectorId, new GunModel.HttpCallBack() {
                @Override
                public void onSuccess(Object bean) {
                    try {
                        JSONObject object = new JSONObject(bean.toString());
                        int type = object.optInt("type");
                        if (type == 0) {
                            deleteReservationNow();
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
            String time = sdf.format(new Date()) + "T" + mTvStartTime.getText().toString() + ":00.000Z";

            GunModel.getInstance().requestReserve(time, "G_SetAmount", cValue, loop, pPresetActivity.pChargingId, pPresetActivity.pConnectorId, new GunModel.HttpCallBack() {
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

        }


    }

}
