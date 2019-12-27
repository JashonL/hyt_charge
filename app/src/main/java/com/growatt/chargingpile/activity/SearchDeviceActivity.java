package com.growatt.chargingpile.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.growatt.chargingpile.BaseActivity;
import com.growatt.chargingpile.EventBusMsg.SearchDevMsg;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.bean.ChargingBean;
import com.growatt.chargingpile.connutil.PostUtil;
import com.growatt.chargingpile.util.Mydialog;
import com.growatt.chargingpile.util.SmartHomeUrlUtil;
import com.growatt.chargingpile.util.SmartHomeUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;
import org.xutils.common.util.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchDeviceActivity extends BaseActivity {


    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.ivLeft)
    ImageView ivLeft;
    @BindView(R.id.ivRight)
    ImageView ivRight;
    @BindView(R.id.relativeLayout1)
    RelativeLayout relativeLayout1;
    @BindView(R.id.headerView)
    LinearLayout headerView;
    @BindView(R.id.v_diver_1)
    View vDiver1;
    @BindView(R.id.etSearch)
    TextView etSearch;
    @BindView(R.id.ll_search)
    LinearLayout llSearch;
    @BindView(R.id.v_diver_2)
    View vDiver2;
    @BindView(R.id.btConfirm)
    Button btConfirm;


    private String devSn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_device);
        ButterKnife.bind(this);
        initViews();
    }

    private void initViews() {
        ivLeft.setImageResource(R.drawable.back);
        tvTitle.setText(getString(R.string.m搜索电桩));
        tvTitle.setTextColor(ContextCompat.getColor(this, R.color.title_bg_white));
    }


    @OnClick({R.id.btConfirm,R.id.ivLeft})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.btConfirm:
                searchPlant();
                break;
            case R.id.ivLeft:
                finish();
                break;
        }
    }


    private void searchPlant() {
        devSn = etSearch.getText().toString();
        if (TextUtils.isEmpty(devSn)) devSn = "";
        freshData();
    }


    /**
     * 刷新列表数据
     * position :刷新列表时选中第几项
     * millis
     */
    private void freshData() {
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("userId", SmartHomeUtil.getUserName());//测试id
        if (!TextUtils.isEmpty(devSn)){
            jsonMap.put("chargeId", devSn);
        }
        jsonMap.put("cmd", "list");
        jsonMap.put("lan", getLanguage());
        String json = SmartHomeUtil.mapToJsonString(jsonMap);
        LogUtil.i(json);
        Mydialog.Show(this);
        PostUtil.postJson(SmartHomeUrlUtil.postByCmd(), json, new PostUtil.postListener() {
            @Override
            public void Params(Map<String, String> params) {

            }

            @Override
            public void success(String json) {
                Mydialog.Dismiss();
                try {
                    List<ChargingBean.DataBean> charginglist = null;
                    JSONObject object = new JSONObject(json);
                    if (object.getInt("code") == 0) {
                        ChargingBean chargingListBean = new Gson().fromJson(json, ChargingBean.class);
                        if (chargingListBean!=null){
                            charginglist = chargingListBean.getData();
                            if (charginglist==null)charginglist=new ArrayList<>();
                        }
                    }
                    if (charginglist==null||charginglist.size()==0){
                        toast(R.string.m搜索结果为空);
                    }else {
                        SearchDevMsg msg=new SearchDevMsg();
                        msg.setDevSn(devSn);
                        EventBus.getDefault().post(msg);
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void LoginError(String str) {
                Mydialog.Dismiss();
            }
        });

    }

}
