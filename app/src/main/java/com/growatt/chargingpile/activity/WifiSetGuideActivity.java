package com.growatt.chargingpile.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.growatt.chargingpile.BaseActivity;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.adapter.WifiGuideAdapter;
import com.growatt.chargingpile.bean.WifiGuideBean;
import com.growatt.chargingpile.util.Constant;
import com.growatt.chargingpile.util.SharedPreferencesUnit;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class WifiSetGuideActivity extends BaseActivity {

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
    @BindView(R.id.rv_gui)
    RecyclerView rvGui;

    private int[] resIds;
    private String[] des;
    private Unbinder bind;
    private boolean isGuide;
    private String devId;
    private int online;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_set_gui);
        bind=ButterKnife.bind(this);
        saveRecorde();
        initResource();
        initHeaderViews();
        initRecycleView();
        devId = getIntent().getStringExtra("sn");
        online=getIntent().getIntExtra("online",0);
    }

    private void saveRecorde() {
        isGuide = SharedPreferencesUnit.getInstance(this).getBoolean(Constant.WIFI_GUIDE_KEY);
        if (!isGuide) {
            SharedPreferencesUnit.getInstance(this).putBoolean(Constant.WIFI_GUIDE_KEY, true);
        }
    }


    private void initResource() {
        if (getLanguage() == 0) {
            resIds = new int[]{R.drawable.ch_1, R.drawable.ch_2, R.drawable.ch_3};
        } else {
            resIds = new int[]{R.drawable.en_1, R.drawable.en_2, R.drawable.en_3};
        }
        des = new String[]{getString(R.string.m309操作引导1), getString(R.string.m310操作引导2), getString(R.string.m311操作引导3)};
    }

    private void initRecycleView() {
        List<WifiGuideBean> list = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            WifiGuideBean bean = new WifiGuideBean();
            bean.setNum(i + 1);
            bean.setDes(des[i]);
            bean.setResid(resIds[i]);
            list.add(bean);
        }
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        WifiGuideAdapter mAdapter = new WifiGuideAdapter(R.layout.item_setwifi_guide, list);
        rvGui.setLayoutManager(mLinearLayoutManager);
        rvGui.setAdapter(mAdapter);
    }

    private void initHeaderViews() {
        ivLeft.setImageResource(R.drawable.back);
        tvTitle.setText(R.string.m312WIFI直连操作指引);
        tvTitle.setTextColor(ContextCompat.getColor(this, R.color.title_1));
    }

    @OnClick(R.id.ivLeft)
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.ivLeft:
                if (isGuide){
                    finish();
                }else {
                    Intent intent5 = new Intent(this, ConnetWiFiActivity.class);
                    intent5.putExtra("sn", devId);
                    intent5.putExtra("online",online);
                    intent5.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    jumpTo(intent5, true);
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
