package com.growatt.chargingpile.view;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.growatt.chargingpile.R;

import java.util.Arrays;

/**
 * Created：2021/8/27 on 9:39:07
 * Author: on admin
 * Description:时间设置
 */

public class AllowSelectTimeDialog extends DialogFragment {

    private static String TAG = AllowSelectTimeDialog.class.getSimpleName();

    private String mTitle;
    private String mTime;
    private static TimeCallBack mTimeCallBack;

    private NumberPickerView mNumberHour;
    private NumberPickerView mNumberMinute;

    public AllowSelectTimeDialog() {
    }

    public static AllowSelectTimeDialog newInstance(String title, String time, TimeCallBack timeCallBack) {
        AllowSelectTimeDialog fragment = new AllowSelectTimeDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("time", time);
        fragment.setArguments(args);
        mTimeCallBack = timeCallBack;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.DialogTheme);
        if (getArguments() != null) {
            mTitle = getArguments().getString("title");
            mTime = getArguments().getString("time");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_charging_allow_time, null);
        getDialog().setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mNumberHour = view.findViewById(R.id.np_hour);
        mNumberMinute = view.findViewById(R.id.np_minute);
        ImageView ivCancel = view.findViewById(R.id.iv_cancel);
        TextView tvConfirm = view.findViewById(R.id.tv_confirm);
        TextView tvTitle = view.findViewById(R.id.tv_title);
        tvTitle.setText(mTitle);


        ivCancel.setOnClickListener(v -> {
            dismiss();
        });

        tvConfirm.setOnClickListener(v -> {
            mTimeCallBack.confirm(mNumberHour.getContentByCurrValue(), mNumberMinute.getContentByCurrValue());
            dismiss();
        });

        if (tvTitle.getText().toString().equals(getString(R.string.charging_time))) {
            view.findViewById(R.id.tv_hour).setVisibility(View.VISIBLE);
            view.findViewById(R.id.tv_min).setVisibility(View.VISIBLE);
            if (mTime.length() < 4) {
                initPicker(mNumberHour, 0, 23, 0, "%02d");
                initPicker(mNumberMinute, 0, 59, 0, "%02d");
            } else {
                initPicker(mNumberHour, 0, 23, Integer.valueOf(mTime.substring(0, 2)), "%02d");
                initPicker(mNumberMinute, 0, 59, Integer.valueOf(mTime.substring(4, 6)), "%02d");
            }
        } else {
            if (mTime.length() < 4) {
                initPicker(mNumberHour, 0, 23, 0, "%02d");
                initPicker(mNumberMinute, 0, 59, 0, "%02d");
            } else {
                initPicker(mNumberHour, 0, 23, Integer.valueOf(mTime.substring(0, 2)), "%02d");
                initPicker(mNumberMinute, 0, 59, Integer.valueOf(mTime.substring(4, 5)), "%02d");
            }
        }

    }

    /**
     * @param picker
     * @param maxValue
     * @param minValue
     */
    private void initPicker(NumberPickerView picker, int minValue, int maxValue, int showValue,
                            String format) {

        int[] arrayInt = new int[maxValue - minValue + 1];
        for (int i = 0; i <= maxValue - minValue; i++) {
            arrayInt[i] = i + minValue;
        }

        int index = Arrays.binarySearch(arrayInt, showValue);
        index = (index >= 0 && index < arrayInt.length) ? index : (arrayInt.length - 1);

        String[] arrayValue = new String[maxValue - minValue + 1];

        for (int i = 0; i <= maxValue - minValue; i++) {
            String str = String.format(format, i + minValue);
            arrayValue[i] = str;
        }

        picker.setDisplayedValuesAndPickedIndex(arrayValue, index, true);
    }

    @Override
    public void onResume() {
        super.onResume();
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = getResources().getDimensionPixelSize(R.dimen.xa688);
        params.height = getResources().getDimensionPixelSize(R.dimen.xa370);
        params.gravity = Gravity.BOTTOM;
        params.y = getResources().getDimensionPixelSize(R.dimen.xa50);
        getDialog().getWindow().setAttributes(params);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTimeCallBack = null;
    }

    public interface TimeCallBack {
        void confirm(String hour, String minute);
    }


}
