package com.growatt.chargingpile.setting;

import static com.growatt.chargingpile.application.MyApplication.sIsVerified;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.growatt.chargingpile.BaseActivity;
import com.growatt.chargingpile.EventBusMsg.RefreshRateMsg;
import com.growatt.chargingpile.EventBusMsg.UnitMsg;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.activity.RateSetActivity;
import com.growatt.chargingpile.adapter.ChargingRatesAdapter;
import com.growatt.chargingpile.bean.ChargingBean;
import com.growatt.chargingpile.bean.PileSetBean;
import com.growatt.chargingpile.model.GunModel;
import com.growatt.chargingpile.model.SettingModel;
import com.growatt.chargingpile.util.Cons;
import com.growatt.chargingpile.util.MyUtil;
import com.growatt.chargingpile.util.Mydialog;
import com.growatt.chargingpile.util.SmartHomeUtil;
import com.growatt.chargingpile.view.ChargingAllowTimeDialog;
import com.growatt.chargingpile.view.DividerItemDecoration;
import com.growatt.chargingpile.view.ModifyDialog;
import com.growatt.chargingpile.view.PassWordDialog;
import com.mylhyl.circledialog.CircleDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 桩体设置
 */
public class PileSettingActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener {
    private static String TAG = PileSettingActivity.class.getSimpleName();
    @BindView(R.id.headerView)
    LinearLayout mHeaderView;
    @BindView(R.id.tvTitle)
    TextView mTvTitle;
    @BindView(R.id.tv_charge_mode)
    TextView mTvChargeMode;
    @BindView(R.id.tv_monetary_unit)
    TextView mTvMonetaryUnit;
    @BindView(R.id.tv_connection_mode)
    TextView mTvConnectionMode;
    @BindView(R.id.tv_eco_limit)
    TextView mTvEcoLimit;
    @BindView(R.id.tv_peak_valley)
    TextView mTvPeakValley;
    @BindView(R.id.tv_time)
    TextView mTvTime;
    @BindView(R.id.tv_meter_type)
    TextView mTvMeterType;
    @BindView(R.id.tv_electronic_lock)
    TextView mTvElectronicLock;
    @BindView(R.id.tv_lcd)
    TextView mTvLcd;
    @BindView(R.id.tv_solar_mode)
    TextView mTvSolarMode;

    @BindView(R.id.tv_low_power_charging)
    TextView mTvLowPowerCharging;

    @BindView(R.id.tv_max_current)
    TextView mTvMaxCurrent;

    @BindView(R.id.rv_charging_rates)
    RecyclerView mRecyclerView;

    private List<String> mDataList = new ArrayList<>();

    private String[] mModelArr = null;

    private ChargingRatesAdapter mChargingRatesAdapter;

    private SettingModel mSettingModel;
    private String mChargingId;
    private PileSetBean mPileSetBean;

    //获取货币单温集合
    private List<String> newUnitKeys;
    private List<String> newUnitValues;

    //货币单位
    private String[] moneyTypes;
    private String[] moneySymbols;

    private String unitKey;
    private String unitValue;

    private String[] wiringArray;
    private String[] solarArray;

    public String[] enableArray;

    public String[] mElectricTypeArray;
    public String[] unLockTypeArray;

    public String[] mLowPowerArray;


    private List<String> mConfigKeys;
    private String mPassword;

    private String unitSymbol;

    private List<ChargingBean.DataBean.PriceConfBean> priceConfBeanList;


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshAdapter(RefreshRateMsg msg) {
        Log.d(TAG, "refreshAdapter: ");
        if (msg.getPriceConfBeanList() != null) {
            priceConfBeanList = msg.getPriceConfBeanList();
            mChargingRatesAdapter.replaceData(priceConfBeanList);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pile_setting);
        ButterKnife.bind(this);
        initToolBar();
        mChargingId = getIntent().getStringExtra("chargingId");
        priceConfBeanList = getIntent().getParcelableArrayListExtra("rate");
        if (priceConfBeanList == null) priceConfBeanList = new ArrayList<>();
        mSettingModel = new SettingModel();
        initRecyclerView();
        initData();
        initMoneyUnit();
    }

