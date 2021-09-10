package com.growatt.chargingpile.adapter;

import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.bean.ChargingBean;

import java.util.List;

/**
 * Created by Administrator on 2019/6/21.
 */

public class RateSetAdapter extends BaseQuickAdapter<ChargingBean.DataBean.PriceConfBean, BaseViewHolder> {
    public RateSetAdapter(int layoutResId, @Nullable List<ChargingBean.DataBean.PriceConfBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ChargingBean.DataBean.PriceConfBean item) {
        TextView start = helper.getView(R.id.start);
        TextView end = helper.getView(R.id.end);
        TextView rateValue = helper.getView(R.id.tv_rate_value);
        String startTime = item.getStartTime();
        String endTime = item.getEndTime();
        if (!TextUtils.isEmpty(startTime)) {
            start.setText(startTime);
        } else {
            start.setText("");
        }

        if (!TextUtils.isEmpty(endTime)) {
            end.setText(endTime);
        } else {
            end.setText("");
        }

        String s = String.valueOf(item.getPrice());
        if (!TextUtils.isEmpty(s)) {
            rateValue.setText(String.valueOf(item.getPrice()));
        } else {
            rateValue.setText("");
        }

        helper.setText(R.id.tv_unit, item.getSymbol());
        helper.addOnClickListener(R.id.iv_delete);
        helper.addOnClickListener(R.id.ll_select_time);
        helper.addOnClickListener(R.id.tv_rate_value);

    }
}
