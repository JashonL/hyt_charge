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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.growatt.chargingpile.R;

/**
 * Created：2021/8/27 on 9:39:07
 * Author: on admin
 * Description:充电模式
 */

public class ChargingModeDialog extends DialogFragment implements View.OnClickListener {

    private static String TAG = ChargingModeDialog.class.getSimpleName();

    private String mType;
    private static CallBack mCallBack;

    private TextView mTvFast, mTvEco, mTvEcoPlus;

    public static ChargingModeDialog newInstance(String type, CallBack callBack) {
        ChargingModeDialog fragment = new ChargingModeDialog();
        Bundle args = new Bundle();
        args.putString("type", type);
        fragment.setArguments(args);
        mCallBack = callBack;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.DialogTheme);
        if (getArguments() != null) {
            mType = getArguments().getString("type");
        }
        Log.d(TAG, "onCreate: ");
    }

    private void initModel(String type) {
        if (type.equals("Fast")) {
            mTvFast.setSelected(true);
        } else if (type.equals("ECO")) {
            mTvEco.setSelected(true);
        } else if (type.equals("ECO +")) {
            mTvEcoPlus.setSelected(true);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_chrage_mode, null);
        getDialog().setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);
        initView(view);
        initModel(mType);
        Log.d(TAG, "onCreateView: ");
        return view;
    }

    private void initView(View view) {
        ImageView ivCancel = view.findViewById(R.id.iv_cancel);
        mTvFast = view.findViewById(R.id.tv_fast);
        mTvFast.setOnClickListener(this);
        mTvEco = view.findViewById(R.id.tv_eco);
        mTvEco.setOnClickListener(this);
        mTvEcoPlus = view.findViewById(R.id.tv_eco_plus);
        mTvEcoPlus.setOnClickListener(this);
        ivCancel.setOnClickListener(v -> {
            mCallBack.cancel();
            dismiss();
        });

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

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tv_fast) {
            if (mTvFast.isSelected()) {
                return;
            }
            mTvEco.setSelected(false);
            mTvEcoPlus.setSelected(false);
            mTvFast.setSelected(true);
            mCallBack.confirm(0);
        } else if (id == R.id.tv_eco) {
            if (mTvEco.isSelected()) {
                return;
            }
            mTvFast.setSelected(false);
            mTvEcoPlus.setSelected(false);
            mTvEco.setSelected(true);
            mCallBack.confirm(1);
        } else if (id == R.id.tv_eco_plus) {
            if (mTvEcoPlus.isSelected()) {
                return;
            }
            mTvFast.setSelected(false);
            mTvEco.setSelected(false);
            mTvEcoPlus.setSelected(true);
            mCallBack.confirm(2);
        }
        dismiss();
    }

    public interface CallBack {

        void confirm(int index);

        void cancel();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCallBack = null;
        Log.d(TAG, "onDestroy: ");
    }

}
