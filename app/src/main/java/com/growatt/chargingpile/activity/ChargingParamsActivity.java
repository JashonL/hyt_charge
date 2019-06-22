package com.growatt.chargingpile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.google.gson.Gson;
import com.growatt.chargingpile.BaseActivity;
import com.growatt.chargingpile.EventBusMsg.SetRateMsg;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.adapter.ParamsSetAdapter;
import com.growatt.chargingpile.bean.ChargingBean;
import com.growatt.chargingpile.bean.ParamsSetBean;
import com.growatt.chargingpile.bean.PileSetBean;
import com.growatt.chargingpile.connutil.PostUtil;
import com.growatt.chargingpile.util.Cons;
import com.growatt.chargingpile.util.MyUtil;
import com.growatt.chargingpile.util.Mydialog;
import com.growatt.chargingpile.util.SmartHomeUrlUtil;
import com.growatt.chargingpile.util.SmartHomeUtil;
import com.growatt.chargingpile.util.T;
import com.mylhyl.circledialog.CircleDialog;

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
import butterknife.Unbinder;


public class ChargingParamsActivity extends BaseActivity {
    @BindView(R.id.headerView)
    View headerView;
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
    private Unbinder bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charging_params);
        bind = ButterKnife.bind(this);
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

    private void initPullView() {
        srlPull.setColorSchemeColors(ContextCompat.getColor(this, R.color.maincolor_1));
        srlPull.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isModyfi = false;
                refreshDate();
            }
        });
    }


    private void setOnclickListener() {
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            MultiItemEntity multiItemEntity = mAdapter.getData().get(position);
            if (multiItemEntity == null) return;
            int itemType = multiItemEntity.getItemType();
            if (itemType == ParamsSetAdapter.PARAM_TITILE || itemType == ParamsSetAdapter.PARAM_ITEM_CANT_CLICK) {
            }
            else if (itemType == ParamsSetAdapter.PARAM_ITEM) {
                String key = ((ParamsSetBean) multiItemEntity).getKey();
                Object object = ((ParamsSetBean) multiItemEntity).getValue();
                String value = String.valueOf(object);
                if (key.equals(keys[1])) {
                    inputEdit("name", value);
                } else if (key.equals(keys[2])) {
                    inputEdit("address", value);
                } else if (key.equals(keys[3])) {
                    inputEdit("site", value);
                } else if (key.equals(keys[4])) {
//                    inputEdit("rate", value);
                } else if (key.equals(keys[5])) {
                    setCapacityUnit();
                } else if (key.equals(keys[6])) {
                    inputEdit("G_MaxCurrent", value);
                } else if (key.equals(keys[7])) {
                    inputEdit("G_ExternalLimitPower", value);
                } else if (key.equals(keys[8])) {
                    setModle();
                } else if (key.equals(keys[10])) {
                    inputEdit("ip", value);
                } else if (key.equals(keys[11])) {
                    inputEdit("gateway", value);
                } else if (key.equals(keys[12])) {
                    inputEdit("mask", value);
                } else if (key.equals(keys[13])) {
                    inputEdit("mac", value);
                } else if (key.equals(keys[14])) {
                    inputEdit("host", value);
                } else if (key.equals(keys[15])) {
                    inputEdit("dns", value);
                } else if (key.equals(keys[17])) {
                    apMode();
                }
            } else if (itemType == ParamsSetAdapter.PARAM_ITEM_RATE) {//设置费率
                Intent intent1 = new Intent(this, RateSetActivity.class);
                intent1.putExtra("sn", chargingId);
                intent1.putExtra("symbol", unitSymbol);
                intent1.putParcelableArrayListExtra("rate", (ArrayList<? extends Parcelable>) priceConfBeanList);
                jumpTo(intent1, false);
            }
        });
    }


    private void apMode() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        new CircleDialog.Builder().setTitle(getString(R.string.m27温馨提示))
                .setText(getString(R.string.m289进入AP模式))
                .setPositive(getString(R.string.m9确定), v -> {
                    Mydialog.Show(this);
                    Map<String, Object> jsonMap = new HashMap<String, Object>();
                    jsonMap.put("chargeId", chargingId);//测试id
                    jsonMap.put("userId", Cons.userBean.getAccountName());//测试id
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


    private void initResource() {
        mModels = new String[]{getString(R.string.m217扫码刷卡), getString(R.string.m218仅刷卡充电), getString(R.string.m219插枪充电)};
        keys = new String[]{
                getString(R.string.m148基础参数), getString(R.string.m149电桩名称), getString(R.string.m150国家城市),
                getString(R.string.m151站点), getString(R.string.m152充电费率), getString(R.string.m315货币单位),
                getString(R.string.m313电桩最大输出电流), getString(R.string.m314智能功率分配), getString(R.string.m154充电模式), getString(R.string.m155高级设置), getString(R.string.m156充电桩IP),
                getString(R.string.m157网关), getString(R.string.m158子网掩码), getString(R.string.m159网络MAC地址),
                getString(R.string.m160服务器URL), getString(R.string.m161DNS地址), "", getString(R.string.m289进入AP模式), ""};
        for (int i = 0; i < keys.length; i++) {
            ParamsSetBean bean = new ParamsSetBean();
            if (i == 0 || i == 9 || i == 16 || i == 18) {
                bean.setTitle(keys[i]);
                bean.setType(ParamsSetAdapter.PARAM_TITILE);
            } else {
                bean.setType(ParamsSetAdapter.PARAM_ITEM);
                bean.setKey(keys[i]);
                bean.setValue("");
            }
            list.add(bean);
        }

        moneyTypes = new String[]{"pound", "dollar", "euro", "baht", "rmb"};
        moneySymbols = new String[]{"£", "$", "€", "฿", "￥"};
        unitKey = moneyTypes[0];
        unitValue = moneySymbols[0];
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


    private void initHeaderView() {
        setHeaderImage(headerView, R.drawable.back, Position.LEFT, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setHeaderTitle(headerView, getString(R.string.m141参数设置), R.color.title_1, true);
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
                .setNegative(this.getString(R.string.m7取消), null)
                .setPositiveInput(this.getString(R.string.m9确定), (text, v) -> {
                    if (TextUtils.isEmpty(text)) {
                        toast(R.string.m140不能为空);
                        return;
                    }
                    if ("ip".equals(key)) {
                        boolean b = MyUtil.isboolIp(value);
                        if (!b) {
                            toast(R.string.m177输入格式不正确);
                            return;
                        }
                    }
                    if (key.equals("G_MaxCurrent")) {
                        boolean numeric3 = MyUtil.isNumberiZidai(text);
                        if (!numeric3) {
                            T.make(getString(R.string.m177输入格式不正确), this);
                            return;
                        }

                        if (Integer.parseInt(text) < 3) {
                            T.make(getString(R.string.m291设定值不能小于), this);
                            return;
                        }
                    }
                    requestEdit(key, text);
                })
                .show(this.getSupportFragmentManager());

/*


        final EditText et = new EditText(this);
        int dimen = getResources().getDimensionPixelSize(R.dimen.xa24);
        et.setTextSize(TypedValue.COMPLEX_UNIT_PX, dimen);
        et.setHint(value);
        et.setPadding(20, 20, 20, 20);
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle(R.string.m27温馨提示)
                .setMessage(getString(R.string.m请输入设置内容)).setView(
                        et).setPositiveButton(R.string.m9确定, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int swich) {
                        String s = et.getText().toString();
                        if (TextUtils.isEmpty(s)) {
                            toast(R.string.m140不能为空);
                            return;
                        }
                        if ("ip".equals(key)) {
                            boolean b = MyUtil.isboolIp(value);
                            if (!b) {
                                toast(R.string.m177输入格式不正确);
                                return;
                            }
                        }
                        requestEdit(key, s);
                    }
                }).setNegativeButton(R.string.m7取消, null).create();
        dialog.setCancelable(true);
        dialog.show();*/
    }


    /**
     * 请求修改参数
     */

    private void requestEdit(String key, Object value) {
        Mydialog.Show(this);
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        jsonMap.put("chargeId", chargingId);//测试id
        jsonMap.put("userId", Cons.userBean.getAccountName());//测试id
        jsonMap.put("lan", getLanguage());//测试id
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
                        refreshDate();
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


    private void refreshDate() {
        if (!isModyfi) Mydialog.Show(this);
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        jsonMap.put("sn", chargingId);//测试id
        jsonMap.put("userId", Cons.userBean.getAccountName());//测试id
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
                        PileSetBean pileSetBean = new Gson().fromJson(json, PileSetBean.class);
                        if (pileSetBean != null) {
                            PileSetBean.DataBean data = pileSetBean.getData();
                            setHeadView(data);
                            refreshRv(data);
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
        List<MultiItemEntity> newlist = new ArrayList<>();
        for (int i = 0; i < keys.length; i++) {
            ParamsSetBean bean = new ParamsSetBean();
            switch (i) {
                case 0:
                case 9:
                case 16:
                case 18:
                    bean.setTitle(keys[i]);
                    bean.setType(ParamsSetAdapter.PARAM_TITILE);
                    break;
                case 1:
                    bean.setType(ParamsSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(data.getName());
                    break;
                case 2:
                    bean.setType(ParamsSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(data.getAddress());
                    break;
                case 3:
                    bean.setType(ParamsSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(data.getSite());
                    break;
                case 4:
                    bean.setType(ParamsSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue("");
                    for (int j = 0; j < priceConfBeanList.size(); j++) {
                        ChargingBean.DataBean.PriceConfBean priceConfBean = priceConfBeanList.get(j);
                        priceConfBean.setItemType(ParamsSetAdapter.PARAM_ITEM_RATE);
                        bean.addSubItem(priceConfBean);
                    }
                    break;
                case 5:
                    bean.setType(ParamsSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(data.getUnit());
                    unitSymbol = data.getSymbol();
                    break;
                case 6:
                    bean.setType(ParamsSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(data.getG_MaxCurrent());
                    break;
                case 7:
                    bean.setType(ParamsSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(data.getG_ExternalLimitPower());
                    break;
                case 8:
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
                case 10:
                    bean.setType(ParamsSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(data.getIp());
                    break;
                case 11:
                    bean.setType(ParamsSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(data.getGateway());
                    break;
                case 12:
                    bean.setType(ParamsSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(data.getMask());
                    break;
                case 13:
                    bean.setType(ParamsSetAdapter.PARAM_ITEM_CANT_CLICK);
                    bean.setKey(keys[i]);
                    bean.setValue(data.getMac());
                    break;
                case 14:
                    bean.setType(ParamsSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(data.getHost());
                    break;
                case 15:
                    bean.setType(ParamsSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(data.getDns());
                    break;
                case 17:
                    bean.setType(ParamsSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue("");
                    break;
            }
            newlist.add(bean);
            mAdapter.setNewData(newlist);
//            mAdapter.replaceData(newlist);
            mAdapter.expandAll();
        }
    }


    /**
     * 设置模式
     */
    public void setModle() {
        FragmentManager fragmentManager = ChargingParamsActivity.this.getSupportFragmentManager();
        new CircleDialog.Builder()
                .setTitle(getString(R.string.m154充电模式))
                .setItems(mModels, new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
                        requestEdit("G_ChargerMode", model);
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
                .setItems(items, (parent, view, position, id) -> {
                    try {
                        unitKey = keys.get(position);
                        unitValue = values.get(position);
                        requestEdit("unit", unitKey);
                    } catch (Exception e) {
                        e.printStackTrace();
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bind != null) bind.unbind();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void aa(SetRateMsg msg) {
        if (msg.getPriceConfBeanList() != null) {
            priceConfBeanList = msg.getPriceConfBeanList();
            refreshDate();
        }

    }
}
