package com.growatt.chargingpile.fragment.preset;

import static com.growatt.chargingpile.util.T.toast;

import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.growatt.chargingpile.EventBusMsg.PreinstallEvent;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.fragment.BaseFragment;
import com.growatt.chargingpile.model.GunModel;
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
 * Description:预设电量
 */

public class ElectricFragment extends BaseFragment {

    private static String TAG = ElectricFragment.class.getSimpleName();
    @BindView(R.id.ll_every_day)
    LinearLayout mLlEveryDay;
    @BindView(R.id.tv_start_time)
    TextView mTvStartTime;
    @BindView(R.id.edit_electric)
    EditText mEditElectric;
    @BindView(R.id.sw_day)
    Switch mSwitchEveryDay;

    @Override
    protected Object setRootView() {
        return R.layout.fragment_electric;
    }

    @Override
    protected void initWidget() {
        if (pPresetActivity.pReservationBean != null && pPresetActivity.pReservationBean.getCKey().equals("G_SetEnergy")) {
            mEditElectric.setText(pPresetActivity.pReservationBean.getCValue());
            mTvStartTime.setText(pPresetActivity.pReservationBean.getExpiryDate().substring(11, 16));

            if (pPresetActivity.pReservationBean.getLoopType() == 0) {
                mLlEveryDay.setVisibility(View.VISIBLE);
                mSwitchEveryDay.setChecked(true);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
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

        if (!ifCanChargeMoney(mEditElectric.getText().toString())) {
            toast(R.string.m输入电量不正确);
            return;
        }

        if (TextUtils.isEmpty(mEditElectric.getText().toString())) {
            toast(R.string.m211请输入电量);
            return;
        }

        if (TextUtils.isEmpty(mTvStartTime.getText())) {
            toast(getString(R.string.m130未设置开始时间));
            return;
        }


        double cValue = Double.parseDouble(mEditElectric.getText().toString());

        if (mTvStartTime.getText().equals(getString(R.string.start_immediately))) {

            GunModel.getInstance().requestCharging("G_SetEnergy", cValue, pPresetActivity.pChargingId, pPresetActivity.pConnectorId, new GunModel.HttpCallBack() {
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
            String time = sdf.format(new Date()) + "T" + mTvStartTime.getText().toString() + ":00.000Z";

            GunModel.getInstance().requestReserve(time, "G_SetEnergy", cValue, loop, pPresetActivity.pChargingId, pPresetActivity.pConnectorId, new GunModel.HttpCallBack() {
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
