package com.growatt.chargingpile.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.bean.ChargingBean;
import com.growatt.chargingpile.util.MyUtil;

import java.util.Collection;
import java.util.List;

/**
 * Created by Administrator on 2018/10/18.
 */

public class ChargingListAdapter extends BaseQuickAdapter<ChargingBean.DataBean, BaseViewHolder>{

    //当前选择的item
    private int nowSelectPosition = -1;

    public ChargingListAdapter(@Nullable List<ChargingBean.DataBean> data) {
        super(R.layout.item_charging_list,data);
    }


    @Override
    protected void convert(BaseViewHolder helper, ChargingBean.DataBean item) {
        LinearLayout llitemContainer = helper.getView(R.id.rl_item_container);
        ImageView ivIcon = helper.getView(R.id.iv_icon);
        TextView tvName = helper.getView(R.id.tv_name);

        LinearLayout llAdd = helper.getView(R.id.ll_add);
        ImageView ivAdd = helper.getView(R.id.iv_add);
        TextView tvAdd = helper.getView(R.id.tv_add);

        String devName = item.getName();

        if (helper.getAdapterPosition() == getItemCount() - 1) {
            MyUtil.hideAllView(View.GONE, llitemContainer);
            MyUtil.showAllView(llAdd);
            ivAdd.setImageResource(R.drawable.add);
            tvAdd.setText(mContext.getText(R.string.m133添加));
        } else {
            MyUtil.hideAllView(View.GONE, llAdd);
            MyUtil.showAllView(llitemContainer);
            if (item.isChecked()) {
                llitemContainer.setBackgroundResource(R.drawable.shape_white_translate40_corner_bg);
            } else {
                llitemContainer.setBackgroundResource(R.drawable.shape_white_translate10_corner_bg);
            }
            tvName.setText(devName);
            ivIcon.setImageResource(R.drawable.charging_pile_icon);
        }
    }

    public int getNowSelectPosition() {
        return nowSelectPosition;
    }



    public void setNowSelectPosition(int position) {
        if (position >= getItemCount()) return;
        //去除其他item选择
        try {
            //不相等时才去除之前选中item以及赋值，防止重复操作
            if (this.nowSelectPosition != position) {
                if (this.nowSelectPosition >=0 && this.nowSelectPosition < getItemCount()) {
                    ChargingBean.DataBean itemPre = getItem(nowSelectPosition);
                    if (itemPre==null)return;
                    itemPre.setChecked(false);
                }

                this.nowSelectPosition = position;
            }
            ChargingBean.DataBean itemNow = getItem(nowSelectPosition);
            if (itemNow==null)return;
            //只有没被选中才刷新数据
            if (!itemNow.isChecked()) {
                itemNow.setChecked(true);
                notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void replaceData(@NonNull Collection<? extends ChargingBean.DataBean> data) {
        super.replaceData(data);
        int nowPos = 0;
        if (nowSelectPosition >= 0 && nowSelectPosition < data.size()){
            nowPos = nowSelectPosition;
        }
        setNowSelectPosition(nowPos);
    }


}
