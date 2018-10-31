package com.growatt.chargingpile.adapter;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.bean.ReservationBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2018/10/22.
 */

public class TimingAdapter extends BaseQuickAdapter<ReservationBean.DataBean, BaseViewHolder> {

    private CheckListnerListener mCheckListener;

    public void setCheckListener(CheckListnerListener listener) {
        this.mCheckListener = listener;
    }

    public TimingAdapter(@Nullable List<ReservationBean.DataBean> data) {
        super(R.layout.item_timing_list, data);
    }

    @Override
    protected void convert(final BaseViewHolder helper, ReservationBean.DataBean item) {
        String expiryDate = item.getExpiryDate();//开始时间
        int cValue = item.getCValue();
        String endDate = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        try {
            Date startDate = sdf.parse(expiryDate);
            long endDateValue = startDate.getTime() + cValue * 60 * 1000;
            Date endTime = new Date(endDateValue);
            endDate = sdf.format(endTime);
            item.setLoopValue(expiryDate.substring(11, 16));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        final int hour = cValue / 60;
        int min = cValue % 60;
        String status = item.getStatus();

        if (!TextUtils.isEmpty(expiryDate)){
            helper.setText(R.id.tv_start_time, expiryDate.substring(11, 16));
        }
        if (!TextUtils.isEmpty(endDate)){
            helper.setText(R.id.tv_stop_time, endDate.substring(11, 16));
        }
        helper.setText(R.id.tvDuration, hour + "h" + min + "min");
        if (status.equals("Accepted")) {
            helper.setChecked(R.id.cb_switch, true);
        } else {
            helper.setChecked(R.id.cb_switch, false);
        }
        if (item.getLoopType() != -1) {
            helper.setChecked(R.id.cb_everyday, true);
        } else {
            helper.setChecked(R.id.cb_everyday, false);
        }
        final CheckBox cbEveryDay = helper.getView(R.id.cb_everyday);
        final CheckBox cbSwitch = helper.getView(R.id.cb_switch);
        cbEveryDay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCheckListener.cyclelistener(helper.getAdapterPosition(), cbSwitch.isChecked(), isChecked);
            }
        });
        cbSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCheckListener.switchlistener(helper.getAdapterPosition(), isChecked, cbEveryDay.isChecked());
            }
        });
    }

    public interface CheckListnerListener {
        void switchlistener(int position, boolean isOpen, boolean isCycle);

        void cyclelistener(int position, boolean isOpen, boolean isCycle);
    }
}
