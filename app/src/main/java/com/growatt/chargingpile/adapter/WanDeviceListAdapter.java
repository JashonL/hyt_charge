package com.growatt.chargingpile.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.bean.UdpSearchBean;

import java.util.List;

/**
 * Created by Administrator on 2018/10/19.
 */

public class WanDeviceListAdapter extends BaseQuickAdapter<UdpSearchBean,BaseViewHolder>{

    public WanDeviceListAdapter(int layoutResId, @Nullable List<UdpSearchBean> data) {
        super(layoutResId, data);
    }


    @Override
    protected void convert(BaseViewHolder helper,UdpSearchBean item) {
        helper.setText(R.id.tvName,item.getDevName());
        helper.setText(R.id.tvIp, item.getDevIp());
    }
}
