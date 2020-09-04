package com.growatt.chargingpile.util;

import android.content.Context;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;

import com.bigkoo.pickerview.adapter.ArrayWheelAdapter;
import com.contrarywind.view.WheelView;
import com.growatt.chargingpile.R;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.view.listener.OnCreateBodyViewListener;

import java.util.List;

public class CircleDialogUtils {

    /**
     * 时间选择框
     * @param context 上下文
     * @param hour  当前时间：时
     * @param min 当前时间：分
     * @param fragmentManager  fragmentManager
     * @param listener 回调监听
     * @return
     */

    public static DialogFragment showWhiteTimeSelect(Context context, int hour, int min, FragmentManager fragmentManager, boolean isShowStatus, timeSelectedListener listener) {
        DialogFragment bulbBodyDialog = new CircleDialog.Builder()
                .setWidth(1)
                .setBodyView(R.layout.dialog_time_select, new OnCreateBodyViewListener() {
                    @Override
                    public void onCreateBodyView(View view) {
                        List<String> hours = SmartHomeUtil.getHours();
                        List<String> mins = SmartHomeUtil.getMins();
                        WheelView wheelHour = view.findViewById(R.id.wheel_hour);
                        WheelView wheelMin = view.findViewById(R.id.wheel_min);
                        //初始化时间选择器
                        wheelHour.setCyclic(true);
                        wheelHour.isCenterLabel(true);
                        wheelHour.setAdapter(new ArrayWheelAdapter<>(hours));
                        wheelHour.setCurrentItem(hour);
                        wheelHour.setTextColorCenter(ContextCompat.getColor(context, R.color.content_bg_white));
                        wheelMin.setCyclic(true);
                        wheelMin.isCenterLabel(true);
                        wheelMin.setAdapter(new ArrayWheelAdapter<>(mins));
                        wheelMin.setCurrentItem(min);
                        wheelMin.setTextColorCenter(ContextCompat.getColor(context, R.color.content_bg_white));

                        Group status = view.findViewById(R.id.group_status);
                        status.setVisibility(isShowStatus?View.VISIBLE:View.GONE);
                        CheckBox cbStatus = view.findViewById(R.id.cb_checked);
                        cbStatus.setChecked(true);

                        view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                listener.cancle();
                            }
                        });

                        view.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int hour = wheelHour.getCurrentItem();
                                int min = wheelMin.getCurrentItem();
                                boolean checked = cbStatus.isChecked();
                                listener.ok(checked,hour,min);
                            }
                        });
                    }
                })
                .setGravity(Gravity.BOTTOM)
                .setYoff(20)
                .show(fragmentManager);
        ;
        return bulbBodyDialog;
    }




    public interface timeSelectedListener {
        void cancle();
        void ok(boolean status,int hour,int min);
    }

}
