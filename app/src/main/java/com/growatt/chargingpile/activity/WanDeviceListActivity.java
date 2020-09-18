package com.growatt.chargingpile.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.growatt.chargingpile.BaseActivity;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.adapter.WanDeviceListAdapter;
import com.growatt.chargingpile.bean.UdpSearchBean;
import com.growatt.chargingpile.util.DeviceSearchThread;
import com.growatt.chargingpile.util.Mydialog;
import com.growatt.chargingpile.view.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class WanDeviceListActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener {

    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.ivLeft)
    ImageView ivLeft;
    @BindView(R.id.tvRight)
    TextView tvRight;
    @BindView(R.id.relativeLayout1)
    RelativeLayout relativeLayout1;
    @BindView(R.id.headerView)
    LinearLayout headerView;
    @BindView(R.id.deviceList)
    RecyclerView deviceList;
    @BindView(R.id.srl_pull)
    SwipeRefreshLayout srlPull;

    private WanDeviceListAdapter adapter;

    public static final int SEARCH_DEVICE_START = 1;
    public static final int SEARCH_DEVICE_FINISH = 2;

    private List<UdpSearchBean> mDeviceList;

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SEARCH_DEVICE_START:
                    Mydialog.Show(WanDeviceListActivity.this);
                    break;
                case SEARCH_DEVICE_FINISH:
                    Mydialog.Dismiss();
                    adapter.replaceData(mDeviceList);
                    srlPull.setRefreshing(false);
                    break;
            }
        }
    };
    private Unbinder bind;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wan_device_list);
        bind=ButterKnife.bind(this);
        mDeviceList = new ArrayList<>();
        initViews();
        initPullView();
        searchDevice();
    }

    private void searchDevice() {
        new DeviceSearchThread() {
            @Override
            public void onSearchStart() {
                Message msg = Message.obtain();
                msg.what = SEARCH_DEVICE_START;
                handler.sendMessage(msg);
            }

            @Override
            public void onSearchFinish(Set deviceSet) {
                for (Object aDeviceSet : deviceSet) {
                    mDeviceList.add((UdpSearchBean) aDeviceSet);
                }
                Message msg = Message.obtain();
                msg.what = SEARCH_DEVICE_FINISH;
                handler.sendMessage(msg);
            }
        }.start();
    }


    private void initPullView() {
        srlPull.setColorSchemeColors(ContextCompat.getColor(this, R.color.maincolor_1));
        srlPull.setOnRefreshListener(this::searchDevice);
    }

    private void initViews() {
        //头部
        ivLeft.setImageResource(R.drawable.back);
        tvTitle.setText(R.string.m105桩体设置);
        tvTitle.setTextColor(ContextCompat.getColor(this, R.color.title_1));

        //初始化列表
        deviceList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new WanDeviceListAdapter(R.layout.item_wan_devicelist, new ArrayList<>());
        deviceList.setAdapter(adapter);
        int dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen.xa2);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST, false, R.color.title_bg_white, dimensionPixelSize);
        deviceList.addItemDecoration(dividerItemDecoration);
        adapter.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

    }


    @OnClick(R.id.ivLeft)
    public void onViewClicked() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
