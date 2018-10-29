package com.growatt.chargingpile.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.growatt.chargingpile.BaseActivity;
import com.growatt.chargingpile.R;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChargingSetActivity extends BaseActivity {

    @BindView(R.id.headerView)
    LinearLayout headerView;

    @BindView(R.id.tvTitle)
    TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charging_set);
        ButterKnife.bind(this);
        initHeadView();
    }

    private void initHeadView() {
        setHeaderImage(headerView, R.drawable.back, Position.LEFT, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvTitle.setTextColor(ContextCompat.getColor(this, R.color.title_1));
        tvTitle.setText("桩体设置");
    }

    @OnClick({R.id.rl_params_setting, R.id.rl_charging_grant})
    public void buttonClick(View view) {
        switch (view.getId()) {
            case R.id.rl_params_setting:
                jumpTo(ChargingParamsActivity.class, false);
                break;
            case R.id.rl_charging_grant:
                jumpTo(ChargingAuthorizationActivity.class, false);
                break;
        }
    }

}
