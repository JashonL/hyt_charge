package com.growatt.chargingpile.adapter;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.growatt.chargingpile.R;

import java.util.List;
import java.util.Map;

public class SettingAdapter extends BaseQuickAdapter<Map<String, Object>, BaseViewHolder> {

    public SettingAdapter(@Nullable List<Map<String, Object>> data) {
        super(R.layout.item_setting, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, Map<String, Object> item) {
        int position = helper.getLayoutPosition();
        Log.d(TAG, "convert: " + position);
        helper.setText(R.id.tv_name, item.get("title").toString());
        helper.setImageResource(R.id.iv_icon, (Integer) item.get("image"));
    }

}
