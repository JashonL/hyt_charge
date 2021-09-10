package com.growatt.chargingpile.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.bean.ChargingBean;

import java.util.List;

/**
 * 充电费率
 */
public class ChargingRatesAdapter extends BaseQuickAdapter<ChargingBean.DataBean.PriceConfBean, BaseViewHolder> {

    private String mSymbol;

    private Context mContext;

    public ChargingRatesAdapter(@Nullable List<ChargingBean.DataBean.PriceConfBean> data, Context context) {
        super(R.layout.item_charing_rates, data);
        this.mContext = context;
    }


    public void setSymbol(String symbol) {
        mSymbol = symbol;
        notifyDataSetChanged();
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, ChargingBean.DataBean.PriceConfBean item) {
        TextView tvName = holder.getView(R.id.tv_name);
        TextView tvTime = holder.getView(R.id.tv_time);
        TextView tvRate = holder.getView(R.id.tv_rate);
        int position = holder.getLayoutPosition();
        if (position != 0) {
            tvName.setVisibility(View.GONE);
        }

        String time =  mContext.getString(R.string.m326时间段)+" :" + item.getTimeX();
        tvTime.setText(time);
        String rate =mContext.getString(R.string.m327费率)+" :" + mSymbol + item.getPrice();
        tvRate.setText(rate);

    }


}
