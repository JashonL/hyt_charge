package com.growatt.chargingpile.adapter;

import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.bean.ReservationBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Administrator on 2018/10/22.
 */

public class TimingAdapter extends BaseQuickAdapter<ReservationBean.DataBean, BaseViewHolder> {
    public TimingAdapter(@Nullable List<ReservationBean.DataBean> data) {
        super(R.layout.item_timing_list, data);
    }

    @Override
    protected void convert(final BaseViewHolder helper, ReservationBean.DataBean item) {
        String expiryDate = item.getExpiryDate();//开始时间
        int cValue = (int) Float.parseFloat(item.getcValue2());
        String endDate = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        try {
            Date startDate = sdf.parse(expiryDate);
            long endDateValue = startDate.getTime() + cValue * 60 * 1000;
            Date endTime = new Date(endDateValue);
            endDate = sdf.format(endTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        final int hour = cValue / 60;
        int min = cValue % 60;
        String status = item.getStatus();
        if (!TextUtils.isEmpty(expiryDate)) {
            helper.setText(R.id.tv_start_time, expiryDate.substring(11, 16));
        }
        if (!TextUtils.isEmpty(endDate)) {
            helper.setText(R.id.tv_stop_time, endDate.substring(11, 16));
        }
        helper.setText(R.id.tvDuration, hour + "h" + min + "min");

        final ImageView ivEveryDay = helper.getView(R.id.iv_everyday);
        final ImageView ivSwitch = helper.getView(R.id.iv_switch);
        if (item.getLoopType() != -1) {
            ivEveryDay.setBackgroundResource(R.drawable.sign_protocal_checked);
        } else {
            ivEveryDay.setBackgroundResource(R.drawable.sign_protocal_not_check);
        }
        if (status.equals("Accepted")) {
            ivSwitch.setBackgroundResource(R.drawable.checkbox_on);
        } else {
            ivSwitch.setBackgroundResource(R.drawable.checkbox_off);
        }
        helper.addOnClickListener(R.id.rl_every_day);
        helper.addOnClickListener(R.id.rl_switch);
    }
}
