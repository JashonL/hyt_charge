package com.growatt.chargingpile.setting;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.growatt.chargingpile.BaseActivity;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.bean.PileSetBean;
import com.growatt.chargingpile.model.GunModel;
import com.growatt.chargingpile.model.SettingModel;
import com.growatt.chargingpile.util.Mydialog;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 负载平衡
 */
public class LoadBalancingActivity extends BaseActivity {

    private static String TAG = LoadBalancingActivity.class.getSimpleName();

    @BindView(R.id.headerView)
    LinearLayout mHeaderView;
    @BindView(R.id.tvTitle)
    TextView mTvTitle;
    @BindView(R.id.rl_max_kw)
    RelativeLayout mRlMaxLayout;
    @BindView(R.id.iv_switch)
    ImageView mIvSwitch;
    @BindView(R.id.edit_kw)
    EditText mEditKw;
    @BindView(R.id.btn_ok)
    Button mOk;

    private String mChargingId;
    private PileSetBean mPileSetBean;

    private boolean mIsCheckStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_balancing);
        ButterKnife.bind(this);
        initToolBar();
        mChargingId = getIntent().getStringExtra("chargingId");
        initData();

        mIvSwitch.setOnClickListener(v -> {
            if (mIsCheckStatus) {
                SettingModel.getInstance().requestEditChargingParams(mChargingId, "G_ExternalLimitPowerEnable", 0, new GunModel.HttpCallBack() {
                    @Override
                    public void onSuccess(Object json) {
                        try {
                            JSONObject object = new JSONObject(json.toString());
                            int code = object.getInt("code");
                            if (code == 0) {
                                mPileSetBean.getData().setG_ExternalLimitPowerEnable(0);
                                handleSwitchStatus(mPileSetBean.getData());
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
                SettingModel.getInstance().requestEditChargingParams(mChargingId, "G_ExternalLimitPowerEnable", 1, new GunModel.HttpCallBack() {
                    @Override
                    public void onSuccess(Object json) {
                        try {
                            JSONObject object = new JSONObject(json.toString());
                            int code = object.getInt("code");
                            if (code == 0) {
                                mPileSetBean.getData().setG_ExternalLimitPowerEnable(1);
                                handleSwitchStatus(mPileSetBean.getData());
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
        });

        mOk.setOnClickListener(v -> {

            if (TextUtils.isEmpty(mEditKw.getText())) {
                return;
            }
            SettingModel.getInstance().requestEditChargingParams(mChargingId, "G_ExternalLimitPower", mEditKw.getText().toString(), new GunModel.HttpCallBack() {
                @Override
                public void onSuccess(Object json) {
                    try {
                        JSONObject jsonObject = new JSONObject(json.toString());
//                        int code = jsonObject.getInt("code");
                        finish();
                        toast(jsonObject.getString("data"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailed() {

                }
            });
        });
    }

    private void initData() {
        Mydialog.Show(this);
        SettingModel.getInstance().requestChargingParams(mChargingId, new GunModel.HttpCallBack() {
            @Override
            public void onSuccess(Object json) {
                Mydialog.Dismiss();
                Log.d(TAG, "onSuccess: " + json.toString());
                try {
                    JSONObject jsonObject = new JSONObject(json.toString());
                    int code = jsonObject.getInt("code");
                    if (code == 0) {
                        mPileSetBean = new Gson().fromJson(json.toString(), PileSetBean.class);
                        if (mPileSetBean != null) {
                            PileSetBean.DataBean data = mPileSetBean.getData();
                            handleSwitchStatus(data);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailed() {
                Mydialog.Dismiss();
            }
        });

    }

    private void handleSwitchStatus(PileSetBean.DataBean data) {
        Log.d(TAG, "handleSwitch: getG_ExternalLimitPowerEnable:" + data.getG_ExternalLimitPowerEnable() + " getG_ExternalLimitPower:" + data.getG_ExternalLimitPower());
        if (data.getG_ExternalLimitPowerEnable() == 0) {
            mIvSwitch.setImageResource(R.drawable.ic_check_gray);
            mIsCheckStatus = false;
            mRlMaxLayout.setVisibility(View.GONE);
            mOk.setVisibility(View.GONE);
        } else {
            mIvSwitch.setImageResource(R.drawable.ic_check_green);
            mIsCheckStatus = true;
            mRlMaxLayout.setVisibility(View.VISIBLE);
            mEditKw.setText(data.getG_ExternalLimitPower());
            mOk.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void initToolBar() {
        setHeaderImage(mHeaderView, R.drawable.back, Position.LEFT, v -> {
            finish();
        });
        mTvTitle.setTextColor(ContextCompat.getColor(this, R.color.black));
        mTvTitle.setText(getString(R.string.load_balancing));
    }

}