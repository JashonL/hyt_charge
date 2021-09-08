package com.growatt.chargingpile.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.growatt.chargingpile.R;

/**
 * Created：2021/9/8 on 9:39:07
 * Author: on admin
 * Description:修改框
 */

public class PassWordDialog extends DialogFragment {

    private static String TAG = PassWordDialog.class.getSimpleName();

    private static CallBack mCallBack;

    public PassWordDialog() {
    }

    public static PassWordDialog newInstance(CallBack callBack) {
        PassWordDialog fragment = new PassWordDialog();
        mCallBack = callBack;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_password, null);
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
        params.height = getResources().getDimensionPixelSize(R.dimen.xa480);
        getDialog().getWindow().setAttributes(params);
    }

    private void initView(View view) {
        EditText editText = view.findViewById(R.id.editText);
        TextView tvCancel = view.findViewById(R.id.tv_cancel);
        tvCancel.setOnClickListener(v -> {
            dismiss();
        });

        TextView tvConfirm = view.findViewById(R.id.tv_confirm);
        tvConfirm.setOnClickListener(v -> {
            dismiss();
            mCallBack.confirm(editText.getText().toString());
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
