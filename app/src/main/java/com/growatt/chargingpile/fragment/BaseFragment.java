package com.growatt.chargingpile.fragment;

import static com.growatt.chargingpile.constant.Constant.DELAYED_MINUTE;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.growatt.chargingpile.activity.GunActivity;
import com.growatt.chargingpile.fragment.preset.PresetActivity;
import com.growatt.chargingpile.model.GunModel;

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

    protected GunModel pModel;

    protected PresetActivity pPresetActivity;

    private Unbinder unbinder;

    //判断是否已进行过加载，避免重复加载
    private boolean isLoad = false;
    //判断当前fragment是否可见
    private boolean isVisibleToUser = false;
    //判断当前fragment是否回调了resume
    private boolean isResume = false;

    protected Handler pHandler = new Handler();


    protected abstract Object setRootView();

    protected abstract void initWidget();

    //预设后3秒获取
    protected Runnable runnableGunInfo = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "runnableGun");
            getGunInfoData();
            pHandler.postDelayed(runnableGunInfo, 3 * 1000);
        }
    };
    //进入枪1分钟获取
    protected Runnable runnableDelayedGun = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "runnableDelayedGun");
            pHandler.postDelayed(runnableDelayedGun, DELAYED_MINUTE);
            getGunInfoData();
        }
    };

    public void startRunnable(boolean isStart) {
        if (isStart) {
            pHandler.postDelayed(runnableDelayedGun, DELAYED_MINUTE);
        } else {
            pHandler.removeCallbacks(runnableDelayedGun);
        }
    }

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
            pPresetActivity = (PresetActivity) getActivity();
        }
        pModel = new GunModel();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        isResume = true;
        lazyLoad();
        initWidget();
    }

    protected void getGunInfoData() {

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
            getGunInfoData();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        pHandler.removeCallbacks(runnableGunInfo);
        pHandler.removeCallbacks(runnableDelayedGun);
        isLoad = false;
        isVisibleToUser = false;
        isResume = false;
        unbinder.unbind();
        Log.d(TAG, "onDestroyView: ");
    }

}
