package com.growatt.chargingpile.adapter;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.bean.ChargingBean;

import java.util.List;

/**
 * Created by Administrator on 2019/6/21.
 */

public class RateSetAdapter extends BaseQuickAdapter<ChargingBean.DataBean.PriceConfBean,BaseViewHolder> {
    public RateSetAdapter(int layoutResId, @Nullable List<ChargingBean.DataBean.PriceConfBean> data) {
        super(layoutResId, data);
    }


    @Override
    protected void convert(BaseViewHolder helper, ChargingBean.DataBean.PriceConfBean item) {
        TextView timeValue = helper.getView(R.id.tv_select_time);
        TextView rateValue = helper.getView(R.id.tv_rate_value);
        if (!TextUtils.isEmpty(item.getTimeX())){
            timeValue.setText(item.getTimeX());
            rateValue.setText(String.valueOf(item.getPrice()));
        }else {
            timeValue.setText("");
            rateValue.setText("");
        }
        helper.setText(R.id.tv_unit,item.getSymbol());
        helper.addOnClickListener(R.id.fl_delete);
        helper.addOnClickListener(R.id.tv_select_time);
        helper.addOnClickListener(R.id.tv_rate_value);

    }
}
