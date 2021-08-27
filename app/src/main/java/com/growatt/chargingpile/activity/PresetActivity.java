package com.growatt.chargingpile.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.growatt.chargingpile.BaseActivity;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.constant.Constant;
import com.growatt.chargingpile.fragment.preset.ElectricFragment;
import com.growatt.chargingpile.fragment.preset.MoneyFragment;
import com.growatt.chargingpile.fragment.preset.TimeFragment;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preset);
        mFragmentManager = getSupportFragmentManager();
        ButterKnife.bind(this);
        initToolBar();
        initCheckRadioType();
    }

    private void initCheckRadioType() {
        mRbTime.setChecked(true);
    }

    @Override
    protected void initToolBar() {
        ivLeft.setImageResource(R.drawable.back);
        ivLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tvTitle.setText(R.string.m209预设);
        tvTitle.setTextColor(ContextCompat.getColor(this, R.color.black));
    }

    @OnCheckedChanged({R.id.rb_time, R.id.rb_money, R.id.rb_electric})
    public void onRadioCheck(CompoundButton view, boolean isChanged) {
        switch (view.getId()) {
            case R.id.rb_time:
                if (isChanged){
                    Log.d(TAG, "onRadioCheck: 1111111");
                gotoWorkFragment(Constant.PRESET_TIME_TYPE);
                }

                break;
            case R.id.rb_money:
                if (isChanged) {
                    Log.d(TAG, "onRadioCheck: 22222222");
                    gotoWorkFragment(Constant.PRESET_MONEY_TYPE);
                }
                break;
            case R.id.rb_electric:
                if (isChanged){
                    Log.d(TAG, "onRadioCheck: 333333333");
                    gotoWorkFragment(Constant.PRESET_ELECTRIC_TYPE);
                }
                break;
            default:
                break;
        }
    }

    public void gotoWorkFragment(int workType) {
        mTransaction = mFragmentManager.beginTransaction();
        if (workType == Constant.PRESET_TIME_TYPE) {
            if (mTimeFragment == null) {
                mTimeFragment = new TimeFragment();
                mTransaction.add(R.id.fl_content, mTimeFragment);
            }
            if (mMoneyFragment!=null){
                mTransaction.hide(mMoneyFragment);
            }
            if (mElectricFragment!=null){
                mTransaction.hide(mElectricFragment);
            }
            mTransaction.show(mTimeFragment);
        } else if (workType == Constant.PRESET_MONEY_TYPE) {
            if (mMoneyFragment == null) {
                mMoneyFragment = new MoneyFragment();
                mTransaction.add(R.id.fl_content, mMoneyFragment);
            }
            if (mTimeFragment!=null){
                mTransaction.hide(mTimeFragment);
            }
            if (mElectricFragment!=null){
                mTransaction.hide(mElectricFragment);
            }
            mTransaction.show(mMoneyFragment);
        } else if (workType == Constant.PRESET_ELECTRIC_TYPE) {
            if (mElectricFragment == null) {
                mElectricFragment = new ElectricFragment();
                mTransaction.add(R.id.fl_content, mElectricFragment);
            }

            if (mTimeFragment!=null){
                mTransaction.hide(mTimeFragment);
            }
            if (mMoneyFragment!=null){
                mTransaction.hide(mMoneyFragment);
            }
            mTransaction.show(mElectricFragment);
        }
        mTransaction.commitAllowingStateLoss();
    }
}