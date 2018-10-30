package com.growatt.chargingpile.adapter;

import android.text.TextUtils;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.bean.ParamsSetBean;

import java.util.List;

/**
 * Created by Administrator on 2018/10/23.
 */

public class ParamsSetAdapter extends BaseMultiItemQuickAdapter<ParamsSetBean, BaseViewHolder> {


    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public ParamsSetAdapter(List<ParamsSetBean> data) {
        super(data);
        addItemType(ParamsSetBean.PARAM_TITILE, R.layout.item_params_set_title);
        addItemType(ParamsSetBean.PARAM_ITEM, R.layout.item_params_set_layout);
    }

    @Override
    protected void convert(BaseViewHolder helper, ParamsSetBean item) {
        if (item.getItemType() == ParamsSetBean.PARAM_TITILE) {
            helper.setText(R.id.tv_title, item.getTitle());

        } else if (item.getItemType() == ParamsSetBean.PARAM_ITEM) {
            helper.setText(R.id.tv_key, item.getKey());
            if (TextUtils.isEmpty(item.getValue().toString())) {
                helper.setText(R.id.tv_value, "");
            } else {
                helper.setText(R.id.tv_value, item.getValue().toString());
            }
        }
    }
}
