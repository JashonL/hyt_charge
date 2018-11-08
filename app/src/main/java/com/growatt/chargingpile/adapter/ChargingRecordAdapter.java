package com.growatt.chargingpile.adapter;

import android.support.annotation.Nullable;
import android.util.Log;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.bean.ChargingRecordBean;
import com.growatt.chargingpile.util.MathUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * Created by Administrator on 2018/6/4.
 */

public class ChargingRecordAdapter extends BaseQuickAdapter<ChargingRecordBean.DataBean, BaseViewHolder> {

    public ChargingRecordAdapter(@Nullable List<ChargingRecordBean.DataBean> data) {
        super(R.layout.item_charging_record, data);
    }
    @Override
    protected void convert(BaseViewHolder helper, ChargingRecordBean.DataBean item) {
        helper.setText(R.id.tv_name, item.getName());
        helper.setText(R.id.tv_chargingId, item.getChargeId());
        String gunName;
        if (item.getConnectorId() == 1) {
            gunName = mContext.getString(R.string.m110A枪);
        } else {
            gunName = mContext.getString(R.string.m111B枪);
        }
        helper.setText(R.id.tv_model, gunName);
        long cTime = item.getCtime() * 60 * 1000;
        long sysStartTime = item.getSysStartTime();
        long sysEndTime = sysStartTime + cTime;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startDate = new Date(sysStartTime);
        Date endDate = new Date(sysEndTime);
        String startTime = sdf.format(startDate);
        String endTime = sdf.format(endDate);
        helper.setText(R.id.tv_calendar, startTime.substring(0, 11));
        helper.setText(R.id.tv_start, startTime.substring(11,16));
        helper.setText(R.id.tv_end, endTime.substring(11,16));
        long durationTime = item.getCtime();
        // 计算差多少小时
        long hour = durationTime / 60;
        // 计算差多少分钟
        long min = durationTime % 60;
        String stringDuration = hour + "h" + min + "min";
        helper.setText(R.id.tv_duration, stringDuration);
        String energy = MathUtil.roundDouble2String(item.getEnergy(), 2) + "kWh";
        helper.setText(R.id.tv_ele, energy);
        String money = MathUtil.roundDouble2String(item.getCost(), 2);
        helper.setText(R.id.tv_money, money);
    }
}
