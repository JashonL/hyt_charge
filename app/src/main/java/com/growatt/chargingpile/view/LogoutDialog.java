package com.growatt.chargingpile.view;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.growatt.chargingpile.R;
import com.growatt.chargingpile.activity.LogoutActivity;

/**
 * Created：2021/9/8 on 9:39:07
 * Author: on admin
 * Description:注销确定框
 */

public class LogoutDialog extends DialogFragment {

    private static String TAG = LogoutDialog.class.getSimpleName();

    public LogoutDialog() {
    }

    public static LogoutDialog newInstance() {
        LogoutDialog fragment = new LogoutDialog();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_logout, null);
        getDialog().setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);
        initView(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // 设置宽度为屏宽, 靠近屏幕底部。
        Window win = getDialog().getWindow();
        // 一定要设置Background，如果不设置，window属性设置无效
        win.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = getResources().getDimensionPixelSize(R.dimen.xa689);
        params.gravity = Gravity.BOTTOM;
        getDialog().getWindow().setAttributes(params);
    }

    private void initView(View view) {

        view.findViewById(R.id.iv_exit).setOnClickListener(v -> {
            dismiss();
        });

        view.findViewById(R.id.btn_logout).setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), LogoutActivity.class));
            dismiss();
        });
    }

}
