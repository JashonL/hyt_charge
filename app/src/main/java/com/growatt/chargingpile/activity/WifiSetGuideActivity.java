package com.growatt.chargingpile.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_set_gui);
        ButterKnife.bind(this);
        saveRecorde();
        initResource();
        initHeaderViews();
        initRecycleView();
    }

    private void saveRecorde() {
        boolean isGuide = SharedPreferencesUnit.getInstance(this).getBoolean(Constant.WIFI_GUIDE_KEY);
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
                finish();
                break;
        }
    }
}
