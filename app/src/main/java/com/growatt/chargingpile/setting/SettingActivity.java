package com.growatt.chargingpile.setting;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.growatt.chargingpile.BaseActivity;
import com.growatt.chargingpile.EventBusMsg.RefreshRateMsg;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.activity.ConnetWiFiActivity;
import com.growatt.chargingpile.activity.WifiSetGuideActivity;
import com.growatt.chargingpile.adapter.SettingAdapter;
import com.growatt.chargingpile.bean.ChargingBean;
import com.growatt.chargingpile.bean.GunBean;
import com.growatt.chargingpile.bean.NoConfigBean;
import com.growatt.chargingpile.connutil.PostUtil;
import com.growatt.chargingpile.model.SettingModel;
import com.growatt.chargingpile.util.Cons;
import com.growatt.chargingpile.util.Constant;
import com.growatt.chargingpile.util.Mydialog;
import com.growatt.chargingpile.util.SharedPreferencesUnit;
import com.growatt.chargingpile.util.SmartHomeUrlUtil;
import com.growatt.chargingpile.util.SmartHomeUtil;
import com.growatt.chargingpile.view.DividerItemDecoration;

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

/**
 * 设置
 */
public class SettingActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener {
    @BindView(R.id.headerView)
    LinearLayout mHeaderView;
    @BindView(R.id.tvTitle)
    TextView mTvTitle;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private List<Map<String, Object>> dataList = new ArrayList<>();
    private SettingAdapter mSettingAdapter;

    private String mChargingId;
    private List<ChargingBean.DataBean.PriceConfBean> mPriceConfBeanList;

    public GunBean mCurrentGunBean;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshAdapter(RefreshRateMsg msg) {
        if (msg.getPriceConfBeanList() != null) {
            mPriceConfBeanList = msg.getPriceConfBeanList();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        initToolBar();
        initRecyclerView();
        initIntent();
        SettingModel.getInstance().requestConfigParams();
    }

    private void initIntent() {
        mChargingId = getIntent().getStringExtra("sn");
        mPriceConfBeanList = getIntent().getParcelableArrayListExtra("rate");
        if (mPriceConfBeanList == null) mPriceConfBeanList = new ArrayList<>();
        mCurrentGunBean = getIntent().getParcelableExtra("gunBean");
    }

    private void initRecyclerView() {
        int[] titles = new int[]{R.string.basic_information, R.string.network_setting,
                R.string.m105桩体设置, R.string.load_balancing, R.string.m142授权管理, R.string.ap_model};
        int[] images = new int[]{R.drawable.ic_basic_information, R.drawable.ic_network_setting,
                R.drawable.ic_pile_setting, R.drawable.ic_load_balancing, R.drawable.ic_permissions, R.drawable.ic_ap};
        for (int i = 0; i < titles.length; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("title", getResources().getString(titles[i]));
            map.put("image", images[i]);
            dataList.add(map);
        }
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mSettingAdapter = new SettingAdapter(dataList);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.setAdapter(mSettingAdapter);
        int dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen.xa2);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST, false, R.color.translate, dimensionPixelSize);
        recyclerView.addItemDecoration(dividerItemDecoration);

        mSettingAdapter.setOnItemClickListener(this);
    }

    @Override
    protected void initToolBar() {
        setHeaderImage(mHeaderView, R.drawable.back, Position.LEFT, v -> {
            finish();
        });
        mTvTitle.setTextColor(ContextCompat.getColor(this, R.color.black));
        mTvTitle.setText(getString(R.string.setting));
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Intent intent = null;
        switch (position) {
            case 0:
                intent = new Intent(this, BasicInfoActivity.class);
                break;
            case 1:
                intent = new Intent(this, NetSettingActivity.class);
                break;
            case 2:
                intent = new Intent(this, PileSettingActivity.class);
                intent.putParcelableArrayListExtra("rate", (ArrayList<? extends Parcelable>) mPriceConfBeanList);
                break;
            case 3:
                intent = new Intent(this, LoadBalancingActivity.class);
                break;
            case 4:
                intent = new Intent(this, PermissionsActivity.class);
                break;
            case 5:
                if (Cons.getNoConfigBean() == null) {
                    getNoConfigParams();
                } else {
                    toConfig();
                }
                return;
            default:
                break;
        }
        intent.putExtra("chargingId", mChargingId);
        jumpTo(intent, false);

    }

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
                NoConfigBean bean = null;
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

    /**
     * 进入WiFi直连设置
     */
    private void toConfig() {
        boolean isGuide = SharedPreferencesUnit.getInstance(this).getBoolean(Constant.WIFI_GUIDE_KEY);
        Class activity;
        if (isGuide) {
            activity = ConnetWiFiActivity.class;
        } else {
            activity = WifiSetGuideActivity.class;
        }
        Intent intent5 = new Intent(this, activity);
        intent5.putExtra("sn", mChargingId);
        int online;
        if (mCurrentGunBean != null && GunBean.UNAVAILABLE.equals(mCurrentGunBean.getData().getStatus())) {
            online = 1;
        } else {
            online = 0;
        }
        intent5.putExtra("online", online);
        intent5.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        jumpTo(intent5, false);
    }

}