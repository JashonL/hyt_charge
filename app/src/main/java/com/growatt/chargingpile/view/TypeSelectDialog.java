package com.growatt.chargingpile.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.growatt.chargingpile.R;

/**
 * Created：2021/9/8 on 9:39:07
 * Author: on admin
 * Description:充电类型选择框
 */

public class TypeSelectDialog extends DialogFragment {

    private static String TAG = TypeSelectDialog.class.getSimpleName();

    private static CallBack mCallBack;

    public TypeSelectDialog() {
    }

    public static TypeSelectDialog newInstance(CallBack callBack) {
        TypeSelectDialog fragment = new TypeSelectDialog();
        mCallBack = callBack;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_charing_type, null);
        getDialog().setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);
        initView(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = getResources().getDimensionPixelSize(R.dimen.xa560);
        params.height = getResources().getDimensionPixelSize(R.dimen.xa320);
        getDialog().getWindow().setAttributes(params);
    }

    private void initView(View view) {

        view.findViewById(R.id.tv_start_action).setOnClickListener(v -> {
            mCallBack.confirm(getString(R.string.start_immediately));
            dismiss();
        });

        view.findViewById(R.id.tv_timer).setOnClickListener(v -> {
            TimeSetDialog.newInstance(0, getString(R.string.m204开始时间), "00:00", (hour, minute) -> {
                mCallBack.confirm(hour + ":" + minute);
                dismiss();
            }).show(getFragmentManager(), "StartDialog");
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCallBack = null;
    }

    public interface CallBack {
        void confirm(String str);
    }


}
