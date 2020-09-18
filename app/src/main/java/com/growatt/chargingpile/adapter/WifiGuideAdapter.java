package com.growatt.chargingpile.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.bean.WifiGuideBean;

import java.util.List;

/**
 * Created by Administrator on 2019/6/17.
 */

public class WifiGuideAdapter extends BaseQuickAdapter<WifiGuideBean,BaseViewHolder>{
    public WifiGuideAdapter(int layoutResId, @Nullable List<WifiGuideBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, WifiGuideBean item) {
        helper.setText(R.id.tv_number,String.valueOf(item.getNum()));
        helper.setText(R.id.tv_des,item.getDes());
        helper.setImageResource(R.id.iv_imageview,item.getResid());
    }
}
