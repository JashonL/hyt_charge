package com.growatt.chargingpile.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.bean.ChargingUserBean;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2018/6/4.
 */

public class ChargingUserAdapter extends BaseQuickAdapter<ChargingUserBean.DataBean, BaseViewHolder>  {

    private DeleteListener mDelListener;


    public void setDelListener(DeleteListener listener){
        this.mDelListener=listener;
    }

    public ChargingUserAdapter(@Nullable List<ChargingUserBean.DataBean> data) {
        super(R.layout.item_charging_user_adapter, data);
    }


    @Override
    protected void convert(final BaseViewHolder helper, final ChargingUserBean.DataBean item) {
        //控件
        TextView dayMonth = helper.getView(R.id.tv_dayMonth);
        TextView tvTime = helper.getView(R.id.tv_time);
        TextView tvUsername = helper.getView(R.id.tv_username);
        LinearLayout llDelete = helper.getView(R.id.ll_delete);

        //设置数据
        String name = item.getUserName();
        tvUsername.setText(name);

        final String userId=item.getUserId();
        //设置时间
        long t = item.getTime();
        Date date = new Date(t);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:MM");
        String dateString = sdf.format(date);

        dayMonth.setText(dateString.substring(0, 10));
        tvTime.setText(dateString.substring(10));
        llDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDelListener.deleteItem(userId,helper.getAdapterPosition());
            }
        });
    }

   public interface DeleteListener{
        void deleteItem(String userId,int position);
   }
}
