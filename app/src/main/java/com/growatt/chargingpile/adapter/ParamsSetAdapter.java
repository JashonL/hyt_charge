package com.growatt.chargingpile.adapter;

import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.bean.ChargingBean;
import com.growatt.chargingpile.bean.ParamsSetBean;
import com.growatt.chargingpile.bean.SolarBean;

import java.util.List;

/**
 * Created by Administrator on 2018/10/23.
 */

public class ParamsSetAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {
    public static final int PARAM_TITILE = 0;
    public static final int PARAM_ITEM = 1;
    public static final int PARAM_ITEM_CANT_CLICK = 2;
    public static final int PARAM_ITEM_RATE = 3;
    public static final int PARAM_ITEM_SOLAR = 4;

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public ParamsSetAdapter(List<MultiItemEntity> data) {
        super(data);
        addItemType(PARAM_TITILE, R.layout.item_params_set_title);
        addItemType(PARAM_ITEM, R.layout.item_params_set_layout);
        addItemType(PARAM_ITEM_CANT_CLICK, R.layout.item_params_set_layout);
        addItemType(PARAM_ITEM_RATE, R.layout.item_params_rate_layout);
        addItemType(PARAM_ITEM_SOLAR, R.layout.item_set_solar);
    }

    @Override
    protected void convert(BaseViewHolder holder, MultiItemEntity item) {
        switch (holder.getItemViewType()) {
            case PARAM_TITILE:
                holder.setText(R.id.tv_title, ((ParamsSetBean) item).getTitle());
                break;
            case PARAM_ITEM:
                String rate = mContext.getString(R.string.m152充电费率);
                holder.setText(R.id.tv_key, ((ParamsSetBean) item).getKey());
                holder.getView(R.id.iv_more1).setVisibility(View.VISIBLE);
                if (((ParamsSetBean) item).getValue() == null) {
                    holder.setText(R.id.tv_value, "");
                } else {
                    holder.setText(R.id.tv_value, ((ParamsSetBean) item).getValue().toString());
                }
                break;
            case PARAM_ITEM_CANT_CLICK:
                holder.setVisible(R.id.iv_more1, false);
                holder.setText(R.id.tv_key, ((ParamsSetBean) item).getKey());
                if (TextUtils.isEmpty(((ParamsSetBean) item).getValue().toString())) {
                    holder.setText(R.id.tv_value, "");
                } else {
                    holder.setText(R.id.tv_value, ((ParamsSetBean) item).getValue().toString());
                }
                break;
            case PARAM_ITEM_RATE:

                String key = mContext.getString(R.string.m326时间段) + ((ChargingBean.DataBean.PriceConfBean) item).getTimeX();
                holder.setText(R.id.tv_time_key, key);
                String value = mContext.getString(R.string.m327费率) + ((ChargingBean.DataBean.PriceConfBean) item).getPrice();
                if(!TextUtils.isEmpty(value)){
                    value += ((ChargingBean.DataBean.PriceConfBean) item).getSymbol();
                }
                holder.setText(R.id.tv_time_value, value);
                break;
            case PARAM_ITEM_SOLAR:
                SolarBean bean = (SolarBean) item;
                String key1 = bean.getKey();
                holder.setText(R.id.tv_key, key1);
                String value1 = bean.getValue();
                holder.setText(R.id.tv_value, value1);
                break;
        }
    }

}
