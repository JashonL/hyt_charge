package com.growatt.chargingpile.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.bean.GunBean;

import java.util.List;


public class GunSwitchAdapter extends BaseQuickAdapter<GunBean.DataBean,BaseViewHolder> {

    public GunSwitchAdapter(@Nullable List<GunBean.DataBean> data) {
        super(R.layout.spinner_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, GunBean.DataBean item) {
        int connectorId = item.getConnectorId();
        helper.setText(R.id.textView1,item.getName());
    }
}
