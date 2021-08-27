package com.growatt.chargingpile.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.growatt.chargingpile.activity.GunActivity;
import com.growatt.chargingpile.activity.PresetActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created：2021/8/24 on 14:45:32
 * Author:gaideng on admin
 * Description:
 */

public abstract class BaseFragment extends Fragment {

    private static String TAG = BaseFragment.class.getSimpleName();

    protected GunActivity pActivity;

    protected PresetActivity mPresetActivity;

    private Unbinder unbinder;

    //判断是否已进行过加载，避免重复加载
    private boolean isLoad = false;
    //判断当前fragment是否可见
    private boolean isVisibleToUser = false;
    //判断当前fragment是否回调了resume
    private boolean isResume = false;

    protected abstract Object setRootView();

    protected abstract void initWidget();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView;
        if (setRootView() instanceof Integer) {
            rootView = inflater.inflate((Integer) setRootView(), container, false);
        } else if (setRootView() instanceof View) {
            rootView = (View) setRootView();
        } else {
            throw new ClassCastException("type of setLayout() must be int or View!");
        }
        unbinder = ButterKnife.bind(this, rootView);

        if (getActivity() instanceof GunActivity) {
            pActivity = (GunActivity) getActivity();
        } else if (getActivity() instanceof PresetActivity) {
            mPresetActivity = (PresetActivity) getActivity();
        }
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        isResume = true;
        lazyLoad();
        initWidget();
    }

    protected void requestData() {

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        //Log.i(TAG, "setUserVisibleHint:" + isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        lazyLoad();
    }

    private void lazyLoad() {
        if (!isLoad && isVisibleToUser && isResume) {
            //懒加载。。。
            isLoad = true;
            Log.d(TAG, "lazyLoad: 懒加载");
            requestData();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isLoad = false;
        isVisibleToUser = false;
        isResume = false;
        unbinder.unbind();
        Log.d(TAG, "onDestroyView: ");
    }

}
