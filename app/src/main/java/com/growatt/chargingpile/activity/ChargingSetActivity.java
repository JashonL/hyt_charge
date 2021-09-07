package com.growatt.chargingpile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.growatt.chargingpile.BaseActivity;
import com.growatt.chargingpile.EventBusMsg.SearchDevMsg;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.bean.ChargingBean;
import com.growatt.chargingpile.bean.NoConfigBean;
import com.growatt.chargingpile.connutil.PostUtil;
import com.growatt.chargingpile.setting.PermissionsActivity;
import com.growatt.chargingpile.util.Cons;
import com.growatt.chargingpile.util.Mydialog;
import com.growatt.chargingpile.util.SmartHomeUrlUtil;
import com.growatt.chargingpile.util.SmartHomeUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;
import org.xutils.common.util.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChargingSetActivity extends BaseActivity {

    @BindView(R.id.headerView)
    LinearLayout headerView;

    @BindView(R.id.tvTitle)
    TextView tvTitle;

    @BindView(R.id.rl_charging_search)
    RelativeLayout rlSearch;

    private String chargingId;

    private List<ChargingBean.DataBean.PriceConfBean> priceConfBeanList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charging_set);
        ButterKnife.bind(this);
        initIntent();
        initHeadView();
        initViews();
    }

    private void initViews() {
        if ("endUser".equals(SmartHomeUtil.getUserAuthority())){
            rlSearch.setVisibility(View.GONE);
        }else {
            rlSearch.setVisibility(View.VISIBLE);
        }
    }

    private void initIntent() {
        chargingId=getIntent().getStringExtra("sn");
        priceConfBeanList = getIntent().getParcelableArrayListExtra("rate");
        if (priceConfBeanList == null) priceConfBeanList = new ArrayList<>();
    }

    private void initHeadView() {
        setHeaderImage(headerView, R.drawable.back, Position.LEFT, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvTitle.setTextColor(ContextCompat.getColor(this, R.color.title_1));
        tvTitle.setText(getString(R.string.m105桩体设置));
    }

    @OnClick({R.id.rl_params_setting, R.id.rl_charging_grant,R.id.rl_charging_search})
    public void buttonClick(View view) {
        switch (view.getId()) {
            case R.id.rl_params_setting:
                if (Cons.getNoConfigBean()==null){
                    getNoConfigParams();
                }else {
                    toConfig();
                }
                break;
            case R.id.rl_charging_grant:
                Intent intent2=new Intent(this, PermissionsActivity.class);
                intent2.putExtra("sn",chargingId);
                jumpTo(intent2, false);
                break;
            case R.id.rl_charging_search:
                Intent intent3=new Intent(this,SearchDeviceActivity.class);
                jumpTo(intent3, false);
                break;
        }
    }

    private void toConfig() {
      Intent intent1=new Intent(this,ChargingParamsActivity.class);
      intent1.putExtra("sn",chargingId);
      intent1.putParcelableArrayListExtra("rate", (ArrayList<? extends Parcelable>) priceConfBeanList);
      jumpTo(intent1, false);
    }


    /**
     * 获取需要密码的设置项
     */
    private void getNoConfigParams() {
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("userId", SmartHomeUtil.getUserName());//测试id
        jsonMap.put("cmd", "noConfig");
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
                NoConfigBean bean=null;
                try {
                    JSONObject object = new JSONObject(json);
                    if (object.getInt("code") == 0) {
                        JSONObject jsonObject = object.optJSONObject("data");
                        bean = new Gson().fromJson(jsonObject.toString(), NoConfigBean.class);
                    }
                    Cons.setNoConfigBean(bean);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                toConfig();
            }

            @Override
            public void LoginError(String str) {
                Mydialog.Dismiss();
                toConfig();
            }
        });

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void searchFresh(SearchDevMsg msg) {
       finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
