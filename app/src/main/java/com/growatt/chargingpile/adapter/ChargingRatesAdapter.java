package com.growatt.chargingpile.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.growatt.chargingpile.R;

import java.util.List;

/**
 * 充电费率
 */
public class ChargingRatesAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public ChargingRatesAdapter(@Nullable List<String> data) {
        super(R.layout.item_charing_rates, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, String item) {
        helper.setText(R.id.tv_name, item);
    }
}
