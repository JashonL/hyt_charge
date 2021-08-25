package com.growatt.chargingpile.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.growatt.chargingpile.BaseActivity;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.bean.ChargingBean;
import com.growatt.chargingpile.fragment.FragmentA;
import com.growatt.chargingpile.fragment.FragmentB;
import com.growatt.chargingpile.fragment.FragmentC;
import com.growatt.chargingpile.fragment.FragmentD;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
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

    public ChargingBean.DataBean mDataBean;
    private Fragment[] mArrFragment = null;
    private String[] mArrTitle = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gun);
        ButterKnife.bind(this);
        mDataBean = (ChargingBean.DataBean) getIntent().getParcelableExtra("chargingBean");
        Log.d(TAG, "onCreate: " + mDataBean.toString());
        initToolBar();
        initTabLayout();
    }

    @Override
    protected void initToolBar() {
        setHeaderImage(mHeaderView, R.drawable.back, Position.LEFT, v -> {
            finish();
        });
        setHeaderImage(mHeaderView, R.drawable.ic_setting, Position.RIGHT, v -> {

        });
        mTvTitle.setTextColor(ContextCompat.getColor(this, R.color.black));
        mTvTitle.setText(mDataBean.getChargeId());
    }

    private void initTabLayout() {
        if (mDataBean.getConnectors() > 1) {
            mArrTitle = new String[]{
                    getString(R.string.m110A枪),
                    getString(R.string.m111B枪),
                    getString(R.string.m112C枪),
                    getString(R.string.m113D枪)};
            Fragment[] mTempFragment = new Fragment[]{new FragmentA(), new FragmentB(), new FragmentC(), new FragmentD()};
            mArrFragment = new Fragment[mDataBean.getConnectors()];
            for (int i = 0; i < mDataBean.getConnectors(); i++) {
                mArrFragment[i] = mTempFragment[i];
            }
            mTabLayout.setupWithViewPager(mTabViewPager);
            mTabViewPager.setOffscreenPageLimit(mDataBean.getConnectors() - 1);
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