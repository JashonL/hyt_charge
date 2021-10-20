package com.growatt.chargingpile.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.growatt.chargingpile.BaseActivity;
import com.growatt.chargingpile.EventBusMsg.PreinstallEvent;
import com.growatt.chargingpile.EventBusMsg.UnitMsg;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.bean.ChargingBean;
import com.growatt.chargingpile.bean.ReservationBean;
import com.growatt.chargingpile.constant.Constant;
import com.growatt.chargingpile.fragment.preset.ElectricFragment;
import com.growatt.chargingpile.fragment.preset.MoneyFragment;
import com.growatt.chargingpile.fragment.preset.TimeFragment;
import com.growatt.chargingpile.model.GunModel;
import com.growatt.chargingpile.model.SettingModel;
import com.growatt.chargingpile.util.Mydialog;
import com.growatt.chargingpile.view.TimeSetDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;


/**
 * 预设
 */
public class PresetActivity extends BaseActivity {

    private static String TAG = PresetActivity.class.getSimpleName();

    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.ivLeft)
    ImageView ivLeft;
    @BindView(R.id.tv_start)
    TextView mTvStartTime;

    @BindView(R.id.rb_time)
    RadioButton mRbTime;
    @BindView(R.id.rb_money)
    RadioButton mRbMoney;
    @BindView(R.id.rb_electric)
    RadioButton mRbElectric;

    @BindView(R.id.rl_start)
    RelativeLayout mRL;
    @BindView(R.id.ll_every_day)
    LinearLayout mLLEveryDay;
    @BindView(R.id.btn_ok)
    Button mBtnOk;

    @BindView(R.id.sw_day)
    Switch mSwitchEveryDay;

    private FragmentManager mFragmentManager;
    private FragmentTransaction mTransaction = null;

    public TimeFragment mTimeFragment;
    public MoneyFragment mMoneyFragment;
    public ElectricFragment mElectricFragment;

    public String pChargingId;
    public int pConnectorId;

    public String pSymbol;

    public ReservationBean.DataBean pReservationBean;

    public List<ChargingBean.DataBean.PriceConfBean> pPriceConfBeanList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preset);
        mFragmentManager = getSupportFragmentManager();
        ButterKnife.bind(this);
        initToolBar();
        handleIntent();
        initCheckPresetType();
        SettingModel model = new SettingModel();
        model.requestConfigParams();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshSymbol(UnitMsg msg) {
        Log.d(TAG, "refreshSymbol: " + msg.getSymbol());
        if (msg.getSymbol() != null) {
            pSymbol = msg.getSymbol();
        }
    }

    private void handleIntent() {
        if (getIntent() != null) {
            pReservationBean = (ReservationBean.DataBean) getIntent().getSerializableExtra("reservation");
            if (pReservationBean != null) {
                pChargingId = pReservationBean.getChargeId();
                pConnectorId = pReservationBean.getConnectorId();
                pSymbol = pReservationBean.getSymbol();
            } else {
                pChargingId = getIntent().getStringExtra("chargingId");
                pConnectorId = getIntent().getIntExtra("connectorId", 0);
                pSymbol = getIntent().getStringExtra("symbol");
                pPriceConfBeanList = getIntent().getParcelableArrayListExtra("rate");
            }

        }
    }

    private void initCheckPresetType() {
        if (pReservationBean != null) {
            if (pReservationBean.getCKey().equals("G_SetTime")) {
                mRbTime.setChecked(true);
            } else if (pReservationBean.getCKey().equals("G_SetAmount")) {
                mRbMoney.setChecked(true);
            } else if (pReservationBean.getCKey().equals("G_SetEnergy")) {
                mRbElectric.setChecked(true);
            } else if (pReservationBean.getCKey().isEmpty()) {

                mRL.setVisibility(View.VISIBLE);
                mLLEveryDay.setVisibility(View.VISIBLE);
                mBtnOk.setVisibility(View.VISIBLE);

                String hour = pReservationBean.getExpiryDate().substring(11, 13);
                String minute = pReservationBean.getExpiryDate().substring(14, 16);
                mTvStartTime.setText(hour + ":" + minute);

                if (pReservationBean.getLoopType() == 0) {
                    mSwitchEveryDay.setChecked(true);
                }

            }
        } else {
            mRL.setVisibility(View.VISIBLE);
            mLLEveryDay.setVisibility(View.VISIBLE);
            mBtnOk.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void initToolBar() {
        ivLeft.setImageResource(R.drawable.back);
        ivLeft.setOnClickListener(view -> {
            finish();
        });
        tvTitle.setText(R.string.m209预设);
        tvTitle.setTextColor(ContextCompat.getColor(this, R.color.black));
    }

    @OnCheckedChanged({R.id.rb_time, R.id.rb_money, R.id.rb_electric})
    public void onRadioCheck(CompoundButton view, boolean isChanged) {
        if (mRL.getVisibility() == View.VISIBLE) {
            mRL.setVisibility(View.GONE);
            mLLEveryDay.setVisibility(View.GONE);
            mBtnOk.setVisibility(View.GONE);
        }
        if (isChanged) {
            switch (view.getId()) {
                case R.id.rb_time:
                    gotoWorkFragment(Constant.PRESET_TIME_TYPE);
                    break;
                case R.id.rb_money:
                    gotoWorkFragment(Constant.PRESET_MONEY_TYPE);
                    break;
                case R.id.rb_electric:
                    gotoWorkFragment(Constant.PRESET_ELECTRIC_TYPE);
                    break;
                default:
                    break;
            }
        }
    }

    @OnClick({R.id.btn_ok, R.id.rl_start})
    public void onClickListener(View view) {
        if (view.getId() == R.id.rl_start) {
            TimeSetDialog.newInstance(0, getString(R.string.m204开始时间), mTvStartTime.getText().toString(), (hour, minute) -> {
                mTvStartTime.setText(hour + ":" + minute);
            }).show(getSupportFragmentManager(), "StartDialog");
        } else if (view.getId() == R.id.btn_ok) {
            requestPreinstall();
        }
    }

    private void requestPreinstall() {
        if (mTvStartTime.getText().equals("00:00")) {
            toast(R.string.m请选择正确的时间段);
            return;
        }

        if (TextUtils.isEmpty(mTvStartTime.getText())) {
            toast(getString(R.string.m130未设置开始时间));
            return;
        }

        int loop = mSwitchEveryDay.isChecked() ? 0 : -1;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String time = sdf.format(new Date()) + "T" + mTvStartTime.getText() + ":00.000Z";
        Mydialog.Show(this);
        GunModel.getInstance().requestReserve(time, "", "", loop, pChargingId, pConnectorId, new GunModel.HttpCallBack() {
            @Override
            public void onSuccess(Object bean) {
                Mydialog.Dismiss();
                try {
                    JSONObject object = new JSONObject(bean.toString());
                    String data = object.getString("data");
                    int type = object.optInt("type");
                    if (type == 0) {
                        finish();
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

    public void gotoWorkFragment(int workType) {
        mTransaction = mFragmentManager.beginTransaction();
        if (workType == Constant.PRESET_TIME_TYPE) {
            if (mTimeFragment == null) {
                mTimeFragment = new TimeFragment();
                mTransaction.add(R.id.fl_content, mTimeFragment);
            }
            if (mMoneyFragment != null) {
                mTransaction.hide(mMoneyFragment);
            }
            if (mElectricFragment != null) {
                mTransaction.hide(mElectricFragment);
            }
            mTransaction.show(mTimeFragment);
        } else if (workType == Constant.PRESET_MONEY_TYPE) {
            if (mMoneyFragment == null) {
                mMoneyFragment = new MoneyFragment();
                mTransaction.add(R.id.fl_content, mMoneyFragment);
            }
            if (mTimeFragment != null) {
                mTransaction.hide(mTimeFragment);
            }
            if (mElectricFragment != null) {
                mTransaction.hide(mElectricFragment);
            }
            mTransaction.show(mMoneyFragment);
        } else if (workType == Constant.PRESET_ELECTRIC_TYPE) {
            if (mElectricFragment == null) {
                mElectricFragment = new ElectricFragment();
                mTransaction.add(R.id.fl_content, mElectricFragment);
            }

            if (mTimeFragment != null) {
                mTransaction.hide(mTimeFragment);
            }
            if (mMoneyFragment != null) {
                mTransaction.hide(mMoneyFragment);
            }
            mTransaction.show(mElectricFragment);
        }
        mTransaction.commitAllowingStateLoss();
    }
}