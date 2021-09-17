package com.growatt.chargingpile.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.growatt.chargingpile.BaseActivity;
import com.growatt.chargingpile.EventBusMsg.FreshAuthMsg;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.activity.AddAuthorizationActivity;
import com.growatt.chargingpile.adapter.ChargingUserAdapter;
import com.growatt.chargingpile.bean.ChargingUserBean;
import com.growatt.chargingpile.connutil.PostUtil;
import com.growatt.chargingpile.util.Mydialog;
import com.growatt.chargingpile.util.SmartHomeUrlUtil;
import com.growatt.chargingpile.util.SmartHomeUtil;
import com.mylhyl.circledialog.CircleDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;
import org.xutils.common.util.LogUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * 权限管理
 */
public class PermissionsActivity extends BaseActivity {
    @BindView(R.id.headerView)
    View headerView;
    /*授权列表*/
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout mSwipeRefresh;
    private List<ChargingUserBean.DataBean> mUserList = new ArrayList<>();
    private ChargingUserAdapter mChargingUserAdapter;
    private String mChargingId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charging_authorization);
        ButterKnife.bind(this);
        initIntent();
        initHeaderView();
        initRecyclerView();
        initListeners();
        refresh();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleRefresh(FreshAuthMsg msg) {
        refresh();
    }

    private void initListeners() {
        mSwipeRefresh.setColorSchemeResources(R.color.maincolor_1);
        mSwipeRefresh.setOnRefreshListener(this::refresh);
    }

    private void initIntent() {
        mChargingId = getIntent().getStringExtra("chargingId");
    }

    private void refresh() {
        Mydialog.Show(this);
        Map<String, Object> jsonMap = new LinkedHashMap<String, Object>();
        jsonMap.put("sn", mChargingId);
        jsonMap.put("userId", SmartHomeUtil.getUserName());
        jsonMap.put("page", 1);
        jsonMap.put("psize", 30);
        jsonMap.put("lan", getLanguage());//测试id
        String json = SmartHomeUtil.mapToJsonString(jsonMap);
        LogUtil.i(json);
        PostUtil.postJson(SmartHomeUrlUtil.postGetAuthorizationList(), json, new PostUtil.postListener() {
            @Override
            public void Params(Map<String, String> params) {
            }

            @Override
            public void success(String json) {
                Mydialog.Dismiss();
                mSwipeRefresh.setRefreshing(false);
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    if (jsonObject.getInt("code") == 0) {
                        ChargingUserBean userBean = new Gson().fromJson(jsonObject.toString(), ChargingUserBean.class);
                        if (userBean != null) {
                            List<ChargingUserBean.DataBean> dataBeans = userBean.getData();
                            mChargingUserAdapter.replaceData(dataBeans);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void LoginError(String str) {
                mSwipeRefresh.setRefreshing(false);
            }
        });
    }

    private void initHeaderView() {
        setHeaderImage(headerView, R.drawable.back, Position.LEFT, v -> finish());
        setHeaderTitle(headerView, getResources().getString(R.string.m142授权管理), R.color.title_1, false);
        setHeaderImage(headerView, R.drawable.ic_add_charg, Position.RIGHT, v -> gotoGrant());
    }

    private void gotoGrant() {
        Intent intent = new Intent(this, AddAuthorizationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("sn", mChargingId);
        startActivity(intent);
    }

    private void initRecyclerView() {
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mChargingUserAdapter = new ChargingUserAdapter(mUserList);
        mChargingUserAdapter.setDelListener((userId, position) -> {

            if (SmartHomeUtil.getUserName().equals(userId)) {
                toast(getString(R.string.not_deleted));
                return;
            }

            deleteUser(userId, position);

        });
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mChargingUserAdapter);
        View emptyView = LayoutInflater.from(this).inflate(R.layout.empty_view, null);
        mChargingUserAdapter.setEmptyView(emptyView);
    }

    private void deleteUser(final String userId, final int pos) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        new CircleDialog.Builder()
                .setWidth(0.75f)
                .setTitle(getString(R.string.m8警告))
                .setText(getString(R.string.m确认删除))
                .setGravity(Gravity.CENTER).setPositive(getString(R.string.m9确定), v -> {
            Mydialog.Show(PermissionsActivity.this);
            Map<String, Object> jsonMap = new LinkedHashMap<String, Object>();
            jsonMap.put("sn", mChargingId);
            jsonMap.put("userId", userId);
            jsonMap.put("lan", getLanguage());//测试id
            String json = SmartHomeUtil.mapToJsonString(jsonMap);
            LogUtil.i(json);
            PostUtil.postJson(SmartHomeUrlUtil.postDeleteAuthorizationUser(), json, new PostUtil.postListener() {
                @Override
                public void Params(Map<String, String> params) {

                }

                @Override
                public void success(String json) {
                    Mydialog.Dismiss();
                    try {
                        JSONObject object = new JSONObject(json);
                        int code = object.getInt("code");
                        if (code == 0) mChargingUserAdapter.remove(pos);
                        String data = object.getString("data");
                        toast(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void LoginError(String str) {

                }
            });
        })
                .setNegative(getString(R.string.m7取消), null)
                .show(fragmentManager);


    }
}
