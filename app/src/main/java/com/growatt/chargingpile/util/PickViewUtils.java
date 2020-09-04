package com.growatt.chargingpile.util;

import android.app.Activity;
import androidx.core.content.ContextCompat;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.application.MyApplication;

import java.util.List;

public class PickViewUtils {
    /**
     * 弹出滚动选择器
     *
     * @param data     数据源
     * @param title    选择器标题
     */
    public static void showPickView(final Activity context, final List<String> data, OnOptionsSelectListener listener, String title) {
        OptionsPickerView<String> pvOptions = new OptionsPickerBuilder(context,listener)
                .setTitleText(title)
                .setCancelText(MyApplication.getInstance().getString(R.string.m260语言))//取消按钮文字
                .setSubmitText(MyApplication.getInstance().getString(R.string.m9确定))//确认按钮文字
                .setTitleBgColor(ContextCompat.getColor(context,R.color.white_background))
                .setTitleColor(ContextCompat.getColor(context,R.color.title_2))
                .setSubmitColor(ContextCompat.getColor(context,R.color.maincolor_1))
                .setCancelColor(ContextCompat.getColor(context,R.color.title_3))
                .setBgColor(ContextCompat.getColor(context,R.color.white_background))
                .setTitleSize(22)
                .setTextColorCenter(ContextCompat.getColor(context,R.color.title_1))
                .build();
        pvOptions.setPicker(data);
        pvOptions.show();
    }
}
