package com.growatt.chargingpile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.growatt.chargingpile.BaseActivity;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.bean.ChargingBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ChargingSetActivity extends BaseActivity {

    @BindView(R.id.headerView)
    LinearLayout headerView;

    @BindView(R.id.tvTitle)
    TextView tvTitle;
    private String chargingId;

    private List<ChargingBean.DataBean.PriceConfBean> priceConfBeanList;
    private Unbinder bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charging_set);
        bind=ButterKnife.bind(this);
        initIntent();
        initHeadView();
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

    @OnClick({R.id.rl_params_setting, R.id.rl_charging_grant})
    public void buttonClick(View view) {
        switch (view.getId()) {
            case R.id.rl_params_setting:
                Intent intent1=new Intent(this,ChargingParamsActivity.class);
                intent1.putExtra("sn",chargingId);
                intent1.putParcelableArrayListExtra("rate", (ArrayList<? extends Parcelable>) priceConfBeanList);
                jumpTo(intent1, false);
                break;
            case R.id.rl_charging_grant:
                Intent intent2=new Intent(this,ChargingAuthorizationActivity.class);
                intent2.putExtra("sn",chargingId);
                jumpTo(intent2, false);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bind!=null)bind.unbind();
    }
}
