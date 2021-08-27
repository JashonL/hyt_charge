package com.growatt.chargingpile.view;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.growatt.chargingpile.R;

import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 * Created：2021/8/27 on 9:39:07
 * Author: on admin
 * Description:充电模式
 */

public class ChrageModeDialog extends DialogFragment {

    private static String TAG = ChrageModeDialog.class.getSimpleName();

    private String mTitle;
    private String mTime;
    private static TimeCallBack mTimeCallBack;

    public static ChrageModeDialog newInstance(String title, TimeCallBack timeCallBack) {
        ChrageModeDialog fragment = new ChrageModeDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
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
        }
        Log.d(TAG, "onCreate: ");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_chrage_mode, null);
        getDialog().setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);
        //initView(view);

        Log.d(TAG, "onCreateView: ");
        return view;
    }

    private void initView(View view) {

//        ImageView ivCancel = (ImageView) view.findViewById(R.id.iv_cancel);
//        TextView tvConfirm = (TextView) view.findViewById(R.id.tv_confirm);
//        TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
//        tvTitle.setText(mTitle);
//
//        ivCancel.setOnClickListener(v -> {
//            mTimeCallBack.cancel();
//            dismiss();
//        });
//
//        tvConfirm.setOnClickListener(v -> {
//
//            dismiss();
//        });

    }

    @Override
    public void onResume() {
        super.onResume();
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = getResources().getDimensionPixelSize(R.dimen.xa683);
        params.height = getResources().getDimensionPixelSize(R.dimen.xa434);
        params.gravity = Gravity.BOTTOM;
        params.y = getResources().getDimensionPixelSize(R.dimen.xa50);
        getDialog().getWindow().setAttributes(params);
        Log.d(TAG, "onResume: ");
    }

    public interface TimeCallBack {

        void confirm(String time);

        void cancel();
    }


}
