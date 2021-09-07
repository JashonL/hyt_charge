package com.growatt.chargingpile.setting;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.growatt.chargingpile.BaseActivity;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.adapter.ChargingRatesAdapter;
import com.growatt.chargingpile.view.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 桩体设置
 */
public class PileSettingActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener {
    @BindView(R.id.headerView)
    LinearLayout mHeaderView;
    @BindView(R.id.tvTitle)
    TextView mTvTitle;

    @BindView(R.id.rv_charging_rates)
    RecyclerView mRecyclerView;

    private List<String> dataList = new ArrayList<>();

    private ChargingRatesAdapter mChargingRatesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pile_setting);
        ButterKnife.bind(this);
        initToolBar();
        initRecyclerView();
    }

    private void initRecyclerView() {
        dataList.add("充电费率");
        dataList.add("");
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mChargingRatesAdapter = new ChargingRatesAdapter(dataList);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mChargingRatesAdapter);
        int dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen.xa2);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST, false, R.color.translate, dimensionPixelSize);
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mChargingRatesAdapter.setOnItemClickListener(this);
    }

    @Override
    protected void initToolBar() {
        setHeaderImage(mHeaderView, R.drawable.back, Position.LEFT, v -> {
            finish();
        });
        mTvTitle.setTextColor(ContextCompat.getColor(this, R.color.black));
        mTvTitle.setText(getString(R.string.pile_setting));
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

    }
}