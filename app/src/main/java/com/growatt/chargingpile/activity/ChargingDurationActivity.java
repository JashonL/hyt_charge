package com.growatt.chargingpile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.google.gson.Gson;
import com.growatt.chargingpile.BaseActivity;
import com.growatt.chargingpile.EventBusMsg.FreshTimingMsg;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.adapter.TimingAdapter;
import com.growatt.chargingpile.bean.ReservationBean;
import com.growatt.chargingpile.connutil.PostUtil;
import com.growatt.chargingpile.util.MyUtil;
import com.growatt.chargingpile.util.Mydialog;
import com.growatt.chargingpile.util.SmartHomeUrlUtil;
import com.growatt.chargingpile.util.SmartHomeUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;
import org.xutils.common.util.LogUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class ChargingDurationActivity extends BaseActivity {

    @BindView(R.id.headerView)
    View headerView;

    @BindView(R.id.ll_duration)
    LinearLayout llDuration;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tv_total_time)
    TextView tvTotal;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout mSwipeRefresh;

    private List<ReservationBean.DataBean> mTimingList = new ArrayList<>();
    private TimingAdapter mAdapter;

    private int totalMinute;
    private String chargingId;
    private Unbinder bind;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charging_duration);
        bind = ButterKnife.bind(this);
        initIntent();
        initHeaderView();
        initRecyclerView();
        initListeners();
        refresh();
    }

    private void initIntent() {
        chargingId=getIntent().getStringExtra("sn");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void fresh(FreshTimingMsg msg) {
        refresh();
    }

    private void initListeners() {
        mSwipeRefresh.setColorSchemeResources(R.color.maincolor_1);
        mSwipeRefresh.setOnRefreshListener(this::refresh);

        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            ReservationBean.DataBean dataBean = mAdapter.getData().get(position);
            Intent intent = new Intent(ChargingDurationActivity.this, EditDurationActivity.class);
            intent.putExtra("bean", new Gson().toJson(dataBean));
            intent.putExtra("sn", chargingId);
            startActivity(intent);
        });

        recyclerView.addOnItemTouchListener(new OnItemChildClickListener() {
            @Override
            public void onSimpleItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                int viewId = view.getId();
                ReservationBean.DataBean dataBean = mAdapter.getData().get(position);
                if (dataBean == null) return;
                String status = dataBean.getStatus();
                int loopType = dataBean.getLoopType();
                switch (viewId) {
                    case R.id.rl_every_day:
                        if (status.equals("Accepted")) {
                            dataBean.setLoopType(loopType == -1 ? 0:-1);//勾选每天开启
                            editTime(dataBean, "1");
                        }
                        break;
                    case R.id.rl_switch:
                        //开启关闭
                        if (!status.equals("Accepted")) {
                            dataBean.setLoopType(loopType != -1 ? 0:-1);//勾选每天开启
                            editTime(dataBean, "1");
                        } else {
                            editTime(dataBean, "2");
                        }
                        break;
                }
            }
        });
    }

    private void refresh() {
        Mydialog.Show(this);
        Map<String, Object> jsonMap = new LinkedHashMap<String, Object>();
        jsonMap.put("userId",SmartHomeUtil.getUserName());
        jsonMap.put("sn",chargingId);
        jsonMap.put("connectorId", 1);
        jsonMap.put("cKey", "G_SetTime");
        jsonMap.put("lan", getLanguage());//测试id
        String json = SmartHomeUtil.mapToJsonString(jsonMap);
        PostUtil.postJson(SmartHomeUrlUtil.postRequestChargingReserveList(), json, new PostUtil.postListener() {
            @Override
            public void Params(Map<String, String> params) {

            }

            @Override
            public void success(String json) {
                Mydialog.Dismiss();
                mSwipeRefresh.setRefreshing(false);
                try {
                    JSONObject object = new JSONObject(json);
                    int code = object.getInt("code");
                    String data = object.getString("data");
                    if (code == 0) {
                        ReservationBean recordBean = new Gson().fromJson(json, ReservationBean.class);
                        List<ReservationBean.DataBean> reserveList = recordBean.getData();
                        if (reserveList!=null){
//                            refreshAdapter(reserveList);
                            mAdapter.replaceData(reserveList);
                            setTotal(reserveList);
                        }
                    } else {
                        toast(data);
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

    private void refreshAdapter(List<ReservationBean.DataBean> reserveList) {
        for (int i = 0; i < reserveList.size(); i++) {
            ReservationBean.DataBean bean = reserveList.get(i);
            String expiryDate = bean.getExpiryDate();
            String endDate = bean.getEndDate();
            //获取年月
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = new Date();
            String yMd = sdf.format(date);
            //把时间改成今天的
            expiryDate = yMd + "T" + expiryDate.substring(11, 16) + ":00.000Z";
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",Locale.getDefault());
            try {
                Date startDate = format.parse(expiryDate);
                long sysStartTime = startDate.getTime();
                long sysEndTime = sysStartTime + Integer.parseInt(bean.getcValue2()) * 60 * 1000;
                Date closeDate = new Date(sysEndTime);
                endDate = format.format(closeDate);

            } catch (ParseException e) {
                e.printStackTrace();
            }
            bean.setExpiryDate(expiryDate);
            bean.setEndDate(endDate);
        }
        mAdapter.replaceData(reserveList);
    }

    private void setTotal(List<ReservationBean.DataBean> reserveList) {
        if (reserveList.size() <= 0) {
            MyUtil.hideAllView(View.GONE, llDuration);
        } else {
            MyUtil.showAllView(llDuration);
            int total = 0;
            for (int i = 0; i < reserveList.size(); i++) {
                ReservationBean.DataBean dataBean = reserveList.get(i);
                int cValue = Integer.parseInt(dataBean.getcValue2());
                total += cValue;
            }
            int hour = total / 60;
            int minute = total % 60;
            String time = hour + "h" + minute + "min";
            tvTotal.setText(time);
            totalMinute = total;
        }

    }


    private void initHeaderView() {
        setHeaderImage(headerView, R.drawable.back, Position.LEFT, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });
        setHeaderTitle(headerView, getString(R.string.m178定时), R.color.title_1, true);
        setHeaderTvRight(headerView, getString(R.string.m179新增), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ChargingDurationActivity.this,EditDurationActivity.class);
                intent.putExtra("sn",chargingId);
                jumpTo(intent,false);
            }
        }, R.color.main_text_color);
    }


    private void backToChargingPile() {
        Intent intent = new Intent();
        intent.putExtra("time", totalMinute);
        setResult(RESULT_OK, intent);
        finish();
    }


    private void initRecyclerView() {
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mAdapter = new TimingAdapter(mTimingList);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.setAdapter(mAdapter);
        View emptyView = LayoutInflater.from(this).inflate(R.layout.empty_view, null);
        mAdapter.setEmptyView(emptyView);
    }


    private void editTime(ReservationBean.DataBean dataBean, String ctype) {
        Mydialog.Show(this);
        String json = new Gson().toJson(dataBean);
        if (!TextUtils.isEmpty(ctype)) {
            try {
                JSONObject object;
                if ("1".equals(ctype)){
                     object = new JSONObject(json);
                }else {
                    object = new JSONObject();
                    object.put("cKey", dataBean.getcKey());
                    object.put("cValue", dataBean.getcValue());
                    object.put("connectorId", dataBean.getConnectorId());
                    object.put("expiryDate", dataBean.getExpiryDate());
                    object.put("loopValue", dataBean.getLoopValue());
                    object.put("reservationId", dataBean.getReservationId());
                    object.put("sn", chargingId);
                    object.put("userId", SmartHomeUtil.getUserName());
                }
                object.put("ctype", ctype);
                object.put("lan", getLanguage());//测试id
                json = object.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (TextUtils.isEmpty(json))return;
        LogUtil.i(json);
        PostUtil.postJson(SmartHomeUrlUtil.postUpdateChargingReservelist(), json, new PostUtil.postListener() {
            @Override
            public void Params(Map<String, String> params) {

            }

            @Override
            public void success(String json) {
                Mydialog.Dismiss();
                try {
                    JSONObject object = new JSONObject(json);
                    int code = object.getInt("code");
                    String data = object.getString("data");
                    if (code == 0) {
//                        refresh();
                        EventBus.getDefault().post(new FreshTimingMsg());
                        toast(R.string.m成功);
                    } else {
                        toast(data);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
