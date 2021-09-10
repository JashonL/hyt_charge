package com.growatt.chargingpile.view;

import android.os.Bundle;
import android.util.Log;
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

public class ModifyDialog extends DialogFragment {

    private static String TAG = ModifyDialog.class.getSimpleName();

    private String mTitle;
    private String mValue;
    private static CallBack mCallBack;

    public ModifyDialog() {
    }

    public static ModifyDialog newInstance(String title, String value, CallBack callBack) {
        ModifyDialog fragment = new ModifyDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("value", value);
        fragment.setArguments(args);
        mCallBack = callBack;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTitle = getArguments().getString("title");
            mValue = getArguments().getString("value");
            if (mValue.equals("--")) {
                mValue = "";
            }
        }
        Log.d(TAG, "onCreate: ");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_modify, null);
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
        params.height = getResources().getDimensionPixelSize(R.dimen.xa420);
        getDialog().getWindow().setAttributes(params);
    }

    private void initView(View view) {

        TextView tvTitle = view.findViewById(R.id.tv_title);
        tvTitle.setText(mTitle);
        EditText editText = view.findViewById(R.id.editText);
        editText.setText(mValue);
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
