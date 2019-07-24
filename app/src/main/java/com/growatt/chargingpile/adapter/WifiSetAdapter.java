package com.growatt.chargingpile.adapter;

import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.bean.SolarBean;
import com.growatt.chargingpile.bean.WifiSetBean;

import java.util.List;

/**
 * Created by Administrator on 2018/10/23.
 */

public class WifiSetAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {
    public static final int PARAM_TITILE = 0;
    public static final int PARAM_ITEM = 1;
    public static final int PARAM_ITEM_CANT_CLICK = 2;
    public static final int PARAM_ITEM_SOLAR = 3;

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public WifiSetAdapter(List<MultiItemEntity> data) {
        super(data);
        addItemType(PARAM_TITILE, R.layout.item_params_set_title);
        addItemType(PARAM_ITEM, R.layout.item_params_set_layout);
        addItemType(PARAM_ITEM_CANT_CLICK, R.layout.item_params_set_layout);
        addItemType(PARAM_ITEM_SOLAR, R.layout.item_set_solar);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {
        if (item.getItemType() == PARAM_TITILE) {
            WifiSetBean bean=(WifiSetBean)item;
            helper.setText(R.id.tv_title, bean.getTitle());

        } else if (item.getItemType() == PARAM_ITEM) {
            WifiSetBean bean=(WifiSetBean)item;
            helper.setText(R.id.tv_key, bean.getKey());
            TextView value = helper.getView(R.id.tv_value);
            int position = helper.getAdapterPosition();
            if (position == 3 || position == 14 || position == 16 || position == 18) {
                value.setTransformationMethod(PasswordTransformationMethod.getInstance());
            } else {
                value.setTransformationMethod(HideReturnsTransformationMethod.getInstance());  // 设置明文格式
            }
            if (TextUtils.isEmpty(bean.getValue().toString())) {
                value.setText("");
            } else {
                value.setText(bean.getValue().toString());
            }

        } else if (item.getItemType() == PARAM_ITEM_SOLAR) {
            SolarBean bean=(SolarBean)item;
            String key = bean.getKey();
            helper.setText(R.id.tv_key, key);
            String value = bean.getValue();
            helper.setText(R.id.tv_value, value);

        } else {
            WifiSetBean bean=(WifiSetBean)item;
            helper.getView(R.id.iv_more1).setVisibility(View.GONE);
            helper.setText(R.id.tv_key, bean.getKey());
            if (TextUtils.isEmpty(bean.getValue().toString())) {
                helper.setText(R.id.tv_value, "");
            } else {
                helper.setText(R.id.tv_value, bean.getValue().toString());
            }
        }
    }
}
