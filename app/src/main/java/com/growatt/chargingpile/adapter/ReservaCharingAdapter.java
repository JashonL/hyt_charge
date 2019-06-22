package com.growatt.chargingpile.adapter;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.TextView;

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
 * Created by Administrator on 2019/6/21.
 */

public class ReservaCharingAdapter extends BaseQuickAdapter<ReservationBean.DataBean, BaseViewHolder> {
    public ReservaCharingAdapter(int layoutResId, @Nullable List<ReservationBean.DataBean> data) {
        super(layoutResId, data);
    }


    @Override
    protected void convert(BaseViewHolder helper, ReservationBean.DataBean item) {
        TextView timeValue = helper.getView(R.id.tv_time);
        TextView rateValue = helper.getView(R.id.tv_rate_value);
        String status = item.getStatus();
        if (status.equals("Accepted")) {
            String expiryDate = item.getExpiryDate();//开始时间
            int cValue = Integer.parseInt(item.getcValue2());
            String endDate = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            try {
                Date startDate = sdf.parse(expiryDate);
                long endDateValue = startDate.getTime() + cValue * 60 * 1000;
                Date endTime = new Date(endDateValue);
                endDate = sdf.format(endTime);
                item.setLoopValue(expiryDate.substring(11, 16));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (!TextUtils.isEmpty(endDate)&&!TextUtils.isEmpty(expiryDate)) {
                String time=expiryDate.substring(11, 16)+"~"+endDate.substring(11, 16);
                timeValue.setText(time);
            }
            String rate=item.getSymbol()+item.getRate()+"/h";
            rateValue.setText(rate);
        }
    }
}
