package com.growatt.chargingpile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import com.google.gson.Gson;
import com.growatt.chargingpile.BaseActivity;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.adapter.ChargingUserAdapter;
import com.growatt.chargingpile.bean.ChargingUserBean;
import com.growatt.chargingpile.connutil.PostUtil;
import com.growatt.chargingpile.util.Cons;
import com.growatt.chargingpile.util.Mydialog;
import com.growatt.chargingpile.util.SmartHomeUrlUtil;
import com.growatt.chargingpile.util.SmartHomeUtil;
import com.mylhyl.circledialog.CircleDialog;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.util.LogUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ChargingAuthorizationActivity extends BaseActivity {

    @BindView(R.id.headerView)
    View headerView;

    /*授权列表*/
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    private List<ChargingUserBean.DataBean> mUserList = new ArrayList<>();
    private LinearLayoutManager mLinearLayoutManager;
    private ChargingUserAdapter mChargingUserAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charging_authorization);
        ButterKnife.bind(this);
        initHeaderView();
        initRecyclerView();
    }


    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        Mydialog.Show(this);
        Map<String, Object> jsonMap = new LinkedHashMap<String, Object>();
        jsonMap.put("sn", Cons.mCurrentPile.getChargeId());
        jsonMap.put("userId", Cons.userBean.getId());
        jsonMap.put("page", 1);
        jsonMap.put("psize", 30);
        jsonMap.put("lan",getLanguage());//测试id
        String json = SmartHomeUtil.mapToJsonString(jsonMap);
        LogUtil.i(json);
        PostUtil.postJson(SmartHomeUrlUtil.GET_AUTHORIZATION_USEER_LIST, json, new PostUtil.postListener() {
            @Override
            public void Params(Map<String, String> params) {

            }

            @Override
            public void success(String json) {
                Mydialog.Dismiss();
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

            }
        });
    }

    /**
     * 初始化头部
     */
    private void initHeaderView() {
        setHeaderImage(headerView, R.drawable.back, Position.LEFT, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setHeaderTitle(headerView, getResources().getString(R.string.m142授权管理),R.color.title_1,false);
        setHeaderImage(headerView, R.drawable.add_authorization_user, Position.RIGHT, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoGrant();
            }
        });
    }


    private void gotoGrant() {
        Intent intent = new Intent(this, AddAuthorizationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }


    private void initRecyclerView() {
        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mChargingUserAdapter = new ChargingUserAdapter(mUserList);
        mChargingUserAdapter.setDelListener(new ChargingUserAdapter.DeleteListener() {
            @Override
            public void deleteItem(String userId, int position) {
                deleteUser(userId, position);
            }
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
                .setGravity(Gravity.CENTER).setPositive(getString(R.string.m9确定), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Mydialog.Show(ChargingAuthorizationActivity.this);
                Map<String, Object> jsonMap = new LinkedHashMap<String, Object>();
                jsonMap.put("sn", Cons.mCurrentPile.getChargeId());
                jsonMap.put("userId", userId);
                jsonMap.put("lan",getLanguage());//测试id
                String json = SmartHomeUtil.mapToJsonString(jsonMap);
                LogUtil.i(json);
                PostUtil.postJson(SmartHomeUrlUtil.DELETE_UTHORIZATION_USERE, json, new PostUtil.postListener() {
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
            }
        })
                .setNegative(getString(R.string.m7取消), null)
                .show(fragmentManager);


    }

}
