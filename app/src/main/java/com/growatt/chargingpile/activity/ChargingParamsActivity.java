package com.growatt.chargingpile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.google.gson.Gson;
import com.growatt.chargingpile.BaseActivity;
import com.growatt.chargingpile.EventBusMsg.FreshListMsg;
import com.growatt.chargingpile.EventBusMsg.RefreshRateMsg;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.adapter.ParamsSetAdapter;
import com.growatt.chargingpile.bean.ChargingBean;
import com.growatt.chargingpile.bean.ParamsSetBean;
import com.growatt.chargingpile.bean.PileSetBean;
import com.growatt.chargingpile.bean.SolarBean;
import com.growatt.chargingpile.connutil.PostUtil;
import com.growatt.chargingpile.util.Cons;
import com.growatt.chargingpile.util.MyUtil;
import com.growatt.chargingpile.util.Mydialog;
import com.growatt.chargingpile.util.SmartHomeUrlUtil;
import com.growatt.chargingpile.util.SmartHomeUtil;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.view.listener.OnInputClickListener;
import com.mylhyl.circledialog.view.listener.OnLvItemClickListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 参数设置
 */
public class ChargingParamsActivity extends BaseActivity {
    @BindView(R.id.headerView)
    View headerView;
    @BindView(R.id.ivLeft)
    ImageView ivLeft;
    @BindView(R.id.tvRight)
    TextView tvRight;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.srl_pull)
    SwipeRefreshLayout srlPull;

    private TextView tvId;
    private TextView tvKeys;
    private TextView tvVersion;


    private ParamsSetAdapter mAdapter;
    private List<MultiItemEntity> list = new ArrayList<>();
    private String[] keys;
    private String[] keySfields;
    private List<String> noConfigKeys;

    private String[] mModels;
    private boolean isModyfi = false;
    private String chargingId;

    //货币单位
    private String[] moneyTypes;
    private String[] moneySymbols;
    //获取货币单温集合
    private List<String> newUnitKeys;
    private List<String> newUnitValues;
    private String unitKey;
    private String unitValue;
    private String unitSymbol;

    private List<ChargingBean.DataBean.PriceConfBean> priceConfBeanList;

    public String[] enableArray;
    public String[] wiringArray;
    public String[] solarArrray;

    public String[] ammterTypeArray;
    public String[] unLockTypeArray;
    public String[] netModeArray;
    public String[] LowPowerArray;

    private PileSetBean initPileSetBean;
    private Map<String, Object> setPileParamMap;
    private String endTime;
    private String startTime;
    private boolean isEditInfo = false;
    private boolean isVerified = false;//是否已验证密码
    private String password;
    private int solar;
    private List<String> countryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charging_params);
        ButterKnife.bind(this);
        initIntent();
        initHeaderView();
        initResource();
        initRecyclerView();
        refreshDate();
        setOnclickListener();
        initPullView();
        getMoneyUnit();
    }

    private void initIntent() {
        chargingId = getIntent().getStringExtra("sn");
        priceConfBeanList = getIntent().getParcelableArrayListExtra("rate");
        if (priceConfBeanList == null) priceConfBeanList = new ArrayList<>();
    }

    private void initHeaderView() {
        ivLeft.setImageResource(R.drawable.back);
        tvTitle.setText(getString(R.string.m141参数设置));
        tvTitle.setTextColor(ContextCompat.getColor(this, R.color.title_1));
        tvRight.setText(R.string.m182保存);
        tvRight.setTextColor(ContextCompat.getColor(this, R.color.title_1));
        tvRight.setVisibility(View.INVISIBLE);
    }


    private void initPullView() {
        srlPull.setColorSchemeColors(ContextCompat.getColor(this, R.color.maincolor_1));
        srlPull.setOnRefreshListener(() -> {
            isModyfi = false;
            refreshDate();
        });
    }


    private void initRecyclerView() {
        View paramHeadView = LayoutInflater.from(this).inflate(R.layout.item_params_header_view, null);
        tvId = paramHeadView.findViewById(R.id.tv_id);
        tvKeys = paramHeadView.findViewById(R.id.tv_keys);
        tvVersion = paramHeadView.findViewById(R.id.tv_version);
        mAdapter = new ParamsSetAdapter(list);
        mAdapter.addHeaderView(paramHeadView);
        recyclerView.setAdapter(mAdapter);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        mAdapter.expandAll();
    }


    private void setOnclickListener() {
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            MultiItemEntity multiItemEntity = mAdapter.getData().get(position);
            if (multiItemEntity == null) return;
            int itemType = multiItemEntity.getItemType();
            if (itemType == ParamsSetAdapter.PARAM_ITEM) {
                ParamsSetBean bean = (ParamsSetBean) mAdapter.getData().get(position);
                if (!bean.isAuthority() && !isVerified) {//如果是不允许设置，又没有验证密码
                    showInputPassword(ParamsSetAdapter.PARAM_ITEM, bean);
                } else {
                    setCommonParams(bean);
                }

            } else if (itemType == ParamsSetAdapter.PARAM_ITEM_RATE) {//设置费率
                ParamsSetBean bean = (ParamsSetBean) mAdapter.getData().get(4);
                if (!bean.isAuthority() && !isVerified) {//如果是不允许设置，又没有验证密码
                    showInputPassword(ParamsSetAdapter.PARAM_ITEM_RATE, bean);
                } else {
                    setRate();
                }
            } else if (itemType == ParamsSetAdapter.PARAM_ITEM_SOLAR) {
                ParamsSetBean bean = (ParamsSetBean) mAdapter.getData().get(19);
                if (solar != 1) {
                    toast(R.string.m只有ECO模式有效);
                    return;
                }
                if (!bean.isAuthority() && !isVerified) {//如果是不允许设置，又没有验证密码
                    showInputPassword(ParamsSetAdapter.PARAM_ITEM_SOLAR, bean);
                } else {
                    setECOLimit();
                }
            }
        });
    }


    private void initResource() {
        mModels = new String[]{getString(R.string.m217扫码刷卡), getString(R.string.m218仅刷卡充电), getString(R.string.m219插枪充电)};
        keys = new String[]{
                getString(R.string.m148基础参数), getString(R.string.m149电桩名称), getString(R.string.m150国家城市),
                getString(R.string.m151站点), getString(R.string.m152充电费率), getString(R.string.m315货币单位),
                getString(R.string.m313电桩最大输出电流), getString(R.string.m314智能功率分配), getString(R.string.m154充电模式),
                getString(R.string.m155高级设置), getString(R.string.m网络模式设置), getString(R.string.m156充电桩IP), getString(R.string.m157网关), getString(R.string.m158子网掩码),
                getString(R.string.m159网络MAC地址), getString(R.string.m160服务器URL), getString(R.string.m161DNS地址), "",
                getString(R.string.m298功率分配使能), getString(R.string.m外部电流采样接线方式), getString(R.string.mSolar模式), getString(R.string.m297峰谷充电使能), getString(R.string.m280允许充电时间), getString(R.string.m电表类型), getString(R.string.m电子锁配置),
                getString(R.string.m低功率预约充电), "LCD", "", getString(R.string.m289进入AP模式), ""};
        keySfields = new String[]{"", "name", "country",
                "site", "rate", "unit",
                "G_MaxCurrent", "G_ExternalLimitPower", "G_ChargerMode",
                "", "G_NetworkMode", "ip", "gateway", "mask",
                "mac", "host", "dns", "",
                "G_ExternalLimitPowerEnable", "G_ExternalSamplingCurWring", "G_SolarMode", "G_PeakValleyEnable",
                "G_AutoChargeTime", "G_PowerMeterType",
                "UnlockConnectorOnEVSideDisconnect", "G_LowPowerReserveEnable", "G_LCDCloseEnable",
                "", "apMode", ""};
        if (Cons.getNoConfigBean() != null) {
            noConfigKeys = Cons.getNoConfigBean().getSfield();
            String configWord = Cons.getNoConfigBean().getConfigWord();
            password = SmartHomeUtil.getDescodePassword(configWord);
        }
        if (noConfigKeys == null) noConfigKeys = new ArrayList<>();

        for (int i = 0; i < keySfields.length; i++) {
            ParamsSetBean bean = new ParamsSetBean();
            bean.setIndex(i);
            String keySfield = keySfields[i];
            if ("".equals(keySfield)) {
                bean.setTitle(keys[i]);
                bean.setType(ParamsSetAdapter.PARAM_TITILE);
            } else {
                bean.setType(ParamsSetAdapter.PARAM_ITEM);
                bean.setKey(keys[i]);
                bean.setValue("");
            }

            bean.setSfield(keySfields[i]);
            if (noConfigKeys.contains(bean.getSfield())) {
                bean.setAuthority(false);
            } else {
                bean.setAuthority(true);
            }
            list.add(bean);
        }

        moneyTypes = new String[]{"pound", "dollar", "euro", "baht", "rmb"};
        moneySymbols = new String[]{"£", "$", "€", "฿", "￥"};
        enableArray = new String[]{getString(R.string.m300禁止), getString(R.string.m299使能)};
        wiringArray = new String[]{"CT2000", getString(R.string.m电表), "CT3000"};
        solarArrray = new String[]{"FAST", "ECO", "ECO+"};
        ammterTypeArray = new String[]{getString(R.string.m安科瑞), getString(R.string.m东宏), "Acrel DDS1352",
                "Acrel DTSD1352(Three)", "Eastron SDM230", "Eastron SDM630(Three)", "Eastron SDM120 MID", "Eastron SDM72D MID(Three)", "Din-Rail DTSU666 MID(Three)"};
        unLockTypeArray = new String[]{getString(R.string.m手动), getString(R.string.m自动)};
        netModeArray = new String[]{"DHCP", "STATIC"};
        LowPowerArray = new String[]{"Enable", "Disable"};
        unitKey = moneyTypes[0];
        unitValue = moneySymbols[0];
        setPileParamMap = new HashMap<>();
    }


    private void setCommonParams(ParamsSetBean bean) {
        String sfield = bean.getSfield();
        Object object = bean.getValue();
        String value = String.valueOf(object);
        switch (sfield) {
            case "country":
                setCountry();
                break;
            case "rate":
                setRate();
                break;
            case "unit":
                setCapacityUnit();
                break;
            case "G_ChargerMode":
                setModle();
                break;
            case "G_ExternalLimitPowerEnable":
                setEnable(17);
                break;
            case "G_ExternalSamplingCurWring":
                setWiring();
                break;
            case "G_SolarMode":
                setSolarMode();
                break;
            case "G_PeakValleyEnable":
                setEnable(20);
                break;
            case "G_AutoChargeTime":
                Intent intent = new Intent(this, TimeSelectActivity.class);
                intent.putExtra("start", startTime);
                intent.putExtra("end", endTime);
                startActivityForResult(intent, 100);
                break;
            case "G_PowerMeterType":
                setAmmterType();
                break;
            case "UnlockConnectorOnEVSideDisconnect":
                setLockType();
                break;
            case "apMode":
                apMode();
                break;

            case "G_LowPowerReserveEnable":
                setLowPowerReserveEnable();
                break;

            case "G_NetworkMode":
                setNetMode();
                break;

            case "G_LCDCloseEnable":
                setLcd();
                break;

            default:
                inputEdit(sfield, value);
                break;
        }
    }

    private void setRate() {
        Intent intent1 = new Intent(this, RateSetActivity.class);
        intent1.putExtra("sn", chargingId);
        intent1.putExtra("symbol", unitSymbol);
        intent1.putParcelableArrayListExtra("rate", (ArrayList<? extends Parcelable>) priceConfBeanList);
        jumpTo(intent1, false);
    }


    private void setECOLimit() {
        String tips = "";
        PileSetBean.DataBean initData = initPileSetBean.getData();
        float solarLimitPower = initData.getG_SolarLimitPower();
        new CircleDialog.Builder()
                .setWidth(0.8f)
                .setTitle(this.getString(R.string.m27温馨提示))
                .setInputHint(tips)
                .setInputText(String.valueOf(solarLimitPower))
                .setInputCounter(1000, (maxLen, currentLen) -> "")
                .setNegative(this.getString(R.string.m7取消), null)
                .setPositiveInput(this.getString(R.string.m9确定), new OnInputClickListener() {
                    @Override
                    public boolean onClick(String text, View v) {
                        if (TextUtils.isEmpty(text)) {
                            toast(R.string.m140不能为空);
                            return true;
                        }
                        boolean numeric_eco = MyUtil.isNumeric(text);
                        if (!numeric_eco) {
                            toast(R.string.m177输入格式不正确);
                            return true;
                        }
                        float v1;
                        try {
                            v1 = Float.parseFloat(text);
                        } catch (NumberFormatException e) {
                            v1 = 0;
                        }
                        setBean("G_SolarLimitPower", v1);
                        return true;
                    }
                })
                .show(this.getSupportFragmentManager());

    }


    private void apMode() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        new CircleDialog.Builder().setTitle(getString(R.string.m27温馨提示))
                .setText(getString(R.string.m289进入AP模式))
                .setPositive(getString(R.string.m9确定), v -> {
                    Mydialog.Show(this);
                    Map<String, Object> jsonMap = new HashMap<>();
                    jsonMap.put("chargeId", chargingId);//测试id
                    jsonMap.put("userId", SmartHomeUtil.getUserName());//测试id
                    jsonMap.put("lan", getLanguage());//测试id
                    String json = SmartHomeUtil.mapToJsonString(jsonMap);
                    PostUtil.postJson(SmartHomeUrlUtil.postRequestSwitchAp(), json, new PostUtil.postListener() {
                        @Override
                        public void Params(Map<String, String> params) {

                        }

                        @Override
                        public void success(String json) {
                            Mydialog.Dismiss();
                            try {
                                JSONObject object = new JSONObject(json);
                                int code = object.getInt("code");
                                if (code == 0) {
                                    Intent intent = new Intent(ChargingParamsActivity.this, ConnetWiFiActivity.class);
                                    intent.putExtra("sn", chargingId);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    jumpTo(intent, false);
                                }
                                toast(object.getString("data"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void LoginError(String str) {

                        }
                    });
                })
                .setNegative(getString(R.string.m7取消), v -> {

                })
                .show(fragmentManager);
    }


    /**
     * 弹框输入修改内容
     * item 修改项
     */
    private void inputEdit(final String key, final String value) {
        String tips = getString(R.string.m213输入更改的设置);
        if (key.equals("G_MaxCurrent")) tips = getString(R.string.m291设定值不能小于);
        new CircleDialog.Builder()
                .setWidth(0.8f)
                .setTitle(this.getString(R.string.m27温馨提示))
                .setInputHint(tips)
                .setInputText(value)
                .setInputCounter(1000, (maxLen, currentLen) -> "")
                .setNegative(this.getString(R.string.m7取消), null)
                .setPositiveInput(this.getString(R.string.m9确定), new OnInputClickListener() {
                    @Override
                    public boolean onClick(String text, View v) {
                        if (TextUtils.isEmpty(text)) {
                            toast(R.string.m140不能为空);
                            return true;
                        }
                        if ("ip".equals(key)) {
                            boolean b = MyUtil.isboolIp(text);
                            if (!b) {
                                toast(R.string.m177输入格式不正确);
                                return true;
                            }
                        }
                        if (key.equals("G_MaxCurrent")) {
                            boolean numeric3 = MyUtil.isNumberiZidai(text);
                            if (!numeric3) {
                                toast(R.string.m177输入格式不正确);
                                return true;
                            }

                            if (Integer.parseInt(text) < 3) {
                                toast(R.string.m291设定值不能小于);
                                return true;
                            }
                        }
                        setBean(key, text);
                        return true;
                    }
                })
                .show(this.getSupportFragmentManager());
    }


    /**
     * 保存设置
     */
    private void save() {
        if (!isEditInfo) {//未更改
            new CircleDialog.Builder()
                    .setTitle(getString(R.string.m27温馨提示))
                    .setWidth(0.8f)
                    .setText(getString(R.string.m304设置未做任何更改))
                    .setPositive(getString(R.string.m9确定), v -> {

                    })
                    .setNegative(getString(R.string.m7取消), null)
                    .show(getSupportFragmentManager());
        } else {
            new CircleDialog.Builder()
                    .setTitle(getString(R.string.m27温馨提示))
                    .setWidth(0.8f)
                    .setText(getString(R.string.m确认修改))
                    .setPositive(getString(R.string.m9确定), v -> {
                        requestEdit();
                    })
                    .setNegative(getString(R.string.m7取消), null)
                    .show(getSupportFragmentManager());
        }

    }

    /**
     * 请求修改参数
     * 这个方法是批量修改
     */
    private void requestEdit() {
        Mydialog.Show(this);
        if (setPileParamMap == null) return;
        setPileParamMap.put("chargeId", chargingId);//测试id
        setPileParamMap.put("userId", SmartHomeUtil.getUserName());//测试id
        setPileParamMap.put("lan", getLanguage());//测试id
        String json = SmartHomeUtil.mapToJsonString(setPileParamMap);
        PostUtil.postJson(SmartHomeUrlUtil.postSetChargingParams(), json, new PostUtil.postListener() {
            @Override
            public void Params(Map<String, String> params) {

            }

            @Override
            public void success(String json) {
                Mydialog.Dismiss();
                try {
                    JSONObject object = new JSONObject(json);
                    int code = object.getInt("code");
                    if (code == 0) {
                        isModyfi = true;
                        isEditInfo = false;
                        setPileParamMap.clear();
                        if (setPileParamMap.containsKey("name")) {
                            EventBus.getDefault().post(new FreshListMsg());
                        }
                    }
                    toast(object.getString("data"));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void LoginError(String str) {

            }
        });
    }


    /**
     * 请求修改参数
     */

    private void requestEdit(String key, Object value, PileSetBean.DataBean data) {
        Mydialog.Show(this);
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("chargeId", chargingId);//测试id
        jsonMap.put("userId", SmartHomeUtil.getUserName());//测试id
        jsonMap.put("lan", getLanguage());//测试id
        if ("unit".equals(key)) {
            jsonMap.put("symbol", unitSymbol);
        }
        jsonMap.put(key, value);
        String json = SmartHomeUtil.mapToJsonString(jsonMap);
        PostUtil.postJson(SmartHomeUrlUtil.postSetChargingParams(), json, new PostUtil.postListener() {
            @Override
            public void Params(Map<String, String> params) {

            }

            @Override
            public void success(String json) {
                Mydialog.Dismiss();
                try {
                    JSONObject object = new JSONObject(json);
                    int code = object.getInt("code");
                    if (code == 0) {
                        isModyfi = true;
                        isEditInfo = false;
                        setPileParamMap.clear();
                        refreshRv(data);
                        if ("name".equals(key) || "unit".equals(key) || "G_SolarMode".equals(key) || "G_SolarLimitPower".equals(key)) {
                            EventBus.getDefault().post(new FreshListMsg());
                        }
                    }
                    toast(object.getString("data"));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void LoginError(String str) {

            }
        });
    }


    private void setBean(String key, Object value) {//这个版本改为一个一个传
        PileSetBean.DataBean initData = initPileSetBean.getData();
        isEditInfo = true;
        setPileParamMap.put(key, value);
        switch (key) {
            case "name":
                initData.setName((String) value);
                break;
            case "country":
                initData.setCountry((String) value);
                break;
            case "site":
                initData.setSite((String) value);
                break;
            case "unit":
                setPileParamMap.put("symbol", unitSymbol);
                initData.setUnit((String) value);
                initData.setSymbol(unitSymbol);
                break;
            case "G_MaxCurrent":
                initData.setG_MaxCurrent((String) value);
                break;
            case "G_ExternalLimitPower":
                initData.setG_ExternalLimitPower((String) value);
                break;
            case "G_ChargerMode":
                initData.setG_ChargerMode(String.valueOf(value));
                break;
            case "ip":
                initData.setIp((String) value);
                break;
            case "gateway":
                initData.setGateway((String) value);
                break;
            case "mask":
                initData.setMask((String) value);
                break;
            case "mac":
                initData.setMac((String) value);
                break;
            case "host":
                initData.setHost((String) value);
                break;
            case "dns":
                initData.setDns((String) value);
                break;
            case "G_ExternalLimitPowerEnable":
                initData.setG_ExternalLimitPowerEnable((int) value);
                break;
            case "G_ExternalSamplingCurWring":
                initData.setG_ExternalSamplingCurWring((int) value);
                break;
            case "G_SolarMode":
                initData.setG_SolarMode((int) value);
                break;
            case "G_SolarLimitPower":
                initData.setG_SolarLimitPower((float) value);
                break;
            case "G_PeakValleyEnable":
                initData.setG_PeakValleyEnable((int) value);
                break;
            case "G_AutoChargeTime":
                initData.setG_AutoChargeTime((String) value);
                break;

            case "G_PowerMeterType":
                initData.setG_PowerMeterType((String) value);
                break;
            case "UnlockConnectorOnEVSideDisconnect":
                initData.setUnlockConnectorOnEVSideDisconnect((String) value);
                break;

            case "G_LowPowerReserveEnable":
                initData.setG_LowPowerReserveEnable((String) value);
                break;
            case "G_NetworkMode":
                initData.setNetMode((String) value);
                break;
            case "G_LCDCloseEnable":
                initData.setG_LCDCloseEnable((String) value);
                break;

        }
        requestEdit(key, value, initData);
    }


    private void refreshDate() {
        if (!isModyfi) Mydialog.Show(this);
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        jsonMap.put("sn", chargingId);//测试id
        jsonMap.put("userId", SmartHomeUtil.getUserName());//测试id
        jsonMap.put("lan", getLanguage());//测试id
        String json = SmartHomeUtil.mapToJsonString(jsonMap);
        PostUtil.postJson(SmartHomeUrlUtil.postRequestChargingParams(), json, new PostUtil.postListener() {
            @Override
            public void Params(Map<String, String> params) {

            }

            @Override
            public void success(String json) {
                Mydialog.Dismiss();
                srlPull.setRefreshing(false);
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    int code = jsonObject.getInt("code");
                    if (code == 0) {
                        initPileSetBean = new Gson().fromJson(json, PileSetBean.class);
                        if (initPileSetBean != null) {
                            PileSetBean.DataBean data = initPileSetBean.getData();
                            setHeadView(data);
                            refreshRv(data);
                        } else {
                            initPileSetBean = new PileSetBean();
                            PileSetBean.DataBean data = new PileSetBean.DataBean();
                            initPileSetBean.setData(data);
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void LoginError(String str) {

            }
        });
    }

    private void setHeadView(PileSetBean.DataBean data) {
        tvId.setText(data.getChargeId());
        tvKeys.setText(data.getG_Authentication());
        tvVersion.setText(data.getVersion());
    }


    private void refreshRv(PileSetBean.DataBean data) {
        String netMode = data.getNetMode();
        List<MultiItemEntity> newlist = new ArrayList<>();
        for (int i = 0; i < keys.length; i++) {
            ParamsSetBean bean = new ParamsSetBean();
            String sfield = keySfields[i];
            bean.setIndex(i);
            switch (sfield) {
                case "":
                    bean.setTitle(keys[i]);
                    bean.setType(ParamsSetAdapter.PARAM_TITILE);
                    break;
                case "name":
                    bean.setType(ParamsSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(data.getName());
                    break;
                case "country":
                    bean.setType(ParamsSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    String country = data.getCountry();
                    if (TextUtils.isEmpty(country)) {
                        bean.setValue("-");
                    } else {
                        bean.setValue(country);
                    }
                    break;
                case "site":
                    bean.setType(ParamsSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    String site = data.getSite();
                    if (TextUtils.isEmpty(site)) {
                        bean.setValue("-");
                    } else {
                        bean.setValue(site);
                    }
                    break;
                case "rate":
                    bean.setType(ParamsSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue("");
                    for (int j = 0; j < priceConfBeanList.size(); j++) {
                        ChargingBean.DataBean.PriceConfBean priceConfBean = priceConfBeanList.get(j);
                        priceConfBean.setItemType(ParamsSetAdapter.PARAM_ITEM_RATE);
                        priceConfBean.setSfield(keySfields[i]);
                        if (!TextUtils.isEmpty(data.getSymbol())) {
                            priceConfBean.setSymbol(data.getSymbol());
                        }
                        if (noConfigKeys.contains(priceConfBean.getSfield())) {
                            priceConfBean.setAuthority(false);
                        } else {
                            priceConfBean.setAuthority(true);
                        }
                        bean.addSubItem(priceConfBean);
                    }
                    break;

                case "unit":
                    bean.setType(ParamsSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    String unit = data.getUnit();
                    if (TextUtils.isEmpty(unit)) {
                        bean.setValue("-");
                    } else {
                        bean.setValue(unit);
                    }
                    unitSymbol = data.getSymbol();
                    break;

                case "G_MaxCurrent":
                    bean.setType(ParamsSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    String g_maxCurrent = data.getG_MaxCurrent();
                    if (TextUtils.isEmpty(g_maxCurrent)) {
                        bean.setValue("-");
                    } else {
                        bean.setValue(g_maxCurrent);
                    }
                    break;

                case "G_ExternalLimitPower":
                    bean.setType(ParamsSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    String g_externalLimitPower = data.getG_ExternalLimitPower();
                    if (TextUtils.isEmpty(g_externalLimitPower)) {
                        bean.setValue("-");
                    } else {
                        bean.setValue(g_externalLimitPower);
                    }
                    break;

                case "G_ChargerMode":
                    bean.setType(ParamsSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    String model = data.getG_ChargerMode();
                    if ("1".equals(model)) {
                        bean.setValue(mModels[0]);
                    } else if ("2".equals(model)) {
                        bean.setValue(mModels[1]);
                    } else if ("3".equals(model)) {
                        bean.setValue(mModels[2]);
                    } else {
                        bean.setValue("");
                    }
                    break;
                case "ip":
                    if (netModeArray[0].equals(netMode)) {//静态不给修改
                        bean.setType(ParamsSetAdapter.PARAM_ITEM_CANT_CLICK);
                    } else {
                        bean.setType(ParamsSetAdapter.PARAM_ITEM);
                    }
                    bean.setKey(keys[i]);
                    String ip = data.getIp();
                    if (TextUtils.isEmpty(ip)) {
                        bean.setValue("-");
                    } else {
                        bean.setValue(ip);
                    }
                    break;
                case "gateway":
                    if (netModeArray[0].equals(netMode)) {//静态不给修改
                        bean.setType(ParamsSetAdapter.PARAM_ITEM_CANT_CLICK);
                    } else {
                        bean.setType(ParamsSetAdapter.PARAM_ITEM);
                    }
                    bean.setKey(keys[i]);
                    String gateway = data.getGateway();
                    if (TextUtils.isEmpty(gateway)) {
                        bean.setValue("-");
                    } else {
                        bean.setValue(gateway);
                    }
                    break;
                case "mask":
                    if (netModeArray[0].equals(netMode)) {//静态不给修改
                        bean.setType(ParamsSetAdapter.PARAM_ITEM_CANT_CLICK);
                    } else {
                        bean.setType(ParamsSetAdapter.PARAM_ITEM);
                    }
                    bean.setKey(keys[i]);
                    String mask = data.getMask();
                    if (TextUtils.isEmpty(mask)) {
                        bean.setValue("-");
                    } else {
                        bean.setValue(mask);
                    }
                    break;
                case "mac":
                    bean.setType(ParamsSetAdapter.PARAM_ITEM_CANT_CLICK);
                    bean.setKey(keys[i]);
                    bean.setValue(data.getMac());
                    String mac = data.getMac();
                    if (TextUtils.isEmpty(mac)) {
                        bean.setValue("-");
                    } else {
                        bean.setValue(mac);
                    }
                    break;

                case "host":
                    bean.setType(ParamsSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    String host = data.getHost();
                    if (TextUtils.isEmpty(host)) {
                        bean.setValue("-");
                    } else {
                        bean.setValue(host);
                    }
                    break;
                case "dns":
                    bean.setType(ParamsSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    String dns = data.getDns();
                    if (TextUtils.isEmpty(dns)) {
                        bean.setValue("-");
                    } else {
                        bean.setValue(dns);
                    }
                    break;

                case "G_NetworkMode":
                    bean.setType(ParamsSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    if (TextUtils.isEmpty(netMode)) {
                        bean.setValue("-");
                    } else {
                        bean.setValue(netMode);
                    }
                    break;
                case "G_ExternalLimitPowerEnable":
                    bean.setType(ParamsSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    int powerenable = data.getG_ExternalLimitPowerEnable();
                    if (powerenable == 0) {//0禁止
                        bean.setValue(enableArray[0]);
                    } else {
                        bean.setValue(enableArray[1]);
                    }
                    break;
                case "G_ExternalSamplingCurWring":
                    bean.setType(ParamsSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    int wiring = data.getG_ExternalSamplingCurWring();
                    if (wiring < wiringArray.length) {
                        bean.setValue(wiringArray[wiring]);
                    } else {
                        bean.setValue(wiring + "");
                    }

                    break;

                case "G_SolarMode":
                    bean.setType(ParamsSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    solar = data.getG_SolarMode();
                    if (solar == 0) {
                        bean.setValue(solarArrray[0]);
                    } else if (solar == 1) {
                        bean.setValue(solarArrray[1]);
                    } else {
                        bean.setValue(solarArrray[2]);
                    }
                    float solarLimitPower = data.getG_SolarLimitPower();
                    SolarBean solarBean = new SolarBean();
                    solarBean.setType(ParamsSetAdapter.PARAM_ITEM_SOLAR);
                    solarBean.setKey(getString(R.string.m电流限制) + "(kW)");
                    solarBean.setValue(String.valueOf(solarLimitPower));
                    solarBean.setSfield(keySfields[i]);
                    if (noConfigKeys.contains(solarBean.getSfield())) {
                        solarBean.setAuthority(false);
                    } else {
                        solarBean.setAuthority(true);
                    }
                    bean.addSubItem(solarBean);
                    break;

                case "G_PeakValleyEnable":
                    bean.setType(ParamsSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    int peakValleyEnable = data.getG_PeakValleyEnable();
                    if (peakValleyEnable == 0) {//0禁止
                        bean.setValue(enableArray[0]);
                    } else {
                        bean.setValue(enableArray[1]);
                    }
                    break;
                case "G_AutoChargeTime":
                    String autoChargeTime = data.getG_AutoChargeTime();
                    if (autoChargeTime.contains("-")) {
                        String[] split = autoChargeTime.split("-");
                        if (split.length >= 2) {
                            startTime = split[0];
                            endTime = split[1];
                        }
                    }
                    bean.setType(ParamsSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(autoChargeTime);
                    break;

                case "G_PowerMeterType":
                    String powerMeterType = data.getG_PowerMeterType();
                    bean.setType(ParamsSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    if (TextUtils.isEmpty(powerMeterType)) {
                        bean.setValue("-");
                    } else {
                        bean.setValue(powerMeterType);
                    }
                    break;

                case "UnlockConnectorOnEVSideDisconnect":
                    String unlockConnectorOnEVSideDisconnect = data.getUnlockConnectorOnEVSideDisconnect();
                    bean.setType(ParamsSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    if ("true".equals(unlockConnectorOnEVSideDisconnect)) {
                        bean.setValue(unLockTypeArray[1]);
                    } else {
                        bean.setValue(unLockTypeArray[0]);
                    }
                    break;

                case "apMode":
                    bean.setType(ParamsSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue("");
                    break;

                case "G_LowPowerReserveEnable":
                    bean.setType(ParamsSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    String lowPowerReserveEnable = data.getG_LowPowerReserveEnable();
                    if (TextUtils.isEmpty(lowPowerReserveEnable)) {
                        bean.setValue("-");
                    } else {
                        bean.setValue(lowPowerReserveEnable);
                    }
                    break;
                case "G_LCDCloseEnable":
                    bean.setType(ParamsSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    String g_lcdCloseEnable = data.getG_LCDCloseEnable();
                    if (TextUtils.isEmpty(g_lcdCloseEnable)) {
                        bean.setValue("-");
                    } else {
                        bean.setValue(g_lcdCloseEnable);
                    }
                    break;
            }
            bean.setSfield(keySfields[i]);
            if (noConfigKeys.contains(bean.getSfield())) {
                bean.setAuthority(false);
            } else {
                bean.setAuthority(true);
            }

            newlist.add(bean);
        }
        mAdapter.setNewData(newlist);
        mAdapter.expandAll();
    }


    /**
     * 设置模式
     */
    public void setModle() {
        FragmentManager fragmentManager = ChargingParamsActivity.this.getSupportFragmentManager();
        new CircleDialog.Builder()
                .setTitle(getString(R.string.m154充电模式))
                .setItems(mModels, new OnLvItemClickListener() {
                    @Override
                    public boolean onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
                        setBean("G_ChargerMode", model);
                        return true;
                    }
                })
                .setNegative(getString(R.string.m7取消), null)
                .show(fragmentManager);
    }


    private void getMoneyUnit() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("cmd", "selectMoneyUnit");
            jsonObject.put("lan", getLanguage());
            jsonObject.put("userId", SmartHomeUtil.getUserName());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String params = jsonObject.toString();
        PostUtil.postJson(SmartHomeUrlUtil.postByCmd(), params, new PostUtil.postListener() {
            @Override
            public void Params(Map<String, String> params) {

            }

            @Override
            public void success(String json) {
                try {
                    JSONObject respon = new JSONObject(json);
                    int code = respon.optInt("code");
                    if (code == 0) {
                        JSONArray jsonObject1 = respon.optJSONArray("data");
                        setUnitMap(jsonObject1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void LoginError(String str) {

            }
        });
    }


    private void setCapacityUnit() {
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
                .setItems(items, new OnLvItemClickListener() {
                    @Override
                    public boolean onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        try {
                            unitKey = keys.get(position);
                            unitValue = values.get(position);
                            unitSymbol = unitValue;
                            setBean("unit", unitKey);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return true;
                    }
                })

                .setNegative(getString(R.string.m7取消), null)
                .show(getSupportFragmentManager());
    }

    /**
     * 货币单位列表初始化
     *
     * @param unitArray
     */
    private void setUnitMap(@NonNull JSONArray unitArray) {
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


    /*设置使能*/
    private void setEnable(int index) {
        String title;
        List<String> list = Arrays.asList(enableArray);
        if (index == 20) title = getString(R.string.m297峰谷充电使能);
        else title = getString(R.string.m298功率分配使能);
        OptionsPickerView<String> pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                if (index == 17) {
                    setBean("G_ExternalLimitPowerEnable", options1);
                } else {
                    setBean("G_PeakValleyEnable", options1);
                }
            }
        })
                .setTitleText(title)
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


    /*电表类型*/
    private void setCountry() {
        if (countryList == null || countryList.size() == 0) {
            getCountry();
        } else {
            seletcCountry();
        }
    }


    private void getCountry() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("cmd", "countryList");
            jsonObject.put("lan", getLanguage());
            jsonObject.put("userId", SmartHomeUtil.getUserName());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String params = jsonObject.toString();
        PostUtil.postJson(SmartHomeUrlUtil.postByCmd(), params, new PostUtil.postListener() {
            @Override
            public void Params(Map<String, String> params) {

            }

            @Override
            public void success(String json) {
                try {
                    JSONObject respon = new JSONObject(json);
                    int code = respon.optInt("code");
                    if (code == 0) {
                        JSONArray jsonObject1 = respon.optJSONArray("data");
                        if (jsonObject1 != null) {
                            parserCountryJson(jsonObject1);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void LoginError(String str) {

            }
        });
    }

    private void parserCountryJson(JSONArray jsonObject1) throws JSONException {
        for (int i = 0; i < jsonObject1.length(); i++) {
            JSONObject jsonObject = jsonObject1.getJSONObject(i);
            String s = jsonObject.optString("country", " ");
            countryList.add(s);
        }
        seletcCountry();
    }

    private void seletcCountry() {
        OptionsPickerView<String> pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                setBean("country", countryList.get(options1));
            }
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
        pvOptions.setPicker(countryList);
        pvOptions.show();
    }


    /*电表类型*/
    private void setAmmterType() {
        List<String> list = Arrays.asList(ammterTypeArray);
        OptionsPickerView<String> pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                setBean("G_PowerMeterType", list.get(options1));
            }
        })
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


    /*网络模式*/
    private void setLowPowerReserveEnable() {
        List<String> list = Arrays.asList(LowPowerArray);
        OptionsPickerView<String> pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                setBean("G_LowPowerReserveEnable", list.get(options1));
            }
        })
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


    /*网络模式*/
    private void setNetMode() {
        List<String> list = Arrays.asList(netModeArray);
        OptionsPickerView<String> pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                setBean("G_NetworkMode", list.get(options1));
            }
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
        pvOptions.setPicker(list);
        pvOptions.show();

    }


    /*网络模式*/
    private void setLcd() {
        List<String> list = Arrays.asList(LowPowerArray);
        OptionsPickerView<String> pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                setBean("G_LCDCloseEnable", list.get(options1));
            }
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


    /*电子锁配置*/
    private void setLockType() {
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
                setBean("UnlockConnectorOnEVSideDisconnect", value);
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


    /*接线方式*/
    private void setWiring() {
        List<String> list = Arrays.asList(wiringArray);
        OptionsPickerView<String> pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                setBean("G_ExternalSamplingCurWring", options1);
            }
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


    /*solar模式*/
    private void setSolarMode() {
        List<String> list = Arrays.asList(solarArrray);
        OptionsPickerView<String> pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                setBean("G_SolarMode", options1);
            }
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 100:
                    String startTime = data.getStringExtra("start");
                    String endTime = data.getStringExtra("end");
                    String chargingTime;
                    if (TextUtils.isEmpty(startTime) && TextUtils.isEmpty(endTime)) {
                        chargingTime = "";
                    } else {
                        if (TextUtils.isEmpty(startTime)) {
                            toast(R.string.m130未设置开始时间);
                            return;
                        }
                        if (TextUtils.isEmpty(endTime)) {
                            toast(R.string.m284未设置结束时间);
                            return;
                        }
                        chargingTime = startTime + "-" + endTime;
                    }
                    setBean("G_AutoChargeTime", chargingTime);
                    break;
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void aa(RefreshRateMsg msg) {
        if (msg.getPriceConfBeanList() != null) {
            priceConfBeanList = msg.getPriceConfBeanList();
            PileSetBean.DataBean data = initPileSetBean.getData();
            refreshRv(data);
        }

    }

    @OnClick({R.id.ivLeft, R.id.tvRight})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivLeft:
                back();
                break;
            case R.id.tvRight:
                break;
        }
    }

    private void back() {
        if (isEditInfo) {//未更改
            new CircleDialog.Builder()
                    .setTitle(getString(R.string.m27温馨提示))
                    .setWidth(0.8f)
                    .setText(getString(R.string.m设置未保存))
                    .setPositive(getString(R.string.m9确定), v -> {
                        finish();
                    })
                    .setNegative(getString(R.string.m7取消), null)
                    .show(getSupportFragmentManager());
        } else {
            finish();
        }
    }


    private void showInputPassword(int type, ParamsSetBean bean) {
        new CircleDialog.Builder()
                .setTitle(getString(R.string.m27温馨提示))
                //添加标题，参考普通对话框
                .setInputHint(getString(R.string.m26请输入密码))//提示
                .setInputCounter(1000, (maxLen, currentLen) -> "")
                .configInput(params -> {
                    params.gravity = Gravity.CENTER;
//                    params.textSize = 45;
//                            params.backgroundColor=ContextCompat.getColor(ChargingPileActivity.this, R.color.preset_edit_time_background);
                    params.strokeColor = ContextCompat.getColor(this, R.color.preset_edit_time_background);
                    params.inputType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
                            | InputType.TYPE_TEXT_FLAG_MULTI_LINE;
                })
                .setPositiveInput(getString(R.string.m9确定), new OnInputClickListener() {
                    @Override
                    public boolean onClick(String text, View v) {
                        if (password.equals(text)) {
                            isVerified = true;
                            switch (type) {
                                case ParamsSetAdapter.PARAM_ITEM:
                                    setCommonParams(bean);
                                    break;
                                case ParamsSetAdapter.PARAM_ITEM_RATE:
                                    setRate();
                                    break;
                                case ParamsSetAdapter.PARAM_ITEM_SOLAR:
                                    setECOLimit();
                                    break;
                            }
                        } else {
                            toast(R.string.m验证失败);
                        }
                        return true;
                    }
                })
                //添加取消按钮，参考普通对话框
                .setNegative(getString(R.string.m7取消), v -> {

                })
                .show(getSupportFragmentManager());
    }

}
