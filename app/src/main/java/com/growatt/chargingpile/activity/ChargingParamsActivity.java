package com.growatt.chargingpile.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
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
import com.growatt.chargingpile.util.Mydialog;
import com.growatt.chargingpile.util.SmartHomeUrlUtil;
import com.growatt.chargingpile.util.SmartHomeUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ChargingParamsActivity extends BaseActivity {
    @BindView(R.id.headerView)
    View headerView;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private TextView tvId;
    private TextView tvKeys;


    private ParamsSetAdapter mAdapter;
    private List<ParamsSetBean> list = new ArrayList<>();
    private LinearLayoutManager mLinearLayoutManager;
    private String[] keys;
    private View paramHeadView;


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
    }

    private void setOnclickListener() {
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (position == 0 || position == 7) return;
                switch (position) {
                    case 1:
                        inputEdit("name");
                        break;
                    case 2:
                        inputEdit("address");
                        break;
                    case 3:
                        inputEdit("address");
                        break;
                    case 4:
                        inputEdit("rate");
                        break;
                    case 5:
                        inputEdit("power");
                        break;
                    case 6:
                        inputEdit("model");
                        break;
                    case 8:
                        inputEdit("ip");
                        break;
                    case 9:
                        inputEdit("gateway");
                        break;
                    case 10:
                        inputEdit("mask");
                        break;
                    case 11:
                        inputEdit("mac");
                        break;
                    case 12:
                        inputEdit("host");
                        break;
                    case 13:
                        inputEdit("dns");
                        break;
                }

            }
        });
    }

    private void initResource() {
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
        setHeaderTitle(headerView,  getString(R.string.m141参数设置), R.color.title_1, true);
    }


    /**
     * 弹框输入修改内容
     * item 修改项
     */
    private void inputEdit(final String key) {
        final EditText et = new EditText(this);
        int dimen = getResources().getDimensionPixelSize(R.dimen.xa24);
        et.setTextSize(TypedValue.COMPLEX_UNIT_PX, dimen);
        et.setPadding(20, 20, 20, 20);
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle(R.string.m27温馨提示)
                .setMessage(getString(R.string.m请输入设置内容)).setView(
                        et).setPositiveButton(R.string.m9确定, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int swich) {
                        String s = et.getText().toString();

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
        jsonMap.put("lan",getLanguage());//测试id
        jsonMap.put(key, value);
        String json = SmartHomeUtil.mapToJsonString(jsonMap);
        PostUtil.postJson(SmartHomeUrlUtil.SET_CHARGING_PARAMS, json, new PostUtil.postListener() {
            @Override
            public void Params(Map<String, String> params) {

            }

            @Override
            public void success(String json) {
                Mydialog.Dismiss();
                refreshDate();
            }

            @Override
            public void LoginError(String str) {

            }
        });
    }


    private void refreshDate() {
        Mydialog.Show(this);
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        jsonMap.put("sn", Cons.mCurrentPile.getChargeId());//测试id
        jsonMap.put("userId", Cons.userBean.getId());//测试id
        jsonMap.put("lan",getLanguage());//测试id
        String json = SmartHomeUtil.mapToJsonString(jsonMap);
        PostUtil.postJson(SmartHomeUrlUtil.REQUEST_CHARGING_PARAMS, json, new PostUtil.postListener() {
            @Override
            public void Params(Map<String, String> params) {

            }

            @Override
            public void success(String json) {
                Mydialog.Dismiss();
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

                } catch (JSONException e) {
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
        tvKeys.setText(data.getSerialNumber());
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
                    bean.setValue(data.getAddress());
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
                    bean.setValue(data.getModel());
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
}
