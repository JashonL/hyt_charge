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
 * 基本信息
 */
public class BasicInfoActivity extends BaseActivity{
    @BindView(R.id.headerView)
    LinearLayout mHeaderView;
    @BindView(R.id.tvTitle)
    TextView mTvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_info);
        ButterKnife.bind(this);
        initToolBar();
        initView();
    }

    private void initView() {


    }

    @Override
    protected void initToolBar() {
        setHeaderImage(mHeaderView, R.drawable.back, Position.LEFT, v -> {
            finish();
        });
        mTvTitle.setTextColor(ContextCompat.getColor(this, R.color.black));
        mTvTitle.setText(getString(R.string.basic_information));
    }

}