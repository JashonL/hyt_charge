package com.growatt.chargingpile.adapter;

import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.bean.WifiSetBean;

import java.util.List;

/**
 * Created by Administrator on 2018/10/23.
 */

public class WifiSetAdapter extends BaseMultiItemQuickAdapter<WifiSetBean, BaseViewHolder> {


    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public WifiSetAdapter(List<WifiSetBean> data) {
        super(data);
        addItemType(WifiSetBean.PARAM_TITILE, R.layout.item_params_set_title);
        addItemType(WifiSetBean.PARAM_ITEM, R.layout.item_params_set_layout);
        addItemType(WifiSetBean.PARAM_ITEM_CANT_CLICK, R.layout.item_params_set_layout);
    }

    @Override
    protected void convert(BaseViewHolder helper, WifiSetBean item) {
        if (item.getItemType() == WifiSetBean.PARAM_TITILE) {
            helper.setText(R.id.tv_title, item.getTitle());

        } else if (item.getItemType() == WifiSetBean.PARAM_ITEM) {
            helper.setText(R.id.tv_key, item.getKey());
            TextView value = helper.getView(R.id.tv_value);
           int position= helper.getAdapterPosition();
            if (position==3||position==14||position==16){
                value.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }else {
                value.setTransformationMethod(HideReturnsTransformationMethod.getInstance());  // 设置明文格式
            }
            if (TextUtils.isEmpty(item.getValue().toString())) {
                value.setText("");
            } else {
                value.setText(item.getValue().toString());
            }

        } else {
            helper.getView(R.id.iv_more1).setVisibility(View.GONE);
            helper.setText(R.id.tv_key, item.getKey());
            if (TextUtils.isEmpty(item.getValue().toString())) {
                helper.setText(R.id.tv_value, "");
            } else {
                helper.setText(R.id.tv_value, item.getValue().toString());
            }
        }
    }
}
