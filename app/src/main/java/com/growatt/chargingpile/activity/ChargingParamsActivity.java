package com.growatt.chargingpile.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.growatt.chargingpile.BaseActivity;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.adapter.ParamsSetAdapter;
import com.growatt.chargingpile.bean.ParamsSetBean;
import com.growatt.chargingpile.bean.PileSetBean;
import com.growatt.chargingpile.connutil.PostUtil;
import com.growatt.chargingpile.util.Cons;
import com.growatt.chargingpile.util.MyUtil;
import com.growatt.chargingpile.util.Mydialog;
import com.growatt.chargingpile.util.SmartHomeUrlUtil;
import com.growatt.chargingpile.util.SmartHomeUtil;
import com.mylhyl.circledialog.CircleDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ChargingParamsActivity extends BaseActivity {
    @BindView(R.id.headerView)
    View headerView;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.srl_pull)
    SwipeRefreshLayout srlPull;

    private TextView tvId;
    private TextView tvKeys;


    private ParamsSetAdapter mAdapter;
    private List<ParamsSetBean> list = new ArrayList<>();
    private LinearLayoutManager mLinearLayoutManager;
    private String[] keys;
    private View paramHeadView;

    private String[] mModels;
    private boolean isModyfi = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charging_params);
        ButterKnife.bind(this);
        initHeaderView();
        initResource();
        initRecyclerView();
        refreshDate();
        setOnclickListener();
        initPullView();
    }

    private void initPullView() {
        srlPull.setColorSchemeColors(ContextCompat.getColor(this, R.color.green_1));
        srlPull.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isModyfi = false;
                refreshDate();
            }
        });
    }


    private void setOnclickListener() {
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (position == 0 || position == 7) return;
                ParamsSetBean bean = mAdapter.getData().get(position);
                switch (position) {
                    case 1:
                        inputEdit("name", (String) bean.getValue());
                        break;
                    case 2:
                        inputEdit("address", (String) bean.getValue());
                        break;
                    case 3:
                        inputEdit("site", (String) bean.getValue());
                        break;
                    case 4:
                        inputEdit("rate", String.valueOf(bean.getValue()));
                        break;
                    case 5:
                        inputEdit("power", String.valueOf(bean.getValue()));
                        break;
                    case 6:
//                        inputEdit("model", (String) bean.getValue());
                        setModle();
                        break;
                    case 8:
                        inputEdit("ip", (String) bean.getValue());
                        break;
                    case 9:
                        inputEdit("gateway", (String) bean.getValue());
                        break;
                    case 10:
                        inputEdit("mask", (String) bean.getValue());
                        break;
                    case 11:
                        inputEdit("mac", (String) bean.getValue());
                        break;
                    case 12:
                        inputEdit("host", (String) bean.getValue());
                        break;
                    case 13:
                        inputEdit("dns", (String) bean.getValue());
                        break;
                }

            }
        });
    }

    private void initResource() {
        mModels = new String[]{getString(R.string.m217扫码刷卡), getString(R.string.m218仅刷卡充电), getString(R.string.m219插枪充电)};
        keys = new String[]{getString(R.string.m148基础参数), getString(R.string.m149电桩名称), getString(R.string.m150国家城市), getString(R.string.m151站点), getString(R.string.m152充电费率), getString(R.string.m153功率设置), getString(R.string.m154充电模式), getString(R.string.m155高级设置), getString(R.string.m156充电桩IP), getString(R.string.m157网关), getString(R.string.m158子网掩码), getString(R.string.m159网络MAC地址), getString(R.string.m160服务器URL), getString(R.string.m161DNS地址)};
        for (int i = 0; i < 14; i++) {
            ParamsSetBean bean = new ParamsSetBean();
            if (i == 0 || i == 7) {
                bean.setTitle(keys[i]);
                bean.setType(ParamsSetBean.PARAM_TITILE);
            } else {
                bean.setType(ParamsSetBean.PARAM_ITEM);
                bean.setKey(keys[i]);
                bean.setValue("");
            }
            list.add(bean);
        }
    }

    private void initRecyclerView() {
        paramHeadView = LayoutInflater.from(this).inflate(R.layout.item_params_header_view, null);
        tvId = paramHeadView.findViewById(R.id.tv_id);
        tvKeys = paramHeadView.findViewById(R.id.tv_keys);
        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mAdapter = new ParamsSetAdapter(list);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.setAdapter(mAdapter);
        mAdapter.addHeaderView(paramHeadView);
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
        dialog.show();
    }


    /**
     * 请求修改参数
     */

    private void requestEdit(String key, Object value) {
        Mydialog.Show(this);
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        jsonMap.put("chargeId", Cons.mCurrentPile.getChargeId());//测试id
        jsonMap.put("userId", Cons.userBean.getId());//测试id
        jsonMap.put("lan", getLanguage());//测试id
        jsonMap.put(key, value);
        String json = SmartHomeUtil.mapToJsonString(jsonMap);
        PostUtil.postJson(SmartHomeUrlUtil.SET_CHARGING_PARAMS, json, new PostUtil.postListener() {
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
        jsonMap.put("sn", Cons.mCurrentPile.getChargeId());//测试id
        jsonMap.put("userId", Cons.userBean.getId());//测试id
        jsonMap.put("lan", getLanguage());//测试id
        String json = SmartHomeUtil.mapToJsonString(jsonMap);
        PostUtil.postJson(SmartHomeUrlUtil.REQUEST_CHARGING_PARAMS, json, new PostUtil.postListener() {
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
        tvKeys.setText(data.getCode());
    }


    private void refreshRv(PileSetBean.DataBean data) {
        List<ParamsSetBean> newlist = new ArrayList<>();
        for (int i = 0; i < 14; i++) {
            ParamsSetBean bean = new ParamsSetBean();
            switch (i) {
                case 0:
                case 7:
                    bean.setTitle(keys[i]);
                    bean.setType(ParamsSetBean.PARAM_TITILE);
                    break;
                case 1:
                    bean.setType(ParamsSetBean.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(data.getName());
                    break;
                case 2:
                    bean.setType(ParamsSetBean.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(data.getAddress());
                    break;
                case 3:
                    bean.setType(ParamsSetBean.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(data.getSite());
                    break;
                case 4:
                    bean.setType(ParamsSetBean.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(data.getRate());
                    break;
                case 5:
                    bean.setType(ParamsSetBean.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(data.getPower());
                    break;
                case 6:
                    bean.setType(ParamsSetBean.PARAM_ITEM);
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

                case 8:
                    bean.setType(ParamsSetBean.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(data.getIp());
                    break;
                case 9:
                    bean.setType(ParamsSetBean.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(data.getGateway());
                    break;
                case 10:
                    bean.setType(ParamsSetBean.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(data.getMask());
                    break;
                case 11:
                    bean.setType(ParamsSetBean.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(data.getMac());
                    break;
                case 12:
                    bean.setType(ParamsSetBean.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(data.getHost());
                    break;
                case 13:
                    bean.setType(ParamsSetBean.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(data.getDns());
                    break;
            }
            newlist.add(bean);
            mAdapter.replaceData(newlist);
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
}
