package com.growatt.chargingpile.setting;

import static com.growatt.chargingpile.application.MyApplication.sIsVerified;

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
import com.growatt.chargingpile.util.Cons;
import com.growatt.chargingpile.util.Mydialog;
import com.growatt.chargingpile.util.SmartHomeUtil;
import com.growatt.chargingpile.view.ModifyDialog;
import com.growatt.chargingpile.view.PassWordDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 网络设置
 */
public class NetSettingActivity extends BaseActivity {

    private static String TAG = NetSettingActivity.class.getSimpleName();

    @BindView(R.id.headerView)
    LinearLayout mHeaderView;
    @BindView(R.id.tvTitle)
    TextView mTvTitle;
    @BindView(R.id.tv_wifi_name)
    TextView mTvWifiName;
    @BindView(R.id.tv_wifi_password)
    TextView mTvWifiPassWord;
    @BindView(R.id.tv_net_mode)
    TextView mTvNetMode;
    @BindView(R.id.tv_gateway)
    TextView mTvGateway;
    @BindView(R.id.tv_subnet_mask)
    TextView mTvSubnetMask;
    @BindView(R.id.tv_dns)
    TextView mTvDns;

    private SettingModel mSettingModel;
    private String mChargingId;
    private PileSetBean mPileSetBean;

    private List<String> mConfigKeys;
    private String mPassword;

