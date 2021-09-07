package com.growatt.chargingpile.setting;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.growatt.chargingpile.BaseActivity;
import com.growatt.chargingpile.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 负载平衡
 */
public class LoadBalancingActivity extends BaseActivity {
    @BindView(R.id.headerView)
    LinearLayout mHeaderView;
    @BindView(R.id.tvTitle)
    TextView mTvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_balancing);
        ButterKnife.bind(this);
        initToolBar();
    }

    @Override
    protected void initToolBar() {
        setHeaderImage(mHeaderView, R.drawable.back, Position.LEFT, v -> {
            finish();
        });
        mTvTitle.setTextColor(ContextCompat.getColor(this, R.color.black));
        mTvTitle.setText(getString(R.string.load_balancing));
    }

}