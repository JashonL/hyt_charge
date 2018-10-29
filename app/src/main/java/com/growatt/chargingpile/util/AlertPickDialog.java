package com.growatt.chargingpile.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.growatt.chargingpile.R;
import com.growatt.chargingpile.view.NumberPicker;


/**
 * Created by letian on 15/6/13.
 */
public class AlertPickDialog {
    public static void showTimePickerDialog(Activity activity,  final String[] values1, String value1, final String[] values2, String value2, final AlertPickCallBack alertPickCallBack) {
        final AlertDialog dialog = new AlertDialog.Builder(activity, R.style.dialog_alert).create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        Window window = dialog.getWindow();
        window.setContentView(R.layout.dialog_action_item);
        window.setGravity(Gravity.BOTTOM);  //此处可以设置dialog显示的位置
        window.setWindowAnimations(R.style.dialog_style);  //添加动画
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        window.getDecorView().setPadding(0, 0, 0, 0);
        final NumberPicker numberPicker1 = (NumberPicker) dialog.findViewById(R.id.np_hour);
        final NumberPicker numberPicker2 = (NumberPicker) dialog.findViewById(R.id.np_minute);
        TextView sureTV = (TextView) dialog.findViewById(R.id.tv_sure);
        TextView cancelTV = (TextView) dialog.findViewById(R.id.tv_cancel);
        initData(numberPicker1, values1, value1);
        initData(numberPicker2, values2, value2);
        sureTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertPickCallBack.confirm(values1[numberPicker1.getValue()], values2[numberPicker2.getValue()]);
                dialog.cancel();
            }
        });
        cancelTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertPickCallBack.cancel();
                dialog.cancel();
            }
        });
    }


    private static void initData(NumberPicker numberPicker, String[] values, String value) {
        //展示值，一个数组
        numberPicker.setDisplayedValues(values);
        int index = 0;
        for (int i = 0; i < values.length; i++) {
            if (value.equals(values[i])) {
                index = i;
                break;
            }
        }
        numberPicker.setWrapSelectorWheel(true);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(values.length - 1);
        //设置当前值
        numberPicker.setValue(index);
    }

    public interface AlertPickCallBack {
        void confirm(String hour,String minute);

        void cancel();
    }
}