    private String[] netModeArray = new String[]{"DHCP", "STATIC"};
    private List<String> mNetModeList = Arrays.asList(netModeArray);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_setting);
        ButterKnife.bind(this);
        initToolBar();
        mChargingId = getIntent().getStringExtra("chargingId");
        mSettingModel = new SettingModel();
        initData();
    }

    private void initData() {
        Mydialog.Show(this);
        mSettingModel.requestChargingParams(mChargingId, new GunModel.HttpCallBack() {
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
                            handleNetInfo(data);
                        }
                    } else {
                        String data = jsonObject.getString("data");
                        toast(data);
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

        if (Cons.getNoConfigBean() != null) {
            mConfigKeys = Cons.getNoConfigBean().getSfield();
            String configWord = Cons.getNoConfigBean().getConfigWord();
            mPassword = SmartHomeUtil.getDescodePassword(configWord);
        }
    }

    private void handleNetInfo(PileSetBean.DataBean data) {
        String wifiSSID = data.getG_WifiSSID();
        String wifiPassWord = data.getG_WifiPassword();
        if (TextUtils.isEmpty(wifiSSID)) {
            wifiSSID = "--";
        }
        if (TextUtils.isEmpty(wifiPassWord)) {
            wifiPassWord = "--";
        }
        mTvWifiName.setText(wifiSSID);
        mTvWifiPassWord.setText(wifiPassWord);

        mTvNetMode.setText(data.getG_NetworkMode());
        mTvGateway.setText(data.getGateway());
        mTvSubnetMask.setText(data.getMask());
        mTvDns.setText(data.getDns());
    }

    @OnClick({R.id.rl_wifi_name, R.id.rl_wifi_password, R.id.rl_net_mode, R.id.rl_gateway, R.id.rl_subnet_mask, R.id.rl_dns})
    public void onClickListener(View view) {
        switch (view.getId()) {
            case R.id.rl_wifi_name:
                if (checkKey("G_WifiSSID") && !sIsVerified) {
                    PassWordDialog.newInstance(str -> {
                        if (str.equals(mPassword)) {
                            sIsVerified = true;
                            requestModify("G_WifiSSID", mTvWifiName.getText().toString(), getString(R.string.m266Wifi名称));
                        } else {
                            toast(getString(R.string.m64原密码错误));
                        }
                    }).show(getSupportFragmentManager(), "G_WifiSSID");
                    return;
                }
                requestModify("G_WifiSSID", mTvWifiName.getText().toString(), getString(R.string.m266Wifi名称));
                break;
            case R.id.rl_wifi_password:
                if (checkKey("G_WifiPassword") && !sIsVerified) {
                    PassWordDialog.newInstance(str -> {
                        if (str.equals(mPassword)) {
                            sIsVerified = true;
                            requestModify("G_WifiPassword", mTvWifiPassWord.getText().toString(), getString(R.string.m267Wifi密码));
                        } else {
                            toast(getString(R.string.m64原密码错误));
                        }
                    }).show(getSupportFragmentManager(), "G_WifiPassword");
                    return;
                }
                requestModify("G_WifiPassword", mTvWifiPassWord.getText().toString(), getString(R.string.m267Wifi密码));
                break;
            case R.id.rl_gateway:
                if (checkKey("gateway") && !sIsVerified) {
                    PassWordDialog.newInstance(str -> {
                        if (str.equals(mPassword)) {
                            sIsVerified = true;
                            requestModify("gateway", mTvGateway.getText().toString(), getString(R.string.m157网关));
                        } else {
                            toast(getString(R.string.m64原密码错误));
                        }
                    }).show(getSupportFragmentManager(), "gateway");
                    return;
                }
                requestModify("gateway", mTvGateway.getText().toString(), getString(R.string.m157网关));
                break;
            case R.id.rl_subnet_mask:
                if (checkKey("mask") && !sIsVerified) {
                    PassWordDialog.newInstance(str -> {
                        if (str.equals(mPassword)) {
                            sIsVerified = true;
                            requestModify("mask", mTvSubnetMask.getText().toString(), getString(R.string.subnet_mask));
                        } else {
                            toast(getString(R.string.m64原密码错误));
                        }
                    }).show(getSupportFragmentManager(), "mask");
                    return;
                }
                requestModify("mask", mTvSubnetMask.getText().toString(), getString(R.string.subnet_mask));
                break;
            case R.id.rl_dns:
                if (checkKey("dns") && !sIsVerified) {
                    PassWordDialog.newInstance(str -> {
                        if (str.equals(mPassword)) {
                            sIsVerified = true;
                            requestModify("dns", mTvDns.getText().toString(), getString(R.string.m161DNS地址));
                        } else {
                            toast(getString(R.string.m64原密码错误));
                        }
                    }).show(getSupportFragmentManager(), "dns");
                } else {
                    requestModify("dns", mTvDns.getText().toString(), getString(R.string.m161DNS地址));
                }
                break;
            case R.id.rl_net_mode:
                requestNetMode();
                break;
            default:
        }
    }

    private void requestModify(String key, String value, String title) {
        ModifyDialog.newInstance(title, value, str -> {
            Mydialog.Show(this);
            if (!mTvWifiName.getText().equals(str)) {
                mSettingModel.requestEditChargingParams(mChargingId, key, str, new GunModel.HttpCallBack() {
                    @Override
                    public void onSuccess(Object json) {
                        Log.d(TAG, "onSuccess: " + json.toString());
                        Mydialog.Dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(json.toString());
                            int code = jsonObject.getInt("code");
                            if (code == 0 && mPileSetBean != null) {
                                if (key.equals("G_WifiSSID")) {
                                    mPileSetBean.getData().setG_WifiSSID(str);
                                } else if (key.equals("G_WifiPassword")) {
                                    mPileSetBean.getData().setG_WifiPassword(str);
                                } else if (key.equals("gateway")) {
                                    mPileSetBean.getData().setGateway(str);
                                } else if (key.equals("mask")) {
                                    mPileSetBean.getData().setMask(str);
                                } else if (key.equals("dns")) {
                                    mPileSetBean.getData().setDns(str);
                                }
                                handleNetInfo(mPileSetBean.getData());
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
        }).show(getSupportFragmentManager(), "");
    }

    private void requestNetMode() {

        OptionsPickerView<String> pvOptions = new OptionsPickerBuilder(this, (options1, options2, options3, v) -> {
            if (mTvNetMode.getText().equals(mNetModeList.get(options1))) {
                return;
            }
            Mydialog.Show(this);
            mSettingModel.requestEditChargingParams(mChargingId, "G_NetworkMode", mNetModeList.get(options1), new GunModel.HttpCallBack() {
                @Override
                public void onSuccess(Object json) {
                    Log.d(TAG, "onSuccess: " + json.toString());
                    Mydialog.Dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(json.toString());
                        int code = jsonObject.getInt("code");
                        if (code == 0) {
                            mPileSetBean.getData().setG_NetworkMode(mNetModeList.get(options1));
                            handleNetInfo(mPileSetBean.getData());
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
        })
                .setTitleText(getString(R.string.m网络模式设置))
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
        pvOptions.setPicker(mNetModeList);
        pvOptions.show();

    }

    @Override
    protected void initToolBar() {
        setHeaderImage(mHeaderView, R.drawable.back, Position.LEFT, v -> {
            finish();
        });
        mTvTitle.setTextColor(ContextCompat.getColor(this, R.color.black));
        mTvTitle.setText(getString(R.string.network_setting));
    }

    private boolean checkKey(String key) {
        for (int i = 0; i < mConfigKeys.size(); i++) {
            if (mConfigKeys.contains(key)) {
                return true;
            }
        }
        return false;
    }

}