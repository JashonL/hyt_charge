package com.growatt.chargingpile.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.bean.ChargingBean;

import java.util.Collection;
import java.util.List;

/**
 * Created by Administrator on 2018/10/18.
 */

public class ChargingListAdapter extends BaseQuickAdapter<ChargingBean.DataBean, BaseViewHolder> {

    //当前选择的item
    private int nowSelectPosition = -1;

    public ChargingListAdapter(@Nullable List<ChargingBean.DataBean> data) {
        super(R.layout.item_charging_list_new, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ChargingBean.DataBean item) {
        LinearLayout llitemContainer = helper.getView(R.id.rl_item_container);
        //ImageView ivIcon = helper.getView(R.id.iv_icon);
        TextView tvName = helper.getView(R.id.tv_name);
        ImageView ivState = helper.getView(R.id.iv_state);
        TextView tvState = helper.getView(R.id.tv_state);
        String devName = item.getName();
        tvName.setText(devName);
        setState(item, tvState,ivState);
    }

    private void setState(ChargingBean.DataBean item, TextView tvState, ImageView ivState) {
        Log.d(TAG, "getStatus_1:" + item.getStatus_1());
        switch (item.getStatus_1()) {
            case "Charging":
                tvState.setText("充电中");
                tvState.setBackgroundResource(R.drawable.shape_recharg_state_bg);
                ivState.setImageResource(R.drawable.ic_recharg_state);
                break;
            case "Finishing":
                tvState.setText("充电结束");
                tvState.setBackgroundResource(R.drawable.shape_end_of_charging_state_bg);
                ivState.setImageResource(R.drawable.ic_end_of_charging);
                break;
            case "Available":
                tvState.setText("idle");
                tvState.setBackgroundResource(R.drawable.shape_end_of_charging_state_bg);
                ivState.setImageResource(R.drawable.ic_idle);
                break;
            case "SuspendedEV":
                tvState.setText("不可用车拒绝充电");
                break;
            case "SuspendedEVSE":
                tvState.setText("不可用桩拒绝充电");
                break;
            case "Faulted":
                tvState.setText("故障");
                break;
            case "unknow":
                tvState.setText("unknow");
                break;
            case "Unavailable":
                tvState.setText("异常");
                tvState.setBackgroundResource(R.drawable.shape_default_state_bg);
                ivState.setImageResource(R.drawable.ic_unavailable);
                break;
            default:
                break;
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
                if (this.nowSelectPosition >= 0 && this.nowSelectPosition < getItemCount()) {
                    ChargingBean.DataBean itemPre = getItem(nowSelectPosition);
                    if (itemPre == null) return;
                    itemPre.setChecked(false);
                }

                this.nowSelectPosition = position;
            }
            ChargingBean.DataBean itemNow = getItem(nowSelectPosition);
            if (itemNow == null) return;
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
        if (nowSelectPosition >= 0 && nowSelectPosition < data.size()) {
            nowPos = nowSelectPosition;
        }
        setNowSelectPosition(nowPos);
    }

}
