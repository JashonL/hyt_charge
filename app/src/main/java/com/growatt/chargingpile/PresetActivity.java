package com.growatt.chargingpile;

import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.growatt.chargingpile.EventBusMsg.UnitMsg;
import com.growatt.chargingpile.bean.ChargingBean;
import com.growatt.chargingpile.bean.ReservationBean;
import com.growatt.chargingpile.constant.Constant;
import com.growatt.chargingpile.fragment.preset.ElectricFragment;
import com.growatt.chargingpile.fragment.preset.MoneyFragment;
import com.growatt.chargingpile.fragment.preset.TimeFragment;
import com.growatt.chargingpile.model.SettingModel;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;


/**
 * 预设
 */
public class PresetActivity extends BaseActivity {

    private static String TAG = PresetActivity.class.getSimpleName();

    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.ivLeft)
    ImageView ivLeft;
    @BindView(R.id.rb_time)
    RadioButton mRbTime;
    @BindView(R.id.rb_money)
    RadioButton mRbMoney;
    @BindView(R.id.rb_electric)
    RadioButton mRbElectric;

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
            }
        } else {
            mRbTime.setChecked(true);
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