    private void initMoneyUnit() {
        mSettingModel.requestMoneyUnit(new GunModel.HttpCallBack() {
            @Override
            public void onSuccess(Object bean) {
                JSONArray jsonArray = (JSONArray) bean;
                handleUnitMap(jsonArray);
            }

            @Override
            public void onFailed() {

            }
        });
    }

    /**
     * 货币单位列表初始化
     *
     * @param unitArray
     */
    private void handleUnitMap(@NonNull JSONArray unitArray) {
        List<String> unitKeys = new ArrayList<>();
        List<String> unitValues = new ArrayList<>();
        for (int i = 0; i < unitArray.length(); i++) {
            try {
                JSONObject jsonObject = unitArray.getJSONObject(i);
                unitKeys.add(jsonObject.optString("unit"));
                unitValues.add(jsonObject.optString("symbol"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        newUnitKeys = unitKeys;
        newUnitValues = unitValues;
    }

    private void initData() {
        Mydialog.Show(this);
        mModelArr = new String[]{getString(R.string.m217扫码刷卡), getString(R.string.m218仅刷卡充电), getString(R.string.m219插枪充电)};
        moneyTypes = new String[]{"pound", "dollar", "euro", "baht", "rmb"};
        moneySymbols = new String[]{"£", "$", "€", "฿", "￥"};
        wiringArray = new String[]{"CT2000", getString(R.string.m电表), "CT3000"};
        solarArray = new String[]{"FAST", "ECO", "ECO+"};
        enableArray = new String[]{getString(R.string.m300禁止), getString(R.string.m299使能)};
        mElectricTypeArray = new String[]{getString(R.string.m安科瑞), getString(R.string.m东宏), "Acrel DDS1352",
                "Acrel DTSD1352(Three)", "Eastron SDM230", "Eastron SDM630(Three)", "Eastron SDM120 MID", "Eastron SDM72D MID(Three)", "Din-Rail DTSU666 MID(Three)"};

        unLockTypeArray = new String[]{getString(R.string.m手动), getString(R.string.m自动)};

        mLowPowerArray = new String[]{"Enable", "Disable"};

        unitKey = moneyTypes[0];
        unitValue = moneySymbols[0];

        if (Cons.getNoConfigBean() != null) {
            mConfigKeys = Cons.getNoConfigBean().getSfield();
            String configWord = Cons.getNoConfigBean().getConfigWord();
            mPassword = SmartHomeUtil.getDescodePassword(configWord);
        }

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
                            handlePileInfo(data);
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
    }

    private void handlePileInfo(PileSetBean.DataBean data) {
        int mode = Integer.parseInt(data.getG_ChargerMode());
        String strMode = null;
        if (mode == 1) {
            strMode = mModelArr[0];
        } else if (mode == 2) {
            strMode = mModelArr[1];
        } else if (mode == 3) {
            strMode = mModelArr[2];
        }
        mTvChargeMode.setText(strMode);

        String symbol = data.getSymbol();
        if (TextUtils.isEmpty(symbol)) {
            symbol = "--";
        }
        mTvMonetaryUnit.setText(symbol);

        int curWring = data.getG_ExternalSamplingCurWring();
        String strWring = null;
        if (curWring == 0) {
            strWring = wiringArray[0];
        } else if (curWring == 1) {
            strWring = wiringArray[1];
        } else if (curWring == 2) {
            strWring = wiringArray[2];
        }
        mTvConnectionMode.setText(strWring);

        mTvEcoLimit.setText(String.valueOf(data.getG_SolarLimitPower()));

        int peakValley = data.getG_PeakValleyEnable();
        String peak;
        if (peakValley == 0) {
            peak = enableArray[0];
        } else {
            peak = enableArray[1];
        }
        mTvPeakValley.setText(peak);

        String time = data.getG_AutoChargeTime();
        if (TextUtils.isEmpty(time)) {
            time = "--";
        }
        mTvTime.setText(time);

        String powerMeterType = data.getG_PowerMeterType();
        if (TextUtils.isEmpty(powerMeterType)) {
            powerMeterType = "--";
        }
        mTvMeterType.setText(powerMeterType);

        String unlockConnector = data.getUnlockConnectorOnEVSideDisconnect();

        if (TextUtils.isEmpty(unlockConnector)) {
            unlockConnector = "--";
        } else if (unlockConnector.equals("true")) {
            unlockConnector = unLockTypeArray[1];
        } else if (unlockConnector.equals("false")) {
            unlockConnector = unLockTypeArray[0];
        }
        mTvElectronicLock.setText(unlockConnector);

        mTvLowPowerCharging.setText(data.getG_LowPowerReserveEnable());

        mTvMaxCurrent.setText(data.getG_MaxCurrent());

        mTvLcd.setText(data.getG_LCDCloseEnable());

        int solarMode = data.getG_SolarMode();
        String solar = null;
        if (solarMode == 0) {
            solar = solarArray[0];
        } else if (solarMode == 1) {
            solar = solarArray[1];
        } else if (solarMode == 2) {
            solar = solarArray[2];
        }
        mTvSolarMode.setText(solar);

        unitSymbol = data.getSymbol();
        mChargingRatesAdapter.setSymbol(unitSymbol);

    }

    private void initRecyclerView() {
        mDataList.add(getString(R.string.m152充电费率));
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mChargingRatesAdapter = new ChargingRatesAdapter(priceConfBeanList, this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mChargingRatesAdapter);
        int dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen.xa2);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST, false, R.color.translate, dimensionPixelSize);
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mChargingRatesAdapter.setOnItemClickListener(this);
    }

    @Override
    protected void initToolBar() {
        setHeaderImage(mHeaderView, R.drawable.back, Position.LEFT, v -> {
            finish();
        });
        mTvTitle.setTextColor(ContextCompat.getColor(this, R.color.black));
        mTvTitle.setText(getString(R.string.pile_setting));
    }

    private void setRate() {
        Intent intent = new Intent(this, RateSetActivity.class);
        intent.putExtra("sn", mChargingId);
        intent.putExtra("symbol", unitSymbol);
        intent.putParcelableArrayListExtra("rate", (ArrayList<? extends Parcelable>) priceConfBeanList);
        jumpTo(intent, false);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        setRate();
    }

    @OnClick({R.id.rl_charge_mode, R.id.rl_monetary_unit, R.id.rl_connection_mode, R.id.rl_lcd, R.id.rl_meter_type, R.id.rl_low_power_charging,
            R.id.rl_solar_mode, R.id.rl_eco_limit, R.id.rl_peak_valley, R.id.rl_time, R.id.rl_electronic_lock, R.id.rl_max_current})
    public void onClickListener(View view) {
        switch (view.getId()) {
            case R.id.rl_charge_mode:
                showChargeModeDialog();
                break;
            case R.id.rl_monetary_unit:
                showCapacityUnitDialog();
                break;
            case R.id.rl_connection_mode:
                checkSamplingCurWring();
                break;
            case R.id.rl_solar_mode:
                checkSolarMode();
                break;
            case R.id.rl_eco_limit:
                checkSolarLimitPower();
                break;
            case R.id.rl_peak_valley:
                checkPeakValley();
                break;
            case R.id.rl_time:
                ChargingAllowTimeDialog.newInstance(mTvTime.getText().toString(), str -> {
                    if (mTvTime.equals(str)) {
                        return;
                    }
                    requestModify("G_AutoChargeTime", str);
                }).show(getSupportFragmentManager(), "");
                break;
            case R.id.rl_meter_type:
                checkMeterType();
                break;
            case R.id.rl_electronic_lock:
                checkElectronicLock();
                break;
            case R.id.rl_low_power_charging:
                checkLowPowerCharging();
                break;
            case R.id.rl_max_current:
                checkMaxCurrent();
                break;
            case R.id.rl_lcd:
                checkLcd();
                break;
            default:
                break;
        }
    }

    private void checkLcd() {
        if (checkKey("G_LCDCloseEnable") && !sIsVerified) {
            PassWordDialog.newInstance(str -> {
                if (str.equals(mPassword)) {
                    sIsVerified = true;
                    showLcdDialog();
                } else {
                    toast(getString(R.string.m64原密码错误));
                }
            }).show(getSupportFragmentManager(), "G_LCDCloseEnable");
            return;
        }
        showLcdDialog();
    }

    private void showLcdDialog() {
        List<String> list = Arrays.asList(mLowPowerArray);
        OptionsPickerView<String> pvOptions = new OptionsPickerBuilder(this, (options1, options2, options3, v) -> {

            requestModify("G_LCDCloseEnable", list.get(options1));

        })
                .setTitleText("LCD")
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
        pvOptions.setPicker(list);
        pvOptions.show();
    }

    private void checkMaxCurrent() {
        if (checkKey("G_MaxCurrent") && !sIsVerified) {
            PassWordDialog.newInstance(str -> {
                if (str.equals(mPassword)) {
                    sIsVerified = true;
                    showMaxCurrentDialog();
                } else {
                    toast(getString(R.string.m64原密码错误));
                }
            }).show(getSupportFragmentManager(), "G_MaxCurrent");
            return;
        }
        showMaxCurrentDialog();
    }

    private void showMaxCurrentDialog() {
        ModifyDialog.newInstance(getString(R.string.m277电桩最大输出电流), mTvMaxCurrent.getText().toString(), str -> {
            if (str.equals(mTvMaxCurrent.getText().toString())) {
                return;
            }
            requestModify("G_MaxCurrent", str);
        }).show(getSupportFragmentManager(), "G_MaxCurrent");

    }

    private void checkLowPowerCharging() {
        if (checkKey("G_LowPowerReserveEnable") && !sIsVerified) {
            PassWordDialog.newInstance(str -> {
                if (str.equals(mPassword)) {
                    sIsVerified = true;
                    showLowPowerChargingDialog();
                } else {
                    toast(getString(R.string.m64原密码错误));
                }
            }).show(getSupportFragmentManager(), "G_LowPowerReserveEnable");
            return;
        }
        showLowPowerChargingDialog();
    }

    private void showLowPowerChargingDialog() {
        List<String> list = Arrays.asList(mLowPowerArray);
        OptionsPickerView<String> pvOptions = new OptionsPickerBuilder(this, (options1, options2, options3, v) ->

                requestModify("G_LowPowerReserveEnable", list.get(options1)))

                .setTitleText(getString(R.string.m低功率预约充电))
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
        pvOptions.setPicker(list);
        pvOptions.show();
    }

    private void checkElectronicLock() {
        if (checkKey("UnlockConnectorOnEVSideDisconnect") && !sIsVerified) {
            PassWordDialog.newInstance(str -> {
                if (str.equals(mPassword)) {
                    sIsVerified = true;
                    showElectronicLockDialog();
                } else {
                    toast(getString(R.string.m64原密码错误));
                }
            }).show(getSupportFragmentManager(), "UnlockConnectorOnEVSideDisconnect");
            return;
        }
        showElectronicLockDialog();
    }

    private void showElectronicLockDialog() {
        List<String> list = Arrays.asList(unLockTypeArray);
        OptionsPickerView<String> pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                String value = "true";
                switch (options1) {
                    case 0:
                        value = "false";
                        break;
                    case 1:
                        value = "true";
                        break;
                }
                requestModify("UnlockConnectorOnEVSideDisconnect", value);
            }
        })
                .setTitleText(getString(R.string.m电子锁配置))
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
        pvOptions.setPicker(list);
        pvOptions.show();
    }

    private void checkMeterType() {
        if (checkKey("G_PowerMeterType") && !sIsVerified) {
            PassWordDialog.newInstance(str -> {
                if (str.equals(mPassword)) {
                    sIsVerified = true;
                    showMeterTypeDialog();
                } else {
                    toast(getString(R.string.m64原密码错误));
                }
            }).show(getSupportFragmentManager(), "G_PowerMeterType");
            return;
        }
        showMeterTypeDialog();
    }

    private void showMeterTypeDialog() {
        List<String> list = Arrays.asList(mElectricTypeArray);
        OptionsPickerView<String> pvOptions = new OptionsPickerBuilder(this, (options1, options2, options3, v) ->

                requestModify("G_PowerMeterType", list.get(options1)))

                .setTitleText(getString(R.string.m电表类型))
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
        pvOptions.setPicker(list);
        pvOptions.show();
    }

    private void checkPeakValley() {
        if (checkKey("G_PeakValleyEnable") && !sIsVerified) {
            PassWordDialog.newInstance(str -> {
                if (str.equals(mPassword)) {
                    sIsVerified = true;
                    showPeakValleyDialog();
                } else {
                    toast(getString(R.string.m64原密码错误));
                }
            }).show(getSupportFragmentManager(), "G_PeakValleyEnable");
            return;
        }
        showPeakValleyDialog();
    }

    private void showPeakValleyDialog() {
        List<String> list = Arrays.asList(enableArray);
        OptionsPickerView<String> pvOptions = new OptionsPickerBuilder(this, (options1, options2, options3, v) ->

                requestModify("G_PeakValleyEnable", options1))

                .setTitleText(getString(R.string.m297峰谷充电使能))
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
        pvOptions.setPicker(list);
        pvOptions.show();
    }

    private void checkSamplingCurWring() {
        if (checkKey("G_ExternalSamplingCurWring") && !sIsVerified) {
            PassWordDialog.newInstance(str -> {
                if (str.equals(mPassword)) {
                    sIsVerified = true;
                    showWiringDialog();
                } else {
                    toast(getString(R.string.m64原密码错误));
                }
            }).show(getSupportFragmentManager(), "G_ExternalSamplingCurWring");
            return;
        }
        showWiringDialog();
    }

    private void checkSolarLimitPower() {
        if (!mTvEcoLimit.equals("ECO")) {
            toast(R.string.m只有ECO模式有效);
            return;
        }
        if (checkKey("G_SolarLimitPower") && !sIsVerified) {
            PassWordDialog.newInstance(str -> {
                if (str.equals(mPassword)) {
                    sIsVerified = true;
                    showECOLimitDialog();
                } else {
                    toast(getString(R.string.m64原密码错误));
                }
            }).show(getSupportFragmentManager(), "G_SolarLimitPower");
            return;
        }
        showECOLimitDialog();
    }

    private void checkSolarMode() {
        if (checkKey("G_SolarMode") && !sIsVerified) {
            PassWordDialog.newInstance(str -> {
                if (str.equals(mPassword)) {
                    sIsVerified = true;
                    showSolarModeDialog();
                } else {
                    toast(getString(R.string.m64原密码错误));
                }
            }).show(getSupportFragmentManager(), "G_SolarMode");
            return;
        }
        showSolarModeDialog();
    }

    private void showECOLimitDialog() {
        ModifyDialog.newInstance(getString(R.string.eco_current_limit_kw), mTvEcoLimit.getText().toString(), str -> {
            if (TextUtils.isEmpty(str)) {
                toast(R.string.m140不能为空);
                return;
            }
            boolean numeric_eco = MyUtil.isNumeric(str);
            if (!numeric_eco) {
                toast(R.string.m177输入格式不正确);
                return;
            }
            float v1;
            try {
                v1 = Float.parseFloat(str);
            } catch (NumberFormatException e) {
                v1 = 0;
            }
            requestModify("G_SolarLimitPower", v1);
        }).show(getSupportFragmentManager(), "G_SolarLimitPower");
    }

    private void showSolarModeDialog() {
        List<String> list = Arrays.asList(solarArray);
        OptionsPickerView<String> pvOptions = new OptionsPickerBuilder(this, (options1, options2, options3, v) -> {
            requestModify("G_SolarMode", options1);
        })
                .setTitleText(getString(R.string.mSolar模式))
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
        pvOptions.setPicker(list);
        pvOptions.show();
    }

    private void showWiringDialog() {
        List<String> list = Arrays.asList(wiringArray);
        OptionsPickerView<String> pvOptions = new OptionsPickerBuilder(this, (options1, options2, options3, v) -> {
            requestModify("G_ExternalSamplingCurWring", options1);
        })
                .setTitleText(getString(R.string.m外部电流采样接线方式))
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
        pvOptions.setPicker(list);
        pvOptions.show();
    }

    private void requestModify(String key, Object value) {
        Mydialog.Show(this);
        mSettingModel.requestEditChargingParams(mChargingId, key, value, new GunModel.HttpCallBack() {
            @Override
            public void onSuccess(Object json) {
                Log.d(TAG, "onSuccess: " + json.toString());
                Mydialog.Dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(json.toString());
                    int code = jsonObject.getInt("code");
                    if (code == 0 && mPileSetBean != null) {
                        if (key.equals("G_ChargerMode")) {
                            mPileSetBean.getData().setG_ChargerMode(String.valueOf(value));
                        } else if (key.equals("G_ExternalSamplingCurWring")) {
                            mPileSetBean.getData().setG_ExternalSamplingCurWring((Integer) value);
                        } else if (key.equals("G_SolarMode")) {
                            mPileSetBean.getData().setG_SolarMode((Integer) value);
                        } else if (key.equals("G_SolarLimitPower")) {
                            mPileSetBean.getData().setG_SolarLimitPower((Float) value);
                        } else if (key.equals("G_PeakValleyEnable")) {
                            mPileSetBean.getData().setG_PeakValleyEnable((Integer) value);
                        } else if (key.equals("G_AutoChargeTime")) {
                            mPileSetBean.getData().setG_AutoChargeTime(String.valueOf(value));
                        } else if (key.equals("G_PowerMeterType")) {
                            mPileSetBean.getData().setG_PowerMeterType(String.valueOf(value));
                        } else if (key.equals("UnlockConnectorOnEVSideDisconnect")) {
                            mPileSetBean.getData().setUnlockConnectorOnEVSideDisconnect(String.valueOf(value));
                        } else if (key.equals("G_LowPowerReserveEnable")) {
                            mPileSetBean.getData().setG_LowPowerReserveEnable(String.valueOf(value));
                        } else if (key.equals("G_MaxCurrent")) {
                            mPileSetBean.getData().setG_MaxCurrent(String.valueOf(value));
                        } else if (key.equals("G_LCDCloseEnable")) {
                            mPileSetBean.getData().setG_LCDCloseEnable(String.valueOf(value));
                        }
                        handlePileInfo(mPileSetBean.getData());
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

    private void showChargeModeDialog() {
        new CircleDialog.Builder()
                .setTitle(getString(R.string.m154充电模式))
                .setItems(mModelArr, (parent, view1, position, id) -> {
                    int model = 4;
                    switch (position) {
                        case 0:
                            model = 1;
                            break;
                        case 1:
                            model = 2;
                            break;
                        case 2:
                            model = 3;
                            break;
                    }

                    if (mTvChargeMode.getText().equals(mModelArr[position])) {
                        return true;
                    }
                    requestModify("G_ChargerMode", String.valueOf(model));
                    return true;
                })
                .setNegative(getString(R.string.m7取消), null)
                .show(getSupportFragmentManager());
    }

    private void showCapacityUnitDialog() {
        List<String> keys;
        List<String> values;
        List<String> items = new ArrayList<>();
        if (newUnitKeys == null) {
            keys = Arrays.asList(moneyTypes);
            values = Arrays.asList(moneySymbols);
        } else {
            keys = newUnitKeys;
            values = newUnitValues;
        }
        for (int i = 0; i < keys.size(); i++) {
            String s = String.format("%1$s(%2$s)", keys.get(i), values.get(i));
            items.add(s);
        }
        new CircleDialog.Builder()
                .setTitle(getString(R.string.m303选择货币))
                .setWidth(0.7f)
                .setMaxHeight(0.5f)
                .setGravity(Gravity.CENTER)
                .setItems(items, (parent, view, position, id) -> {
                    try {
                        unitKey = keys.get(position);
                        unitValue = values.get(position);
                        unitSymbol = unitValue;
                        if (mTvMonetaryUnit.equals(unitValue)) {
                            return true;
                        }
                        requestUnit(unitKey, unitValue);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                })

                .setNegative(getString(R.string.m7取消), null)
                .show(getSupportFragmentManager());
    }

    private void requestUnit(Object value, String unitSymbol) {
        Mydialog.Show(this);
        mSettingModel.requestEditChargingUnit(mChargingId, value, unitSymbol, new GunModel.HttpCallBack() {
            @Override
            public void onSuccess(Object json) {
                Log.d(TAG, "onSuccess: " + json.toString());
                Mydialog.Dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(json.toString());
                    int code = jsonObject.getInt("code");
                    if (code == 0 && mPileSetBean != null) {
                        mPileSetBean.getData().setSymbol(unitSymbol);
                        handlePileInfo(mPileSetBean.getData());
                        EventBus.getDefault().post(new UnitMsg(unitSymbol));
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

    private boolean checkKey(String key) {
        for (int i = 0; i < mConfigKeys.size(); i++) {
            if (mConfigKeys.contains(key)) {
                return true;
            }
        }
        return false;
    }

}