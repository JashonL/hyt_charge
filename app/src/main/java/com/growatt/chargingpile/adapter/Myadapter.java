package com.growatt.chargingpile.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.growatt.chargingpile.R;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/10/19.
 */

public class Myadapter extends BaseQuickAdapter<Map<String, Object>,BaseViewHolder>{

    public Myadapter(@Nullable List<Map<String, Object>> data) {
        super(R.layout.item_me,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Map<String, Object> item) {
        helper.setText(R.id.textView1,item.get("title").toString());
        helper.setImageResource(R.id.imageView1, (Integer) item.get("image"));
    }
}
