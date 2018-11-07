package com.growatt.chargingpile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;

import com.google.gson.Gson;
import com.growatt.chargingpile.BaseActivity;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.adapter.ChargingRecordAdapter;
import com.growatt.chargingpile.bean.ChargingRecordBean;
import com.growatt.chargingpile.connutil.PostUtil;
import com.growatt.chargingpile.util.Cons;
import com.growatt.chargingpile.util.Mydialog;
import com.growatt.chargingpile.util.SmartHomeUrlUtil;
import com.growatt.chargingpile.util.SmartHomeUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChargingRecoderActivity extends BaseActivity {

    @BindView(R.id.headerView)
    View headerView;

    /*选择充电桩*/
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    private List<ChargingRecordBean.DataBean> mRecordList = new ArrayList<>();
    private LinearLayoutManager mLinearLayoutManager;
    private ChargingRecordAdapter mChargingRecordAdapter;

    private int currentPage = 1;
    private int pageSize = 30;
    private boolean isLastPage = false;
    /*是否在加载*/
    private boolean isLoading = false;
    private int lastVisiblePosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charging_recoder);
        ButterKnife.bind(this);
        initHeaderView();
        initRecyclerView();
        setOnClickListener();
        refresh(1, 30);
    }

    private void setOnClickListener() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && !isLoading) {
                    if (mLinearLayoutManager.getChildCount() > 0 && lastVisiblePosition + 1 >= mLinearLayoutManager.getItemCount()) {
                        if (!isLastPage) {
                            currentPage++;
                            refresh(currentPage, pageSize);
                        }
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisiblePosition = mLinearLayoutManager.findLastVisibleItemPosition();//当前可见的最后一行
            }
        });
    }

    private void initRecyclerView() {
        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mChargingRecordAdapter = new ChargingRecordAdapter(mRecordList);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mChargingRecordAdapter);
    }

    private void initHeaderView() {
        setHeaderImage(headerView, R.drawable.back, Position.LEFT, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                backPileActivity();
            }
        });
        setHeaderTitle(headerView, getString(R.string.m104充电记录), R.color.title_1, false);
    }


    private void refresh(int page, int psize) {
        Mydialog.Show(this);
        Map<String, Object> jsonMap = new LinkedHashMap<String, Object>();
        jsonMap.put("userId", Cons.userBean.getId());
        jsonMap.put("sn", Cons.mCurrentPile.getChargeId());
        jsonMap.put("page", page);
        jsonMap.put("psize", psize);
        jsonMap.put("lan",getLanguage());//测试id
        String json = SmartHomeUtil.mapToJsonString(jsonMap);
        PostUtil.postJson(SmartHomeUrlUtil.USER_CHARGING_RECORD, json, new PostUtil.postListener() {
            @Override
            public void Params(Map<String, String> params) {

            }

            @Override
            public void success(String json) {
                try {
                    Mydialog.Dismiss();
                    JSONObject object = new JSONObject(json);
                    int code = object.getInt("code");
                    if (code == 0) {
                        ChargingRecordBean recordBean = new Gson().fromJson(json, ChargingRecordBean.class);
                        List<ChargingRecordBean.DataBean> recordList = recordBean.getData();
                        if (recordList.size() < pageSize) {
                            isLastPage = true;
                        }
                        if (recordBean == null || recordList.size() == 0) {
                            isLastPage = true;
                            if (currentPage > 1) {
                                currentPage--;
                            }
                        } else {
                            if (currentPage == 1) {
                                mChargingRecordAdapter.replaceData(recordList);
                            } else {
                                mChargingRecordAdapter.addData(recordList);
                            }
                        }

                    } else {
                        isLastPage = true;
                        if (currentPage > 1) {
                            currentPage--;
                        }
                    }
                } catch (Exception e) {
                    isLastPage = true;
                    if (currentPage > 1) {
                        currentPage--;
                    }
                    e.printStackTrace();
                }
            }

            @Override
            public void LoginError(String str) {

            }
        });
    }



    private void backPileActivity(){
        Intent intent = new Intent();
        intent.putExtra("activity",ChargingRecoderActivity.this.getClass().getName());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        backPileActivity();
        return super.onKeyDown(keyCode, event);
    }
}
