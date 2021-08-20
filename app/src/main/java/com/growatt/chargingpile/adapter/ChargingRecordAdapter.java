package com.growatt.chargingpile.adapter;

import androidx.annotation.Nullable;

import android.text.TextUtils;
import android.util.Log;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.bean.ChargingRecordBean;
import com.growatt.chargingpile.util.MathUtil;
import com.growatt.chargingpile.util.SmartHomeUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * Created by Administrator on 2018/6/4.
 */

public class ChargingRecordAdapter extends BaseQuickAdapter<ChargingRecordBean.DataBean, BaseViewHolder> {

    public ChargingRecordAdapter(@Nullable List<ChargingRecordBean.DataBean> data) {
        super(R.layout.item_charging_record111, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ChargingRecordBean.DataBean item) {
        String name = item.getName();
        Log.d(TAG, "convert: name:"+name);
        if (TextUtils.isEmpty(name)) name = item.getChargeId();
        helper.setText(R.id.tv_name, name);
        helper.setText(R.id.tv_chargingId, item.getChargeId());

        //Log.d(TAG, "convert: tv_chargingId:"+item.getChargeId());

        int connectorId = item.getConnectorId();

        if (connectorId < 0) {
            connectorId = 1;
        }
        String gunName = SmartHomeUtil.getLetter().get(connectorId - 1) + " " + mContext.getString(R.string.枪);
        helper.setText(R.id.tv_model, gunName);
        Log.d(TAG, "convert:gunName="+gunName);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
  /*      long cTime = item.getCtime() * 60 * 1000;
        long sysStartTime = item.getSysStartTime();
        long sysEndTime = sysStartTime + cTime;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE);
        Date startDate = new Date(sysStartTime);
        Date endDate = new Date(sysEndTime);
        String startTime = sdf.format(startDate);
        String endTime = sdf.format(endDate);*/
        String startTime = item.getStarttime();
        Date sysStartTime = null;
        Date sysEndTime = null;
        if (!TextUtils.isEmpty(startTime)) {
            //helper.setText(R.id.tv_calendar, startTime.substring(0, 11));
            Log.d(TAG, "convert:startTime:"+startTime.substring(11, 16));
            helper.setText(R.id.tv_start, startTime.substring(11, 16));
            try {
                sysStartTime = sdf.parse(startTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        String endTime = item.getEndtime();
        if (!TextUtils.isEmpty(endTime)) {
            Log.d(TAG, "convert: endTime:"+endTime);
            helper.setText(R.id.tv_end_date, endTime.substring(0, 11));
            helper.setText(R.id.tv_end, endTime.substring(11, 16));
            try {
                sysEndTime = sdf.parse(endTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            ;
        }
        long durationTime = item.getCtime();
        if (sysStartTime != null && sysEndTime != null) {
            durationTime = sysEndTime.getTime() - sysStartTime.getTime();
        }

        // 计算差多少小时
        int hour = (int) (durationTime / (1000 * 60 * 60));
        // 计算差多少分钟
        int min = (int) ((durationTime % (1000 * 60 * 60)) / (60 * 1000));
        //计算多少秒
        int ss = (int) ((durationTime % (1000 * 60)) / 1000);
        String stringDuration = hour+"";
        helper.setText(R.id.tv_duration, stringDuration);

        String stringMin = min+"";
        helper.setText(R.id.tv_min, stringMin);

        String energy = MathUtil.roundDouble2String(item.getEnergy(), 2);
        helper.setText(R.id.tv_ele, energy);
        String money = MathUtil.roundDouble2String(item.getCost(), 2);
        if (!TextUtils.isEmpty(item.getSymbol())) {
            money += item.getSymbol();
        }
        helper.setText(R.id.tv_money, money);
    }
}
