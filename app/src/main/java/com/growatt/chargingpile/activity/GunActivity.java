package com.growatt.chargingpile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.growatt.chargingpile.BaseActivity;
import com.growatt.chargingpile.EventBusMsg.RefreshRateMsg;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.bean.ChargingBean;
import com.growatt.chargingpile.bean.GunBean;
import com.growatt.chargingpile.bean.PileSetBean;
import com.growatt.chargingpile.fragment.gun.FragmentA;
import com.growatt.chargingpile.fragment.gun.FragmentB;
import com.growatt.chargingpile.fragment.gun.FragmentC;
import com.growatt.chargingpile.fragment.gun.FragmentD;
import com.growatt.chargingpile.model.GunModel;
import com.growatt.chargingpile.setting.SettingActivity;
import com.growatt.chargingpile.util.SmartHomeUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GunActivity extends BaseActivity {

    private static String TAG = GunActivity.class.getSimpleName();
    @BindView(R.id.headerView)
    LinearLayout mHeaderView;
    @BindView(R.id.tvTitle)
    TextView mTvTitle;
    @BindView(R.id.tl_tabs)
    TabLayout mTabLayout;
    @BindView(R.id.vp_tabs)
    ViewPager mTabViewPager;
    @BindView(R.id.line)
    View mLine;

    //当前枪
    public GunBean pCurrentGunBean;

    //当前桩的
    public String pSymbol = "";


    public ChargingBean.DataBean pDataBean;
    private Fragment[] mArrFragment = null;
    private String[] mArrTitle = null;

    public List<ChargingBean.DataBean.PriceConfBean> pPriceConfBeanList;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshAdapter(RefreshRateMsg msg) {
        if (msg.getPriceConfBeanList() != null) {
            pPriceConfBeanList = msg.getPriceConfBeanList();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gun);
        ButterKnife.bind(this);
        pDataBean = getIntent().getParcelableExtra("chargingBean");
        initToolBar();
        initTabLayout();
        getPileSettingParams();
    }


    private void getPileSettingParams() {
        GunModel.getInstance().requestChargingParams(pDataBean.getChargeId(), new GunModel.HttpCallBack() {
            @Override
            public void onSuccess(Object bean) {
                PileSetBean.DataBean data = (PileSetBean.DataBean) bean;
                pSymbol = data.getSymbol();
                Log.d(TAG, "pSymbol: " + pSymbol);
            }

            @Override
            public void onFailed() {

            }
        });
    }

    @Override
    protected void initToolBar() {
        setHeaderImage(mHeaderView, R.drawable.back, Position.LEFT, v -> {
            finish();
        });
        setHeaderImage(mHeaderView, R.drawable.ic_setting, Position.RIGHT, v -> {

            if (SmartHomeUtil.isFlagUser()) {
                toast(getString(R.string.m66你的账号没有操作权限));
                return;
            }
            if (pDataBean.getType() == 1) {
                toast(getString(R.string.m66你的账号没有操作权限));
                return;
            }

            List<ChargingBean.DataBean.PriceConfBean> conf = pDataBean.getPriceConf();
            ArrayList<ChargingBean.DataBean.PriceConfBean> priceConf = new ArrayList<>();
            if (conf != null) priceConf.addAll(conf);
            Intent intent = new Intent(this, SettingActivity.class);
            intent.putExtra("sn", pDataBean.getChargeId());
            if (pPriceConfBeanList != null) {
                intent.putParcelableArrayListExtra("rate", (ArrayList<? extends Parcelable>) pPriceConfBeanList);
            } else {
                intent.putParcelableArrayListExtra("rate", priceConf);
            }
            intent.putExtra("gunBean", pCurrentGunBean);
            jumpTo(intent, false);
        });


        mTvTitle.setTextColor(ContextCompat.getColor(this, R.color.black));
        if (TextUtils.isEmpty(pDataBean.getName())) {
            mTvTitle.setText(pDataBean.getChargeId());
        } else {
            mTvTitle.setText(pDataBean.getName());
        }
    }

    private void initTabLayout() {
        if (pDataBean.getConnectors() > 1) {
            mArrTitle = new String[]{
                    getString(R.string.m110A枪),
                    getString(R.string.m111B枪),
                    getString(R.string.m112C枪),
                    getString(R.string.m113D枪)};
            Fragment[] mTempFragment = new Fragment[]{new FragmentA(), new FragmentB(), new FragmentC(), new FragmentD()};
            mArrFragment = new Fragment[pDataBean.getConnectors()];
            for (int i = 0; i < pDataBean.getConnectors(); i++) {
                mArrFragment[i] = mTempFragment[i];
            }
            mTabLayout.setupWithViewPager(mTabViewPager);
            mTabViewPager.setOffscreenPageLimit(pDataBean.getConnectors() - 1);
        } else {
            mArrTitle = new String[]{""};
            mArrFragment = new Fragment[]{new FragmentA()};
            mTabLayout.setVisibility(View.GONE);
            mLine.setVisibility(View.GONE);
        }
        mTabViewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return mArrFragment[position];
            }

            @Override
            public int getCount() {
                return mArrFragment.length;
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return mArrTitle[position];
            }
        });
    }

}