package com.growatt.chargingpile.adapter;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.bean.ChargingBean;
import com.growatt.chargingpile.bean.GunBean;

import java.util.Collection;
import java.util.List;

/**
 * Created by Administrator on 2018/10/18.
 */

public class ChargingListAdapter extends BaseQuickAdapter<ChargingBean.DataBean, BaseViewHolder> {

    //当前选择的item
    private int nowSelectPosition = -1;

    private Context mContext;

    public ChargingListAdapter(@Nullable List<ChargingBean.DataBean> data, Context context) {
        super(R.layout.item_charging_list_new, data);
        mContext = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, ChargingBean.DataBean item) {
        //LinearLayout llitemContainer = helper.getView(R.id.rl_item_container);
        //ImageView ivIcon = helper.getView(R.id.iv_icon);
        TextView tvName = helper.getView(R.id.tv_name);
        ImageView ivState = helper.getView(R.id.iv_state);
        TextView tvState = helper.getView(R.id.tv_state);
        String devName = item.getName();
        tvName.setText(devName);
        setState(item, tvState, ivState);
    }

    private void setState(ChargingBean.DataBean item, TextView tvState, ImageView ivState) {
        Log.d(TAG, "getStatus_1:" + item.getStatus_1());
        switch (item.getStatus_1()) {
            case GunBean.PREPARING://m119准备中
                tvState.setText(mContext.getString(R.string.m119准备中));
                tvState.setBackgroundResource(R.drawable.shape_end_of_charging_state_bg);
                ivState.setImageResource(R.drawable.ic_end_of_charging);
                break;
            case GunBean.CHARGING:
                tvState.setText(mContext.getString(R.string.m118充电中));
                tvState.setBackgroundResource(R.drawable.shape_recharg_state_bg);
                ivState.setImageResource(R.drawable.ic_recharg_state);
                break;
            case GunBean.FINISHING:
                tvState.setText(mContext.getString(R.string.m120充电结束));
                tvState.setBackgroundResource(R.drawable.shape_end_of_charging_state_bg);
                ivState.setImageResource(R.drawable.ic_end_of_charging);
                break;
            case GunBean.AVAILABLE:
                tvState.setText(mContext.getString(R.string.m117空闲));
                tvState.setBackgroundResource(R.drawable.shape_end_of_charging_state_bg);
                ivState.setImageResource(R.drawable.ic_idle);
                break;
            case GunBean.SUSPENDEEV:
                tvState.setText(mContext.getString(R.string.m133车拒绝充电));
                tvState.setBackgroundResource(R.drawable.shape_default_state_bg);
                ivState.setImageResource(R.drawable.ic_unavailable);
                break;
            case GunBean.SUSPENDEDEVSE:
                tvState.setText(mContext.getString(R.string.m292桩拒绝充电));
                tvState.setBackgroundResource(R.drawable.shape_default_state_bg);
                ivState.setImageResource(R.drawable.ic_unavailable);
                break;
            case GunBean.FAULTED:
                tvState.setText(mContext.getString(R.string.m121故障));
                tvState.setBackgroundResource(R.drawable.shape_default_state_bg);
                ivState.setImageResource(R.drawable.ic_unavailable);
                break;
            case GunBean.UNAVAILABLE:
                tvState.setText(mContext.getString(R.string.m122不可用));
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
