package com.growatt.chargingpile.fragment.preset;

import static com.growatt.chargingpile.util.T.toast;

import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.growatt.chargingpile.EventBusMsg.PreinstallEvent;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.fragment.BaseFragment;
import com.growatt.chargingpile.model.GunModel;
import com.growatt.chargingpile.view.TimeSetDialog;

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
 * Description:预设金额
 */

public class MoneyFragment extends BaseFragment {

    private static String TAG = MoneyFragment.class.getSimpleName();

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
                mSwitchEveryDay.setChecked(true);
            }
        }
        mTvMoneyType.setText(pPresetActivity.pSymbol);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @OnClick({R.id.rl_start_time, R.id.btn_ok})
    public void onClickListener(View view) {
        switch (view.getId()) {
            case R.id.btn_ok:
                requestPreinstall();
                break;
            case R.id.rl_start_time:
                TimeSetDialog.newInstance(getString(R.string.m204开始时间), mTvStartTime.getText().toString(), (hour, minute) -> {
                    Log.d(TAG, "confirm: hour:" + hour + "  minute:" + minute);
                    mTvStartTime.setText(hour + ":" + minute);
                }).show(pPresetActivity.getSupportFragmentManager(), "startDialog");
                break;
            default:
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void requestPreinstall() {
        int loop = mSwitchEveryDay.isChecked() ? 0 : -1;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String time = sdf.format(new Date()) + "T" + mTvStartTime.getText().toString() + ":00.000Z";

        double money = Double.parseDouble(mEditMoney.getText().toString());

        pModel.requestReserve(1, time, "G_SetAmount", money, loop, pPresetActivity.pChargingId, pPresetActivity.pConnectorId, new GunModel.HttpCallBack() {
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
