package com.growatt.chargingpile.view;

import android.os.Bundle;
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

/**
 * Created：2021/9/8 on 9:39:07
 * Author: on admin
 * Description:注销结果
 */

public class LogoutResultDialog extends DialogFragment {

    private static String TAG = LogoutResultDialog.class.getSimpleName();

    private static CallBack mCallBack;

    private int mCode;

    public LogoutResultDialog() {
    }

    public static LogoutResultDialog newInstance(int code, CallBack callBack) {
        LogoutResultDialog fragment = new LogoutResultDialog();
        Bundle args = new Bundle();
        args.putInt("code", code);
        fragment.setArguments(args);
        mCallBack = callBack;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCode = getArguments().getInt("code");
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_logout_result, null);
        getDialog().setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);
        initView(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = getResources().getDimensionPixelSize(R.dimen.xa535);
        params.height = getResources().getDimensionPixelSize(R.dimen.xa480);
        getDialog().getWindow().setAttributes(params);
    }

    private void initView(View view) {

        ImageView ivResult = view.findViewById(R.id.iv_result);
        TextView tvResult = view.findViewById(R.id.tv_result);

        if (mCode == 0) {
            ivResult.setImageResource(R.drawable.ic_success);
            tvResult.setText(R.string.m320注销成功);
        } else if (mCode == 1) {
            ivResult.setImageResource(R.drawable.ic_fail);
            tvResult.setText(R.string.m321注销失败);
        }

        view.findViewById(R.id.tv_ok).setOnClickListener(v -> {
            mCallBack.confirm(mCode);
            dismiss();
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCallBack = null;
    }

    public interface CallBack {
        void confirm(int code);
    }


}
