package com.growatt.chargingpile.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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

public class ChargingAllowTimeDialog extends DialogFragment implements View.OnClickListener {

    private static String TAG = ChargingAllowTimeDialog.class.getSimpleName();

    private static CallBack mCallBack;

    private String mTime;

    private TextView tvStartTime;
    private TextView tvEndTime;

    public ChargingAllowTimeDialog() {
    }

    public static ChargingAllowTimeDialog newInstance(String time, CallBack callBack) {
        ChargingAllowTimeDialog fragment = new ChargingAllowTimeDialog();
        Bundle args = new Bundle();
        args.putString("time", time);
        fragment.setArguments(args);
        mCallBack = callBack;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_charing_allow, null);
        getDialog().setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);
        initView(view);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTime = getArguments().getString("time");
        }
        Log.d(TAG, "onCreate: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = getResources().getDimensionPixelSize(R.dimen.xa560);
        params.height = getResources().getDimensionPixelSize(R.dimen.xa430);
        getDialog().getWindow().setAttributes(params);
    }

    private void initView(View view) {
        tvStartTime = view.findViewById(R.id.tv_start_time);
        tvStartTime.setOnClickListener(this);
        tvEndTime = view.findViewById(R.id.tv_end_time);
        tvEndTime.setOnClickListener(this);
        TextView tvCancel = view.findViewById(R.id.tv_cancel);
        tvCancel.setOnClickListener(this);
        TextView tvConfirm = view.findViewById(R.id.tv_confirm);
        tvConfirm.setOnClickListener(this);

        if (!mTime.equals("--")) {
            tvStartTime.setText(mTime.substring(0, 5));
            tvEndTime.setText(mTime.substring(6, 11));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCallBack = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_start_time:
                AllowSelectTimeDialog.newInstance(getString(R.string.m204开始时间), tvStartTime.getText().toString(), (hour, minute) -> {
                    Log.d(TAG, "confirm: hour:" + hour + "  minute:" + minute);
                    tvStartTime.setText(hour + ":" + minute);
                }).show(this.getChildFragmentManager(), "endDialog");

                break;
            case R.id.tv_end_time:
                AllowSelectTimeDialog.newInstance(getString(R.string.m282结束时间), tvEndTime.getText().toString(), (hour, minute) -> {
                    Log.d(TAG, "confirm: hour:" + hour + "  minute:" + minute);
                    tvEndTime.setText(hour + ":" + minute);
                }).show(this.getChildFragmentManager(), "endDialog");
                break;
            case R.id.tv_confirm:
                mCallBack.confirm(tvStartTime.getText().toString() + "-" + tvEndTime.getText().toString());
                dismiss();
                break;
            case R.id.tv_cancel:
                dismiss();
                break;
            default:
                break;
        }
    }

    public interface CallBack {
        void confirm(String str);
    }
}
