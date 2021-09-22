package com.growatt.chargingpile.setting;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.google.gson.Gson;
import com.growatt.chargingpile.BaseActivity;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.bean.PileSetBean;
import com.growatt.chargingpile.model.GunModel;
import com.growatt.chargingpile.model.SettingModel;
import com.growatt.chargingpile.util.Mydialog;
import com.growatt.chargingpile.view.ModifyDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 基本信息
 */
public class BasicInfoActivity extends BaseActivity {

    private static String TAG = BasicInfoActivity.class.getSimpleName();

    @BindView(R.id.headerView)
    LinearLayout mHeaderView;
    @BindView(R.id.tvTitle)
    TextView mTvTitle;
    @BindView(R.id.tv_pile_id)
    TextView mTvId;
    @BindView(R.id.tv_pile_name)
    TextView mTvName;
    @BindView(R.id.tv_city)
    TextView mTvCity;
    @BindView(R.id.tv_belongs_plant)
    TextView mTvPlant;
    @BindView(R.id.tv_version)
    TextView mTvVersion;

    private String mChargingId;
    private PileSetBean mPileSetBean;

    private List<String> mCountryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_info);
        ButterKnife.bind(this);
        initToolBar();
        mChargingId = getIntent().getStringExtra("chargingId");
        initData();
    }

    @Override
    protected void initToolBar() {
        setHeaderImage(mHeaderView, R.drawable.back, Position.LEFT, v -> {
            finish();
        });
        mTvTitle.setTextColor(ContextCompat.getColor(this, R.color.black));
        mTvTitle.setText(getString(R.string.basic_information));
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
                            handleBasicInfo(data);
                        }
                        SettingModel.getInstance().requestCountry(new GunModel.HttpCallBack() {
                            @Override
                            public void onSuccess(Object bean) {
                                mCountryList.clear();
                                mCountryList = (List<String>) bean;
                                Log.d(TAG, "onSuccess: mCountryList:" + mCountryList.size());
                            }

                            @Override
                            public void onFailed() {

                            }
                        });
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

    private void handleBasicInfo(PileSetBean.DataBean data) {
        mTvId.setText(data.getChargeId());
        if (TextUtils.isEmpty(data.getName())) {
            mTvName.setText(data.getChargeId());
        } else {
            mTvName.setText(data.getName());
        }
        mTvCity.setText(data.getCountry());
        mTvPlant.setText(data.getSite());
        mTvVersion.setText(data.getVersion());
    }

    @OnClick({R.id.rl_pile_name, R.id.rl_city, R.id.rl_belongs_plant})
    public void onClickListener(View view) {
        switch (view.getId()) {
            case R.id.rl_pile_name:
                requestEditName();
                break;
            case R.id.rl_belongs_plant:
                requestEditPlant();
                break;
            case R.id.rl_city:
                OptionsPickerView<String> pvOptions = new OptionsPickerBuilder(this, (options1, options2, options3, v) -> {
                    if (mTvCity.getText().toString().equals(mCountryList.get(options1))) {
                        return;
                    }
                    requestEditCity(mCountryList.get(options1));
                })
                        .setTitleText(getString(R.string.m150国家城市))
                        .setSubmitText(getString(R.string.m9确定))
                        .setCancelText(getString(R.string.m7取消))
                        .setTitleBgColor(0xffffffff)
                        .setTitleColor(0xff333333)
                        .setSubmitColor(0xff333333)
                        .setCancelColor(0xff999999)
                        .setBgColor(0xffffffff)
                        .setTitleSize(14)
                        .setTextColorCenter(0xff333333)
                        .build();
                pvOptions.setPicker(mCountryList);
                pvOptions.show();
                break;
            default:
                break;
        }

    }

    private void requestEditCity(String city) {
        Log.d(TAG, "requestEditCity: " + city);
        Mydialog.Show(this);
        SettingModel.getInstance().requestEditChargingParams(mChargingId, "country", city, new GunModel.HttpCallBack() {
            @Override
            public void onSuccess(Object json) {
                Mydialog.Dismiss();
                Log.d(TAG, "onSuccess: " + json.toString());
                try {
                    JSONObject jsonObject = new JSONObject(json.toString());
                    int code = jsonObject.getInt("code");
                    if (code == 0) {
                        mPileSetBean.getData().setCountry(city);
                        handleBasicInfo(mPileSetBean.getData());
                    }
                    String data = jsonObject.getString("data");
                    toast(data);
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

    private void requestEditPlant() {
        ModifyDialog.newInstance(getString(R.string.belongs_to_plant), mTvPlant.getText().toString(), (str) -> {
            Log.d(TAG, "plant: " + str);
            if (mTvPlant.getText().toString().equals(str)) {
                return;
            }

            Mydialog.Show(this);
            SettingModel.getInstance().requestEditChargingParams(mChargingId, "site", str, new GunModel.HttpCallBack() {
                @Override
                public void onSuccess(Object json) {
                    Mydialog.Dismiss();
                    Log.d(TAG, "onSuccess: " + json.toString());
                    try {
                        JSONObject jsonObject = new JSONObject(json.toString());
                        int code = jsonObject.getInt("code");
                        if (code == 0) {
                            mPileSetBean.getData().setSite(str);
                            handleBasicInfo(mPileSetBean.getData());
                        }
                        String data = jsonObject.getString("data");
                        toast(data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailed() {
                    Mydialog.Dismiss();
                }
            });

        }).show(getSupportFragmentManager(), "plantDialog");
    }

    private void requestEditName() {
        ModifyDialog.newInstance(getString(R.string.charging_pile_alias), mTvName.getText().toString(), (str) -> {
            Log.d(TAG, "name: " + str);
            if (mTvName.getText().toString().equals(str)) {
                return;
            }
            Mydialog.Show(this);
            SettingModel.getInstance().requestEditChargingParams(mChargingId, "name", str, new GunModel.HttpCallBack() {
                @Override
                public void onSuccess(Object json) {
                    Mydialog.Dismiss();
                    Log.d(TAG, "onSuccess: " + json.toString());
                    try {
                        JSONObject jsonObject = new JSONObject(json.toString());
                        int code = jsonObject.getInt("code");
                        if (code == 0) {
                            mPileSetBean.getData().setName(str);
                            handleBasicInfo(mPileSetBean.getData());
                        }
                        String data = jsonObject.getString("data");
                        toast(data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailed() {
                    Mydialog.Dismiss();
                }
            });
        }).show(getSupportFragmentManager(), "nameDialog");
    }

}